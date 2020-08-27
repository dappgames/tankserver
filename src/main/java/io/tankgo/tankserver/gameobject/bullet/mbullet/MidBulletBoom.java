package io.tankgo.tankserver.gameobject.bullet.mbullet;

import io.tankgo.tankserver.config.StateStr;
import io.tankgo.tankserver.gameobject.GameObject;
import io.tankgo.tankserver.gameobject.GameObjectState;
import io.tankgo.tankserver.pojo.HitReward;

public class MidBulletBoom implements GameObjectState {

    private MidBullet midBullet;

    public MidBulletBoom(MidBullet midBullet) {
        this.midBullet = midBullet;
    }


    @Override
    public void boom() {

    }

    @Override
    public void worldUpdate(double worldTime) {

    }

    @Override
    public boolean checkRemove() {
        double time= midBullet.getWorldTime();
        if(time-this.midBullet.getStateTime()>0.2){
            return true;
        }
        return false;
    }

    @Override
    public HitReward hit(GameObject object) {
        HitReward hitReward=new HitReward();
        hitReward.setSuccess(false);
        return hitReward;
    }

    @Override
    public String getState() {
        return StateStr.boom;
    }

    @Override
    public void destroy() {
        this.midBullet =null;
    }
}
