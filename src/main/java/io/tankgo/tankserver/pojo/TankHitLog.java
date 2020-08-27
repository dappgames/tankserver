package io.tankgo.tankserver.pojo;

import io.tankgo.tankserver.entity.BaseEntity;
import lombok.*;
import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.Id;
import org.nutz.dao.entity.annotation.Table;

import java.util.Date;

@Table("tank_hit_log")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = false)
public class TankHitLog extends BaseEntity {
    private static final long serialVersionUID = 8661426952648624272L;

    @Id
    private Long id;
    @Column("ctime")
    private Date ctime;
    @Column("ptime")
    private Date ptime;

    @Column("from_player_id")
    private String fromPlayerId;
    @Column("to_player_id")
    private String toPlayerId;

    @Column("from_account")
    private String fromAccount;

    @Column("to_account")
    private String toAccount;

    @Column("robot")
    private Integer robot;

    @Column("validate")
    private Integer validate;

    @Column("token_code")
    private String tokenCode;

    @Column("token_symbol")
    private String tokenSymbol;

    @Column("token_amount")
    private String tokenAmount;

    @Column("payed")
    private Integer payed;
}
