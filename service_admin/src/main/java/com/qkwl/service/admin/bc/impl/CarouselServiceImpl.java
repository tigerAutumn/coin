package com.qkwl.service.admin.bc.impl;

import java.util.List;
import java.util.Set;

import javax.validation.Valid;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageInfo;
import com.qkwl.common.dto.carousel.ListCarouselReq;
import com.qkwl.common.dto.carousel.SaveCarouselReq;
import com.qkwl.common.dto.carousel.SystemCarousel;
import com.qkwl.common.dto.carousel.SystemCarouselCriteria;
import com.qkwl.common.dto.carousel.SystemCarouselCriteria.Criteria;
import com.qkwl.common.framework.redis.RedisHelper;
import com.qkwl.common.redis.RedisConstant;
import com.qkwl.common.dto.carousel.UpdateCarouselReq;
import com.qkwl.common.rpc.admin.ICarouselService;
import com.qkwl.common.util.I18NUtils;
import com.qkwl.service.admin.bc.dao.SystemCarouselMapper;

/**
 * 
 * @author huangjinfeng
 */
@Service("carouselService")
public class CarouselServiceImpl implements ICarouselService {

	private static final Logger logger = LoggerFactory.getLogger(CarouselServiceImpl.class);

	@Autowired
	private SystemCarouselMapper carouselMapper;
	@Autowired
    private RedisHelper redisHelper;

	@Override
	public PageInfo<SystemCarousel> listSystemCarousel(ListCarouselReq dto) {

		SystemCarouselCriteria example = new SystemCarouselCriteria();
		example.setOrderByClause(String.format("%s %s", dto.getOrderField(), dto.getOrderDirection()));
		Criteria createCriteria = example.createCriteria();
		if (StringUtils.isNotBlank(dto.getName())) {
			createCriteria.andNameLike(String.format("%s%s%s", "%", dto.getName(), "%"));
		}
		if (StringUtils.isNotBlank(dto.getLang())) {
			createCriteria.andLangEqualTo(dto.getLang());
		}
		List<SystemCarousel> selectByExample = carouselMapper.selectByExample(example);
		return new PageInfo<SystemCarousel>(selectByExample);
	}

	@Override
	public SystemCarousel selectById(Integer id) {
		SystemCarouselCriteria example = new SystemCarouselCriteria();
		example.createCriteria().andIdEqualTo(id);
		List<SystemCarousel> list = carouselMapper.selectByExample(example);
		return CollectionUtils.isEmpty(list) ? null : list.get(0);
	}

	@Override
	public void saveCarousel(@Valid SaveCarouselReq req) {
		SystemCarousel record = new SystemCarousel();
		record.setDescription(req.getDescription());
		record.setLang(req.getLang());
		record.setName(req.getName());
		record.setTargetUrl(req.getTargetUrl());
		record.setPriority(req.getPriority().shortValue());
		record.setUrl(req.getUrl());
		carouselMapper.insertSelective(record);
		resetRedis();
	}

	@Override
	public void updateCarousel(@Valid UpdateCarouselReq req) {
		SystemCarousel record = new SystemCarousel();
		record.setDescription(req.getDescription());
		record.setLang(req.getLang());
		record.setName(req.getName());
		record.setPriority(req.getPriority().shortValue());
		record.setUrl(req.getUrl());
		record.setTargetUrl(req.getTargetUrl());
		SystemCarouselCriteria example = new SystemCarouselCriteria();
		example.createCriteria().andIdEqualTo(req.getId());
		carouselMapper.updateByExampleSelective(record, example);
		resetRedis();
	}

	@Override
	public void deleteCarousel(Integer id) {
		SystemCarouselCriteria example=new SystemCarouselCriteria();
		example.createCriteria().andIdEqualTo(id);
		carouselMapper.deleteByExample(example);
		resetRedis();
	}
	
	
	private void resetRedis(){
		List<SystemCarousel> selectByExample = carouselMapper.selectByExample(null);
		Set<String> keys  = redisHelper.keys(String.format("%s%s", RedisConstant.SYSTEM_CAROUSEL,"*"));
		if(CollectionUtils.isNotEmpty(keys)) {
			keys.forEach(e->{
				redisHelper.delete(e);
			});
		}
		for(SystemCarousel systemCarousel:selectByExample) {
			redisHelper.setNoExpire(String.format("%s%s%s%s", RedisConstant.SYSTEM_CAROUSEL,systemCarousel.getLang(),":",systemCarousel.getId()), JSON.toJSONString(systemCarousel));
		}
	}

}
