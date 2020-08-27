package io.tankgo.tankserver.gameobject.player.mtankc;

import io.tankgo.tankserver.config.StateStr;
import io.tankgo.tankserver.gameobject.GameObject;
import io.tankgo.tankserver.gameobject.GameObjectState;
import io.tankgo.tankserver.pojo.HitReward;

public class MidTankCDead implements GameObjectState{

    private MidTankC midTankC;

    public MidTankCDead(MidTankC midTankC) {
        this.midTankC = midTankC;
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
        double time= midTankC.getWorldTime();
        if(time-this.midTankC.getStateTime()>0.15){
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
        this.midTankC =null;
    }
}
