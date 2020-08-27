package io.tankgo.tankserver.gameobject.bullet.laserbullet;

import io.tankgo.tankserver.config.StateStr;
import io.tankgo.tankserver.gameobject.GameObject;
import io.tankgo.tankserver.gameobject.GameObjectState;
import io.tankgo.tankserver.pojo.HitReward;

public class LaserBulletIdel implements GameObjectState {

    private LaserBullet laserBullet;

    public LaserBulletIdel(LaserBullet laserBullet) {
        this.laserBullet = laserBullet;
    }

    @Override
    public void boom() {
        this.laserBullet.setGameObjectState(new LaserBulletBoom(laserBullet));
    }

    @Override
    public void worldUpdate(double worldTime) {
        double time= laserBullet.getWorldTime();
        if(this.laserBullet.getStateTime()>0){
            if(time-this.laserBullet.getStateTime()>5){
                this.laserBullet.setGameObjectState(new LaserBulletBoom(laserBullet));
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
        this.laserBullet =null;
    }
}
