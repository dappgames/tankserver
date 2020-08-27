package io.tankgo.tankserver.gameobject.player;

import io.tankgo.tankserver.gameobject.player.enemytank.EnemyTank;
import io.tankgo.tankserver.gameobject.player.htanka.HeavyTankA;
import io.tankgo.tankserver.gameobject.player.htankb.HeavyTankB;
import io.tankgo.tankserver.gameobject.player.htankc.HeavyTankC;
import io.tankgo.tankserver.gameobject.player.mtanka.MidTankA;
import io.tankgo.tankserver.gameobject.player.mtankb.MidTankB;
import io.tankgo.tankserver.gameobject.player.mtankc.MidTankC;
import io.tankgo.tankserver.gameobject.player.stanka.SmallTankA;
import io.tankgo.tankserver.gameobject.player.stankb.SmallTankB;
import io.tankgo.tankserver.gameobject.player.stankc.SmallTankC;
import io.tankgo.tankserver.utils.MyUtils;
import org.apache.commons.lang3.StringUtils;

public class PlayerFactory {

    public static String[] tankSkins={"sma","smb","smc","mda","mdb","mdc","hva","hvb","hvc"};

    public static BasePlayer createPlayer(String skin){
        if(StringUtils.isEmpty(skin)){
            return new SmallTankA();
        }
        BasePlayer basePlayer=null;
        if(skin.equals("sma")){
            basePlayer=new SmallTankA();
        }else if(skin.equals("smb")){
            basePlayer=new SmallTankB();
        }else if(skin.equals("smc")){
            basePlayer=new SmallTankC();
        }else if(skin.equals("mda")){
            basePlayer=new MidTankA();
        }else if(skin.equals("mdb")){
            basePlayer=new MidTankB();
        }else if(skin.equals("mdc")){
            basePlayer=new MidTankC();
        }else if(skin.equals("hva")){
            basePlayer=new HeavyTankA();
        }else if(skin.equals("hvb")){
            basePlayer=new HeavyTankB();
        }else if(skin.equals("hvc")){
            basePlayer=new HeavyTankC();
        }else if(skin.equals("ea")){
            basePlayer=new EnemyTank();
        }
        if(basePlayer==null){
            basePlayer=new SmallTankA();
        }
        return basePlayer;
    }

    public static BasePlayer randomTank(){
        int inx = MyUtils.randomBetween(0,tankSkins.length);
        if (inx>=tankSkins.length){
            inx=0;
        }
        String skin=tankSkins[inx];
        return createPlayer(skin);
    }
}
