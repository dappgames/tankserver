package io.tankgo.tankserver.gameobject.player.stankb;

import io.tankgo.tankserver.config.StateStr;
import io.tankgo.tankserver.gameobject.GameObject;
import io.tankgo.tankserver.gameobject.GameObjectState;
import io.tankgo.tankserver.pojo.HitReward;

public class SmallTankBBorn implements GameObjectState {
    private SmallTankB smallTankB;

    public SmallTankBBorn(SmallTankB smallTankB) {
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
        if(this.smallTankB.getStateTime()>0){
            if(worldTime-this.smallTankB.getStateTime()>3){
                this.smallTankB.setGameObjectState(new SmallTankBIdle(smallTankB));
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
        this.smallTankB =null;
    }
}
