package com.hotcoin.coin.entity;

import java.math.BigDecimal;
import java.math.BigInteger;

public class CoinInfoVo {
    private Integer id;

    private String coinName;

    private String shortName;

    private Integer isMainnet;

    private Integer mainnetId;

    private String rpcIp;

    private String rpcPort;

    private String walletRpcIp;

    private String walletRpcPort;

    private String rpcUser;

    private String rpcPassword;

    private String managePassword;

    private String contractAddress;

    private String hotAddress;

    private String hotAddressPassword;

    private BigDecimal hotAddressKeep;

    private String coldAddress;

    private String feeAddress;

    private String feeAddressPassword;

    private Integer depositConfirm;

    private Integer withdrawConfirm;

    private BigInteger blockNumber;

    private Integer depositStatus;

    private Integer withdrawStatus;

    private Integer isMemo;

    private Integer decimals;

    private BigDecimal feeRateMin;

    private BigDecimal feeRateMax;

    private BigDecimal collectionLimit;

    private BigDecimal collectionKeep;

    private Integer isHotMode;

    private String explorerUrl;

    private String createTime;

    private String updateTime;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCoinName() {
        return coinName;
    }

    public void setCoinName(String coinName) {
        this.coinName = coinName;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public Integer getIsMainnet() {
        return isMainnet;
    }

    public void setIsMainnet(Integer isMainnet) {
        this.isMainnet = isMainnet;
    }

    public Integer getMainnetId() {
        return mainnetId;
    }

    public void setMainnetId(Integer mainnetId) {
        this.mainnetId = mainnetId;
    }

    public String getRpcIp() {
        return rpcIp;
    }

    public void setRpcIp(String rpcIp) {
        this.rpcIp = rpcIp;
    }

    public String getRpcPort() {
        return rpcPort;
    }

    public void setRpcPort(String rpcPort) {
        this.rpcPort = rpcPort;
    }

    public String getWalletRpcIp() {
        return walletRpcIp;
    }

    public void setWalletRpcIp(String walletRpcIp) {
        this.walletRpcIp = walletRpcIp;
    }

    public String getWalletRpcPort() {
        return walletRpcPort;
    }

    public void setWalletRpcPort(String walletRpcPort) {
        this.walletRpcPort = walletRpcPort;
    }

    public String getRpcUser() {
        return rpcUser;
    }

    public void setRpcUser(String rpcUser) {
        this.rpcUser = rpcUser;
    }

    public String getRpcPassword() {
        return rpcPassword;
    }

    public void setRpcPassword(String rpcPassword) {
        this.rpcPassword = rpcPassword;
    }

    public String getManagePassword() {
        return managePassword;
    }

    public void setManagePassword(String managePassword) {
        this.managePassword = managePassword;
    }

    public String getContractAddress() {
        return contractAddress;
    }

    public void setContractAddress(String contractAddress) {
        this.contractAddress = contractAddress;
    }

    public String getHotAddress() {
        return hotAddress;
    }

    public void setHotAddress(String hotAddress) {
        this.hotAddress = hotAddress;
    }

    public String getColdAddress() {
        return coldAddress;
    }

    public void setColdAddress(String coldAddress) {
        this.coldAddress = coldAddress;
    }

    public String getFeeAddress() {
        return feeAddress;
    }

    public void setFeeAddress(String feeAddress) {
        this.feeAddress = feeAddress;
    }

    public String getFeeAddressPassword() {
        return feeAddressPassword;
    }

    public void setFeeAddressPassword(String feeAddressPassword) {
        this.feeAddressPassword = feeAddressPassword;
    }

    public String getHotAddressPassword() {
        return hotAddressPassword;
    }

    public void setHotAddressPassword(String hotAddressPassword) {
        this.hotAddressPassword = hotAddressPassword;
    }

    public BigDecimal getHotAddressKeep() {
        return hotAddressKeep;
    }

    public void setHotAddressKeep(BigDecimal hotAddressKeep) {
        this.hotAddressKeep = hotAddressKeep;
    }

    public Integer getDepositConfirm() {
        return depositConfirm;
    }

    public void setDepositConfirm(Integer depositConfirm) {
        this.depositConfirm = depositConfirm;
    }

    public Integer getWithdrawConfirm() {
        return withdrawConfirm;
    }

    public void setWithdrawConfirm(Integer withdrawConfirm) {
        this.withdrawConfirm = withdrawConfirm;
    }

    public BigInteger getBlockNumber() {
        return blockNumber;
    }

    public void setBlockNumber(BigInteger blockNumber) {
        this.blockNumber = blockNumber;
    }

    public Integer getDepositStatus() {
        return depositStatus;
    }

    public void setDepositStatus(Integer depositStatus) {
        this.depositStatus = depositStatus;
    }

    public Integer getWithdrawStatus() {
        return withdrawStatus;
    }

    public void setWithdrawStatus(Integer withdrawStatus) {
        this.withdrawStatus = withdrawStatus;
    }

    public Integer getIsMemo() {
        return isMemo;
    }

    public void setIsMemo(Integer isMemo) {
        this.isMemo = isMemo;
    }

    public Integer getDecimals() {
        return decimals;
    }

    public void setDecimals(Integer decimals) {
        this.decimals = decimals;
    }

    public BigDecimal getFeeRateMin() {
        return feeRateMin;
    }

    public void setFeeRateMin(BigDecimal feeRateMin) {
        this.feeRateMin = feeRateMin;
    }

    public BigDecimal getFeeRateMax() {
        return feeRateMax;
    }

    public void setFeeRateMax(BigDecimal feeRateMax) {
        this.feeRateMax = feeRateMax;
    }

    public BigDecimal getCollectionLimit() {
        return collectionLimit;
    }

    public void setCollectionLimit(BigDecimal collectionLimit) {
        this.collectionLimit = collectionLimit;
    }

    public BigDecimal getCollectionKeep() {
        return collectionKeep;
    }

    public void setCollectionKeep(BigDecimal collectionKeep) {
        this.collectionKeep = collectionKeep;
    }

    public Integer getIsHotMode() {
        return isHotMode;
    }

    public void setIsHotMode(Integer isHotMode) {
        this.isHotMode = isHotMode;
    }

    public String getExplorerUrl() {
        return explorerUrl;
    }

    public void setExplorerUrl(String explorerUrl) {
        this.explorerUrl = explorerUrl == null ? null : explorerUrl.trim();
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    @Override
    public String toString() {
        return "CoinInfoVo{" +
                "id=" + id +
                ", coinName='" + coinName + '\'' +
                ", shortName='" + shortName + '\'' +
                ", isMainnet=" + isMainnet +
                ", mainnetId=" + mainnetId +
                ", rpcIp='" + rpcIp + '\'' +
                ", rpcPort='" + rpcPort + '\'' +
                ", walletRpcIp='" + walletRpcIp + '\'' +
                ", walletRpcPort='" + walletRpcPort + '\'' +
                ", rpcUser='" + rpcUser + '\'' +
                ", rpcPassword='" + rpcPassword + '\'' +
                ", managePassword='" + managePassword + '\'' +
                ", contractAddress='" + contractAddress + '\'' +
                ", hotAddress='" + hotAddress + '\'' +
                ", hotAddressPassword='" + hotAddressPassword + '\'' +
                ", hotAddressKeep=" + hotAddressKeep +
                ", coldAddress='" + coldAddress + '\'' +
                ", feeAddress='" + feeAddress + '\'' +
                ", feeAddressPassword='" + feeAddressPassword + '\'' +
                ", depositConfirm=" + depositConfirm +
                ", withdrawConfirm=" + withdrawConfirm +
                ", blockNumber=" + blockNumber +
                ", depositStatus=" + depositStatus +
                ", withdrawStatus=" + withdrawStatus +
                ", isMemo=" + isMemo +
                ", decimals=" + decimals +
                ", feeRateMin=" + feeRateMin +
                ", feeRateMax=" + feeRateMax +
                ", collectionLimit=" + collectionLimit +
                ", collectionKeep=" + collectionKeep +
                ", isHotMode=" + isHotMode +
                ", explorerUrl='" + explorerUrl + '\'' +
                ", createTime='" + createTime + '\'' +
                ", updateTime='" + updateTime + '\'' +
                '}';
    }
}