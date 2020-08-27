package io.tankgo.tankserver.gameobject.bullet.laserbullet;

import io.tankgo.tankserver.config.StateStr;
import io.tankgo.tankserver.gameobject.GameObject;
import io.tankgo.tankserver.gameobject.GameObjectState;
import io.tankgo.tankserver.pojo.HitReward;

public class LaserBulletBoom implements GameObjectState {

    private LaserBullet laserBullet;

    public LaserBulletBoom(LaserBullet laserBullet) {
        this.laserBullet = laserBullet;
    }


    @Override
    public void boom() {

    }

    @Override
    public void worldUpdate(double worldTime) {

    }

    @Override
    public boolean checkRemove() {
        double time= laserBullet.getWorldTime();
        if(time-this.laserBullet.getStateTime()>0.1){
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
        this.laserBullet =null;
    }
}
