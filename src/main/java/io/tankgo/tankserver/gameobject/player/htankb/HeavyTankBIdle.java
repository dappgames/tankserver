package io.tankgo.tankserver.gameobject.player.htankb;

import io.tankgo.tankserver.config.StateStr;
import io.tankgo.tankserver.gameobject.GameObject;
import io.tankgo.tankserver.gameobject.GameObjectState;
import io.tankgo.tankserver.gameobject.bullet.BaseBullet;
import io.tankgo.tankserver.gameobject.player.htanka.HeavyTankAPatrol;
import io.tankgo.tankserver.gameobject.player.stankc.SmallTankCDead;
import io.tankgo.tankserver.pojo.HitReward;

import java.math.BigDecimal;

public class HeavyTankBIdle implements GameObjectState {

    private HeavyTankB heavyTankB;

    public HeavyTankBIdle(HeavyTankB heavyTankB) {
        this.heavyTankB = heavyTankB;
    }



    @Override
    public HitReward hit(GameObject object) {
        if(object instanceof BaseBullet){
            BaseBullet bullet= (BaseBullet) object;
            HitReward hitReward = bullet.getHitDamage().hit(this.heavyTankB);
            if(this.heavyTankB.getHp().compareTo(new BigDecimal("0"))<1){
                this.heavyTankB.setGameObjectState(new HeavyTankBDead(this.heavyTankB));
            }
            return hitReward;
        }else{
            return new HitReward();
        }
    }

    @Override
    public void boom() {

    }

    @Override
    public void worldUpdate(double worldTime) {
        if(this.heavyTankB.getRobot()){
            if(this.heavyTankB.getStateTime()>0){
                if(worldTime-this.heavyTankB.getStateTime()>2){
                    this.heavyTankB.setGameObjectState(new HeavyTankBPatrol(heavyTankB));
                }
            }
        }
    }

    @Override
    public boolean checkRemove() {
        return false;
    }

    @Override
    public String getState() {
        return StateStr.idle;
    }

    @Override
    public void destroy() {
        this.heavyTankB =null;
    }
}
