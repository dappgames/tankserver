package io.tankgo.tankserver.gameobject.player.htanka;

import io.tankgo.tankserver.config.StateStr;
import io.tankgo.tankserver.gameobject.GameObject;
import io.tankgo.tankserver.gameobject.GameObjectState;
import io.tankgo.tankserver.pojo.HitReward;

public class HeavyTankABorn implements GameObjectState {
    private HeavyTankA heavyTankA;

    public HeavyTankABorn(HeavyTankA heavyTankA) {
        this.heavyTankA = heavyTankA;
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
        if(this.heavyTankA.getStateTime()>0){
            if(worldTime-this.heavyTankA.getStateTime()>3){
                this.heavyTankA.setGameObjectState(new HeavyTankAIdle(heavyTankA));
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
        this.heavyTankA =null;
    }
}
