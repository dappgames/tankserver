package io.tankgo.tankserver.gameobject.player.enemytank;


import io.tankgo.tankserver.config.StateStr;
import io.tankgo.tankserver.gameobject.GameObject;
import io.tankgo.tankserver.gameobject.GameObjectState;
import io.tankgo.tankserver.pojo.HitReward;

public class EnemyTankBorn implements GameObjectState {
    private EnemyTank enemyTank;

    public EnemyTankBorn(EnemyTank enemyTank) {
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
        if(this.enemyTank.getStateTime()>0){
            if(worldTime-this.enemyTank.getStateTime()>3){
                this.enemyTank.setGameObjectState(new EnemyTankIdle(enemyTank));
            }
        }
    }

    @Override
    public boolean checkRemove() {
        return false;
    }

    @Override
    public String getState() {
        return StateStr.born;
    }

    @Override
    public void destroy() {
        this.enemyTank=null;
    }
}
