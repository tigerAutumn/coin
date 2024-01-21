package com.qkwl.common.dto.activity_v2.bo;

import java.util.Date;

/**
 * @author hf
 * @date 2019-06-13 02:29:35
 */
public class AirdropActivityDetailV2Bo {
    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    private Integer id;
    /**
     * 1:注册空投活动,2:充值空投活动3:交易空投活动
     */
    private Integer activityType;
    /**
     * amount etc
     */
    private String typeDetail;
    /**
     * 用户id
     */
    private Integer userId;
    /**
     * 空投币种
     */
    private String airDropCoin;
    /**
     * 活动币种
     */
    private String activityCoin;
    /**
     * 空投数量
     */
    private Double airDropTotal;
    /**
     * 空投时间
     */
    private Date airDropTime;
    /**
     * 发放状态:-1:发放失败,0:待发放,1发放成功
     */
    private Integer status;
    /**
     * 更新时间
     */
    private Date updateTime;
    /**
     * 生成该条记录时间
     */
    private Date createTime;
    /**
     * 空投规则
     */
    private Double rule;
    /**
     * 当前页
     */
    private int currentPage;
    /**
     * 每页条数
     */
    private int pageSize;
    /**
     * 总页数
     */
    private int total;

    private int offSet;

    public int getOffSet() {
        return offSet;
    }

    public void setOffSet(int offSet) {
        this.offSet = offSet;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getActivityType() {
        return activityType;
    }

    public void setActivityType(Integer activityType) {
        this.activityType = activityType;
    }

    public String getTypeDetail() {
        return typeDetail;
    }

    public void setTypeDetail(String typeDetail) {
        this.typeDetail = typeDetail;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getAirDropCoin() {
        return airDropCoin;
    }

    public void setAirDropCoin(String airDropCoin) {
        this.airDropCoin = airDropCoin;
    }

    public String getActivityCoin() {
        return activityCoin;
    }

    public void setActivityCoin(String activityCoin) {
        this.activityCoin = activityCoin;
    }

    public Double getAirDropTotal() {
        return airDropTotal;
    }

    public void setAirDropTotal(Double airDropTotal) {
        this.airDropTotal = airDropTotal;
    }

    public Date getAirDropTime() {
        return airDropTime;
    }

    public void setAirDropTime(Date airDropTime) {
        this.airDropTime = airDropTime;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Double getRule() {
        return rule;
    }

    public void setRule(Double rule) {
        this.rule = rule;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public AirdropActivityDetailV2Bo(Integer activityType, String activityCoin) {
        this.activityCoin = activityCoin;
        this.activityType = activityType;
    }

    public AirdropActivityDetailV2Bo() {
    }
}
