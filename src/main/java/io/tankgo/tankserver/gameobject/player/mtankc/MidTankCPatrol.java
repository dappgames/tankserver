package io.tankgo.tankserver.gameobject.player.mtankc;

import io.tankgo.tankserver.config.StateStr;
import io.tankgo.tankserver.gameobject.GameObject;
import io.tankgo.tankserver.gameobject.GameObjectState;
import io.tankgo.tankserver.gameobject.bullet.BaseBullet;
import io.tankgo.tankserver.gameobject.player.stankc.SmallTankCDead;
import io.tankgo.tankserver.pojo.GameUserInput;
import io.tankgo.tankserver.pojo.HitReward;
import io.tankgo.tankserver.utils.MyUtils;

import java.math.BigDecimal;

public class MidTankCPatrol implements GameObjectState {

    private MidTankC midTankC;
    private Double radian;
    private Double bodyAngle;
    private Double towerAngle;
    private Double addx;
    private Double addy;
    public MidTankCPatrol(MidTankC midTankC) {
        this.midTankC = midTankC;
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
            HitReward hitReward = bullet.getHitDamage().hit(this.midTankC);
            if(this.midTankC.getHp().compareTo(new BigDecimal("0"))<1){
                this.midTankC.setGameObjectState(new MidTankCDead(this.midTankC));
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
        gameUserInput.setPlayId(this.midTankC.getId());
        gameUserInput.setAddx(addx);
        gameUserInput.setAddy(addy);
        gameUserInput.setBodyAngle(bodyAngle);
        gameUserInput.setTowerAngle(null);
        this.midTankC.doUserInput(gameUserInput);
        if(this.midTankC.getStateTime()>0){
            double dt=worldTime-this.midTankC.getStateTime();
            int mdt= (int)(Math.round(dt*10000));
            if(mdt%6000==0){
                gameUserInput.setButton("shoot");
                this.midTankC.doUserInput(gameUserInput);
            }
            if(dt>(2.5+Math.random())){
                this.midTankC.setGameObjectState(new MidTankCIdle(midTankC));
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
        this.midTankC=null;
    }
}
