package io.tankgo.tankserver.gameobject.player.stankc;

import io.tankgo.tankserver.config.StateStr;
import io.tankgo.tankserver.gameobject.GameObject;
import io.tankgo.tankserver.gameobject.GameObjectState;
import io.tankgo.tankserver.pojo.HitReward;

public class SmallTankCBorn implements GameObjectState {
    private SmallTankC smallTankC;

    public SmallTankCBorn(SmallTankC smallTankC) {
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
        if(this.smallTankC.getStateTime()>0){
            if(worldTime-this.smallTankC.getStateTime()>3){
                this.smallTankC.setGameObjectState(new SmallTankCIdle(smallTankC));
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
        this.smallTankC =null;
    }
}
