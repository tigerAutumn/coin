package com.qkwl.service.otc.dao;


import java.util.List;

import org.apache.ibatis.annotations.Param;

import org.apache.ibatis.annotations.Mapper;
import com.qkwl.common.dto.capital.FUserBankinfoDTO;

/**
 * 用户银行卡数据接口
 * @author YCJ
 */
@Mapper
public interface FUserBankinfoMapper {
	
    
    /**
     * 根据用户和类型查询银行卡
     * @param fuid 用户ID
     * @param ftype 银行卡类型
     * @return 实体对象列表
     */
    List<FUserBankinfoDTO> getBankInfoListByUser(@Param("fuid") int fuid, @Param("ftype") Integer ftype);
}