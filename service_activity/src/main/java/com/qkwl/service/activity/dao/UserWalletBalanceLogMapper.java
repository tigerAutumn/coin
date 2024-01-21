package com.qkwl.service.activity.dao;

import java.math.BigDecimal;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.qkwl.common.dto.wallet.UserWalletBalanceLog;

@Mapper
public interface UserWalletBalanceLogMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(UserWalletBalanceLog record);

    int insertSelective(UserWalletBalanceLog record);

    UserWalletBalanceLog selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(UserWalletBalanceLog record);

    int updateByPrimaryKey(UserWalletBalanceLog record);
    
    BigDecimal getFrozenTotalAmount(Map<String, Object> param);
}