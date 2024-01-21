package com.hotcoin.activity.model.po;

import com.hotcoin.activity.model.bo.AdminActivityRechargeBo;
import com.hotcoin.activity.util.DateUtil;
import org.springframework.util.StringUtils;

import java.io.Serializable;
import java.util.Date;

/**
 * @author hf
 * @date 2019-06-10 09:20:42
 */
public class AdminActivityRechargePo implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    private Integer id;
    /**
     * 充值币种
     */
    private String rechargeCoin;
    /**
     * 空投币种
     */
    private String airDropCoin;
    /**
     * 空投数量
     */
    private String airDropAmount;
    /**
     * 空投总数
     */
    private String airDropTotal;
    /**
     * 空投类型:法币充值(2),币币充值(3)
     */
    private String airDropType;
    /**
     * 空投规则
     */
    private Double airDropRule;
    /**
     * 活动开始时间
     */
    private Date startTime;
    /**
     * 活动结束时间
     */
    private Date endTime;
    /**
     * 活动状态::-1:禁用,0:结束1:开始
     */
    private Integer status;
    /**
     * 创建时间
     */
    private Date createTime;
    /**
     * 修改时间
     */
    private Date updateTime;

    /**
     * 主键
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * 主键
     */
    public Integer getId() {
        return id;
    }

    /**
     * 充值币种
     */
    public void setRechargeCoin(String rechargeCoin) {
        this.rechargeCoin = rechargeCoin;
    }

    /**
     * 充值币种
     */
    public String getRechargeCoin() {
        return rechargeCoin;
    }

    /**
     * 空投币种
     */
    public void setAirDropCoin(String airDropCoin) {
        this.airDropCoin = airDropCoin;
    }

    /**
     * 空投币种
     */
    public String getAirDropCoin() {
        return airDropCoin;
    }

    /**
     * 空投数量
     */
    public void setAirDropAmount(String airDropAmount) {
        this.airDropAmount = airDropAmount;
    }

    /**
     * 空投数量
     */
    public String getAirDropAmount() {
        return airDropAmount;
    }

    /**
     * 空投总数
     */
    public void setAirDropTotal(String airDropTotal) {
        this.airDropTotal = airDropTotal;
    }

    /**
     * 空投总数
     */
    public String getAirDropTotal() {
        return airDropTotal;
    }

    /**
     * 空投类型:交易量(amount),交易额(volume)
     */
    public void setAirDropType(String airDropType) {
        this.airDropType = airDropType;
    }

    /**
     * 空投类型:交易量(amount),交易额(volume)
     */
    public String getAirDropType() {
        return airDropType;
    }

    public Double getAirDropRule() {
        return airDropRule;
    }

    public void setAirDropRule(Double airDropRule) {
        this.airDropRule = airDropRule;
    }

    /**
     * 活动开始时间
     */
    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    /**
     * 活动开始时间
     */
    public Date getStartTime() {
        return startTime;
    }

    /**
     * 活动结束时间
     */
    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    /**
     * 活动结束时间
     */
    public Date getEndTime() {
        return endTime;
    }

    /**
     * 活动状态::-1:禁用,0:结束1:开始
     */
    public void setStatus(Integer status) {
        this.status = status;
    }

    /**
     * 活动状态::-1:禁用,0:结束1:开始
     */
    public Integer getStatus() {
        return status;
    }

    /**
     * 创建时间
     */
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    /**
     * 创建时间
     */
    public Date getCreateTime() {
        return createTime;
    }

    /**
     * 修改时间
     */
    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    /**
     * 修改时间
     */
    public Date getUpdateTime() {
        return updateTime;
    }

    public AdminActivityRechargePo() {

    }

    /**
     * (新增时)构造器
     *
     * @param ab
     */
    public AdminActivityRechargePo(AdminActivityRechargeBo ab, Date date) {
        this.startTime = DateUtil.strToDate(ab.getStartTime());
        this.endTime = DateUtil.strToDate(ab.getEndTime());
        this.rechargeCoin = ab.getRechargeCoin();
        this.airDropCoin = ab.getAirDropCoin();
        this.airDropAmount = ab.getAirDropAmount();
        this.airDropTotal = ab.getAirDropTotal();
        this.airDropType = ab.getAirDropType();
        this.airDropRule = ab.getAirDropRule();
        this.status = 1;
        this.createTime = date;
        this.updateTime = date;
    }

    /**
     * (修改时)构造器
     *
     * @param ab
     */
    public AdminActivityRechargePo(AdminActivityRechargeBo ab) {
        this.id = ab.getId();
        if (!StringUtils.isEmpty(ab.getStartTime())) {
            this.startTime = DateUtil.strToDate(ab.getStartTime());
        }
        if (!StringUtils.isEmpty(ab.getEndTime())) {
            this.endTime = DateUtil.strToDate(ab.getEndTime());
        }
        if (!StringUtils.isEmpty(ab.getRechargeCoin())) {
            this.rechargeCoin = ab.getRechargeCoin();
        }
        if (!StringUtils.isEmpty(ab.getAirDropCoin())) {
            this.airDropCoin = ab.getAirDropCoin();
        }
        if (!StringUtils.isEmpty(ab.getAirDropAmount())) {
            this.airDropAmount = ab.getAirDropAmount();
        }
        if (!StringUtils.isEmpty(ab.getAirDropTotal())) {
            this.airDropTotal = ab.getAirDropTotal();
        }
        if (!StringUtils.isEmpty(ab.getAirDropType())) {
            this.airDropType = ab.getAirDropType();
        }
        if (!StringUtils.isEmpty(ab.getAirDropRule())) {
            this.airDropRule = ab.getAirDropRule();
        }
        if (ab.getStatus() != null) {
            this.status = ab.getStatus();
        }
        this.updateTime = new Date();
    }

    /**
     * 活动状态::-1:禁用,0:结束1:开始
     * @param status
     */
    public AdminActivityRechargePo(int status) {
        this.status = status;
    }
}
