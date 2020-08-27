package io.tankgo.tankserver.gameobject.player.mtanka;

import io.tankgo.tankserver.config.StateStr;
import io.tankgo.tankserver.gameobject.GameObject;
import io.tankgo.tankserver.gameobject.GameObjectState;
import io.tankgo.tankserver.pojo.HitReward;

public class MidTankADead implements GameObjectState{

    private MidTankA midTankA;

    public MidTankADead(MidTankA midTankA) {
        this.midTankA = midTankA;
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
        double time= midTankA.getWorldTime();
        if(time-this.midTankA.getStateTime()>0.15){
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
        this.midTankA =null;
    }
}
