package io.tankgo.tankserver.gameobject.player.mtankb;

import io.tankgo.tankserver.config.StateStr;
import io.tankgo.tankserver.gameobject.GameObject;
import io.tankgo.tankserver.gameobject.GameObjectState;
import io.tankgo.tankserver.gameobject.bullet.BaseBullet;
import io.tankgo.tankserver.gameobject.player.htanka.HeavyTankAIdle;
import io.tankgo.tankserver.gameobject.player.stankc.SmallTankCDead;
import io.tankgo.tankserver.pojo.HitReward;

import java.math.BigDecimal;

public class MidTankBIdle implements GameObjectState {

    private MidTankB midTankB;

    public MidTankBIdle(MidTankB midTankB) {
        this.midTankB = midTankB;
    }



    @Override
    public HitReward hit(GameObject object) {
        if(object instanceof BaseBullet){
            BaseBullet bullet= (BaseBullet) object;
            HitReward hitReward = bullet.getHitDamage().hit(this.midTankB);
            if(this.midTankB.getHp().compareTo(new BigDecimal("0"))<1){
                this.midTankB.setGameObjectState(new MidTankBDead(this.midTankB));
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
        if(this.midTankB.getRobot()){
            if(this.midTankB.getStateTime()>0){
                if(worldTime-this.midTankB.getStateTime()>2){
                    this.midTankB.setGameObjectState(new MidTankBPatrol(midTankB));
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
        this.midTankB =null;
    }
}
