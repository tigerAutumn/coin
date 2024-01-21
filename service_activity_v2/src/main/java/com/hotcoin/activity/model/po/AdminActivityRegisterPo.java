package com.hotcoin.activity.model.po;

import com.hotcoin.activity.model.bo.AdminActivityRegisterBo;
import com.hotcoin.activity.util.DateUtil;
import org.springframework.util.StringUtils;

import java.io.Serializable;
import java.util.Date;

/**
 * @author hf
 * @date 2019-06-10 09:20:43
 */
public class AdminActivityRegisterPo implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    private Integer id;
    /**
     * 空投币种
     */
    private String airDropCoin;
    /**
     * 空投数量
     */
    private String airDropAmount;
    /**
     * 空投总额度
     */
    private String airDropTotal;
    /**
     * 活动开始时间
     */
    private Date startTime;
    /**
     * 结束时间
     */
    private Date endTime;
    /**
     * 状态:-1:禁用,0:结束1:开始
     */
    private Integer status;
    /**
     * 更新时间
     */
    private Date updateTime;
    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * id
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * id
     */
    public Integer getId() {
        return id;
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
     * 空投总额度
     */
    public void setAirDropTotal(String airDropTotal) {
        this.airDropTotal = airDropTotal;
    }

    /**
     * 空投总额度
     */
    public String getAirDropTotal() {
        return airDropTotal;
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
     * 结束时间
     */
    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    /**
     * 结束时间
     */
    public Date getEndTime() {
        return endTime;
    }

    /**
     * 状态:-1:禁用,0:结束1:开始
     */
    public void setStatus(Integer status) {
        this.status = status;
    }

    /**
     * 状态:-1:禁用,0:结束1:开始
     */
    public Integer getStatus() {
        return status;
    }

    /**
     * 更新时间
     */
    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    /**
     * 更新时间
     */
    public Date getUpdateTime() {
        return updateTime;
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

    public AdminActivityRegisterPo() {
    }

    /**
     * (新增时)构造器
     *
     * @param ab
     */
    public AdminActivityRegisterPo(AdminActivityRegisterBo ab, Date date) {
        this.startTime = DateUtil.strToDate(ab.getStartTime());
        this.endTime = DateUtil.strToDate(ab.getEndTime());
        this.airDropCoin = ab.getAirDropCoin();
        this.airDropAmount = ab.getAirDropAmount();
        this.airDropTotal = ab.getAirDropTotal();
        this.status = 1;
        this.createTime = date;
        this.updateTime = date;
    }

    /**
     * (修改时)构造器
     *
     * @param ab
     */
    public AdminActivityRegisterPo(AdminActivityRegisterBo ab) {
        this.id = ab.getId();
        if (!StringUtils.isEmpty(ab.getStartTime())) {
            this.startTime = DateUtil.strToDate(ab.getStartTime());
        }
        if (!StringUtils.isEmpty(ab.getEndTime())) {
            this.endTime = DateUtil.strToDate(ab.getEndTime());
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
        if (ab.getStatus() != null) {
            this.status = ab.getStatus();
        }
        this.updateTime = new Date();
    }

    /**
     * 查询已激活状态构造器
     */
    public AdminActivityRegisterPo(Integer status) {
        this.status = status;
    }
}
