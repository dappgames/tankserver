package io.tankgo.tankserver.service;

import io.tankgo.tankserver.entity.UserBalanceDO;
import io.tankgo.tankserver.entity.UserInfoDO;
import io.tankgo.tankserver.entity.UserWalletDO;
import io.tankgo.tankserver.pojo.RespEntity;
import io.tankgo.tankserver.pojo.UserBalanceDTO;
import io.tankgo.tankserver.pojo.UserInfoDTO;
import io.tankgo.tankserver.utils.CryptUtils;
import io.tankgo.tankserver.utils.MyUtils;
import io.tankgo.tankserver.utils.RespUtil;
import org.apache.commons.lang3.StringUtils;
import org.nutz.dao.Cnd;
import org.nutz.dao.impl.NutDao;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class UserInfoService {

    @Autowired
    private NutDao nutDao;


    @Autowired
    private BlockCheckService blockCheckService;

    public RespEntity login(UserInfoDTO userInfoDTO){
        if(StringUtils.isEmpty(userInfoDTO.getChainName())){
            return RespUtil.fail(-1,"login fail,missing chainName");
        }
        if(StringUtils.isEmpty(userInfoDTO.getAccountName())){
            return RespUtil.fail(-1,"login fail,missing accountName");
        }
        UserWalletDO userWalletDO = nutDao.fetch(UserWalletDO.class,
                Cnd.where("chain_name","=",userInfoDTO.getChainName())
                        .and("account_name","=",userInfoDTO.getAccountName()));
        if(userWalletDO==null){
            try {
                UserInfoDO userInfoDO= UserInfoDO.builder()
                        .ctime(new Date())
                        .ptime(new Date())
                        .nickName(userInfoDTO.getAccountName())
                        .build();
                userInfoDO = nutDao.insert(userInfoDO,true,false,false);
                UserBalanceDO boomBalance=UserBalanceDO.builder()
                        .ctime(new Date())
                        .ptime(new Date())
                        .userId(userInfoDO.getId())
                        .chainName("free")
                        .tokenSymbol("BOOM")
                        .balance(new BigDecimal("1000"))
                        .version(0L)
                        .build();
                nutDao.insert(boomBalance,true,false,false);
                if(userInfoDO!=null&&userInfoDO.getId()!=null){
                    userWalletDO = UserWalletDO.builder()
                            .ctime(new Date())
                            .ptime(new Date())
                            .chainName(userInfoDTO.getChainName())
                            .accountName(userInfoDTO.getAccountName())
                            .userId(userInfoDO.getId())
                            .sk(MyUtils.getRandomName(16))
                            .build();
                    userWalletDO = nutDao.insert(userWalletDO,true,false,false);
                    if(userWalletDO!=null&&userWalletDO.getId()!=null){
                        int accountNum = nutDao.count(UserWalletDO.class,Cnd.where("user_id","=",userInfoDO.getId()));
                        userInfoDO.setAccountNum(accountNum);
                        nutDao.updateIgnoreNull(userInfoDO);
                        userWallet2userInfoDto(userInfoDTO,userInfoDO, userWalletDO);
                        //设置checkMemo，置空userId，提示前端需要进行区块验证
                        userInfoDTO.setCheckMemo(MyUtils.getRandomName(8));
                        userInfoDTO.setUserId(null);
                        blockCheckService.addLoginCheck(userInfoDTO);
                        return RespUtil.success(userInfoDTO);
                    }else{
                        return RespUtil.fail(-1,"create walletinfo fail");
                    }
                }else{
                    return RespUtil.fail(-1,"create userinfo fail");
                }
            }catch (Exception e){
                return RespUtil.fail(-1,"create account fail");
            }
        }else if(userWalletDO.getUserId()!=null&&userWalletDO.getUserId()>0){
            UserInfoDO userInfoDO=nutDao.fetch(UserInfoDO.class,userWalletDO.getUserId());
            if(userInfoDO!=null){
                userWallet2userInfoDto(userInfoDTO,userInfoDO, userWalletDO);
                List<UserBalanceDTO> balanceDTOS=new ArrayList<>();
                if(userInfoDO!=null&&userInfoDO.getId()!=null&&userInfoDO.getId()>0){
                    List<UserBalanceDO> balanceDOS=nutDao.query(UserBalanceDO.class,Cnd.where("user_id","=",userInfoDO.getId()));
                    if(!CollectionUtils.isEmpty(balanceDOS)){
                        for(UserBalanceDO balanceDO:balanceDOS){
                            UserBalanceDTO balanceDTO=new UserBalanceDTO();
                            BeanUtils.copyProperties(balanceDO,balanceDTO);
                            balanceDTOS.add(balanceDTO);
                        }
                    }
                }
                userInfoDTO.setBalances(balanceDTOS);
                if(checkUserSign(userInfoDTO,userWalletDO)){
                    return RespUtil.success(userInfoDTO);
                }else{
                    userInfoDTO.setCheckMemo(MyUtils.getRandomName(8));
                    userInfoDTO.setUserId(null);
                    blockCheckService.addLoginCheck(userInfoDTO);
                    return RespUtil.success(userInfoDTO);
                }
            }else{
                return RespUtil.fail(-1,"login fail,user not found");
            }
        }else{
            return RespUtil.fail(-1,"login fail");
        }
    }

    public UserInfoDTO getUserInfoSkByWalletId(Long walletId){
        UserWalletDO userWalletDO=nutDao.fetch(UserWalletDO.class,walletId);
        UserInfoDO userInfoDO=nutDao.fetch(UserInfoDO.class,userWalletDO.getUserId());
        UserInfoDTO userInfoDTO=new UserInfoDTO();
        userWallet2userInfoDto(userInfoDTO,userInfoDO,userWalletDO);
        userInfoDTO.setSk(userWalletDO.getSk());
        return userInfoDTO;
    }

    private void userWallet2userInfoDto(UserInfoDTO userInfoDTO,UserInfoDO userInfoDO, UserWalletDO userWalletDO){
        userInfoDTO.setUserId(userInfoDO.getId());
        userInfoDTO.setUserWalletId(userWalletDO.getId());
        userInfoDTO.setNickName(userInfoDO.getNickName());
        userInfoDTO.setChainName(userWalletDO.getChainName());
        userInfoDTO.setAccountName(userWalletDO.getAccountName());
    }

    private boolean checkUserSign(UserInfoDTO userInfoDTO,UserWalletDO userWalletDO){

        if(userInfoDTO==null){
            return false;
        }
        if(userWalletDO==null){
            return false;
        }
        String signMsg=userInfoDTO.getSignMsg();
        if(StringUtils.isEmpty(signMsg)){
            return false;
        }
        Long reqTime=userInfoDTO.getReqTime();
        if(reqTime==null){
            return false;
        }
        long now=System.currentTimeMillis();
        if(Math.abs(now-reqTime)>30000){
            return false;
        }
        String sk=userWalletDO.getSk();
        if(StringUtils.isEmpty(sk)){
            sk=MyUtils.getRandomName(16);
            userWalletDO.setSk(sk);
            nutDao.updateIgnoreNull(userWalletDO);
        }
        String serverSignMsg = CryptUtils.hmacCry1(sk,reqTime+"");
        if(signMsg.equals(serverSignMsg)){
            return true;
        }
        return false;
    }
}
