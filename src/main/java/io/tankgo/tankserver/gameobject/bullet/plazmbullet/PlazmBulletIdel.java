package io.tankgo.tankserver.gameobject.bullet.plazmbullet;

import io.tankgo.tankserver.config.StateStr;
import io.tankgo.tankserver.gameobject.GameObject;
import io.tankgo.tankserver.gameobject.GameObjectState;
import io.tankgo.tankserver.pojo.HitReward;

public class PlazmBulletIdel implements GameObjectState {

    private PlazmBullet plazmBullet;

    public PlazmBulletIdel(PlazmBullet plazmBullet) {
        this.plazmBullet = plazmBullet;
    }

    @Override
    public void boom() {
        this.plazmBullet.setGameObjectState(new PlazmBulletBoom(plazmBullet));
    }

    @Override
    public void worldUpdate(double worldTime) {
        double time= plazmBullet.getWorldTime();
        if(this.plazmBullet.getStateTime()>0){
            if(time-this.plazmBullet.getStateTime()>1.2){
                this.plazmBullet.setGameObjectState(new PlazmBulletBoom(plazmBullet));
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
        this.plazmBullet =null;
    }
}
