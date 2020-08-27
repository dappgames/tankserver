package io.tankgo.tankserver.pojo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class HitReward {
    private String coin;
    private BigDecimal amount=new BigDecimal("0");
    @JsonIgnore
    private Boolean success=false;

    public void add(HitReward hitReward){
        this.amount=this.amount.add(hitReward.getAmount());
    }
}
