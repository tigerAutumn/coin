package com.qkwl.common.dto.capital;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class AssetImbalance implements Serializable{

	private static final long serialVersionUID = 1L;

	private Integer id;

    private Integer userId;

    private Integer coinId;
    
    private String coinName;

    private BigDecimal recharge;

    private BigDecimal rechargeWork;

    private BigDecimal rewardCoin;

    private BigDecimal withdraw;

    private BigDecimal buy;

    private BigDecimal sell;

    private BigDecimal fees;

    private BigDecimal coinTradeBuy;

    private BigDecimal coinTradeSell;

    private BigDecimal coinTradeFee;

    private BigDecimal vip6;

    private BigDecimal pushIn;

    private BigDecimal pushOut;

    private BigDecimal financesCountSend;

    private BigDecimal withdrawFrozen;

    private BigDecimal tradeFrozen;

    private BigDecimal tradeFrozenCoin;

    private BigDecimal pushFrozen;

    private BigDecimal frozenFinances;

    private BigDecimal c2cRecharge;

    private BigDecimal c2cWithdraw;

    private BigDecimal c2cWithdrawFrozen;

    private BigDecimal commission;
    
    private BigDecimal airdrop;
    
    //otc
    private BigDecimal otcTransferIn;
    private BigDecimal otcTransferOut;
    
    //杠杆
    private BigDecimal leverTransferIn;
    private BigDecimal leverTransferOut;

    private BigDecimal freePlan;

    private BigDecimal free;
    
    
    private BigDecimal frozen;
    private BigDecimal frozenPlan;
    
    private BigDecimal unfreeze;
    
    private BigDecimal dividend;
    
    private BigDecimal reward;
    
    //矿池
    private BigDecimal orepoolLock;
    private BigDecimal orepoolUnlock;
    private BigDecimal orepoolIncome;
    
    //创新区存币
    private BigDecimal innovationDepositLock;
    private BigDecimal innovationDepositUnlock;
    //存币解冻
    private BigDecimal depositUnfrozen;
    
    //平仓
    private BigDecimal closePositionOut;
    private BigDecimal closePositionIn;
    
    
    //红包
    private BigDecimal sendRedEnvelope;
    private BigDecimal receiveRedEnvelope;
    private BigDecimal returnRedEnvelope;
    private BigDecimal deductRedEnvelope;
    
    private Date createTime;
    
    //查询条件用，是否过滤API用户
    private boolean filterApi;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getCoinId() {
        return coinId;
    }

    public void setCoinId(Integer coinId) {
        this.coinId = coinId;
    }

    public BigDecimal getRecharge() {
        return recharge;
    }

    public void setRecharge(BigDecimal recharge) {
        this.recharge = recharge;
    }

    public BigDecimal getRechargeWork() {
        return rechargeWork;
    }

    public void setRechargeWork(BigDecimal rechargeWork) {
        this.rechargeWork = rechargeWork;
    }

    public BigDecimal getRewardCoin() {
        return rewardCoin;
    }

    public void setRewardCoin(BigDecimal rewardCoin) {
        this.rewardCoin = rewardCoin;
    }

    public BigDecimal getWithdraw() {
        return withdraw;
    }

    public void setWithdraw(BigDecimal withdraw) {
        this.withdraw = withdraw;
    }

    public BigDecimal getBuy() {
        return buy;
    }

    public void setBuy(BigDecimal buy) {
        this.buy = buy;
    }

    public BigDecimal getSell() {
        return sell;
    }

    public void setSell(BigDecimal sell) {
        this.sell = sell;
    }

    public BigDecimal getFees() {
        return fees;
    }

    public void setFees(BigDecimal fees) {
        this.fees = fees;
    }

    public BigDecimal getCoinTradeBuy() {
        return coinTradeBuy;
    }

    public void setCoinTradeBuy(BigDecimal coinTradeBuy) {
        this.coinTradeBuy = coinTradeBuy;
    }

    public BigDecimal getCoinTradeSell() {
        return coinTradeSell;
    }

    public void setCoinTradeSell(BigDecimal coinTradeSell) {
        this.coinTradeSell = coinTradeSell;
    }

    public BigDecimal getCoinTradeFee() {
        return coinTradeFee;
    }

    public void setCoinTradeFee(BigDecimal coinTradeFee) {
        this.coinTradeFee = coinTradeFee;
    }

    public BigDecimal getVip6() {
        return vip6;
    }

    public void setVip6(BigDecimal vip6) {
        this.vip6 = vip6;
    }

    public BigDecimal getPushIn() {
        return pushIn;
    }

    public void setPushIn(BigDecimal pushIn) {
        this.pushIn = pushIn;
    }

    public BigDecimal getPushOut() {
        return pushOut;
    }

    public void setPushOut(BigDecimal pushOut) {
        this.pushOut = pushOut;
    }

    public BigDecimal getFinancesCountSend() {
        return financesCountSend;
    }

    public void setFinancesCountSend(BigDecimal financesCountSend) {
        this.financesCountSend = financesCountSend;
    }

    public BigDecimal getWithdrawFrozen() {
        return withdrawFrozen;
    }

    public void setWithdrawFrozen(BigDecimal withdrawFrozen) {
        this.withdrawFrozen = withdrawFrozen;
    }

    public BigDecimal getTradeFrozen() {
        return tradeFrozen;
    }

    public void setTradeFrozen(BigDecimal tradeFrozen) {
        this.tradeFrozen = tradeFrozen;
    }

    public BigDecimal getTradeFrozenCoin() {
        return tradeFrozenCoin;
    }

    public void setTradeFrozenCoin(BigDecimal tradeFrozenCoin) {
        this.tradeFrozenCoin = tradeFrozenCoin;
    }

    public BigDecimal getPushFrozen() {
        return pushFrozen;
    }

    public void setPushFrozen(BigDecimal pushFrozen) {
        this.pushFrozen = pushFrozen;
    }

    public BigDecimal getFrozenFinances() {
        return frozenFinances;
    }

    public void setFrozenFinances(BigDecimal frozenFinances) {
        this.frozenFinances = frozenFinances;
    }

    public BigDecimal getC2cRecharge() {
        return c2cRecharge;
    }

    public void setC2cRecharge(BigDecimal c2cRecharge) {
        this.c2cRecharge = c2cRecharge;
    }

    public BigDecimal getC2cWithdraw() {
        return c2cWithdraw;
    }

    public void setC2cWithdraw(BigDecimal c2cWithdraw) {
        this.c2cWithdraw = c2cWithdraw;
    }

    public BigDecimal getC2cWithdrawFrozen() {
        return c2cWithdrawFrozen;
    }

    public void setC2cWithdrawFrozen(BigDecimal c2cWithdrawFrozen) {
        this.c2cWithdrawFrozen = c2cWithdrawFrozen;
    }

    public BigDecimal getCommission() {
        return commission;
    }

    public void setCommission(BigDecimal commission) {
        this.commission = commission;
    }

    public BigDecimal getFreePlan() {
        return freePlan;
    }

    public void setFreePlan(BigDecimal freePlan) {
        this.freePlan = freePlan;
    }

    public BigDecimal getFree() {
        return free;
    }

    public void setFree(BigDecimal free) {
        this.free = free;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

	public String getCoinName() {
		return coinName;
	}

	public void setCoinName(String coinName) {
		this.coinName = coinName;
	}

	public BigDecimal getAirdrop() {
		return airdrop;
	}

	public void setAirdrop(BigDecimal airdrop) {
		this.airdrop = airdrop;
	}

	public BigDecimal getOtcTransferIn() {
		return otcTransferIn;
	}

	public void setOtcTransferIn(BigDecimal otcTransferIn) {
		this.otcTransferIn = otcTransferIn;
	}

	public BigDecimal getOtcTransferOut() {
		return otcTransferOut;
	}

	public void setOtcTransferOut(BigDecimal otcTransferOut) {
		this.otcTransferOut = otcTransferOut;
	}

	public BigDecimal getUnfreeze() {
		return unfreeze;
	}

	public void setUnfreeze(BigDecimal unfreeze) {
		this.unfreeze = unfreeze;
	}

	public BigDecimal getDividend() {
		return dividend;
	}

	public void setDividend(BigDecimal dividend) {
		this.dividend = dividend;
	}

	public BigDecimal getReward() {
		return reward;
	}

	public void setReward(BigDecimal reward) {
		this.reward = reward;
	}

	public BigDecimal getOrepoolLock() {
		return orepoolLock;
	}

	public void setOrepoolLock(BigDecimal orepoolLock) {
		this.orepoolLock = orepoolLock;
	}

	public BigDecimal getOrepoolUnlock() {
		return orepoolUnlock;
	}

	public void setOrepoolUnlock(BigDecimal orepoolUnlock) {
		this.orepoolUnlock = orepoolUnlock;
	}

	public BigDecimal getOrepoolIncome() {
		return orepoolIncome;
	}

	public void setOrepoolIncome(BigDecimal orepoolIncome) {
		this.orepoolIncome = orepoolIncome;
	}

	public BigDecimal getLeverTransferIn() {
		return leverTransferIn;
	}

	public void setLeverTransferIn(BigDecimal leverTransferIn) {
		this.leverTransferIn = leverTransferIn;
	}

	public BigDecimal getLeverTransferOut() {
		return leverTransferOut;
	}

	public void setLeverTransferOut(BigDecimal leverTransferOut) {
		this.leverTransferOut = leverTransferOut;
	}

	public BigDecimal getInnovationDepositLock() {
		return innovationDepositLock;
	}

	public void setInnovationDepositLock(BigDecimal innovationDepositLock) {
		this.innovationDepositLock = innovationDepositLock;
	}

	public BigDecimal getInnovationDepositUnlock() {
		return innovationDepositUnlock;
	}

	public void setInnovationDepositUnlock(BigDecimal innovationDepositUnlock) {
		this.innovationDepositUnlock = innovationDepositUnlock;
	}

	public BigDecimal getDepositUnfrozen() {
		return depositUnfrozen;
	}

	public void setDepositUnfrozen(BigDecimal depositUnfrozen) {
		this.depositUnfrozen = depositUnfrozen;
	}

	public BigDecimal getFrozen() {
		return frozen;
	}

	public void setFrozen(BigDecimal frozen) {
		this.frozen = frozen;
	}

	public BigDecimal getFrozenPlan() {
		return frozenPlan;
	}

	public void setFrozenPlan(BigDecimal frozenPlan) {
		this.frozenPlan = frozenPlan;
	}

	public BigDecimal getClosePositionOut() {
		return closePositionOut;
	}

	public void setClosePositionOut(BigDecimal closePositionOut) {
		this.closePositionOut = closePositionOut;
	}

	public BigDecimal getClosePositionIn() {
		return closePositionIn;
	}

	public void setClosePositionIn(BigDecimal closePositionIn) {
		this.closePositionIn = closePositionIn;
	}
	

	public BigDecimal getSendRedEnvelope() {
		return sendRedEnvelope;
	}

	public void setSendRedEnvelope(BigDecimal sendRedEnvelope) {
		this.sendRedEnvelope = sendRedEnvelope;
	}

	public BigDecimal getReceiveRedEnvelope() {
		return receiveRedEnvelope;
	}

	public void setReceiveRedEnvelope(BigDecimal receiveRedEnvelope) {
		this.receiveRedEnvelope = receiveRedEnvelope;
	}

	public BigDecimal getReturnRedEnvelope() {
		return returnRedEnvelope;
	}

	public void setReturnRedEnvelope(BigDecimal returnRedEnvelope) {
		this.returnRedEnvelope = returnRedEnvelope;
	}

	public BigDecimal getDeductRedEnvelope() {
		return deductRedEnvelope;
	}

	public void setDeductRedEnvelope(BigDecimal deductRedEnvelope) {
		this.deductRedEnvelope = deductRedEnvelope;
	}

	public boolean isFilterApi() {
		return filterApi;
	}

	public void setFilterApi(boolean filterApi) {
		this.filterApi = filterApi;
	}
    
}