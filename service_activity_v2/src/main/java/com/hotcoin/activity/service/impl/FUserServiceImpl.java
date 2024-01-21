package com.hotcoin.activity.service.impl;

import com.hotcoin.activity.dao.FUserPoDao;
import com.hotcoin.activity.model.param.FUserDto;
import com.hotcoin.activity.model.po.FUserPo;
import com.hotcoin.activity.service.FUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * @ProjectName: service_activity_v2
 * @Package: com.hotcoin.activity.service.impl
 * @ClassName: FUserServiceImpl
 * @Author: hf
 * @Description:
 * @Date: 2019/6/12 11:51
 * @Version: 1.0
 */
@Service
public class FUserServiceImpl implements FUserService {
    @Autowired
    private FUserPoDao fUserPoDao;

    @Override
    public List<FUserPo> getUserListByParam(Date startTime, Date endTime) {
        return fUserPoDao.getUserListByParam(startTime, endTime);
    }

    @Override
    public List<FUserPo> getUserListByDtoParam(FUserDto fUserDto) {
        return fUserPoDao.getUserByDtoParam(fUserDto);
    }

}
