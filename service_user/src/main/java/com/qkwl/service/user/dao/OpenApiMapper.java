package com.qkwl.service.user.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.qkwl.common.entity.OpenApiEntity;
import com.qkwl.service.user.dao.base.OpenApiBaseMapper;

@Mapper
public interface OpenApiMapper extends OpenApiBaseMapper {

	/**
	 * @param urlPattern
	 * @return
	 */
	List<OpenApiEntity> findByUrlPattern(String urlPattern);
   
}
