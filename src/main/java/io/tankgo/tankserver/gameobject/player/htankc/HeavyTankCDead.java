package io.tankgo.tankserver.gameobject.player.htankc;

import io.tankgo.tankserver.config.StateStr;
import io.tankgo.tankserver.gameobject.GameObject;
import io.tankgo.tankserver.gameobject.GameObjectState;
import io.tankgo.tankserver.pojo.HitReward;

public class HeavyTankCDead implements GameObjectState{

    private HeavyTankC heavyTankC;

    public HeavyTankCDead(HeavyTankC heavyTankC) {
        this.heavyTankC = heavyTankC;
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
        double time= heavyTankC.getWorldTime();
        if(time-this.heavyTankC.getStateTime()>0.15){
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
        this.heavyTankC =null;
    }
}
