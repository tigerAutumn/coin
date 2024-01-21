package com.qkwl.service.admin.bc.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.qkwl.common.dto.dict.DictItem;

@Mapper
public interface DictItemMapper {

	int deleteByPrimaryKey(Integer Id);

    int insert(DictItem rule);

    int insertSelective(DictItem record);

    DictItem selectByPrimaryKey(Integer Id);

    int updateByPrimaryKeySelective(DictItem record);

    int updateByPrimaryKey(DictItem record);
    
    List<DictItem> selectAll();
    
    /**
     * 交易对规则分页查询的总记录数
     */
    int countDictItemListByParam(Map<String, Object> map);
    
    /**
     * 交易对规则分页查询
     */
	List<DictItem> getDictItemPageList(Map<String, Object> map);
}
