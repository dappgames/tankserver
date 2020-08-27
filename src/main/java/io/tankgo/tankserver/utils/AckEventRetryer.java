package io.tankgo.tankserver.utils;

import com.corundumstudio.socketio.AckCallback;
import com.corundumstudio.socketio.SocketIOClient;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Slf4j
public class AckEventRetryer {
    private SocketIOClient socketIOClient;
    private String event;
    private Object data;
    private int timeout;
    private int trytimes;
    private int maxtimes;
    private ScheduledExecutorService checkExecutor;



    public AckEventRetryer(SocketIOClient socketIOClient,String event, Object data,int timeout,int maxtimes) {
        this.socketIOClient = socketIOClient;
        this.checkExecutor=Executors.newSingleThreadScheduledExecutor();
        this.event=event;
        this.data = data;
        this.timeout=timeout;
        this.trytimes=0;
        this.maxtimes=maxtimes;
    }

    public void execute(){
        if(socketIOClient!=null&&data!=null&&event!=null){
            checkExecutor.scheduleAtFixedRate(new Runnable() {
                @Override
                public void run() {
                    try {
                        trytimes+=1;
                        if(trytimes>maxtimes){
                            AckEventRetryer.this.release();
                        }
                        socketIOClient.sendEvent(event, new AckCallback<String>(String.class,timeout) {
                            @Override
                            public void onSuccess(String result) {
                                try {
                                    if(socketIOClient!=null&&checkExecutor!=null){
                                        log.info(result);
                                        AckEventRetryer.this.release();
                                    }else{
                                        AckEventRetryer.this.release();
                                    }
                                }catch (Exception e){

                                }
                            }
                        },data);
                    }catch (Exception e){

                    }
                }
            },1,timeout, TimeUnit.MILLISECONDS);


        }
    }

    public void release(){
        this.socketIOClient=null;
        this.data=null;
        this.event=null;
        checkExecutor.shutdownNow();
        this.checkExecutor=null;
        log.info("AckEventRetryer released");
    }
}
