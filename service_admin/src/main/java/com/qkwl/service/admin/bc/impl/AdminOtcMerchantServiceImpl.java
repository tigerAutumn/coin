package com.qkwl.service.admin.bc.impl;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.qkwl.common.Enum.validate.LocaleEnum;
import com.qkwl.common.dto.Enum.otc.OtcMerchantStatusEnum;
import com.qkwl.common.dto.Enum.otc.OtcTransferTypeEnum;
import com.qkwl.common.dto.Enum.otc.OtcUserTypeEnum;
import com.qkwl.common.dto.common.Pagination;
import com.qkwl.common.dto.otc.OtcAdvert;
import com.qkwl.common.dto.otc.OtcMerchant;
import com.qkwl.common.dto.otc.OtcUserExt;
import com.qkwl.common.dto.otc.OtcUserTransfer;
import com.qkwl.common.dto.user.FUser;
import com.qkwl.common.dto.user.FUserExtend;
import com.qkwl.common.dto.wallet.UserOtcCoinWallet;
import com.qkwl.common.exceptions.BCException;
import com.qkwl.common.match.MathUtils;
import com.qkwl.common.properties.ChatProperties;
import com.qkwl.common.rpc.admin.IAdminOtcMerchantService;
import com.qkwl.common.rpc.admin.IAdminOtcService;
import com.qkwl.common.util.Constant;
import com.qkwl.common.util.ReturnResult;
import com.qkwl.common.util.Utils;
import com.qkwl.service.admin.bc.dao.FUserExtendMapper;
import com.qkwl.service.admin.bc.dao.FUserMapper;
import com.qkwl.service.admin.bc.dao.OtcAdvertMapper;
import com.qkwl.service.admin.bc.dao.OtcMerchantMapper;
import com.qkwl.service.admin.bc.dao.OtcUserExtMapper;
import com.qkwl.service.admin.bc.dao.OtcUserTransferMapper;
import com.qkwl.service.admin.bc.dao.UserOtcCoinWalletMapper;
import com.qkwl.service.admin.req.SendSmsReq;

import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

@Service("adminOtcMerchantService")
public class AdminOtcMerchantServiceImpl implements IAdminOtcMerchantService {

	private static final Logger logger = LoggerFactory.getLogger(AdminOtcMerchantServiceImpl.class);
	
	@Autowired
	private OtcMerchantMapper otcMerchantMapper;
	@Autowired
	private OtcUserExtMapper otcUserExtMapper;
	@Autowired
	private UserOtcCoinWalletMapper userOtcCoinWalletMapper;
	@Autowired
	private OtcAdvertMapper otcAdvertMapper;
	@Autowired
	private IAdminOtcService adminOtcService;
	@Autowired
	private ChatProperties chatProperties;
	@Autowired
	private OtcUserTransferMapper userOtcTransferMapper;
	@Autowired
	private FUserMapper userMapper;
	@Autowired
	private FUserExtendMapper userExtendMapper;
	
	@Override
	public Pagination<OtcMerchant> selectOtcMerchantPageList(Pagination<OtcMerchant> pageParam,
			OtcMerchant otcMerchant) {
		// 组装查询条件数据
		Map<String,Object> map = new HashMap<String, Object>();
		map.put("uid", otcMerchant.getUid());
		map.put("status", otcMerchant.getStatus());
		map.put("offset", pageParam.getOffset());
		map.put("limit", pageParam.getPageSize());
		map.put("orderField", pageParam.getOrderField());
		map.put("orderDirection", pageParam.getOrderDirection());
		
		// 查询总返佣数
		int count = otcMerchantMapper.countOtcMerchantListByParam(map);
		if(count > 0) {
			// 查询返佣列表
			List<OtcMerchant> otcMerchantList = otcMerchantMapper.getOtcMerchantPageList(map);
			// 设置返回数据
			pageParam.setData(otcMerchantList);
		}
		pageParam.setTotalRows(count);
		return pageParam;
	}

	@Override
	public OtcMerchant selectOtcMerchantById(Integer id) {
		return otcMerchantMapper.selectByPrimaryKey(id);
	}

	@Override
	public ReturnResult passOtcMerchant(OtcMerchant otcMerchant) {
		try {
			//更新商户表
			Date now = new Date();
			otcMerchant.setStatus(OtcMerchantStatusEnum.Passed.getCode());
			otcMerchant.setOperateTime(now);
			otcMerchantMapper.updateByPrimaryKey(otcMerchant);
			//更新用户为已认证商户
			OtcUserExt otcUserExt = otcUserExtMapper.selectByUserId(otcMerchant.getUid());
			if (otcUserExt == null) {
				return ReturnResult.FAILUER("otc扩展表中不存在该商户！");
			}
			otcUserExt.setOtcUserType(OtcUserTypeEnum.Certified_Merchant.getCode());
			otcUserExt.setUpdateTime(now);
			otcUserExtMapper.updateByPrimaryKey(otcUserExt);
			return ReturnResult.SUCCESS("通过成功！");
		} catch (Exception e) {
			logger.error("通过异常，id："+otcMerchant.getId(),e);
			return ReturnResult.FAILUER("通过失败！");
		}
	}

	@Override
	public ReturnResult refuseOtcMerchant(OtcMerchant otcMerchant) {
		try {
			//更新商户表
			Date now = new Date();
			otcMerchant.setStatus(OtcMerchantStatusEnum.Refused.getCode());
			otcMerchant.setOperateTime(now);
			otcMerchantMapper.updateByPrimaryKey(otcMerchant);
			sendSMSForMerchants(otcMerchant.getUid(), "apply_merchants");
			return ReturnResult.SUCCESS("拒绝成功！");
		} catch (Exception e) {
			logger.error("拒绝异常，id："+otcMerchant.getId(),e);
			return ReturnResult.FAILUER("拒绝失败！");
		}
	}

	@Override
	@Transactional(value="flexibleTransMgr" ,isolation = Isolation.REPEATABLE_READ, propagation=Propagation.REQUIRED, rollbackFor = Exception.class)
	public ReturnResult removeOtcMerchant(OtcMerchant otcMerchant) throws BCException {
		try {
			//更新商户表
			Date now = new Date();
			otcMerchant.setStatus(OtcMerchantStatusEnum.Removed.getCode());
			otcMerchant.setOperateTime(now);
			otcMerchantMapper.updateByPrimaryKey(otcMerchant);
			//更新用户为普通商户
			OtcUserExt otcUserExt = otcUserExtMapper.selectByUserId(otcMerchant.getUid());
			if (otcUserExt == null) {
				return ReturnResult.FAILUER("otc扩展表中不存在该商户！");
			}
			otcUserExt.setOtcUserType(OtcUserTypeEnum.Ordinary_Merchant.getCode());
			otcUserExt.setUpdateTime(now);
			otcUserExtMapper.updateByPrimaryKey(otcUserExt);
			
			//归还押金
			UserOtcCoinWallet wallet = userOtcCoinWalletMapper.select(otcMerchant.getUid(), 9);
			int result = userOtcCoinWalletMapper.updateUnFrozenCAS(otcMerchant.getDeposit(), new Date(), wallet.getId(), wallet.getVersion());
			if (result <= 0) {
				throw new BCException("押金归还失败");
			}
			
			//下架所有广告
			List<OtcAdvert> advertList = otcAdvertMapper.selectAdvertByUid(otcMerchant.getUid());
			for (OtcAdvert otcAdvert : advertList) {
				//检查otc资产平衡
				if (!adminOtcService.otcBalance(otcAdvert.getUserId(), otcAdvert.getCoinId())) {
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
					UserOtcCoinWallet advertWallet = userOtcCoinWalletMapper.select(otcAdvert.getUserId(), otcAdvert.getCoinId());
					userOtcCoinWalletMapper.updateUnFrozenCAS(MathUtils.toScaleNum(unfrozenVolume, digit), new Date(), advertWallet.getId(), advertWallet.getVersion());
				}
				//更新广告
				otcAdvert.setUpdateTime(new Date());
				otcAdvert.setStatus(Constant.OTC_ADVERT_OFF);
				otcAdvertMapper.updateOtcAdvert(otcAdvert);
			}
			return ReturnResult.SUCCESS("解除正常商户权限成功！");
		} catch (BCException e) {
			throw e;
		} catch (Exception e) {
			throw new BCException("解除正常商户权限失败");
		}
	}

	@Override
	@Transactional(value="flexibleTransMgr" ,isolation = Isolation.REPEATABLE_READ, propagation=Propagation.REQUIRED, rollbackFor = Exception.class)
	public ReturnResult prohibitOtcMerchant(OtcMerchant otcMerchant) {
		try {
			//更新商户表
			Date now = new Date();
			otcMerchant.setStatus(OtcMerchantStatusEnum.Prohibited.getCode());
			otcMerchant.setOperateTime(now);
			otcMerchantMapper.updateByPrimaryKey(otcMerchant);
			//更新用户为普通商户
			OtcUserExt otcUserExt = otcUserExtMapper.selectByUserId(otcMerchant.getUid());
			if (otcUserExt == null) {
				return ReturnResult.FAILUER("otc扩展表中不存在该商户！");
			}
			otcUserExt.setOtcUserType(OtcUserTypeEnum.Ordinary_Merchant.getCode());
			otcUserExt.setUpdateTime(now);
			otcUserExtMapper.updateByPrimaryKey(otcUserExt);
			//扣除押金
			UserOtcCoinWallet wallet = userOtcCoinWalletMapper.select(otcMerchant.getUid(), 9);
			wallet.setFrozen(MathUtils.sub(wallet.getFrozen(), otcMerchant.getDeposit()));
			int result = userOtcCoinWalletMapper.updateCAS(wallet);
			if (result <= 0) {
				throw new BCException("押金扣除失败");
			}
			OtcUserTransfer otcUserTransfer = new OtcUserTransfer();
			otcUserTransfer.setAmount(otcMerchant.getDeposit());
			otcUserTransfer.setCoinId(9);
			otcUserTransfer.setType(OtcTransferTypeEnum.otcMerchantDeposit.getCode());
			otcUserTransfer.setUserId(otcMerchant.getUid());
			otcUserTransfer.setCreateTime(Utils.getTimestamp());
			if (userOtcTransferMapper.insert(otcUserTransfer) <= 0) {
				throw new Exception("otc用户押金扣除失败uid = " + otcMerchant.getUid());
			}
			
			//下架所有广告
			List<OtcAdvert> advertList = otcAdvertMapper.selectAdvertByUid(otcMerchant.getUid());
			for (OtcAdvert otcAdvert : advertList) {
				//检查otc资产平衡
				if (!adminOtcService.otcBalance(otcAdvert.getUserId(), otcAdvert.getCoinId())) {
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
					UserOtcCoinWallet advertWallet = userOtcCoinWalletMapper.select(otcAdvert.getUserId(), otcAdvert.getCoinId());
					userOtcCoinWalletMapper.updateUnFrozenCAS(MathUtils.toScaleNum(unfrozenVolume, digit), new Date(), advertWallet.getId(), advertWallet.getVersion());
				}
				//更新广告
				otcAdvert.setUpdateTime(new Date());
				otcAdvert.setStatus(Constant.OTC_ADVERT_OFF);
				otcAdvertMapper.updateOtcAdvert(otcAdvert);
			}
			return ReturnResult.SUCCESS("解除恶意商户权限成功！");
		} catch (Exception e) {
			logger.error("解除恶意商户权限异常，id："+otcMerchant.getId(),e);
			return ReturnResult.FAILUER("解除恶意商户权限失败！");
		}
	}

	@Override
	public ReturnResult resumeOtcMerchant(OtcMerchant otcMerchant) {
		try {
			//更新商户表
			Date now = new Date();
			otcMerchant.setStatus(OtcMerchantStatusEnum.Removed.getCode());
			otcMerchant.setOperateTime(now);
			otcMerchantMapper.updateByPrimaryKey(otcMerchant);
			return ReturnResult.SUCCESS("恢复成功！");
		} catch (Exception e) {
			logger.error("恢复异常，id："+otcMerchant.getId(),e);
			return ReturnResult.FAILUER("恢复失败！");
		}
	}
	
	public void sendSMSForMerchants(Integer receiver, String modelId) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					FUser user = userMapper.selectByPrimaryKey(receiver);
					if (StringUtils.isEmpty(user.getFtelephone())) {
						return;
					}
					SendSmsReq sendSmsReq = new SendSmsReq();
					Map<String, Object> map = new HashMap<>();
					map.put("mobile", user.getFtelephone());
					FUserExtend userExtend = userExtendMapper.selectByUid(receiver);
					String lang = "zh_CN";
					if (userExtend != null) {
						lang = userExtend.getLanguage();
					}
					sendSmsReq.setParams(map);
					sendSmsReq.setBusinessType(modelId);
					sendSmsReq.setPhone(user.getFtelephone());
					sendSmsReq.setLang(lang);
					sendSmsReq.setPlatform("OTC");
					String url = chatProperties.getSmsUrl();
					RequestBody requestBody = FormBody.create(MediaType.parse("application/json; charset=utf-8")
			        , JSON.toJSONString(sendSmsReq));
					OkHttpClient client = new OkHttpClient.Builder()
			                .connectTimeout(10, TimeUnit.SECONDS)
			                .writeTimeout(10, TimeUnit.SECONDS)
			                .readTimeout(10, TimeUnit.SECONDS)
			                .build();
			        Response response=client.newCall(new Request.Builder()
						        .url(url).post(requestBody)
						        .build())
						        .execute();
			        if(response.code() == 200) {
			        	JSONObject binanceObject = JSON.parseObject(response.body().string());
						logger.info(binanceObject.toJSONString());
			        }
				} catch (Exception e) {
					logger.error("sendsms error,uid:{},orderNo:{},coinName:{},coinCount:{},ex:{}",receiver,e);
				}
			}
		}).start();
	}
}
