package com.qkwl.service.capital.job;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.qkwl.common.dto.Enum.ErrorCodeEnum;
import com.qkwl.common.dto.Enum.RedEnvelopeStatusEnum;
import com.qkwl.common.dto.Enum.UserWalletBalanceLogDirectionEnum;
import com.qkwl.common.dto.Enum.UserWalletBalanceLogFieldEnum;
import com.qkwl.common.dto.Enum.UserWalletBalanceLogTypeEnum;
import com.qkwl.common.dto.capital.RedEnvelope;
import com.qkwl.common.dto.wallet.UserWalletBalanceLog;
import com.qkwl.common.exceptions.BizException;
import com.qkwl.common.match.MathUtils;
import com.qkwl.common.result.Result;
import com.qkwl.common.rpc.capital.IUserWalletService;
import com.qkwl.service.capital.dao.RedEnvelopeMapper;
import com.qkwl.service.capital.dao.UserWalletBalanceLogMapper;

@Component
public class RedEnvelopeJob {
	
	/**
	 * 日志
	 */
	private static final Logger logger = LoggerFactory.getLogger(RedEnvelopeJob.class);
	
	@Autowired
	private RedEnvelopeMapper redEnvelopeMapper;
	@Autowired
	private IUserWalletService userWalletService;
	@Autowired
	private UserWalletBalanceLogMapper userWalletBalanceLogMapper;

	@Scheduled(cron="0 0/1 * * * ? ")
	public void work() {
		//查询过期未发完的红包
		List<RedEnvelope> list = redEnvelopeMapper.selectOverdueEnvelope();
		for (RedEnvelope redEnvelope : list) {
			try {
				returnRedEnvelope(redEnvelope);
			} catch (Exception e) {
				e.printStackTrace();
				logger.error("退还红包异常，红包id:{}", redEnvelope.getId());
			}
		}
	}
	
	@Transactional(value="flexibleTransMgr", isolation = Isolation.REPEATABLE_READ, propagation= Propagation.REQUIRED, rollbackFor = Exception.class)
	private void returnRedEnvelope(RedEnvelope redEnvelope) {
		BigDecimal amount = MathUtils.sub(redEnvelope.getAmount(), redEnvelope.getReceiveAmount());
		
		Result frozen2TotalResult = userWalletService.frozen2Total(redEnvelope.getUid(), redEnvelope.getCoinId(), amount);
		if (!frozen2TotalResult.getSuccess()) {
			logger.error("冻结转可用失败,红包id:{}", redEnvelope.getId());
			throw new BizException(ErrorCodeEnum.UNFROZEN_FAILED);
		}
		
		Date now = new Date();
		// 领取红包流水
		UserWalletBalanceLog addTotalLog = new UserWalletBalanceLog();
		addTotalLog.setUid(redEnvelope.getUid());
		addTotalLog.setCoinId(redEnvelope.getCoinId());
		addTotalLog.setFieldId(UserWalletBalanceLogFieldEnum.total.getValue());
		addTotalLog.setChange(amount);
		addTotalLog.setSrcId(redEnvelope.getId());
		addTotalLog.setSrcType(UserWalletBalanceLogTypeEnum.RETURN_RED_ENVELOPE.getCode());
		addTotalLog.setDirection(UserWalletBalanceLogDirectionEnum.in.getValue());
		addTotalLog.setCreatetime(now);
		addTotalLog.setCreatedatestamp(now.getTime());
		userWalletBalanceLogMapper.insert(addTotalLog);
		
		redEnvelope.setStatus(RedEnvelopeStatusEnum.RETURN.getCode());
		redEnvelopeMapper.updateStatus(redEnvelope);
	}
} 
