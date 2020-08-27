package io.tankgo.tankserver.utils;

import java.util.Random;
import java.util.UUID;

public class MyUtils {


    public static String getRandColor(){
        //红色
        String red;
        //绿色
        String green;
        //蓝色
        String blue;
        //生成随机对象
        Random random = new Random();
        //生成红色颜色代码
        red = Integer.toHexString(random.nextInt(256)).toUpperCase();
        //生成绿色颜色代码
        green = Integer.toHexString(random.nextInt(256)).toUpperCase();
        //生成蓝色颜色代码
        blue = Integer.toHexString(random.nextInt(256)).toUpperCase();

        //判断红色代码的位数
        red = red.length()==1 ? "0" + red : red ;
        //判断绿色代码的位数
        green = green.length()==1 ? "0" + green : green ;
        //判断蓝色代码的位数
        blue = blue.length()==1 ? "0" + blue : blue ;
        //生成十六进制颜色值
        String color = "#"+red+green+blue;
        return color;
    }

    public static String getRandomString(int length) {
        String str = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random random = new Random();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < length; i++) {
            int number = random.nextInt(62);
            sb.append(str.charAt(number));
        }
        return sb.toString();
    }

    public static String getRandomName(int length) {
        String str = "abcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < length; i++) {
            int number = random.nextInt(36);
            sb.append(str.charAt(number));
        }
        return sb.toString();
    }

    public static String getRandomChinese() {
        return new String(new char[]{(char) (new Random().nextInt(20902) + 19968)});
    }

    public static String getRandName(int length) {
        String name = "";
        for (int i = 0; i < length; i++) {
            name += getRandomChinese();
        }
        return name;
    }

    public static int randomBetween(int start,int end) {
        Random random = new Random();
        return random.nextInt(end-start)+start;
    }

    public static double randomBetween(double start,double end) {
        return Math.random()*(end-start)+start;
    }

    public static int randomDir() {
        Random random = new Random();
        int r=random.nextInt(2);
        if(r==0){
            return -1;
        }else{
            return 1;
        }
    }


    private static String digits(long val, int digits) {
        long hi = 1L << (digits * 4);
        return Numbers.toString(hi | (val & (hi - 1)), Numbers.MAX_RADIX)
                .substring(1);
    }

    /**
     * 以62进制（字母加数字）生成19位UUID，最短的UUID
     *
     * @return
     */
    public static String shortUuid() {
        UUID uuid = UUID.randomUUID();
        StringBuilder sb = new StringBuilder();
        sb.append(digits(uuid.getMostSignificantBits() >> 32, 8));
        sb.append(digits(uuid.getMostSignificantBits() >> 16, 4));
        sb.append(digits(uuid.getMostSignificantBits(), 4));
        sb.append(digits(uuid.getLeastSignificantBits() >> 48, 4));
        sb.append(digits(uuid.getLeastSignificantBits(), 12));
        return sb.toString();
    }

    public static String randomCoin(){
//        String[] coins={"cocos_test.COCOS","free.BOOM","eos.EOS","trx.TRX","iost.IOST","cocos.COCOS",};
        String[] coins={"cocos_test.COCOS","cocos_test.COCOS","free.BOOM"};
        int inx=randomBetween(0,3);
        return coins[inx];
    }

    public static String randomAllCoin(){
        String[] coins={"cocos_test.COCOS","free.BOOM","eos.EOS","trx.TRX","iost.IOST","cocos.COCOS",};
//        String[] coins={"cocos_test.COCOS","free.BOOM"};
        int inx=randomBetween(0,5);
        return coins[inx];
    }


}
