package io.tankgo.tankserver.gameobject.player.stanka;

import io.tankgo.tankserver.config.StateStr;
import io.tankgo.tankserver.gameobject.GameObject;
import io.tankgo.tankserver.gameobject.GameObjectState;
import io.tankgo.tankserver.gameobject.bullet.BaseBullet;
import io.tankgo.tankserver.gameobject.player.htanka.HeavyTankAIdle;
import io.tankgo.tankserver.gameobject.player.stankc.SmallTankCDead;
import io.tankgo.tankserver.pojo.HitReward;

import java.math.BigDecimal;

public class SmallTankAIdle implements GameObjectState {

    private SmallTankA smallTankA;

    public SmallTankAIdle(SmallTankA smallTankA) {
        this.smallTankA = smallTankA;
    }



    @Override
    public HitReward hit(GameObject object) {
        if(object instanceof BaseBullet){
            BaseBullet bullet= (BaseBullet) object;
            HitReward hitReward = bullet.getHitDamage().hit(this.smallTankA);
            if(this.smallTankA.getHp().compareTo(new BigDecimal("0"))<1){
                this.smallTankA.setGameObjectState(new SmallTankADead(this.smallTankA));
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
        if(this.smallTankA.getRobot()){
            if(this.smallTankA.getStateTime()>0){
                if(worldTime-this.smallTankA.getStateTime()>2){
                    this.smallTankA.setGameObjectState(new SmallTankAPatrol(smallTankA));
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
        this.smallTankA =null;
    }
}
