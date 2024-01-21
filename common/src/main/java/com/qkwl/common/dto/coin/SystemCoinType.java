package com.qkwl.common.dto.coin;

import com.qkwl.common.dto.Enum.SystemCoinSortEnum;
import com.qkwl.common.dto.Enum.SystemCoinStatusEnum;
import com.qkwl.common.dto.Enum.SystemCoinTypeEnum;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

/**
 * 币种类型表
 */
public class SystemCoinType implements Serializable {
    private static final long serialVersionUID = 1L;
    // 主键ID
    private Integer id;
    // 排序
    private Integer sortId;
    // 名称
    private String name;
    // 简称
    private String shortName;
    // 符号
    private String symbol;
    // 类型
    private Integer type;
    // 钱包类型
    private Integer coinType;
    // 钱包类型
    private String coinName;
    // 状态
    private Integer status;
    // 是否提现
    private Boolean isWithdraw;
    // 是否充值
    private Boolean isRecharge;
    // 风控阀值
    private BigDecimal riskNum;
    // 是否PUSH
    private Boolean isPush;
    // 是否理财
    private Boolean isFinances;
    // 平台ID
    private Integer platformId;
    // 钱包链接IP
    private String walletLink;
    // 钱包链接端口
    private String chainLink;
    // 钱包链接user
    private String accessKey;
    // 钱包链接pass
    private String secrtKey;
    
    //钱包账户
    private String walletAccount;
    
    // ethaccount
    private String ethAccount;
    // 资产ID
    private BigInteger assetId;
    // 网络手续费
    private BigDecimal networkFee;
    // 充值到账确认数
    private Integer confirmations;
    // WEB站LOGO
    private String webLogo;
    // APP站LOGO
    private String appLogo;
    // 创建时间
    private Date gmtCreate;
    // 更新时间
    private Date gmtModified;
    // 版本号
    private Integer version;
    // 合约账户
    private String contractAccount;
    // 合约位号
    private Integer contractWei;
    // 浏览器地址
    private String explorerUrl;
    
    // 浏览器地址
    private String addressUrl;
    
    
    // 是否使用地址标签
    private boolean isUseMemo;
    // 最小充值
    private BigDecimal rechargeMinLimit;
    // 扩展字段
    private String typeName;
    private String statusName;
    private String coinTypeName;
    
    //是否开发otc
    private boolean isOpenOtc; 
    //平台互转是否收取手续费，0 不收，1收
    private boolean isPlatformTransferFee; 
    
    //20190103修改
    //是否是创新区的币
    private boolean isInnovateAreaCoin;
    //释放锁定比例
    private BigDecimal releaseLockingRatio;
    //单日释放上限比例
    private BigDecimal dayReleaseRatio;
    //币种帐户生成情况
    private String createCoinAcountInfo;
    
    
    //是否是从币。从币不会生成钱包，而且充提都会归集到主钱包
    private boolean isSubCoin;
    //关联币种
    private String linkCoin;
    
    
    //是否使用新的方式对接
    private boolean useNewWay;
    //钱包机地址
    private String walletUrl;
    
    
    

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getSortId() {
        return sortId;
    }

    public void setSortId(Integer sortId) {
        this.sortId = sortId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getCoinType() {
        return coinType;
    }

    public void setCoinType(Integer coinType) {
        this.coinType = coinType;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Boolean getIsWithdraw() {
        return isWithdraw;
    }

    public void setIsWithdraw(Boolean withdraw) {
        isWithdraw = withdraw;
    }

    public Boolean getIsRecharge() {
        return isRecharge;
    }

    public void setIsRecharge(Boolean recharge) {
        isRecharge = recharge;
    }

    public BigDecimal getRiskNum() {
        return riskNum;
    }

    public void setRiskNum(BigDecimal riskNum) {
        this.riskNum = riskNum;
    }

    public Boolean getIsPush() {
        return isPush;
    }

    public void setIsPush(Boolean push) {
        isPush = push;
    }

    public Boolean getIsFinances() {
        return isFinances;
    }

    public void setIsFinances(Boolean finances) {
        isFinances = finances;
    }


	public String getWalletLink() {
		return walletLink;
	}

	public void setWalletLink(String walletLink) {
		this.walletLink = walletLink;
	}

	public String getChainLink() {
		return chainLink;
	}

	public void setChainLink(String chainLink) {
		this.chainLink = chainLink;
	}

	public String getAccessKey() {
        return accessKey;
    }

    public void setAccessKey(String accessKey) {
        this.accessKey = accessKey;
    }

    public String getSecrtKey() {
        return secrtKey;
    }

    public void setSecrtKey(String secrtKey) {
        this.secrtKey = secrtKey;
    }

    public BigInteger getAssetId() {
        return assetId;
    }

    public void setAssetId(BigInteger assetId) {
        this.assetId = assetId;
    }

    public Integer getPlatformId() {
        return platformId;
    }

    public void setPlatformId(Integer platformId) {
        this.platformId = platformId;
    }

    public BigDecimal getNetworkFee() {
        return networkFee;
    }

    public void setNetworkFee(BigDecimal networkFee) {
        this.networkFee = networkFee;
    }

    public Integer getConfirmations() {
        return confirmations;
    }

    public void setConfirmations(Integer confirmations) {
        this.confirmations = confirmations;
    }

    public String getWebLogo() {
        return webLogo;
    }

    public void setWebLogo(String webLogo) {
        this.webLogo = webLogo;
    }

    public String getAppLogo() {
        return appLogo;
    }

    public void setAppLogo(String appLogo) {
        this.appLogo = appLogo;
    }

    public Date getGmtCreate() {
        return gmtCreate;
    }

    public void setGmtCreate(Date gmtCreate) {
        this.gmtCreate = gmtCreate;
    }

    public Date getGmtModified() {
        return gmtModified;
    }

    public void setGmtModified(Date gmtModified) {
        this.gmtModified = gmtModified;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public String getTypeName() {
        return SystemCoinTypeEnum.getValueByCode(this.type);
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public String getStatusName() {
        return SystemCoinStatusEnum.getValueByCode(this.status);
    }

    public void setStatusName(String statusName) {
        this.statusName = statusName;
    }

    public String getCoinTypeName() {
        return SystemCoinSortEnum.getValueByCode(this.coinType);
    }

    public void setCoinTypeName(String coinTypeName) {
        this.coinTypeName = coinTypeName;
    }

    public String getEthAccount() {
        return ethAccount;
    }

    public void setEthAccount(String ethAccount) {
        this.ethAccount = ethAccount;
    }
    

	public boolean isOpenOtc() {
		return isOpenOtc;
	}

	public void setOpenOtc(boolean isOpenOtc) {
		this.isOpenOtc = isOpenOtc;
	}

	public String getContractAccount() {
        return contractAccount;
    }

    public void setContractAccount(String contractAccount) {
        this.contractAccount = contractAccount;
    }

    public Integer getContractWei() {
        return contractWei;
    }

    public void setContractWei(Integer contractWei) {
        this.contractWei = contractWei;
    }

    public String getExplorerUrl() {
        return explorerUrl;
    }

    public void setExplorerUrl(String explorerUrl) {
        this.explorerUrl = explorerUrl;
    }

	public BigDecimal getRechargeMinLimit() {
		if(rechargeMinLimit != null) {
			return rechargeMinLimit;
		}
		return BigDecimal.ZERO;	
		
	}

	public void setRechargeMinLimit(BigDecimal rechargeMinLimit) {
		this.rechargeMinLimit = rechargeMinLimit;
	}
	
	public Boolean getIsUseMemo() {
		return isUseMemo;
	}

	public void setIsUseMemo(Boolean isUseMemo) {
		this.isUseMemo = isUseMemo;
	}

	public void setUseMemo(boolean isUseMemo) {
		this.isUseMemo = isUseMemo;
	}

	public String getCoinName() {
		return coinName;
	}

	public void setCoinName(String coinName) {
		this.coinName = coinName;
	}

	public String getWalletAccount() {
		return walletAccount;
	}

	public void setWalletAccount(String walletAccount) {
		this.walletAccount = walletAccount;
	}

	public boolean getIsPlatformTransferFee() {
		return isPlatformTransferFee;
	}

	public void setIsPlatformTransferFee(boolean isPlatformTransferFee) {
		this.isPlatformTransferFee = isPlatformTransferFee;
	}

	public Boolean getIsInnovateAreaCoin() {
		return isInnovateAreaCoin;
	}

	public void setIsInnovateAreaCoin(Boolean isInnovateAreaCoin) {
		this.isInnovateAreaCoin = isInnovateAreaCoin;
	}

	public BigDecimal getReleaseLockingRatio() {
		return releaseLockingRatio;
	}

	public void setReleaseLockingRatio(BigDecimal releaseLockingRatio) {
		this.releaseLockingRatio = releaseLockingRatio;
	}

	public BigDecimal getDayReleaseRatio() {
		return dayReleaseRatio;
	}

	public void setDayReleaseRatio(BigDecimal dayReleaseRatio) {
		this.dayReleaseRatio = dayReleaseRatio;
	}

	public String getCreateCoinAcountInfo() {
		return createCoinAcountInfo;
	}

	public void setCreateCoinAcountInfo(String createCoinAcountInfo) {
		this.createCoinAcountInfo = createCoinAcountInfo;
	}

	public Boolean getIsSubCoin() {
		return isSubCoin;
	}

	public void setIsSubCoin(Boolean isSubCoin) {
		this.isSubCoin = isSubCoin;
	}

	

	public String getLinkCoin() {
		return linkCoin;
	}

	public void setLinkCoin(String linkCoin) {
		this.linkCoin = linkCoin;
	}

	public void setPlatformTransferFee(boolean isPlatformTransferFee) {
		this.isPlatformTransferFee = isPlatformTransferFee;
	}

	public void setInnovateAreaCoin(boolean isInnovateAreaCoin) {
		this.isInnovateAreaCoin = isInnovateAreaCoin;
	}

	public boolean getUseNewWay() {
		return useNewWay;
	}

	public void setUseNewWay(boolean useNewWay) {
		this.useNewWay = useNewWay;
	}

	public String getWalletUrl() {
		return walletUrl;
	}

	public void setWalletUrl(String walletUrl) {
		this.walletUrl = walletUrl;
	}

	public String getAddressUrl() {
		return addressUrl;
	}

	public void setAddressUrl(String addressUrl) {
		this.addressUrl = addressUrl;
	}

	public void setSubCoin(boolean isSubCoin) {
		this.isSubCoin = isSubCoin;
	}
    
	

	
}