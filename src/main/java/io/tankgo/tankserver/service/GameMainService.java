package io.tankgo.tankserver.service;

import com.corundumstudio.socketio.*;
import com.corundumstudio.socketio.listener.ConnectListener;
import com.corundumstudio.socketio.listener.DataListener;
import com.corundumstudio.socketio.listener.DisconnectListener;
import io.tankgo.tankserver.bo.ChatObject;
import io.tankgo.tankserver.pojo.*;
import io.tankgo.tankserver.utils.AckEventRetryer;
import io.tankgo.tankserver.utils.CryptUtils;
import io.tankgo.tankserver.utils.RespUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.InputStream;
import java.util.List;
import java.util.UUID;

@Component
@Slf4j
public class GameMainService implements DisposableBean {

    @Autowired
    private GameLogService gameLogService;
    @Autowired
    private UserInfoService userInfoService;

    @Autowired
    private GameRoom gameRoom;

    private static Logger logger = LoggerFactory.getLogger(GameMainService.class);
    private SocketIOServer server;

    @PostConstruct
    private void init() {
        Configuration config = new Configuration();
        config.setPort(10800);
        try {
            server.stop();
        } catch (Exception e) {

        }
        try {
            Thread.sleep(2000);
        } catch (Exception e) {

        }
        SocketConfig socketConfig = new SocketConfig();
        socketConfig.setReuseAddress(true);
        config.setSocketConfig(socketConfig);
        config.setRandomSession(true);
        server = new SocketIOServer(config);
        server.start();
        initSocketListener();
    }


    public void allowLogin(String socketId, Long userWalletId){
        if(StringUtils.isEmpty(socketId)){
            return;
        }
        SocketIOClient socketIOClient = server.getClient(UUID.fromString(socketId));
        if(socketIOClient==null){
            return;
        }
        UserInfoDTO userInfoDTO = userInfoService.getUserInfoSkByWalletId(userWalletId);
        AckEventRetryer ackEventRetryer=new AckEventRetryer(socketIOClient,"allowLogin",
                userInfoDTO,5000,3);
        ackEventRetryer.execute();
    }

    public void testAck(GameUser gameUser){
        if(StringUtils.isEmpty(gameUser.getSocketId())){
            return;
        }
        SocketIOClient socketIOClient = server.getClient(UUID.fromString(gameUser.getSocketId()));
        if(socketIOClient==null){
            return;
        }
        AckEventRetryer ackEventRetryer=new AckEventRetryer(socketIOClient,"allowJoin",
                gameUser,2000,5);
        ackEventRetryer.execute();
    }

    public void allowJoin(GameUser gameUser){
        if(StringUtils.isEmpty(gameUser.getSocketId())){
            return;
        }
        SocketIOClient socketIOClient = server.getClient(UUID.fromString(gameUser.getSocketId()));
        if(socketIOClient==null){
            return;
        }
        AckEventRetryer ackEventRetryer=new AckEventRetryer(socketIOClient,"allowJoin",
                gameUser,3000,2);
        ackEventRetryer.execute();
    }

    public void hitTips(String socketId, List<HitTip> hitTips){
        if(StringUtils.isEmpty(socketId)){
            return;
        }
        SocketIOClient socketIOClient = server.getClient(UUID.fromString(socketId));
        if(socketIOClient==null){
            return;
        }
        socketIOClient.sendEvent("hitTips",hitTips);
//        AckEventRetryer ackEventRetryer=new AckEventRetryer(socketIOClient,"hitTips",
//                hitTips,5000,3);
//        ackEventRetryer.execute();
    }

    public void initSocketListener() {

        server.addEventListener("chatevent", io.tankgo.tankserver.bo.ChatObject.class, new DataListener<io.tankgo.tankserver.bo.ChatObject>() {
            @Override
            public void onData(SocketIOClient client, ChatObject data, AckRequest ackRequest) {
                // broadcast messages to all clients
                server.getBroadcastOperations().sendEvent("chatevent", data);
            }
        });

        server.addEventListener("login", UserInfoDTO.class, new DataListener<UserInfoDTO>() {
            @Override
            public void onData(SocketIOClient client, UserInfoDTO userInfoDTO, AckRequest ackRequest) throws Exception {
                if(ackRequest.isAckRequested()){
                    userInfoDTO.setSocketId(client.getSessionId().toString());
                    RespEntity respEntity = userInfoService.login(userInfoDTO);
                    ackRequest.sendAckData(respEntity);
                }
            }
        });

        server.addEventListener("preJoinRoom", GameUser.class, new DataListener<GameUser>() {
            @Override
            public void onData(SocketIOClient socketIOClient, GameUser gameUser, AckRequest ackRequest) throws Exception {
                if (ackRequest.isAckRequested()) {
                    //TODO 如果已经在房间里，禁止再次进入
                    //TODO 如果已经在房间里，但前端未消费，则直接进入


                    gameUser.setSocketId(socketIOClient.getSessionId().toString());
                    RespEntity respEntity = gameLogService.enterRoom(gameUser,gameRoom);
                    ackRequest.sendAckData(respEntity);
                    logger.info("preJoinRoom");
                }
            }
        });

        server.addEventListener("joinRoom", GameUser.class, new DataListener<GameUser>() {
            @Override
            public void onData(SocketIOClient socketIOClient, GameUser gameUser, AckRequest ackRequest) throws Exception {
                if (ackRequest.isAckRequested()) {
                    gameUser.setSocketId(socketIOClient.getSessionId().toString());
                    gameUser.setSocketIOClient(socketIOClient);
                    GameUser ret = gameRoom.doUserJoinRoom(gameUser);
                    ackRequest.sendAckData(RespUtil.success(ret));
                    logger.info("joinRoom");
                }
            }
        });

        server.addEventListener("leaveRoom", GameUser.class, new DataListener<GameUser>() {
            @Override
            public void onData(SocketIOClient socketIOClient, GameUser gameUser, AckRequest ackRequest) throws Exception {
                doUserLeaveRoom(socketIOClient);
            }
        });

        server.addEventListener("userInput", GameUserInput.class, new DataListener<GameUserInput>() {
            @Override
            public void onData(SocketIOClient socketIOClient, GameUserInput gameUserInput, AckRequest ackRequest) throws Exception {
                doUserInput(gameUserInput);
            }
        });

        server.addConnectListener(new ConnectListener() {
            @Override
            public void onConnect(SocketIOClient socketIOClient) {
                String uuid = socketIOClient.getSessionId().toString();
                logger.info(uuid + ":" + "onConnect");
            }
        });
        server.addDisconnectListener(new DisconnectListener() {
            @Override
            public void onDisconnect(SocketIOClient socketIOClient) {
                String uuid = socketIOClient.getSessionId().toString();
                logger.info(uuid + ":" + "onDisconnect");
                doUserLeaveRoom(socketIOClient);
                try {
                    socketIOClient.disconnect();
                } catch (Exception e) {

                }
            }
        });
    }


    public void doUserInput(GameUserInput gameUserInput){

        if(gameRoom!=null){
            gameRoom.doUserInput(gameUserInput);
        }
    }

    public void doUserLeaveRoom(SocketIOClient socketIOClient){
        gameRoom.doUserLeaveRoom(socketIOClient);
    }


    public void disconnectClient(String sid) {
        UUID uuid = UUID.fromString(sid);
        SocketIOClient client=server.getClient(uuid);
        if(client!=null){
            client.disconnect();
        }
    }

    @PreDestroy
    public void stop() {
        System.out.println("PreDestroy");
        try {
            server.stop();
        } catch (Exception e) {

        }
    }

    @Override
    public void destroy() {
        System.out.println("destroy");
        try {
            server.stop();
        } catch (Exception e) {

        }
    }
}
