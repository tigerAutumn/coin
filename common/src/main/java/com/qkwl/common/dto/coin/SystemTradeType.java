package com.qkwl.common.dto.coin;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.Setting;

import com.qkwl.common.dto.Enum.SystemTradeTypeEnum;

/**
 * 交易类型表
 */
@Document(indexName = "system_trade_type_index", type = "systemTradeType")
@Setting(settingPath = "elasticsearch/my-analyser.json")
public class SystemTradeType implements Serializable {
    private static final long serialVersionUID = 1L;
    // 主键ID
    @Id
    private Integer id;
    // 排序ID

    private Integer sortId;
    // 交易类型
    private Integer type;
    // 状态
    private Integer status;
    // 买方id
    private Integer buyCoinId;
    @Field(type = FieldType.Keyword, normalizer = "lower_case_normalizer")
    private String buySymbol;
    @Field(type = FieldType.Keyword, normalizer = "lower_case_normalizer")
    private String buyName;
    @Field(type = FieldType.Keyword, normalizer = "lower_case_normalizer")
    private String buyShortName;
    private String buyWebLogo;
    private String buyAppLogo;
    // 卖方id
    private Integer sellCoinId;
    @Field(type = FieldType.Keyword, normalizer = "lower_case_normalizer")
    private String sellSymbol;
    @Field(type = FieldType.Keyword, normalizer = "lower_case_normalizer")
    private String sellName;
    @Field(type = FieldType.Keyword, normalizer = "lower_case_normalizer")
    private String sellShortName;
    private String sellWebLogo;
    private String sellAppLogo;
    // 是否交易
    private Boolean isShare;
    // 是否停盘
    private Boolean isStop;
    // 开盘时间
    private Date openTime;
    // 停盘时间
    private Date stopTime;
    // 买手续费
    private BigDecimal buyFee;
    // 卖手续费
    private BigDecimal sellFee;
    // 特殊交易币种id
    private Integer remoteId;
    // 价格涨跌停波动
    private BigDecimal priceWave;
    // 价格涨跌幅波动
    private BigDecimal priceRange;
    // 最小下单
    private BigDecimal minCount;
    // 最大下单
    private BigDecimal maxCount;
    // 最小价格
    private BigDecimal minPrice;
    // 最大价格
    private BigDecimal maxPrice;
    // 数量偏移量
    private String amountOffset;
    // 价格偏移量
    private String priceOffset;
    // 小数位
    private String digit;
    // 开盘价
    private BigDecimal openPrice;
    // 卷商ID
    private Integer agentId;
    // 创建时间
    private Date gmtCreate;
    // 更新时间
    private Date gmtModified;
    // 版本号
    private Integer version;

    // 扩展字段
    private String typeName;
    // 上市时间
    private Date listedDateTime;
    
    
    //2019-03-12添加，IEO售卖
    //是否开启ioe售卖，true为开启
    private Boolean isOpenIoe;
    //ioe金额限额
    private BigDecimal ioeAmountLimit;
    //ioe售卖白名单,用户id，使用“#”号分割
    private String ioeWhitelist;
    

    //2019-03-18 杠杆功能
    //是否开启杠杆交易
    private boolean isOpenLever = false;

    private String isOpen;


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

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getBuyCoinId() {
        return buyCoinId;
    }

    public void setBuyCoinId(Integer buyCoinId) {
        this.buyCoinId = buyCoinId;
    }

    public String getBuySymbol() {
        return buySymbol;
    }

    public void setBuySymbol(String buySymbol) {
        this.buySymbol = buySymbol;
    }

    public String getBuyShortName() {
        return buyShortName;
    }

    public void setBuyShortName(String buyShortName) {
        this.buyShortName = buyShortName;
    }

    public String getBuyWebLogo() {
        return buyWebLogo;
    }

    public void setBuyWebLogo(String buyWebLogo) {
        this.buyWebLogo = buyWebLogo;
    }

    public String getBuyAppLogo() {
        return buyAppLogo;
    }

    public void setBuyAppLogo(String buyAppLogo) {
        this.buyAppLogo = buyAppLogo;
    }

    public Integer getSellCoinId() {
        return sellCoinId;
    }

    public void setSellCoinId(Integer sellCoinId) {
        this.sellCoinId = sellCoinId;
    }

    public String getSellSymbol() {
        return sellSymbol;
    }

    public void setSellSymbol(String sellSymbol) {
        this.sellSymbol = sellSymbol;
    }

    public String getSellShortName() {
        return sellShortName;
    }

    public void setSellShortName(String sellShortName) {
        this.sellShortName = sellShortName;
    }

    public String getSellWebLogo() {
        return sellWebLogo;
    }

    public void setSellWebLogo(String sellWebLogo) {
        this.sellWebLogo = sellWebLogo;
    }

    public String getSellAppLogo() {
        return sellAppLogo;
    }

    public void setSellAppLogo(String sellAppLogo) {
        this.sellAppLogo = sellAppLogo;
    }

    public Boolean getIsShare() {
        return isShare;
    }

    public void setIsShare(Boolean share) {
        isShare = share;
    }

    public Boolean getIsStop() {
        return isStop;
    }

    public void setIsStop(Boolean stop) {
        isStop = stop;
    }

    public Date getOpenTime() {
        return openTime;
    }

    public void setOpenTime(Date openTime) {
        this.openTime = openTime;
    }

    public Date getStopTime() {
        return stopTime;
    }

    public void setStopTime(Date stopTime) {
        this.stopTime = stopTime;
    }

    public BigDecimal getBuyFee() {
        return buyFee;
    }

    public void setBuyFee(BigDecimal buyFee) {
        this.buyFee = buyFee;
    }

    public BigDecimal getSellFee() {
        return sellFee;
    }

    public void setSellFee(BigDecimal sellFee) {
        this.sellFee = sellFee;
    }

    public Integer getRemoteId() {
        return remoteId;
    }

    public void setRemoteId(Integer remoteId) {
        this.remoteId = remoteId;
    }

    public BigDecimal getPriceWave() {
        return priceWave;
    }

    public void setPriceWave(BigDecimal priceWave) {
        this.priceWave = priceWave;
    }

    public BigDecimal getPriceRange() {
        return priceRange;
    }

    public void setPriceRange(BigDecimal priceRange) {
        this.priceRange = priceRange;
    }

    public BigDecimal getMinCount() {
        return minCount;
    }

    public void setMinCount(BigDecimal minCount) {
        this.minCount = minCount;
    }

    public BigDecimal getMaxCount() {
        return maxCount;
    }

    public void setMaxCount(BigDecimal maxCount) {
        this.maxCount = maxCount;
    }

    public BigDecimal getMinPrice() {
        return minPrice;
    }

    public void setMinPrice(BigDecimal minPrice) {
        this.minPrice = minPrice;
    }

    public BigDecimal getMaxPrice() {
        return maxPrice;
    }

    public void setMaxPrice(BigDecimal maxPrice) {
        this.maxPrice = maxPrice;
    }

    public String getAmountOffset() {
        return amountOffset;
    }

    public void setAmountOffset(String amountOffset) {
        this.amountOffset = amountOffset;
    }

    public String getPriceOffset() {
        return priceOffset;
    }

    public void setPriceOffset(String priceOffset) {
        this.priceOffset = priceOffset;
    }

    public String getDigit() {
        return digit;
    }

    public void setDigit(String digit) {
        this.digit = digit;
    }

    public BigDecimal getOpenPrice() {
        return openPrice;
    }

    public void setOpenPrice(BigDecimal openPrice) {
        this.openPrice = openPrice;
    }

    public Integer getAgentId() {
        return agentId;
    }

    public void setAgentId(Integer agentId) {
        this.agentId = agentId;
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


    public String getBuyName() {
        return buyName;
    }

    public void setBuyName(String buyName) {
        this.buyName = buyName;
    }

    public String getSellName() {
        return sellName;
    }

    public void setSellName(String sellName) {
        this.sellName = sellName;
    }

    public String getTypeName() {
        return SystemTradeTypeEnum.getValueByCode(this.type);
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

	public Boolean getIsOpenIoe() {
		return isOpenIoe;
	}

	public void setIsOpenIoe(Boolean isOpenIoe) {
		this.isOpenIoe = isOpenIoe;
	}

	public BigDecimal getIoeAmountLimit() {
		return ioeAmountLimit;
	}

	public void setIoeAmountLimit(BigDecimal ioeAmountLimit) {
		this.ioeAmountLimit = ioeAmountLimit;
	}

	public String getIoeWhitelist() {
		return ioeWhitelist;
	}

	public void setIoeWhitelist(String ioeWhitelist) {
		this.ioeWhitelist = ioeWhitelist;
	}
    public Date getListedDateTime() {
        return listedDateTime;
    }

    public void setListedDateTime(Date listedDateTime) {
        this.listedDateTime = listedDateTime;
    }

    public boolean isOpenLever() {
        return isOpenLever;
    }

    public void setOpenLever(boolean openLever) {
        isOpenLever = openLever;
    }

    public String getIsOpen() {
        return isOpen;
    }

    public void setIsOpen(String isOpen) {
        this.isOpen = isOpen;
    }
}