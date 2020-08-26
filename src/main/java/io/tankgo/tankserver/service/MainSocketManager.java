package io.tankgo.tankserver.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.corundumstudio.socketio.*;
import com.corundumstudio.socketio.listener.ConnectListener;
import com.corundumstudio.socketio.listener.DataListener;
import com.corundumstudio.socketio.listener.DisconnectListener;
import io.tankgo.tankserver.bo.ChatObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.Date;
import java.util.UUID;

@Component
@Slf4j
public class MainSocketManager implements DisposableBean {

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


    public void initSocketListener() {

        server.addEventListener("chatevent", ChatObject.class, new DataListener<ChatObject>() {
            @Override
            public void onData(SocketIOClient client, ChatObject data, AckRequest ackRequest) {
                // broadcast messages to all clients
                server.getBroadcastOperations().sendEvent("chatevent", data);
            }
        });


        server.addConnectListener(new ConnectListener() {
            @Override
            public void onConnect(SocketIOClient socketIOClient) {
                String uuid = socketIOClient.getSessionId().toString();
                log.info("onConnect. uuid={}", uuid );
            }
        });
        server.addDisconnectListener(new DisconnectListener() {
            @Override
            public void onDisconnect(SocketIOClient socketIOClient) {
                String uuid = socketIOClient.getSessionId().toString();
                log.info("onDisconnect. uuid={}", uuid );
                try {
                    socketIOClient.disconnect();
                } catch (Exception e) {

                }
            }
        });
    }

    public void disconnectClient(String sid) {
        UUID uuid = UUID.fromString(sid);
        SocketIOClient client = server.getClient(uuid);
        if (client != null) {
            client.disconnect();
        }
    }

    @PreDestroy
    public void stop() {
        System.out.println("PreDestroy");
        try {
            server.stop();
            log.info("PreDestroy stop");
        } catch (Exception e) {

        }
    }

    @Override
    public void destroy() {
        System.out.println("destroy");
        try {
            server.stop();
            log.info("destroy stop");
        } catch (Exception e) {

        }
    }
}
