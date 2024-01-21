package hotcoin.model.resp;

import lombok.Getter;
import lombok.Setter;

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
@Setter
@Getter
public class EntrustResp implements Serializable {
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
}
