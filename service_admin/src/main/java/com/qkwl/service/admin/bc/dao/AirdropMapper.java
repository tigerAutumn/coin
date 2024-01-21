package com.qkwl.service.admin.bc.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.qkwl.common.dto.airdrop.Airdrop;

@Mapper
public interface AirdropMapper {

	/**
     * 空投活动分页查询的总记录数
     * @param map 参数map
     * @return 查询记录数
     */
	int countAirdropListByParam(Map<String, Object> map);
	
	/**
     * 空投活动分页查询
     * @param map 参数map
     * @return 用户列表
     */
	List<Airdrop> getAirdropPageList(Map<String, Object> map);
	
	/**
     * 插入
     * @param record
     * @return
     */
    int insert(Airdrop airdrop);
	
	/**
	 * 根据id查询空投活动
	 * @param 
	 * @return 空投活动
	 */
	public Airdrop selectAirdropById(Integer id);
	
	/**
	 * 更新空投活动
	 * @param 
	 * @return 空投活动
	 */
	public void updateAirdrop(Airdrop airdrop);
	
	/**
	 * 删除空投活动
	 * @param 
	 * @return 空投活动
	 */
	public void deleteAirdrop(Airdrop airdrop);
	
	/**
	 * 查询所有空投活动
	 */
	public List<Airdrop> selectAllAirdrop();
}
