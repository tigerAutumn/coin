package com.qkwl.common.rpc.otc;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import com.qkwl.common.dto.common.Pagination;
import com.qkwl.common.dto.otc.OtcAdvert;
import com.qkwl.common.dto.otc.OtcAdvertPrice;
import com.qkwl.common.dto.otc.OtcPayment;
import com.qkwl.common.exceptions.BCException;
import com.qkwl.common.result.Result;

/*
 * otc 广告
 */
public interface IOtcAdvertService {

	/**
	 * 交易大厅查询广告
	 * @param pageParam
	 * @param otcAdvert
	 * @return
	 */
	Pagination<OtcAdvert> selectAdvertPageList(Pagination<OtcAdvert> pageParam, OtcAdvert otcAdvert
			, BigDecimal averagePrice, BigDecimal hotcoinPrice);
	
	/**
	 * 查询广告正在处理中的订单
	 * @param advertId
	 * @return
	 */
	Integer getProcessingOrders(Integer advertId);
	
	/**
	 * 
	 * @param otcAdvert
	 * @return
	 */
	List<OtcAdvert> selectMerchantAdvert(OtcAdvert otcAdvert);
	
	/**
	 * 
	 * @param otcAdvert
	 * @return
	 */
	List<OtcAdvert> selectOtherMerchantAdvert(OtcAdvert otcAdvert);
	
	/**
	 * 查询固定广告的最低价和最高价
	 * @return
	 */
	OtcAdvertPrice selectAdvertPrice(Integer coinId, Integer currencyId);
	
	/**
	 * 查询用户默认的支付方式
	 * @param uid
	 * @return
	 */
	List<OtcPayment> getAllPaymentList(Integer uid);
	
	/**
	 * 发布广告
	 * @param advert
	 * @throws Exception 
	 */
	Integer releaseAdvert(OtcAdvert advert) throws BCException;
	
	/**
	 * 
	 */
	Result updateAdvert(OtcAdvert advert) throws BCException;
	
	/**
	 * 广告管理查询我的广告
	 * @param pageParam
	 * @param otcAdvert
	 * @return
	 */
	Pagination<OtcAdvert> selectMyAdvert(Pagination<OtcAdvert> pageParam, OtcAdvert otcAdvert);
	
	/**
	 * 根据id查询广告
	 * @param id
	 * @return
	 */
	OtcAdvert selectAdvertById(Integer id);
	
	/**
	 * 下架广告
	 * @param id
	 * @return
	 */
	Result putOffAdvert(OtcAdvert advert) throws BCException;
	
	/**
	 * 激活广告
	 * @param id
	 * @return
	 */
	Result activityAdvert(OtcAdvert advert);
	
	Map<String, String> getParam();
	
	/**
	 * 根据id查询广告带行级锁
	 * @param id
	 * @return
	 */
	OtcAdvert selectAdvertByIdLock(Integer id);
	
	/**
	 * 校验用户otc是否禁用
	 */
	boolean checkUser(Integer userId);
	
	/**
	 * 获取市场平均价
	 * @return
	 */
	BigDecimal getAveragePrice(String coinName, String currencyName);
	
	/**
	 * 获取热币平均价
	 * @return
	 */
	BigDecimal getHotcoinPrice(Integer coinId);
	
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
	 * 下架不符合的广告
	 */
	void putOffUnableAds();
	
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
	 * 交易大厅查询广告
	 * @param pageParam
	 * @param otcAdvert
	 * @return
	 */
	List<OtcAdvert> selectBestAdvertList(OtcAdvert otcAdvert
			, BigDecimal averagePrice, BigDecimal hotcoinPrice, Integer type);
}
