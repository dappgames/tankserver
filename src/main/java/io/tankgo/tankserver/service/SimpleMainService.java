package io.tankgo.tankserver.service;

import com.corundumstudio.socketio.*;
import com.corundumstudio.socketio.listener.ConnectListener;
import com.corundumstudio.socketio.listener.DataListener;
import com.corundumstudio.socketio.listener.DisconnectListener;
import io.tankgo.tankserver.bo.ChatObject;
import io.tankgo.tankserver.pojo.*;
import io.tankgo.tankserver.utils.AckEventRetryer;
import io.tankgo.tankserver.utils.RespUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.List;
import java.util.UUID;

@Component
@Slf4j
public class SimpleMainService implements DisposableBean {


    @Autowired
    private SimpleRoom simpleRoom;

    private SocketIOServer server;

    @PostConstruct
    private void init() {
        Configuration config = new Configuration();
        config.setPort(10900);
        try {
            server.stop();
        } catch (Exception e) {

        }
        try {
            Thread.sleep(2500);
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

    public SocketIOServer getServer() {
        return server;
    }


    public void initSocketListener() {

        server.addEventListener("chatevent", ChatObject.class, new DataListener<ChatObject>() {
            @Override
            public void onData(SocketIOClient client, ChatObject data, AckRequest ackRequest) {
                // broadcast messages to all clients
                server.getBroadcastOperations().sendEvent("chatevent", data);
            }
        });

        server.addEventListener("simpleInput", GameUserInput.class, new DataListener<GameUserInput>() {
            @Override
            public void onData(SocketIOClient client, GameUserInput data, AckRequest ackRequest) {
                // broadcast messages to all clients
                simpleRoom.onUserInput(data);
            }
        });



        server.addConnectListener(new ConnectListener() {
            @Override
            public void onConnect(SocketIOClient socketIOClient) {
                String uuid = socketIOClient.getSessionId().toString();
                log.info(uuid + ":" + "onConnect");
            }
        });
        server.addDisconnectListener(new DisconnectListener() {
            @Override
            public void onDisconnect(SocketIOClient socketIOClient) {
                String uuid = socketIOClient.getSessionId().toString();
                log.info(uuid + ":" + "onDisconnect");
            }
        });
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
