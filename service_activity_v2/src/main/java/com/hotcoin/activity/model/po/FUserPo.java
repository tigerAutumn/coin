package com.hotcoin.activity.model.po;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

/**
 * @ProjectName: service_activity_v2
 * @Package: com.hotcoin.activity.model.po
 * @ClassName: FUserPo
 * @Author: hf
 * @Description:
 * @Date: 2019/6/12 11:26
 * @Version: 1.0
 */
@Getter
@Setter
public class FUserPo implements Serializable {

    private static final long serialVersionUID = 1L;
    private int fid;
    private Date fregistertime;
    /**
     * 0表示未实名,1表示实名认证通过
     */
    private Integer fhasrealvalidate;
}
