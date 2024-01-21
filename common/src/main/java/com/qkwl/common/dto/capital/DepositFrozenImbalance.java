package com.qkwl.common.dto.capital;

import java.math.BigDecimal;
import java.util.Date;

import com.qkwl.common.match.MathUtils;

public class DepositFrozenImbalance {
    private Integer id;

    private Integer userId;

    private Integer coinId;

    private BigDecimal rechargeFrozen;

    private BigDecimal otcTransferIn;

    private BigDecimal tradeUnfreeze;
    
    private BigDecimal depositUnfrozen;
    
    private BigDecimal depositFrozenCalculation;
    
    private BigDecimal depositFrozenTotalCalculation;

    private BigDecimal walletDepositFrozen;

    private BigDecimal walletDepositFrozenTotal;

    private Date createTime;

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

    public BigDecimal getRechargeFrozen() {
        return rechargeFrozen;
    }

    public void setRechargeFrozen(BigDecimal rechargeFrozen) {
        this.rechargeFrozen = rechargeFrozen;
    }

    public BigDecimal getOtcTransferIn() {
        return otcTransferIn;
    }

    public void setOtcTransferIn(BigDecimal otcTransferIn) {
        this.otcTransferIn = otcTransferIn;
    }

    public BigDecimal getTradeUnfreeze() {
        return tradeUnfreeze;
    }

    public void setTradeUnfreeze(BigDecimal tradeUnfreeze) {
        this.tradeUnfreeze = tradeUnfreeze;
    }

    public BigDecimal getWalletDepositFrozen() {
        return walletDepositFrozen;
    }

    public void setWalletDepositFrozen(BigDecimal walletDepositFrozen) {
        this.walletDepositFrozen = walletDepositFrozen;
    }

    public BigDecimal getWalletDepositFrozenTotal() {
        return walletDepositFrozenTotal;
    }

    public void setWalletDepositFrozenTotal(BigDecimal walletDepositFrozenTotal) {
        this.walletDepositFrozenTotal = walletDepositFrozenTotal;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

	public BigDecimal getDepositFrozenCalculation() {
		BigDecimal add = BigDecimal.ZERO;
		if(rechargeFrozen != null && otcTransferIn != null) {
			add = MathUtils.add(rechargeFrozen, otcTransferIn);
		}else if(rechargeFrozen == null && otcTransferIn != null) {
			add = otcTransferIn;
		}else if(rechargeFrozen != null && otcTransferIn == null) {
			add = rechargeFrozen;
		}
		if(tradeUnfreeze != null) {
			add = MathUtils.sub(add, tradeUnfreeze);
		}
		if(depositUnfrozen != null) {
			add = MathUtils.sub(add, depositUnfrozen);
		}
		return add;
	}

	public BigDecimal getDepositFrozenTotalCalculation() {
		BigDecimal add = BigDecimal.ZERO;
		if(rechargeFrozen != null && otcTransferIn != null) {
			add = MathUtils.add(rechargeFrozen, otcTransferIn);
		}else if(rechargeFrozen == null && otcTransferIn != null) {
			add = otcTransferIn;
		}else if(rechargeFrozen != null && otcTransferIn == null) {
			add = rechargeFrozen;
		}
		return add;
	}

	public BigDecimal getDepositUnfrozen() {
		return depositUnfrozen;
	}

	public void setDepositUnfrozen(BigDecimal depositUnfrozen) {
		this.depositUnfrozen = depositUnfrozen;
	}
    
    
}