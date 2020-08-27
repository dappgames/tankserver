package io.tankgo.tankserver.gameobject.bullet.rocketbullet;

import io.tankgo.tankserver.config.StateStr;
import io.tankgo.tankserver.gameobject.GameObject;
import io.tankgo.tankserver.gameobject.GameObjectState;
import io.tankgo.tankserver.pojo.HitReward;

public class RocketBulletIdel implements GameObjectState {

    private RocketBullet rocketBullet;

    public RocketBulletIdel(RocketBullet rocketBullet) {
        this.rocketBullet = rocketBullet;
    }

    @Override
    public void boom() {
        this.rocketBullet.setGameObjectState(new RocketBulletBoom(rocketBullet));
    }

    @Override
    public void worldUpdate(double worldTime) {
        double time= rocketBullet.getWorldTime();
        if(this.rocketBullet.getStateTime()>0){
            if(time-this.rocketBullet.getStateTime()>10){
                this.rocketBullet.setGameObjectState(new RocketBulletBoom(rocketBullet));
            }
        }
    }

    @Override
    public boolean checkRemove() {
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
        return StateStr.idle;
    }

    @Override
    public void destroy() {
        this.rocketBullet =null;
    }
}
