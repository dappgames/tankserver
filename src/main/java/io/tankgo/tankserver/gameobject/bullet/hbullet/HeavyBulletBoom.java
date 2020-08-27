package io.tankgo.tankserver.gameobject.bullet.hbullet;

import io.tankgo.tankserver.config.StateStr;
import io.tankgo.tankserver.gameobject.GameObject;
import io.tankgo.tankserver.gameobject.GameObjectState;
import io.tankgo.tankserver.pojo.HitReward;

public class HeavyBulletBoom implements GameObjectState {

    private HeavyBullet heavyBullet;

    public HeavyBulletBoom(HeavyBullet heavyBullet) {
        this.heavyBullet = heavyBullet;
    }


    @Override
    public void boom() {

    }

    @Override
    public void worldUpdate(double worldTime) {

    }

    @Override
    public boolean checkRemove() {
        double time= heavyBullet.getWorldTime();
        if(time-this.heavyBullet.getStateTime()>0.2){
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
        this.heavyBullet =null;
    }
}
