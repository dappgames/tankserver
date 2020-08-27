package io.tankgo.tankserver.gameobject.bullet.sbullet;

import io.tankgo.tankserver.config.StateStr;
import io.tankgo.tankserver.gameobject.GameObject;
import io.tankgo.tankserver.gameobject.GameObjectState;
import io.tankgo.tankserver.pojo.HitReward;

public class SmallBulletIdel implements GameObjectState {

    private SmallBullet smallBullet;

    public SmallBulletIdel(SmallBullet smallBullet) {
        this.smallBullet = smallBullet;
    }

    @Override
    public void boom() {
        this.smallBullet.setGameObjectState(new SmallBulletBoom(smallBullet));
    }

    @Override
    public void worldUpdate(double worldTime) {
        double time= smallBullet.getWorldTime();
        if(this.smallBullet.getStateTime()>0){
            if(time-this.smallBullet.getStateTime()>1.2){
                this.smallBullet.setGameObjectState(new SmallBulletBoom(smallBullet));
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
        hitReward.setSuccess(false);
        return hitReward;
    }

    @Override
    public String getState() {
        return StateStr.idle;
    }

    @Override
    public void destroy() {
        this.smallBullet =null;
    }
}
