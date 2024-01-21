package com.qkwl.service.admin.bc.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.qkwl.common.dto.dict.DictItemAttr;

@Mapper
public interface DictItemAttrMapper {

	int deleteByPrimaryKey(Integer Id);

    int insert(DictItemAttr rule);

    int insertSelective(DictItemAttr record);

    DictItemAttr selectByPrimaryKey(Integer Id);

    int updateByPrimaryKeySelective(DictItemAttr record);

    int updateByPrimaryKey(DictItemAttr record);
    
    List<DictItemAttr> selectAll();
    
    /**
     * 交易对规则分页查询的总记录数
     */
    int countDictItemAttrListByParam(Map<String, Object> map);
    
    /**
     * 交易对规则分页查询
     */
	List<DictItemAttr> getDictItemAttrPageList(Map<String, Object> map);
}
