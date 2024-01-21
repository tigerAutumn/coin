package com.hotcoin.activity.model.resp;

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
public class FEntrustLogResp implements Serializable {
    private static final long serialVersionUID = 1L;
    private Integer fid;
    private Integer fuid;
    private Integer fentrustid;
    /**
     *
     */
    private Integer fmatchid;
    private BigDecimal famount;

    private BigDecimal fprize;

    private BigDecimal fcount;

    private Integer ftradeid;

    private Date fcreatetime;

    /**
     * 0买单
     * 1 卖单
     * 2 撤单
     */
    private Integer fentrusttype;
}
