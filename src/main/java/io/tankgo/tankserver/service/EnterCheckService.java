package io.tankgo.tankserver.service;

import io.tankgo.tankserver.entity.ChainOpLogDO;
import io.tankgo.tankserver.enums.EnterLogStatus;
import io.tankgo.tankserver.pojo.GameUser;
import io.tankgo.tankserver.pojo.TankEnterLog;
import io.tankgo.tankserver.utils.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.nutz.dao.Cnd;
import org.nutz.dao.impl.NutDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class EnterCheckService {
    private ScheduledExecutorService checkExecutor = Executors.newSingleThreadScheduledExecutor();
    @Autowired
    private NutDao nutDao;

    @Autowired
    private GameMainService gameMainService;

    @Autowired
    private GameRoom gameRoom;


    @PostConstruct
    public void init(){
        checkExecutor.scheduleWithFixedDelay(new Runnable() {
            @Override
            public void run() {
                try {
                    String roomName=gameRoom.getName();
                    Date oldDate= DateUtils.getMinuteBefore(new Date(),5);
                    List<TankEnterLog> tankEnterLogs=nutDao.query(TankEnterLog.class, Cnd
                            .where("room_name","=",roomName)
                            .and("validate","<", EnterLogStatus.SUCCESS));
                    if(!CollectionUtils.isEmpty(tankEnterLogs)){
                        for(TankEnterLog enterLog:tankEnterLogs){
                            String chainName=enterLog.getChainName();
                            String userName=enterLog.getUserName();
                            String memo=enterLog.getMemo();
                            ChainOpLogDO opLogDO = nutDao.fetch(ChainOpLogDO.class, Cnd
                                    .where("chain_name","=",chainName)
                                    .and("tx_from","=",userName)
                                    .and("memo","=",memo));
                            if(opLogDO!=null){
                                enterLog.setValidate(1);
                                log.info("enterLog"+enterLog.getId()+"validate");
                                nutDao.updateIgnoreNull(enterLog);
                                GameUser gameUser=new GameUser();
                                gameUser.setUserName(enterLog.getUserName());
                                gameUser.setSkin(enterLog.getTankSkin());
                                gameUser.setCoin(enterLog.getChainName()+"."+enterLog.getTokenSymbol());
                                gameUser.setToken(enterLog.getToken());
                                gameUser.setSocketId(enterLog.getSocketId());
                                gameMainService.allowJoin(gameUser);
                            }else{
                                if(enterLog.getCreateTime().before(oldDate)){
                                    enterLog.setValidate(EnterLogStatus.FAIL);
                                    nutDao.updateIgnoreNull(enterLog);
                                }
                            }
                        }
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        },500,300, TimeUnit.MILLISECONDS);
    }
}
