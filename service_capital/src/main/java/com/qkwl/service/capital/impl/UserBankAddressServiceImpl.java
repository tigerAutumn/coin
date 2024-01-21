package com.qkwl.service.capital.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.hotcoin.coin.utils.AddressValidateUtils;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.qkwl.common.Enum.validate.BusinessTypeEnum;
import com.qkwl.common.Enum.validate.LocaleEnum;
import com.qkwl.common.Enum.validate.PlatformEnum;
import com.qkwl.common.dto.Enum.BankInfoStatusEnum;
import com.qkwl.common.dto.Enum.LogUserActionEnum;
import com.qkwl.common.dto.Enum.SystemCoinSortEnum;
import com.qkwl.common.dto.Enum.SystemCoinStatusEnum;
import com.qkwl.common.dto.Enum.UserBankInfoDefaultEnum;
import com.qkwl.common.dto.Enum.WithdrawBankTypeEnum;
import com.qkwl.common.dto.Enum.c2c.UserC2CEntrustStatusEnum;
import com.qkwl.common.dto.Enum.c2c.UserC2CEntrustTypeEnum;
import com.qkwl.common.dto.Enum.otc.OtcPaymentStatusEnum;
import com.qkwl.common.dto.Enum.otc.OtcPaymentTypeEnum;
import com.qkwl.common.dto.capital.FUserBankinfoDTO;
import com.qkwl.common.dto.capital.FUserVirtualAddressDTO;
import com.qkwl.common.dto.capital.FUserVirtualAddressWithdrawDTO;
import com.qkwl.common.dto.capital.UserBankinfoDTO;
import com.qkwl.common.dto.capital.UserVirtualAddressWithdrawDTO;
import com.qkwl.common.dto.coin.SystemCoinType;
import com.qkwl.common.dto.otc.OtcPayment;
import com.qkwl.common.dto.system.FSystemBankinfoWithdraw;
import com.qkwl.common.dto.user.FUser;
import com.qkwl.common.framework.pre.PreValidationHelper;
import com.qkwl.common.framework.redis.RedisHelper;
import com.qkwl.common.framework.validate.ValidateHelper;
import com.qkwl.common.framework.validate.ValidationCheckHelper;
import com.qkwl.common.result.Result;
import com.qkwl.common.rpc.capital.IUserCapitalAccountService;
import com.qkwl.common.util.Constant;
import com.qkwl.common.util.I18NUtils;
import com.qkwl.common.util.PojoConvertUtil;
import com.qkwl.common.util.Utils;
import com.qkwl.service.capital.dao.FPoolMapper;
import com.qkwl.service.capital.dao.FUserBankinfoMapper;
import com.qkwl.service.capital.dao.FUserMapper;
import com.qkwl.service.capital.dao.FUserVirtualAddressMapper;
import com.qkwl.service.capital.dao.FUserVirtualAddressWithdrawMapper;
import com.qkwl.service.capital.dao.OtcAdvertMapper;
import com.qkwl.service.capital.dao.c2c.UserC2CEntrustMapper;
import com.qkwl.service.capital.model.FPoolDO;
import com.qkwl.service.capital.model.FUserBankinfoDO;
import com.qkwl.service.capital.model.FUserVirtualAddressDO;
import com.qkwl.service.capital.model.FUserVirtualAddressWithdrawDO;
import com.qkwl.service.capital.tx.CoinCapitalServiceTx;
import com.qkwl.service.capital.util.MQSendUtils;
import com.qkwl.service.common.mapper.UserCommonMapper;

/**
 * 用户银行卡、地址接口实现
 */
@Service("userBandAddressService")
public class UserBankAddressServiceImpl implements IUserCapitalAccountService {

    private static final Logger logger = LoggerFactory.getLogger(UserBankAddressServiceImpl.class);

    @Autowired
    private FUserBankinfoMapper userBankInfoMapper;
    @Autowired
    private FUserVirtualAddressWithdrawMapper userVirtualAddressWithdrawMapper;
    @Autowired
    private FUserVirtualAddressMapper userVirtualAddressMapper;
    @Autowired
    private FPoolMapper poolMapper;
    @Autowired
    private CoinCapitalServiceTx coinCapitalServiceTx;
    @Autowired
    private MQSendUtils mqSendUtils;
    @Autowired
    private RedisHelper redisHelper;
    @Autowired
    private UserCommonMapper userCommonMapper;
    @Autowired
    private PreValidationHelper preValidationHelper;
    @Autowired
    private ValidationCheckHelper validationCheckHelper;
    
    @Autowired
    private OtcAdvertMapper otcAdvertMapper;
    @Autowired
    private UserC2CEntrustMapper userC2CEntrustMapper;
    @Autowired
	private ValidateHelper validateHelper;
    @Autowired
    private FUserMapper userMapper;
    
    
    
    /**
    * 银行卡信息的参数验证
    * @param userBankinfo 银行信息DTO
    * @return Result   返回结果<br/>
    * 200 : 成功
    * 1003: 您没有绑定手机或谷歌验证，请去<a href\='/user/security.html'>安全中心</a>绑定手机或谷歌验证后提现！<br/>
    * 1004: 请先完成实名认证！<br/>
    * 1005: 银行卡账号名必须与您的实名认证姓名一致！<br/>
    * 1006: 您已绑定该银行卡，请勿重复绑定！<br/>
    */
    private Result validateParam(UserBankinfoDTO userBankinfo){
        if (userBankinfo.getUserId() == null) {
            return Result.param("userId is null");
        }
        if (userBankinfo.getBankNumber() == null) {
            return Result.param("bankNumber is null");
        }
        if (userBankinfo.getRealName() == null) {
            return Result.param("realName is null");
        }
        if (userBankinfo.getProv() == null) {
            return Result.param("prov is null");
        }
        if (userBankinfo.getCity() == null) {
            return Result.param("city is null");
        }
        if (userBankinfo.getAddress() == null) {
            return Result.param("address is null");
        }
        if (userBankinfo.getType() == null) {
            return Result.param("type is null");
        }
        if (userBankinfo.getIp() == null) {
            return Result.param("ip is null");
        }

        FUser user = userCommonMapper.selectOneById(userBankinfo.getUserId());
        if(user == null){
            return Result.param("find user fall");
        }
        // 绑定手机或者谷歌
        if (!user.getFgooglebind() && !user.getFistelephonebind()) {
            return Result.failure(1003,"您没有绑定手机或谷歌验证，请去<a href\\='/user/security.html'>安全中心</a>绑定手机或谷歌验证后提现！");
        }
        // 判断实名
        if(!user.getFhasrealvalidate()){
            return Result.failure(1004, "请先完成实名认证！");
        }

        if (!userBankinfo.getRealName().equals(user.getFrealname())) {
            return Result.failure(1005, "银行卡账号名必须与您的实名认证姓名一致！");
        }
        // 验证手机验证码，谷歌验证码
        Result checkResult = validationCheckHelper.getChangeCheck(user, userBankinfo.getPhoneCode(),
                BusinessTypeEnum.SMS_CNY_WITHDRAW.getCode(), userBankinfo.getTotpCode(), userBankinfo.getIp(), userBankinfo.getPlatform().getCode());
        if (!checkResult.getSuccess()) {
            return checkResult;
        }
        //如果有主键id,表示为修改银行卡
        if(userBankinfo.getId() != null && userBankinfo.getId() > 0){
            return Result.success();
        }

        Map<String,Object> param = new HashMap<>();
        param.put("banknumber",userBankinfo.getBankNumber());
        param.put("init",true);

        //判断银行卡是否存在
        if(userBankInfoMapper.getBankInfoByCount(param) > 0){
            return Result.failure(1006, "您已绑定该银行卡，请勿重复绑定！");
        }
        return Result.success();
    }

    /**
     * 添加或修改提现银行卡
     * @param userBankinfo 银行信息DTO
     * @return Result   返回结果<br/>
     * 200 : 成功
     * 1000: 提现银行卡未找到！<br/>
     * 1001: 修改失败！<br/>
     * 1002: 新增失败！<br/>
     * 1003: 您没有绑定手机或谷歌验证，请去<a href\='/user/security.html'>安全中心</a>绑定手机或谷歌验证后提现！<br/>
     * 1004: 请先完成实名认证！<br/>
     * 1005: 银行卡账号名必须与您的实名认证姓名一致！<br/>
     * 1006: 您已绑定该银行卡，请勿重复绑定！<br/>
    */
    @Override
    public Result createOrUpdateBankInfo(UserBankinfoDTO userBankinfo) {
        //参数验证，失败跳出
        Result result = validateParam(userBankinfo);
        if(!result.getSuccess()){
            return result;
        }

        String backName = "Alipay";
        if(userBankinfo.getType().equals(WithdrawBankTypeEnum.Bank.getCode())){
            FSystemBankinfoWithdraw withdraw = redisHelper.getWithdrawBank(userBankinfo.getSystemBankId());
            if (withdraw == null) {
                return Result.failure(1000, "提现账号未找到！");
            }
            backName = withdraw.getFcnname();
        }

        //新增还是修改
        boolean isUpdate = false;
        FUserBankinfoDO bankinfo = null;
        //判断组件id是否存在，存在查询记
        if(userBankinfo.getId() != null && userBankinfo.getId() > 0){
            isUpdate = true;
            bankinfo = userBankInfoMapper.selectByPrimaryKey(userBankinfo.getId());
        }
        if(bankinfo == null){
            bankinfo = new FUserBankinfoDO();
            bankinfo.setFuid(userBankinfo.getUserId());
            bankinfo.setFname(backName);
            bankinfo.setFbanknumber(userBankinfo.getBankNumber());
            bankinfo.setFbanktype(userBankinfo.getSystemBankId());
            bankinfo.setFrealname(userBankinfo.getRealName());
            bankinfo.setFtype(userBankinfo.getType());
            bankinfo.setInit(true);
            bankinfo.setFstatus(BankInfoStatusEnum.NORMAL_VALUE);
            bankinfo.setFcreatetime(new Date());
            bankinfo.setVersion(0);
        }
        bankinfo.setFprov(userBankinfo.getProv());
        bankinfo.setFcity(userBankinfo.getCity());
        bankinfo.setFdist(userBankinfo.getDist());
        bankinfo.setFaddress(userBankinfo.getAddress());

        //判断是新增还是修改
        if(isUpdate){
            if (userBankInfoMapper.updateByPrimaryKey(bankinfo) <= 0) {
                return Result.failure(1001,"修改失败！");
            }
            mqSendUtils.SendUserAction(userBankinfo.getUserId(), LogUserActionEnum.MODIFY_BANK, userBankinfo.getIp());
            return Result.success("修改成功");
        } else{
            if (userBankInfoMapper.insert(bankinfo) <= 0) {
                return Result.failure(1002,"新增失败！");
            }
            // MQ_USER_ACTION
            mqSendUtils.SendUserAction(userBankinfo.getUserId(), LogUserActionEnum.ADD_BANK, userBankinfo.getIp());
            return Result.success("新增成功");
        }
    }

    /**
     * 删除提现银行卡或支付宝
     * @param userId 用户id
     * @param bankId 提现银行卡id
     * @return Result   返回结果<br/>
     * 200 : 删除成功
     * capital.bank.withdraw.1000=提現賬號未找到！
     * capital.bank.withdraw.1010=userId为空
     * capital.bank.withdraw.1014=支付方式id为空
     */
    @Override
    public Result deleteBankInfo(Integer userId, Integer bankId) {
    	try {
    		if(userId == null){
            	return Result.failure(1010, "userId为空");
            }
            if(bankId == null){
            	return Result.failure(1014, "支付方式id为空");
            }
            FUserBankinfoDO userBankinfo = userBankInfoMapper.selectByPrimaryKey(bankId);
            if (userBankinfo == null || !userBankinfo.getFuid().equals(userId)) {
                return Result.failure(1000, "记录不存在");
            }
            if(userBankinfo.getIsDefault() == UserBankInfoDefaultEnum.TRUE.getCode()) {
            	return Result.failure(1022, "默认支付方式不允许删除");
            }
            
            //如果还有委完成的c2c订单
            ArrayList<Integer> arrayList = new ArrayList<>();
        	arrayList.add(UserC2CEntrustStatusEnum.processing.getCode());
        	arrayList.add(UserC2CEntrustStatusEnum.wait.getCode());
        	Integer countEntrust = userC2CEntrustMapper.countEntrust(userBankinfo.getFuid(), userBankinfo.getFid(), UserC2CEntrustTypeEnum.withdraw.getCode(), arrayList);
        	if(countEntrust != null && countEntrust > 0) {
        		return Result.failure(1024,"请等待C2C提现完成后再进行修改");
        	}
            userBankinfo.setFstatus(BankInfoStatusEnum.ABNORMAL_VALUE);
            if (userBankInfoMapper.updateByPrimaryKey(userBankinfo) <= 0) {
                return Result.failure("删除失败");
            }
            return Result.success("删除成功");
		} catch (Exception e) {
			logger.error("删除支付方式异常",e);
			return Result.failure(1001,"修改失敗！");
		}
        
    }

    @Override
    public List<FUserBankinfoDTO> listBankInfo(Integer userId, Integer type, Integer status) {
    	try {
    		List<FUserBankinfoDO> list = userBankInfoMapper.getBankInfoListByUser(userId, type,status);
            return PojoConvertUtil.convert(list, FUserBankinfoDTO.class);
		} catch (Exception e) {
			logger.error("listBankInfo异常",e);
			return null;
		}
    }

    /* (non-Javadoc)
     * @see com.qkwl.common.rpc.capital.IUserCapitalAccountService#createCoinAddressRecharge(java.lang.Integer, java.lang.Integer, java.lang.String)
     */
    @Override
    public Result createCoinAddressRecharge(Integer userId, Integer coinId, String ip) {
        if(userId == null || coinId == null || StringUtils.isEmpty(ip)){
            return Result.param("参数异常!");
        }

        FUser fuser = userCommonMapper.selectOneById(userId);
        if(fuser == null){
            return Result.param("user not found!");
        }

        SystemCoinType coinType = redisHelper.getCoinType(coinId);
        if (coinType == null || !coinType.getIsRecharge() || coinType.getStatus().equals(SystemCoinStatusEnum.ABNORMAL.getCode()) ) {
            return Result.param("coin not found or unavailable!");
        }

        //获取币种的归属主币
        coinId = redisHelper.getCoinTypeByCoinSort(coinType.getId()).getId();
        
        FUserVirtualAddressDO virtualAddress;
        if(coinType.getCoinType().equals(SystemCoinSortEnum.GXS.getCode())){
            virtualAddress = new FUserVirtualAddressDO();
            virtualAddress.setFadderess(coinType.getAccessKey());
            virtualAddress.setFuid(userId);
            virtualAddress.setFcoinid(coinType.getId());
        }else if(coinType.getIsUseMemo()){
            virtualAddress = new FUserVirtualAddressDO();
            virtualAddress.setFadderess(coinType.getEthAccount());
            virtualAddress.setFuid(userId);
            virtualAddress.setFcoinid(coinType.getId());
            virtualAddress.setMemo(userId.toString());
        }else{
            virtualAddress = userVirtualAddressMapper.selectByUserAndCoin(userId, coinId);
        }
        if (virtualAddress != null) {
        	if(virtualAddress.getFadderess().startsWith("bitcoincash:")) {
        		virtualAddress.setFadderess(AddressValidateUtils.bchAddr2btcAddr(virtualAddress.getFadderess()));
        	}
            return Result.success("充值地址创建成功", PojoConvertUtil.convert(virtualAddress, FUserVirtualAddressDTO.class));
        }
        FPoolDO fpool = this.poolMapper.selectOneFpool(coinId);
        if (fpool == null) {
            return Result.failure(1000, "充值地址不足");
        }
        String address = fpool.getFaddress();
        if (address == null || address.trim().equalsIgnoreCase("null") || address.trim().equals("")) {
            return Result.failure(1000, "充值地址不足");
        }
        FUserVirtualAddressDO fvirtualaddress = new FUserVirtualAddressDO();
        fvirtualaddress.setFadderess(address);
        fvirtualaddress.setFcreatetime(Utils.getTimestamp());
        fvirtualaddress.setFuid(userId);
        fvirtualaddress.setFcoinid(coinId);
        fvirtualaddress.setSecret(fpool.getSecret());
        try {
            if (coinCapitalServiceTx.createCoinAddress(fpool, fvirtualaddress)) {
                mqSendUtils.SendUserAction(userId, LogUserActionEnum.ADD_ADDRESS_RECHARGE, ip);
                if(fvirtualaddress.getFadderess().startsWith("bitcoincash:")) {
                	fvirtualaddress.setFadderess(AddressValidateUtils.bchAddr2btcAddr(fvirtualaddress.getFadderess()));
            	}
                return Result.success("充值地址创建成功", PojoConvertUtil.convert(fvirtualaddress, FUserVirtualAddressDTO.class));
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("createCoinAddress err userId:{},coinId:{},ip:{} ", userId, coinId, ip);
        }
        return Result.failure(1000, "充值地址不足");
    }

    @Override
    public Result createCoinAddressWithdraw(UserVirtualAddressWithdrawDTO dto) {
        if(dto.getFuid() == null){
            return Result.param("userId is null");
        }
        if(dto.getFcoinid() == null){
            return Result.param("coinId is null");
        }
        if(dto.getFadderess() == null || "".equals(dto.getFadderess().trim())){
            return Result.param("address is null");
        }
        if(dto.getIp() == null || "".equals(dto.getIp().trim())){
            return Result.param("ip is null");
        }

        FUser user = userCommonMapper.selectOneById(dto.getFuid());
        if(user == null){
            return Result.param("user not found!");
        }
        Boolean preValidate = preValidationHelper.validateUserTradePasswordIsSetting(user);
        if(preValidate){
            return Result.failure(1001, "请先设置交易密码");
        }else{
            Result result = validationCheckHelper.getTradePasswordCheck(user.getFtradepassword(), dto.getPassword(), dto.getIp());
            if(!result.getSuccess()){
                return result;
            }
        }
        FUserVirtualAddressWithdrawDO withdraw = PojoConvertUtil.convert(dto, FUserVirtualAddressWithdrawDO.class);
        
        List<FUserVirtualAddressWithdrawDO> virtualCoinWithdrawAddressList = userVirtualAddressWithdrawMapper.getVirtualCoinWithdrawAddressList(withdraw);
        if(virtualCoinWithdrawAddressList != null && virtualCoinWithdrawAddressList.size() > 0) {
        	return Result.failure(1003, "地址已存在！");
        }
        if(userVirtualAddressWithdrawMapper.insert(withdraw) <= 0){
            return Result.failure("创建失败");
        }
        // MQ_USER_ACTION
        mqSendUtils.SendUserAction(withdraw.getFuid(), LogUserActionEnum.ADD_ADDRESS_WITHDRAW, dto.getIp());
        return Result.success("创建成功");
    }

    @Override
    public Result deleteCoinAddressWithdraw(Integer userId, Integer addressId) {
        if(userId == null){
            return Result.param("userId is null");
        }
        if(addressId == null){
            return Result.param("addressId is null");
        }

        FUser user = userCommonMapper.selectOneById(userId);
        if(user == null){
            return Result.param("user not found");
        }

        FUserVirtualAddressWithdrawDO address = userVirtualAddressWithdrawMapper.selectByPrimaryKey(addressId);
        if (!address.getFuid().equals(userId)) {
            return Result.failure(1002,"地址不存在");
        }
        address.setInit(false);
        if (userVirtualAddressWithdrawMapper.updateByPrimaryKey(address) <= 0) {
            return Result.failure("删除失败");
        }
        return Result.success("删除成功");
    }

    @Override
    public List<FUserVirtualAddressWithdrawDTO> listCoinAddressWithdraw(Integer userId, Integer coinId) {
    	try {
	        FUserVirtualAddressWithdrawDO withdraw = new FUserVirtualAddressWithdrawDO();
	        withdraw.setInit(true);
	        withdraw.setFuid(userId);
	        withdraw.setFcoinid(coinId);
	        List<FUserVirtualAddressWithdrawDO> virtualCoinWithdrawAddressList = userVirtualAddressWithdrawMapper.getVirtualCoinWithdrawAddressList(withdraw);
	        List<FUserVirtualAddressWithdrawDTO> convert = PojoConvertUtil.convert(virtualCoinWithdrawAddressList, FUserVirtualAddressWithdrawDTO.class);
	        SystemCoinType coinTypeSystem = redisHelper.getCoinTypeSystem(coinId);
	        //如果这个币种平台互转收手续费那就不用查了，直接返回
	        if(coinTypeSystem.getIsPlatformTransferFee() || convert.size() == 0) {
	        	return convert;
	        }
	        //获取基础币种
	        SystemCoinType coinTypeByCoinSort = redisHelper.getCoinTypeByCoinSort(coinId);
	        for (FUserVirtualAddressWithdrawDTO fUserVirtualAddressWithdrawDTO : convert) {
	        	//r如果是eos提现到我们的充值地址
	        	if(coinTypeSystem.getCoinType().equals(SystemCoinSortEnum.EOS.getCode())
	        			|| coinTypeSystem.getCoinType().equals(SystemCoinSortEnum.BTS.getCode())
	        			||coinTypeSystem.getCoinType().equals(SystemCoinSortEnum.XRP.getCode())) {
	        		if(fUserVirtualAddressWithdrawDTO.getFadderess().equals(coinTypeSystem.getEthAccount())) {
	        			fUserVirtualAddressWithdrawDTO.setIsCollectFee(false);
	        		}
	        	}else {
	        		//如果提现到我们的客户充值地址
	        		FUserVirtualAddressDO selectByCoinAndAddress = userVirtualAddressMapper.selectByCoinAndAddress(coinTypeByCoinSort.getId(), fUserVirtualAddressWithdrawDTO.getFadderess());
					//确定是平台互转
					if(selectByCoinAndAddress != null) {
						fUserVirtualAddressWithdrawDTO.setIsCollectFee(false);
					}
	        	}
			}
	        return convert;
		} catch (Exception e) {
			logger.info("查询提现地址异常",e);
			return null;
		}
    }

    //新版本接口，去掉了手机验证码和谷歌验证码  目前只能添加银行卡类型
    @Deprecated
	@Override
	public Result createOrUpdateBankInfoV1(UserBankinfoDTO userBankinfo) {
		//参数验证，失败跳出
        Result result = validateNewParam(userBankinfo);
        if(!result.getSuccess()){
            return result;
        }

        String backName = "Alipay";
        String logo = "";
        //if(userBankinfo.getType().equals(WithdrawBankTypeEnum.Bank.getCode())){
            FSystemBankinfoWithdraw withdraw = redisHelper.getWithdrawBank(userBankinfo.getSystemBankId());
            if (withdraw == null) {
                return Result.failure(1000, "提现账号未找到！");
            }
            backName = withdraw.getFcnname();
            
            logo = withdraw.getFlogo();
        //}

        //新增还是修改
        boolean isUpdate = false;
        FUserBankinfoDO bankinfo = null;
        //判断组件id是否存在，存在查询记
        if(userBankinfo.getId() != null && userBankinfo.getId() > 0){
            isUpdate = true;
            bankinfo = userBankInfoMapper.selectByPrimaryKey(userBankinfo.getId());
        }
        if(bankinfo == null){
            bankinfo = new FUserBankinfoDO();
            bankinfo.setFuid(userBankinfo.getUserId());
            
            bankinfo.setFrealname(userBankinfo.getRealName());
            bankinfo.setFtype(userBankinfo.getType());
            bankinfo.setInit(true);
            bankinfo.setFstatus(BankInfoStatusEnum.NORMAL_VALUE);
            bankinfo.setFcreatetime(new Date());
            bankinfo.setVersion(0);
        }
        bankinfo.setFbanknumber(userBankinfo.getBankNumber());
        bankinfo.setFprov(userBankinfo.getProv());
        bankinfo.setFcity(userBankinfo.getCity());
        bankinfo.setFdist(userBankinfo.getDist());
        bankinfo.setFaddress(userBankinfo.getAddress());
        bankinfo.setLogo(logo);
        bankinfo.setFname(backName);
        bankinfo.setFbanktype(userBankinfo.getSystemBankId());
        
        //判断是新增还是修改
        if(isUpdate){
            if (userBankInfoMapper.updateByPrimaryKey(bankinfo) <= 0) {
                return Result.failure(1001,"修改失败！");
            }
            mqSendUtils.SendUserAction(userBankinfo.getUserId(), LogUserActionEnum.MODIFY_BANK, userBankinfo.getIp());
            return Result.success("修改成功");
        } else{
        	//查询一遍用户是否绑定了银行卡 如果一张都没有绑定则新增的为默认银行卡
        	List<FUserBankinfoDTO> list = listBankInfo(userBankinfo.getUserId(), 1, OtcPaymentStatusEnum.Normal.getCode());
        	if(list == null || list.size()==0) {
        		bankinfo.setIsDefault(UserBankInfoDefaultEnum.TRUE.getCode());
        	}else if(list.size() >= 3) {
        		return Result.failure(1003,"只能添加三張銀行卡！");
        	}
        	
            if (userBankInfoMapper.insert(bankinfo) <= 0) {
                return Result.failure(1002,"新增失败！");
            }
            // MQ_USER_ACTION
            mqSendUtils.SendUserAction(userBankinfo.getUserId(), LogUserActionEnum.ADD_BANK, userBankinfo.getIp());
            return Result.success("新增成功");
        }
	}
	
	
    /**
     * 创建或修改支付方式
     * @param userBankinfo 银行信息DTO
     * @return Result   返回结果  capital.bank.withdraw.+ code
     * 
     */
	@Override
	public Result createOrUpdatePayment(UserBankinfoDTO userBankinfo) {
		try {
            logger.debug("createOrUpdatePayment userBankinfo:{}", JSON.toJSONString(userBankinfo));
			//新增还是修改
	        boolean isUpdate = userBankinfo.getId() != null ;
			FUserBankinfoDO bankinfo = null;
			if(isUpdate) {
				bankinfo = userBankInfoMapper.selectByPrimaryKey(userBankinfo.getId());
				if(bankinfo == null || bankinfo.getFstatus()  == BankInfoStatusEnum.ABNORMAL_VALUE) {
					return Result.failure(1009, "支付方式不存在");
				}
				userBankinfo.setType(bankinfo.getFtype());
			}
			
			//获取支付方式
			List<OtcPayment> allPaymentList = redisHelper.getAllPaymentList();
			OtcPayment otcPayment = null;
			for (OtcPayment o : allPaymentList) {
				if(o.getId() == userBankinfo.getType()) {
					otcPayment = o ;
					break;
				}
			}
			//支付方式不存在
			if(otcPayment == null) {
				return Result.failure(1009, "支付方式不存在");
			}
			
			//参数验证，失败跳出
	        Result result = validateParamV2(userBankinfo,otcPayment);
	        if(!result.getSuccess()){
	            return result;
	        }
			
	        //如果不是银行卡类型，logo直接用支付宝或微信或paypal的logo
	        String bankName = redisHelper.getMultiLangMsg(otcPayment.getNameCode());
	        String logo = otcPayment.getPicture();
	        
	        //银行卡类型
	        if (StringUtils.isNotEmpty(userBankinfo.getBankName())) {
	        	bankName = userBankinfo.getBankName();
			}
	       
	        if(!isUpdate) {//新增
	            bankinfo = new FUserBankinfoDO();
	            bankinfo.setFuid(userBankinfo.getUserId());
	            bankinfo.setFrealname(userBankinfo.getRealName());
	            bankinfo.setFtype(userBankinfo.getType());
	            bankinfo.setInit(true);
	            bankinfo.setFstatus(BankInfoStatusEnum.NORMAL_VALUE);
	            bankinfo.setFcreatetime(new Date());
	            bankinfo.setVersion(0);
	        }
	        if(userBankinfo.getType() == 1) {//银行卡类型不为空，即不为银行卡类型
	             bankinfo.setFaddress(userBankinfo.getAddress());
	        }else {
	        	bankinfo.setQrcodeImg(userBankinfo.getQrcodeImg());
	        }
	        bankinfo.setFbanknumber(userBankinfo.getBankNumber());
	        bankinfo.setLogo(logo);
	        bankinfo.setFname(bankName);
	        
	        FUser user = userMapper.selectByPrimaryKey(userBankinfo.getUserId());
	        String localeStr = I18NUtils.getCurrentLang();
	        Integer langCode = LocaleEnum.getCodeByName(localeStr);
	        if(isUpdate){
	        	//如果是修改且是默认的支付方式
	        	if(bankinfo.getIsDefault() == UserBankInfoDefaultEnum.TRUE.getCode()) {
	        		//查询是否有进行中的otc广告用了该种支付方式
	        		Integer countAdvertActionByUserIdAndType = otcAdvertMapper.countAdvert(bankinfo.getFuid(), bankinfo.getFtype(),Constant.OTC_ADVERT_ON);
		        	if(countAdvertActionByUserIdAndType != null && countAdvertActionByUserIdAndType > 0) {
		        		return Result.failure(1023,"请先下架您的otc广告后再修改该支付方式");
		        	}
		        	//如果还有委完成的c2c订单
		        	ArrayList<Integer> arrayList = new ArrayList<>();
		        	arrayList.add(UserC2CEntrustStatusEnum.processing.getCode());
		        	arrayList.add(UserC2CEntrustStatusEnum.wait.getCode());
		        	Integer countEntrust = userC2CEntrustMapper.countEntrust(bankinfo.getFuid(), bankinfo.getFid(), UserC2CEntrustTypeEnum.withdraw.getCode(), arrayList);
		        	if(countEntrust != null && countEntrust > 0) {
		        		return Result.failure(1024,"请等待C2C提现完成后再进行修改");
		        	}
	        	}
	            if (userBankInfoMapper.updateByPrimaryKey(bankinfo) <= 0) {
	                return Result.failure(1001,"修改失败！");
	            }
	            mqSendUtils.SendUserAction(userBankinfo.getUserId(), LogUserActionEnum.MODIFY_BANK, userBankinfo.getIp());
	            // 发送短信
				if (user.getFistelephonebind()) {
					validateHelper.smsPaymentMethod(user.getFareacode(), user.getFtelephone(), PlatformEnum.BC.getCode(),
							BusinessTypeEnum.SMS_UPDATE_PAYMENT_METHED.getCode(), bankName, userBankinfo.getBankNumber(), langCode);
				}
	            return Result.success("修改成功");
	        } else{
	        	//查询一遍用户此种类型的支付方式
	        	List<FUserBankinfoDTO> list = listBankInfo(userBankinfo.getUserId(), otcPayment.getId(), BankInfoStatusEnum.NORMAL_VALUE);
	        	//银行卡类型最多三种
	        	if(otcPayment.getType() == OtcPaymentTypeEnum.Bankcard.getCode()) {
	        		if(list == null || list.size()==0) {
	            		bankinfo.setIsDefault(UserBankInfoDefaultEnum.TRUE.getCode());
	            	}else if(list.size() >= 3) {
	            		return Result.failure(1018,"只能添加三張銀行卡！");
	            	}else {
	            		bankinfo.setIsDefault(UserBankInfoDefaultEnum.FALSE.getCode());
	            	}
	        	}else {   
	        	//银行卡以外的其他类型最多一种，且是默认支付方式
	        		if(list == null || list.size() == 0) {
	            		bankinfo.setIsDefault(UserBankInfoDefaultEnum.TRUE.getCode());
	            	}else{
	            		return Result.failure(1019,"该支付方式只能添加一种");
	            	}
	        	}
	            if (userBankInfoMapper.insert(bankinfo) <= 0) {
	                return Result.failure(1002,"新增失败！");
	            }
	            // MQ_USER_ACTION
	            mqSendUtils.SendUserAction(userBankinfo.getUserId(), LogUserActionEnum.ADD_BANK, userBankinfo.getIp());
	            
	            // 发送短信
				if (user.getFistelephonebind()) {
					validateHelper.smsPaymentMethod(user.getFareacode(), user.getFtelephone(), PlatformEnum.BC.getCode(),
							BusinessTypeEnum.SMS_ADD_PAYMENT_METHED.getCode(), bankName, userBankinfo.getBankNumber(), langCode);
				}
	            return Result.success("新增成功");
	        }
		} catch (Exception e) {
			logger.error("新增或修改支付方式异常",e);
			return Result.failure(1002,"新增失败！");
		}
		 
	}
	
	
	/**
     * 支付方式的参数验证
     * @param userBankinfo 银行信息DTO
     * @return Result   返回结果<br/>
     * capital.bank.withdraw.1004=请先完成实名认证！
     * capital.bank.withdraw.1007=支付密码为空
     * capital.bank.withdraw.1008=支付账号为空
     * capital.bank.withdraw.1009=支付方式不存在
     * capital.bank.withdraw.1010=userId为空
     * capital.bank.withdraw.1011=ip为空
     * capital.bank.withdraw.1012=开户行地址信息错误
     * capital.bank.withdraw.1013=用户不存在
     * capital.bank.withdraw.1015=请先设置交易密码
     * capital.bank.withdraw.1016=您已绑定该银行卡，请勿重复绑定！
     * capital.bank.withdraw.1017=bankId为空
     */
     private Result validateParamV2(UserBankinfoDTO userBankinfo,OtcPayment otcPayment){
         if (userBankinfo.getBankNumber() == null) {
        	 return Result.failure(1008, "支付账号为空");
         }
         if (userBankinfo.getUserId() == null) {
        	 return Result.failure(1010, "userId为空");
         }
         if (StringUtils.isEmpty(userBankinfo.getIp())) {
        	 return Result.failure(1011, "ip为空");
         }
         //银行卡类型需要的字段
         if(otcPayment.getType() == OtcPaymentTypeEnum.Bankcard.getCode()) {
        	 
			/*
			 * 省市区不必填 黄锦锋 20190514修改
			 * if (StringUtils.isEmpty(userBankinfo.getProv())) { //省 return
			 * Result.failure(1012, "开户行地址信息错误"); } if
			 * (StringUtils.isEmpty(userBankinfo.getCity())) { //市 return
			 * Result.failure(1012, "开户行地址信息错误"); } if
			 * (StringUtils.isEmpty(userBankinfo.getAddress())) { //区 return
			 * Result.failure(1012, "开户行地址信息错误"); }
			 */
             if (userBankinfo.getBankName() == null) { //银行卡类型id
            	 return Result.failure(1017, "bankName为空");
             }
         }

         FUser user = userCommonMapper.selectOneById(userBankinfo.getUserId());
         if(user == null){
        	 return Result.failure(1013, "用户不存在");
         }
         // 判断实名
         if(!user.getFhasrealvalidate()){
             return Result.failure(1004, "请先完成实名认证！");
         }
         
         boolean preValidate = preValidationHelper.validateUserTradePasswordIsSetting(user);
         if(preValidate){
             return Result.failure(1015, "请先设置交易密码");
         }else{
             Result result = validationCheckHelper.getTradePasswordCheck(user.getFtradepassword(), userBankinfo.getPassword(), userBankinfo.getIp());
             if(!result.getSuccess()){
                 return result;
             }
         }
         


         Map<String,Object> param = new HashMap<>();
         param.put("banknumber",userBankinfo.getBankNumber());
         param.put("status",BankInfoStatusEnum.NORMAL_VALUE);
         param.put("type",otcPayment.getId());
         param.put("userId",userBankinfo.getUserId());
         
         
         //如果有主键id,表示为修改银行卡
         if(userBankinfo.getId() != null && userBankinfo.getId() > 0){
             //return Result.success();
        	 param.put("notInId",userBankinfo.getId());
         }
         //判断银行卡是否存在
         if(userBankInfoMapper.getBankInfoByCount(param) > 0){
             return Result.failure(1016, "您已绑定该支付方式号码，请勿重复绑定！");
         }
         return Result.success();
     }
    
	
	/**
    * 银行卡信息的参数验证
    * @param userBankinfo 银行信息DTO
    * @return Result   返回结果<br/>
    * 200 : 成功
    * 1004: 请先完成实名认证！<br/>
    * 1005: 银行卡账号名必须与您的实名认证姓名一致！<br/>
    * 1006: 您已绑定该银行卡，请勿重复绑定！<br/>
    */
    private Result validateNewParam(UserBankinfoDTO userBankinfo){
        if (userBankinfo.getUserId() == null) {
            return Result.param("userId is null");
        }
        if (userBankinfo.getBankNumber() == null) {
            return Result.param("bankNumber is null");
        }
        if (userBankinfo.getRealName() == null) {
            return Result.param("realName is null");
        }
        if (userBankinfo.getProv() == null) {
            return Result.param("prov is null");
        }
        if (userBankinfo.getCity() == null) {
            return Result.param("city is null");
        }
        if (userBankinfo.getAddress() == null) {
            return Result.param("address is null");
        }
        if (userBankinfo.getType() == null) {
            return Result.param("type is null");
        }
        if (userBankinfo.getIp() == null) {
            return Result.param("ip is null");
        }

        FUser user = userCommonMapper.selectOneById(userBankinfo.getUserId());
        if(user == null){
            return Result.param("find user fall");
        }
        // 判断实名
        if(!user.getFhasrealvalidate()){
            return Result.failure(10001, "请先完成实名认证！");
        }
        
        boolean preValidate = preValidationHelper.validateUserTradePasswordIsSetting(user);
        if(preValidate){
            return Result.failure(10003, "请先设置交易密码");
        }else{
            Result result = validationCheckHelper.getTradePasswordCheck(user.getFtradepassword(), userBankinfo.getPassword(), userBankinfo.getIp());
            if(!result.getSuccess()){
                return result;
            }
        }
        
        if (!userBankinfo.getRealName().equals(user.getFrealname())) {
            return Result.failure(1005, "银行卡账号名必须与您的实名认证姓名一致！");
        }
        
        Map<String,Object> param = new HashMap<>();
        param.put("banknumber",userBankinfo.getBankNumber());
        param.put("status",BankInfoStatusEnum.NORMAL_VALUE);
        param.put("type",1);
        param.put("userId",userBankinfo.getUserId());
        
        //如果有主键id,表示为修改银行卡
        if(userBankinfo.getId() != null && userBankinfo.getId() > 0){
            //return Result.success();
        	param.put("notInId",userBankinfo.getId());
        }

        //判断银行卡是否存在
        if(userBankInfoMapper.getBankInfoByCount(param) > 0){
            return Result.failure(1006, "您已绑定该银行卡，请勿重复绑定！");
        }
        return Result.success();
    }

    
    
    
    
	@Override
	public Result defaultBankInfo(Integer userId, Integer id) {
		try {
			if (userId == null || userId.equals(0)) {
				return Result.failure(1010, "userId为空");
	        }
			
			if(id == null || id.equals(0)) {
				return Result.failure(1014, "支付方式id为空");
			}
			
			FUser user = userCommonMapper.selectOneById(userId);
	        if(user == null){
	        	return Result.failure(1013, "用户不存在");
	        }
	        
	        FUserBankinfoDO bankinfo = userBankInfoMapper.selectByPrimaryKey(id);
	        logger.info(bankinfo.toString());
	        if(bankinfo == null || !bankinfo.getFuid().equals(userId) || bankinfo.getFstatus() != BankInfoStatusEnum.NORMAL_VALUE){
	        	logger.error("支付方式不存在");
	        	return Result.failure(1009, "支付方式不存在");
	        }
	        
	        //获取支付方式
	      	List<OtcPayment> allPaymentList = redisHelper.getAllPaymentList();
	      	OtcPayment otcPayment = null;
	      	for (OtcPayment o : allPaymentList) {
	      		if(o.getId() == bankinfo.getFtype()) {
	      			otcPayment = o ;
	      			break;
	      		}
	      	}
	      	
	      	if(otcPayment.getType() != OtcPaymentTypeEnum.Bankcard.getCode()) {
	      		return Result.success("修改成功");
	      		//return Result.failure(1020, "当前支付方式不允许修改默认支付方式");
	      	}
	      	
	      	Integer countAdvertActionByUserIdAndType = otcAdvertMapper.countAdvert(userId, otcPayment.getId(),Constant.OTC_ADVERT_ON);
	      	if(countAdvertActionByUserIdAndType != null && countAdvertActionByUserIdAndType > 0) {
	      		return Result.failure(1023,"请先下架您的otc广告后再修改该支付方式");
	      	}
	      	
	        //将其他银行卡状态设置为0
	        List<FUserBankinfoDO> list = userBankInfoMapper.getBankInfoListByUser(user.getFid(), otcPayment.getType(),BankInfoStatusEnum.NORMAL_VALUE);
	        if(list != null && list.size() > 0) {
	        	for(FUserBankinfoDO obj : list) {
	        		if(!obj.getFid().equals(id)) {
	        			obj.setIsDefault(UserBankInfoDefaultEnum.FALSE.getCode());
	                    userBankInfoMapper.updateByPrimaryKey(obj);
	        		}
	        	}
	        }
	        
	        bankinfo.setIsDefault(UserBankInfoDefaultEnum.TRUE.getCode());
	        userBankInfoMapper.updateByPrimaryKey(bankinfo);
	        return Result.success("修改成功");
		} catch (Exception e) {
			logger.error("设置默认支付方式异常",e);
			return Result.failure(1001,"修改失敗！");
		}
	}
	
}
