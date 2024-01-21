package com.qkwl.common.rpc.admin;

import com.qkwl.common.dto.common.Pagination;
import com.qkwl.common.dto.transDict.SystemTransDict;
import com.qkwl.common.util.ReturnResult;

public interface IAdminTransDictService {

	/**
	 * 分页查询港盛字典
	 * @param pageParam 分页参数
	 * @param systemTransDict 实体参数
	 * @return 分页查询记录列表
	 */
	public Pagination<SystemTransDict> selectSystemTransDictPageList(Pagination<SystemTransDict> pageParam);
	
	/**
	 * 根据id查询港盛字典
	 * @param 
	 * @return 港盛字典记录
	 */
	public SystemTransDict selectSystemTransDictById(Integer id);
	
	/**
     * 新增港盛字典
     *
     * @param systemTransDict 港盛字典
     * @return true：成功，false：失败
     */
	public ReturnResult insertSystemTransDict(SystemTransDict systemTransDict);
	
	/**
	 * 删除港盛字典
	 * @param systemTransDict
	 */
	public ReturnResult deleteSystemTransDict(SystemTransDict systemTransDict);
	
	/**
	 * 修改港盛字典
	 * @param systemTransDict
	 */
	public ReturnResult updateSystemTransDict(SystemTransDict systemTransDict);
}
