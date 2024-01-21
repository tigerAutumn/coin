package com.qkwl.web.front.controller.base;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.util.TextUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSONObject;
import com.qkwl.common.Enum.validate.LocaleEnum;
import com.qkwl.common.dto.ExchangeRateResp;
import com.qkwl.common.dto.Enum.SystemTradeTypeEnum;
import com.qkwl.common.dto.coin.SystemCoinType;
import com.qkwl.common.dto.coin.SystemTradeType;
import com.qkwl.common.dto.common.DeviceInfo;
import com.qkwl.common.dto.user.FUser;
import com.qkwl.common.dto.wallet.UserCoinWallet;
import com.qkwl.common.framework.redis.RedisHelper;
import com.qkwl.common.match.MathUtils;
import com.qkwl.common.util.HttpRequestUtils;
import com.qkwl.common.util.I18NUtils;
import com.qkwl.web.permission.Enum.ExchangeRateEnum;
import com.qkwl.web.utils.CnyUtils;
import com.qkwl.web.utils.WebConstant;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class JsonBaseController extends RedisBaseControll {
	
	private static final Logger logger = LoggerFactory.getLogger(JsonBaseController.class);
	

    @Autowired 
    private RedisHelper redisHelper;


    public FUser getUser() {
    	FUser webUser = getCurrentUserInfoByToken();
    	if(webUser != null) {
    		return webUser;
    	}
    	
    	FUser appUser = getCurrentUserInfoByApiToken();
    	if(appUser != null) {
    		return appUser;
    	}
    	return null;
    }
    
    

    public LocaleEnum getLanEnum() {
        //String localeStr = LuangeHelper.getLan(HttpHolderUtils.getHttpServletRequest());
      String localeStr = I18NUtils.getCurrentLang();
        for (LocaleEnum locale : LocaleEnum.values()) {
            if (locale.getName().equals(localeStr)) {
                return locale;
            }
        }
        return null;
    }

    
    /**
     * @deprecated
     * @return
     */
    public String getIpAddr() {
        return HttpRequestUtils.getIPAddress();
    }

    public String getLan() {
        return I18NUtils.getCurrentLang();
    }

    public BigDecimal getNetAssets(List<UserCoinWallet> coinWallets) {
        Map<Integer, Integer> trades = redisHelper.getCoinIdToTradeId(WebConstant.BCAgentId);
        BigDecimal totalAssets = BigDecimal.ZERO;
        BigDecimal assets, price;
        Integer tradeId;
        for (UserCoinWallet coinWallet : coinWallets) {
            if (coinWallet.getCoinId().equals(9)) {
                assets = MathUtils.add(coinWallet.getTotal(), coinWallet.getFrozen());
                assets = MathUtils.add(assets, coinWallet.getBorrow());
                totalAssets = MathUtils.add(totalAssets, assets);
                continue;
            }
            
            SystemCoinType coinType = redisHelper.getCoinTypeSystem(coinWallet.getCoinId());
            if(coinType.getIsInnovateAreaCoin()) {
            	SystemTradeType tradeType = redisHelper.getTradeTypeFromInnvationCoinId(coinWallet.getCoinId(), WebConstant.BCAgentId);
        		if (tradeType == null) {
        			continue;
        		}
        		
        		price = redisHelper.getLastPrice(tradeType.getId());
        		if(tradeType.getBuyShortName().equals("GAVC")) {
                    assets = MathUtils.add(coinWallet.getTotal(), coinWallet.getFrozen());
                    assets = MathUtils.add(assets, coinWallet.getBorrow());
                    assets = MathUtils.mul(assets, price);
                    totalAssets = MathUtils.add(totalAssets, assets);
        		}else {
        			tradeId = trades.get(tradeType.getBuyCoinId());
        			BigDecimal lastPrice = BigDecimal.ONE;
        			if (tradeId == null) {
                        continue;
                    }
        			lastPrice = redisHelper.getLastPrice(tradeId);
        			price = MathUtils.mul(lastPrice, price);
                    assets = MathUtils.add(coinWallet.getTotal(), coinWallet.getFrozen());
                    assets = MathUtils.add(assets, coinWallet.getBorrow());
                    assets = MathUtils.mul(assets, price);
                    totalAssets = MathUtils.add(totalAssets, assets);
        		}
            }else {
                tradeId = trades.get(coinWallet.getCoinId());
                if (tradeId == null) {
                    continue;
                }
                price = redisHelper.getLastPrice(tradeId);
                assets = MathUtils.add(coinWallet.getTotal(), coinWallet.getFrozen());
                assets = MathUtils.add(assets, coinWallet.getBorrow());
                assets = MathUtils.mul(assets, price);
                totalAssets = MathUtils.add(totalAssets, assets);
            }
        }
        return MathUtils.toScaleNum(totalAssets, MathUtils.DEF_CNY_SCALE);
    }
    public BigDecimal getTotalAssets(List<UserCoinWallet> coinWallets) {
    	BigDecimal totalAssets = BigDecimal.ZERO;
        BigDecimal assets = BigDecimal.ZERO;
        BigDecimal tmp = BigDecimal.ZERO;
        List<SystemTradeType> GAVCTypeList = redisHelper.getTradeTypeSortAndInnovation(
        		SystemTradeTypeEnum.GAVC.getCode(), WebConstant.BCAgentId);
        List<SystemTradeType> BTCTypeList = redisHelper.getTradeTypeSortAndInnovation(
        		SystemTradeTypeEnum.BTC.getCode(), WebConstant.BCAgentId);
        List<SystemTradeType> ETHTypeList = redisHelper.getTradeTypeSortAndInnovation(
        		SystemTradeTypeEnum.ETH.getCode(), WebConstant.BCAgentId);
        List<SystemTradeType> USDTTypeList = redisHelper.getTradeTypeSortAndInnovation(
        		SystemTradeTypeEnum.USDT.getCode(), WebConstant.BCAgentId);
    	for (UserCoinWallet coinWallet : coinWallets) {
    		assets = MathUtils.add(coinWallet.getTotal(), coinWallet.getFrozen());
            assets = MathUtils.add(assets, coinWallet.getBorrow());
    		if (MathUtils.compareTo(assets, BigDecimal.ZERO) == 0) {
    			coinWallet.setPrice(BigDecimal.ZERO);
    			continue;
			}
    		//币种为GAVC时
            if (coinWallet.getCoinId() == 9) {
                totalAssets = MathUtils.add(totalAssets, assets);
                coinWallet.setPrice(BigDecimal.ONE);
                continue;
            }
            
    		//GAVC交易区
    		BigDecimal GAVCAssets = getAssets(coinWallet, GAVCTypeList, 9);
    		if (MathUtils.compareTo(GAVCAssets, BigDecimal.ZERO) != 0) {
    			tmp = GAVCAssets;
    			totalAssets = MathUtils.add(totalAssets, tmp);
    			continue;
    		}
    		//BTC交易区
    		BigDecimal BTCAssets = getAssets(coinWallet, BTCTypeList, 1);
    		if (MathUtils.compareTo(BTCAssets, BigDecimal.ZERO) != 0) {
    			tmp = BTCAssets;
    			totalAssets = MathUtils.add(totalAssets, tmp);
    			continue;
    		}
    		//ETH交易区
    		BigDecimal ETHAssets = getAssets(coinWallet, ETHTypeList, 4);
    		if (MathUtils.compareTo(ETHAssets, BigDecimal.ZERO) != 0) {
    			tmp = ETHAssets;
    			totalAssets = MathUtils.add(totalAssets, tmp);
    			continue;
    		}
            //USDT交易区
            BigDecimal USDTAssets = getAssets(coinWallet, USDTTypeList, 52);
            if (MathUtils.compareTo(USDTAssets, BigDecimal.ZERO) != 0) {
            	tmp = USDTAssets;
            	totalAssets = MathUtils.add(totalAssets, tmp);
            	continue;
            }
    	}
        return MathUtils.toScaleNum(totalAssets, MathUtils.DEF_CNY_SCALE);
    }
    
    /**
     * 获取币种对应交易区的资产
     * @param coinWallet
     * @param coinId
     * @return
     */
    private BigDecimal getAssets(UserCoinWallet coinWallet, List<SystemTradeType> tradeTypeList, int coinId) {
    	BigDecimal price = BigDecimal.ZERO;
    	BigDecimal assets = BigDecimal.ZERO;
    	Integer tradeId;
    	for (SystemTradeType systemTradeType : tradeTypeList) {
			if (systemTradeType.getSellCoinId().equals(coinWallet.getCoinId()) && systemTradeType.getBuyCoinId().equals(coinId)) {
                price = redisHelper.getLastPrice(systemTradeType.getId());
                //买方币种不为GAVC时
                if (systemTradeType.getBuyCoinId() != 9) {
                	Map<Integer, Integer> trades = redisHelper.getCoinIdToTradeId(WebConstant.BCAgentId);
                	//其他区币种转化为gavc卖方币种
        			tradeId = trades.get(systemTradeType.getBuyCoinId());
        			if (tradeId == null) {
        				continue;
					}
        			BigDecimal lastPrice = redisHelper.getLastPrice(tradeId);
        			price = MathUtils.mul(lastPrice, price);
				}
                coinWallet.setPrice(price);
                assets = MathUtils.add(coinWallet.getTotal(), coinWallet.getFrozen());
                assets = MathUtils.add(assets, coinWallet.getBorrow());
                assets = MathUtils.mul(assets, price);
                break;
			}
		}
    	return assets;
    }
    
    public DeviceInfo getDeviceInfo() {
    	HttpServletRequest requset = sessionContextUtils.getContextRequest();
    	String platform = requset.getHeader("platform");
    	String deviceId = requset.getHeader("deviceId");
    	String versionCode = requset.getHeader("versionCode");
    	String buildCode = requset.getHeader("buildCode");
    	String sysVer = requset.getHeader("sysVer");
    	String deviceModel = requset.getHeader("deviceModel");
    	DeviceInfo deviceInfo = new DeviceInfo();
    	if(StringUtils.isEmpty(platform)) {
    		deviceInfo.setBuildCode(requset.getParameter("buildCode"));
        	deviceInfo.setPlatform(Integer.parseInt(requset.getParameter("platform")));
        	deviceInfo.setDeviceId(requset.getParameter("deviceId"));
        	deviceInfo.setSysVer(requset.getParameter("sysVer"));
        	deviceInfo.setDeviceModel(requset.getParameter("deviceModel"));
        	deviceInfo.setVersionCode(requset.getParameter("versionCode"));
    	}else{
    		deviceInfo.setBuildCode(buildCode);
        	deviceInfo.setPlatform(Integer.parseInt(platform));
        	deviceInfo.setDeviceId(deviceId);
        	deviceInfo.setSysVer(sysVer);
        	deviceInfo.setDeviceModel(deviceModel);
        	deviceInfo.setVersionCode(versionCode);
    	}
		return deviceInfo;
    }	
    
    
    public BigDecimal getCnyByCoinId(int buyCoinId,int sellCoinId,BigDecimal p_new) {
		SystemCoinType coinType = new SystemCoinType();
		if(buyCoinId == 9) {
			coinType = redisHelper.getCoinType(sellCoinId);
			return p_new;
		}else {
			//ȡBTC/GSET���׶Լ۸����
			coinType = redisHelper.getCoinType(buyCoinId);
		}

		String symbol = coinType.getShortName()+"_GAVC";
		String[] symbols = symbol.split("_");
        List<SystemTradeType> tradeTypeList = redisHelper.getTradeTypeList(0);
        SystemTradeType systemTradeType = new SystemTradeType();
        for (SystemTradeType tradeType : tradeTypeList) {
            if (tradeType.getSellShortName().toLowerCase().equals(symbols[0].toLowerCase())
                    && tradeType.getBuyShortName().toLowerCase().equals(symbols[1].toLowerCase())) {
            	systemTradeType = tradeType;
            }
        }
		BigDecimal cny = redisHelper.getLastPrice(systemTradeType.getId());
		
		BigDecimal money = MathUtils.mul(p_new, cny);
		BigDecimal newMoney = MathUtils.toScaleNum(money, 2);
		if(newMoney.compareTo(BigDecimal.ZERO)<=0){
			return CnyUtils.validateCny(money);
		}
		return newMoney;
	}
    
    

    public String[] accessKeyList = {"bf18da33f4011c6dc52c839a4688bd3b","029ca634fd5aa600f6393d6fc423fc48","029ca634fd5aa600f6393d6fc423fc48","f5cb72cd2809303436866ffd284f9f9d"};
    @PostConstruct
    public String getCNYValue() {
	        String cny_value = redisHelper.getRedisData("CNY_VALUE");
	        if (!TextUtils.isEmpty(cny_value)) {
	            return cny_value;
	        }
	        
	        for (String string : accessKeyList) {
	        	try {
	        		String url = "http://data.fixer.io/api/latest?access_key="+string;
			        OkHttpClient client = new OkHttpClient.Builder()
			                .connectTimeout(10, TimeUnit.SECONDS)
			                .writeTimeout(10, TimeUnit.SECONDS)
			                .readTimeout(10, TimeUnit.SECONDS)
			                .build();
			        Response response = client.newCall(new Request.Builder()
						        .url(url)
						        .build())
						        .execute();
			        JSONObject jsonObject = JSONObject.parseObject(response.body().string());
			        if(!jsonObject.containsKey("rates")) {
			        	continue;
			        }
			        JSONObject rates = jsonObject.getJSONObject("rates");
			        BigDecimal bigDecimal = rates.getBigDecimal("CNY");
			        BigDecimal bigDecimal2 = rates.getBigDecimal("USD");
			        BigDecimal bg = bigDecimal.divide(bigDecimal2,BigDecimal.ROUND_HALF_UP);
			        cny_value = bg.toString();
			        setCNYValue(bg.toString());
		        	return cny_value;
				} catch (Exception e) {
					logger.info("查询汇率异常",e);
				}
			}
    	return null;
    }
    
    
	public List<ExchangeRateResp> getExchangeRate() {
		String exchangeRateStr = redisHelper.getRedisData("exchange_rate");
		if (StringUtils.isBlank(exchangeRateStr)) {
			for (String string : accessKeyList) {
				try {
					String url = "http://data.fixer.io/api/latest?access_key="+string;
			        OkHttpClient client = new OkHttpClient.Builder()
			                .connectTimeout(10, TimeUnit.SECONDS)
			                .writeTimeout(10, TimeUnit.SECONDS)
			                .readTimeout(10, TimeUnit.SECONDS)
			                .build();
			        Response response = client.newCall(new Request.Builder()
						        .url(url)
						        .build())
						        .execute();
			        
			        String result = response.body().string();
			        logger.info("获取汇率返回结果:{}",result);
			        JSONObject jsonObject = JSONObject.parseObject(result);
					if (jsonObject.containsKey("rates")) {
						exchangeRateStr = result;
						// 缓存redis
						redisHelper.setRedisData("exchange_rate", exchangeRateStr, 60 * 60);
						break;
					}

				} catch (Exception e) {
					logger.info("查询汇率异常", e);
				}
			}
			
		}
		JSONObject jsonObject = JSONObject.parseObject(exchangeRateStr);
		JSONObject rates = jsonObject.getJSONObject("rates");
		BigDecimal baseRate = rates.getBigDecimal("CNY");
		
		ExchangeRateEnum[] values = ExchangeRateEnum.values();
		List<ExchangeRateResp> list=new ArrayList<ExchangeRateResp>(6);
		ExchangeRateResp resp=null;
		for(ExchangeRateEnum e:values) {
			resp=new ExchangeRateResp();
			resp.setMoneySymbol(e.getCode());
			resp.setName(I18NUtils.getString(Locale.CHINA, e.getCode()));
			resp.setEnUSName(I18NUtils.getString(Locale.US, e.getCode()));
			resp.setKoKRName(I18NUtils.getString(Locale.KOREA, e.getCode()));
			resp.setRate(rates.getBigDecimal(e.getCode()).setScale(6).divide(baseRate, BigDecimal.ROUND_HALF_UP));
			list.add(resp);
		}
		return list;
	}
    
	
	public static void main(String[] args) {
		System.out.println(BigDecimal.ONE.divide(new BigDecimal(7.810116),  BigDecimal.ROUND_HALF_UP).setScale(6));
	}
	
    

    public void setCNYValue(String cny) {
        if (TextUtils.isEmpty(cny)) {
            return;
        }
        redisHelper.setRedisData("CNY_VALUE", cny, 60 * 60);
    }

}
