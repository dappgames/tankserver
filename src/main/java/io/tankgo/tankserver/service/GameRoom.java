package io.tankgo.tankserver.service;

import com.corundumstudio.socketio.SocketIOClient;
import com.google.common.collect.HashBasedTable;
import io.tankgo.tankserver.gameobject.GameObject;
import io.tankgo.tankserver.gameobject.bullet.BaseBullet;
import io.tankgo.tankserver.gameobject.player.BasePlayer;
import io.tankgo.tankserver.config.BidAskConfig;
import io.tankgo.tankserver.entity.UserBalanceDO;
import io.tankgo.tankserver.entity.UserWalletDO;
import io.tankgo.tankserver.pojo.*;
import io.tankgo.tankserver.utils.AckEventRetryer;
import io.tankgo.tankserver.utils.MyUtils;
import io.tankgo.tankserver.utils.RespUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.dyn4j.collision.narrowphase.Penetration;
import org.dyn4j.dynamics.Body;
import org.dyn4j.dynamics.BodyFixture;
import org.dyn4j.dynamics.CollisionAdapter;
import org.dyn4j.dynamics.World;
import org.dyn4j.geometry.Geometry;
import org.dyn4j.geometry.MassType;
import org.nutz.dao.Chain;
import org.nutz.dao.Cnd;
import org.nutz.dao.impl.NutDao;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.*;

@Component
@Slf4j
public class GameRoom {

    @Autowired
    private GameLogService gameLogService;

    @Autowired
    private GameMainService gameMainService;

    @Autowired
    private NutDao nutDao;

    private String name;

    private World world = new World();
    private ConcurrentMap<UUID, GameUser> clientUserMap = new ConcurrentHashMap<>();
    private HashBasedTable<Integer, Integer, ConcurrentMap<String, GameObject>> gridMap;
    private ConcurrentHashMap<String, BasePlayer> dynPlayers = new ConcurrentHashMap<>();
    private List<BaseBullet> dynBullets = new ArrayList<>();
    private boolean running = false;
    private final static int GRIDLEN = 8;
    private final static double worldUpdateSec = 0.02d;
    private final static long worldUpdateMillisec = 10;
    private double worldTime = 0d;
    private long step = 0;
    private long nanoTime = 0;
    private ScheduledExecutorService dynExecutor = Executors.newSingleThreadScheduledExecutor();
    private ScheduledExecutorService serverExecutor = Executors.newSingleThreadScheduledExecutor();
    private ExecutorService socketExecutor = Executors.newFixedThreadPool(4);
    private ScheduledExecutorService envExecutor = Executors.newSingleThreadScheduledExecutor();
    private ScheduledExecutorService checkExecutor = Executors.newSingleThreadScheduledExecutor();

    @PostConstruct
    public void init() {
        this.name= MyUtils.shortUuid();
        this.world.setGravity(World.ZERO_GRAVITY);
        this.addWorldWall(32.00);
        this.running = true;
        double initDelay = Math.random() * 1000;
        gridMap = HashBasedTable.create(GRIDLEN, GRIDLEN);
        for (int i = 0; i < GRIDLEN; i++) {
            for (int j = 0; j < GRIDLEN; j++) {
                gridMap.put(i, j, new ConcurrentHashMap<>());
            }
        }
        /**
         * 游戏主循环
         */
        dynExecutor.scheduleWithFixedDelay(new Runnable() {
            @Override
            public void run() {
                if (running) {
                    try {
                        step += 1;
                        if (step % 200 == 0) {
                            long nt = System.nanoTime();
                            log.info("update:" + (nt - nanoTime) / 200000000);
                            nanoTime = nt;
                        }

                        if (world == null) {
                            return;
                        }
                        Thread.sleep(2);
                        synchronized (world) {
                            Iterator<Map.Entry<String, BasePlayer>> itPlayer = dynPlayers.entrySet().iterator();
                            while (itPlayer.hasNext()) {
                                io.tankgo.tankserver.gameobject.player.BasePlayer player = itPlayer.next().getValue();
                                player.worldUpdate(worldTime);
                                gridMap.get(player.getGx(), player.getGy()).remove(player.getId());
                                if (player.checkRemove()) {
                                    if (!player.getRobot()) {
                                        try {
                                            for(String socketId:player.getSocketIds()){
                                                GameUser oldUser = clientUserMap.get(UUID.fromString(socketId));
                                                if(oldUser!=null&&oldUser.getSocketIOClient()!=null){
                                                    doUserGameOver(oldUser.getSocketIOClient());
                                                }
                                            }
                                        }catch (Exception e1){
                                            e1.printStackTrace();
                                        }
                                        try {
                                            gameLogService.destroyPlayer(player.getId());
                                        }catch (Exception e2){
                                            e2.printStackTrace();
                                        }
                                    }
                                    clearMoney(player);
                                    player.destroy();
                                    itPlayer.remove();
                                } else {
                                    player.asyncData();
                                    gridMap.get(player.getGx(), player.getGy()).put(player.getId(), player);
                                }
                            }
                            Iterator<BaseBullet> itBullet = dynBullets.iterator();
                            while (itBullet.hasNext()) {
                                io.tankgo.tankserver.gameobject.bullet.BaseBullet bullet = itBullet.next();
                                bullet.worldUpdate(worldTime);
                                ConcurrentMap<String, GameObject> bulletMap1 = gridMap.get(bullet.getGx(), bullet.getGy());
                                if (bulletMap1 != null) {
                                    bulletMap1.remove(bullet.getId());
                                } else {
                                    log.error("bulletMap1 not found,x:" + bullet.getGx() + ",y:" + bullet.getGy());
                                }
                                if (bullet.checkRemove()) {
                                    itBullet.remove();
                                    bullet.destroy();
                                } else {
                                    bullet.asyncData();
                                    ConcurrentMap<String, GameObject> bulletMap2 = gridMap.get(bullet.getGx(), bullet.getGy());
                                    if (bulletMap2 != null) {
                                        bulletMap2.put(bullet.getId(), bullet);
                                    } else {
                                        log.error("bulletMap2 not found,x:" + bullet.getGx() + ",y:" + bullet.getGy());
                                    }
                                }
                            }
                            Thread.sleep(2);
                            world.update(worldUpdateSec);
                            worldTime += worldUpdateSec;
                        }
                        Thread.sleep(2);
                    } catch (Exception e) {
                        log.info("dynExecutor exception");
                        e.printStackTrace();
                    }
                }
            }
        }, (long) initDelay, worldUpdateMillisec, TimeUnit.MILLISECONDS);
        /**
         * 发送socket通知
         */
        serverExecutor.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                try {
                    Iterator<Map.Entry<UUID, GameUser>> itUser = clientUserMap.entrySet().iterator();
                    while (itUser.hasNext()) {
                        GameUser gameUser = itUser.next().getValue();
                        List<GameObject> gameObjects = new ArrayList<>();
                        String playId = gameUser.getPlayId();
                        io.tankgo.tankserver.gameobject.GameObject gameObject = dynPlayers.get(playId);
                        Integer gx = gameUser.getLastGx();
                        Integer gy = gameUser.getLastGy();
                        if (gameObject != null) {
                            gx = gameObject.getGx();
                            gy = gameObject.getGy();
                            gameUser.setLastGx(gx);
                            gameUser.setLastGy(gy);
                        }
                        if (gx == null || gy == null) {
                            continue;
                        }
                        int minx = gx - 1;
                        if (minx < 0) {
                            minx = 0;
                        }
                        int miny = gy - 1;
                        if (miny < 0) {
                            miny = 0;
                        }
                        int maxx = gx + 1;
                        if (maxx >= GRIDLEN) {
                            maxx = GRIDLEN - 1;
                        }
                        int maxy = gy + 1;
                        if (maxy >= GRIDLEN) {
                            maxy = GRIDLEN - 1;
                        }

                        for (int x = minx; x <= maxx; x++) {
                            for (int y = miny; y <= maxy; y++) {
                                //只把临近一个格子的游戏对象返回给用户
                                gameObjects.addAll(gridMap.get(x, y).values());
                            }
                        }
//                        for(ConcurrentMap<String,GameObject> map:gridMap.values()){
//                            gameObjects.addAll(map.values());
//                        }
                        if(gameUser!=null&&gameUser.getSocketIOClient()!=null){
                            gameUser.getSocketIOClient().sendEvent("gameUpdate", RespUtil.success(gameObjects, worldTime));
                        }
                    }
                } catch (Exception e) {
                    log.info("serverExecutor error");
                    e.printStackTrace();
                }
            }
        }, (long) initDelay, 40, TimeUnit.MILLISECONDS);


        /**
         * 不断添加机器人
         */
        envExecutor.scheduleWithFixedDelay(new Runnable() {
            @Override
            public void run() {
                try {
                    int enemySize = 0;
                    Iterator<Map.Entry<String, BasePlayer>> itPlayer = dynPlayers.entrySet().iterator();
                    while (itPlayer.hasNext()) {
                        io.tankgo.tankserver.gameobject.player.BasePlayer player = itPlayer.next().getValue();
                        if (player.getRobot()) {
                            enemySize += 1;
                        }
                    }
                    if (enemySize < 5) {
                        GameRoom.this.doEnemyJoinRoom(MyUtils.getRandomName(6));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, 2, 1, TimeUnit.SECONDS);
        world.addListener(new BulletContactListener());
    }

    public String getName() {
        return name;
    }

    private class BulletContactListener extends CollisionAdapter {

        @Override
        public boolean collision(Body body1, BodyFixture fixture1, Body body2, BodyFixture fixture2, Penetration penetration) {

            boolean check1 = checkCollision(body1, body2);
            boolean check2 = checkCollision(body2, body1);
            return check1 && check2 && true;
        }
    }

    /**
     * 碰撞检测
     *
     * @param body1
     * @param body2
     * @return
     */
    private boolean checkCollision(Body body1, Body body2) {
        boolean ret = true;
        Object obj1 = body1.getUserData();
        Object obj2 = body2.getUserData();
        if (obj1 == null) {
            return true;
        }
        if (obj2 == null) {
            return true;
        }
        if (obj1 instanceof io.tankgo.tankserver.gameobject.bullet.BaseBullet) {
            io.tankgo.tankserver.gameobject.bullet.BaseBullet bullet1 = (io.tankgo.tankserver.gameobject.bullet.BaseBullet) obj1;
            if (obj2 instanceof io.tankgo.tankserver.gameobject.bullet.BaseBullet) {
                io.tankgo.tankserver.gameobject.bullet.BaseBullet bullet2 = (io.tankgo.tankserver.gameobject.bullet.BaseBullet) obj2;
                if (!bullet1.getPlayId().equals(bullet2.getPlayId())) {
                    bullet1.boom();
                    bullet2.boom();
                }
            } else if (obj2 instanceof io.tankgo.tankserver.gameobject.player.BasePlayer) {
                io.tankgo.tankserver.gameobject.player.BasePlayer player2 = (io.tankgo.tankserver.gameobject.player.BasePlayer) obj2;
//                if (bullet1.getGrp() != null && player2.getGrp() != null) {
//                    if (bullet1.getGrp().equals(player2.getGrp())) {
//                        //如果是同组，直接返回
//                        return ret;
//                    }
//                }
                String playId1 = bullet1.getPlayId();
                String playId2 = player2.getId();
                if (!playId1.equals(playId2)) {
                    if (!"boom".equals(bullet1.getState())) {
                        List<HitTip> hitTips = new ArrayList<>();
                        HitReward hitReward = player2.hit(bullet1);

                        if (hitReward.getSuccess()) {
                            HitTip loseTip = new HitTip(player2, hitReward, -1);
                            hitTips.add(loseTip);
                            bullet1.boom();
                            io.tankgo.tankserver.gameobject.player.BasePlayer player1 = dynPlayers.get(playId1);
                            if (player1 != null) {
                                HitTip winTip = new HitTip(player1, hitReward, 1);
                                hitTips.add(winTip);
                                HitReward oldReward = player1.getRewards().get(hitReward.getCoin());
                                if (oldReward == null) {
                                    player1.getRewards().put(hitReward.getCoin(), hitReward);
                                } else {
                                    oldReward.add(hitReward);
                                    player1.getRewards().put(hitReward.getCoin(), oldReward);
                                }
                                for (String socketId : player1.getSocketIds()) {
                                    gameMainService.hitTips(socketId, hitTips);
                                }
                            }
                            for (String socketId : player2.getSocketIds()) {
                                gameMainService.hitTips(socketId, hitTips);
                            }
//                            log.info(hitReward.toString());
//                            gameLogService.hit(player1,player2);
                        }
                    }
                }
            } else if (obj2 instanceof io.tankgo.tankserver.gameobject.wall.BaseWall) {
                io.tankgo.tankserver.gameobject.wall.BaseWall wall = (io.tankgo.tankserver.gameobject.wall.BaseWall) obj2;
                if (bullet1.hit(wall).getSuccess()) {
                    bullet1.boom();
                }
            }
        }
        return ret;
    }

    public int getPlayerNum() {
        return dynPlayers.size();
    }

    public GameUser doUserJoinRoom(GameUser gameUser) {
        synchronized (world) {
            GameUser gameUserRet = new GameUser();
            String playId = gameUser.getPlayId();
            io.tankgo.tankserver.gameobject.player.BasePlayer basePlayer = null;
            if (StringUtils.isEmpty(playId)) {
                //第一次进入房间
                //TODO 判断这个用户是否拥有该Skin
                basePlayer = io.tankgo.tankserver.gameobject.player.PlayerFactory.createPlayer(gameUser.getSkin());
                basePlayer.setKey(MyUtils.getRandomName(12));
                basePlayer.setPlayerListener(playerListener);
                String token = gameUser.getToken();
                TankEnterLog enterLog = nutDao.fetch(TankEnterLog.class, Cnd.where("token", "=", token)
                        .and("validate", "=", 1).and("destroy_time", "is", null));
                if (enterLog != null) {
                    //付费玩家
                    gameUser.setCoin(enterLog.getChainName() + "." + enterLog.getTokenSymbol());
                    gameUser.setAmount(enterLog.getTokenAmount());
                } else {
                    //免费玩家
                    gameUser.setCoin("free.BOOM");
                    Double amt = 1d;
                    gameUser.setAmount(new BigDecimal(amt).setScale(4, BigDecimal.ROUND_HALF_UP));
                }
                basePlayer.init(gameUser, world, worldTime);
                basePlayer.getSocketIds().add(gameUser.getSocketId());
                dynPlayers.put(basePlayer.getId(), basePlayer);
                gameUser.setKey(basePlayer.getKey());
                gameUser.setPlayId(basePlayer.getId());
                clientUserMap.put(gameUser.getSocketIOClient().getSessionId(), gameUser);
                if(enterLog!=null){
                    gameLogService.firstEnterRoom(gameUser.getToken(), gameUser.getSocketId(), basePlayer);
                }
            } else {
                basePlayer = dynPlayers.get(playId);
                if (basePlayer != null) {
                    clientUserMap.put(gameUser.getSocketIOClient().getSessionId(), gameUser);
                }
            }
            BeanUtils.copyProperties(gameUser, gameUserRet);
            gameUserRet.setSocketIOClient(null);
            return gameUserRet;
        }
    }

    public void doEnemyJoinRoom(String userName) {
        synchronized (world) {
            io.tankgo.tankserver.gameobject.player.BasePlayer enemyTank = io.tankgo.tankserver.gameobject.player.PlayerFactory.randomTank();
//                BasePlayer enemyTank = PlayerFactory.createPlayer("sma");
            enemyTank.setPlayerListener(playerListener);
            GameUser enemyUser = new GameUser();
            enemyUser.setUserName(userName);

            String coin = MyUtils.randomCoin();
            Double amt = 10d;
            if(coin.equals("free.BOOM")){
                amt=1d;
            }
            enemyUser.setCoin(coin);
            enemyUser.setAmount(new BigDecimal(amt).setScale(4, BigDecimal.ROUND_HALF_UP));
            enemyTank.init(enemyUser, world, worldTime);
            enemyTank.setRobot(true);
            dynPlayers.put(enemyTank.getId(), enemyTank);
        }
    }

    public void doUserGameOver(SocketIOClient client){
        synchronized (world) {
            if(client!=null){
                GameUser gameUser=clientUserMap.get(client.getSessionId());
                if(gameUser!=null){
                    gameUser.setSocketIOClient(null);
                    AckEventRetryer ackEventRetryer=new AckEventRetryer(client,"gameOver",
                            gameUser,5000,3);
                    ackEventRetryer.execute();
                    String socketId = client.getSessionId().toString();
                    clientUserMap.remove(client.getSessionId());
                    gameLogService.leaveRoom(socketId);
                }
            }
        }
    }

    public void doUserLeaveRoom(SocketIOClient client) {
        synchronized (world) {
            if(client!=null){
                GameUser gameUser=clientUserMap.get(client.getSessionId());
                if(gameUser!=null){
                    gameUser.setSocketIOClient(null);
                    AckEventRetryer ackEventRetryer=new AckEventRetryer(client,"leaveRoom",
                            gameUser,5000,3);
                    ackEventRetryer.execute();
                    io.tankgo.tankserver.gameobject.player.BasePlayer basePlayer=dynPlayers.get(gameUser.getPlayId());
                    //结算
                    clearMoney(basePlayer);
                    try {
                        gridMap.get(basePlayer.getGx(), basePlayer.getGy()).remove(basePlayer.getId());
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    basePlayer.destroy();
                    dynPlayers.remove(basePlayer.getId());
                    String socketId = client.getSessionId().toString();
                    clientUserMap.remove(client.getSessionId());
                    gameLogService.leaveRoom(socketId);
                    gameLogService.destroyPlayer(basePlayer.getId());

                }
            }
        }
    }

    private void clearMoney(io.tankgo.tankserver.gameobject.player.BasePlayer basePlayer){
        String coin=basePlayer.getCoin();
        UserWalletDO userWalletDO=null;
        try {
            if(StringUtils.isNotEmpty(coin)){
                String[] coinArr=coin.split("\\.");
                String chainName=coinArr[0];
                String symbol=coinArr[1];
                userWalletDO = nutDao.fetch(UserWalletDO.class, Cnd.where("chain_name","=",chainName)
                                .and("account_name","=",basePlayer.getUserName()));
            }
        }catch (Exception e){

        }
        if(userWalletDO==null){
            return;
        }
        for(String coinKey: BidAskConfig.getCoins()){
            String[] coinArr=coinKey.split("\\.");
            String chainName=coinArr[0];
            String symbol=coinArr[1];
            BigDecimal amount=new BigDecimal(0);
            if(coinKey.equals(coin)){
                if(basePlayer.getHp()!=null){
                    if(basePlayer.getHp().compareTo(new BigDecimal("0"))>0){
                        amount=amount.add(basePlayer.getHp());
                    }
                }
            }
            HitReward hitReward = basePlayer.getRewards().get(coinKey);
            if(hitReward!=null&&hitReward.getAmount()!=null){
                amount=amount.add(hitReward.getAmount());
            }
            if(amount.compareTo(new BigDecimal("0"))>0){
                if(coinKey.equals(coin)){
                    //是自己的链，可以直接打款
                    gameLogService.quickTransfer(basePlayer.getUserName(),amount);
                }else{
                    //其他链，存起来，以后自己提现
                    UserBalanceDO balanceDO=nutDao.fetch(UserBalanceDO.class,Cnd.where("user_id","=",userWalletDO.getUserId())
                            .and("chain_name","=",chainName).and("token_symbol","=",symbol));
                    if(balanceDO==null){
                        balanceDO=new UserBalanceDO();
                        balanceDO.setCtime(new Date());
                        balanceDO.setPtime(balanceDO.getCtime());
                        balanceDO.setBalance(amount);
                        balanceDO.setChainName(chainName);
                        balanceDO.setTokenSymbol(symbol);
                        balanceDO.setUserId(userWalletDO.getUserId());
                        nutDao.insert(balanceDO,true,false,false);
                    }else{
//                        balanceDO.setId();
//                        balanceDO.setBalance(balanceDO.getBalance().add(amount));
//                        balanceDO.setPtime(new Date());
                        nutDao.update(UserBalanceDO.class,
                                Chain.makeSpecial("balance","+"+amount.setScale(4,BigDecimal.ROUND_HALF_UP).toPlainString()),
                                Cnd.where("id","=",balanceDO.getId()));
                    }
                }

            }
        }
    }



    public void doUserInput(GameUserInput gameUserInput) {
        String playId = gameUserInput.getPlayId();
        if (StringUtils.isNotEmpty(playId)) {
            io.tankgo.tankserver.gameobject.player.BasePlayer basePlayer = dynPlayers.get(playId);
            if (basePlayer != null) {
                basePlayer.doUserInput(gameUserInput);
            }
        }
    }

    private io.tankgo.tankserver.gameobject.player.PlayerListener playerListener = new io.tankgo.tankserver.gameobject.player.PlayerListener() {
        @Override
        public void onShoot(BulletParam bulletParam) {
            if (bulletParam == null) {
                return;
            }
            synchronized (world) {
                io.tankgo.tankserver.gameobject.bullet.BaseBullet baseBullet = io.tankgo.tankserver.gameobject.bullet.BulletFactory.createBullet(bulletParam.getSkin());
                if (baseBullet != null) {
                    baseBullet.init(bulletParam, world, worldTime);
                    dynBullets.add(baseBullet);
                }
            }
        }
    };

    private void addWorldWall(double wallsize) {

        double halfwall = wallsize / 2;

        Body wall1 = new Body();
        wall1.addFixture(Geometry.createRectangle(1.28, wallsize));
        wall1.setMass(MassType.INFINITE);
        wall1.translate(halfwall, 0);
        wall1.setUserData(new io.tankgo.tankserver.gameobject.wall.NormalWall());
        this.world.addBody(wall1);

        Body wall2 = new Body();
        wall2.addFixture(Geometry.createRectangle(1.28, wallsize));
        wall2.setMass(MassType.INFINITE);
        wall2.translate(-halfwall, 0);
        wall2.setUserData(new io.tankgo.tankserver.gameobject.wall.NormalWall());
        this.world.addBody(wall2);


        Body wall3 = new Body();
        wall3.addFixture(Geometry.createRectangle(wallsize, 1.28));
        wall3.setMass(MassType.INFINITE);
        wall3.translate(0, halfwall);
        wall3.setUserData(new io.tankgo.tankserver.gameobject.wall.NormalWall());
        this.world.addBody(wall3);

        Body wall4 = new Body();
        wall4.addFixture(Geometry.createRectangle(wallsize, 1.28));
        wall4.setMass(MassType.INFINITE);
        wall4.translate(0, -halfwall);
        wall4.setUserData(new io.tankgo.tankserver.gameobject.wall.NormalWall());
        this.world.addBody(wall4);


        Body wallRightTop1 = new Body();
        wallRightTop1.addFixture(Geometry.createRectangle(0.84, 2.56));
        wallRightTop1.setMass(MassType.INFINITE);
        wallRightTop1.translate(3.52, 1.72);
        wallRightTop1.setUserData(new io.tankgo.tankserver.gameobject.wall.NormalWall());
        this.world.addBody(wallRightTop1);

        Body wallRightTop2 = new Body();
        wallRightTop2.addFixture(Geometry.createRectangle(3.4, 0.91));
        wallRightTop2.setMass(MassType.INFINITE);
        wallRightTop2.translate(2.24, 3.455);
        wallRightTop2.setUserData(new io.tankgo.tankserver.gameobject.wall.NormalWall());
        this.world.addBody(wallRightTop2);

        Body wallLeftTop1 = new Body();
        wallLeftTop1.addFixture(Geometry.createRectangle(0.84, 2.56));
        wallLeftTop1.setMass(MassType.INFINITE);
        wallLeftTop1.translate(-3.52, 1.72);
        wallLeftTop1.setUserData(new io.tankgo.tankserver.gameobject.wall.NormalWall());
        this.world.addBody(wallLeftTop1);

        Body wallLeftTop2 = new Body();
        wallLeftTop2.addFixture(Geometry.createRectangle(3.4, 0.91));
        wallLeftTop2.setMass(MassType.INFINITE);
        wallLeftTop2.translate(-2.24, 3.455);
        wallLeftTop2.setUserData(new io.tankgo.tankserver.gameobject.wall.NormalWall());
        this.world.addBody(wallLeftTop2);

        Body wallRightDown1 = new Body();
        wallRightDown1.addFixture(Geometry.createRectangle(0.84, 2.56));
        wallRightDown1.setMass(MassType.INFINITE);
        wallRightDown1.translate(3.52, -1.85);
        wallRightDown1.setUserData(new io.tankgo.tankserver.gameobject.wall.NormalWall());
        this.world.addBody(wallRightDown1);

        Body wallRightDown2 = new Body();
        wallRightDown2.addFixture(Geometry.createRectangle(3.4, 0.91));
        wallRightDown2.setMass(MassType.INFINITE);
        wallRightDown2.translate(2.24, -3.585);
        wallRightDown2.setUserData(new io.tankgo.tankserver.gameobject.wall.NormalWall());
        this.world.addBody(wallRightDown2);

        Body wallLeftDown1 = new Body();
        wallLeftDown1.addFixture(Geometry.createRectangle(0.84, 2.56));
        wallLeftDown1.setMass(MassType.INFINITE);
        wallLeftDown1.translate(-3.52, -1.85);
        wallLeftDown1.setUserData(new io.tankgo.tankserver.gameobject.wall.NormalWall());
        this.world.addBody(wallLeftDown1);

        Body wallLeftDown2 = new Body();
        wallLeftDown2.addFixture(Geometry.createRectangle(3.4, 0.91));
        wallLeftDown2.setMass(MassType.INFINITE);
        wallLeftDown2.translate(-2.24, -3.585);
        wallLeftDown2.setUserData(new io.tankgo.tankserver.gameobject.wall.NormalWall());
        this.world.addBody(wallLeftDown2);


        this.addWallElement(-14.5, 14.5, 5, 1);
        this.addWallElement(-14.5, 13.5, 1, 1);
        //
        this.addWallElement(13, 12.5, 4, 1);
        this.addWallElement(11.5, 14, 1, 4);
        //
        this.addWallElement(-15.5, -13.5, 1, 3);
        this.addWallElement(-14.5, -15.5, 1, 3);
        //
        this.addWallElement(13, -14.5, 4, 1);
        this.addWallElement(14.5, -13, 1, 4);

    }


    private void addWallElement(double x, double y, int w, int h) {
        double hh = h * 0.64 + 0.24;
        double ww = w * 0.64 + 0.2;
        double xx = x * 0.64;
        double yy = y * 0.64 - 0.04;
        Body wall = new Body();
        wall.addFixture(Geometry.createRectangle(ww, hh));
        wall.setMass(MassType.INFINITE);
        wall.translate(xx, yy);
        wall.setUserData(new io.tankgo.tankserver.gameobject.wall.NormalWall());
        this.world.addBody(wall);
    }

    public void stop() {
        this.running = false;
        this.world.removeAllBodiesAndJoints();
        this.world.removeAllListeners();
    }

}
