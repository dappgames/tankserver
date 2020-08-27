package io.tankgo.tankserver.gameobject.player.htankb;

import io.tankgo.tankserver.config.StateStr;
import io.tankgo.tankserver.gameobject.GameObject;
import io.tankgo.tankserver.gameobject.GameObjectState;
import io.tankgo.tankserver.pojo.HitReward;

public class HeavyTankBBorn implements GameObjectState {
    private HeavyTankB heavyTankB;

    public HeavyTankBBorn(HeavyTankB heavyTankB) {
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
        if(this.heavyTankB.getStateTime()>0){
            if(worldTime-this.heavyTankB.getStateTime()>3){
                this.heavyTankB.setGameObjectState(new HeavyTankBIdle(heavyTankB));
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
        this.heavyTankB =null;
    }
}
