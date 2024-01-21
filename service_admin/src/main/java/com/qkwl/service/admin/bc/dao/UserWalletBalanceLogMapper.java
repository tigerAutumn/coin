package com.qkwl.service.admin.bc.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.qkwl.common.dto.wallet.UserWalletBalanceLog;
import com.qkwl.common.dto.wallet.UserWalletBalanceLogDto;

@Mapper
public interface UserWalletBalanceLogMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(UserWalletBalanceLog record);

    int insertSelective(UserWalletBalanceLog record);

    UserWalletBalanceLog selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(UserWalletBalanceLog record);

    int updateByPrimaryKey(UserWalletBalanceLog record);
    
    
	/**
	 * 统计流水表表
	 * @param params 
	 * @return 
	 */
    List<UserWalletBalanceLog> sumUserWalletBalanceLog(Map<String,Object> params);
    
    
    /**
	 * 统计流水表表
	 * @param params 
	 * @return 
	 */
    List<UserWalletBalanceLogDto> selectList(Map<String,Object> params);
}