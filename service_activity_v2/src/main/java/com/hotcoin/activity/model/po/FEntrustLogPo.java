package com.hotcoin.activity.model.po;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * ?ˉ???D&mu;??????&eacute;????????????
 *
 * @author hf
 * @date 2019-06-12 03:01:58
 */
public class FEntrustLogPo implements Serializable {
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
    private Integer ftradeid;

    /**
     * 0买单
     * 1 卖单
     * 2 撤单
     */
    private Integer fentrusttype;
    /**
     *
     */
    private Integer fentrustid;
    /**
     *
     */
    private Integer fmatchid;
    /**
     *
     */
    private BigDecimal famount;
    /**
     *
     */
    private BigDecimal fprize;
    /**
     *
     */
    private BigDecimal fcount;
    /**
     *
     */
    private Boolean fisactive;
    /**
     *
     */
    private Date flastupdattime;
    /**
     *
     */
    private Date fcreatetime;
    /**
     *
     */
    private String fremark;
    /**
     *
     */
    private Integer version;
    /**
     *
     */
    private BigDecimal ffee;

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

    /**
     *
     */
    public void setFtradeid(Integer ftradeid) {
        this.ftradeid = ftradeid;
    }

    /**
     *
     */
    public Integer getFtradeid() {
        return ftradeid;
    }

    /**
     *
     */
    public void setFentrusttype(Integer fentrusttype) {
        this.fentrusttype = fentrusttype;
    }

    /**
     *
     */
    public Integer getFentrusttype() {
        return fentrusttype;
    }

    /**
     *
     */
    public void setFentrustid(Integer fentrustid) {
        this.fentrustid = fentrustid;
    }

    /**
     *
     */
    public Integer getFentrustid() {
        return fentrustid;
    }

    /**
     *
     */
    public void setFmatchid(Integer fmatchid) {
        this.fmatchid = fmatchid;
    }

    /**
     *
     */
    public Integer getFmatchid() {
        return fmatchid;
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
    public void setFprize(BigDecimal fprize) {
        this.fprize = fprize;
    }

    /**
     *
     */
    public BigDecimal getFprize() {
        return fprize;
    }

    /**
     *
     */
    public void setFcount(BigDecimal fcount) {
        this.fcount = fcount;
    }

    /**
     *
     */
    public BigDecimal getFcount() {
        return fcount;
    }

    /**
     *
     */
    public void setFisactive(Boolean fisactive) {
        this.fisactive = fisactive;
    }

    /**
     *
     */
    public Boolean getFisactive() {
        return fisactive;
    }

    /**
     *
     */
    public void setFlastupdattime(Date flastupdattime) {
        this.flastupdattime = flastupdattime;
    }

    /**
     *
     */
    public Date getFlastupdattime() {
        return flastupdattime;
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
    public void setFremark(String fremark) {
        this.fremark = fremark;
    }

    /**
     *
     */
    public String getFremark() {
        return fremark;
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
    public void setFfee(BigDecimal ffee) {
        this.ffee = ffee;
    }

    /**
     *
     */
    public BigDecimal getFfee() {
        return ffee;
    }
}
