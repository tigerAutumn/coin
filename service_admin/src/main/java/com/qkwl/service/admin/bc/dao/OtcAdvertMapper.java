package com.qkwl.service.admin.bc.dao;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.qkwl.common.dto.capital.FUserBankinfoDTO;
import com.qkwl.common.dto.otc.OtcAdvert;

@Mapper
public interface OtcAdvertMapper {

	/**
     * otc广告分页查询的总记录数
     * @param map 参数map
     * @return 查询记录数
     */
    int countOtcAdvertListByParam(Map<String, Object> map);
    
    /**
     * otc广告分页查询
     * @param map 参数map
     * @return 用户列表
     */
	List<OtcAdvert> getOtcAdvertPageList(Map<String, Object> map);
	
	/**
	 * otc查询广告支付信息
	 * @param id
	 * @return
	 */
	List<FUserBankinfoDTO> getBankinfoList(Integer id);
	
	/**
	 * 根据id查询广告
	 * @param id
	 * @return
	 */
	OtcAdvert selectAdvertById(Integer id);
	
	/**
	 * 更新广告
	 * @param advert
	 */
	void updateAdvert(OtcAdvert advert);
	
	/**
	 * 更新广告
	 * @param advert
	 */
	int updateOtcAdvert(OtcAdvert advert);
	
	/**
	 * 根据id查询广告带锁
	 * @param id
	 * @return
	 */
	OtcAdvert selectAdvertByIdLock(Integer id);
	
	/**
	 * 根据uid查询广告
	 * @param uid
	 * @return
	 */
	List<OtcAdvert> selectAdvertByUid(Integer uid);
	
	/**
	 * 查询订单所有冻结数量
	 * @param advertId
	 * @return
	 */
	BigDecimal getOrderVolume(Integer advertId);
	
	/**
	 * 上架中的广告金额
	 */
	BigDecimal getAdvertFrozen(@Param("userId") Integer userId, @Param("coinId") Integer coinId);
	
	/**
	 * 订单已消耗
	 */
	BigDecimal getOrderConsumption(@Param("userId") Integer userId, @Param("coinId") Integer coinId);
	
	/**
	 * 进行中的订单金额
	 */
	BigDecimal getOrderFrozen(@Param("userId")Integer userId, @Param("coinId") Integer coinId);
}
