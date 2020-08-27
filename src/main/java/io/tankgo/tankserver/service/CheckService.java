package io.tankgo.tankserver.service;

import io.tankgo.tankserver.entity.BlockCheckLogDO;
import org.nutz.dao.Cnd;
import org.nutz.dao.impl.NutDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Component
public class CheckService {

    private ScheduledExecutorService checkExecutor = Executors.newSingleThreadScheduledExecutor();
    private ScheduledExecutorService fakeExecutor = Executors.newSingleThreadScheduledExecutor();

    @Autowired
    private NutDao nutDao;

    @Autowired
    private GameMainService gameMainService;

    @PostConstruct
    public void init(){
        checkExecutor.scheduleWithFixedDelay(new Runnable() {
            @Override
            public void run() {
                try {
                    List<BlockCheckLogDO> blockCheckLogDOS=nutDao.query(BlockCheckLogDO.class,
                            Cnd.where("status","=",2));
                    if(blockCheckLogDOS!=null){
                        for(BlockCheckLogDO logDO:blockCheckLogDOS){
                            if(logDO.getFastEvent().equals("allowLogin")){
                                gameMainService.allowLogin(logDO.getSocketId(),logDO.getWalletId());
                                logDO.setStatus(3);
                                nutDao.updateIgnoreNull(logDO);
                            }
                        }
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        },500,500, TimeUnit.MILLISECONDS);
        fakePass();
    }

    private void fakePass(){
        fakeExecutor.scheduleWithFixedDelay(new Runnable() {
            @Override
            public void run() {
                try {
                    List<BlockCheckLogDO> blockCheckLogDOS=nutDao.query(BlockCheckLogDO.class,
                            Cnd.where("status","=",0).and("fast_event","=","allowLogin"));
                    if(blockCheckLogDOS!=null){
                        for(BlockCheckLogDO logDO:blockCheckLogDOS){
                            logDO.setStatus(2);
                            nutDao.updateIgnoreNull(logDO);
                        }
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        },500,500,TimeUnit.MILLISECONDS);
    }
}
