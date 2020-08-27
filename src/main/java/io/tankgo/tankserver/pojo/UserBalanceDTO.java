package io.tankgo.tankserver.pojo;

import io.tankgo.tankserver.entity.BaseEntity;
import lombok.*;
import org.nutz.dao.entity.annotation.Column;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = false)
public class UserBalanceDTO extends BaseEntity {
    private static final long serialVersionUID = -4149539174416988371L;

    @Column("user_id")
    private Long userId;
    @Column("chain_name")
    private String chainName;
    @Column("token_symbol")
    private String tokenSymbol;

    @Column("balance")
    private BigDecimal balance;
}
