package io.tankgo.tankserver.gameobject.bullet.plazmbullet;

import io.tankgo.tankserver.config.StateStr;
import io.tankgo.tankserver.gameobject.GameObject;
import io.tankgo.tankserver.gameobject.GameObjectState;
import io.tankgo.tankserver.pojo.HitReward;

public class PlazmBulletBoom implements GameObjectState {

    private PlazmBullet plazmBullet;

    public PlazmBulletBoom(PlazmBullet plazmBullet) {
        this.plazmBullet = plazmBullet;
    }


    @Override
    public void boom() {

    }

    @Override
    public void worldUpdate(double worldTime) {

    }

    @Override
    public boolean checkRemove() {
        double time= plazmBullet.getWorldTime();
        if(time-this.plazmBullet.getStateTime()>0.2){
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
        this.plazmBullet =null;
    }
}
