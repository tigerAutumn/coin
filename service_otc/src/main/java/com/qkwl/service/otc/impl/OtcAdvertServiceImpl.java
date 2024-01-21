package com.qkwl.service.otc.impl;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.util.TextUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.qkwl.common.dto.Enum.SystemTradeTypeEnum;
import com.qkwl.common.dto.coin.SystemCoinTypeVO;
import com.qkwl.common.dto.coin.SystemTradeType;
import com.qkwl.common.dto.common.Pagination;
import com.qkwl.common.dto.otc.OtcAdvert;
import com.qkwl.common.dto.otc.OtcAdvertPrice;
import com.qkwl.common.dto.otc.OtcCurrency;
import com.qkwl.common.dto.otc.OtcPayment;
import com.qkwl.common.dto.otc.SystemOtcSetting;
import com.qkwl.common.dto.user.FUser;
import com.qkwl.common.exceptions.BCException;
import com.qkwl.common.framework.redis.RedisHelper;
import com.qkwl.common.match.MathUtils;
import com.qkwl.common.result.Result;
import com.qkwl.common.rpc.otc.IOtcAdvertService;
import com.qkwl.common.rpc.otc.IOtcCoinWalletService;
import com.qkwl.common.util.Constant;
import com.qkwl.common.util.ModelMapperUtils;
import com.qkwl.service.otc.dao.FUserMapper;
import com.qkwl.service.otc.dao.OtcAdvertMapper;
import com.qkwl.service.otc.dao.SystemOtcSettingMapper;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

@Service("otcAdvertService")
public class OtcAdvertServiceImpl implements IOtcAdvertService {

    private static final Logger logger = LoggerFactory.getLogger(OtcAdvertServiceImpl.class);

	@Autowired
	private OtcAdvertMapper otcAdvertMapper;
	@Autowired
	private IOtcCoinWalletService otcCoinWalletService;
	@Autowired
	private SystemOtcSettingMapper systemOtcSettingMapper;
	@Autowired
	private FUserMapper userMapper;
	@Autowired
    private RedisHelper redisHelper;
	
	@Override
	public Pagination<OtcAdvert> selectAdvertPageList(Pagination<OtcAdvert> pageParam, OtcAdvert otcAdvert
			, BigDecimal averagePrice, BigDecimal hotcoinPrice) {
		// 组装查询条件数据 
		Map<String,Object> map = new HashMap<String, Object>();
		map.put("offset", pageParam.getOffset());
		map.put("limit", pageParam.getPageSize());
		map.put("side", otcAdvert.getSide());
		map.put("currencyId", otcAdvert.getCurrencyId());
		map.put("bankinfoType", otcAdvert.getBankinfoType());
		map.put("amount", otcAdvert.getAmount());
		map.put("coinId", otcAdvert.getCoinId());
		map.put("averagePrice", averagePrice);
		map.put("hotcoinPrice", hotcoinPrice);
		
		// 查询广告总数
		int count = otcAdvertMapper.countAdvertListByParam(map);
		
		if(count > 0) {
			// 查询广告列表
			List<OtcAdvert> advertList = otcAdvertMapper.getAdvertPageList(map);
			// 设置返回数据
			pageParam.setData(advertList);
		}
		pageParam.setTotalRows(count);
		return pageParam;
	}

	@Override
	public List<OtcAdvert> selectMerchantAdvert(OtcAdvert otcAdvert) {
		// 组装查询条件数据
		Map<String,Object> map = new HashMap<String, Object>();
		map.put("userId", otcAdvert.getUserId());
		map.put("side", otcAdvert.getSide());
		return otcAdvertMapper.selectMerchantAdvert(map);
	}
	
	@Override
	public List<OtcAdvert> selectOtherMerchantAdvert(OtcAdvert otcAdvert) {
		// 组装查询条件数据
		Map<String,Object> map = new HashMap<String, Object>();
		map.put("userId", otcAdvert.getUserId());
		map.put("side", otcAdvert.getSide());
		return otcAdvertMapper.selectOtherMerchantAdvert(map);
	}
	
	@Override
	public OtcAdvertPrice selectAdvertPrice(Integer coinId, Integer currencyId) {
		Map<String,Object> map = new HashMap<String, Object>();
		map.put("coinId", coinId);
		map.put("currencyId", currencyId);
		return otcAdvertMapper.selectAdvertPrice(map);
	}

	@Override
	public List<OtcPayment> getAllPaymentList(Integer uid) {
		return otcAdvertMapper.getAllPaymentList(uid);
	}
	
	@Override
	@Transactional(value="flexibleTransMgr" ,isolation = Isolation.REPEATABLE_READ, propagation=Propagation.REQUIRED, rollbackFor = Exception.class)
	public Integer releaseAdvert(OtcAdvert advert) throws BCException {
		try {
			if (!otcCoinWalletService.otcBalance(advert.getUserId(), advert.getCoinId())) {
				return null;
			}
			//冻结钱包
			if (Constant.OTC_SELL.equals(advert.getSide())) {
				BigDecimal amount = MathUtils.mul(advert.getVisiableVolume(), MathUtils.add(advert.getFeeRate(), BigDecimal.ONE));
				Integer digit = Constant.OTC_COUNT_DIGIT;
				Result frozenResult = otcCoinWalletService.userOtcFrozen(advert.getUserId(), advert.getCoinId(), MathUtils.toScaleNum(amount, digit));
				if (!frozenResult.getSuccess()) {
				    logger.error("钱包冻结失败");
					throw new BCException("钱包冻结失败");
		        }
			}
			//生成广告
			if (otcAdvertMapper.insertOtcAdvert(advert) <= 0) {
                logger.error("生成广告失败");
				throw new BCException("生成广告失败");
			}
			return advert.getId();
		} catch (BCException e) {
            logger.error("发布广告 releaseAdvert advert:{},BCException：{}",JSON.toJSONString(advert),e);
			throw e;
		} catch (Exception e) {
            logger.error("发布广告 releaseAdvert  advert:{},Exception：{}",JSON.toJSONString(advert),e);
			throw new BCException("发布广告异常");
		}
		
	}
	
	@Override
	@Transactional(value="flexibleTransMgr" ,isolation = Isolation.REPEATABLE_READ, propagation=Propagation.REQUIRED, rollbackFor = Exception.class)
	public Result updateAdvert(OtcAdvert advert) throws BCException {
		try {
			OtcAdvert otcAdvert = otcAdvertMapper.selectAdvertById(advert.getId());
			if(otcAdvert == null) {
				return Result.failure("广告不存在！");
			}
			if (otcAdvert.getIsFrozen() == 1) {
				return Result.failure("广告冻结中！");
			}
			if (otcAdvert.getStatus() != 2) {
				return Result.failure("广告不处于下架状态！");
			}
			int userId = advert.getUserId();
			if (otcAdvert.getUserId() != userId) {
				return Result.failure("用户不一致！");
			}
			if (!otcCoinWalletService.otcBalance(otcAdvert.getUserId(), otcAdvert.getCoinId())) {
				return Result.failure("用户otc账户不平衡！");
			}
			
			//冻结钱包
			if (Constant.OTC_SELL.equals(advert.getSide())) {
				BigDecimal amount = MathUtils.mul(advert.getVisiableVolume(), MathUtils.add(advert.getFeeRate(), BigDecimal.ONE));
				Integer digit = Constant.OTC_COUNT_DIGIT;
				Result frozenResult = otcCoinWalletService.userOtcFrozen(advert.getUserId(), advert.getCoinId(), MathUtils.toScaleNum(amount, digit));
				if (!frozenResult.getSuccess()) {
					return frozenResult;
				}
			}
			//更新广告
			if (otcAdvertMapper.updateOtcAdvert(advert) <= 0) {
				throw new BCException("更新广告失败");
			}
			return Result.success();
		}  catch (BCException e) {
			throw e;
		} catch (Exception e) {
			throw new BCException("更新广告异常");
		}
	}
	
	@Override
	public Pagination<OtcAdvert> selectMyAdvert(Pagination<OtcAdvert> pageParam, OtcAdvert otcAdvert) {
		// 组装查询条件数据
		Map<String,Object> map = new HashMap<String, Object>();
		map.put("offset", pageParam.getOffset());
		map.put("limit", pageParam.getPageSize());
		map.put("userId", otcAdvert.getUserId());
		map.put("side", otcAdvert.getSide());
		map.put("currencyId", otcAdvert.getCurrencyId());
		map.put("coinId", otcAdvert.getCoinId());
		map.put("status", otcAdvert.getStatus());
		
		// 查询广告总数
		int count = otcAdvertMapper.countMyAdvertByParam(map);
		
		if(count > 0) {
			// 查询广告列表
			List<OtcAdvert> advertList = otcAdvertMapper.getMyAdvert(map);
			// 设置返回数据
			pageParam.setData(advertList);
		}
		pageParam.setTotalRows(count);
		return pageParam;
	}

	@Override
	public OtcAdvert selectAdvertById(Integer id) {
		return otcAdvertMapper.selectAdvertById(id);
	}

	@Override
	@Transactional(value="flexibleTransMgr", isolation = Isolation.REPEATABLE_READ, propagation=Propagation.REQUIRED, rollbackFor = Exception.class)
	public Result putOffAdvert(OtcAdvert advert) throws BCException {
		try {
			if (!otcCoinWalletService.otcBalance(advert.getUserId(), advert.getCoinId())) {
				return Result.failure("用户otc账户不平衡！");
			}
			
			//解冻钱包
			if (Constant.OTC_SELL.equals(advert.getSide())) {
				Integer digit = Constant.OTC_COUNT_DIGIT;
				BigDecimal totalVolume = MathUtils.mul(advert.getVolume(), MathUtils.add(advert.getFeeRate(), BigDecimal.ONE));
				BigDecimal orderVolume = otcAdvertMapper.getOrderVolume(advert.getId());
				BigDecimal unfrozenVolume = MathUtils.sub(totalVolume, orderVolume);
				Result frozenResult = otcCoinWalletService.userOtcUnFrozen(advert.getUserId(), advert.getCoinId(), MathUtils.toScaleNum(unfrozenVolume, digit));
				if (!frozenResult.getSuccess()) {
		            return frozenResult;
		        }
			}
			//更新广告
			if (otcAdvertMapper.updateOtcAdvert(advert) <= 0) {
				throw new BCException("下架广告失败");
			}
			return Result.success();
		} catch (BCException e) {
			throw e;
		} catch (Exception e) {
			throw new BCException("下架广告异常");
		}
		
	}
	
	@Override
	public Result activityAdvert(OtcAdvert advert) {
		if (!otcCoinWalletService.otcBalance(advert.getUserId(), advert.getCoinId())) {
			return Result.failure("用户otc账户不平衡！");
		}
		//更新广告
		if (otcAdvertMapper.updateOtcAdvert(advert) <= 0) {
			return Result.failure("激活广告失败");
		}
		return Result.success();
	}
	
	/**
	 * 查询otc公共参数
	 */
	@Override
	public Map<String, String> getParam() {
		Map<String, String> map = new HashMap<>();
		try {
			List<SystemOtcSetting> list = systemOtcSettingMapper.selectAll();
			for(SystemOtcSetting value : list) {
				map.put(value.getKey(), value.getValue());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return map;
	}
	
	@Override
	public OtcAdvert selectAdvertByIdLock(Integer id) {
		return otcAdvertMapper.selectAdvertByIdLock(id);
	}
	
	@Override
	public boolean checkUser(Integer userId) {
		FUser user = userMapper.selectByPrimaryKey(userId);
		return user.isOtcAction();
	}

	@Override
	public BigDecimal getAveragePrice(String coinName, String currencyName) {
	    logger.info("getAveragePrice  coinName:{},currencyName:{}",coinName,currencyName);
	    Long beginTime =System.currentTimeMillis();
		//获取汇率
		BigDecimal cnyValue = new BigDecimal(getCNYValue(currencyName));
        Long endTime =System.currentTimeMillis();
        logger.info("cnyValue:{} losttime:{} s",cnyValue,(endTime-beginTime)/1000F);
		//市场平均价
		BigDecimal averagePrice = BigDecimal.ZERO;
		BigDecimal averagePriceUSD = BigDecimal.ZERO;
		String redis = redisHelper.getRedisData("AVERAGE_PRICE_" + coinName);
		if (!TextUtils.isEmpty(redis)) {
			averagePriceUSD = new BigDecimal(redis);
			averagePrice = MathUtils.mul(cnyValue, averagePriceUSD);
			return averagePrice;
		}
		try {
            Long beginTime2 =System.currentTimeMillis();
			String url = "https://api.coinmarketcap.com/v1/ticker/";
			OkHttpClient client = new OkHttpClient.Builder()
	                .connectTimeout(1, TimeUnit.SECONDS)
	                .writeTimeout(1, TimeUnit.SECONDS)
	                .readTimeout(1, TimeUnit.SECONDS)
	                .build();
	        
	        Response response;
				response = client.newCall(new Request.Builder()
				        .url(url)
				        .build())
				        .execute();
			JSONArray averageArray = JSON.parseArray(response.body().string());
			for (Object object : averageArray) {
				JSONObject averageObject = (JSONObject)object;
				if (coinName.equals(averageObject.getString("symbol"))) {
					averagePriceUSD = averageObject.getBigDecimal("price_usd");
				}
			}
            Long endTime2 =System.currentTimeMillis();
            logger.info("342 averagePriceUSD:{} losttime:{} s",averagePriceUSD,(endTime2-beginTime2)/1000F);
			averagePrice = MathUtils.mul(cnyValue, averagePriceUSD);
			redisHelper.setRedisData("AVERAGE_PRICE_"+coinName, averagePriceUSD, 60);
		} catch (Exception e) {
			averagePrice = BigDecimal.ZERO;
		}
		return averagePrice;
	}

	@Override
	public BigDecimal getHotcoinPrice(Integer coinId) {
		//币种为GAVC时
        if (coinId == 9) {
            return BigDecimal.ZERO;
        }
        List<SystemTradeType> GAVCTypeList = redisHelper.getTradeTypeSortAndInnovation(
        		SystemTradeTypeEnum.GAVC.getCode(), 0);
        List<SystemTradeType> BTCTypeList = redisHelper.getTradeTypeSortAndInnovation(
        		SystemTradeTypeEnum.BTC.getCode(), 0);
        List<SystemTradeType> ETHTypeList = redisHelper.getTradeTypeSortAndInnovation(
        		SystemTradeTypeEnum.ETH.getCode(), 0);
        List<SystemTradeType> USDTTypeList = redisHelper.getTradeTypeSortAndInnovation(
        		SystemTradeTypeEnum.USDT.getCode(), 0);
        //GAVC交易区
		BigDecimal price = getPrice(coinId, GAVCTypeList, 9);
		if (MathUtils.compareTo(price, BigDecimal.ZERO) != 0) {
			return price;
		}
		//BTC交易区
		price = getPrice(coinId, BTCTypeList, 1);
		if (MathUtils.compareTo(price, BigDecimal.ZERO) != 0) {
			return price;
		}
		//ETH交易区
		price = getPrice(coinId, ETHTypeList, 4);
		if (MathUtils.compareTo(price, BigDecimal.ZERO) != 0) {
			return price;
		}
        //USDT交易区
		price = getPrice(coinId, USDTTypeList, 52);
		return price;
	}
	
	private BigDecimal getPrice(Integer coinId, List<SystemTradeType> tradeTypeList, Integer tradeCoinId) {
		BigDecimal price = BigDecimal.ZERO;
		Integer tradeId;
    	for (SystemTradeType systemTradeType : tradeTypeList) {
			if (systemTradeType.getSellCoinId() == coinId && systemTradeType.getBuyCoinId() == tradeCoinId) {
                price = redisHelper.getLastPrice(systemTradeType.getId());
                //买方币种不为GAVC时
                if (systemTradeType.getBuyCoinId() != 9) {
                	Map<Integer, Integer> trades = redisHelper.getCoinIdToTradeId(0);
                	//其他区币种转化为gavc卖方币种
        			tradeId = trades.get(systemTradeType.getBuyCoinId());
        			if (tradeId == null) {
        				continue;
					}
        			BigDecimal lastPrice = redisHelper.getLastPrice(tradeId);
        			price = MathUtils.mul(lastPrice, price);
				}
                break;
			}
		}
    	return price;
	}

    public String[] accessKeyList = {"f5cb72cd2809303436866ffd284f9f9d","bf18da33f4011c6dc52c839a4688bd3b","029ca634fd5aa600f6393d6fc423fc48","029ca634fd5aa600f6393d6fc423fc48"};

	private String getCNYValue(String currencyName) {
            String cny_value = redisHelper.getRedisData("CNY_VALUE_" + currencyName);
            if (!TextUtils.isEmpty(cny_value)) {
                logger.info("getCNYValue  redis cny_value :{}", cny_value);
                return cny_value;
            }
		//创建OkHttpClient实例对象
		OkHttpClient okHttpClient = new OkHttpClient.Builder()
				.connectTimeout(10, TimeUnit.SECONDS)
				.writeTimeout(10, TimeUnit.SECONDS)
				.readTimeout(10, TimeUnit.SECONDS)
				.build();

		//创建Request对象
		for (String string : accessKeyList) {
			String urlStr = "http://data.fixer.io/api/latest?access_key=" + string;
			Request request = new Request.Builder()
					.url(urlStr)
					.get()
					.build();
			//同步请求
			Response response;
			try {
				logger.info("url request:{}", urlStr);
				response = okHttpClient.newCall(request).execute();
				JSONObject jsonObject = JSONObject.parseObject(response.body().string());
				if (!jsonObject.containsKey("rates")) {
					continue;
				}
				JSONObject rates = jsonObject.getJSONObject("rates");
				BigDecimal bigDecimal = rates.getBigDecimal("CNY");
				BigDecimal bigDecimal2 = rates.getBigDecimal("USD");
				BigDecimal bg = bigDecimal.divide(bigDecimal2, bigDecimal.ROUND_HALF_UP);
				cny_value = bg.toString();
				setCNYValue(bg.toString(), currencyName);
				return cny_value;
			} catch (IOException e) {
				logger.info("getCNYValue exeception :{}", e);
				continue;
			}
		}
		return "0";

    }
	
	private void setCNYValue(String cny, String currencyName) {
	    logger.info("setCNYValue cny:{},currencyName:{}",cny,currencyName);
        if (TextUtils.isEmpty(cny)) {
            return;
        }
        redisHelper.setRedisData("CNY_VALUE_"+currencyName, cny, 60 * 60);
    }
	
	@Override
	public String getCoinName(Integer coinId) {
		return otcAdvertMapper.getCoinName(coinId);
	}
	
	@Override
	public String getCurrencyName(Integer currencyId) {
		return otcAdvertMapper.getCurrencyName(currencyId);
	}

	@Override
	public Integer getProcessingOrders(Integer advertId) {
		return otcAdvertMapper.getProcessingOrders(advertId);
	}
	
	@Override
	@Transactional(value="flexibleTransMgr" ,isolation = Isolation.REPEATABLE_READ, propagation=Propagation.REQUIRED, rollbackFor = Exception.class)
	public void putOffUnableAds() {
//		1. 将广告过期
		//查询过期的广告
		List<OtcAdvert> overdueAds = otcAdvertMapper.getOverdueAds();
		for (OtcAdvert otcAdvert : overdueAds) {
			if (!otcCoinWalletService.otcBalance(otcAdvert.getUserId(), otcAdvert.getCoinId())) {
				continue;
			}
			//解冻钱包
			if (Constant.OTC_SELL.equals(otcAdvert.getSide()) && Constant.OTC_ADVERT_ON.equals(otcAdvert.getStatus())) {
				Integer digit = Constant.OTC_COUNT_DIGIT;
				BigDecimal totalVolume = MathUtils.mul(otcAdvert.getVolume(), MathUtils.add(otcAdvert.getFeeRate(), BigDecimal.ONE));
				BigDecimal orderVolume = otcAdvertMapper.getOrderVolume(otcAdvert.getId());
				BigDecimal unfrozenVolume = MathUtils.sub(totalVolume, orderVolume);
				otcCoinWalletService.userOtcUnFrozen(otcAdvert.getUserId(), otcAdvert.getCoinId(), MathUtils.toScaleNum(unfrozenVolume, digit));
			}
			//更新广告
			otcAdvert.setUpdateTime(new Date());
			otcAdvert.setStatus(Constant.OTC_ADVERT_OVERDUE);
			otcAdvertMapper.updateOtcAdvert(otcAdvert);
		}
		
//		2. 下架总金额小于最小额度的固定价格广告
		//查询广告
		List<OtcAdvert> fixedAds = otcAdvertMapper.getFixedAds();
		for (OtcAdvert otcAdvert : fixedAds) {
			if (!otcCoinWalletService.otcBalance(otcAdvert.getUserId(), otcAdvert.getCoinId())) {
				continue;
			}
			//解冻钱包
			if (Constant.OTC_SELL.equals(otcAdvert.getSide())) {
				Integer digit = Constant.OTC_COUNT_DIGIT;
				BigDecimal totalVolume = MathUtils.mul(otcAdvert.getVolume(), MathUtils.add(otcAdvert.getFeeRate(), BigDecimal.ONE));
				BigDecimal orderVolume = otcAdvertMapper.getOrderVolume(otcAdvert.getId());
				if (orderVolume == null) {
					orderVolume = BigDecimal.ZERO;
				}
				BigDecimal unfrozenVolume = MathUtils.sub(totalVolume, orderVolume);
				otcCoinWalletService.userOtcUnFrozen(otcAdvert.getUserId(), otcAdvert.getCoinId(), MathUtils.toScaleNum(unfrozenVolume, digit));
			}
			//更新广告
			otcAdvert.setUpdateTime(new Date());
			otcAdvert.setStatus(Constant.OTC_ADVERT_OFF);
			otcAdvertMapper.updateOtcAdvert(otcAdvert);
		}
	
//		3. 下架总金额小于最小额度的浮动价格广告
		//查询所有法币名称
		List<OtcCurrency> currencyList = new ArrayList<>();
		currencyList = redisHelper.getOpenOtcCurrencyList();
		//查询所有币种名称
		List<SystemCoinTypeVO> coinTypeList = new ArrayList<>();
		coinTypeList = ModelMapperUtils.mapper(redisHelper.getOpenOtcCoinTypeList(), SystemCoinTypeVO.class);
		
		Map<String,Object> map = new HashMap<String, Object>();
		for (OtcCurrency otcCurrency : currencyList) {
			for (SystemCoinTypeVO systemCoinTypeVO : coinTypeList) {
				//平均价
				BigDecimal averagePrice = getAveragePrice(systemCoinTypeVO.getShortname(), otcCurrency.getCurrencyCode());
//				//币安价
//				BigDecimal binancePrice = getBinancePrice(systemCoinTypeVO.getShortname(), otcCurrency.getCurrencyCode());
//				//火币价
//				BigDecimal houbiPrice = getHoubiPrice(systemCoinTypeVO.getShortname(), otcCurrency.getCurrencyCode());
				map.put("coinId", systemCoinTypeVO.getId());
				map.put("currencyId", otcCurrency.getId());
				map.put("averagePrice", averagePrice);
//				map.put("binancePrice", binancePrice);
//				map.put("houbiPrice", houbiPrice);
				//更改过期广告的状态
				List<OtcAdvert> floatAds = otcAdvertMapper.getFloatAds(map);
				for (OtcAdvert otcAdvert : floatAds) {
					if (!otcCoinWalletService.otcBalance(otcAdvert.getUserId(), otcAdvert.getCoinId())) {
						continue;
					}
					//解冻钱包
					if (Constant.OTC_SELL.equals(otcAdvert.getSide())) {
						Integer digit = Constant.OTC_COUNT_DIGIT;
						BigDecimal totalVolume = MathUtils.mul(otcAdvert.getVolume(), MathUtils.add(otcAdvert.getFeeRate(), BigDecimal.ONE));
						BigDecimal orderVolume = otcAdvertMapper.getOrderVolume(otcAdvert.getId());
						if (orderVolume == null) {
							orderVolume = BigDecimal.ZERO;
						}
						BigDecimal unfrozenVolume = MathUtils.sub(totalVolume, orderVolume);
						otcCoinWalletService.userOtcUnFrozen(otcAdvert.getUserId(), otcAdvert.getCoinId(), MathUtils.toScaleNum(unfrozenVolume, digit));
					}
					//更新广告
					otcAdvert.setUpdateTime(new Date());
					otcAdvert.setStatus(Constant.OTC_ADVERT_OFF);
					otcAdvertMapper.updateOtcAdvert(otcAdvert);
				}
			}
		}
	}

	@Override
	public Integer countProcessingAdvert(OtcAdvert advert) {
		return otcAdvertMapper.countProcessingAdvert(advert);
	}
	
	@Override
	public Integer countAdvertByUid(Integer userId) {
		return otcAdvertMapper.countAdvertByUid(userId);
	}
	
	@Override
	public List<OtcAdvert> selectBestAdvertList(OtcAdvert otcAdvert
			, BigDecimal averagePrice, BigDecimal hotcoinPrice, Integer type) {
		// 组装查询条件数据 
		Map<String,Object> map = new HashMap<String, Object>();
		map.put("side", otcAdvert.getSide());
		map.put("amount", otcAdvert.getAmount());
		map.put("coinId", otcAdvert.getCoinId());
		map.put("averagePrice", averagePrice);
		map.put("hotcoinPrice", hotcoinPrice);
		map.put("type", type);
		
		// 查询广告列表
		List<OtcAdvert> advertList = otcAdvertMapper.getBestAdvertList(map);
		return advertList;
	}
}
