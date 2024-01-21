package com.qkwl.service.activity.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.qkwl.common.dto.capital.RedEnvelope;
import com.qkwl.common.dto.wallet.UserCoinWallet;

@Mapper
public interface RedEnvelopeMapper {

	int insert(RedEnvelope redEnvelope);
	
	RedEnvelope selectByRedEnvelopeNo(String redEnvelopeNo);
	
	int countEnvelopeList(Map<String, Object> map);
	
	List<RedEnvelope> getEnvelopeList(Map<String, Object> map);
	
	List<UserCoinWallet> getEnvelopeTotal(RedEnvelope redEnvelope);
	
	List<RedEnvelope> selectOverdueEnvelope();
	
	Integer updateStatus(RedEnvelope redEnvelope);
}
