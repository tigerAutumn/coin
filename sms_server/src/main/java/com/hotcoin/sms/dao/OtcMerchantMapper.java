package com.hotcoin.sms.dao;

import com.hotcoin.sms.model.OtcMerchant;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OtcMerchantMapper {

    /**
     * ID查询用户
     */
    OtcMerchant selectByUid(Long fid);
}
