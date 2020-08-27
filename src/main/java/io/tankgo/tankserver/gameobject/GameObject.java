package io.tankgo.tankserver.gameobject;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
public abstract class GameObject implements GameObjectState {

    protected String id;
    protected String userName;
    protected String grp;
    protected String type;
    protected String skin;
    protected String state;

    protected double x;
    protected double y;
    protected double rad;

    @JsonIgnore
    protected int ogx;
    @JsonIgnore
    protected int ogy;
//    @JsonIgnore
    protected int gx;
//    @JsonIgnore
    protected int gy;


}
