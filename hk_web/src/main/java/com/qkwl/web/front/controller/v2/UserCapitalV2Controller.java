package com.qkwl.web.front.controller.v2;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.util.HtmlUtils;

import com.alibaba.fastjson.JSONObject;
import com.hotcoin.coin.utils.AddressValidateUtils;
import com.qkwl.common.Enum.validate.PlatformEnum;
import com.qkwl.common.dto.Enum.BankInfoStatusEnum;
import com.qkwl.common.dto.Enum.DataSourceEnum;
import com.qkwl.common.dto.Enum.SystemCoinStatusEnum;
import com.qkwl.common.dto.Enum.SystemCoinTypeEnum;
import com.qkwl.common.dto.Enum.UserStatusEnum;
import com.qkwl.common.dto.Enum.VirtualCapitalOperationTypeEnum;
import com.qkwl.common.dto.capital.CoinOperationOrderDTO;
import com.qkwl.common.dto.capital.FUserBankinfoDTO;
import com.qkwl.common.dto.capital.FUserVirtualAddressWithdrawDTO;
import com.qkwl.common.dto.capital.UserBankinfoDTO;
import com.qkwl.common.dto.capital.UserVirtualAddressWithdrawDTO;
import com.qkwl.common.dto.coin.SystemCoinSetting;
import com.qkwl.common.dto.coin.SystemCoinType;
import com.qkwl.common.dto.user.FUser;
import com.qkwl.common.dto.wallet.UserCoinWallet;
import com.qkwl.common.framework.redis.RedisHelper;
import com.qkwl.common.framework.validate.ValidationCheckHelper;
import com.qkwl.common.match.MathUtils;
import com.qkwl.common.result.Result;
import com.qkwl.common.rpc.capital.IUserCapitalAccountService;
import com.qkwl.common.rpc.capital.IUserCapitalService;
import com.qkwl.common.rpc.capital.IUserWalletService;
import com.qkwl.common.rpc.user.IUserService;
import com.qkwl.common.util.CoinCommentUtils;
import com.qkwl.common.util.Constant;
import com.qkwl.common.util.HttpRequestUtils;
import com.qkwl.common.util.I18NUtils;
import com.qkwl.common.util.ReturnResult;
import com.qkwl.common.util.SecurityUtil;
import com.qkwl.common.util.Utils;
import com.qkwl.web.front.controller.base.JsonBaseController;

import io.swagger.annotations.ApiOperation;
import springfox.documentation.annotations.ApiIgnore;

@Controller
@ApiIgnore
public class UserCapitalV2Controller extends JsonBaseController {
	private static final Logger logger = LoggerFactory.getLogger(UserCapitalV2Controller.class);
	@Autowired
	private IUserService userService;
	@Autowired
	private RedisHelper redisHelper;
	
	@Autowired
	private IUserCapitalAccountService userCapitalAccountService;
	
    @Autowired
    private IUserWalletService userWalletService;
    
    @Autowired
    private IUserCapitalService userCapitalService;
    
    @Autowired
    private ValidationCheckHelper validationCheckHelper;

	/**
	 * 新增提现地址
	 * @param coinId  币种id
	 * @param withdrawAddr  提现地址
	 * @param password  交易密码
	 * @param remark  备注
	 * @param memo  地址标签
	 * @return Result   
	 */
	@ResponseBody
    @ApiOperation("")
	@RequestMapping(value="/v2/capital/save_withdraw_address",method = {RequestMethod.GET,RequestMethod.POST})
	public ReturnResult addWithdrawAddress(
			@RequestParam(required = true) int coinId,
			@RequestParam(required = true) String withdrawAddr,
			@RequestParam(required = true) String password,
			@RequestParam(required = true) String remark,
			@RequestParam(required = false) String memo
			){
			FUser fuser = getUser();
			fuser = userService.selectUserById(fuser.getFid());
			String ip = getIpAddr();
			//查找币种
			SystemCoinType coinType = redisHelper.getCoinType(coinId);
			if (coinType == null || coinType.getStatus().equals(SystemCoinStatusEnum.ABNORMAL.getCode())) {
				return ReturnResult.FAILUER(I18NUtils.getString("com.trade.error.10000")); 
			}

			if(!CoinCommentUtils.isLegitimateAddress(coinType.getCoinType(), withdrawAddr)) {
				return ReturnResult.FAILUER(I18NUtils.getString("com.validate.error.11017"));
			}
			UserVirtualAddressWithdrawDTO address = new UserVirtualAddressWithdrawDTO();
			address.setFuid(fuser.getFid());
			address.setInit(true);
			address.setFcreatetime(new Date());
			address.setFadderess(withdrawAddr);
			address.setFcoinid(coinType.getId());
			address.setFremark(remark);
			address.setVersion(0);
			address.setIp(ip);
			address.setPassword(Utils.MD5(password));
			address.setPlatform(PlatformEnum.BC);
			address.setMemo(memo);
			Result result = userCapitalAccountService.createCoinAddressWithdraw(address);
			if(result.getCode() == 200){
				return ReturnResult.SUCCESS();
			} else if(result.getCode() > 200 && result.getCode() < 1000){
				return ReturnResult.FAILUER(I18NUtils.getString("common.error" + result.getCode())); 
			} else if(result.getCode() >= 1000 && result.getCode() < 10000){
				return ReturnResult.FAILUER(I18NUtils.getString("user.address.error." + result.getCode())); 
			} else{
				return ReturnResult.FAILUER(I18NUtils.getString("com.error."+result.getCode(), result.getData().toString())); 
			}
	}
	
	/**
	 * 查询提现地址
	 * @param coinId  币种id
	 * @return Result   
	 */
	@ResponseBody
    @ApiOperation("")
	@RequestMapping(value="/v2/capital/list_withdraw_address",method = {RequestMethod.GET,RequestMethod.POST})
	public ReturnResult getWithdrawAddress(	@RequestParam(required = true) int coinId){
			FUser fuser = super.getCurrentUserInfoByToken();
			//查找币种
			SystemCoinType coinType = redisHelper.getCoinType(coinId);
			if (coinType == null || coinType.getStatus().equals(SystemCoinStatusEnum.ABNORMAL.getCode())) {
				return ReturnResult.FAILUER(I18NUtils.getString("com.trade.error.10000")); 
			}
			// 地址
	        List<FUserVirtualAddressWithdrawDTO> withdrawAddress = userCapitalAccountService.listCoinAddressWithdraw(fuser.getFid(), coinType.getId());
	        for (FUserVirtualAddressWithdrawDTO fUserVirtualAddressWithdrawDTO : withdrawAddress) {
	        	fUserVirtualAddressWithdrawDTO.setWebLogo(coinType.getWebLogo());
	        	fUserVirtualAddressWithdrawDTO.setShortName(coinType.getShortName());
			}
	        return ReturnResult.SUCCESS(withdrawAddress);
	}
	
	/**
	 * 删除提现地址
	 * @param id  地址id
	 * @return Result   
	 */
	@ResponseBody
    @ApiOperation("")
	@RequestMapping(value="/v2/capital/remove_withdraw_address",method = {RequestMethod.GET,RequestMethod.POST})
	public ReturnResult detelCoinAddress(
			@RequestParam(required = true) int id) {
			FUser fuser = super.getCurrentUserInfoByToken();

			Result result = userCapitalAccountService.deleteCoinAddressWithdraw(fuser.getFid(), id);
			if(result.getCode() == 200){
				return ReturnResult.SUCCESS();
			} else if(result.getCode() > 200 && result.getCode() < 1000){
				return ReturnResult.FAILUER(I18NUtils.getString("common.error." + result.getCode())); 
			} else if(result.getCode() >= 1000 && result.getCode() < 10000){
				return ReturnResult.FAILUER(I18NUtils.getString("user.address.error." + result.getCode())); 
			} else{
				return ReturnResult.FAILUER(I18NUtils.getString("com.error."+result.getCode())); 
			}
	}
	
	
	/**
     * 获取提币所需参数
     * @param coinId        币种id
     * @return Pagination
     */
    @ResponseBody
    @ApiOperation("")
	@RequestMapping(value="/v2/capital/coin_withdraw_info",method = {RequestMethod.GET,RequestMethod.POST})
    public ReturnResult coinWithdrawInfo(
            @RequestParam(required = true) Integer coinId) {
    	// 币种查找
        SystemCoinType coinType = redisHelper.getCoinType(coinId);
        if (coinType == null || !coinType.getIsWithdraw()
                || coinType.getStatus().equals(SystemCoinStatusEnum.ABNORMAL.getCode())
                || !coinType.getType().equals(SystemCoinTypeEnum.COIN.getCode())) {
            coinType = redisHelper.getCoinTypeIsWithdrawFirst(SystemCoinTypeEnum.COIN.getCode());
            if (coinType == null) {
                return ReturnResult.FAILUER(""); 
            }
        }
        // 用户
        FUser user = getUser();
        // 钱包
        UserCoinWallet userWallet = null;
        if(coinType.getIsSubCoin()) {
        	//如果是从币就去查主币的钱包
        	SystemCoinType coinTypeShortName = redisHelper.getCoinType(Integer.valueOf(coinType.getLinkCoin()));
        	if(coinTypeShortName == null) {
        		return ReturnResult.FAILUER(I18NUtils.getString("common.error.400")); 
        	}
        	userWallet = userWalletService.getUserCoinWallet(user.getFid(), coinTypeShortName.getId());
        }else {
        	userWallet = userWalletService.getUserCoinWallet(user.getFid(), coinType.getId());
        }
        // 地址
        //List<FUserVirtualAddressWithdrawDTO> withdrawAddress = userCapitalAccountService.listCoinAddressWithdraw(user.getFid(), coinType.getId());
        // VIP设置
        SystemCoinSetting withdrawSetting = redisHelper.getCoinSetting(coinType.getId(), user.getLevel());
        JSONObject jsonObject = new JSONObject();

        if(user.getFstatus() == UserStatusEnum.FORBBIN_VALUE || user.getFiscoin() == UserStatusEnum.FORBBIN_VALUE) {
        	jsonObject.put("isWithdraw", false); 
        }else {
        	jsonObject.put("isWithdraw", true); 
        }
        
        BigDecimal todayWithDraw = userCapitalService.getDayWithdrawCoin(user.getFid(), coinId);
        BigDecimal surplusWithDraw = withdrawSetting.getWithdrawDayLimit().subtract(todayWithDraw).compareTo(BigDecimal.ZERO) == -1 ? 
        		BigDecimal.ZERO : withdrawSetting.getWithdrawDayLimit().subtract(todayWithDraw);
        
        
        jsonObject.put("amountAvailable", userWallet == null ? 0 : userWallet.getTotal()); 
        jsonObject.put("isPercentage", withdrawSetting.getIsPercentage()); 
        jsonObject.put("withdrawFee", withdrawSetting.getWithdrawFee()); 
        jsonObject.put("networkFee", coinType.getNetworkFee()); 
        jsonObject.put("withdrawMin", withdrawSetting.getWithdrawMin());
        jsonObject.put("withdrawMax", withdrawSetting.getWithdrawMax()); 
        jsonObject.put("withdrawDayLimit", withdrawSetting.getWithdrawDayLimit());
        //剩余当天提现额度：当天限额-已提额度 > 钱包额度？钱包额度：当天限额-已提额度
        jsonObject.put("withdrawDayAvailable", 
        		userWallet == null ? 0 : 
        		surplusWithDraw.compareTo(userWallet.getTotal()) == 1 ? 
        				userWallet.getTotal() : surplusWithDraw);
        
        return ReturnResult.SUCCESS(jsonObject);    
     }
    
    
    /**
     * 提币地址校验
     * @param address   地址
     * @param coinId   币种id
     * @param memo     地址标签
     */
    @ResponseBody
    @ApiOperation("")
	@RequestMapping(value="/v2/capital/validateAddress",method = {RequestMethod.GET,RequestMethod.POST})
    public ReturnResult validateAddress(
            @RequestParam(required = true) String address,
            @RequestParam(required = true) Integer coinId,
    		@RequestParam(required = false) String memo	){
    	// 获取币种的归属的币种
        SystemCoinType coinType = redisHelper.getCoinTypeByCoinSort(coinId);
        if (coinType == null) {
            return ReturnResult.FAILUER(I18NUtils.getString("UserCapitalV2Controller.33")); 
        }
        if(!AddressValidateUtils.validateAddress(coinType.getShortName(), address)) {
        	//请输入正确的地址
        	return ReturnResult.FAILUER(I18NUtils.getString("com.validate.error.11017"));
        }
        if(!AddressValidateUtils.validateMemo(coinType.getShortName(), memo)) {
        	//请输入正确的地址标签
        	return ReturnResult.FAILUER(I18NUtils.getString("com.validate.error.11023"));
        }
        return ReturnResult.SUCCESS();
    }
    
    /**
     * 虚拟币提现申请
     * @param addressId   地址id
     * @param withdrawAmount   提币金额
     * @param tradePwd   提现密码
     * @param coinId   币种id
     * @param googleCode   谷歌验证码
     * @param phoneCode   手机验证码
     * @param emailCode   邮箱验证码
     * @param btcfeesIndex   btc手续费选择
     * @param memo   地址标签
     * 
     */
    @ResponseBody
    @ApiOperation("")
	@RequestMapping(value="/v2/capital/withdraw",method = {RequestMethod.GET,RequestMethod.POST})
    public ReturnResult withdrawBtcSubmit(
            @RequestParam(required = false) Integer addressId,
            @RequestParam(required = false) String address,
            @RequestParam BigDecimal withdrawAmount,
            @RequestParam String tradePwd,
            @RequestParam Integer coinId,
            @RequestParam(required = false, defaultValue = "0") String googleCode,
            @RequestParam(required = false, defaultValue = "0") String phoneCode,
            @RequestParam(required = false, defaultValue = "0") String emailCode,
            @RequestParam(required = false, defaultValue = "0") Integer btcfeesIndex,
            @RequestParam(required = false) String memo) {
        if (addressId == null && address == null) {
            return ReturnResult.FAILUER(I18NUtils.getString("capital.coin.withdraw.com.1000")); 
        }
        if (withdrawAmount == null) {
            return ReturnResult.FAILUER(I18NUtils.getString("capital.coin.withdraw.com.1001")); 
        }
        withdrawAmount = MathUtils.toScaleNum(withdrawAmount, MathUtils.ENTER_COIN_SCALE);
        if (withdrawAmount.compareTo(BigDecimal.ZERO) <= 0) {
            return ReturnResult.FAILUER(I18NUtils.getString("capital.coin.withdraw.com.1002")); 
        }
        if (StringUtils.isEmpty(tradePwd)) {
            return ReturnResult.FAILUER(I18NUtils.getString("com.error.10116")); 
        }
        // 用户
        FUser user = getUser();
        if(!user.getFhasrealvalidate()) {
        	return ReturnResult.FAILUER(I18NUtils.getString("com.user.security.1011")); 
        }
        // 币信息
        SystemCoinType coinType = redisHelper.getCoinType(coinId);
        if (coinType == null) {
            return ReturnResult.FAILUER(I18NUtils.getString("UserCapitalV2Controller.33")); 
        }
        if (coinType.getStatus().equals(SystemCoinStatusEnum.ABNORMAL.getCode())) {
        	return ReturnResult.FAILUER(I18NUtils.getString("UserCapitalV2Controller.34")); 
        }
        if (!coinType.getIsWithdraw()) {
        	return ReturnResult.FAILUER(I18NUtils.getString("UserCapitalV2Controller.35")); 
		}
        
        Integer walletCoinId = coinType.getId();
        if(coinType.getIsSubCoin()) {
        	try {
        		walletCoinId = Integer.valueOf(coinType.getLinkCoin());
			} catch (Exception e) {
				logger.error("提现异常",e);
				return ReturnResult.FAILUER(I18NUtils.getString("com.error.10117")); 
			}
        }

		//地址判断
		if(!StringUtils.isEmpty(address) && !CoinCommentUtils.isLegitimateAddress(coinType.getCoinType(), address)) {
			return ReturnResult.FAILUER(I18NUtils.getString("com.validate.error.11017"));
		}

        String ip = HttpRequestUtils.getIPAddress();
        // BTC网络手续费
        BigDecimal BTCFees = coinType.getNetworkFee();
        if (coinType.getShortName().equals("BTC")) { 
            if (btcfeesIndex <= 0 || btcfeesIndex >= Constant.BTC_FEES_MAX) {
                BTCFees = Constant.BTC_FEES[0];
            } else {
                BTCFees = Constant.BTC_FEES[btcfeesIndex];
            }
        }
        // 提现
        try {
            tradePwd = Utils.MD5(tradePwd);
        } catch (Exception e) {
            return ReturnResult.FAILUER(I18NUtils.getString("com.error.10117")); 
        }
        if (coinType.getShortName().equals("BTK") && MathUtils.toScaleNum(withdrawAmount, MathUtils.INTEGER_SCALE).compareTo(withdrawAmount) != 0) { 
            return ReturnResult.FAILUER(I18NUtils.getString("com.error.10119")); 
        }
        
        Boolean risk = coinType.getRiskNum() != null && coinType.getRiskNum().compareTo(BigDecimal.ZERO) > 0 && coinType.getRiskNum().compareTo(withdrawAmount) < 0;
        CoinOperationOrderDTO order = new CoinOperationOrderDTO();
        order.setUserId(user.getFid());
        order.setCoinId(coinType.getId());
        order.setCoinName(coinType.getName());
        order.setOperationType(VirtualCapitalOperationTypeEnum.COIN_OUT);
        order.setDataSource(DataSourceEnum.WEB);
        order.setPlatform(PlatformEnum.BC);
        order.setAddressBindId(addressId);
        order.setAddress(address);
        order.setAmount(withdrawAmount);
        order.setTradePass(tradePwd);
        order.setPhoneCode(phoneCode);
        order.setGoogleCode(googleCode);
        order.setEmailCode(emailCode);
        order.setIp(ip);
        order.setRisk(risk);
        order.setNetworkFees(BTCFees);
        order.setMemo(StringUtils.isEmpty(memo) ? null : memo);
        order.setWalletCoinId(walletCoinId);
		Result result = userCapitalService.createCoinOperation(order);
		if (result.getSuccess()) {
			return ReturnResult.SUCCESS(I18NUtils.getString("capital.coin.withdraw.create." + result.getCode().toString()));
		} else if (result.getCode().equals(Result.PARAM)) {
			logger.error("withdrawBtcSubmit {}", result.getMsg());
		} else if (result.getCode() < 10000) {
			return ReturnResult.FAILUER(result.getCode(), I18NUtils.getString("capital.coin.withdraw.create." + result.getCode().toString(), result.getData().toString()));
		} else {
			return ReturnResult.FAILUER(result.getCode(), I18NUtils.getString("com.error." + result.getCode().toString(), result.getData().toString()));
		}
        return ReturnResult.FAILUER(I18NUtils.getString("com.error.10117")); 
    }
    
    /**
     * 验证提现密码
     * @param tradePwd   提现密码
     */
    @ResponseBody
    @ApiOperation("")
	@RequestMapping(value="/v2/capital/check_tradePwd",method = {RequestMethod.GET,RequestMethod.POST})
    public ReturnResult withdrawBtcSubmit(@RequestParam(required = true) String tradePwd) {
		if (StringUtils.isEmpty(tradePwd)) {
			return ReturnResult.FAILUER(I18NUtils.getString("com.error.10116"));
		}
		// 用户
		FUser user = getUser();
		user = userService.selectUserById(user.getFid());
		if (StringUtils.isEmpty(user.getFtradepassword())) {
			return ReturnResult.FAILUER(I18NUtils.getString("com.error.10003"));
		}
		tradePwd = Utils.MD5(tradePwd);
		Result tradePasswordCheck = validationCheckHelper.getTradePasswordCheck(user.getFtradepassword(), tradePwd, getIpAddr());
		if (tradePasswordCheck.getSuccess()) {
			return ReturnResult.SUCCESS(I18NUtils.getString("common.succeed.200"));
		}
		return ReturnResult.FAILUER(tradePasswordCheck.getCode(), I18NUtils.getString("com.error." + tradePasswordCheck.getCode().toString(), tradePasswordCheck.getData().toString()));
    }
    
    
    /**
     * 用户支付方式列表
     * @return
     */
    @ResponseBody
    @ApiOperation("")
	@RequestMapping(value="/v2/capital/list_payment_method",method = {RequestMethod.GET,RequestMethod.POST})
    public ReturnResult paymentMethodList() 
    {
			//如果获取不到用户则返回报错
			FUser user = getUser();
			if (user == null) {
	            return ReturnResult.FAILUER(ReturnResult.FAULURE_USER_NOT_LOGIN,I18NUtils.getString("com.public.error.10001"));
	        }
			
			//查询支付方式
			List<FUserBankinfoDTO> list = userCapitalAccountService.listBankInfo(user.getFid(), null,BankInfoStatusEnum.NORMAL_VALUE);
			if(list == null) {
				return ReturnResult.FAILUER(I18NUtils.getString("com.public.error.10001")); 
			}
			for (FUserBankinfoDTO fUserBankinfoDTO : list) {
				fUserBankinfoDTO.setFbanknumber(fUserBankinfoDTO.getFbanknumber());
				fUserBankinfoDTO.setFrealname(fUserBankinfoDTO.getFrealname());
			}
			
	        return ReturnResult.SUCCESS(list);
    }
    
    /**
	 * 新增支付方式
	 */
	@ResponseBody
	@ApiOperation("")
	@RequestMapping(value="/v2/capital/add_payment_method",method = {RequestMethod.GET,RequestMethod.POST})
	public ReturnResult addPaymentMethod(
			@RequestParam(required = false,defaultValue = "0") Integer type,
			@RequestParam(required = false) String qrcodeImg,
			@RequestParam(required = true) String account,
			@RequestParam(required = false) String address,
			@RequestParam(required = false) String bankName,
			@RequestParam(required = true) String password
			){
			String ip = getIpAddr();
			// 用户
			FUser fuser = getUser();
			//支付密码为空
			if(StringUtils.isEmpty(password)) {
				return ReturnResult.FAILUER(I18NUtils.getString("capital.bank.withdraw.1007")); 
			}
			//账号为空
			if(StringUtils.isEmpty(account)) {
				return ReturnResult.FAILUER(I18NUtils.getString("capital.bank.withdraw.1008")); 
			}
			
			UserBankinfoDTO userBankinfo = new UserBankinfoDTO();
			userBankinfo.setUserId(fuser.getFid());
			userBankinfo.setRealName(fuser.getFrealname());
			userBankinfo.setPassword(Utils.MD5(password));
			userBankinfo.setPlatform(PlatformEnum.BC);
			userBankinfo.setIp(ip);
			userBankinfo.setBankNumber(HtmlUtils.htmlEscape(account));
			userBankinfo.setType(type);
			
			//银行卡类型
			userBankinfo.setBankName(bankName);
			userBankinfo.setAddress(address);
			//支付宝或微信
			userBankinfo.setQrcodeImg(qrcodeImg);	
			
			//新增接口。去掉谷歌验证码和手机验证码，但是加上了交易密码验证
			Result result = userCapitalAccountService.createOrUpdatePayment(userBankinfo);
			if(result.getCode() == 200){
				return ReturnResult.SUCCESS();
			} else if(result.getCode() <= 10000){
				return ReturnResult.FAILUER(I18NUtils.getString("capital.bank.withdraw." + result.getCode())); 
			}else {
				return ReturnResult.FAILUER(I18NUtils.getString("com.error." + result.getCode(), result.getData())); 
			}
	}
	
	/**
	 * 修改支付方式
	 */
	@ResponseBody
	@ApiOperation("")
	@RequestMapping(value="/v2/capital/update_payment_method",method = {RequestMethod.GET,RequestMethod.POST})
	public ReturnResult updatePaymentMethod(
			@RequestParam(required = false) String qrcodeImg,
			@RequestParam(required = true) String account,
			@RequestParam(required = false) String address,
			@RequestParam(required = false) String bankName,
			@RequestParam(required = true) String password,
			@RequestParam(required = true) Integer id
			){
			String ip = getIpAddr();
			// 用户
			FUser fuser = getUser();
			//支付密码为空
			if(StringUtils.isEmpty(password)) {
				//参数错误
				return ReturnResult.FAILUER(I18NUtils.getString("com.public.error.10007")); 
			}
			//账号为空
			if(StringUtils.isEmpty(account)) {
				//参数错误
				return ReturnResult.FAILUER(I18NUtils.getString("com.public.error.10007")); 
			}
			UserBankinfoDTO userBankinfo = new UserBankinfoDTO();
			userBankinfo.setId(id);
			userBankinfo.setUserId(fuser.getFid());
			userBankinfo.setRealName(fuser.getFrealname());
			userBankinfo.setPassword(Utils.MD5(password));
			userBankinfo.setPlatform(PlatformEnum.BC);
			userBankinfo.setIp(ip);
			userBankinfo.setBankNumber(HtmlUtils.htmlEscape(account));
			//银行卡类型
			userBankinfo.setBankName(bankName);
			userBankinfo.setAddress(address);
			//支付宝或微信
			userBankinfo.setQrcodeImg(qrcodeImg);	
			//新增接口。去掉谷歌验证码和手机验证码，但是加上了交易密码验证
			Result result = userCapitalAccountService.createOrUpdatePayment(userBankinfo);
			if(result.getCode() == 200){
				return ReturnResult.SUCCESS();
			} else if(result.getCode() <= 10000){
				return ReturnResult.FAILUER(I18NUtils.getString("capital.bank.withdraw." + result.getCode())); 
			}else {
				return ReturnResult.FAILUER(I18NUtils.getString("com.error." + result.getCode(), result.getData())); 
			}
	}
	
	/**
	 * 删除支付方式
	 */
	@ResponseBody
	@ApiOperation("")
	@RequestMapping(value="/v2/capital/del_payment_method",method = {RequestMethod.GET,RequestMethod.POST})
	public ReturnResult updateOutAddress(Integer bankId) {
			//如果获取不到用户则返回报错
			FUser user = getUser();
			if (user == null) {
	            return ReturnResult.FAILUER(ReturnResult.FAULURE_USER_NOT_LOGIN,I18NUtils.getString("com.public.error.10001"));
	        }
			
			Result result = userCapitalAccountService.deleteBankInfo(user.getFid(), bankId);
			
			if(result.getCode() == 200){
				return ReturnResult.SUCCESS();
			} else if(result.getCode() <= 10000){
				return ReturnResult.FAILUER(I18NUtils.getString("capital.bank.withdraw." + result.getCode())); 
			}else {
				return ReturnResult.FAILUER(I18NUtils.getString("com.error." + result.getCode(), result.getData())); 
			}
	}
	
	/**
	 * 设置默认银行卡
	 */
	@ResponseBody
	@ApiOperation("")
	@RequestMapping(value="/v2/capital/default_payment_method",method = {RequestMethod.GET,RequestMethod.POST})
	public ReturnResult defaultPayment(Integer bankId) {
			//如果获取不到用户则返回报错
			FUser user = getUser();
			if (user == null) {
	            return ReturnResult.FAILUER(ReturnResult.FAULURE_USER_NOT_LOGIN,I18NUtils.getString("com.public.error.10001"));
	        }
			
			Result result = userCapitalAccountService.defaultBankInfo(user.getFid(), bankId);
			
			if(result.getCode() == 200){
				return ReturnResult.SUCCESS();
			} else if(result.getCode() <= 10000){
				return ReturnResult.FAILUER(I18NUtils.getString("capital.bank.withdraw." + result.getCode())); 
			}else {
				return ReturnResult.FAILUER(I18NUtils.getString("com.error." + result.getCode(), result.getData())); 
			}
	}
}
