package com.qkwl.web.front.controller.v2;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.druid.util.StringUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.qkwl.common.dto.Enum.SystemCoinTypeEnum;
import com.qkwl.common.dto.Enum.SystemTradeStatusEnum;
import com.qkwl.common.dto.Enum.SystemTradeTypeNewEnum;
import com.qkwl.common.dto.Enum.otc.OtcPaymentStatusEnum;
import com.qkwl.common.dto.carousel.SystemCarousel;
import com.qkwl.common.dto.coin.SystemCoinInfo;
import com.qkwl.common.dto.coin.SystemCoinType;
import com.qkwl.common.dto.coin.SystemCoinTypeVO;
import com.qkwl.common.dto.coin.SystemTradeType;
import com.qkwl.common.dto.market.TickerData;
import com.qkwl.common.dto.news.FArticle;
import com.qkwl.common.dto.news.FArticleType;
import com.qkwl.common.dto.otc.OtcPayment;
import com.qkwl.common.dto.system.FSystemBankinfoWithdraw;
import com.qkwl.common.dto.system.HistoricVersion;
import com.qkwl.common.dto.web.FFriendLink;
import com.qkwl.common.dto.web.FSystemLan;
import com.qkwl.common.framework.redis.RedisHelper;
import com.qkwl.common.match.MathUtils;
import com.qkwl.common.redis.RedisConstant;
import com.qkwl.common.rpc.push.PushService;
import com.qkwl.common.util.HttpRequestUtils;
import com.qkwl.common.util.I18NUtils;
import com.qkwl.common.util.ModelMapperUtils;
import com.qkwl.common.util.ReturnResult;
import com.qkwl.web.front.controller.base.JsonBaseController;
import com.qkwl.web.permission.annotation.PassToken;
import com.qkwl.web.utils.WebConstant;

import io.swagger.annotations.ApiOperation;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import qiniu.ip17mon.LocationInfo;
import qiniu.ip17mon.Locator;
import springfox.documentation.annotations.ApiIgnore;

@Controller
@ApiIgnore
public class CommonV2Controller extends JsonBaseController {
	private static final Logger logger = LoggerFactory.getLogger(CommonV2Controller.class);
	
	@Autowired
    private RedisHelper redisHelper;
    @Autowired
    private PushService pushService;
	
	/**
     * 首页轮播图以及公告
     */
    @ResponseBody
	@PassToken
    @ApiOperation("")
	@RequestMapping(value="/v2/common/index",method = {RequestMethod.GET,RequestMethod.POST})
    public ReturnResult businessList() 
    {
    		JSONArray imageList = new JSONArray();
    		//获取轮播图所有key
    		Set<String> keys  = redisHelper.keys(String.format("%s%s%s", RedisConstant.SYSTEM_CAROUSEL,I18NUtils.getCurrentLang(),"*"));
    		for(String key:keys) {
    			JSONObject object = new JSONObject();
    			SystemCarousel carousel = JSON.parseObject(redisHelper.get(key), SystemCarousel.class);
    			object.put("url",carousel.getUrl()); 
    			object.put("targetUrl",carousel.getTargetUrl()); 
    			imageList.add(object);
    		}
			//logger.error("systemLan :{}", JSONUtils.toJSONString(request));
    		FSystemLan systemLan = redisHelper.getLanguageType(I18NUtils.getCurrentLang());
    		FArticleType farticletype = redisHelper.getArticleType(5, systemLan.getFid());
    		
            if (farticletype == null) {
                return ReturnResult.FAILUER(""); 
            }
            List<FArticle> farticles = redisHelper.getArticles(systemLan.getFid(),2,farticletype.getFid(),1,WebConstant.BCAgentId);
            JSONArray articleList = new JSONArray();
    		if(farticles != null && farticles.size()>0) {
                for(FArticle fArticle : farticles) {
                	JSONObject object = new JSONObject();
                	object.put("title",fArticle.getFtitle()); 
        			object.put("id",fArticle.getFid()); 
        			object.put("typeId",fArticle.getFarticletype()); 
        			object.put("farticletype_s",I18NUtils.getString("CommonV2Controller.12"));  //$NON-NLS-2$
        			articleList.add(object);
                }
    		}
            
            JSONObject result = new JSONObject();
            result.put("imageList", imageList); 
            result.put("articleList", articleList); 
	        return ReturnResult.SUCCESS(result);
    }
    
    
    /**
     * 根据交易区id查询交易对列表
     */
    @ResponseBody
    @ApiOperation("")
	@RequestMapping(value="/v2/common/tradeList",method = {RequestMethod.GET,RequestMethod.POST})
    public ReturnResult tradeList(@RequestParam(required = true, defaultValue = "0") Integer id) 
    {
    		List<SystemTradeType> list = redisHelper.getAllTradeTypeList(WebConstant.BCAgentId);
    		
    		JSONArray array = new JSONArray();
            for(SystemTradeType systemTradeType : list) {
            	if(!systemTradeType.getStatus().equals(SystemTradeStatusEnum.ABNORMAL.getCode())
            			&&systemTradeType.getType().equals(id)) {
            		JSONObject obj = new JSONObject();
            		obj.put("id", systemTradeType.getId()); 
            		obj.put("sellShortName", systemTradeType.getSellShortName()); 
            		array.add(systemTradeType);
            	}
            }
	        return ReturnResult.SUCCESS(array);
    }
    
    
    /**
     * 查询四大交易区总量
     */
    @ResponseBody
	@PassToken
    @ApiOperation("")
	@RequestMapping(value="/v2/common/tradeVol",method = {RequestMethod.GET,RequestMethod.POST})
    public ReturnResult tradeVol() 
    {
    		List<SystemTradeType> list = redisHelper.getAllTradeTypeList(WebConstant.BCAgentId);
    		BigDecimal btcVol = BigDecimal.ZERO;
    		BigDecimal gsetVol = BigDecimal.ZERO;
    		BigDecimal ethVol = BigDecimal.ZERO;
    		BigDecimal usdtVol = BigDecimal.ZERO;
            for(SystemTradeType systemTradeType : list) {
            	//如果币种没有被禁用
            	if(!systemTradeType.getStatus().equals(SystemTradeStatusEnum.ABNORMAL.getCode())) 
            	{
            		//计算BTC区的交易总量
            		if(systemTradeType.getType().equals(SystemTradeTypeNewEnum.BTC.getCode())) {
            			TickerData tickerData = redisHelper.getTickerData(systemTradeType.getId());
            			if(tickerData != null) {
            				//单价*数量就是BTC总数
            				BigDecimal price = tickerData.getLast()==null?BigDecimal.ZERO:tickerData.getLast();
            				BigDecimal vol = tickerData.getVol()==null?BigDecimal.ZERO:tickerData.getVol();
            				BigDecimal count = MathUtils.mul(price, vol);
            				btcVol = MathUtils.add(btcVol, count);
            			}
            		}
            		
            		//计算GSET区的交易总量
            		if(systemTradeType.getType().equals(SystemTradeTypeNewEnum.GAVC.getCode())) {
            			TickerData tickerData = redisHelper.getTickerData(systemTradeType.getId());
            			if(tickerData != null) {
            				gsetVol = MathUtils.add(gsetVol, tickerData.getVol());
            			}
            		}
            		
            		//计算ETH区的交易总量
            		if(systemTradeType.getType().equals(SystemTradeTypeNewEnum.ETH.getCode())) {
            			TickerData tickerData = redisHelper.getTickerData(systemTradeType.getId());
            			if(tickerData != null) {
            				BigDecimal price = tickerData.getLast()==null?BigDecimal.ZERO:tickerData.getLast();
            				BigDecimal vol = tickerData.getVol()==null?BigDecimal.ZERO:tickerData.getVol();
            				BigDecimal count = MathUtils.mul(price, vol);
            				ethVol = MathUtils.add(ethVol, count);
            			}
            		}
            		
            		//计算USDT区的交易总量
            		if(systemTradeType.getType().equals(SystemTradeTypeNewEnum.USDT.getCode())) {
            			TickerData tickerData = redisHelper.getTickerData(systemTradeType.getId());
            			if(tickerData != null) {
            				BigDecimal price = tickerData.getLast()==null?BigDecimal.ZERO:tickerData.getLast();
            				BigDecimal vol = tickerData.getVol()==null?BigDecimal.ZERO:tickerData.getVol();
            				BigDecimal count = MathUtils.mul(price, vol);
            				usdtVol = MathUtils.add(usdtVol, count);
            			}
            		}
            	}
            }
            
            JSONObject obj = new JSONObject();
    		obj.put("btcVol", btcVol); 
    		obj.put("usdtVol", usdtVol); 
    		obj.put("ethVol", ethVol); 
    		obj.put("gsetVol", gsetVol); 
	        return ReturnResult.SUCCESS(obj);
    }

    /*
    public static void main(String[] args) {
    	
    	
		BigDecimal btcVol = BigDecimal.ZERO;
		BigDecimal gsetVol = BigDecimal.ZERO;
		BigDecimal ethVol = BigDecimal.ZERO;
		BigDecimal usdtVol = BigDecimal.ZERO;
        for(int i = 0;i<10;i++) {
        	//如果币种没有被禁用
        	btcVol = MathUtils.add(btcVol, new BigDecimal(5));
        }

        System.out.println(btcVol);
	}
	*/
    
    /**
     * 根据币种id查询币种信息
     */
    @ResponseBody
    @ApiOperation("")
	@RequestMapping(value="/v2/get_coin_info",method = {RequestMethod.GET,RequestMethod.POST})
    public ReturnResult coinInfo(@RequestParam(required = true) Integer coinId) 
    {
    		SystemCoinInfo coinInfo = redisHelper.getCoinInfo(coinId, getLanEnum().getCode()+""); 
    		if(coinInfo == null) {
    			return ReturnResult.FAILUER(""); 
    		}
    		TickerData tickerData = pushService.getTickerData(coinId);
    		if(tickerData != null) {
        		coinInfo.setChg(tickerData.getChg());
        		coinInfo.setLastPrice(tickerData.getLast());
    		}
	        return ReturnResult.SUCCESS(coinInfo);
    }
    
    /**
     * 查询可充值币种信息
     */
    @ResponseBody
	@PassToken
    @ApiOperation("")
	@RequestMapping(value="/v2/coin_deposit",method = {RequestMethod.GET,RequestMethod.POST})
    public ReturnResult coinDeposit() 
    {
    	try{
    		List<SystemCoinTypeVO> mapper = ModelMapperUtils.mapper(
                    redisHelper.getCoinTypeIsRechargeList(SystemCoinTypeEnum.COIN.getCode()), SystemCoinTypeVO.class);
            return ReturnResult.SUCCESS(mapper);
		}catch(Exception e){
			logger.error("查询可充值币种信息异常，参数",e); 
		}
    	return ReturnResult.FAILUER(I18NUtils.getString("CommonV2Controller.32")); 
    }
    
    /**
     * 查询可提现币种信息
     */
    @ResponseBody
	@PassToken
    @ApiOperation("")
	@RequestMapping(value="/v2/coin_withdraw",method = {RequestMethod.GET,RequestMethod.POST})
    public ReturnResult coinWithdraw() 
    {
		List<SystemCoinTypeVO> mapper = ModelMapperUtils.mapper(
                redisHelper.getCoinTypeIsWithdrawList(SystemCoinTypeEnum.COIN.getCode()), SystemCoinTypeVO.class);
        return ReturnResult.SUCCESS(mapper);
    }
    
    /**
     * 根据简称查询币种信息
     */
    @ResponseBody
	@PassToken
    @ApiOperation("")
	@RequestMapping(value="/v2/coin_info",method = {RequestMethod.GET,RequestMethod.POST})
    public ReturnResult getCoinInfoByShortName(@RequestParam(required = true) String shortName) 
    {
    		if(StringUtils.isEmpty(shortName)) {
    			return ReturnResult.FAILUER(""); 
    		}
    		SystemCoinType coinTypeShortName = redisHelper.getCoinTypeShortName(shortName.toUpperCase());
    		if(coinTypeShortName == null) {
    			return ReturnResult.FAILUER(""); 
    		}
    		SystemCoinInfo coinInfo = redisHelper.getCoinInfo(coinTypeShortName.getId(), getLanEnum().getCode()+""); 
    		if(coinInfo == null) {
    			logger.error("coinTypeShortName:{},lancode:{} ",coinTypeShortName.getId(),getLanEnum().getCode());
    			return ReturnResult.FAILUER(""); 
    		}
    		coinInfo.setLogo(coinTypeShortName.getWebLogo());
    		TickerData tickerData = pushService.getTickerData(coinTypeShortName.getId());
    		if(tickerData != null) {
        		coinInfo.setChg(tickerData.getChg());
        		coinInfo.setLastPrice(tickerData.getLast());
    		}
	        return ReturnResult.SUCCESS(coinInfo);
    }
    
    /**
     * 查询银行
     */
    @ResponseBody
	@PassToken
    @ApiOperation("")
	@RequestMapping(value="/v2/bank_info",method = {RequestMethod.GET,RequestMethod.POST})
    public ReturnResult getBankInfo() 
    {
    	try{
    		List<FSystemBankinfoWithdraw> withdrawBankList = redisHelper.getWithdrawBankList();
    		return ReturnResult.SUCCESS(withdrawBankList);
		}catch(Exception e){
			logger.error("查询银行异常",e); 
		}
    	return ReturnResult.FAILUER(""); 
    }
    
    /**
     * 获取外部行情接口
     */
    @ResponseBody
	@PassToken
    @ApiOperation("")
	@RequestMapping(value="/v2/currency/ranks",method = {RequestMethod.GET,RequestMethod.POST})
    public ReturnResult ranks() 
    {
    	try {
    		//通过这个接口获取美元对人名币的汇率
	        String url = "http://api.coindog.com/api/v1/currency/ranks"; 
	        OkHttpClient client = new OkHttpClient.Builder()
	                .connectTimeout(10, TimeUnit.SECONDS)
	                .writeTimeout(10, TimeUnit.SECONDS)
	                .readTimeout(10, TimeUnit.SECONDS)
	                .build();
	        Response response = client.newCall(new Request.Builder().url(url).build()).execute();
	        JSONArray jsonObject = JSONArray.parseArray(response.body().string());
        	return ReturnResult.SUCCESS(jsonObject);
    	} catch (Exception e) {
			logger.error(e.getMessage());
			return ReturnResult.FAILUER(""); 
    	}
    }
    
    /**
     * 手机下载链接
     */
    @ResponseBody
	@PassToken
    @ApiOperation("")
	@RequestMapping(value="/v2/common/dowloadUrl",method = {RequestMethod.GET,RequestMethod.POST})
    public ReturnResult dowloadUrl() 
    {
    		String androidUrl = redisHelper.getSystemArgs("AndroidDownloadUrl"); 
    		
    		String iosUrl = redisHelper.getSystemArgs("IosDownloadUrl"); 
    		
    		String webUrl = redisHelper.getSystemArgs("WebDownloadUrl"); 
            
            JSONObject result = new JSONObject();
            result.put("androidUrl", androidUrl); 
            result.put("iosUrl", iosUrl); 
            result.put("webUrl", webUrl); 
	        return ReturnResult.SUCCESS(result);
    }
    
    /**
     * 历史版本
     */
    @ResponseBody
	@PassToken
    @ApiOperation("")
	@RequestMapping(value="/v2/common/historicVersion",method = {RequestMethod.GET,RequestMethod.POST})
    public ReturnResult historicVersion() 
    {
    		List<HistoricVersion> versionList = redisHelper.getHistoricVersionList();
	        return ReturnResult.SUCCESS(versionList);
    }
    
    //获取用户id所在地，区域访问
  	@ResponseBody
	@PassToken
  	@ApiOperation("")
	@RequestMapping(value="/comm/getIp",method = {RequestMethod.GET,RequestMethod.POST})
  	public ReturnResult getIp() {
      	try {
      		String ip = HttpRequestUtils.getIPAddress();
      		String key = "comm_ip_limit_"+ip; 
      		String countStr = redisHelper.get(key);
      		if(!StringUtils.isEmpty(countStr)) {
      			return ReturnResult.SUCCESS(200,"false"); 
      		}
      		
      		Locator locator = Locator.loadFromLocal("/home/17monipdb.datx"); 
      		LocationInfo ipInfo = locator.find(ip);
  			if(ipInfo!=null && !StringUtils.isEmpty(ipInfo.country)) {
  				String country = ipInfo.country;
  				if(!StringUtils.isEmpty(country)&&country.equals("中国")) { 
  					Calendar calendar = Calendar.getInstance();
  					calendar.add(Calendar.DAY_OF_YEAR, 1);
  					calendar.set(Calendar.HOUR_OF_DAY, 0);
  					calendar.set(Calendar.SECOND, 0);
  					calendar.set(Calendar.MINUTE, 0);
  					calendar.set(Calendar.MILLISECOND, 0);
  					Long timeout = ((calendar.getTimeInMillis()-System.currentTimeMillis()) / 1000);
  					
  					redisHelper.set(key, "1" ,timeout.intValue()); 
  					return ReturnResult.SUCCESS(200,"true"); 
  				}else {
  					return ReturnResult.SUCCESS(200,"false"); 
  				}
  			}
  		}catch (IOException e) {
  			logger.error("getIp IOex:{}",e); 
  		}catch (Exception e) {
			logger.error("getIp ex:{}",e); 
  		}
      	return ReturnResult.SUCCESS(200,"false"); 
  	}
  	
  	/**
     * 友情链接
     */
    @ResponseBody
	@PassToken
    @ApiOperation("")
	@RequestMapping(value="/v2/common/friendlink",method = {RequestMethod.GET,RequestMethod.POST})
    public ReturnResult friendlink() 
    {
    	try{
    		 //友情链接
    		List<FFriendLink> ffriendlinks = redisHelper.getFFriendLinkList();
    		
	        return ReturnResult.SUCCESS(ffriendlinks);
		}catch(Exception e){
    		logger.error("/v2/common/friendlink error:{}",e); 
			//logger.error(e.getMessage());();
		}
    	return ReturnResult.FAILUER(""); 
    }
    
  	/**
     *  获取启用的支付方式
     */
    @ResponseBody
	@PassToken
    @ApiOperation("")
	@RequestMapping(value="/v2/common/payment_method",method = {RequestMethod.GET,RequestMethod.POST})
    public ReturnResult paymentMethod() 
    {
    		 List<OtcPayment> allPaymentList = redisHelper.getAllPaymentList();
    		 List<OtcPayment> collect = allPaymentList.stream().filter(p -> p.getStatus() == OtcPaymentStatusEnum.Normal.getCode()).map(e->otcPaymentWithName(e)).collect(Collectors.toList());
	        return ReturnResult.SUCCESS(collect);
    }
    
    private OtcPayment otcPaymentWithName(OtcPayment o) {
      String msg = redisHelper.getMultiLangMsg(o.getNameCode());
      o.setName(msg);
      o.setChineseName(msg);
      return o;
    }
    
}
