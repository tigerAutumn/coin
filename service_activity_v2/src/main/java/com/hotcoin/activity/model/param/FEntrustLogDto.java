package com.hotcoin.activity.model.param;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @ProjectName: service_activity_v2
 * @Package: com.hotcoin.activity.model.param
 * @ClassName: FEntrustLogDto
 * @Author: hf
 * @Description:
 * @Date: 2019/6/28 17:01
 * @Version: 1.0
 */
@Setter
@Getter
public class FEntrustLogDto implements Serializable {
    private static final long serialVersionUID = 1L;
    private Integer fid;
    private Integer fuid;

    private BigDecimal famount;

    private BigDecimal fprize;

    private BigDecimal fcount;

    private Integer ftradeid;

    private Date fcreatetime;

    private Date startTime;

    private Date endTime;
    private Integer fentrusttype;

    public FEntrustLogDto(Date startTime, Date endTime) {
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public FEntrustLogDto() {
    }
}
