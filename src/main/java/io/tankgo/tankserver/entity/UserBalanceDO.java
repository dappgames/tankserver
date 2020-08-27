package io.tankgo.tankserver.entity;


import lombok.*;
import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.Id;
import org.nutz.dao.entity.annotation.Table;

import java.math.BigDecimal;
import java.util.Date;

@Table("user_balance")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = false)
public class UserBalanceDO extends BaseEntity{
    private static final long serialVersionUID = -7677021990274226795L;

    @Id
    private Long id;

    @Column("ctime")
    private Date ctime;
    @Column("ptime")
    private Date ptime;

    @Column("user_id")
    private Long userId;
    @Column("chain_name")
    private String chainName;
    @Column("token_symbol")
    private String tokenSymbol;

    @Column("balance")
    private BigDecimal balance;

    @Column("version")
    private Long version;
}
