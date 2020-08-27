package io.tankgo.tankserver.gameobject;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.tankgo.tankserver.pojo.HitReward;

public interface GameObjectState {
    @JsonIgnore
    void boom();

    @JsonIgnore
    void worldUpdate(double worldTime);

    @JsonIgnore
    boolean checkRemove();

    @JsonIgnore
    HitReward hit(GameObject object);

    String getState();

    @JsonIgnore
    void destroy();

}
