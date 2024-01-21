package hotcoin.model.po;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * ??&sect;??
 *
 * @author hf
 * @date 2019-08-20 07:24:59
 * @since jdk1.8
 */
public class EntrustPo implements Serializable {
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
     *
     */
    private Integer fbuycoinid;
    /**
     *
     */
    private Integer fsellcoinid;
    /**
     *
     */
    private Integer fstatus;
    /**
     *
     */
    private Integer ftype;
    /**
     *
     */
    private Integer fmatchtype;
    /**
     *
     */
    private BigDecimal flast;
    /**
     *
     */
    private BigDecimal flastamount;
    /**
     *
     */
    private Integer flastcount;
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
    private BigDecimal famount;
    /**
     *
     */
    private BigDecimal fsuccessamount;
    /**
     *
     */
    private BigDecimal fleftcount;
    /**
     *
     */
    private BigDecimal ffees;
    /**
     *
     */
    private BigDecimal fleftfees;
    /**
     *
     */
    private Integer fsource;
    /**
     *
     */
    private Long fhuobientrustid;
    /**
     *
     */
    private Integer fhuobiaccountid;
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
    private Integer fagentid;
    /**
     *
     */
    private Long version;
    /**
     *
     */
    private Integer fleverorder;

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
    public void setFbuycoinid(Integer fbuycoinid) {
        this.fbuycoinid = fbuycoinid;
    }

    /**
     *
     */
    public Integer getFbuycoinid() {
        return fbuycoinid;
    }

    /**
     *
     */
    public void setFsellcoinid(Integer fsellcoinid) {
        this.fsellcoinid = fsellcoinid;
    }

    /**
     *
     */
    public Integer getFsellcoinid() {
        return fsellcoinid;
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
    public void setFmatchtype(Integer fmatchtype) {
        this.fmatchtype = fmatchtype;
    }

    /**
     *
     */
    public Integer getFmatchtype() {
        return fmatchtype;
    }

    /**
     *
     */
    public void setFlast(BigDecimal flast) {
        this.flast = flast;
    }

    /**
     *
     */
    public BigDecimal getFlast() {
        return flast;
    }

    /**
     *
     */
    public void setFlastamount(BigDecimal flastamount) {
        this.flastamount = flastamount;
    }

    /**
     *
     */
    public BigDecimal getFlastamount() {
        return flastamount;
    }

    /**
     *
     */
    public void setFlastcount(Integer flastcount) {
        this.flastcount = flastcount;
    }

    /**
     *
     */
    public Integer getFlastcount() {
        return flastcount;
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
    public void setFsuccessamount(BigDecimal fsuccessamount) {
        this.fsuccessamount = fsuccessamount;
    }

    /**
     *
     */
    public BigDecimal getFsuccessamount() {
        return fsuccessamount;
    }

    /**
     *
     */
    public void setFleftcount(BigDecimal fleftcount) {
        this.fleftcount = fleftcount;
    }

    /**
     *
     */
    public BigDecimal getFleftcount() {
        return fleftcount;
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
    public void setFleftfees(BigDecimal fleftfees) {
        this.fleftfees = fleftfees;
    }

    /**
     *
     */
    public BigDecimal getFleftfees() {
        return fleftfees;
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
    public void setFhuobientrustid(Long fhuobientrustid) {
        this.fhuobientrustid = fhuobientrustid;
    }

    /**
     *
     */
    public Long getFhuobientrustid() {
        return fhuobientrustid;
    }

    /**
     *
     */
    public void setFhuobiaccountid(Integer fhuobiaccountid) {
        this.fhuobiaccountid = fhuobiaccountid;
    }

    /**
     *
     */
    public Integer getFhuobiaccountid() {
        return fhuobiaccountid;
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
    public void setVersion(Long version) {
        this.version = version;
    }

    /**
     *
     */
    public Long getVersion() {
        return version;
    }

    /**
     *
     */
    public void setFleverorder(Integer fleverorder) {
        this.fleverorder = fleverorder;
    }

    /**
     *
     */
    public Integer getFleverorder() {
        return fleverorder;
    }

    public EntrustPo(Integer fuid) {
        this.fuid = fuid;
    }
    public EntrustPo() {
    }
}
