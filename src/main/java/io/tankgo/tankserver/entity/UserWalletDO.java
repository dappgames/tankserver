package io.tankgo.tankserver.entity;

import lombok.*;
import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.Id;
import org.nutz.dao.entity.annotation.Table;

import java.util.Date;

@Table("user_wallet")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = false)
public class UserWalletDO extends BaseEntity{
    private static final long serialVersionUID = -1963595265431906371L;
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
    @Column("account_name")
    private String accountName;
    @Column("sk")
    private String sk;
}
