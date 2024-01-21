package com.hotcoin.activity.service;

import com.hotcoin.activity.model.param.FUserDto;
import com.hotcoin.activity.model.po.FUserPo;

import java.util.Date;
import java.util.List;

/**
 * @ProjectName: service_activity_v2
 * @Package: com.hotcoin.activity.service
 * @ClassName: FUserService
 * @Author: hf
 * @Description:
 * @Date: 2019/6/12 11:20
 * @Version: 1.0
 */
public interface FUserService {
    List<FUserPo> getUserListByParam(Date startTime, Date endTime);

    List<FUserPo> getUserListByDtoParam(FUserDto fUserDto);
}
