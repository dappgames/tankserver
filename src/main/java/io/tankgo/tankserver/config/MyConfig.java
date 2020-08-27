package io.tankgo.tankserver.config;


public class MyConfig {



    public static String testStr="";
    static {
        String s="aaaaaaaaaa";
        StringBuilder sb=new StringBuilder();
        for(int i=0;i<10000;i++){
            sb.append(i);
            sb.append(s);
            sb.append("\r\n");
        }
        testStr=sb.toString();
    }

}
