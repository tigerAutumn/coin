package com.hotcoin.activity.model.param;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @ProjectName: service_activity_v2
 * @Package: com.hotcoin.activity.model.param
 * @ClassName: VirtualCapitalOperationDto
 * @Author: hf
 * @Description:
 * @Date: 2019/6/28 16:15
 * @Version: 1.0
 */
@Getter
@Setter
public class VirtualCapitalOperationDto implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer fuid;

    private Integer walletCoinId;

    private BigDecimal famount;

    private Integer ftype;

    private Integer fstatus;

    private Date fcreatetime;
    private Date startTime;
    private Date endTime;

    /**
     * ftype =1 表示充值and fstatus=3 表示充值成功
     */
    public VirtualCapitalOperationDto(Integer ftype, Integer fstatus, Date startTime, Date endTime) {
        this.ftype = ftype;
        this.fstatus = fstatus;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public VirtualCapitalOperationDto() {
    }
}
