package io.tankgo.tankserver.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.Id;
import org.nutz.dao.entity.annotation.Table;

import java.math.BigDecimal;
import java.util.Date;

@Table("chain_op_log")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChainOpLogDO {
    @Id
    private Long id;

    @Column("ctime")
    private Date ctime;

    @Column("ptime")
    private Date ptime;

    @Column("chain_name")
    private String chainName;

    @Column("block_num")
    private Long blockNum;

    @Column("txid")
    private String txid;

    @Column("contract")
    private String contract;

    @Column("action")
    private String action;

    @Column("tx_from")
    private String txFrom;

    @Column("tx_to")
    private String txTo;

    @Column("token_symbol")
    private String tokenSymbol;

    @Column("amount")
    private BigDecimal amount;

    @Column("memo")
    private String memo;

    @Column("args")
    private String args;

    @Column("status")
    /**
     * 0新建
     * 2不可逆
     * 11失败
     */
    private Integer status;
}
