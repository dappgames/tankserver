package io.tankgo.tankserver.gameobject.player.enemytank;

import io.tankgo.tankserver.config.StateStr;
import io.tankgo.tankserver.gameobject.GameObject;
import io.tankgo.tankserver.gameobject.GameObjectState;
import io.tankgo.tankserver.gameobject.bullet.BaseBullet;
import io.tankgo.tankserver.gameobject.player.stankc.SmallTankCDead;
import io.tankgo.tankserver.pojo.GameUserInput;
import io.tankgo.tankserver.pojo.HitReward;
import io.tankgo.tankserver.utils.MyUtils;

import java.math.BigDecimal;

public class EnemyTankPatrol implements GameObjectState {

    private EnemyTank enemyTank;
    private Double radian;
    private Double bodyAngle;
    private Double towerAngle;
    private Double addx;
    private Double addy;
    public EnemyTankPatrol(EnemyTank enemyTank) {
        this.enemyTank = enemyTank;
        radian= MyUtils.randomBetween(0,2*Math.PI);
        bodyAngle=radian*180/Math.PI;
        addx=Math.cos(radian);
        addy=Math.sin(radian);
        towerAngle=null;
    }

    @Override
    public HitReward hit(GameObject object) {
        if(object instanceof BaseBullet){
            BaseBullet bullet= (BaseBullet) object;
            HitReward hitReward = bullet.getHitDamage().hit(this.enemyTank);
            if(this.enemyTank.getHp().compareTo(new BigDecimal("0"))<1){
                this.enemyTank.setGameObjectState(new EnemyTankDead(this.enemyTank));
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
        gameUserInput.setPlayId(this.enemyTank.getId());
        gameUserInput.setAddx(addx);
        gameUserInput.setAddy(addy);
        gameUserInput.setBodyAngle(bodyAngle);
        gameUserInput.setTowerAngle(null);
        this.enemyTank.doUserInput(gameUserInput);
        if(this.enemyTank.getStateTime()>0){
            double dt=worldTime-this.enemyTank.getStateTime();
            int mdt= (int)(Math.round(dt*10000));
            if(mdt%6000==0){
                gameUserInput.setButton("shoot");
                this.enemyTank.doUserInput(gameUserInput);
            }
            if(dt>(2.5+Math.random())){
                this.enemyTank.setGameObjectState(new EnemyTankIdle(enemyTank));
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
        this.enemyTank=null;
    }
}
