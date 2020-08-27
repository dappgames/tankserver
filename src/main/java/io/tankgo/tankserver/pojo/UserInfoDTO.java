package io.tankgo.tankserver.pojo;

import io.tankgo.tankserver.entity.BaseEntity;
import lombok.*;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = false)
public class UserInfoDTO extends BaseEntity {
    private static final long serialVersionUID = -3716293992806450681L;
    private Long userId;
    private Long userWalletId;
    private String socketId;
    private String nickName;
    private String chainName;
    private String accountName;
    private String checkMemo;
    private Long reqTime;
    private String signMsg;
    private String sk;

    private List<UserBalanceDTO> balances;
}
