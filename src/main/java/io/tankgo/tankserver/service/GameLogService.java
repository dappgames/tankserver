package io.tankgo.tankserver.service;

import io.tankgo.tankserver.pojo.GameUser;
import io.tankgo.tankserver.pojo.RespEntity;
import io.tankgo.tankserver.pojo.TankEnterLog;
import io.tankgo.tankserver.pojo.TankHitLog;
import io.tankgo.tankserver.utils.MyUtils;
import io.tankgo.tankserver.utils.RespUtil;
import org.nutz.dao.Cnd;
import org.nutz.dao.impl.NutDao;
import org.nutz.http.Http;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

@Component
public class GameLogService {

    @Autowired
    private NutDao nutDao;

    @Autowired
    private GameRoom gameRoom;

    public boolean hit(io.tankgo.tankserver.gameobject.player.BasePlayer player1, io.tankgo.tankserver.gameobject.player.BasePlayer player2){
        Date now=new Date();
        try {
            if (player1 != null&&player2!=null) {
//                if ((player1.getRobot() && player2.getRobot())) {
//
//                }
                TankHitLog tankHitLog = TankHitLog.builder()
                        .ctime(now).ptime(now)
                        .fromPlayerId(player1.getId())
                        .toPlayerId(player2.getId())
                        .fromAccount(player1.getUserName())
                        .toAccount(player2.getUserName())
                        .robot(player1.getRobot()?1:0)
                        .validate(0)
                        .tokenCode("COCOS")
                        .tokenSymbol("COCOS")
                        .tokenAmount("1")
                        .payed(0)
                        .build();
                TankHitLog hitLog = nutDao.insert(tankHitLog,true,false,false);
                if(hitLog!=null){
                    return true;
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }

    public RespEntity enterRoom(GameUser gameUser, GameRoom gameRoom){


        Date now = new Date();
        String memo= MyUtils.shortUuid();
        memo="join:"+memo;
        try {
            String[] coinArray=gameUser.getCoin().split("\\.");
            TankEnterLog tankEnterLog = TankEnterLog.builder()
                    .socketId(gameUser.getSocketId())
                    .roomName(gameRoom.getName())
                    .userName(gameUser.getUserName())
//                    .playerId(gameUser.getPlayId())
                    .tankSkin(gameUser.getSkin())
//                    .joinTime(now)
//                    .leaveTime(null)
                    .createTime(now)
//                    .destroyTime(null)
                    .chainName(coinArray[0])
                    .gameContract("cocostankgo")
//                    .transactionId("xxxx")
                    .memo(memo)
                    .robot(0)
                    .validate(0)
                    .tokenCode("COCOS")
                    .tokenSymbol(coinArray[1])
                    .tokenAmount(gameUser.getAmount())
                    .token(MyUtils.shortUuid())
                    .build();
            tankEnterLog = nutDao.insert(tankEnterLog);
            if(tankEnterLog!=null){
                return RespUtil.success(tankEnterLog);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return RespUtil.fail(-1,"prepare join room fail");
    }

    public boolean firstEnterRoom(String token,String socketId,  io.tankgo.tankserver.gameobject.player.BasePlayer basePlayer){
        Date now = new Date();
        try{
            TankEnterLog tankEnterLog = nutDao.fetch(TankEnterLog.class,
                    Cnd.where("token", "=", token));
            if (tankEnterLog != null) {
                TankEnterLog updateLog=TankEnterLog.builder()
                        .id(tankEnterLog.getId())
                        .socketId(socketId)
                        .joinTime(now)
                        .playerId(basePlayer.getId())
                        .tankSkin(basePlayer.getSkin())
                        .build();
                int ret = nutDao.updateIgnoreNull(updateLog);
                if(ret>0){
                    return true;
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }

    public boolean leaveRoom(String socketId){
        TankEnterLog tankEnterLog = nutDao.fetch(TankEnterLog.class,
                Cnd.where("socket_id", "=", socketId));
        if (tankEnterLog != null) {
            TankEnterLog updateTank= TankEnterLog.builder()
                    .id(tankEnterLog.getId())
                    .leaveTime(new Date())
                    .build();
            int ret = nutDao.updateIgnoreNull(updateTank);
            if(ret>0){
                return true;
            }
        }
        return false;
    }

    public boolean destroyPlayer(String playId){
        try {
            Date now = new Date();
            TankEnterLog tankEnterLog = nutDao.fetch(TankEnterLog.class,
                    Cnd.where("player_id", "=", playId));
            if (tankEnterLog != null) {


                tankEnterLog.setDestroyTime(now);
                int ret = nutDao.update(tankEnterLog);
                if(ret>0){
                    return true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public TankEnterLog fetchActiveEnter(String userName,String roomName){
        try {
            TankEnterLog tankEnterLog = nutDao.fetch(TankEnterLog.class,
                    Cnd.where("user_name", "=", userName)
                            .and("room_name", "=", roomName).and("validate","=",1)
                            .and("destroy_time","is",null).orderBy("id","desc"));
            return tankEnterLog;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    public List<TankHitLog> checkNeedPay(){
        List<TankHitLog> tankHitLogs = nutDao.query(TankHitLog.class,
                Cnd.where("payed","=",0).and("robot","=","0").limit(1,10));
        if(tankHitLogs!=null&&tankHitLogs.size()>0){
            for(TankHitLog hitLog:tankHitLogs){
                doTransfer(hitLog);
            }
        }
        return tankHitLogs;
    }

    public void doTransfer(TankHitLog hitLog){
        String tokenSymbol = hitLog.getTokenSymbol();
        if(tokenSymbol.equals("COCOS")){
            String url = "http://47.244.51.198:3000/cocos/transfer";
            HashMap<String,Object> params=new HashMap<>();
            params.put("from","cocostankgo");
            params.put("to",hitLog.getFromAccount());
            params.put("asset","COCOS");
            params.put("amount",hitLog.getTokenAmount());
            params.put("memo","win from "+hitLog.getToAccount());
            Http.get(url,params,5000);
            hitLog.setPayed(1);
            hitLog.setValidate(1);
            nutDao.updateIgnoreNull(hitLog);
        }
    }

    public void quickTransfer(String userName, BigDecimal amount){
        String url = "http://chain.exit3d.io:3000/cocos/transfer";
        HashMap<String,Object> params=new HashMap<>();
        params.put("from","cocostankgo");
        params.put("to",userName);
        params.put("asset","COCOS");
        params.put("amount",amount);
        params.put("memo","cocostankgo reward:"+MyUtils.shortUuid());
        String ret = Http.get(url,params,5000).getContent();
        System.out.println(ret);
    }



//    public List<TankEnterLog> checkRoomEnter(GameRoom gameRoom){
//        Date startTime=new Date();
//        startTime= DateUtils.addMins(startTime,-180);
//        List<TankEnterLog> tankEnterLogs=nutDao.query(TankEnterLog.class,
//                Cnd.where("room_name","=",gameRoom.getRoomName())
//                        .and("validate","=","0")
//                        .and("robot","=","0")
//                        .and("create_time",">",startTime)
//        );
//        if(tankEnterLogs!=null&&tankEnterLogs.size()>0){
//
//        }else{
//            return null;
//        }
//        if(gameRoom.getRoomConfig().getRoomName().equals("free")){
//            List<TankEnterLog> resultList=new ArrayList<>();
//            for(TankEnterLog enterLog:tankEnterLogs){
//                enterLog.setValidate(1);
//                enterLog.setReceiveAmount(enterLog.getTokenAmount());
//                resultList.add(enterLog);
//                nutDao.updateIgnoreNull(enterLog);
//            }
//            return resultList;
//        }
//        if(gameRoom.getRoomConfig().getRoomName().equals("cocos")){
//            String url = "http://47.244.51.198:3000/cocos/queryAccountOperations?account=cocostankgo&limit=20";
//            Response response = Http.get(url,5000);
//            String respStr=response.getContent();
//            JSONObject json=JSON.parseObject(respStr);
//            if(json==null){
//                return null;
//            }
//            JSONArray jary=json.getJSONArray("data");
//            if(jary==null){
//                return null;
//            }
//            List<TransferDTO> transferDTOList=jary.toJavaList(TransferDTO.class);
//            if(transferDTOList!=null&&transferDTOList.size()>0){
//                List<TankEnterLog> resultLogList=new ArrayList<>();
//                HashMap<String,TransferDTO> resultMap=new HashMap<>();
//                for(TransferDTO transferDTO:transferDTOList){
//                    String to = transferDTO.getTo();
//                    if(gameRoom.getRoomConfig().getGameContract().equals(to)){
//                        resultMap.put(transferDTO.getMemo(),transferDTO);
//                    }
//                }
//                for(TankEnterLog enterLog:tankEnterLogs){
//                    String memo = enterLog.getMemo();
//                    TransferDTO transferDTO=resultMap.get(memo);
//                    if(transferDTO!=null){
//                        TankEnterLog updateLog= TankEnterLog.builder()
//                                .id(enterLog.getId())
//                                .validate(1)
//                                .receiveAmount(transferDTO.getAmount())
//                                .build();
//                        enterLog.setValidate(1);
//                        enterLog.setReceiveAmount(transferDTO.getAmount());
//                        resultLogList.add(enterLog);
//                        nutDao.updateIgnoreNull(updateLog);
//                    }
//                }
//                return resultLogList;
//            }
//        }
//        return null;
//    }


}
