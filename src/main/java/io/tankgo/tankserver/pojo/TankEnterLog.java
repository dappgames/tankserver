package io.tankgo.tankserver.pojo;

import io.tankgo.tankserver.entity.BaseEntity;
import lombok.*;
import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.Id;
import org.nutz.dao.entity.annotation.Table;

import java.math.BigDecimal;
import java.util.Date;


@Table("tank_enter_log")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = false)
public class TankEnterLog extends BaseEntity {
    private static final long serialVersionUID = -8974853457490183275L;
    @Id
    private Long id;
    @Column("socket_id")
    private String socketId;
    @Column("room_name")
    private String roomName;
    @Column("user_name")
    private String userName;
    @Column("player_id")
    private String playerId;
    @Column("tank_skin")
    private String tankSkin;
    @Column("join_time")
    private Date joinTime;
    @Column("leave_time")
    private Date leaveTime;
    @Column("create_time")
    private Date createTime;
    @Column("destroy_time")
    private Date destroyTime;
    @Column("chain_name")
    private String chainName;
    @Column("game_contract")
    private String gameContract;
    @Column("txid")
    private String txid;
    @Column("memo")
    private String memo;
    @Column("robot")
    private Integer robot;
    @Column("validate")
    private Integer validate;

    @Column("token_code")
    private String tokenCode;

    @Column("token_symbol")
    private String tokenSymbol;

    @Column("token_amount")
    private BigDecimal tokenAmount;

    @Column("receive_amount")
    private BigDecimal receiveAmount;

    @Column("token")
    private String token;

}
