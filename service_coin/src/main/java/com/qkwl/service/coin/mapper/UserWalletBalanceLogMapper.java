package com.qkwl.service.coin.mapper;

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
}