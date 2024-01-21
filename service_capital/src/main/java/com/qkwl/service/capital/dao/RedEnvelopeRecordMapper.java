package com.qkwl.service.capital.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.qkwl.common.dto.capital.RedEnvelopeRecord;
import com.qkwl.common.dto.wallet.UserCoinWallet;

@Mapper
public interface RedEnvelopeRecordMapper {

	Integer insert(RedEnvelopeRecord redEnvelopeRecord);
	
	List<RedEnvelopeRecord> selectByRedEnvelopeNo(RedEnvelopeRecord redEnvelopeRecord);
	
	Integer updateReceived(RedEnvelopeRecord redEnvelopeRecord);
	
	Integer updateIsMost(RedEnvelopeRecord redEnvelopeRecord);
	
	int countRecordList(Map<String, Object> map);
	
	List<RedEnvelopeRecord> getRecordList(Map<String, Object> map);
	
	List<UserCoinWallet> getRecordTotal(RedEnvelopeRecord redEnvelopeRecord);
	
	int getBestCount(RedEnvelopeRecord redEnvelopeRecord);
	
}
