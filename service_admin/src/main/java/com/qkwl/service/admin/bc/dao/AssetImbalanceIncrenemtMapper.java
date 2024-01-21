package com.qkwl.service.admin.bc.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.qkwl.common.dto.capital.AssetImbalance;
import com.qkwl.common.dto.capital.AssetImbalanceIncrenemt;
@Mapper
public interface AssetImbalanceIncrenemtMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(AssetImbalanceIncrenemt record);

    int insertSelective(AssetImbalanceIncrenemt record);

    AssetImbalanceIncrenemt selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(AssetImbalanceIncrenemt record);

    int updateByPrimaryKey(AssetImbalanceIncrenemt record);
    

	/**
	 * 查询记录
	 * @param uid 用户id
	 * @param coinid 币种id
	 * @return 
	 */
    AssetImbalanceIncrenemt selectByUidAndCoinId(@Param("uid") Integer uid,@Param("coinid") Integer coinid);
    
	/**
	 * 查询记录
	 * @param AssetImbalanceIncrenemt
	 * @return 
	 */
    List<AssetImbalanceIncrenemt> selectByParams(AssetImbalanceIncrenemt record);
    
	/**
	 * 删除记录
	 * @param uid 用户id
	 * @param coinid 币种id
	 * @return 
	 */
    int deleteByUidAndCoinId(@Param("userId") Integer uid,@Param("coinId") Integer coinid);
}