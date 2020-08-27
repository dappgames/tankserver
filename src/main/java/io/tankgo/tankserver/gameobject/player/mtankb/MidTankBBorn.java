package io.tankgo.tankserver.gameobject.player.mtankb;

import io.tankgo.tankserver.config.StateStr;
import io.tankgo.tankserver.gameobject.GameObject;
import io.tankgo.tankserver.gameobject.GameObjectState;
import io.tankgo.tankserver.pojo.HitReward;

public class MidTankBBorn implements GameObjectState {
    private MidTankB midTankB;

    public MidTankBBorn(MidTankB midTankB) {
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
        if(this.midTankB.getStateTime()>0){
            if(worldTime-this.midTankB.getStateTime()>3){
                this.midTankB.setGameObjectState(new MidTankBIdle(midTankB));
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
        this.midTankB =null;
    }
}
