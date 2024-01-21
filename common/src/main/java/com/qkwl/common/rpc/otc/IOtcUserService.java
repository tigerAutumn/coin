package com.qkwl.common.rpc.otc;

import com.qkwl.common.dto.otc.OtcMerchant;
import com.qkwl.common.dto.otc.OtcUserExt;
import com.qkwl.common.dto.user.FUser;
import com.qkwl.common.exceptions.BCException;
import com.qkwl.common.result.Result;
import com.qkwl.common.util.ReturnResult;

/*
 * otc 广告
 */
public interface IOtcUserService {

	/**
	 * 查询用户otc详情
	 * @param userId
	 * @return
	 */
	OtcUserExt getOtcUserExt(Integer userId);
	
	
	void update(OtcUserExt otcUserExt);
	
	Result applyAuthentication(OtcMerchant otcMerchant) throws BCException;
	
	OtcMerchant getMerchantByUid(Integer uid);
	
	FUser getUser(Integer uid);
}
