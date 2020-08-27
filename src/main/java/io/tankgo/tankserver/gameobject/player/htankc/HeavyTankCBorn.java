package io.tankgo.tankserver.gameobject.player.htankc;

import io.tankgo.tankserver.config.StateStr;
import io.tankgo.tankserver.gameobject.GameObject;
import io.tankgo.tankserver.gameobject.GameObjectState;
import io.tankgo.tankserver.pojo.HitReward;

public class HeavyTankCBorn implements GameObjectState {
    private HeavyTankC heavyTankC;

    public HeavyTankCBorn(HeavyTankC heavyTankC) {
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
        if(this.heavyTankC.getStateTime()>0){
            if(worldTime-this.heavyTankC.getStateTime()>3){
                this.heavyTankC.setGameObjectState(new HeavyTankCIdle(heavyTankC));
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
        this.heavyTankC =null;
    }
}
