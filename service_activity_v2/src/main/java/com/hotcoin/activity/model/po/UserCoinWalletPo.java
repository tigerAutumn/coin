package com.hotcoin.activity.model.po;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author hf
 * @date 2019-07-01 06:43:56
 * @since jdk1.8
 */
public class UserCoinWalletPo implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     *
     */
    private Integer id;
    /**
     *
     */
    private Integer uid;
    /**
     *
     */
    private Integer coinId;
    /**
     *
     */
    private BigDecimal total;
    /**
     *
     */
    private BigDecimal frozen;
    /**
     *
     */
    private BigDecimal borrow;
    /**
     *
     */
    private BigDecimal ico;
    /**
     *
     */
    private Date gmtCreate;
    /**
     *
     */
    private Date gmtModified;
    /**
     *
     */
    private Long version;
    /**
     * 创新区充值冻结值
     */
    private BigDecimal depositFrozen;
    /**
     * 创新区充值累计值
     */
    private BigDecimal depositFrozenTotal;

    /**
     *
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     *
     */
    public Integer getId() {
        return id;
    }

    /**
     *
     */
    public void setUid(Integer uid) {
        this.uid = uid;
    }

    /**
     *
     */
    public Integer getUid() {
        return uid;
    }

    /**
     *
     */
    public void setCoinId(Integer coinId) {
        this.coinId = coinId;
    }

    /**
     *
     */
    public Integer getCoinId() {
        return coinId;
    }

    /**
     *
     */
    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    /**
     *
     */
    public BigDecimal getTotal() {
        return total;
    }

    /**
     *
     */
    public void setFrozen(BigDecimal frozen) {
        this.frozen = frozen;
    }

    /**
     *
     */
    public BigDecimal getFrozen() {
        return frozen;
    }

    /**
     *
     */
    public void setBorrow(BigDecimal borrow) {
        this.borrow = borrow;
    }

    /**
     *
     */
    public BigDecimal getBorrow() {
        return borrow;
    }

    /**
     *
     */
    public void setIco(BigDecimal ico) {
        this.ico = ico;
    }

    /**
     *
     */
    public BigDecimal getIco() {
        return ico;
    }

    /**
     *
     */
    public void setGmtCreate(Date gmtCreate) {
        this.gmtCreate = gmtCreate;
    }

    /**
     *
     */
    public Date getGmtCreate() {
        return gmtCreate;
    }

    /**
     *
     */
    public void setGmtModified(Date gmtModified) {
        this.gmtModified = gmtModified;
    }

    /**
     *
     */
    public Date getGmtModified() {
        return gmtModified;
    }

    /**
     *
     */
    public void setVersion(Long version) {
        this.version = version;
    }

    /**
     *
     */
    public Long getVersion() {
        return version;
    }

    /**
     * 创新区充值冻结值
     */
    public void setDepositFrozen(BigDecimal depositFrozen) {
        this.depositFrozen = depositFrozen;
    }

    /**
     * 创新区充值冻结值
     */
    public BigDecimal getDepositFrozen() {
        return depositFrozen;
    }

    /**
     * 创新区充值累计值
     */
    public void setDepositFrozenTotal(BigDecimal depositFrozenTotal) {
        this.depositFrozenTotal = depositFrozenTotal;
    }

    /**
     * 创新区充值累计值
     */
    public BigDecimal getDepositFrozenTotal() {
        return depositFrozenTotal;
    }

    public UserCoinWalletPo(Integer userId, Integer coinId) {
        this.uid = userId;
        this.coinId = coinId;
    }

    public UserCoinWalletPo() {
    }
}
