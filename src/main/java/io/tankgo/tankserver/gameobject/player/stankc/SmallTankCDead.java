package io.tankgo.tankserver.gameobject.player.stankc;

import io.tankgo.tankserver.config.StateStr;
import io.tankgo.tankserver.gameobject.GameObject;
import io.tankgo.tankserver.gameobject.GameObjectState;
import io.tankgo.tankserver.pojo.HitReward;

public class SmallTankCDead implements GameObjectState{

    private SmallTankC smallTankC;

    public SmallTankCDead(SmallTankC smallTankC) {
        this.smallTankC = smallTankC;
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
        double time= smallTankC.getWorldTime();
        if(time-this.smallTankC.getStateTime()>0.15){
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
        this.smallTankC =null;
    }
}
