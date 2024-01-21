package com.hotcoin.activity.component;

import com.alibaba.fastjson.JSON;
import com.hotcoin.activity.dao.UserCoinWalletDao;
import com.hotcoin.activity.model.po.AirdropActivityDetailV2Po;
import com.hotcoin.activity.model.po.UserCoinWalletPo;
import com.hotcoin.activity.model.po.UserWalletBalanceLogPo;
import com.hotcoin.activity.service.AirdropActivityDetailV2Service;
import com.hotcoin.activity.service.UserWalletBalanceLogService;
import com.qkwl.common.dto.Enum.UserWalletBalanceLogDirectionEnum;
import com.qkwl.common.dto.Enum.UserWalletBalanceLogFieldEnum;
import com.qkwl.common.dto.Enum.UserWalletBalanceLogTypeEnum;
import com.qkwl.common.exceptions.BCException;
import com.qkwl.common.match.MathUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * @ProjectName: service_activity_v2
 * @Package: com.hotcoin.activity.component
 * @ClassName: WalletOptComponent
 * @Author: hf
 * @Description:
 * @Date: 2019/7/1 15:32
 * @Version: 1.0
 */
@Component
@Slf4j
public class WalletOptComponent {
    @Autowired
    UserCoinWalletDao userCoinWalletDao;
    @Autowired
    private AirdropActivityDetailV2Service detailV2Service;
    @Autowired
    private UserWalletBalanceLogService userWalletBalanceLogService;
    //可用增加
    public final static int total_add = 0;
    //可用扣除
    public final static int total_sub = 1;
    //冻结增加
    public final static int frozen_add = 2;
    //冻结扣除
    public final static int frozen_sub = 3;
    //可用转冻结
    public final static int total_to_frozen = 4;
    //冻结转可用
    public final static int frozen_to_total = 5;

    int TRY_TIMES = 3;

    /**
     * 更新钱包和记录表(balanceLog和空投记录表)
     *
     * @return
     */
    @Transactional(value = "xaTransMgr", isolation = Isolation.REPEATABLE_READ, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public Boolean changeWallet(Integer userId, Integer coinId, int businessType, BigDecimal amount) throws BCException {
        if (userId == null || coinId == null || amount == null || BigDecimal.ZERO.equals(amount)) {
            throw new BCException("参数错误");
        }
        log.info("start to update wallet userId is[{}],coinId is [{}],amount is[{}]", userId, coinId, amount);
        int trytimes = 0;
        while (true) {
            UserCoinWalletPo userCoinWalletPo = new UserCoinWalletPo(userId, coinId);
            List<UserCoinWalletPo> walletList = userCoinWalletDao.query(userCoinWalletPo);
            if (CollectionUtils.isEmpty(walletList)) {
                throw new BCException("钱包不存在");
            }
            UserCoinWalletPo wallet = walletList.get(0);
            switch (businessType) {
                case total_add:
                    wallet.setTotal(MathUtils.add(wallet.getTotal(), amount));
                    break;
                case total_sub:
                    wallet.setTotal(MathUtils.sub(wallet.getTotal(), amount));
                    break;
                case frozen_add:
                    wallet.setFrozen(MathUtils.add(wallet.getFrozen(), amount));
                    break;
                case frozen_sub:
                    wallet.setFrozen(MathUtils.sub(wallet.getFrozen(), amount));
                    break;
                case total_to_frozen:
                    wallet.setTotal(MathUtils.sub(wallet.getTotal(), amount));
                    wallet.setFrozen(MathUtils.add(wallet.getFrozen(), amount));
                    break;
                case frozen_to_total:
                    wallet.setTotal(MathUtils.add(wallet.getTotal(), amount));
                    wallet.setFrozen(MathUtils.sub(wallet.getFrozen(), amount));
                    break;
                default:
                    return false;
            }
            if (BigDecimal.ZERO.compareTo(wallet.getTotal()) > 0) {
                throw new BCException("钱包可用金额不足");
            }
            if (BigDecimal.ZERO.compareTo(wallet.getFrozen()) > 0) {
                throw new BCException("钱包冻结金额不足");
            }
            wallet.setGmtModified(new Date());
            if (userCoinWalletDao.update(wallet) == 0) {
                if (trytimes == TRY_TIMES) {
                    throw new BCException("更改失败");
                }
                trytimes = trytimes + 1;
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    log.error("update wallet opt fail->{}", e);
                }
            } else {
                return true;
            }
        }
    }


     @Transactional(value = "xaTransMgr", isolation = Isolation.REPEATABLE_READ, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void updateMultipleRecord(Integer userId, Integer coinId, BigDecimal amount, Integer detailId) throws BCException {
        if (userId == null || coinId == null || amount == null || BigDecimal.ZERO.equals(amount)) {
            throw new BCException("参数错误");
        }
        log.info("start to update wallet userId is[{}],coinId is [{}],amount is[{}]", userId, coinId, amount);
        UserCoinWalletPo userCoinWalletPo = new UserCoinWalletPo(userId, coinId);
        List<UserCoinWalletPo> walletList = userCoinWalletDao.query(userCoinWalletPo);
        if (CollectionUtils.isEmpty(walletList)) {
            throw new BCException("钱包不存在");
        }
        UserCoinWalletPo wallet = walletList.get(0);

        wallet.setTotal(MathUtils.add(wallet.getTotal(), amount));

        if (BigDecimal.ZERO.compareTo(wallet.getTotal()) > 0) {
            throw new BCException("钱包可用金额不足");
        }
        if (BigDecimal.ZERO.compareTo(wallet.getFrozen()) > 0) {
            throw new BCException("钱包冻结金额不足");
        }
        wallet.setGmtModified(new Date());
        if (userCoinWalletDao.update(wallet) == 0) {
            throw new BCException("更改失败");
        }
        log.info("update wallet userId is[{}],coinId is [{}],amount is[{}],success", userId, coinId, amount);
        log.info("start 2 update balanceLog");
        //记录钱包操作日志
        UserWalletBalanceLogPo userWalletBalanceLog = new UserWalletBalanceLogPo();
        userWalletBalanceLog.setCoinId(coinId);
        userWalletBalanceLog.setChange(amount);
        userWalletBalanceLog.setCreatedatestamp(System.currentTimeMillis());
        userWalletBalanceLog.setCreatetime(new Date());
        userWalletBalanceLog.setDirection(UserWalletBalanceLogDirectionEnum.in.getValue());
        userWalletBalanceLog.setFieldId(UserWalletBalanceLogFieldEnum.total.getValue());
        userWalletBalanceLog.setSrcId(detailId);
        userWalletBalanceLog.setSrcType(UserWalletBalanceLogTypeEnum.Airdrop_Candy.getCode());
        userWalletBalanceLog.setUid(userId);
        userWalletBalanceLog.setOldvalue(BigDecimal.valueOf(0));
        if (userWalletBalanceLogService.insert(userWalletBalanceLog) <= 0) {
            log.error("添加userBalanceLog记录失败----用户userId= " + userId);
            throw new BCException("添加userBalanceLog记录失败----用户userId= " + userId);
        }
        log.info("insert userBalanceLog success userId is[{}],coinId is [{}],amount is[{}]", userId, coinId, amount);
        AirdropActivityDetailV2Po detailV2Po = new AirdropActivityDetailV2Po();
        detailV2Po.setId(detailId);
        detailV2Po.setStatus(1);
        detailV2Po.setUpdateTime(new Date());
        log.info("start to update activityDetail content [{}]", JSON.toJSONString(detailV2Po));
        Integer num = detailV2Service.updateByPrimaryKey(detailV2Po);
        if (num < 1) {
            log.error("insert activityDetail fail");
            throw new BCException("insert activityDetail fail----userId:[{}] ", userId);
        }
        log.info("insert activityDetail success userId is[{}],coinId is [{}],amount is[{}],success", userId, coinId, amount);
    }

}

