package com.qkwl.service.admin.bc.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.qkwl.common.dto.airdrop.AirdropRecord;
@Mapper
public interface AirdropRecordMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(AirdropRecord record);

    int insertSelective(AirdropRecord record);

    AirdropRecord selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(AirdropRecord record);

    int updateByPrimaryKey(AirdropRecord record);
    
	/**
	 * 查询空投记录
	 * @param uid
	 * @return
	 */
    List<AirdropRecord> selectSumByUid(@Param("userId")Integer userId);
}