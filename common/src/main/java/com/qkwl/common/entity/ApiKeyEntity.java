package com.qkwl.common.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class ApiKeyEntity implements Serializable {
    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column api_key.id
     *
     * @mbg.generated Mon Nov 04 20:39:23 CST 2019
     */
    private Integer id;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column api_key.ip
     *
     * @mbg.generated Mon Nov 04 20:39:23 CST 2019
     */
    private String ip;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column api_key.remark
     *
     * @mbg.generated Mon Nov 04 20:39:23 CST 2019
     */
    private String remark;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column api_key.uid
     *
     * @mbg.generated Mon Nov 04 20:39:23 CST 2019
     */
    private Integer uid;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column api_key.access_key
     *
     * @mbg.generated Mon Nov 04 20:39:23 CST 2019
     */
    private String accessKey;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column api_key.secret_key
     *
     * @mbg.generated Mon Nov 04 20:39:23 CST 2019
     */
    private String secretKey;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column api_key.status
     *
     * @mbg.generated Mon Nov 04 20:39:23 CST 2019
     */
    private Integer status;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column api_key.types
     *
     * @mbg.generated Mon Nov 04 20:39:23 CST 2019
     */
    private String types;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column api_key.rate
     *
     * @mbg.generated Mon Nov 04 20:39:23 CST 2019
     */
    private BigDecimal rate;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column api_key.create_time
     *
     * @mbg.generated Mon Nov 04 20:39:23 CST 2019
     */
    private Date createTime;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column api_key.update_time
     *
     * @mbg.generated Mon Nov 04 20:39:23 CST 2019
     */
    private Date updateTime;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database table api_key
     *
     * @mbg.generated Mon Nov 04 20:39:23 CST 2019
     */
    private static final long serialVersionUID = 1L;

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column api_key.id
     *
     * @return the value of api_key.id
     *
     * @mbg.generated Mon Nov 04 20:39:23 CST 2019
     */
    public Integer getId() {
        return id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column api_key.id
     *
     * @param id the value for api_key.id
     *
     * @mbg.generated Mon Nov 04 20:39:23 CST 2019
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column api_key.ip
     *
     * @return the value of api_key.ip
     *
     * @mbg.generated Mon Nov 04 20:39:23 CST 2019
     */
    public String getIp() {
        return ip;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column api_key.ip
     *
     * @param ip the value for api_key.ip
     *
     * @mbg.generated Mon Nov 04 20:39:23 CST 2019
     */
    public void setIp(String ip) {
        this.ip = ip == null ? null : ip.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column api_key.remark
     *
     * @return the value of api_key.remark
     *
     * @mbg.generated Mon Nov 04 20:39:23 CST 2019
     */
    public String getRemark() {
        return remark;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column api_key.remark
     *
     * @param remark the value for api_key.remark
     *
     * @mbg.generated Mon Nov 04 20:39:23 CST 2019
     */
    public void setRemark(String remark) {
        this.remark = remark == null ? null : remark.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column api_key.uid
     *
     * @return the value of api_key.uid
     *
     * @mbg.generated Mon Nov 04 20:39:23 CST 2019
     */
    public Integer getUid() {
        return uid;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column api_key.uid
     *
     * @param uid the value for api_key.uid
     *
     * @mbg.generated Mon Nov 04 20:39:23 CST 2019
     */
    public void setUid(Integer uid) {
        this.uid = uid;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column api_key.access_key
     *
     * @return the value of api_key.access_key
     *
     * @mbg.generated Mon Nov 04 20:39:23 CST 2019
     */
    public String getAccessKey() {
        return accessKey;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column api_key.access_key
     *
     * @param accessKey the value for api_key.access_key
     *
     * @mbg.generated Mon Nov 04 20:39:23 CST 2019
     */
    public void setAccessKey(String accessKey) {
        this.accessKey = accessKey == null ? null : accessKey.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column api_key.secret_key
     *
     * @return the value of api_key.secret_key
     *
     * @mbg.generated Mon Nov 04 20:39:23 CST 2019
     */
    public String getSecretKey() {
        return secretKey;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column api_key.secret_key
     *
     * @param secretKey the value for api_key.secret_key
     *
     * @mbg.generated Mon Nov 04 20:39:23 CST 2019
     */
    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey == null ? null : secretKey.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column api_key.status
     *
     * @return the value of api_key.status
     *
     * @mbg.generated Mon Nov 04 20:39:23 CST 2019
     */
    public Integer getStatus() {
        return status;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column api_key.status
     *
     * @param status the value for api_key.status
     *
     * @mbg.generated Mon Nov 04 20:39:23 CST 2019
     */
    public void setStatus(Integer status) {
        this.status = status;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column api_key.types
     *
     * @return the value of api_key.types
     *
     * @mbg.generated Mon Nov 04 20:39:23 CST 2019
     */
    public String getTypes() {
        return types;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column api_key.types
     *
     * @param types the value for api_key.types
     *
     * @mbg.generated Mon Nov 04 20:39:23 CST 2019
     */
    public void setTypes(String types) {
        this.types = types == null ? null : types.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column api_key.rate
     *
     * @return the value of api_key.rate
     *
     * @mbg.generated Mon Nov 04 20:39:23 CST 2019
     */
    public BigDecimal getRate() {
        return rate;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column api_key.rate
     *
     * @param rate the value for api_key.rate
     *
     * @mbg.generated Mon Nov 04 20:39:23 CST 2019
     */
    public void setRate(BigDecimal rate) {
        this.rate = rate;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column api_key.create_time
     *
     * @return the value of api_key.create_time
     *
     * @mbg.generated Mon Nov 04 20:39:23 CST 2019
     */
    public Date getCreateTime() {
        return createTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column api_key.create_time
     *
     * @param createTime the value for api_key.create_time
     *
     * @mbg.generated Mon Nov 04 20:39:23 CST 2019
     */
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column api_key.update_time
     *
     * @return the value of api_key.update_time
     *
     * @mbg.generated Mon Nov 04 20:39:23 CST 2019
     */
    public Date getUpdateTime() {
        return updateTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column api_key.update_time
     *
     * @param updateTime the value for api_key.update_time
     *
     * @mbg.generated Mon Nov 04 20:39:23 CST 2019
     */
    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table api_key
     *
     * @mbg.generated Mon Nov 04 20:39:23 CST 2019
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", ip=").append(ip);
        sb.append(", remark=").append(remark);
        sb.append(", uid=").append(uid);
        sb.append(", accessKey=").append(accessKey);
        sb.append(", secretKey=").append(secretKey);
        sb.append(", status=").append(status);
        sb.append(", types=").append(types);
        sb.append(", rate=").append(rate);
        sb.append(", createTime=").append(createTime);
        sb.append(", updateTime=").append(updateTime);
        sb.append("]");
        return sb.toString();
    }
}