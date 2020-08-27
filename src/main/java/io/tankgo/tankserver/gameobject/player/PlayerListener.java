package io.tankgo.tankserver.gameobject.player;

import io.tankgo.tankserver.pojo.BulletParam;
import io.tankgo.tankserver.pojo.GameUserInput;

import java.util.List;

public interface PlayerListener {
    void onShoot(BulletParam bulletParam);
}
