package io.tankgo.tankserver.gameobject.player.stankc;

import io.tankgo.tankserver.config.StateStr;
import io.tankgo.tankserver.gameobject.GameObject;
import io.tankgo.tankserver.gameobject.GameObjectState;
import io.tankgo.tankserver.gameobject.bullet.BaseBullet;
import io.tankgo.tankserver.gameobject.player.htanka.HeavyTankAIdle;
import io.tankgo.tankserver.pojo.HitReward;

import java.math.BigDecimal;

public class SmallTankCIdle implements GameObjectState {

    private SmallTankC smallTankC;

    public SmallTankCIdle(SmallTankC smallTankC) {
        this.smallTankC = smallTankC;
    }



    @Override
    public HitReward hit(GameObject object) {
        if(object instanceof BaseBullet){
            BaseBullet bullet= (BaseBullet) object;
            HitReward hitReward = bullet.getHitDamage().hit(this.smallTankC);
            if(this.smallTankC.getHp().compareTo(new BigDecimal("0"))<1){
                this.smallTankC.setGameObjectState(new SmallTankCDead(this.smallTankC));
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
        if(this.smallTankC.getRobot()){
            if(this.smallTankC.getStateTime()>0){
                if(worldTime-this.smallTankC.getStateTime()>2){
                    this.smallTankC.setGameObjectState(new SmallTankCPatrol(smallTankC));
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
        this.smallTankC =null;
    }
}
