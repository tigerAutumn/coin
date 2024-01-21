package com.qkwl.service.admin.bc.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.qkwl.common.dto.otc.OtcCurrency;

@Mapper
public interface OtcCurrencyMapper {

	int deleteByPrimaryKey(Integer id);

    int insert(OtcCurrency record);

    int insertSelective(OtcCurrency record);

    OtcCurrency selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(OtcCurrency record);

    int updateByPrimaryKey(OtcCurrency record);
    
    List<OtcCurrency> selectAll();
    
    /**
     * otc法币分页查询的总记录数
     * @param map 参数map
     * @return 查询记录数
     */
    int countOtcCurrencyListByParam(Map<String, Object> map);
    
    /**
     * otc法币分页查询
     * @param map 参数map
     * @return 用户列表
     */
	List<OtcCurrency> getOtcCurrencyPageList(Map<String, Object> map);
	
}
