package com.qkwl.service.otc.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

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
    
    //获取用户的未完成订单
    int countOrder(@Param("userId")Integer userId);
    
    /**
	 * 根据
	 * @param advertId
	 * @return
	 */
	Integer countSuccBuyOrder(@Param("userId")Integer userId);
	
	/**
	 * 根据
	 * @param advertId
	 * @return
	 */
	Integer countSuccSellOrder(@Param("userId")Integer userId);
	
	/**
	 * 查询广告的30内订单完成率
	 * @param advertId
	 * @return
	 */
	String getCompletionRate(Integer userId);
	
	/**
	 * 查询广告的30内订单完成率
	 * @param advertId
	 * @return
	 */
	Integer getCmpOrders(Integer userId);
}