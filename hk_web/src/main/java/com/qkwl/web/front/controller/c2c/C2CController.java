package com.qkwl.web.front.controller.c2c;


import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.util.HtmlUtils;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageInfo;
import com.qkwl.common.Enum.validate.PlatformEnum;
import com.qkwl.common.dto.Enum.BankInfoStatusEnum;
import com.qkwl.common.dto.Enum.c2c.C2CBusinessTypeEnum;
import com.qkwl.common.dto.Enum.c2c.UserC2CEntrustStatusEnum;
import com.qkwl.common.dto.Enum.c2c.UserC2CEntrustTypeEnum;
import com.qkwl.common.dto.c2c.C2CBusiness;
import com.qkwl.common.dto.c2c.UserC2CEntrust;
import com.qkwl.common.dto.capital.FUserBankinfoDTO;
import com.qkwl.common.dto.capital.UserBankinfoDTO;
import com.qkwl.common.dto.common.DeviceInfo;
import com.qkwl.common.dto.user.FUser;
import com.qkwl.common.dto.user.FUserIdentity;
import com.qkwl.common.exceptions.BCException;
import com.qkwl.common.framework.redis.RedisHelper;
import com.qkwl.common.match.MathUtils;
import com.qkwl.common.result.Result;
import com.qkwl.common.rpc.c2c.C2CService;
import com.qkwl.common.rpc.capital.IUserCapitalAccountService;
import com.qkwl.common.rpc.user.IUserIdentityService;
import com.qkwl.common.util.HttpRequestUtils;
import com.qkwl.common.util.I18NUtils;
import com.qkwl.common.util.ReturnResult;
import com.qkwl.common.util.SecurityUtil;
import com.qkwl.common.util.Utils;
import com.qkwl.web.front.controller.base.JsonBaseController;
import com.qkwl.web.permission.annotation.PassToken;

import io.swagger.annotations.ApiOperation;
import springfox.documentation.annotations.ApiIgnore;

@Controller
@ApiIgnore
public class C2CController extends JsonBaseController {
	private static final Logger logger = LoggerFactory.getLogger(C2CController.class);
	
	@Autowired
	private C2CService c2cService;
	
	@Autowired
	private IUserCapitalAccountService userCapitalAccountService;
	
	@Autowired
    private RedisHelper redisHelper;
	
	@Autowired
	private IUserIdentityService userIdentityService;
	/**
     * 获取商户列表
     */
    @ResponseBody
    @ApiOperation("")
	@RequestMapping(value="/c2c/businessList",method = {RequestMethod.GET,RequestMethod.POST})
    public ReturnResult businessList(Integer coinId) 
    {
    	try{
    		//如果获取不到用户则返回报错
			FUser user = getUser();
			if (user == null) {
	            return ReturnResult.FAILUER(ReturnResult.FAULURE_USER_NOT_LOGIN,I18NUtils.getString("com.public.error.10001"));
	        }
			
			if(coinId == null || coinId == 0) {
				coinId = 9;
			}
			
    		List<C2CBusiness> withdraw = c2cService.selectBusinessByType(C2CBusinessTypeEnum.withdraw.getCode(),coinId);
    		List<C2CBusiness> recharge = c2cService.selectBusinessByType(C2CBusinessTypeEnum.recharge.getCode(),coinId);
    		if(withdraw==null && recharge == null ) {
    			return ReturnResult.FAILUER(""); 
    		}
    		JSONObject jsonObject = new JSONObject();
	        jsonObject.put("withdraw", withdraw); 
	        jsonObject.put("recharge", recharge); 
	        return ReturnResult.SUCCESS(jsonObject);
		}catch(Exception e){
			logger.error(e.getMessage());
		}
    	return ReturnResult.FAILUER(""); 
    }
    
    @ResponseBody
    @ApiOperation("")
	@RequestMapping(value="/user/getUserInfo",method = {RequestMethod.GET,RequestMethod.POST})
    public ReturnResult userInfo() {
    	FUser user = getUser();
		if (user == null) {
            return ReturnResult.FAILUER(ReturnResult.FAULURE_USER_NOT_LOGIN,I18NUtils.getString("com.public.error.10001"));
        }
		
		FUserIdentity identity = userIdentityService.selectByUser(user.getFid());
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("login", false); 
    	if (user != null) {
            jsonObject.put("isTelephoneBind", user.getFistelephonebind()); 
            jsonObject.put("tradePassword", user.getFtradepassword() == null); 
            jsonObject.put("needTradePasswd", redisHelper.getNeedTradePassword(user.getFid())); 
            jsonObject.put("login", true); 
            jsonObject.put("isRealNameVerify", user.getFhasrealvalidate()); 
            jsonObject.put("identity", identity); 
        }
    	return ReturnResult.SUCCESS(jsonObject);
    }
    
    /**
     * 获取用户订单列表
     * @param currentPage
     * @param startTime
     * @param endTime
     * @param type
     * @param status
     * @return
     */
    @ResponseBody
    @ApiOperation("")
	@RequestMapping(value="/c2c/orderList",method = {RequestMethod.GET,RequestMethod.POST})
    public ReturnResult orderList(Integer page,String startTime,String endTime,
    		Integer type,Integer status,Integer coinId) 
    {
			//如果获取不到用户则返回报错
			FUser user = getUser();
			if (user == null) {
	            return ReturnResult.FAILUER(ReturnResult.FAULURE_USER_NOT_LOGIN,I18NUtils.getString("com.public.error.10001"));
	        }
			
			if(coinId == null || coinId == 0) {
				coinId = 9;
			}
			UserC2CEntrust param = new UserC2CEntrust();
			param.setStartTime(startTime);
			param.setEndTime(endTime);
			param.setType(type);
			param.setStatus(status);
			param.setUserId(user.getFid());
			param.setCoinId(coinId);
			PageInfo<UserC2CEntrust> info = c2cService.selectList(param, page, 10);
			for (UserC2CEntrust entrust : info.getList()) {
				entrust.setStatusString(I18NUtils.getString("UserC2CEntrustStatusEnum."+entrust.getStatus()));
				entrust.setPhone(SecurityUtil.getStr(entrust.getPhone()));
				entrust.setBankCode(entrust.getBankCode());
			}
	        return ReturnResult.SUCCESS(info);
    }
    
    
    /**
     * 生成充值提现订单
     * @return
     * @throws BCException 
     */
    @ResponseBody
    @ApiOperation("")
	@RequestMapping(value="/c2c/order",method = {RequestMethod.GET,RequestMethod.POST})
    public ReturnResult createOrder(BigDecimal amount,Integer type,
    		Integer businessId,Integer coinId) throws BCException 
    {
			String ip = HttpRequestUtils.getIPAddress();
			
			//如果获取不到用户则返回报错
			FUser user = getUser();
			if(user == null) {
				return ReturnResult.FAILUER(ReturnResult.FAULURE_USER_NOT_LOGIN,I18NUtils.getString("com.public.error.10001"));
			}
			
			if(coinId == null || coinId == 0) {
				coinId = 9;
			}
			
			String key = "c2c_cancel_limit_" + user.getFid(); 
			
			String countStr = redisHelper.get(key);
			int fid = user.getFid();
	        if(StringUtils.isNotEmpty(countStr)) {
	        	int count = Integer.parseInt(countStr);
	            if(count >= 3) {
					return ReturnResult.FAILUER(I18NUtils.getString("C2CController.17"));
	            }
	        }
			
			DeviceInfo deviceInfo = getDeviceInfo();
			int platform = deviceInfo.getPlatform();
			
			UserC2CEntrust param = new UserC2CEntrust();
			param.setUserId(user.getFid());
			param.setType(type);
			param.setAmount(amount);
			param.setBusinessId(businessId);
			param.setIp(ip);
			param.setCoinId(coinId);
			param.setPlatform(platform);
			
			//生成订单
			Result result = c2cService.createEntrust(param);
			if (result.getSuccess()) {
                return ReturnResult.SUCCESS(result.getData());
            } else if (result.getCode().equals(Result.PARAM)) {
                logger.error("createOrder is param userId:{} error:{} ", fid, result.getMsg());
            } else if (result.getCode() >= 10000) {
				return ReturnResult.FAILUER(result.getCode(), I18NUtils.getString("com.error." + result.getCode().toString(), result.getData().toString()));
            } else {
                return ReturnResult.FAILUER(result.getCode(), I18NUtils.getString("c2c.entrust.error." + result.getCode().toString(), result.getData().toString()));
            }
		return ReturnResult.FAILUER(I18NUtils.getString("C2CController.21")); 
    }
    
    /**
     * 订单详情
     * @return
     */
    @ResponseBody
    @ApiOperation("")
	@RequestMapping(value="/c2c/orderDetail",method = {RequestMethod.GET,RequestMethod.POST})
    public ReturnResult orderDetail(Integer orderId) 
    {
			if(orderId == null ) {
				return ReturnResult.FAILUER(I18NUtils.getString("com.public.error.10001")); 
			}
			
			//如果获取不到用户则返回报错
			FUser user = getUser();
			if (user == null) {
	            return ReturnResult.FAILUER(ReturnResult.FAULURE_USER_NOT_LOGIN,I18NUtils.getString("com.public.error.10001"));
	        }
			
			//查询对应的订单，如果不存在则报错
			UserC2CEntrust userC2CEntrust = c2cService.selectOrderById(orderId);
			if(userC2CEntrust == null) {
				return ReturnResult.FAILUER(I18NUtils.getString("C2CController.90")); 
			}
			if (!user.getFid().equals(userC2CEntrust.getUserId())) {
				return ReturnResult.FAILUER(I18NUtils.getString("C2CController.90"));
			}
			
			String bankAccount = userC2CEntrust.getBankAccount(); 
			String bankCode = userC2CEntrust.getBankCode();
			String phone = userC2CEntrust.getPhone();
			String bank = userC2CEntrust.getBank();
			if(userC2CEntrust.getType().equals(UserC2CEntrustTypeEnum.withdraw.getCode())) {
				if(StringUtils.isNotEmpty(bankAccount)) {
					userC2CEntrust.setBankAccount("*******"); 
				}
				
				if(StringUtils.isNotEmpty(bankCode)) {
					userC2CEntrust.setBankCode("**************"); 
				}
				
				if(StringUtils.isNotEmpty(phone)) {
					userC2CEntrust.setPhone("*******"); 
				}
				
				if(StringUtils.isNotEmpty(bank)) {
					userC2CEntrust.setBank(Utils.replaceAction(bank));
				}
			}
			
	        return ReturnResult.SUCCESS(userC2CEntrust);
    }
    
    /**
     * 系统参数配置
     * @return
     */
    @ResponseBody
    @ApiOperation("")
	@RequestMapping(value="/c2c/setting",method = {RequestMethod.GET,RequestMethod.POST})
    public ReturnResult setting() 
    {
			//如果获取不到用户则返回报错
			FUser user = getUser();
			if (user == null) {
	            return ReturnResult.FAILUER(ReturnResult.FAULURE_USER_NOT_LOGIN,I18NUtils.getString("com.public.error.10001"));
	        }
			
			Map<String, String> param = c2cService.getParam();
			JSONObject obj = new JSONObject();
			obj.put("minBuyAmount", param.get("minBuyAmount"));  //$NON-NLS-2$
			obj.put("buyPrice", param.get("buyPrice"));  //$NON-NLS-2$
			obj.put("sellPrice", param.get("sellPrice"));  //$NON-NLS-2$
			obj.put("buyExpireTime", param.get("buyExpireTime"));  //$NON-NLS-2$
			obj.put("minSellAmount", param.get("minSellAmount"));  //$NON-NLS-2$
			obj.put("digit", param.get("digit"));  //$NON-NLS-2$
			obj.put("isOpenGSET", param.get("isOpenGSET"));  //$NON-NLS-2$
			obj.put("isOpenUSDT", param.get("isOpenUSDT"));  //$NON-NLS-2$
			obj.put("isUsdtFixedPrice", param.get("isUSDTFixedPrice"));  //$NON-NLS-2$
			obj.put("minUsdtSellAmount", param.get("minUsdtSellAmount"));  //$NON-NLS-2$
			obj.put("minUsdtBuyAmount", param.get("minUsdtBuyAmount"));  //$NON-NLS-2$
			
			//获取usdt最新价格
			String usdtSellPrice = param.get("usdtSellPrice"); 
			String usdtBuyPrice = param.get("usdtBuyPrice"); 
			String usdtPriceStr = getCNYValue();
			BigDecimal usdtPrice = new BigDecimal(usdtPriceStr);
			BigDecimal addPrice = new BigDecimal(usdtBuyPrice);
			BigDecimal subPrice = new BigDecimal(usdtSellPrice);
			
			//1、开启 0、关闭
			//开启固定价格
			if(param.get("isUSDTFixedPrice").equals("1")) {  //$NON-NLS-2$
				obj.put("usdtSellPrice", param.get("usdtSellFixedPrice"));  //$NON-NLS-2$
				obj.put("usdtBuyPrice", param.get("usdtBuyFixedPrice"));  //$NON-NLS-2$
			}else {
				obj.put("usdtSellPrice", MathUtils.sub(usdtPrice, subPrice).stripTrailingZeros()); 
				obj.put("usdtBuyPrice", MathUtils.add(usdtPrice, addPrice).stripTrailingZeros()); 
			}
			
			UserC2CEntrust record = new UserC2CEntrust();
			record.setUserId(user.getFid());
			List<Integer> statusList = new ArrayList<>();
			statusList.add(UserC2CEntrustStatusEnum.processing.getCode());
			statusList.add(UserC2CEntrustStatusEnum.wait.getCode());
			record.setStatusList(statusList);
			int count = c2cService.getEntrustCount(record);
			obj.put("count", count); 
	        return ReturnResult.SUCCESS(obj);
    }
    
    /**
     * 系统参数配置
     * @return
     */
    @PassToken
    @ResponseBody
    @ApiOperation("")
	@RequestMapping(value="/c2c/referencePrice",method = {RequestMethod.GET,RequestMethod.POST})
    public ReturnResult referencePrice() 
    {
			Map<String, String> param = c2cService.getParam();
			JSONObject obj = new JSONObject();
			obj.put("buyPrice", param.get("buyPrice"));  //$NON-NLS-2$
			
			//获取usdt最新价格
			String usdtBuyPrice = param.get("usdtBuyPrice"); 
			String usdtPriceStr = getCNYValue();
			BigDecimal usdtPrice = new BigDecimal(usdtPriceStr);
			BigDecimal addPrice = new BigDecimal(usdtBuyPrice);
			
			//1、开启 0、关闭
			//开启固定价格
			if(param.get("isUSDTFixedPrice").equals("1")) {  //$NON-NLS-2$
				obj.put("usdtBuyPrice", param.get("usdtBuyFixedPrice"));  //$NON-NLS-2$
			}else {
				obj.put("usdtBuyPrice", MathUtils.add(usdtPrice, addPrice).stripTrailingZeros()); 
			}
	        return ReturnResult.SUCCESS(obj);
    }
    
    /**
     * 用户绑定银行卡列表
     * @return
     */
    @ResponseBody
    @ApiOperation("")
	@RequestMapping(value="/c2c/userBanklist",method = {RequestMethod.GET,RequestMethod.POST})
    public ReturnResult userBanklist() 
    {
			//如果获取不到用户则返回报错
			FUser user = getUser();
			if (user == null) {
	            return ReturnResult.FAILUER(ReturnResult.FAULURE_USER_NOT_LOGIN,I18NUtils.getString("com.public.error.10001"));
	        }
			
			//查询
			List<FUserBankinfoDTO> list = userCapitalAccountService.listBankInfo(user.getFid(), 1,BankInfoStatusEnum.NORMAL_VALUE);
			if(list == null) {
				return ReturnResult.FAILUER(I18NUtils.getString("com.public.error.10001")); 
			}
			
	        return ReturnResult.SUCCESS(list);
    }
    
    /**
	 * 新增银行卡
	 */
	@ResponseBody
	@ApiOperation("")
	@RequestMapping(value="/c2c/save_bankinfo",method = {RequestMethod.GET,RequestMethod.POST})
	public ReturnResult updateOutAddress(
			@RequestParam(required = true) String account,
			@RequestParam(required = true) String address,
			@RequestParam(required = false) String prov,
			@RequestParam(required = false) String city,
			@RequestParam(required = false) String dist,
			@RequestParam(required = true) String realName,
			@RequestParam(required = true) Integer bankId,
			@RequestParam(required = true) String password) throws Exception {
			String ip = getIpAddr();
			// 用户
			FUser fuser = getUser();

			UserBankinfoDTO userBankinfo = new UserBankinfoDTO();
			userBankinfo.setUserId(fuser.getFid());
			userBankinfo.setSystemBankId(bankId);
			userBankinfo.setRealName(realName);
			userBankinfo.setBankNumber(HtmlUtils.htmlEscape(account));
			userBankinfo.setProv(prov);
			userBankinfo.setCity(city);
			userBankinfo.setDist(dist);
			userBankinfo.setAddress(address);
			userBankinfo.setPlatform(PlatformEnum.BC);
			userBankinfo.setPassword(Utils.MD5(password));
			userBankinfo.setType(1);
			userBankinfo.setIp(ip);

			//新增接口。去掉谷歌验证码和手机验证码，但是加上了交易密码验证
			Result result = userCapitalAccountService.createOrUpdateBankInfoV1(userBankinfo);
			if(result.getCode() == 200){
				return ReturnResult.SUCCESS();
			} else if(result.getCode() > 200 && result.getCode() < 1000){
				return ReturnResult.FAILUER(I18NUtils.getString("common.error." + result.getCode())); 
			} else if(result.getCode() >= 1000 && result.getCode() < 10000){
				if(result.getCode().equals(1003)) {
					 return ReturnResult.FAILUER(result.getCode(), I18NUtils.getString("c2c.entrust.error." + result.getCode().toString(), result.getData().toString())); 
				}
				return ReturnResult.FAILUER(I18NUtils.getString("capital.bank.withdraw." + result.getCode())); 
			} else{
				return ReturnResult.FAILUER(I18NUtils.getString("com.error." + result.getCode(), result.getData())); 
			}
	}
	
	/**
	 * 修改银行卡
	 */
	@ResponseBody
	@ApiOperation("")
	@RequestMapping(value="/c2c/update_bankinfo",method = {RequestMethod.GET,RequestMethod.POST})
	public ReturnResult updateBankinfo(
			@RequestParam(required = true) Integer id,
			@RequestParam(required = true) String account,
			@RequestParam(required = true) String address,
			@RequestParam(required = false) String prov,
			@RequestParam(required = false) String city,
			@RequestParam(required = false) String dist,
			@RequestParam(required = true) String realName,
			@RequestParam(required = true) Integer bankId,
			@RequestParam(required = true) String password) throws Exception {
			String ip = getIpAddr();
			// 用户
			FUser fuser = getUser();

			UserBankinfoDTO userBankinfo = new UserBankinfoDTO();
			userBankinfo.setUserId(fuser.getFid());
			userBankinfo.setSystemBankId(bankId);
			userBankinfo.setRealName(realName);
			userBankinfo.setBankNumber(HtmlUtils.htmlEscape(account));
			userBankinfo.setProv(prov);
			userBankinfo.setCity(city);
			userBankinfo.setDist(dist);
			userBankinfo.setAddress(address);
			userBankinfo.setPlatform(PlatformEnum.BC);
			userBankinfo.setPassword(Utils.MD5(password));
			userBankinfo.setType(1);
			userBankinfo.setIp(ip);
			userBankinfo.setId(id);

			//新增接口。去掉谷歌验证码和手机验证码，但是加上了交易密码验证
			Result result = userCapitalAccountService.createOrUpdateBankInfoV1(userBankinfo);
			if(result.getCode() == 200){
				return ReturnResult.SUCCESS();
			} else if(result.getCode() > 200 && result.getCode() < 1000){
				return ReturnResult.FAILUER(I18NUtils.getString("common.error." + result.getCode())); 
			} else if(result.getCode() >= 1000 && result.getCode() < 10000){
				if(result.getCode().equals(1003)) {
					 return ReturnResult.FAILUER(result.getCode(), I18NUtils.getString("c2c.entrust.error." + result.getCode().toString(), result.getData().toString())); 
				}
				return ReturnResult.FAILUER(I18NUtils.getString("capital.bank.withdraw." + result.getCode())); 
			} else{
				return ReturnResult.FAILUER(I18NUtils.getString("com.error." + result.getCode(), result.getData())); 
			}
	}
	
	/**
	 * 删除银行卡银行卡
	 */
	@ResponseBody
	@ApiOperation("")
	@RequestMapping(value="/c2c/del_bankinfo",method = {RequestMethod.GET,RequestMethod.POST})
	public ReturnResult updateOutAddress(Integer bankId) {
		//如果获取不到用户则返回报错
		FUser user = getUser();
		if (user == null) {
            return ReturnResult.FAILUER(ReturnResult.FAULURE_USER_NOT_LOGIN,I18NUtils.getString("com.public.error.10001"));
        }
		
		Result result = userCapitalAccountService.deleteBankInfo(user.getFid(), bankId);
		
		if(result.getCode() == 200){
			return ReturnResult.SUCCESS();
		} else if(result.getCode() > 200 && result.getCode() < 1000){
			return ReturnResult.FAILUER(I18NUtils.getString("common.error." + result.getCode())); 
		} else if(result.getCode() >= 1000 && result.getCode() < 10000){
			return ReturnResult.FAILUER(I18NUtils.getString("capital.bank.withdraw." + result.getCode())); 
		} else{
			return ReturnResult.FAILUER(I18NUtils.getString("com.error." + result.getCode(), result.getData())); 
		}
	}
	
	
	/**
	 * 设置默认银行卡
	 */
	@ResponseBody
	@ApiOperation("")
	@RequestMapping(value="/c2c/default_bankinfo",method = {RequestMethod.GET,RequestMethod.POST})
	public ReturnResult default_bankinfo(Integer bankId) {
		//如果获取不到用户则返回报错
		FUser user = getUser();
		if (user == null) {
            return ReturnResult.FAILUER(ReturnResult.FAULURE_USER_NOT_LOGIN,I18NUtils.getString("com.public.error.10001"));
        }
		
		Result result = userCapitalAccountService.defaultBankInfo(user.getFid(), bankId);
		
		if(result.getCode() == 200){
			return ReturnResult.SUCCESS();
		} else if(result.getCode() > 200 && result.getCode() < 1000){
			return ReturnResult.FAILUER(I18NUtils.getString("common.error." + result.getCode())); 
		} else if(result.getCode() >= 1000 && result.getCode() < 10000){
			return ReturnResult.FAILUER(I18NUtils.getString("capital.bank.withdraw." + result.getCode())); 
		} else{
			return ReturnResult.FAILUER(I18NUtils.getString("com.error." + result.getCode(), result.getData())); 
		}
	}
	
	
	/**
	 * 修改订单状态
	 */
	@ResponseBody
	@ApiOperation("")
	@RequestMapping(value="/c2c/updateOrder",method = {RequestMethod.GET,RequestMethod.POST})
	public ReturnResult updateOrder(Integer orderId) {
		//如果获取不到用户则返回报错
		FUser user = getUser();
		if (user == null) {
            return ReturnResult.FAILUER(ReturnResult.FAULURE_USER_NOT_LOGIN,I18NUtils.getString("com.public.error.10001"));
        }
		UserC2CEntrust userC2CEntrust = c2cService.selectOrderById(orderId);
		if(userC2CEntrust == null) {
			return ReturnResult.FAILUER(I18NUtils.getString("C2CController.90")); 
		}
		if (!user.getFid().equals(userC2CEntrust.getUserId())) {
			return ReturnResult.FAILUER(I18NUtils.getString("C2CController.90"));
		}
		boolean result = c2cService.updateEntrust(orderId);
		if(result) {
			return ReturnResult.SUCCESS();
		}else {
			return ReturnResult.FAILUER(I18NUtils.getString("C2CController.88")); 
		}
	}
	
	
	/**
	 * 取消订单
	 * @throws BCException 
	 */
	@ResponseBody
	@ApiOperation("")
	@RequestMapping(value="/c2c/cancelOrder",method = {RequestMethod.GET,RequestMethod.POST})
	public ReturnResult cancelOrder(Integer orderId) throws BCException {
		//如果获取不到用户则返回报错
		FUser user = getUser();
		if (user == null) {
            return ReturnResult.FAILUER(ReturnResult.FAULURE_USER_NOT_LOGIN,I18NUtils.getString("com.public.error.10001"));
        }
		UserC2CEntrust userC2CEntrust = c2cService.selectOrderById(orderId);
		if(userC2CEntrust == null) {
			return ReturnResult.FAILUER(I18NUtils.getString("C2CController.90")); 
		}
		if (!user.getFid().equals(userC2CEntrust.getUserId())) {
			return ReturnResult.FAILUER(I18NUtils.getString("C2CController.90"));
		}
		String key = "c2c_cancel_limit_" + user.getFid(); 
		
		boolean result = c2cService.cancelEntrust(orderId);
		if(result) {
			//撤销过三次订单则需要提示用户不准撤销订单了。
			redisHelper.getIncrByKey(key,1800);
			return ReturnResult.SUCCESS();
		}else {
			return ReturnResult.FAILUER(I18NUtils.getString("C2CController.91")); 
		}
	}
}
