package com.qkwl.service.capital.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface OtcAdvertMapper {
	
	/**
     * 查询广告条数
     * @param 用户id
     * @param 支付方式id
     * @param 状态
     * @return 查询记录数
     */
    Integer countAdvert(@Param("userId") Integer userId ,@Param("paymentId") Integer paymentId , @Param("status") Integer status);
}
