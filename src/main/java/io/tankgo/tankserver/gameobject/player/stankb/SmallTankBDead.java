package io.tankgo.tankserver.gameobject.player.stankb;

import io.tankgo.tankserver.config.StateStr;
import io.tankgo.tankserver.gameobject.GameObject;
import io.tankgo.tankserver.gameobject.GameObjectState;
import io.tankgo.tankserver.pojo.HitReward;

public class SmallTankBDead implements GameObjectState{

    private SmallTankB smallTankB;

    public SmallTankBDead(SmallTankB smallTankB) {
        this.smallTankB = smallTankB;
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
        double time= smallTankB.getWorldTime();
        if(time-this.smallTankB.getStateTime()>0.15){
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
        this.smallTankB =null;
    }
}
