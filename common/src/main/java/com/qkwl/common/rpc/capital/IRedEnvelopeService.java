package com.qkwl.common.rpc.capital;

import java.math.BigDecimal;

import com.qkwl.common.dto.capital.RedEnvelope;
import com.qkwl.common.dto.capital.RedEnvelopeRecord;
import com.qkwl.common.dto.capital.RedEnvelopeResp;
import com.qkwl.common.dto.common.Pagination;

public interface IRedEnvelopeService {

	String send(RedEnvelope redEnvelope);
	
	RedEnvelope getDetail(String id);
	
	int getIsReceived(RedEnvelopeRecord record);
	
	RedEnvelopeResp receive(Integer uid, String id);
	
	RedEnvelopeResp getReceiveDetail(String id);
	
	Pagination<RedEnvelopeRecord> getRecordList(Pagination<RedEnvelopeRecord> pageParam, RedEnvelopeRecord redEnvelopeRecord);
	
	BigDecimal getRecordTotal(RedEnvelopeRecord redEnvelopeRecord);
	
	int getBestCount(RedEnvelopeRecord redEnvelopeRecord);
	
	BigDecimal getEnvelopeTotal(RedEnvelope redEnvelope);
	
	Pagination<RedEnvelope> getEnvelopeList(Pagination<RedEnvelope> pageParam, RedEnvelope redEnvelope);
}
