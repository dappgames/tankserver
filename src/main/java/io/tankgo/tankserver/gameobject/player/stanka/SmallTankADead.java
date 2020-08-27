package io.tankgo.tankserver.gameobject.player.stanka;

import io.tankgo.tankserver.config.StateStr;
import io.tankgo.tankserver.gameobject.GameObject;
import io.tankgo.tankserver.gameobject.GameObjectState;
import io.tankgo.tankserver.pojo.HitReward;

public class SmallTankADead implements GameObjectState{

    private SmallTankA smallTankA;

    public SmallTankADead(SmallTankA smallTankA) {
        this.smallTankA = smallTankA;
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
        double time= smallTankA.getWorldTime();
        if(time-this.smallTankA.getStateTime()>0.15){
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
        this.smallTankA =null;
    }
}
