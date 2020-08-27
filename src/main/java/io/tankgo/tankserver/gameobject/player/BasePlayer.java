package io.tankgo.tankserver.gameobject.player;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.tankgo.tankserver.gameobject.GameObject;
import io.tankgo.tankserver.gameobject.GameObjectState;
import io.tankgo.tankserver.pojo.FireVec;
import io.tankgo.tankserver.pojo.GameUser;
import io.tankgo.tankserver.pojo.GameUserInput;
import io.tankgo.tankserver.pojo.HitReward;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.dyn4j.dynamics.Body;
import org.dyn4j.dynamics.World;
import org.dyn4j.dynamics.joint.MotorJoint;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@EqualsAndHashCode(callSuper = false)
public abstract class BasePlayer extends GameObject{

    @JsonIgnore
    protected World world;
    @JsonIgnore
    protected MotorJoint control;
    @JsonIgnore
    protected Body controller;
    @JsonIgnore
    protected Body player;

    @JsonIgnore
    protected Boolean followTarget = false;

    @JsonIgnore
    protected PlayerListener playerListener;
    @JsonIgnore
    protected GameObjectState gameObjectState;

    @JsonIgnore
    protected double worldTime;
    @JsonIgnore
    protected double stateTime;
    @JsonIgnore
    protected Boolean robot=false;

    @JsonIgnore
    protected String key;

    protected String coin;

    @JsonIgnore
    protected List<String> socketIds = new ArrayList<>();

    protected Map<String,HitReward> rewards=new HashMap<>();
    //炮塔角度
    protected double trad;
    protected String color;
    protected BigDecimal hp;
    protected BigDecimal hpMax;
    //装弹时间
    protected double rltMax;
    protected double rltNow;
    //弹夹内子弹
    protected int bltMax;
    protected int bltNow;



    public abstract void init(GameUser gameUser, World world, double worldTime);

    public abstract void doUserInput(GameUserInput userInput);

    @JsonIgnore
    public abstract List<FireVec> getFireVecs();




    public void setGameObjectState(GameObjectState gameObjectState) {
        GameObjectState oldState=this.gameObjectState;
        this.gameObjectState = gameObjectState;
        this.stateTime=this.worldTime;
        if(oldState!=null){
            oldState.destroy();
        }
    }

    public void asyncData() {
        if(player!=null){
            double x = player.getTransform().getTranslationX();
            double y = player.getTransform().getTranslationY();
            double radian = player.getTransform().getRotation();
            this.setGx((int)Math.floor(x/4+4));
            this.setGy((int)Math.floor(y/4+4));
            this.setX(x * 100);
            this.setY(y * 100);
            this.setRad(radian);
            if (!this.getFollowTarget()) {
                this.setTrad(radian);
            }
        }
    }

    @Override
    public void boom() {

    }

    public void setRobot(Boolean robot) {
        this.robot=robot;
        this.setGrp("e");
    }

    public PlayerListener getPlayerListener() {
        return playerListener;
    }

    public void setPlayerListener(PlayerListener playerListener) {
        this.playerListener = playerListener;
    }

}
