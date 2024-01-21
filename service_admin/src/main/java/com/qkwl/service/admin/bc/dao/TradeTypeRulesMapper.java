package com.qkwl.service.admin.bc.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.qkwl.common.dto.coin.SystemTradeTypeRule;

@Mapper
public interface TradeTypeRulesMapper {

	int deleteByPrimaryKey(Integer id);

    int insert(SystemTradeTypeRule rule);

    int insertSelective(SystemTradeTypeRule record);

    SystemTradeTypeRule selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(SystemTradeTypeRule record);

    int updateByPrimaryKey(SystemTradeTypeRule record);
    
    List<SystemTradeTypeRule> selectAll();
    
    /**
     * 交易对规则分页查询的总记录数
     */
    int countTradeTypeRulesListByParam(Map<String, Object> map);
    
    /**
     * 交易对规则分页查询
     */
	List<SystemTradeTypeRule> getTradeTypeRulesPageList(Map<String, Object> map);
}
