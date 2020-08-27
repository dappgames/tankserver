package io.tankgo.tankserver.gameobject.player.htankc;

import io.tankgo.tankserver.config.StateStr;
import io.tankgo.tankserver.gameobject.GameObject;
import io.tankgo.tankserver.gameobject.GameObjectState;
import io.tankgo.tankserver.gameobject.bullet.BaseBullet;
import io.tankgo.tankserver.gameobject.player.htanka.HeavyTankAIdle;
import io.tankgo.tankserver.gameobject.player.htanka.HeavyTankAPatrol;
import io.tankgo.tankserver.gameobject.player.stankc.SmallTankCDead;
import io.tankgo.tankserver.pojo.HitReward;

import java.math.BigDecimal;

public class HeavyTankCIdle implements GameObjectState {

    private HeavyTankC heavyTankC;

    public HeavyTankCIdle(HeavyTankC heavyTankC) {
        this.heavyTankC = heavyTankC;
    }



    @Override
    public HitReward hit(GameObject object) {
        if(object instanceof BaseBullet){
            BaseBullet bullet= (BaseBullet) object;
            HitReward hitReward = bullet.getHitDamage().hit(this.heavyTankC);
            if(this.heavyTankC.getHp().compareTo(new BigDecimal("0"))<1){
                this.heavyTankC.setGameObjectState(new HeavyTankCDead(this.heavyTankC));
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
        if(this.heavyTankC.getRobot()){
            if(this.heavyTankC.getStateTime()>0){
                if(worldTime-this.heavyTankC.getStateTime()>2){
                    this.heavyTankC.setGameObjectState(new HeavyTankCPatrol(heavyTankC));
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
        this.heavyTankC =null;
    }
}
