package io.tankgo.tankserver.gameobject.player.mtankc;

import io.tankgo.tankserver.config.StateStr;
import io.tankgo.tankserver.gameobject.GameObject;
import io.tankgo.tankserver.gameobject.GameObjectState;
import io.tankgo.tankserver.pojo.HitReward;

public class MidTankCBorn implements GameObjectState {
    private MidTankC midTankC;

    public MidTankCBorn(MidTankC midTankC) {
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
        if(this.midTankC.getStateTime()>0){
            if(worldTime-this.midTankC.getStateTime()>3){
                this.midTankC.setGameObjectState(new MidTankCIdle(midTankC));
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
        this.midTankC =null;
    }
}
