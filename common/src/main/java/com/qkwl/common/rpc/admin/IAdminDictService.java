package com.qkwl.common.rpc.admin;

import com.qkwl.common.dto.common.Pagination;
import com.qkwl.common.dto.dict.DictGroup;
import com.qkwl.common.dto.dict.DictItem;
import com.qkwl.common.dto.dict.DictItemAttr;
import com.qkwl.common.util.ReturnResult;


public interface IAdminDictService {

	/**
	 * 分页查询交易市场
	 */
	public Pagination<DictGroup> selectDictGroupPageList(Pagination<DictGroup> pageParam);
	
	/**
	 * 根据id查询交易市场
	 */
	public DictGroup selectDictGroupById(Integer Id);
	
	/**
     * 新增市场
     */
	public ReturnResult insertDictGroup(DictGroup dictGroup);
	
	/**
	 * 删除市场
	 */
	public ReturnResult deleteDictGroup(DictGroup dictGroup);
	
	/**
	 * 修改市场
	 */
	public ReturnResult updateDictGroup(DictGroup dictGroup);
	
	/**
	 * 分页查询交易定义
	 */
	public Pagination<DictItem> selectDictItemPageList(Pagination<DictItem> pageParam);
	
	/**
	 * 根据id查询交易定义
	 */
	public DictItem selectDictItemById(Integer Id);
	
	/**
     * 新增定义
     */
	public ReturnResult insertDictItem(DictItem dictItem);
	
	/**
	 * 删除定义
	 */
	public ReturnResult deleteDictItem(DictItem dictItem);
	
	/**
	 * 修改定义
	 */
	public ReturnResult updateDictItem(DictItem dictItem);
	
	/**
	 * 分页查询交易定义
	 */
	public Pagination<DictItemAttr> selectDictItemAttrPageList(Pagination<DictItemAttr> pageParam);
	
	/**
	 * 根据id查询交易定义
	 */
	public DictItemAttr selectDictItemAttrById(Integer Id);
	
	/**
     * 新增定义
     */
	public ReturnResult insertDictItemAttr(DictItemAttr dictItemAttr);
	
	/**
	 * 删除定义
	 */
	public ReturnResult deleteDictItemAttr(DictItemAttr dictItemAttr);
	
	/**
	 * 修改定义
	 */
	public ReturnResult updateDictItemAttr(DictItemAttr dictItemAttr);
}
