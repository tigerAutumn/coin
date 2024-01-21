package com.qkwl.service.otc.impl;

import java.math.BigDecimal;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSON;
import com.qkwl.common.dto.Enum.otc.OtcMerchantStatusEnum;
import com.qkwl.common.dto.Enum.otc.OtcUserTypeEnum;
import com.qkwl.common.dto.otc.OtcMerchant;
import com.qkwl.common.dto.otc.OtcUserExt;
import com.qkwl.common.dto.user.FUser;
import com.qkwl.common.exceptions.BCException;
import com.qkwl.common.framework.redis.RedisHelper;
import com.qkwl.common.match.MathUtils;
import com.qkwl.common.result.Result;
import com.qkwl.common.rpc.otc.IOtcCoinWalletService;
import com.qkwl.common.rpc.otc.IOtcUserService;
import com.qkwl.common.util.ArgsConstant;
import com.qkwl.common.util.Constant;
import com.qkwl.common.util.I18NUtils;
import com.qkwl.common.util.ReturnResult;
import com.qkwl.service.otc.dao.FUserMapper;
import com.qkwl.service.otc.dao.OtcMerchantMapper;
import com.qkwl.service.otc.dao.OtcUserExtMapper;

@Service("otcUserService")
public class OtcUserServiceImpl implements IOtcUserService {
	private static final Logger logger = LoggerFactory.getLogger(OtcUserServiceImpl.class);
	
	@Autowired
	private FUserMapper userMapper;
	@Autowired
	private OtcUserExtMapper otcUserExtMapper;
	@Autowired
	private OtcMerchantMapper otcMerchantMapper;
	@Autowired
	private IOtcCoinWalletService otcCoinWalletService;

	@Override
	public OtcUserExt getOtcUserExt(Integer userId) {
		try {
			//查询
			OtcUserExt otcUserExt = otcUserExtMapper.selectByUserId(userId);
			FUser user = userMapper.selectByPrimaryKey(userId);
			//如果otc扩展表没有就添加
			if(otcUserExt == null && user != null) {
				otcUserExt = new OtcUserExt();
				otcUserExt.setBadEvaluation(0);
				otcUserExt.setCmpOrders(0);
				otcUserExt.setGoodEvaluation(0);
				otcUserExt.setSuccAmt(BigDecimal.ZERO);
				otcUserExt.setSumAppeal(0);
				otcUserExt.setUserId(userId);
				otcUserExt.setWinAppeal(0);
				otcUserExt.setOtcUserType(OtcUserTypeEnum.Ordinary_Merchant.getCode());
				otcUserExt.setCreateTime(new Date());
				otcUserExt.setUpdateTime(new Date());
				otcUserExtMapper.insert(otcUserExt);
			}else if(user == null){
				return null;
			}
			otcUserExt.setGooglebind(user.getFgooglebind());
			otcUserExt.setIsmailbind(user.getFismailbind());
			otcUserExt.setIstelephonebind(user.getFistelephonebind());
			otcUserExt.setHasrealvalidate(user.getFhasrealvalidate());
			otcUserExt.setNickname(user.getFnickname());
			otcUserExt.setPhoto(user.getPhoto());
			otcUserExt.setRegistertime(user.getFregistertime());
			//查询商户押金
			OtcMerchant otcMerchant = otcMerchantMapper.selectByUid(userId);
			if (otcMerchant != null) {
				otcUserExt.setDeposit(otcMerchant.getDeposit());
			}
			return otcUserExt;
		} catch (Exception e) {
			logger.error("查询用户otc扩展表异常",e);
		}
		return null;
	}


	@Override
	public void update(OtcUserExt otcUserExt) {
		otcUserExtMapper.updateByPrimaryKey(otcUserExt);
	}
	
	@Override
	public OtcMerchant getMerchantByUid(Integer uid) {
		return otcMerchantMapper.selectByUid(uid);
	}
	
	@Override
	public FUser getUser(Integer userId) {
		return userMapper.selectByPrimaryKey(userId);
	}
	
	@Override
	@Transactional(value="flexibleTransMgr" ,isolation = Isolation.REPEATABLE_READ, propagation=Propagation.REQUIRED, rollbackFor = Exception.class)
	public Result applyAuthentication(OtcMerchant otcMerchant) throws BCException {
		try {
			OtcMerchant merchant = otcMerchantMapper.selectByUid(otcMerchant.getUid());
			if (merchant != null) {
				if (merchant.getStatus() == OtcMerchantStatusEnum.Inited.getCode()) {
					return Result.failure("已提交过申请，请勿重复提交！");
				}
				if (merchant.getStatus() == OtcMerchantStatusEnum.Passed.getCode()) {
					return Result.failure("已认证商户，请勿提交！");
				}
				if (merchant.getStatus() == OtcMerchantStatusEnum.Prohibited.getCode()) {
					return Result.failure("已禁用商户，禁止提交！");
				}
				String msg = "first";
				if (merchant.getStatus() == OtcMerchantStatusEnum.Removed.getCode()) {
					//冻结钱包
					Result frozenResult = otcCoinWalletService.userOtcFrozen(otcMerchant.getUid(), 9, otcMerchant.getDeposit());
					if (!frozenResult.getSuccess()) {
						throw new BCException(frozenResult.getMsg());
					}
				} else {
					otcMerchant.setDeposit(merchant.getDeposit());
					msg = "notFirst";
				}
				//更新商户申请
				if (otcMerchantMapper.update(otcMerchant) <= 0) {
					throw new BCException("商户记录更新失败");
				}
				return Result.success(msg);
			} else {
				//记录商户申请
				if (otcMerchantMapper.insert(otcMerchant) <= 0) {
					throw new BCException("商户记录生成失败");
				}
				//冻结钱包
				Result frozenResult = otcCoinWalletService.userOtcFrozen(otcMerchant.getUid(), 9, otcMerchant.getDeposit());
				if (!frozenResult.getSuccess()) {
					throw new BCException(frozenResult.getMsg());
				}
				return Result.success("first");
			}
		} catch (BCException e) {
            logger.error("申请商户 advert:{},BCException：{}",JSON.toJSONString(otcMerchant),e);
			throw e;
		} catch (Exception e) {
            logger.error("申请商户  advert:{},Exception：{}",JSON.toJSONString(otcMerchant),e);
			throw new BCException("申请商户异常");
		}
	}
}
