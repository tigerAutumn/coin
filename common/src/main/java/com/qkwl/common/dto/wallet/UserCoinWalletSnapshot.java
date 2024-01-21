package com.qkwl.common.dto.wallet;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class UserCoinWalletSnapshot implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	// 主键ID
	private Integer id;
	//钱包ID
	private Integer walletId;
	// 用户ID
    private Integer uid;
    // 币种ID
    private Integer coinId;
    // 可用
    private BigDecimal total;
    // 冻结
    private BigDecimal frozen;
    // 理财
    private BigDecimal borrow;
    // ico
    private BigDecimal ico;
    // 创建时间
    private Date gmtCreate;
    // 更新时间
    private Date gmtModified;
    //空投活动id
    private Integer airdropId;
    //快照时间
    private Date snapshotTime;
    
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Integer getWalletId() {
		return walletId;
	}
	public void setWalletId(Integer walletId) {
		this.walletId = walletId;
	}
	public Integer getUid() {
		return uid;
	}
	public void setUid(Integer uid) {
		this.uid = uid;
	}
	public Integer getCoinId() {
		return coinId;
	}
	public void setCoinId(Integer coinId) {
		this.coinId = coinId;
	}
	public BigDecimal getTotal() {
		return total;
	}
	public void setTotal(BigDecimal total) {
		this.total = total;
	}
	public BigDecimal getFrozen() {
		return frozen;
	}
	public void setFrozen(BigDecimal frozen) {
		this.frozen = frozen;
	}
	public BigDecimal getBorrow() {
		return borrow;
	}
	public void setBorrow(BigDecimal borrow) {
		this.borrow = borrow;
	}
	public BigDecimal getIco() {
		return ico;
	}
	public void setIco(BigDecimal ico) {
		this.ico = ico;
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
	public Integer getAirdropId() {
		return airdropId;
	}
	public void setAirdropId(Integer airdropId) {
		this.airdropId = airdropId;
	}
	public Date getSnapshotTime() {
		return snapshotTime;
	}
	public void setSnapshotTime(Date snapshotTime) {
		this.snapshotTime = snapshotTime;
	}
    
}
