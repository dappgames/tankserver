package io.tankgo.tankserver.gameobject.player.mtankb;

import io.tankgo.tankserver.config.StateStr;
import io.tankgo.tankserver.gameobject.GameObject;
import io.tankgo.tankserver.gameobject.GameObjectState;
import io.tankgo.tankserver.pojo.HitReward;

public class MidTankBDead implements GameObjectState{

    private MidTankB midTankB;

    public MidTankBDead(MidTankB midTankB) {
        this.midTankB = midTankB;
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
        double time= midTankB.getWorldTime();
        if(time-this.midTankB.getStateTime()>0.15){
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
        this.midTankB =null;
    }
}
