package io.tankgo.tankserver.config;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class BidAskConfig {
    private static ConcurrentMap<String,Double> map=new ConcurrentHashMap<>();

    private String binanceAk="DFxHSWeusryRQT89d1LbENDcluLUywq3pIXfi8lYLzIJURshz2y5sThfaT6Qdoyq";
    private String binanceSK="QdprSdBIM6sndoTX7jxZLun5FXQdDHaWHQ6jVqavTXL6uIbNdVTOuJN9l0zQBK3P";
    static {
        map.put("eos.EOS",3.5d);
        map.put("cocos.COCOS",0.002d);
        map.put("cocos_test.COCOS",0.002d);
        map.put("free.BOOM",0.1d);
        map.put("iost.IOST",0.008);
        map.put("trx.TRX",0.016);
    }

    public static Double getPrice(String symbol){
        Double price=map.getOrDefault(symbol,0.01d);
        return price;
    }

    public static Double setPrice(String symbol){
        Double price=map.getOrDefault(symbol,0.01d);
        return price;
    }

    public static List<String> getCoins(){
        return new ArrayList<>(map.keySet());
    }
}
