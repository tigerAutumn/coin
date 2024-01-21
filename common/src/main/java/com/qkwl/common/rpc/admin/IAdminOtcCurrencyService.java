package com.qkwl.common.rpc.admin;

import com.qkwl.common.dto.common.Pagination;
import com.qkwl.common.dto.otc.OtcCurrency;
import com.qkwl.common.util.ReturnResult;

public interface IAdminOtcCurrencyService {

	/**
	 * 分页查询otc法币币种
	 * @param pageParam 分页参数
	 * @param otcCurrency 实体参数
	 * @return 分页查询记录列表
	 */
	public Pagination<OtcCurrency> selectOtcCurrencyPageList(Pagination<OtcCurrency> pageParam, OtcCurrency otcCurrency);
	
	/**
	 * 根据id查询法币
	 * @param 
	 * @return 法币记录
	 */
	public OtcCurrency selectOtcCurrencyById(Integer id);
	
	/**
     * 新增法币
     *
     * @param OtcCurrency 法币
     * @return true：成功，false：失败
     */
	public ReturnResult insertOtcCurrency(OtcCurrency otcCurrency);
	
	/**
	 * 删除法币
	 * @param commission
	 */
	public ReturnResult deleteOtcCurrency(OtcCurrency otcCurrency);
	
	/**
	 * 修改法币
	 * @param commission
	 */
	public ReturnResult updateOtcCurrency(OtcCurrency otcCurrency);
}
