package io.tankgo.tankserver.gameobject.bullet.mbullet;

import io.tankgo.tankserver.config.StateStr;
import io.tankgo.tankserver.gameobject.GameObject;
import io.tankgo.tankserver.gameobject.GameObjectState;
import io.tankgo.tankserver.pojo.HitReward;

public class MidBulletIdel implements GameObjectState {

    private MidBullet midBullet;

    public MidBulletIdel(MidBullet midBullet) {
        this.midBullet = midBullet;
    }

    @Override
    public void boom() {
        this.midBullet.setGameObjectState(new MidBulletBoom(midBullet));
    }

    @Override
    public void worldUpdate(double worldTime) {
        double time= midBullet.getWorldTime();
        if(this.midBullet.getStateTime()>0){
            if(time-this.midBullet.getStateTime()>1.2){
                this.midBullet.setGameObjectState(new MidBulletBoom(midBullet));
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
        this.midBullet =null;
    }
}
