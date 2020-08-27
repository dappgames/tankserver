package io.tankgo.tankserver.gameobject.player.stankc;

import io.tankgo.tankserver.config.StateStr;
import io.tankgo.tankserver.gameobject.GameObject;
import io.tankgo.tankserver.gameobject.GameObjectState;
import io.tankgo.tankserver.gameobject.bullet.BaseBullet;
import io.tankgo.tankserver.pojo.GameUserInput;
import io.tankgo.tankserver.pojo.HitReward;
import io.tankgo.tankserver.utils.MyUtils;

import java.math.BigDecimal;

public class SmallTankCPatrol implements GameObjectState {

    private SmallTankC smallTankC;
    private Double radian;
    private Double bodyAngle;
    private Double towerAngle;
    private Double addx;
    private Double addy;
    public SmallTankCPatrol(SmallTankC smallTankC) {
        this.smallTankC = smallTankC;
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
            HitReward hitReward = bullet.getHitDamage().hit(this.smallTankC);
            if(this.smallTankC.getHp().compareTo(new BigDecimal("0"))<1){
                this.smallTankC.setGameObjectState(new SmallTankCDead(this.smallTankC));
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
        gameUserInput.setPlayId(this.smallTankC.getId());
        gameUserInput.setAddx(addx);
        gameUserInput.setAddy(addy);
        gameUserInput.setBodyAngle(bodyAngle);
        gameUserInput.setTowerAngle(null);
        this.smallTankC.doUserInput(gameUserInput);
        if(this.smallTankC.getStateTime()>0){
            double dt=worldTime-this.smallTankC.getStateTime();
            int mdt= (int)(Math.round(dt*10000));
            if(mdt%6000==0){
                gameUserInput.setButton("shoot");
                this.smallTankC.doUserInput(gameUserInput);
            }
            if(dt>(2.5+Math.random())){
                this.smallTankC.setGameObjectState(new SmallTankCIdle(smallTankC));
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
        this.smallTankC=null;
    }
}
