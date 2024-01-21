package com.qkwl.common.rpc.admin;

import java.util.List;

import com.qkwl.common.dto.common.Pagination;
import com.qkwl.common.dto.matchGroup.MatchGroup;
import com.qkwl.common.util.ReturnResult;

public interface IAdminMatchGroupService {

	/**
	 * 查询所有相应的交易对
	 * @return
	 */
	public  List<MatchGroup> selectAll();
	
	/**
	 * 查询其他交易对组
	 * @return
	 */
	public  List<MatchGroup> selectOthers(Integer id);
	
	/**
	 * 分页查询交易对组
	 * @param pageParam 分页参数
	 * @param matchGroup 实体参数
	 * @return 分页查询记录列表
	 */
	public Pagination<MatchGroup> selectMatchGroupPageList(Pagination<MatchGroup> pageParam, MatchGroup matchGroup);
	
	/**
	 * 根据id交易对组
	 * @param id
	 * @return 交易对组
	 */
	public MatchGroup selectMatchGroupById(Integer id);
	
	/**
	 * 根据交易对组名称查询交易对组
	 * @param groupName
	 * @return 交易对组
	 */
	public MatchGroup selectMatchGroupByGroupName(String groupName);
	
	/**
     * 新增交易对组
     *
     * @param matchGroup 交易对组
     * @return true：成功，false：失败
     */
	public ReturnResult insertMatchGroup(MatchGroup matchGroup);
	
	/**
	 * 删除交易对组
	 * @param matchGroup
	 */
	public ReturnResult deleteMatchGroup(MatchGroup matchGroup);
	
	/**
	 * 修改交易对组
	 * @param matchGroup
	 */
	public ReturnResult updateMatchGroup(MatchGroup MatchGroup);
}
