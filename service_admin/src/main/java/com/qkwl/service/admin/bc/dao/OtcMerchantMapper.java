package com.qkwl.service.admin.bc.dao;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.qkwl.common.dto.otc.OtcMerchant;

@Mapper
public interface OtcMerchantMapper {

	/**
     * otc商户分页查询的总记录数
     * @param map 参数map
     * @return 查询记录数
     */
    int countOtcMerchantListByParam(Map<String, Object> map);
    
    /**
     * otc商户分页查询
     * @param map 参数map
     * @return 用户列表
     */
	List<OtcMerchant> getOtcMerchantPageList(Map<String, Object> map);
	
	/**
	 * 根据主键查询商户
	 * @param id
	 * @return
	 */
	OtcMerchant selectByPrimaryKey(Integer id);
	
	/**
	 * 更新商户表
	 * @param otcMerchant
	 * @return
	 */
	int updateByPrimaryKey(OtcMerchant otcMerchant);
	
	BigDecimal getDeposit(Integer uid);
}
