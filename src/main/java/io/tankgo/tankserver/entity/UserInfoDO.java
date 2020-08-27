package io.tankgo.tankserver.entity;

import lombok.*;
import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.Id;
import org.nutz.dao.entity.annotation.Table;

import java.util.Date;

@Table("user_info")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = false)
public class UserInfoDO extends BaseEntity {
    private static final long serialVersionUID = -1138737348336286434L;
    @Id
    private Long id;
    @Column("ctime")
    private Date ctime;
    @Column("ptime")
    private Date ptime;
    @Column("nick_name")
    private String nickName;
    @Column("account_num")
    private Integer accountNum;
    @Column("mail")
    private String mail;
    @Column("pwd")
    private String pwd;
}
