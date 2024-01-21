/**
 * 
 */
package com.qkwl.common.dto;

import java.io.Serializable;
import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * @author huangjinfeng
 */
@JsonInclude(Include.NON_NULL)
@ApiModel(value = "TradeInfoResp", description = "币对交易信息")
public class TradeInfoResp implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@ApiModelProperty(value = "交易对id")
	private Integer tradeId;
	@ApiModelProperty(value = "交易对id")
	private Integer id;
	@ApiModelProperty(value = "buySymbol")
	private String buySymbol;
	@ApiModelProperty(value = "sellSymbol")
	private String sellSymbol;
	@ApiModelProperty(value = "image")
	private String image;
	@ApiModelProperty(value = "sellShortName")
	private String sellShortName;
	@ApiModelProperty(value = "buyShortName")
	private String buyShortName;
	@ApiModelProperty(value = "实时成交价")
	private BigDecimal p_new;
	@ApiModelProperty(value = "开盘价")
	private BigDecimal p_open;
	@ApiModelProperty(value = "24小时成交量")
	private BigDecimal  total;
	@ApiModelProperty(value = "24小时成交额")
	private BigDecimal  totalAmount;
	@ApiModelProperty(value = "买一价")
	private BigDecimal  buy;
	@ApiModelProperty(value = "卖一价")
	private BigDecimal sell;
	@ApiModelProperty(value = "24小时涨跌幅")
	private BigDecimal rose;
	@ApiModelProperty(value = "折合人民币")
	private BigDecimal cny;
	@ApiModelProperty(value = "精度")
	private String digit;
	@ApiModelProperty(value = "周涨跌幅")
	private BigDecimal weekChg;
	@ApiModelProperty(value = "交易区")
	private Integer type;
	@ApiModelProperty(value = "是否已开盘" )
	private String isOpen;
	
	public Integer getType() {
		return type;
	}
	public void setType(Integer type) {
		this.type = type;
	}
	public Integer getTradeId() {
		return tradeId;
	}
	public void setTradeId(Integer tradeId) {
		this.tradeId = tradeId;
	}
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getBuySymbol() {
		return buySymbol;
	}
	public void setBuySymbol(String buySymbol) {
		this.buySymbol = buySymbol;
	}
	public String getSellSymbol() {
		return sellSymbol;
	}
	public void setSellSymbol(String sellSymbol) {
		this.sellSymbol = sellSymbol;
	}
	public String getImage() {
		return image;
	}
	public void setImage(String image) {
		this.image = image;
	}
	public String getSellShortName() {
		return sellShortName;
	}
	public void setSellShortName(String sellShortName) {
		this.sellShortName = sellShortName;
	}
	public String getBuyShortName() {
		return buyShortName;
	}
	public void setBuyShortName(String buyShortName) {
		this.buyShortName = buyShortName;
	}
	public String getDigit() {
		return digit;
	}
	public void setDigit(String digit) {
		this.digit = digit;
	}
	public BigDecimal getP_new() {
		return p_new;
	}
	public void setP_new(BigDecimal p_new) {
		this.p_new = p_new;
	}
	public BigDecimal getP_open() {
		return p_open;
	}
	public void setP_open(BigDecimal p_open) {
		this.p_open = p_open;
	}
	public BigDecimal getTotal() {
		return total;
	}
	public void setTotal(BigDecimal total) {
		this.total = total;
	}
	public BigDecimal getTotalAmount() {
		return totalAmount;
	}
	public void setTotalAmount(BigDecimal totalAmount) {
		this.totalAmount = totalAmount;
	}
	public BigDecimal getBuy() {
		return buy;
	}
	public void setBuy(BigDecimal buy) {
		this.buy = buy;
	}
	public BigDecimal getSell() {
		return sell;
	}
	public void setSell(BigDecimal sell) {
		this.sell = sell;
	}
	public BigDecimal getRose() {
		return rose;
	}
	public void setRose(BigDecimal rose) {
		this.rose = rose;
	}
	public BigDecimal getCny() {
		return cny;
	}
	public void setCny(BigDecimal cny) {
		this.cny = cny;
	}
	public BigDecimal getWeekChg() {
		return weekChg;
	}
	public void setWeekChg(BigDecimal weekChg) {
		this.weekChg = weekChg;
	}

	public String getIsOpen() {
		return isOpen;
	}

	public void setIsOpen(String isOpen) {
		this.isOpen = isOpen;
	}
}
