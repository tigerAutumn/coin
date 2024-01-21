package com.qkwl.initMarket.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;

public class InitMarket implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	// 主键ID
	private BigInteger fid;
	// 用户ID
	private Integer fuid;
	// 交易id
	private Integer ftradeid;
	// 买方币id
	private Integer fbuycoinid;
	// 卖方币id(虚拟币)
	private Integer fsellcoinid;
	// 委单类型
	private Integer ftype;
	// 下单价格
	private BigDecimal fprice;
	// 下单数量
	private BigDecimal fcount;
	// 处理状态
	private Integer fstatus;
	
	public BigInteger getFid() {
		return fid;
	}
	public void setFid(BigInteger fid) {
		this.fid = fid;
	}
	public Integer getFuid() {
		return fuid;
	}
	public void setFuid(Integer fuid) {
		this.fuid = fuid;
	}
	public Integer getFtradeid() {
		return ftradeid;
	}
	public void setFtradeid(Integer ftradeid) {
		this.ftradeid = ftradeid;
	}
	public Integer getFbuycoinid() {
		return fbuycoinid;
	}
	public void setFbuycoinid(Integer fbuycoinid) {
		this.fbuycoinid = fbuycoinid;
	}
	public Integer getFsellcoinid() {
		return fsellcoinid;
	}
	public void setFsellcoinid(Integer fsellcoinid) {
		this.fsellcoinid = fsellcoinid;
	}
	public Integer getFtype() {
		return ftype;
	}
	public void setFtype(Integer ftype) {
		this.ftype = ftype;
	}
	public BigDecimal getFprice() {
		return fprice;
	}
	public void setFprize(BigDecimal fprice) {
		this.fprice = fprice;
	}
	public BigDecimal getFcount() {
		return fcount;
	}
	public void setFcount(BigDecimal fcount) {
		this.fcount = fcount;
	}
	public Integer getFstatus() {
		return fstatus;
	}
	public void setFstatus(Integer fstatus) {
		this.fstatus = fstatus;
	}
}
