package io.tankgo.tankserver.pojo;

import io.tankgo.tankserver.config.BidAskConfig;
import lombok.Data;

import java.math.BigDecimal;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Data
public class HitDamage {
    /**
     * chainName,symbol,damage
     */
    private ConcurrentMap<String, Double> map = new ConcurrentHashMap<>();

    public void create(BulletParam bulletParam) {
        double rate=1;
        if(bulletParam.getCoin()!=null&&bulletParam.getCoin().equals("free.BOOM")){
            rate=0.1;
        }
        map.put("cocos.COCOS", 0.002*rate);
        map.put("cocos_test.COCOS", 0.002*rate);
        map.put("eos.EOS", 0.01*rate);
        map.put("trx.TRX",0.01*rate);
        map.put("iost.IOST",0.01*rate);
        //TODO 伤害值随充值提高
        map.put("free.BOOM", 0.01*rate);
    }

    /**
     * @param losePlayer 被打的坦克，掉血
     * @return
     */
    public HitReward hit(io.tankgo.tankserver.gameobject.player.BasePlayer losePlayer) {
        String coin = losePlayer.getCoin();
        Double damage = map.get(coin);
        if (damage == null) {
            damage = 0.1;
        }
        HitReward hitReward = new HitReward();
        hitReward.setCoin(coin);
        hitReward.setSuccess(true);
        BigDecimal hp = losePlayer.getHp();

        BigDecimal hurt = new BigDecimal(damage / BidAskConfig.getPrice(coin)).setScale(4,BigDecimal.ROUND_HALF_UP);
        if (hurt.compareTo(hp) > 0) {
            if (hp.compareTo(new BigDecimal(0)) < 1) {
                hp = new BigDecimal("0");
                hitReward.setSuccess(false);
            }
            hurt = hp;
            hp=new BigDecimal("0");
        } else {
            hp = hp.subtract(hurt);
        }
        losePlayer.setHp(hp);
        hitReward.setAmount(hurt);

        return hitReward;
    }
}
