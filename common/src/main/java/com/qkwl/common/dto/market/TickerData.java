package com.qkwl.common.dto.market;

import com.alibaba.fastjson.annotation.JSONField;
import java.io.Serializable;
import java.math.BigDecimal;
/**
 * 行情
 * @author TT
 */
public class TickerData implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	//买一价
	private BigDecimal buy;

	//卖一价
	private BigDecimal sell;

	//24小时最高价
	private BigDecimal high;

	//24小时最低价
	private BigDecimal low;

	// 实时成交价
	private BigDecimal last;

	// 开盘价
	private BigDecimal kai;

	// 24小时成交量
	private BigDecimal vol;

	// 24小时涨跌幅
	private BigDecimal chg;

	/**
	 * 交易对id
	 */
	@JSONField(serialize=false)
	private Integer tradeId;

	/**
	 * 交易成交时间毫秒数
	 */
	private Long dateTime;

	public TickerData() {

	}

	public TickerData(Integer tradeId, BigDecimal price, BigDecimal vol, BigDecimal chg, Long dateTime) {
		this.tradeId = tradeId;
		this.buy = price;
		this.sell = price;
		this.high=price;
		this.kai=price;
		this.low=price;
		this.last = price;
		this.vol=vol;
		this.chg=chg;
		this.dateTime = dateTime;
	}

	public TickerData(Integer tradeId, BigDecimal buy, BigDecimal sell, BigDecimal kai, BigDecimal price, BigDecimal vol, BigDecimal chg, Long dateTime) {
		this.buy = buy;
		this.sell = sell;
		this.high = price;
		this.low = price;
		this.last = price;
		this.kai = kai;
		this.vol = vol;
		//BigDecimal chgTmp = MathUtils.div(MathUtils.sub(last, kai), kai);
		//this.chg=MathUtils.mul(chgTmp, new BigDecimal("100"));
		this.chg = chg;
		this.tradeId = tradeId;
		this.dateTime = dateTime;
	}

	public TickerData(Integer tradeId, BigDecimal buy, BigDecimal sell, BigDecimal high, BigDecimal low, BigDecimal last, BigDecimal vol, BigDecimal chg,Long dateTime) {
		this.tradeId = tradeId;
		this.buy = buy;
		this.sell = sell;
		this.high = high;
		this.low = low;
		this.last = last;
		this.vol = vol;
		this.chg = chg;
		this.dateTime = dateTime;
	}


	@Deprecated
	public TickerData(BigDecimal buy, BigDecimal sell, BigDecimal high, BigDecimal low, BigDecimal last, BigDecimal vol, BigDecimal chg) {
		this.buy = buy;
		this.sell = sell;
		this.high = high;
		this.low = low;
		this.last = last;
		this.vol = vol;
		this.chg = chg;
	}

	public Integer getTradeId() {
		return tradeId;
	}

	public void setTradeId(Integer tradeId) {
		this.tradeId = tradeId;
	}

	public Long getDateTime() {
		return dateTime;
	}

	public void setDateTime(Long dateTime) {
		this.dateTime = dateTime;
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

	public BigDecimal getHigh() {
		return high;
	}

	public void setHigh(BigDecimal high) {
		this.high = high;
	}

	public BigDecimal getLow() {
		return low;
	}

	public void setLow(BigDecimal low) {
		this.low = low;
	}

	public BigDecimal getLast() {
		return last;
	}

	public void setLast(BigDecimal last) {
		this.last = last;
	}

	public BigDecimal getVol() {
		return vol;
	}

	public void setVol(BigDecimal vol) {
		this.vol = vol;
	}

	public BigDecimal getChg() {
		return chg;
	}

	public void setChg(BigDecimal chg) {
		this.chg = chg;
	}

	public BigDecimal getKai() {
		return kai;
	}

	public void setKai(BigDecimal kai) {
		this.kai = kai;
	}
}
