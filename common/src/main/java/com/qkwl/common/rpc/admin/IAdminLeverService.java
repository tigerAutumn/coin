package com.qkwl.common.rpc.admin;

import java.util.List;
import java.util.Map;

import com.qkwl.common.dto.lever.LeverRecord;
import com.qkwl.common.dto.lever.SystemLeverSetting;

public interface IAdminLeverService {

	
	
	/**
	 * 查询c2c设置
	 * @return
	 */
	List<SystemLeverSetting> getLeverSetting();
	
	/**
	 * 通过Id查询c2c设置
	 * @return
	 */
	SystemLeverSetting getLeverSettingById(Integer id);
	
	/**
	 * 通过id修改设置
	 * @return
	 */
	int updateLeverSetting(SystemLeverSetting systemLeverSetting,Integer adminId);
	
	/**
	 * 统计lever资金互转
	 * @param uid
	 * @return  in：转入金额，out：转出金额
	 */
	public Map<Integer,LeverRecord> sumLeverTransfer(Integer uid);


}
