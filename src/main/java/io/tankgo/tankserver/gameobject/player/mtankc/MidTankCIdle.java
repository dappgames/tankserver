package io.tankgo.tankserver.gameobject.player.mtankc;

import io.tankgo.tankserver.config.StateStr;
import io.tankgo.tankserver.gameobject.GameObject;
import io.tankgo.tankserver.gameobject.GameObjectState;
import io.tankgo.tankserver.gameobject.bullet.BaseBullet;
import io.tankgo.tankserver.gameobject.player.htanka.HeavyTankAIdle;
import io.tankgo.tankserver.gameobject.player.stankc.SmallTankCDead;
import io.tankgo.tankserver.pojo.HitReward;

import java.math.BigDecimal;

public class MidTankCIdle implements GameObjectState {

    private MidTankC midTankC;

    public MidTankCIdle(MidTankC midTankC) {
        this.midTankC = midTankC;
    }



    @Override
    public HitReward hit(GameObject object) {
        if(object instanceof BaseBullet){
            BaseBullet bullet= (BaseBullet) object;
            HitReward hitReward = bullet.getHitDamage().hit(this.midTankC);
            if(this.midTankC.getHp().compareTo(new BigDecimal("0"))<1){
                this.midTankC.setGameObjectState(new MidTankCDead(this.midTankC));
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
        if(this.midTankC.getRobot()){
            if(this.midTankC.getStateTime()>0){
                if(worldTime-this.midTankC.getStateTime()>2){
                    this.midTankC.setGameObjectState(new MidTankCPatrol(midTankC));
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
        this.midTankC =null;
    }
}
