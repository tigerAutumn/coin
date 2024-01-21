package com.hotcoin.activity.model.param;

import java.util.Date;

/**
 * @ProjectName: service_activity_v2
 * @Package: com.hotcoin.activity.model.param
 * @ClassName: FUserDto
 * @Author: hf
 * @Description: 用于用户信息查询
 * @Date: 2019/6/12 14:11
 * @Version: 1.0
 */
public class FUserDto {
    private Integer fid;
    private Date fregistertime;
    /**
     * 0表示未实名,1表示实名认证通过
     */
    private Integer fhasrealvalidate;
    /**
     * 开始时间
     */
    private Date startTime;
    /**
     * 结束时间
     */
    private Date endTime;

    public FUserDto(Integer fhasrealvalidate, Date startTime, Date endTime) {
        this.fhasrealvalidate = fhasrealvalidate;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public FUserDto(Integer fid, Integer fhasrealvalidate) {
        this.fid = fid;
        this.fhasrealvalidate = fhasrealvalidate;
    }

    public FUserDto( ) {
    }
}
