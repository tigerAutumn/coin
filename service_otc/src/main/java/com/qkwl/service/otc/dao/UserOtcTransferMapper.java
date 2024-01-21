package com.qkwl.service.otc.dao;


import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.qkwl.common.dto.otc.OtcUserTransfer;

/**
 * 用户划转记录
 */
@Mapper
public interface UserOtcTransferMapper {

	
	int insert(OtcUserTransfer record);
	
	/**
	 * 查询用户划转记录
	 * @param map 条件MAP
	 * @return 实体对象列表
	 */
	List<OtcUserTransfer> selectByType(Map<String, Object> map);
	
	/**
	 * 根据用户查询用户划转记录数
	 * @param map 条件MAP 
	 * @return 记录条数
	 */
	int countListByUser(Map<String, Object> map);

	/**
	 * 统计otc资金互转
	 * @param uid
	 * @return  
	 **/
    OtcUserTransfer sumOtcTransferBalance(@Param("userId") Integer userId,@Param("type") Integer type,@Param("coinId") Integer coinId);
}
