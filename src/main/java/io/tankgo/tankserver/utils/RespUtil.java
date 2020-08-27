package io.tankgo.tankserver.utils;


import io.tankgo.tankserver.pojo.RespEntity;

public class RespUtil {
    public static RespEntity success(Object data){
        RespEntity respEntity=new RespEntity(0,true,"success",data);
        return respEntity;
    }

    public static RespEntity success(Object data,double time){
        RespEntity respEntity=new RespEntity(0,true,"success",data);
        respEntity.setTime(time);
        return respEntity;
    }

    public static RespEntity success(){
        RespEntity respEntity=new RespEntity(0,true,"success",new Object());
        return respEntity;
    }

    public static RespEntity fail(int code,String msg){
        RespEntity respEntity=new RespEntity(code,false,msg,null);
        return respEntity;
    }
}
