package com.qkwl.web.elasticsearch;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import com.qkwl.common.dto.coin.SystemTradeType;

/**
 * 接口关系：
 * ElasticsearchRepository --> ElasticsearchCrudRepository --> PagingAndSortingRepository --> CrudRepository
 */
public interface SystemTradeTypeRepository extends ElasticsearchRepository<SystemTradeType, Integer>{

}