package io.tankgo.tankserver.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HitTip {
    private String playId;
    private String coin;
    private String chainName;
    private String symbol;
    private BigDecimal amount;
    private int dir;

    public HitTip(io.tankgo.tankserver.gameobject.player.BasePlayer basePlayer, HitReward hitReward, int dir){
        this.playId=basePlayer.getId();
        this.dir=dir;
        this.coin=hitReward.getCoin();
        this.amount=hitReward.getAmount();
        if(StringUtils.isNotEmpty(coin)){
            try {
                String[] cs=coin.split("\\.");
                this.chainName=cs[0];
                this.symbol=cs[1];
            }catch (Exception e){

            }
        }
    }
}
