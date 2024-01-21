package com.qkwl.common.rpc.admin;

import com.qkwl.common.dto.common.Pagination;
import com.qkwl.common.dto.otc.OtcMerchant;
import com.qkwl.common.exceptions.BCException;
import com.qkwl.common.util.ReturnResult;

public interface IAdminOtcMerchantService {

	/**
	 * 分页查询otc商户
	 * @param pageParam 分页参数
	 * @param otcMerchant 实体参数
	 * @return 分页查询记录列表
	 */
	Pagination<OtcMerchant> selectOtcMerchantPageList(Pagination<OtcMerchant> pageParam, OtcMerchant otcMerchant);
	
	/**
	 * 根据id查询商户
	 * @param 
	 * @return 商户记录
	 */
	OtcMerchant selectOtcMerchantById(Integer id);
	
	ReturnResult passOtcMerchant(OtcMerchant otcMerchant);
	
	ReturnResult refuseOtcMerchant(OtcMerchant otcMerchant);
	
	ReturnResult removeOtcMerchant(OtcMerchant otcMerchant) throws BCException;
	
	ReturnResult prohibitOtcMerchant(OtcMerchant otcMerchant);
	
	ReturnResult resumeOtcMerchant(OtcMerchant otcMerchant);
}
