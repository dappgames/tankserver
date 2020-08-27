package io.tankgo.tankserver.gameobject.wall;


import io.tankgo.tankserver.gameobject.GameObject;
import io.tankgo.tankserver.pojo.HitReward;

public class BaseWall extends GameObject {
    @Override
    public void boom() {

    }

    @Override
    public void worldUpdate(double worldTime) {

    }

    @Override
    public boolean checkRemove() {
        return false;
    }

    @Override
    public HitReward hit(GameObject object) {
        return new HitReward();
    }

    @Override
    public void destroy() {

    }
}
