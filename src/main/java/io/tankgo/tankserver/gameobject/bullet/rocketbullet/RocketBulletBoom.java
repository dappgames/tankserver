package io.tankgo.tankserver.gameobject.bullet.rocketbullet;

import io.tankgo.tankserver.config.StateStr;
import io.tankgo.tankserver.gameobject.GameObject;
import io.tankgo.tankserver.gameobject.GameObjectState;
import io.tankgo.tankserver.pojo.HitReward;

public class RocketBulletBoom implements GameObjectState {

    private RocketBullet rocketBullet;

    public RocketBulletBoom(RocketBullet rocketBullet) {
        this.rocketBullet = rocketBullet;
    }


    @Override
    public void boom() {

    }

    @Override
    public void worldUpdate(double worldTime) {

    }

    @Override
    public boolean checkRemove() {
        double time= rocketBullet.getWorldTime();
        if(time-this.rocketBullet.getStateTime()>0.2){
            return true;
        }
        return false;
    }

    @Override
    public HitReward hit(GameObject object) {
        HitReward hitReward=new HitReward();
        hitReward.setSuccess(true);
        return hitReward;
    }

    @Override
    public String getState() {
        return StateStr.boom;
    }

    @Override
    public void destroy() {
        this.rocketBullet =null;
    }
}
