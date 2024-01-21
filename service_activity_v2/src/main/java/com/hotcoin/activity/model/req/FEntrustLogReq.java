package com.hotcoin.activity.model.req;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * ?ˉ???D&mu;??????&eacute;????????????
 *
 * @author hf
 * @date 2019-06-12 03:01:58
 */
@Setter
@Getter
public class FEntrustLogReq implements Serializable {
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
}
