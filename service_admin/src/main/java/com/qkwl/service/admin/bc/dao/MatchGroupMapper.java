package com.qkwl.service.admin.bc.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.qkwl.common.dto.matchGroup.MatchGroup;

@Mapper
public interface MatchGroupMapper {

	int deleteByPrimaryKey(Integer id);

    int insert(MatchGroup matchGroup);

    int insertSelective(MatchGroup matchGroup);

    MatchGroup selectByPrimaryKey(Integer id);
    
    MatchGroup selectByGroupName(String groupName);

    int updateByPrimaryKeySelective(MatchGroup matchGroup);

    int updateByPrimaryKey(MatchGroup matchGroup);
    
    List<MatchGroup> selectAll();
    
    List<MatchGroup> selectOthers(Integer id);
    
    /**
     * 交易对组分页查询的总记录数
     * @param map 参数map
     * @return 查询记录数
     */
    int countMatchGroupListByParam(Map<String, Object> map);
    
    /**
     * 交易对组分页查询
     * @param map 参数map
     * @return 用户列表
     */
	List<MatchGroup> getMatchGroupPageList(Map<String, Object> map);
}
