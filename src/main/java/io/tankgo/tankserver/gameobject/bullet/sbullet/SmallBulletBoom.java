package io.tankgo.tankserver.gameobject.bullet.sbullet;

import io.tankgo.tankserver.config.StateStr;
import io.tankgo.tankserver.gameobject.GameObject;
import io.tankgo.tankserver.gameobject.GameObjectState;
import io.tankgo.tankserver.pojo.HitReward;

public class SmallBulletBoom implements GameObjectState {

    private SmallBullet smallBullet;

    public SmallBulletBoom(SmallBullet smallBullet) {
        this.smallBullet = smallBullet;
    }


    @Override
    public void boom() {

    }

    @Override
    public void worldUpdate(double worldTime) {

    }

    @Override
    public boolean checkRemove() {
        double time= smallBullet.getWorldTime();
        if(time-this.smallBullet.getStateTime()>0.2){
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
        this.smallBullet =null;
    }
}
