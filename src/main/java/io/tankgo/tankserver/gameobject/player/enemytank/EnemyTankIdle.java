package io.tankgo.tankserver.gameobject.player.enemytank;

import io.tankgo.tankserver.config.StateStr;
import io.tankgo.tankserver.gameobject.GameObject;
import io.tankgo.tankserver.gameobject.GameObjectState;
import io.tankgo.tankserver.gameobject.bullet.BaseBullet;
import io.tankgo.tankserver.gameobject.player.stankc.SmallTankCDead;
import io.tankgo.tankserver.pojo.HitReward;

import java.math.BigDecimal;

public class EnemyTankIdle implements GameObjectState {

    private EnemyTank enemyTank;

    public EnemyTankIdle(EnemyTank enemyTank) {
        this.enemyTank = enemyTank;
    }


    @Override
    public HitReward hit(GameObject object) {
        if(object instanceof BaseBullet){
            BaseBullet bullet= (BaseBullet) object;
            HitReward hitReward = bullet.getHitDamage().hit(this.enemyTank);
            if(this.enemyTank.getHp().compareTo(new BigDecimal("0"))<1){
                this.enemyTank.setGameObjectState(new EnemyTankDead(this.enemyTank));
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
        if(this.enemyTank.getStateTime()>0){
            if(worldTime-this.enemyTank.getStateTime()>2){
                this.enemyTank.setGameObjectState(new EnemyTankPatrol(enemyTank));
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
        this.enemyTank=null;
    }
}
