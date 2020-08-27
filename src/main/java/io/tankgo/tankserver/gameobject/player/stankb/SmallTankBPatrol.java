package io.tankgo.tankserver.gameobject.player.stankb;

import io.tankgo.tankserver.config.StateStr;
import io.tankgo.tankserver.gameobject.GameObject;
import io.tankgo.tankserver.gameobject.GameObjectState;
import io.tankgo.tankserver.gameobject.bullet.BaseBullet;
import io.tankgo.tankserver.gameobject.player.enemytank.EnemyTank;
import io.tankgo.tankserver.gameobject.player.enemytank.EnemyTankDead;
import io.tankgo.tankserver.gameobject.player.enemytank.EnemyTankIdle;
import io.tankgo.tankserver.gameobject.player.stankc.SmallTankCDead;
import io.tankgo.tankserver.pojo.GameUserInput;
import io.tankgo.tankserver.pojo.HitReward;
import io.tankgo.tankserver.utils.MyUtils;

import java.math.BigDecimal;

public class SmallTankBPatrol implements GameObjectState {

    private SmallTankB smallTankB;
    private Double radian;
    private Double bodyAngle;
    private Double towerAngle;
    private Double addx;
    private Double addy;
    public SmallTankBPatrol(SmallTankB smallTankB) {
        this.smallTankB = smallTankB;
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
            HitReward hitReward = bullet.getHitDamage().hit(this.smallTankB);
            if(this.smallTankB.getHp().compareTo(new BigDecimal("0"))<1){
                this.smallTankB.setGameObjectState(new SmallTankBDead(this.smallTankB));
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
        gameUserInput.setPlayId(this.smallTankB.getId());
        gameUserInput.setAddx(addx);
        gameUserInput.setAddy(addy);
        gameUserInput.setBodyAngle(bodyAngle);
        gameUserInput.setTowerAngle(null);
        this.smallTankB.doUserInput(gameUserInput);
        if(this.smallTankB.getStateTime()>0){
            double dt=worldTime-this.smallTankB.getStateTime();
            int mdt= (int)(Math.round(dt*10000));
            if(mdt%6000==0){
                gameUserInput.setButton("shoot");
                this.smallTankB.doUserInput(gameUserInput);
            }
            if(dt>(2.5+Math.random())){
                this.smallTankB.setGameObjectState(new SmallTankBIdle(smallTankB));
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
        this.smallTankB=null;
    }
}
