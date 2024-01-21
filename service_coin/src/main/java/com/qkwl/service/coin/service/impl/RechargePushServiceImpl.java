package com.qkwl.service.coin.service.impl;

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
import com.qkwl.service.coin.mapper.FUserExtendMapper;
import com.qkwl.service.coin.mapper.FUserMapper;
import com.qkwl.service.coin.mapper.FUserVirtualAddressMapper;
import com.qkwl.service.coin.mapper.FVirtualCapitalOperationMapper;
import com.qkwl.service.coin.mapper.OrepoolRecordMapper;
import com.qkwl.service.coin.mapper.UserWhiteListMapper;
import com.qkwl.service.coin.service.RechargePushService;
import com.qkwl.service.coin.util.WalletUtils;

@Service("rechargePushService")
public class RechargePushServiceImpl implements RechargePushService {
	private static final Logger logger = LoggerFactory.getLogger(RechargePushServiceImpl.class);

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
	
	@Autowired
	private RechargePushServiceTx rechargePushServiceTx;

	@Override
	public boolean handlingRechargePush(SystemCoinType coinType,FVirtualCapitalOperationDTO record) throws BCException {
		List<FVirtualCapitalOperationDTO> selectByTxid = fVirtualCapitalOperationMapper.selectByTxid(record.getFuniquenumber());
		
		if(selectByTxid == null || selectByTxid.size() == 0) {
			//如果记录不存在就添加
			return rechargePushServiceTx.insertRecord(coinType, record);
		}else {
			//更新确认数入账
			return true;
		}
	}
	
	
	@Override
	public boolean handlingConfirmRecharge(SystemCoinType coinType,FVirtualCapitalOperationDTO record) throws BCException {
		List<FVirtualCapitalOperationDTO> selectByTxid = fVirtualCapitalOperationMapper.selectByTxid(record.getFuniquenumber());
		if(selectByTxid == null || selectByTxid.size() == 0) {
			//如果记录不存在就添加
			if(!rechargePushServiceTx.insertRecord(coinType, record)) {
				throw new BCException("充值入库失败");
			}
			selectByTxid = fVirtualCapitalOperationMapper.selectByTxid(record.getFuniquenumber());
		}
		//更新确认数入账
		return rechargePushServiceTx.updateRecord(coinType, record,selectByTxid.get(0));
		
	}
	
	@Override
	public boolean updateConfirm(SystemCoinType coinType) {
		BigInteger blockNumber = null;
		try {
			blockNumber = CoinRpcUtlis.getBlockNumber(coinType);
		} catch (Exception e) {
			logger.error("查询区块高度异常,symbol:"+coinType.getShortName(),e);
			return false;
		}
		if(blockNumber == null) {
			return false;
		}
		List<Integer> statusList = new ArrayList<Integer>();
		statusList.add(VirtualCapitalOperationInStatusEnum.WAIT_0);
		statusList.add(VirtualCapitalOperationInStatusEnum.WAIT_1);
		statusList.add(VirtualCapitalOperationInStatusEnum.WAIT_2);
		int updateConfirm = fVirtualCapitalOperationMapper.updateConfirm(coinType.getId(), VirtualCapitalOperationTypeEnum.COIN_IN.getCode(), blockNumber, statusList);
		if(updateConfirm > 0) {
			return true;
		}
		return false;
	}

	@Override
	public boolean rechargeArrivals(SystemCoinType coinType) {
		List<Integer> statusList = new ArrayList<Integer>();
		statusList.add(VirtualCapitalOperationInStatusEnum.WAIT_0);
		statusList.add(VirtualCapitalOperationInStatusEnum.WAIT_1);
		statusList.add(VirtualCapitalOperationInStatusEnum.WAIT_2);
		List<FVirtualCapitalOperationDTO> seletcByParam = fVirtualCapitalOperationMapper.seletcByParam(coinType.getId(), coinType.getConfirmations(), VirtualCapitalOperationTypeEnum.COIN_IN.getCode(), statusList);
		if(seletcByParam == null || seletcByParam.size() == 0) {
			return false;
		}
		for (FVirtualCapitalOperationDTO record : seletcByParam) {
			try {
				//去钱包机查询交易
				FVirtualCapitalOperationDTO depositRecord = CoinRpcUtlis.getDepositRecord(coinType, record);
				if(depositRecord == null) {
					continue;
				}
				//入帐
				rechargePushServiceTx.updateRecord(coinType, depositRecord, record);
			} catch (Exception e) {
				logger.error("更新记录失败recordid:"+record.getFid(),e);
			}
		}
		return true;
	};
}
