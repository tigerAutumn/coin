package com.hotcoin.activity.model.po;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 *
 * @author hf
 * @date 2019-06-28 05:57:15
 * @since jdk1.8
 */
public class UserC2cEntrustPo implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     *
     */
    private Integer id;
    /**
     * &plusmn;&ograve;??ID
     */
    private Integer coinId;
    /**
     *
     */
    private Integer bankId;
    /**
     *
     */
    private Integer businessId;
    /**
     *
     */
    private Integer userId;
    /**
     *
     */
    private Date createTime;
    /**
     *
     */
    private BigDecimal amount;
    /**
     * 单价
     */
    private BigDecimal price;
    /**
     *
     */
    private BigDecimal money;
    /**
     *
     */
    private Integer type;
    /**
     *
     */
    private Integer status;
    /**
     *
     */
    private String remark;
    /**
     * &ograve;?DD??3?
     */
    private String bank;
    /**
     * &ograve;?DD???&sect;
     */
    private String bankAccount;
    /**
     * &ograve;?DD?a?&sect;DD
     */
    private String bankAddress;
    /**
     * &ograve;?DD?&uml;o?
     */
    private String bankCode;
    /**
     * &ecirc;??&uacute;o?
     */
    private String phone;
    /**
     * ?&uuml;D?&ecirc;&plusmn;??
     */
    private Date updateTime;
    /**
     * 2&ugrave;&times;&divide;&egrave;?
     */
    private Integer adminId;
    /**
     * 用户c2c订单表
     */
    private Integer version;
    /**
     * ?&mu;&iacute;3&agrave;&prime;?&prime;
     */
    private Integer platform;
    /**
     * ??&mu;￥&plusmn;&agrave;o?
     */
    private String orderNumber;

    /**
     * ??&mu;￥id
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * ??&mu;￥id
     */
    public Integer getId() {
        return id;
    }

    /**
     * &plusmn;&ograve;??ID
     */
    public void setCoinId(Integer coinId) {
        this.coinId = coinId;
    }

    /**
     * &plusmn;&ograve;??ID
     */
    public Integer getCoinId() {
        return coinId;
    }

    /**
     * &oacute;??&sect;&deg;&oacute;?&uml;&ograve;?DD?&uml;id
     */
    public void setBankId(Integer bankId) {
        this.bankId = bankId;
    }

    /**
     * &oacute;??&sect;&deg;&oacute;?&uml;&ograve;?DD?&uml;id
     */
    public Integer getBankId() {
        return bankId;
    }

    /**
     * &eacute;&igrave;?&sect;id
     */
    public void setBusinessId(Integer businessId) {
        this.businessId = businessId;
    }

    /**
     * &eacute;&igrave;?&sect;id
     */
    public Integer getBusinessId() {
        return businessId;
    }

    /**
     * &oacute;??&sect;id
     */
    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    /**
     * &oacute;??&sect;id
     */
    public Integer getUserId() {
        return userId;
    }

    /**
     * &prime;&prime;?&uml;&ecirc;&plusmn;??
     */
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    /**
     * &prime;&prime;?&uml;&ecirc;&plusmn;??
     */
    public Date getCreateTime() {
        return createTime;
    }

    /**
     * &ecirc;y&aacute;?
     */
    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    /**
     * &ecirc;y&aacute;?
     */
    public BigDecimal getAmount() {
        return amount;
    }

    /**
     * 单价
     */
    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    /**
     * 单价
     */
    public BigDecimal getPrice() {
        return price;
    }

    /**
     * ?e??
     */
    public void setMoney(BigDecimal money) {
        this.money = money;
    }

    /**
     * ?e??
     */
    public BigDecimal getMoney() {
        return money;
    }

    /**
     * &agrave;&agrave;D&iacute;￡o￡&uml;1￡?3??&mu; 2￡?&igrave;&aacute;??￡?
     */
    public void setType(Integer type) {
        this.type = type;
    }

    /**
     * &agrave;&agrave;D&iacute;￡o￡&uml;1￡?3??&mu; 2￡?&igrave;&aacute;??￡?
     */
    public Integer getType() {
        return type;
    }

    /**
     * &times;&prime;&igrave;?￡o￡&uml;￡?
     */
    public void setStatus(Integer status) {
        this.status = status;
    }

    /**
     * &times;&prime;&igrave;?￡o￡&uml;￡?
     */
    public Integer getStatus() {
        return status;
    }

    /**
     * ??&mu;￥&plusmn;?&times;￠
     */
    public void setRemark(String remark) {
        this.remark = remark;
    }

    /**
     * ??&mu;￥&plusmn;?&times;￠
     */
    public String getRemark() {
        return remark;
    }

    /**
     * &ograve;?DD??3?
     */
    public void setBank(String bank) {
        this.bank = bank;
    }

    /**
     * &ograve;?DD??3?
     */
    public String getBank() {
        return bank;
    }

    /**
     * &ograve;?DD???&sect;
     */
    public void setBankAccount(String bankAccount) {
        this.bankAccount = bankAccount;
    }

    /**
     * &ograve;?DD???&sect;
     */
    public String getBankAccount() {
        return bankAccount;
    }

    /**
     * &ograve;?DD?a?&sect;DD
     */
    public void setBankAddress(String bankAddress) {
        this.bankAddress = bankAddress;
    }

    /**
     * &ograve;?DD?a?&sect;DD
     */
    public String getBankAddress() {
        return bankAddress;
    }

    /**
     * &ograve;?DD?&uml;o?
     */
    public void setBankCode(String bankCode) {
        this.bankCode = bankCode;
    }

    /**
     * &ograve;?DD?&uml;o?
     */
    public String getBankCode() {
        return bankCode;
    }

    /**
     * &ecirc;??&uacute;o?
     */
    public void setPhone(String phone) {
        this.phone = phone;
    }

    /**
     * &ecirc;??&uacute;o?
     */
    public String getPhone() {
        return phone;
    }

    /**
     * ?&uuml;D?&ecirc;&plusmn;??
     */
    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    /**
     * ?&uuml;D?&ecirc;&plusmn;??
     */
    public Date getUpdateTime() {
        return updateTime;
    }

    /**
     * 2&ugrave;&times;&divide;&egrave;?
     */
    public void setAdminId(Integer adminId) {
        this.adminId = adminId;
    }

    /**
     * 2&ugrave;&times;&divide;&egrave;?
     */
    public Integer getAdminId() {
        return adminId;
    }

    /**
     * 用户c2c订单表
     */
    public void setVersion(Integer version) {
        this.version = version;
    }

    /**
     * 用户c2c订单表
     */
    public Integer getVersion() {
        return version;
    }

    /**
     * ?&mu;&iacute;3&agrave;&prime;?&prime;
     */
    public void setPlatform(Integer platform) {
        this.platform = platform;
    }

    /**
     * ?&mu;&iacute;3&agrave;&prime;?&prime;
     */
    public Integer getPlatform() {
        return platform;
    }

    /**
     * ??&mu;￥&plusmn;&agrave;o?
     */
    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    /**
     * ??&mu;￥&plusmn;&agrave;o?
     */
    public String getOrderNumber() {
        return orderNumber;
    }
}
