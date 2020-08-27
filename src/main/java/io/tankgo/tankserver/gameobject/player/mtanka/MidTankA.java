package io.tankgo.tankserver.gameobject.player.mtanka;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.tankgo.tankserver.gameobject.GameObject;
import io.tankgo.tankserver.gameobject.player.BasePlayer;
import io.tankgo.tankserver.gameobject.player.PlayerListener;
import io.tankgo.tankserver.pojo.*;
import io.tankgo.tankserver.utils.MyUtils;
import org.apache.commons.lang3.StringUtils;
import org.dyn4j.dynamics.Body;
import org.dyn4j.dynamics.World;
import org.dyn4j.dynamics.joint.MotorJoint;
import org.dyn4j.geometry.Geometry;
import org.dyn4j.geometry.MassType;
import org.dyn4j.geometry.Transform;

import java.util.Arrays;
import java.util.List;

//移动速度，加速速度，炮弹速度，装甲厚度，重量，生命值，装填速度

@JsonInclude(JsonInclude.Include.NON_NULL)
public class MidTankA extends BasePlayer {

    @JsonIgnore
    private Double addx = 0d;
    @JsonIgnore
    private Double addy = 0d;
    @JsonIgnore
    private Double bodyAngle = 0d;
    @JsonIgnore
    private Double towerAngle = null;

    @JsonIgnore
    private int oriV=2;
    @JsonIgnore
    private int maxV=4;
    @JsonIgnore
    private int nowV = 2;
    @JsonIgnore
    private double accTime=0;

    @Override
    public void init(GameUser gameUser, World world, double worldTime) {
        this.setWorldTime(worldTime);
        MidTankABorn midTankABorn = new MidTankABorn(this);
        setGameObjectState(midTankABorn);
        this.world = world;
        controller = new Body();
        controller.addFixture(Geometry.createRectangle(0.3, 0.3),
                2000,
                0.2,
                0.4);
        controller.setMass(MassType.NORMAL);
        controller.setAutoSleepingEnabled(false);
        controller.setLinearDamping(5);
        controller.setAngularDamping(5);
        world.addBody(controller);
        //
        this.player = new Body();
        player.addFixture(Geometry.createRectangle(0.4, 0.4),
                200,
                0.2,
                0.8);
        player.setMass(MassType.NORMAL);
        player.setAutoSleepingEnabled(false);
        player.setUserData(this);
        world.addBody(player);
        //
        control = new MotorJoint(player, controller);
        control.setCollisionAllowed(false);
        control.setCorrectionFactor(0.7);
        control.setMaximumForce(Integer.MAX_VALUE);
        control.setMaximumTorque(Integer.MAX_VALUE);
        world.addJoint(control);
        int randX = MyUtils.randomDir() * MyUtils.randomBetween(5, 14);
        int randY = MyUtils.randomDir() * MyUtils.randomBetween(5, 14);
        controller.getTransform().setTranslation(randX, randY);
        player.getTransform().setTranslation(randX, randY);

        //
        this.setId(player.getId().toString());
        this.setUserName(gameUser.getUserName());
        this.setType("t");
        this.setSkin("mda");
        this.setCoin(gameUser.getCoin());
        this.setHp(gameUser.getAmount());
        this.setHpMax(gameUser.getAmount());
        this.setX(0d);
        this.setY(0d);
        this.setRltMax(0.5);
        this.setRltNow(0);
        this.setBltMax(7);
        this.setBltNow(7);
        this.setRad(player.getTransform().getRotation());
        this.setTrad(player.getTransform().getRotation());
        this.setColor(MyUtils.getRandColor());
    }

    @Override
    public void destroy() {
        this.world.removeJoint(control);
        this.world.removeBody(player);
        this.world.removeBody(controller);
        this.getGameObjectState().destroy();
        setPlayerListener(null);
        this.player.setUserData(null);
        this.world=null;
        this.player=null;
        this.controller=null;
    }

    @Override
    public void doUserInput(GameUserInput input) {

        if(input==null){
            return;
        }
        if(StringUtils.isEmpty(input.getPlayId())){
            return;
        }
        if(StringUtils.isEmpty(input.getButton())){
            this.addx = input.getAddx();
            this.addy = input.getAddy();
            this.bodyAngle = input.getBodyAngle();
            this.towerAngle = input.getTowerAngle();
        }else{
            if("shoot".equals(input.getButton())){
                if(this.getBltNow()>0){
                    this.setBltNow(this.getBltNow()-1);
                    if(this.getBltNow()<0){
                        this.setBltNow(0);
                    }
                    PlayerListener playerListener=getPlayerListener();
                    if(playerListener!=null){
                        List<FireVec> fireVecs=getFireVecs();
                        for(FireVec fireVec:fireVecs){
                            BulletParam bulletParam=BulletParam.builder()
                                    .playId(this.getId())
                                    .skin("md")
                                    .robot(this.getRobot())
                                    .fireVec(fireVec)
                                    .grp(this.grp)
                                    .build();
                            playerListener.onShoot(bulletParam);
                        }
                    }
                }
            }else if("thunder".equals(input.getButton())){
                System.out.println("thunder");
                nowV=maxV;
            }
        }


    }

    @Override
    public List<FireVec> getFireVecs(){
        double x = player.getTransform().getTranslationX();
        double y = player.getTransform().getTranslationY();
        double r = this.getTrad();

        return Arrays.asList(new FireVec(x, y, r, 0.45, 6));
    }


    @Override
    public HitReward hit(GameObject object) {
        return this.getGameObjectState().hit(object);
    }

    @Override
    public void worldUpdate(double worldTime) {
        double dltTime=worldTime-this.getWorldTime();
        this.setWorldTime(worldTime);
        if (addx != 0 || addy != 0) {
            double addvx = addx*nowV;
            double addvy = addy*nowV;
            double nowx = controller.getTransform().getTranslationX();
            double nowy = controller.getTransform().getTranslationY();
            double radian = bodyAngle * Math.PI / 180;
            Transform transform = new Transform();
            transform.setTranslation(nowx, nowy);
            transform.setRotation(radian);
            controller.setTransform(transform);
            controller.setLinearVelocity(addvx, addvy);
            //controller.applyImpulse(new Vector2(addvx*200,addvy*200));
            controller.setAngularVelocity(0);
        }
        if(this.getBltNow()<1){
            this.setRltNow(this.getRltNow()+dltTime);
        }
        if(this.getRltNow()>=this.getRltMax()){
            this.setRltNow(0);
            this.setBltNow(this.getBltMax());
        }
        if(nowV==maxV){
            accTime+=dltTime;
            if(accTime>0.1){
                nowV=oriV;
                accTime=0;
            }
        }
        if (towerAngle != null) {
            this.setTrad((90 - towerAngle) * Math.PI / 180);
            this.followTarget = true;
        } else {
            this.followTarget = false;
        }
        this.getGameObjectState().worldUpdate(worldTime);
    }

    @Override
    public boolean checkRemove() {
        return this.getGameObjectState().checkRemove();
    }

    @Override
    public String getState() {
        return this.getGameObjectState().getState();
    }

    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }
}
