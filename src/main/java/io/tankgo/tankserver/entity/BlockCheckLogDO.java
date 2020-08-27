package io.tankgo.tankserver.entity;

import lombok.*;
import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.Id;
import org.nutz.dao.entity.annotation.Table;

import java.util.Date;

@Table("block_check_log")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = false)
public class BlockCheckLogDO extends BaseEntity {

    private static final long serialVersionUID = 7786416877845532256L;

    @Id
    private Long id;

    @Column("ctime")
    private Date ctime;

    @Column("ptime")
    private Date ptime;

    @Column("wallet_id")
    private Long walletId;

    @Column("socket_id")
    private String socketId;

    @Column("ext_args")
    private String extArgs;

    @Column("chain_name")
    private String chainName;

    @Column("contract_name")
    private String contractName;

    @Column("action_name")
    private String actionName;

    @Column("from_account")
    private String fromAccount;

    @Column("to_account")
    private String toAccount;

    @Column("quantity")
    private String quantity;

    @Column("memo")
    private String memo;

    @Column("actual_quantity")
    private String actualQuantity;

    @Column("status")
    private Integer status;

    @Column("try_cnt")
    private Integer tryCnt;

    @Column("fast_event")
    private String fastEvent;

    @Column("finish_event")
    private String finishEvent;

    @Column("fail_event")
    private String failEvent;
}
