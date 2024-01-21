package com.qkwl.common.dto.wallet;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import java.util.List;

/**
 * 用户钱包
 *
 * @author LY
 */
public class UserCoinWallet implements Serializable {

    private static final long serialVersionUID = 1L;
    // 主键ID
    private Integer id;
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
    // 创新区锁仓
    private BigDecimal depositFrozen;
    // 创新区锁仓统计
    private BigDecimal depositFrozenTotal;
    // 创建时间
    private Date gmtCreate;
    // 更新时间
    private Date gmtModified;

    private BigInteger version;

    // WEB站LOGO
    private String webLogo;

    //是否使用地址标签
    private Boolean isUseMemo;

    // 扩展字段
    private String loginName;
    private String nickName;
    private String realName;
    private String coinName;
    private String shortName;
    //是否可以提现
    private Boolean withdraw;
    //是否可以充币
    private Boolean recharge;

    /**
     * borrowFrozen 为borrow和Frozen字段计算后获得的结果值
     */
    private BigDecimal borrowFrozen;
    
    // 币种折合价格
    private BigDecimal price;
    
    
    private List<Object> SubCoinList;
    
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public BigInteger getVersion() {
        return version;
    }

    public void setVersion(BigInteger version) {
        this.version = version;
    }

    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public String getCoinName() {
        return coinName;
    }

    public void setCoinName(String coinName) {
        this.coinName = coinName;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }


    public BigDecimal getIco() {
        return ico;
    }

    public void setIco(BigDecimal ico) {
        this.ico = ico;
    }

    public String getWebLogo() {
        return webLogo;
    }

    public void setWebLogo(String webLogo) {
        this.webLogo = webLogo;
    }

    public Boolean getIsUseMemo() {
        if (isUseMemo == null) {
            return false;
        }
        return isUseMemo;
    }

    public void setIsUseMemo(Boolean isUseMemo) {
        this.isUseMemo = isUseMemo;
    }

    public BigDecimal getDepositFrozen() {
        return depositFrozen;
    }

    public void setDepositFrozen(BigDecimal depositFrozen) {
        this.depositFrozen = depositFrozen;
    }

    public BigDecimal getDepositFrozenTotal() {
        return depositFrozenTotal;
    }

    public void setDepositFrozenTotal(BigDecimal depositFrozenTotal) {
        this.depositFrozenTotal = depositFrozenTotal;
    }

    public Boolean getWithdraw() {
        return withdraw;
    }

    public void setWithdraw(Boolean withdraw) {
        this.withdraw = withdraw;
    }

    public Boolean getRecharge() {
        return recharge;
    }

    public void setRecharge(Boolean recharge) {
        this.recharge = recharge;
    }

    public BigDecimal getBorrowFrozen() {
        return borrowFrozen;
    }

    public void setBorrowFrozen(BigDecimal borrowFrozen) {
        this.borrowFrozen = borrowFrozen;
    }

	public BigDecimal getPrice() {
		return price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}

	public List<Object> getSubCoinList() {
		return SubCoinList;
	}

	public void setSubCoinList(List<Object> subCoinList) {
		SubCoinList = subCoinList;
	}

	@Override
	public String toString() {
		return "UserCoinWallet [id=" + id + ", uid=" + uid + ", coinId=" + coinId + ", total=" + total + ", frozen="
				+ frozen + ", borrow=" + borrow + ", ico=" + ico + ", depositFrozen=" + depositFrozen
				+ ", depositFrozenTotal=" + depositFrozenTotal + ", gmtCreate=" + gmtCreate + ", gmtModified="
				+ gmtModified + ", version=" + version + "]";
	}
	
	
}