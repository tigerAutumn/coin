package com.qkwl.service.coin.service.impl;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.hotcoin.coin.utils.CoinRpcUtlis;
import com.qkwl.common.Enum.validate.BusinessTypeEnum;
import com.qkwl.common.Enum.validate.LocaleEnum;
import com.qkwl.common.dto.Enum.UserWhiteListTypeEnum;
import com.qkwl.common.dto.Enum.VirtualCapitalOperationInStatusEnum;
import com.qkwl.common.dto.Enum.VirtualCapitalOperationTypeEnum;
import com.qkwl.common.dto.Enum.orepool.OrepoolRecordStatusEnum;
import com.qkwl.common.dto.capital.FUserVirtualAddressDTO;
import com.qkwl.common.dto.capital.FVirtualCapitalOperationDTO;
import com.qkwl.common.dto.coin.SystemCoinType;
import com.qkwl.common.dto.orepool.OrepoolRecord;
import com.qkwl.common.dto.user.FUser;
import com.qkwl.common.dto.user.FUserExtend;
import com.qkwl.common.dto.whiteList.UserWhiteList;
import com.qkwl.common.exceptions.BCException;
import com.qkwl.common.framework.validate.ValidateHelper;
import com.qkwl.common.match.MathUtils;
import com.qkwl.service.coin.mapper.FUserExtendMapper;
import com.qkwl.service.coin.mapper.FUserMapper;
import com.qkwl.service.coin.mapper.FUserVirtualAddressMapper;
import com.qkwl.service.coin.mapper.FVirtualCapitalOperationMapper;
import com.qkwl.service.coin.mapper.OrepoolRecordMapper;
import com.qkwl.service.coin.mapper.UserWhiteListMapper;
import com.qkwl.service.coin.service.RechargePushService;
import com.qkwl.service.coin.util.WalletUtils;

@Service("rechargePushServiceTx")
public class RechargePushServiceTx{
	private static final Logger logger = LoggerFactory.getLogger(RechargePushServiceTx.class);

	@Autowired
	private FVirtualCapitalOperationMapper fVirtualCapitalOperationMapper;
	
	@Autowired
	private FUserVirtualAddressMapper fUserVirtualAddressMapper;
	
	@Autowired
	private FUserMapper userMapper;
	
	@Autowired
	private UserWhiteListMapper userWhiteListMapper;
	
	@Autowired
	private WalletUtils walletUtils;
	
	@Autowired
	private OrepoolRecordMapper orepoolRecordMapper;
	
	@Autowired
	private FUserExtendMapper userExtendMapper;
	
	@Autowired
	private ValidateHelper validateHelper;

	
	//充值记录入库
	//@Transactional(isolation = Isolation.REPEATABLE_READ, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@Transactional(value="xaTransMgr", isolation = Isolation.REPEATABLE_READ, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public boolean insertRecord(SystemCoinType coinType,FVirtualCapitalOperationDTO record) {
		//查看是哪个用户充值
		Integer uid = null;
		if(coinType.getIsUseMemo()) {
			if(StringUtils.isNumeric(record.getMemo())) {
				FUser user = userMapper.selectByPrimaryKey(Integer.valueOf(record.getMemo()));
				if(user != null) {
					uid = user.getFid();
				}
			}
		}else {
			FUserVirtualAddressDTO userAddress = fUserVirtualAddressMapper.selectByCoinAndAddress(record.getAddressCoinid(), record.getFrechargeaddress());
			if(userAddress != null) {
				uid = userAddress.getFuid();
			}
		}
		
		if(uid != null) {
			record.setFhasowner(true);
			record.setFuid(uid);
			//如果是创新区
			if(coinType.getIsInnovateAreaCoin()) {
				List<UserWhiteList> selectByUserAndCoinAndType = userWhiteListMapper.selectByUserAndCoinAndType(uid, coinType.getId(), UserWhiteListTypeEnum.RECHARGE_OF_INNOVATION_ZONE.getCode());
				if(selectByUserAndCoinAndType != null && selectByUserAndCoinAndType.size() > 0) {
					//如果是在白名单中就不做冻结
					record.setIsFrozen(false);
				}else {
					record.setIsFrozen(true);
				}
			}
		}else{
			record.setFhasowner(false);
		}
		//入库
		fVirtualCapitalOperationMapper.insert(record);
		return true;
	}
	
	//充值到账
	//@Transactional( isolation = Isolation.REPEATABLE_READ, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@Transactional(value="xaTransMgr", isolation = Isolation.REPEATABLE_READ, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public boolean updateRecord(SystemCoinType coinType,FVirtualCapitalOperationDTO record,FVirtualCapitalOperationDTO oldRecord) throws BCException{
		//如果是充值成功的就直接返回
		if(oldRecord.getFstatus() != null && oldRecord.getFstatus().equals(VirtualCapitalOperationInStatusEnum.SUCCESS)) {
			return true;
		}
		//如果确认数没到就认为应该是上一次推送失败，重新推了一次，如果充值地址没有客户使用也直接返回
		if(record.getFconfirmations().compareTo(coinType.getConfirmations()) < 0 || oldRecord.getFhasowner() == null || !oldRecord.getFhasowner() ) {
			return true;
		}
		// 更新订单
		oldRecord.setFstatus(VirtualCapitalOperationInStatusEnum.SUCCESS);
		oldRecord.setFconfirmations(record.getFconfirmations());
		oldRecord.setFupdatetime(new Date());
		if (this.fVirtualCapitalOperationMapper.updateByPrimaryKey(oldRecord) <= 0) {
			throw new BCException("更改订单失败");
		}
		BigDecimal amount = oldRecord.getFamount();
		if(amount.scale() > MathUtils.DEF_DIV_SCALE) {
			amount = amount.setScale(10,  BigDecimal.ROUND_HALF_UP);
		}
		//充值成功
		if(oldRecord.getIsFrozen()) {
			if(!walletUtils.change(oldRecord.getFuid(), oldRecord.getWalletCoinId(), WalletUtils.depositFrozen_add, amount)) {
				throw new BCException("更改钱包错误");
			}
			//修改用户创新区锁仓记录
			OrepoolRecord orepoolRecord = new OrepoolRecord();
			orepoolRecord.setUserId(oldRecord.getFuid());
			orepoolRecord.setLockCoinId(oldRecord.getFcoinid());
			List<OrepoolRecord> innovationRecordList = orepoolRecordMapper.getInnovationRecord(orepoolRecord);
			for (OrepoolRecord orepool : innovationRecordList) {
				orepool.setStatus(OrepoolRecordStatusEnum.counting.getCode());
				orepool.setUpdateTime(new Date());
				orepoolRecordMapper.update(orepool);
			}
		}else{
			if(!walletUtils.change(oldRecord.getFuid(), oldRecord.getWalletCoinId(), WalletUtils.total_add, amount)) {
				throw new BCException("更改钱包错误");
			}
		}
		
		FUser user = userMapper.selectByPrimaryKey(oldRecord.getFuid());
		FUserExtend userExtend = userExtendMapper.selectByUid(oldRecord.getFuid());
		Integer langCode = 1;
		if (userExtend != null) {
			langCode = LocaleEnum.getCodeByName(userExtend.getLanguage());
		}
		// 发送短信
		if (user.getFistelephonebind()) {
			try {
				validateHelper.smsCoinToAccount(user.getFareacode(), user.getFtelephone(), user.getFplatform(),
						BusinessTypeEnum.SMS_CHARGE_TO_ACCOUNT.getCode(), amount, coinType.getShortName(),
						langCode);
			} catch (Exception e) {
				logger.error("smsCoinToAccound err");
				e.printStackTrace();
			}
		}
		return true;
	}
}
