package com.qkwl.service.admin.bc.dao;

import java.util.List;
import java.util.Map;
import org.apache.ibatis.annotations.Mapper;
import com.qkwl.common.dto.dict.DictGroup;

@Mapper
public interface DictGroupMapper {

	int deleteByPrimaryKey(Integer Id);

    int insert(DictGroup rule);

    int insertSelective(DictGroup record);

    DictGroup selectByPrimaryKey(Integer Id);

    int updateByPrimaryKeySelective(DictGroup record);

    int updateByPrimaryKey(DictGroup record);
    
    List<DictGroup> selectAll();
    
    /**
     * 交易对规则分页查询的总记录数
     */
    int countDictGroupListByParam(Map<String, Object> map);
    
    /**
     * 交易对规则分页查询
     */
	List<DictGroup> getDictGroupPageList(Map<String, Object> map);
}
