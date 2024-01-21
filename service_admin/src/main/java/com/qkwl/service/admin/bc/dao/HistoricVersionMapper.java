package com.qkwl.service.admin.bc.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.qkwl.common.dto.system.HistoricVersion;

@Mapper
public interface HistoricVersionMapper {

	int deleteByPrimaryKey(Integer id);

    int insert(HistoricVersion record);

    int insertSelective(HistoricVersion record);

    HistoricVersion selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(HistoricVersion record);

    int updateByPrimaryKey(HistoricVersion record);
    
    List<HistoricVersion> selectLimitTen();
    
    /**
     * otc法币分页查询的总记录数
     * @param map 参数map
     * @return 查询记录数
     */
    int countHistoricVersionList();
    
    /**
     * otc法币分页查询
     * @param map 参数map
     * @return 用户列表
     */
	List<HistoricVersion> getHistoricVersionPageList(Map<String, Object> map);
}
