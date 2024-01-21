package com.qkwl.common.rpc.admin;

import com.qkwl.common.dto.common.Pagination;
import com.qkwl.common.dto.orepool.OrepoolPlan;
import com.qkwl.common.dto.orepool.OrepoolRecord;
import com.qkwl.common.util.ReturnResult;

public interface IAdminOrepoolService {

	/**
	 * 分页查询矿池计划
	 * @param pageParam 分页参数
	 * @param orepoolPlan 实体参数
	 * @return 分页查询计划列表
	 */
	public Pagination<OrepoolPlan> selectPlanPageList(Pagination<OrepoolPlan> pageParam, OrepoolPlan orepoolPlan);
	
	/**
	 * 分页查询矿池记录
	 * @param pageParam 分页参数
	 * @param orepoolRecord 实体参数
	 * @return 分页查询记录列表
	 */
	public Pagination<OrepoolRecord> selectRecordPageList(Pagination<OrepoolRecord> pageParam, OrepoolRecord orepoolRecord);
	
	/**
	 * 根据id查询计划
	 * @param 
	 * @return 矿池计划
	 */
	public OrepoolPlan selectPlanById(Integer id);
	
	/**
	 * 删除计划
	 * @param orepoolPlan
	 */
	public ReturnResult deletePlan(OrepoolPlan orepoolPlan);
	
	/**
	 * 解锁计划
	 * @param orepoolPlan
	 */
	public ReturnResult unlockPlan(OrepoolPlan orepoolPlan) throws Exception;
	
	/**
     * 新增矿池计划
     *
     * @param OrepoolPlan 矿池计划
     * @return true：成功，false：失败
     */
	public ReturnResult insertPlan(OrepoolPlan orepoolPlan);
	
	/**
	 * 开启矿池计划
	 * @param orepoolPlan
	 */
	public ReturnResult openOrepoolPlan(OrepoolPlan orepoolPlan);
	
	/**
	 * 禁止矿池计划
	 * @param orepoolPlan
	 */
	public ReturnResult forbidOrepoolPlan(OrepoolPlan orepoolPlan);
	
	/**
	 * 更新计划
	 * @param orepoolPlan
	 * @return
	 */
	public ReturnResult updateOrepoolPlan(OrepoolPlan orepoolPlan);
}
