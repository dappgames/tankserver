package io.tankgo.tankserver.gameobject.player.htankb;

import io.tankgo.tankserver.config.StateStr;
import io.tankgo.tankserver.gameobject.GameObject;
import io.tankgo.tankserver.gameobject.GameObjectState;
import io.tankgo.tankserver.pojo.HitReward;

public class HeavyTankBDead implements GameObjectState{

    private HeavyTankB heavyTankB;

    public HeavyTankBDead(HeavyTankB heavyTankB) {
        this.heavyTankB = heavyTankB;
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
        double time= heavyTankB.getWorldTime();
        if(time-this.heavyTankB.getStateTime()>0.15){
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
        this.heavyTankB =null;
    }
}
