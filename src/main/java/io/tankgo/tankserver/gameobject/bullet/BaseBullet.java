package io.tankgo.tankserver.gameobject.bullet;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.tankgo.tankserver.gameobject.GameObject;
import io.tankgo.tankserver.gameobject.GameObjectState;
import io.tankgo.tankserver.pojo.BulletParam;
import io.tankgo.tankserver.pojo.FireVec;
import io.tankgo.tankserver.pojo.HitDamage;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.dyn4j.dynamics.Body;
import org.dyn4j.dynamics.World;

@Data
@EqualsAndHashCode(callSuper = false)
public abstract class BaseBullet extends GameObject{

    @JsonIgnore
    protected World world;
    @JsonIgnore
    protected Body bullet;

    @JsonIgnore
    protected GameObjectState gameObjectState;
    @JsonIgnore
    protected double worldTime;
    @JsonIgnore
    protected double stateTime;

    protected String playId;

    @JsonIgnore
    protected HitDamage hitDamage;

    public abstract void init(BulletParam param, World world, double worldTime);

    public void asyncData() {
        double x = bullet.getTransform().getTranslationX();
        double y = bullet.getTransform().getTranslationY();
        double radian = bullet.getTransform().getRotation();
        this.setGx((int)Math.floor(x/4+4));
        this.setGy((int)Math.floor(y/4+4));
        this.setX(x * 100);
        this.setY(y * 100);
        this.setRad(radian);
    }

    public GameObjectState getGameObjectState() {
        return gameObjectState;
    }

    public void setGameObjectState(GameObjectState gameObjectState) {
        GameObjectState oldState=this.gameObjectState;
        this.gameObjectState = gameObjectState;
        this.stateTime=this.worldTime;
        if(oldState!=null){
            oldState.destroy();
        }
    }
}
