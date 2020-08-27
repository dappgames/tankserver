package io.tankgo.tankserver.pojo;

import com.corundumstudio.socketio.SocketIOClient;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class GameUser implements Serializable{
    private static final long serialVersionUID = -6029959864872032203L;
    private Long userId;
    private String playId;
    private String key;
    private String userName;
    private String skin;
    private String socketId;
    private String coin;
    private BigDecimal amount;
    private SocketIOClient socketIOClient;
    private String token;
    private Integer lastGx;
    private Integer lastGy;
}
