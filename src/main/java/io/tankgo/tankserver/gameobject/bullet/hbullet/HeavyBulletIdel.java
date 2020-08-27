package io.tankgo.tankserver.gameobject.bullet.hbullet;

import io.tankgo.tankserver.config.StateStr;
import io.tankgo.tankserver.gameobject.GameObject;
import io.tankgo.tankserver.gameobject.GameObjectState;
import io.tankgo.tankserver.pojo.HitReward;

public class HeavyBulletIdel implements GameObjectState {

    private HeavyBullet heavyBullet;

    public HeavyBulletIdel(HeavyBullet heavyBullet) {
        this.heavyBullet = heavyBullet;
    }

    @Override
    public void boom() {
        this.heavyBullet.setGameObjectState(new HeavyBulletBoom(heavyBullet));
    }

    @Override
    public void worldUpdate(double worldTime) {
        double time= heavyBullet.getWorldTime();
        if(this.heavyBullet.getStateTime()>0){
            if(time-this.heavyBullet.getStateTime()>1.2){
                this.heavyBullet.setGameObjectState(new HeavyBulletBoom(heavyBullet));
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
        this.heavyBullet =null;
    }
}
