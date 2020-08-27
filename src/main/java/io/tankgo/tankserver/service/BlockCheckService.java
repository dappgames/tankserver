package io.tankgo.tankserver.service;

import io.tankgo.tankserver.entity.BlockCheckLogDO;
import io.tankgo.tankserver.pojo.UserInfoDTO;
import org.apache.commons.lang3.StringUtils;
import org.nutz.dao.impl.NutDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class BlockCheckService {

    @Autowired
    private NutDao nutDao;

    public boolean addLoginCheck(UserInfoDTO userInfoDTO){
        Date date=new Date();
        String contractName="";
        if(userInfoDTO.getChainName().equals("cocos_test")){
            contractName="cocostankgo";
        }else if(userInfoDTO.getChainName().equals("eos")){
            contractName="tankgo.io";
        }
        if(StringUtils.isEmpty(contractName)){
            return false;
        }
        BlockCheckLogDO blockCheckLogDO=BlockCheckLogDO.builder()
                .ctime(date)
                .ptime(date)
                .walletId(userInfoDTO.getUserWalletId())
                .socketId(userInfoDTO.getSocketId())
                .extArgs("")
                .chainName(userInfoDTO.getChainName())
                .contractName(contractName)
                .actionName("login")
                .fromAccount(userInfoDTO.getAccountName())
                .toAccount(contractName)
                .quantity(null)
                .memo(userInfoDTO.getCheckMemo())
                .actualQuantity(null)
                .status(0)
                .tryCnt(0)
                .fastEvent("allowLogin")
                .finishEvent("")
                .failEvent("")
                .build();
        blockCheckLogDO=nutDao.insert(blockCheckLogDO,true,false,false);
        if(blockCheckLogDO.getId()!=null){
            return true;
        }
        return false;
    }
}
