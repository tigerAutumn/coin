package com.hotcoin.activity.model.req;

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
public class VirtualCapitalOperationReq implements Serializable {
    private static final long serialVersionUID = 1L;
    private Integer fuid;

    private Integer fcoinid;

    private BigDecimal famount;

    private Integer ftype;

    private Integer fstatus;

    private Date fcreatetime;
}
