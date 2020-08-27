package io.tankgo.tankserver.gameobject.player.htanka;

import io.tankgo.tankserver.config.StateStr;
import io.tankgo.tankserver.gameobject.GameObject;
import io.tankgo.tankserver.gameobject.GameObjectState;
import io.tankgo.tankserver.pojo.HitReward;

public class HeavyTankADead implements GameObjectState{

    private HeavyTankA heavyTankA;

    public HeavyTankADead(HeavyTankA heavyTankA) {
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

    }

    @Override
    public boolean checkRemove() {
        double time= heavyTankA.getWorldTime();
        if(time-this.heavyTankA.getStateTime()>0.15){
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
        this.heavyTankA =null;
    }
}
