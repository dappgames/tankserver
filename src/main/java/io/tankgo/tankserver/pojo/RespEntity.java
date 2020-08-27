package io.tankgo.tankserver.pojo;

import lombok.Data;

import java.io.Serializable;

@Data
public class RespEntity implements Serializable{
    private static final long serialVersionUID = -7143230026097105767L;
    private int code;
    private Boolean success;
    private double time;
    private String msg;
    private Object data;

    public RespEntity(int code,boolean success, String msg, Object data) {
        this.code = code;
        this.success=success;
        this.msg = msg;
        this.data = data;
    }
}