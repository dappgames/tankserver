package io.tankgo.tankserver.gameobject.player.htanka;

import io.tankgo.tankserver.config.StateStr;
import io.tankgo.tankserver.gameobject.GameObject;
import io.tankgo.tankserver.gameobject.GameObjectState;
import io.tankgo.tankserver.gameobject.bullet.BaseBullet;
import io.tankgo.tankserver.gameobject.player.enemytank.EnemyTankPatrol;
import io.tankgo.tankserver.gameobject.player.stankc.SmallTankCDead;
import io.tankgo.tankserver.pojo.HitReward;

import java.math.BigDecimal;

public class HeavyTankAIdle implements GameObjectState {

    private HeavyTankA heavyTankA;

    public HeavyTankAIdle(HeavyTankA heavyTankA) {
        this.heavyTankA = heavyTankA;
    }



    @Override
    public HitReward hit(GameObject object) {
        if(object instanceof BaseBullet){
            BaseBullet bullet= (BaseBullet) object;
            HitReward hitReward = bullet.getHitDamage().hit(this.heavyTankA);
            if(this.heavyTankA.getHp().compareTo(new BigDecimal("0"))<1){
                this.heavyTankA.setGameObjectState(new HeavyTankADead(this.heavyTankA));
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
        if(this.heavyTankA.getRobot()){
            if(this.heavyTankA.getStateTime()>0){
                if(worldTime-this.heavyTankA.getStateTime()>2){
                    this.heavyTankA.setGameObjectState(new HeavyTankAPatrol(heavyTankA));
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
        this.heavyTankA =null;
    }
}
