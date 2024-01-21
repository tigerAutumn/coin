package com.qkwl.service.admin.bc.dao;


import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.qkwl.common.dto.capital.FUserBankinfoDTO;

/**
 * 用户银行卡数据操作接口
 * @author ZKF
 */
@Mapper
public interface FUserBankinfoMapper {

    /**
     * 根据id查询用户银行卡
     * @param fid 主键ID
     * @return 实体对象
     */
    FUserBankinfoDTO selectByPrimaryKey(Integer fid);
    
    /**
     * 根据user和类型查询支付方式
     * @param userId 
     * @param type
     * @param status
     * @return 实体对象
     */
    FUserBankinfoDTO selectByUserAndType(@Param("userId")Integer userId,@Param("type")Integer type,@Param("status")Integer status,@Param("isDefault") Boolean isDefault);


    
}