package hotcoin.service.impl;

import com.alibaba.fastjson.JSON;
import com.qkwl.common.dto.Enum.EntrustTypeEnum;
import com.qkwl.common.dto.mq.MQEntrust;
import hotcoin.component.CancelOrderComponent;
import hotcoin.component.HttpClientComponent;
import hotcoin.component.RabbitMqSendComponent;
import hotcoin.model.po.EntrustPo;
import hotcoin.model.po.SystemRiskManagementPo;
import hotcoin.model.po.UserCoinWalletPo;
import hotcoin.service.EntrustService;
import hotcoin.service.RealTimeTradeStatService;
import hotcoin.service.SystemRiskManagementService;
import hotcoin.service.UserCoinWalletService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @ProjectName: hotcoin-risk-management
 * @Package: hotcoin.service.impl
 * @ClassName: RealTimeTradeStatServiceImpl
 * @Author: hf
 * @Description:
 * @Date: 2019/8/17 11:15
 * @Version: 1.0
 */
@Service
@Slf4j
public class RealTimeTradeStatServiceImpl implements RealTimeTradeStatService {
    @Autowired
    private SystemRiskManagementService riskManagementService;
    @Autowired
    private UserCoinWalletService userCoinWalletService;

    @Autowired
    private HttpClientComponent httpClientComponent;
    @Autowired
    private EntrustService entrustService;
    @Autowired
    private CancelOrderComponent cancelOrderComponent;
    @Autowired
    private RabbitMqSendComponent rabbitMqSendComponent;

    @Override
    public void processWalletInfo() {
        List<SystemRiskManagementPo> list = riskManagementService.query(new SystemRiskManagementPo());
        if (CollectionUtils.isEmpty(list)) {
            return;
        }
        List<SystemRiskManagementPo> filterResultList = list.stream().filter(item -> item.getUserStatus() <= 2)
                .collect(Collectors.toList());
        if (CollectionUtils.isEmpty(filterResultList)) {
            return;
        }
        log.info("get riskManagement config from db list is->{}", JSON.toJSONString(filterResultList));
        for (SystemRiskManagementPo item : filterResultList) {
            Double accountRealFunds = item.getAccountRealFunds();
            Integer id = item.getId();
            Double closeoutLineFunds = item.getCapitalCloseoutLine();
            Double debitTimes = item.getDebitTimes();
            Integer status = item.getUserStatus();
            Integer userId = item.getUserId();
            Integer coinId = item.getCoinId();
            Double rechargeFunds = item.getRechargeFunds();
            Double warningLineFunds = item.getCapitalWarningLine();
            UserCoinWalletPo userCoinWallet = new UserCoinWalletPo(userId, coinId);
            List<UserCoinWalletPo> userCoinWalletPoList = userCoinWalletService.query(userCoinWallet);
            if (CollectionUtils.isEmpty(userCoinWalletPoList)) {
                continue;
            }
            UserCoinWalletPo walletQueryResult = userCoinWalletPoList.get(0);
            Double walletTotal = walletQueryResult.getTotal().doubleValue();
            log.info("用户状态:->{},用户本金->{},用户借款:->{},用户总资产->{},accountRealFunds is->{}",
                    status, rechargeFunds, rechargeFunds * debitTimes, walletTotal, accountRealFunds);

            boolean isChanged = Math.abs(walletTotal - accountRealFunds) > 0;
            if (isChanged) {
                log.error("data is changed wallet data is->{},accountRealFunds is->{}", walletTotal, accountRealFunds);
                SystemRiskManagementPo updateData = new SystemRiskManagementPo(id, walletTotal);
                riskManagementService.updateByPrimaryKey(updateData);
            }

            double debitAmount = walletTotal - rechargeFunds * debitTimes;
            boolean reachWarning = isSuit2Warning(debitAmount, rechargeFunds, warningLineFunds);
            // 状态回退,一般为用户继续充值了
            if (!reachWarning && status.equals(2)) {
                updateUserStatusByPrimaryKey(id, 1);
                log.error("用户继续充值,更新状态成功");
                continue;
            }
            // 是否达到预警条件
            if (status.equals(1) && reachWarning) {
                log.error("达到预警线 ->{}");
                updateStatusAndSendWarningSMS(id, item);
                continue;
            }
            // 已预警,判断是否需要平仓
            boolean reachCloseout = isSuit2CloseOut(debitAmount, rechargeFunds, closeoutLineFunds);
            if (status.equals(2) && reachCloseout) {
                log.error("reach closeout->{},closeLineFunds is->{}", JSON.toJSONString(item), closeoutLineFunds);
                updateStatusAndSendCloseoutSMS(id, item);
                // 发送平仓mq消息
                rabbitMqSendComponent.sendCloseoutMQAction(item, coinId);
                // 发送撤单mq
                getAllOrderAndCancel(userId);
                continue;
            }
        }
    }

    @Override
    public void test(Integer userId, Integer coinId) {
        List<EntrustPo> list = entrustService.query(new EntrustPo(userId));
        log.info("query result->{}", JSON.toJSONString(list));
        if (CollectionUtils.isEmpty(list)) {
            return;
        }

        for (EntrustPo item : list) {
            MQEntrust entrust = convert2MQEntrust(item);
            rabbitMqSendComponent.sendCancelOrderAction(coinId, entrust);
        }
    }

    /**
     * 更新状态并发送预警短信
     *
     * @param id
     * @param item
     */
    private void updateStatusAndSendWarningSMS(Integer id, SystemRiskManagementPo item) {
        boolean isUpdated = updateUserStatusByPrimaryKey(id, 2);
        if (isUpdated) {
            boolean isSuccess = httpClientComponent.sendWarningSms(item);
            if (isSuccess) {
                log.error("发送预警短信成功");
            } else {
                log.error("发送预警短信失败");
            }
        } else {
            log.error("预警更新用户状态异常");
        }
    }

    /**
     * 更新状态并发送平仓短信
     *
     * @param id
     * @param item
     */
    private void updateStatusAndSendCloseoutSMS(Integer id, SystemRiskManagementPo item) {
        boolean isUpdated = updateUserStatusByPrimaryKey(id, 3);
        if (isUpdated) {
            boolean isSuccess = httpClientComponent.sendCloseoutSms(item);
            if (isSuccess) {
                log.error("发送平仓短信成功");
            } else {
                log.error("发送平仓短信失败");
            }
        } else {
            log.error("平仓更新用户状态异常");
        }
    }

    private boolean isSuit2CloseOut(double debitAmount, double rechargeFunds, double closeoutLineFunds) {
        return debitAmount <= closeoutLineFunds * (rechargeFunds / 100.0);
    }

    private boolean isSuit2Warning(double debitAmount, double rechargeFunds, double warningLineFunds) {
        log.info(" debitAmount is ->{},rechargeFunds is->{},warningLineFunds is->{},calc is->{}", debitAmount, rechargeFunds,
                warningLineFunds, warningLineFunds * (rechargeFunds / 100.0));
        return debitAmount <= warningLineFunds * (rechargeFunds / 100.0);
    }

    /**
     * 获取用户所以订单
     *
     * @param userId
     * @return
     */
    private void getAllOrderAndCancel(Integer userId) {
        List<EntrustPo> list = entrustService.query(new EntrustPo(userId));
        if (CollectionUtils.isEmpty(list)) {
            return;
        }
        for (EntrustPo item : list) {
            Integer status = item.getFstatus();
            if (status.equals(1) || status.equals(2)) {
                MQEntrust entrust = convert2MQEntrust(item);
                cancelOrderComponent.cancleEntrust(entrust);
            }
        }
    }


    /**
     * 转换mqEntrust字段
     *
     * @param po
     */
    private MQEntrust convert2MQEntrust(EntrustPo po) {
        MQEntrust mqEntrust = new MQEntrust();
        int ftype = EntrustTypeEnum.CANCEL.getCode();
        mqEntrust.setFtype(ftype);
        mqEntrust.setCreatedate(po.getFcreatetime().getTime());
        mqEntrust.setFentrustid(po.getFid().toString());
        mqEntrust.setFreefee(false);
        mqEntrust.setFsource(po.getFsource());
        mqEntrust.setFstatus(po.getFstatus());
        mqEntrust.setFtradeid(po.getFtradeid());
        mqEntrust.setFuid(po.getFuid());
        mqEntrust.setLeftcount(po.getFleftcount().toString());
        BigDecimal prize = po.getFprize().setScale(10);
        mqEntrust.setPrize(prize.toString());
        mqEntrust.setRecover(0);
        mqEntrust.setSize(po.getFcount().toString());
        return mqEntrust;
    }

    /**
     * 更新用户状态
     *
     * @param id
     * @param status
     */
    private boolean updateUserStatusByPrimaryKey(Integer id, Integer status) {
        SystemRiskManagementPo updateStatusData = new SystemRiskManagementPo(id, status);
        Integer b = riskManagementService.updateByPrimaryKey(updateStatusData);
        if (b > 0) {
            log.error("update user status success");
            return true;
        } else {
            log.error("update user status fail");
            return false;
        }
    }
}
