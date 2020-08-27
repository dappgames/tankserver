package io.tankgo.tankserver.service;

import io.tankgo.tankserver.pojo.GameUserInput;
import io.tankgo.tankserver.pojo.SimpleObject;
import org.apache.commons.lang3.StringUtils;
import org.dyn4j.dynamics.Body;
import org.dyn4j.dynamics.World;
import org.dyn4j.geometry.Geometry;
import org.dyn4j.geometry.MassType;
import org.dyn4j.geometry.Transform;
import org.dyn4j.geometry.Vector2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Component
public class SimpleRoom {
    private World world = new World();
    private Body body;
    private ScheduledExecutorService gameExecutor = Executors.newSingleThreadScheduledExecutor();

    @Autowired
    private SimpleMainService gameMainService;

    @PostConstruct
    public void init() {
        this.world.setGravity(World.ZERO_GRAVITY);
//        this.addWorldWall(400);
        //添加游戏物体
        body=new Body();
        body.addFixture(Geometry.createRectangle(0.3, 0.3),
                50,
                0.5,
                0.01);
        body.setMass(MassType.NORMAL);
        body.setAutoSleepingEnabled(false);
        body.setUserData(this);
        world.addBody(body);


        gameExecutor.scheduleWithFixedDelay(new Runnable() {
            @Override
            public void run() {
                try {
                    world.update(20);
                    SimpleObject simpleObject=new SimpleObject();
                    simpleObject.setX(body.getTransform().getTranslationX());
                    simpleObject.setY(body.getTransform().getTranslationY());
                    simpleObject.setR(body.getTransform().getRotation());
                    gameMainService.getServer().getBroadcastOperations().sendEvent("simpleMove",simpleObject);
                }catch (Exception e){

                }
            }
        },1,20, TimeUnit.MILLISECONDS);
    }


    public void onUserInput(GameUserInput input) {
        double addx = input.getAddx();
        double addy = input.getAddy();
        double bodyAngle = input.getBodyAngle();
        Transform transform=body.getTransform();
        double radian = bodyAngle * Math.PI / 180;
        transform.setRotation(radian);
//        transform.setTranslationX(transform.getTranslationX()+addx*10);
//        transform.setTranslationY(transform.getTranslationY()+addy*10);
        body.setTransform(transform);
        body.applyImpulse(new Vector2(addx*200,addy*200));
        body.setAngularVelocity(0);


    }



    private void addWorldWall(double wallsize) {

        double halfwall = wallsize / 2;

        Body wall1 = new Body();
        wall1.addFixture(Geometry.createRectangle(1, wallsize));
        wall1.setMass(MassType.INFINITE);
        wall1.translate(halfwall, 0);
        wall1.setUserData(new io.tankgo.tankserver.gameobject.wall.NormalWall());
        this.world.addBody(wall1);

        Body wall2 = new Body();
        wall2.addFixture(Geometry.createRectangle(1, wallsize));
        wall2.setMass(MassType.INFINITE);
        wall2.translate(-halfwall, 0);
        wall2.setUserData(new io.tankgo.tankserver.gameobject.wall.NormalWall());
        this.world.addBody(wall2);


        Body wall3 = new Body();
        wall3.addFixture(Geometry.createRectangle(wallsize, 1));
        wall3.setMass(MassType.INFINITE);
        wall3.translate(0, halfwall);
        wall3.setUserData(new io.tankgo.tankserver.gameobject.wall.NormalWall());
        this.world.addBody(wall3);

        Body wall4 = new Body();
        wall4.addFixture(Geometry.createRectangle(wallsize, 1));
        wall4.setMass(MassType.INFINITE);
        wall4.translate(0, -halfwall);
        wall4.setUserData(new io.tankgo.tankserver.gameobject.wall.NormalWall());
        this.world.addBody(wall4);
    }
}
