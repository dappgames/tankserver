package io.tankgo.tankserver.gameobject.player.stanka;

import io.tankgo.tankserver.config.StateStr;
import io.tankgo.tankserver.gameobject.GameObject;
import io.tankgo.tankserver.gameobject.GameObjectState;
import io.tankgo.tankserver.gameobject.bullet.BaseBullet;
import io.tankgo.tankserver.gameobject.player.stankc.SmallTankCDead;
import io.tankgo.tankserver.pojo.GameUserInput;
import io.tankgo.tankserver.pojo.HitReward;
import io.tankgo.tankserver.utils.MyUtils;

import java.math.BigDecimal;

public class SmallTankAPatrol implements GameObjectState {

    private SmallTankA smallTankA;
    private Double radian;
    private Double bodyAngle;
    private Double towerAngle;
    private Double addx;
    private Double addy;
    public SmallTankAPatrol(SmallTankA smallTankA) {
        this.smallTankA = smallTankA;
        radian= MyUtils.randomBetween(0,2*Math.PI);
        bodyAngle=radian*180/Math.PI;
        addx=Math.cos(radian)*0.3;
        addy=Math.sin(radian)*0.3;
        towerAngle=null;
    }

    @Override
    public HitReward hit(GameObject object) {
        if(object instanceof BaseBullet){
            BaseBullet bullet= (BaseBullet) object;
            HitReward hitReward = bullet.getHitDamage().hit(this.smallTankA);
            if(this.smallTankA.getHp().compareTo(new BigDecimal("0"))<1){
                this.smallTankA.setGameObjectState(new SmallTankADead(this.smallTankA));
            }
            return hitReward;
        }else{
            return new HitReward();
        }
    }

    @Override
    public void boom() {

    }

    @Override
    public void worldUpdate(double worldTime) {

        GameUserInput gameUserInput=new GameUserInput();
        gameUserInput.setPlayId(this.smallTankA.getId());
        gameUserInput.setAddx(addx);
        gameUserInput.setAddy(addy);
        gameUserInput.setBodyAngle(bodyAngle);
        gameUserInput.setTowerAngle(null);
        this.smallTankA.doUserInput(gameUserInput);
        if(this.smallTankA.getStateTime()>0){
            double dt=worldTime-this.smallTankA.getStateTime();
            int mdt= (int)(Math.round(dt*10000));
            if(mdt%6000==0){
                gameUserInput.setButton("shoot");
                this.smallTankA.doUserInput(gameUserInput);
            }
            if(dt>(2.5+Math.random())){
                this.smallTankA.setGameObjectState(new SmallTankAIdle(smallTankA));
            }
        }
    }

    @Override
    public boolean checkRemove() {
        return false;
    }

    @Override
    public String getState() {
        return StateStr.patrol;
    }

    @Override
    public void destroy() {
        this.smallTankA=null;
    }
}
