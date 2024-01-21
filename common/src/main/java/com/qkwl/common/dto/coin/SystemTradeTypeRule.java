package com.qkwl.common.dto.coin;

import java.io.Serializable;

public class SystemTradeTypeRule implements Serializable {
    private static final long serialVersionUID = 1L;
    private Integer id;
    private Integer tradeid;
    private String rectype;
    private String ruleid;
    private String exp;
    private Integer status;
    private Integer resultcode;
    
    private String rectypeName;
    private String ruleidName;
    
    private String buyCoinName;
    private String sellCoinName;
    
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Integer getTradeid() {
		return tradeid;
	}
	public void setTradeid(Integer tradeid) {
		this.tradeid = tradeid;
	}
	public String getRectype() {
		return rectype;
	}
	public void setRectype(String rectype) {
		this.rectype = rectype;
	}
	public String getRuleid() {
		return ruleid;
	}
	public void setRuleid(String ruleid) {
		this.ruleid = ruleid;
	}
	public String getExp() {
		return exp;
	}
	public void setExp(String exp) {
		this.exp = exp;
	}
	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
	}
	public Integer getResultcode() {
		return resultcode;
	}
	public void setResultcode(Integer resultcode) {
		this.resultcode = resultcode;
	}
	public String getRectypeName() {
		return rectypeName;
	}
	public void setRectypeName(String rectypeName) {
		this.rectypeName = rectypeName;
	}
	public String getRuleidName() {
		return ruleidName;
	}
	public void setRuleidName(String ruleidName) {
		this.ruleidName = ruleidName;
	}
	public String getBuyCoinName() {
		return buyCoinName;
	}
	public void setBuyCoinName(String buyCoinName) {
		this.buyCoinName = buyCoinName;
	}
	public String getSellCoinName() {
		return sellCoinName;
	}
	public void setSellCoinName(String sellCoinName) {
		this.sellCoinName = sellCoinName;
	}

}
