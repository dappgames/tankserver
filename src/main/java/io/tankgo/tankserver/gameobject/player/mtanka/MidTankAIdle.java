package io.tankgo.tankserver.gameobject.player.mtanka;

import io.tankgo.tankserver.config.StateStr;
import io.tankgo.tankserver.gameobject.GameObject;
import io.tankgo.tankserver.gameobject.GameObjectState;
import io.tankgo.tankserver.gameobject.bullet.BaseBullet;
import io.tankgo.tankserver.gameobject.player.htanka.HeavyTankAIdle;
import io.tankgo.tankserver.gameobject.player.stankc.SmallTankCDead;
import io.tankgo.tankserver.pojo.HitReward;

import java.math.BigDecimal;

public class MidTankAIdle implements GameObjectState {

    private MidTankA midTankA;

    public MidTankAIdle(MidTankA midTankA) {
        this.midTankA = midTankA;
    }



    @Override
    public HitReward hit(GameObject object) {
        if(object instanceof BaseBullet){
            BaseBullet bullet= (BaseBullet) object;
            HitReward hitReward = bullet.getHitDamage().hit(this.midTankA);
            if(this.midTankA.getHp().compareTo(new BigDecimal("0"))<1){
                this.midTankA.setGameObjectState(new MidTankADead(this.midTankA));
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
        if(this.midTankA.getRobot()){
            if(this.midTankA.getStateTime()>0){
                if(worldTime-this.midTankA.getStateTime()>2){
                    this.midTankA.setGameObjectState(new MidTankAPatrol(midTankA));
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
        this.midTankA =null;
    }
}
