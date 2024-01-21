package com.hotcoin.activity.model.bo;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * &egrave;????????&egrave;&mu;?&eacute;?&lsquo;?&mu;???
 *
 * @author hf
 * @date 2019-06-12 03:05:41
 */
@Setter
@Getter
public class VirtualCapitalOperationBo implements Serializable {
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
    private Integer fcoinid;
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
}
