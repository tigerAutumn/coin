package com.qkwl.service.otc.dao;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.qkwl.common.dto.otc.OtcAdvert;
import com.qkwl.common.dto.otc.OtcAdvertPrice;
import com.qkwl.common.dto.otc.OtcPayment;

@Mapper
public interface OtcAdvertMapper {
	
	/**
     * 查询的总广告
     * @param map 参数map
     * @return 查询记录数
     */
    int countAdvertListByParam(Map<String, Object> map);
	
	/**
	 * 分页查询广告
	 * @param map
	 * @return
	 */
	List<OtcAdvert> getAdvertPageList(Map<String, Object> map);
	
	/**
	 * 查询过期的广告
	 * @return
	 */
	List<OtcAdvert> getOverdueAds();
	
	/**
	 * 查询固定广告
	 * @return
	 */
	List<OtcAdvert> getFixedAds();
	
	/**
	 * 查询浮动广告
	 * @return
	 */
	List<OtcAdvert> getFloatAds(Map<String, Object> map);
	
	/**
	 * 分页查询广告
	 * @param map
	 * @return
	 */
	List<OtcAdvert> selectMerchantAdvert(Map<String, Object> map);
	
	/**
	 * 分页查询广告
	 * @param map
	 * @return
	 */
	List<OtcAdvert> selectOtherMerchantAdvert(Map<String, Object> map);
	
	/**
	 * 查询固定广告的最低价和最高价
	 * @return
	 */
	OtcAdvertPrice selectAdvertPrice(Map<String, Object> map);
	
	/**
	 * 查询所有默认的支付方式
	 * @param uid
	 * @return
	 */
	List<OtcPayment> getAllPaymentList(Integer uid);
	
	/**
     * 查询的总广告
     * @param map 参数map
     * @return 查询记录数
     */
    int countMyAdvertByParam(Map<String, Object> map);
	
	/**
	 * 分页查询广告
	 * @param map
	 * @return
	 */
	List<OtcAdvert> getMyAdvert(Map<String, Object> map);
	
	/**
	 * 根据id查询广告
	 * @param id
	 * @return
	 */
	OtcAdvert selectAdvertById(Integer id);
	
	/**
	 * 生成广告
	 * @param advert
	 */
	int insertOtcAdvert(OtcAdvert advert);
	
	/**
	 * 更新广告
	 * @param advert
	 */
	int updateOtcAdvert(OtcAdvert advert);
	
	/**
	 * 根据id查询广告带行级锁
	 * @param id
	 * @return
	 */
	OtcAdvert selectAdvertByIdLock(Integer id);
	
	/**
	 * 根据coinId获取币种名称
	 * @param coinId
	 * @return
	 */
	String getCoinName(Integer coinId);
	
	/**
	 * 根据currencyId获取法币名称
	 * @param currencyId
	 * @return
	 */
	String getCurrencyName(Integer currencyId);
	
	/**
	 * 查询广告正在处理中的订单
	 * @param advertId
	 * @return
	 */
	Integer getProcessingOrders(Integer advertId);
	
	/**
	 * 查询订单所有冻结数量
	 * @param advertId
	 * @return
	 */
	BigDecimal getOrderVolume(Integer advertId);
	
	/**
	 * 查询上架中的广告数量
	 * @param advert
	 * @return
	 */
	Integer countProcessingAdvert(OtcAdvert advert);
	
	/**
	 * 根据uid查询广告数量
	 * @param uid
	 * @return
	 */
	Integer countAdvertByUid(Integer uid);
	
	/**
	 * 分页查询广告
	 * @param map
	 * @return
	 */
	List<OtcAdvert> getBestAdvertList(Map<String, Object> map);
	
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
