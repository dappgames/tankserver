package io.tankgo.tankserver.gameobject.bullet;

import io.tankgo.tankserver.gameobject.bullet.hbullet.HeavyBullet;
import io.tankgo.tankserver.gameobject.bullet.laserbullet.LaserBullet;
import io.tankgo.tankserver.gameobject.bullet.mbullet.MidBullet;
import io.tankgo.tankserver.gameobject.bullet.plazmbullet.PlazmBullet;
import io.tankgo.tankserver.gameobject.bullet.rocketbullet.RocketBullet;
import io.tankgo.tankserver.gameobject.bullet.sbullet.SmallBullet;
import org.apache.commons.lang3.StringUtils;

public class BulletFactory {
    public static BaseBullet createBullet(String skin) {
        BaseBullet baseBullet = null;
        if (StringUtils.isEmpty(skin)) {
            return new SmallBullet();
        }
        if (skin.equals("sm")) {
            baseBullet = new SmallBullet();
        } else if (skin.equals("md")) {
            baseBullet = new MidBullet();
        } else if (skin.equals("hv")) {
            baseBullet = new HeavyBullet();
        } else if (skin.equals("pz")) {
            baseBullet = new PlazmBullet();
        } else if (skin.equals("ls")) {
            baseBullet = new LaserBullet();
        } else if (skin.equals("rk")) {
            baseBullet = new RocketBullet();
        }
        return baseBullet;
    }
}
