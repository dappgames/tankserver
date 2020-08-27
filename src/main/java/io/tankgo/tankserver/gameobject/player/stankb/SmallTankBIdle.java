package io.tankgo.tankserver.gameobject.player.stankb;

import io.tankgo.tankserver.config.StateStr;
import io.tankgo.tankserver.gameobject.GameObject;
import io.tankgo.tankserver.gameobject.GameObjectState;
import io.tankgo.tankserver.gameobject.bullet.BaseBullet;
import io.tankgo.tankserver.gameobject.player.htanka.HeavyTankAIdle;
import io.tankgo.tankserver.gameobject.player.stankc.SmallTankCDead;
import io.tankgo.tankserver.gameobject.player.stankc.SmallTankCIdle;
import io.tankgo.tankserver.pojo.HitReward;

import java.math.BigDecimal;

public class SmallTankBIdle implements GameObjectState {

    private SmallTankB smallTankB;

    public SmallTankBIdle(SmallTankB smallTankB) {
        this.smallTankB = smallTankB;
    }



    @Override
    public HitReward hit(GameObject object) {
        if(object instanceof BaseBullet){
            BaseBullet bullet= (BaseBullet) object;
            HitReward hitReward = bullet.getHitDamage().hit(this.smallTankB);
            if(this.smallTankB.getHp().compareTo(new BigDecimal("0"))<1){
                this.smallTankB.setGameObjectState(new SmallTankBDead(this.smallTankB));
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
        if(this.smallTankB.getRobot()){
            if(this.smallTankB.getStateTime()>0){
                if(worldTime-this.smallTankB.getStateTime()>2){
                    this.smallTankB.setGameObjectState(new SmallTankBPatrol(smallTankB));
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
        this.smallTankB =null;
    }
}
