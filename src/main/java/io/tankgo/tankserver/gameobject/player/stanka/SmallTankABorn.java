package io.tankgo.tankserver.gameobject.player.stanka;

import io.tankgo.tankserver.config.StateStr;
import io.tankgo.tankserver.gameobject.GameObject;
import io.tankgo.tankserver.gameobject.GameObjectState;
import io.tankgo.tankserver.pojo.HitReward;

public class SmallTankABorn implements GameObjectState {
    private SmallTankA smallTankA;

    public SmallTankABorn(SmallTankA smallTankA) {
        this.smallTankA = smallTankA;
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
        if(this.smallTankA.getStateTime()>0){
            if(worldTime-this.smallTankA.getStateTime()>3){
                this.smallTankA.setGameObjectState(new SmallTankAIdle(smallTankA));
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
        this.smallTankA =null;
    }
}
