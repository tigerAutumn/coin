package com.qkwl.service.admin.bc.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.qkwl.common.dto.otc.OtcOrder;

@Mapper
public interface OtcOrderMapper {
    int insertSelective(OtcOrder record);

    OtcOrder selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(OtcOrder record);
    
    List<OtcOrder> listOrder(Map<String, Object> param);
    
    //查询订单带锁
    OtcOrder selectByPrimaryKeyLock(Long id);
    
    //当前时间大于limitTime的
    List<OtcOrder> expiredOrderList();
}