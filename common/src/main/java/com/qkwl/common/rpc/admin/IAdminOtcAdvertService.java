package com.qkwl.common.rpc.admin;

import java.util.List;

import com.qkwl.common.dto.capital.FUserBankinfoDTO;
import com.qkwl.common.dto.common.Pagination;
import com.qkwl.common.dto.otc.OtcAdvert;

public interface IAdminOtcAdvertService {

	/**
	 * 分页查询otc广告
	 * @param pageParam 分页参数
	 * @param otcAdvert 实体参数
	 * @return 分页查询记录列表
	 */
	public Pagination<OtcAdvert> selectOtcAdvertPageList(Pagination<OtcAdvert> pageParam, OtcAdvert otcAdvert);
	
	/**
	 * 根据广告id查询支付信息
	 * @param id
	 * @return
	 */
	public List<FUserBankinfoDTO> getBankinfoList(Integer id);
	
	/**
	 * 根据广告id
	 * @param id
	 * @return
	 */
	public OtcAdvert selectAdvertById(Integer id);
	
	/**
	 * 更新otc广告
	 * @param advert
	 */
	public void updateAdvert(OtcAdvert advert);
}
