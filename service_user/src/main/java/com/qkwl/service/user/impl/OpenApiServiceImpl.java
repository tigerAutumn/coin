package com.qkwl.service.user.impl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.Resource;
import javax.validation.Valid;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.qkwl.common.Enum.OpenApiStatusEnum;
import com.qkwl.common.dto.ListApiKeyReq;
import com.qkwl.common.dto.ListApiKeyResp;
import com.qkwl.common.dto.ListOpenApiReq;
import com.qkwl.common.dto.ListOpenApiResp;
import com.qkwl.common.dto.SaveOpenApiReq;
import com.qkwl.common.dto.UpdateOpenApiReq;
import com.qkwl.common.dto.Enum.ApiKeyStatusEnum;
import com.qkwl.common.dto.Enum.ApiKeyTypeEnum;
import com.qkwl.common.dto.Enum.OpenApiTypeEnum;
import com.qkwl.common.entity.ApiKeyEntity;
import com.qkwl.common.entity.ApiKeyEntityCriteria;
import com.qkwl.common.entity.OpenApiEntity;
import com.qkwl.common.entity.OpenApiEntityCriteria;
import com.qkwl.common.entity.OpenApiEntityCriteria.Criteria;
import com.qkwl.common.rpc.user.IOpenApiService;
import com.qkwl.common.util.PageData;
import com.qkwl.service.user.dao.OpenApiMapper;

@Service("openApiService")
public class OpenApiServiceImpl implements IOpenApiService{

	private final Logger logger=LoggerFactory.getLogger(this.getClass());

	@Resource
	private OpenApiMapper openApiMapper;


	@Override
	@Cacheable(value="open_api_cache")
	public OpenApiEntity findByUrl(String url) {
		OpenApiEntity openApiEntity=null;
		OpenApiEntityCriteria example=new OpenApiEntityCriteria();
		example.or().andUrlEqualTo(url).andStatusEqualTo(OpenApiStatusEnum.NORMAL.getCode());
		List<OpenApiEntity> list = openApiMapper.selectByExample(example);
		if(!CollectionUtils.isEmpty(list)) {
			openApiEntity=list.get(0);
		}
		return openApiEntity;
	}
	
	
	@Override
	@Cacheable(value="open_api_cache")
	public OpenApiEntity findByUrlPattern(String urlPattern) {
		OpenApiEntity openApiEntity=null;
		List<OpenApiEntity> findByUrlPattern = openApiMapper.findByUrlPattern(urlPattern);
		if(!CollectionUtils.isEmpty(findByUrlPattern)) {
			openApiEntity=findByUrlPattern.get(0);
		}
		return openApiEntity;
	}
	


	@Override
	public PageData<ListOpenApiResp> listOpenApi(@Valid ListOpenApiReq req) {
		OpenApiEntityCriteria example=new OpenApiEntityCriteria();
		Criteria createCriteria = example.createCriteria();
		if(StringUtils.isNotEmpty(req.getUrl()))
		{
			createCriteria.andUrlLike("%".concat(req.getUrl().concat("%")));
		}
		PageHelper.startPage(req.getPageNum(), req.getPageSize());
		List<OpenApiEntity> list = openApiMapper.selectByExample(example);
		PageInfo<OpenApiEntity> pageInfo = PageInfo.of(list);
		PageData<ListOpenApiResp> pageData = new PageData<>(pageInfo.getPageNum(), pageInfo.getPageSize(), pageInfo.getTotal());
		pageData.setList(list.stream().map(e -> entity2ListOpenApiResp(e)).collect(Collectors.toList()));
		return pageData;
	}


	/**
	 * @param e
	 * @return
	 */
	private ListOpenApiResp entity2ListOpenApiResp(OpenApiEntity e) {
		if (e == null) {
			return null;
		}
		ListOpenApiResp resp = new ListOpenApiResp();
		resp.setCreateTime(e.getCreateTime());
		resp.setDescription(e.getDescription());
		resp.setId(e.getId());
		resp.setIfSignVerify(e.getIfSignVerify());
		resp.setStatus(e.getStatus());
		resp.setStatusStr(OpenApiStatusEnum.getByCode(e.getStatus()).getDesc());
		resp.setTypes(e.getTypes());
		resp.setTypesStr(Stream.of(e.getTypes().split(",")).map(o->OpenApiTypeEnum.getByCode(Integer.valueOf(o)).getDesc()).collect(Collectors.joining(",")));
		resp.setUpdateTime(e.getUpdateTime());
		resp.setUrl(e.getUrl());
		return resp;
	}


	@Override
	@CacheEvict(value="open_api_cache",allEntries=true)  //清除所有缓存
	public void delete(Integer id) {
		OpenApiEntityCriteria example=new OpenApiEntityCriteria();
		example.createCriteria().andIdEqualTo(id);
		openApiMapper.deleteByExample(example);
	}


	@Override
	@Cacheable(value="open_api_cache")
	public OpenApiEntity findById(Integer id) {
		OpenApiEntity openApi=null;
		OpenApiEntityCriteria example=new OpenApiEntityCriteria();
		example.or().andIdEqualTo(id);
		List<OpenApiEntity> list = openApiMapper.selectByExample(example);
		if(!CollectionUtils.isEmpty(list)) {
			openApi=list.get(0);
		}
		return openApi;
	}


	@Override
	@CacheEvict(value="open_api_cache",allEntries=true)  //清除所有缓存
	public void save(SaveOpenApiReq req) {
		OpenApiEntity record=new OpenApiEntity();
		record.setDescription(req.getDescription());
		record.setIfSignVerify(BooleanUtils.toBoolean(Integer.valueOf(req.getIfSignVerify())));
		record.setStatus(Integer.valueOf(req.getStatus()));
		record.setTypes(req.getTypes());
		record.setUrl(req.getUrl());
		openApiMapper.insertSelective(record);
	}


	@Override
	@CacheEvict(value="open_api_cache",allEntries=true)  //清除所有缓存
	public void update(UpdateOpenApiReq req) {
		OpenApiEntity record=new OpenApiEntity();
		record.setDescription(req.getDescription());
		record.setIfSignVerify(BooleanUtils.toBoolean(Integer.valueOf(req.getIfSignVerify())));
		record.setStatus(Integer.valueOf(req.getStatus()));
		record.setTypes(req.getTypes());
		record.setUrl(req.getUrl());
		OpenApiEntityCriteria example=new OpenApiEntityCriteria();
		example.createCriteria().andIdEqualTo(Integer.valueOf(req.getId()));
		openApiMapper.updateByExampleSelective(record, example);
	}


}
