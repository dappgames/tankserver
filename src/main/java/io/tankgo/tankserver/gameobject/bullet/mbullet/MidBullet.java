package io.tankgo.tankserver.gameobject.bullet.mbullet;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.tankgo.tankserver.gameobject.GameObject;
import io.tankgo.tankserver.gameobject.GameObjectState;
import io.tankgo.tankserver.gameobject.bullet.BaseBullet;
import io.tankgo.tankserver.pojo.BulletParam;
import io.tankgo.tankserver.pojo.FireVec;
import io.tankgo.tankserver.pojo.HitDamage;
import io.tankgo.tankserver.pojo.HitReward;
import lombok.extern.slf4j.Slf4j;
import org.dyn4j.dynamics.Body;
import org.dyn4j.dynamics.World;
import org.dyn4j.geometry.Geometry;
import org.dyn4j.geometry.MassType;
import org.dyn4j.geometry.Transform;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Slf4j
public class MidBullet extends BaseBullet {

    @Override
    public void init(BulletParam param, World world, double worldTime) {
        this.world = world;
        this.setWorldTime(worldTime);
        this.setGameObjectState(new MidBulletIdel(this));
        bullet = new Body();
        bullet.addFixture(Geometry.createRectangle(0.12,0.24),
                2000,
                0.1,
                0.9);
//        bullet.addFixture(Geometry.createCircle(0.01),
//                4000,
//                0,
//                1);
        bullet.setMass(MassType.NORMAL);
        bullet.setAutoSleepingEnabled(false);
        bullet.setUserData(this);
//        bullet.setAngularDamping(Integer.MAX_VALUE);
        world.addBody(bullet);
        Transform transform = new Transform();
        FireVec fireVec=param.getFireVec();
        double initX = fireVec.getX();
        double initY = fireVec.getY();
        double vx = Math.cos(fireVec.getR());
        double vy = Math.sin(fireVec.getR());
        transform.setRotation(fireVec.getR());
        transform.setTranslation(initX + fireVec.getLenRatio() * vx, initY + fireVec.getLenRatio() * vy);
        bullet.setTransform(transform);
        bullet.setLinearVelocity(vx * fireVec.getV(), vy * fireVec.getV());
        this.setId(bullet.getId().toString());

        this.setPlayId(param.getPlayId());
        this.setGrp(param.getGrp());
        this.setType("b");
        this.setSkin("md");
        this.hitDamage=new HitDamage();
        this.hitDamage.create(param);
    }

    @Override
    public void destroy() {
        this.world.removeBody(bullet);
        this.getGameObjectState().destroy();
        this.bullet.setUserData(null);
        this.world=null;
        this.bullet=null;
    }

    @Override
    public void boom() {
        this.getGameObjectState().boom();
    }

    @Override
    public void worldUpdate(double worldTime) {
        this.setWorldTime(worldTime);
        this.getGameObjectState().worldUpdate(worldTime);
    }

    @Override
    public boolean checkRemove() {
        return this.getGameObjectState().checkRemove();
    }

    @Override
    public HitReward hit(GameObject object) {
        return gameObjectState.hit(object);
    }
    @Override
    public String getState() {
        GameObjectState state=this.getGameObjectState();
        if (state==null){
            log.info("null");
            return "null";
        }
        return state.getState();
    }
}
