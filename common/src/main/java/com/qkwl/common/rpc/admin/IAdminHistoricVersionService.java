package com.qkwl.common.rpc.admin;

import com.qkwl.common.dto.common.Pagination;
import com.qkwl.common.dto.system.HistoricVersion;
import com.qkwl.common.util.ReturnResult;

public interface IAdminHistoricVersionService {

	/**
	 * 分页查询历史版本
	 * @param pageParam 分页参数
	 * @param HistoricVersion 实体参数
	 * @return 分页查询记录列表
	 */
	public Pagination<HistoricVersion> selectHistoricVersionPageList(Pagination<HistoricVersion> pageParam, HistoricVersion historicVersion);
	
	/**
	 * 根据id查询历史版本
	 * @param 
	 * @return 法币记录
	 */
	public HistoricVersion selectHistoricVersionById(Integer id);
	
	/**
     * 新增历史版本
     *
     * @param historicVersion 历史版本
     * @return true：成功，false：失败
     */
	public ReturnResult insertHistoricVersion(HistoricVersion historicVersion);
	
	/**
	 * 删除历史版本
	 * @param historicVersion
	 */
	public ReturnResult deleteHistoricVersion(HistoricVersion historicVersion);
	
	/**
	 * 修改历史版本
	 * @param historicVersion
	 */
	public ReturnResult updateHistoricVersion(HistoricVersion historicVersion);
}
