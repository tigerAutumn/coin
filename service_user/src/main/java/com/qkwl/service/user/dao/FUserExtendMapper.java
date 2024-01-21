package com.qkwl.service.user.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.qkwl.common.dto.user.FUserExtend;

@Mapper
public interface FUserExtendMapper {

	/**
     * 根据uid查询用户扩展信息
     * @param uid
     * @return
     */
    FUserExtend selectByUid(Integer uid);
    
    /**
	 * 新增用户扩展信息
	 * @param record 实体对象
	 * @return 成功条数
	 */
    int insert(FUserExtend record);
    
    /**
     * 更新用户语言
     * @param record 实体对象
     * @return 成功条数
     */
    int updateLanguage(FUserExtend record);
    
    /**
     * 更新用户二次验证
     * @param record
     * @return
     */
    int updateSecondaryVerification(FUserExtend record);

	/**
	 * @param id
	 * @param antiPhishingCode
	 */
	void updateAntiPhishingCodeById(@Param(value = "id") Integer id, @Param(value = "antiPhishingCode") String antiPhishingCode);
}
