package io.tankgo.tankserver.gameobject.player.mtanka;

import io.tankgo.tankserver.config.StateStr;
import io.tankgo.tankserver.gameobject.GameObject;
import io.tankgo.tankserver.gameobject.GameObjectState;
import io.tankgo.tankserver.pojo.HitReward;

public class MidTankABorn implements GameObjectState {
    private MidTankA midTankA;

    public MidTankABorn(MidTankA midTankA) {
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
        if(this.midTankA.getStateTime()>0){
            if(worldTime-this.midTankA.getStateTime()>3){
                this.midTankA.setGameObjectState(new MidTankAIdle(midTankA));
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
        this.midTankA =null;
    }
}
