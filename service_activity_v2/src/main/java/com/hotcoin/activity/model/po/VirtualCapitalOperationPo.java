package com.hotcoin.activity.model.po;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * &egrave;????????&egrave;&mu;?&eacute;?&lsquo;?&mu;???
 *
 * @author hf
 * @date 2019-06-12 03:05:41
 */
public class VirtualCapitalOperationPo implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     *
     */
    private Integer fid;
    /**
     *
     */
    private Integer fuid;
    /**
     *
     */
    private Integer walletCoinId;
    /**
     *
     */
    private BigDecimal famount;
    /**
     *
     */
    private BigDecimal ffees;
    /**
     *
     */
    private Integer ftype;
    /**
     *
     */
    private Integer fstatus;
    /**
     *
     */
    private Date fcreatetime;
    /**
     *
     */
    private Date fupdatetime;
    /**
     *
     */
    private Date txTime;
    /**
     *
     */
    private String fwithdrawaddress;
    /**
     *
     */
    private String frechargeaddress;
    /**
     *
     */
    private String funiquenumber;
    /**
     *
     */
    private Integer fconfirmations;
    /**
     *
     */
    private Boolean fhasowner;
    /**
     *
     */
    private Integer fblocknumber;
    /**
     *
     */
    private BigDecimal fbtcfees;
    /**
     *
     */
    private Integer fadminid;
    /**
     *
     */
    private Integer version;
    /**
     *
     */
    private Integer fagentid;
    /**
     *
     */
    private Integer fsource;
    /**
     *
     */
    private Integer fnonce;
    /**
     *
     */
    private Integer fplatform;
    /**
     *
     */
    private String memo;
    /**
     * 充值成功后是否冻结，1，冻结，0不冻结
     */
    private Integer isFrozen;

    /**
     *
     */
    public void setFid(Integer fid) {
        this.fid = fid;
    }

    /**
     *
     */
    public Integer getFid() {
        return fid;
    }

    /**
     *
     */
    public void setFuid(Integer fuid) {
        this.fuid = fuid;
    }

    /**
     *
     */
    public Integer getFuid() {
        return fuid;
    }

    public Integer getWalletCoinId() {
        return walletCoinId;
    }

    public void setWalletCoinId(Integer walletCoinId) {
        this.walletCoinId = walletCoinId;
    }

    /**
     *
     */
    public void setFamount(BigDecimal famount) {
        this.famount = famount;
    }

    /**
     *
     */
    public BigDecimal getFamount() {
        return famount;
    }

    /**
     *
     */
    public void setFfees(BigDecimal ffees) {
        this.ffees = ffees;
    }

    /**
     *
     */
    public BigDecimal getFfees() {
        return ffees;
    }

    /**
     *
     */
    public void setFtype(Integer ftype) {
        this.ftype = ftype;
    }

    /**
     *
     */
    public Integer getFtype() {
        return ftype;
    }

    /**
     *
     */
    public void setFstatus(Integer fstatus) {
        this.fstatus = fstatus;
    }

    /**
     *
     */
    public Integer getFstatus() {
        return fstatus;
    }

    /**
     *
     */
    public void setFcreatetime(Date fcreatetime) {
        this.fcreatetime = fcreatetime;
    }

    /**
     *
     */
    public Date getFcreatetime() {
        return fcreatetime;
    }

    /**
     *
     */
    public void setFupdatetime(Date fupdatetime) {
        this.fupdatetime = fupdatetime;
    }

    /**
     *
     */
    public Date getFupdatetime() {
        return fupdatetime;
    }

    /**
     *
     */
    public void setTxTime(Date txTime) {
        this.txTime = txTime;
    }

    /**
     *
     */
    public Date getTxTime() {
        return txTime;
    }

    /**
     *
     */
    public void setFwithdrawaddress(String fwithdrawaddress) {
        this.fwithdrawaddress = fwithdrawaddress;
    }

    /**
     *
     */
    public String getFwithdrawaddress() {
        return fwithdrawaddress;
    }

    /**
     *
     */
    public void setFrechargeaddress(String frechargeaddress) {
        this.frechargeaddress = frechargeaddress;
    }

    /**
     *
     */
    public String getFrechargeaddress() {
        return frechargeaddress;
    }

    /**
     *
     */
    public void setFuniquenumber(String funiquenumber) {
        this.funiquenumber = funiquenumber;
    }

    /**
     *
     */
    public String getFuniquenumber() {
        return funiquenumber;
    }

    /**
     *
     */
    public void setFconfirmations(Integer fconfirmations) {
        this.fconfirmations = fconfirmations;
    }

    /**
     *
     */
    public Integer getFconfirmations() {
        return fconfirmations;
    }

    /**
     *
     */
    public void setFhasowner(Boolean fhasowner) {
        this.fhasowner = fhasowner;
    }

    /**
     *
     */
    public Boolean getFhasowner() {
        return fhasowner;
    }

    /**
     *
     */
    public void setFblocknumber(Integer fblocknumber) {
        this.fblocknumber = fblocknumber;
    }

    /**
     *
     */
    public Integer getFblocknumber() {
        return fblocknumber;
    }

    /**
     *
     */
    public void setFbtcfees(BigDecimal fbtcfees) {
        this.fbtcfees = fbtcfees;
    }

    /**
     *
     */
    public BigDecimal getFbtcfees() {
        return fbtcfees;
    }

    /**
     *
     */
    public void setFadminid(Integer fadminid) {
        this.fadminid = fadminid;
    }

    /**
     *
     */
    public Integer getFadminid() {
        return fadminid;
    }

    /**
     *
     */
    public void setVersion(Integer version) {
        this.version = version;
    }

    /**
     *
     */
    public Integer getVersion() {
        return version;
    }

    /**
     *
     */
    public void setFagentid(Integer fagentid) {
        this.fagentid = fagentid;
    }

    /**
     *
     */
    public Integer getFagentid() {
        return fagentid;
    }

    /**
     *
     */
    public void setFsource(Integer fsource) {
        this.fsource = fsource;
    }

    /**
     *
     */
    public Integer getFsource() {
        return fsource;
    }

    /**
     *
     */
    public void setFnonce(Integer fnonce) {
        this.fnonce = fnonce;
    }

    /**
     *
     */
    public Integer getFnonce() {
        return fnonce;
    }

    /**
     *
     */
    public void setFplatform(Integer fplatform) {
        this.fplatform = fplatform;
    }

    /**
     *
     */
    public Integer getFplatform() {
        return fplatform;
    }

    /**
     *
     */
    public void setMemo(String memo) {
        this.memo = memo;
    }

    /**
     *
     */
    public String getMemo() {
        return memo;
    }

    /**
     * 充值成功后是否冻结，1，冻结，0不冻结
     */
    public void setIsFrozen(Integer isFrozen) {
        this.isFrozen = isFrozen;
    }

    /**
     * 充值成功后是否冻结，1，冻结，0不冻结
     */
    public Integer getIsFrozen() {
        return isFrozen;
    }
}
