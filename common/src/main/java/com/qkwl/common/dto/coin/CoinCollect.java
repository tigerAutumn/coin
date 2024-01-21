package com.qkwl.common.dto.coin;

import java.math.BigDecimal;
import java.util.Date;

public class CoinCollect {
    private Integer id;

    private Integer coinId;

    private String address;

    private String secret;

    private Integer status;

    private Date createtime;

    private Date updatetime;

    private Boolean isRechargeFuelCoin;

    private BigDecimal rechargeFuelCoin;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getCoinId() {
        return coinId;
    }

    public void setCoinId(Integer coinId) {
        this.coinId = coinId;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address == null ? null : address.trim();
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret == null ? null : secret.trim();
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Date getCreatetime() {
        return createtime;
    }

    public void setCreatetime(Date createtime) {
        this.createtime = createtime;
    }

    public Date getUpdatetime() {
        return updatetime;
    }

    public void setUpdatetime(Date updatetime) {
        this.updatetime = updatetime;
    }

    public Boolean getIsRechargeFuelCoin() {
    	if(isRechargeFuelCoin == null) {
    		return false;
    	}
        return isRechargeFuelCoin;
    }

    public void setIsRechargeFuelCoin(Boolean isRechargeFuelCoin) {
        this.isRechargeFuelCoin = isRechargeFuelCoin;
    }

    public BigDecimal getRechargeFuelCoin() {
        return rechargeFuelCoin;
    }

    public void setRechargeFuelCoin(BigDecimal rechargeFuelCoin) {
        this.rechargeFuelCoin = rechargeFuelCoin;
    }
}