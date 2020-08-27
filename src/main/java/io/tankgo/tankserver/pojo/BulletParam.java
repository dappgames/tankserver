package io.tankgo.tankserver.pojo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BulletParam {
    private String playId;
    private String grp;
    private Boolean robot;
    private String skin;
    private String coin;
    private BigDecimal amount;
    private FireVec fireVec;
}
