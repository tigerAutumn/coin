package com.qkwl.service.admin.elasticsearch;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import com.qkwl.common.dto.coin.SystemTradeType;

public interface SystemTradeTypeSearchMapper extends ElasticsearchRepository<SystemTradeType, Integer>{

}