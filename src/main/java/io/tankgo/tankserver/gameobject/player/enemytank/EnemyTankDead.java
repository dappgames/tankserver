package io.tankgo.tankserver.gameobject.player.enemytank;

import io.tankgo.tankserver.config.StateStr;
import io.tankgo.tankserver.gameobject.GameObject;
import io.tankgo.tankserver.gameobject.GameObjectState;
import io.tankgo.tankserver.pojo.HitReward;

public class EnemyTankDead implements GameObjectState {

    private EnemyTank enemyTank;

    public EnemyTankDead(EnemyTank enemyTank) {
        this.enemyTank = enemyTank;
    }


    @Override
    public HitReward hit(GameObject object) {
        return new HitReward();
    }

    @Override
    public void boom() {

    }

    @Override
    public void worldUpdate(double worldTime) {

    }

    @Override
    public boolean checkRemove() {
        double time= enemyTank.getWorldTime();
        if(time-this.enemyTank.getStateTime()>0.15){
            return true;
        }
        return false;
    }

    @Override
    public String getState() {
        return StateStr.dead;
    }

    @Override
    public void destroy() {
        this.enemyTank=null;
    }
}
