/**
 * 
 */
package com.hotcoin.notice.service.impl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.annotation.Resource;
import javax.validation.Valid;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.hotcoin.notice.Enum.TemplateTypeEnum;
import com.hotcoin.notice.dao.MessageTemplateMapper;
import com.hotcoin.notice.dto.AddMessageTemplateReq;
import com.hotcoin.notice.dto.DelMessageTemplateReq;
import com.hotcoin.notice.dto.ListMessageTemplateReq;
import com.hotcoin.notice.dto.MessageTemplateResp;
import com.hotcoin.notice.dto.UpdateMessageTemplateReq;
import com.hotcoin.notice.entity.MessageTemplateEntity;
import com.hotcoin.notice.entity.MessageTemplateEntityCriteria;
import com.hotcoin.notice.service.MessageTemplateService;
import com.hotcoin.notice.util.BeanLocator;
import com.hotcoin.notice.util.PageData;

/**
 * @author huangjinfeng
 */
@Service
public class MessageTemplateServiceImpl implements MessageTemplateService{
	
	
	@Resource
	private MessageTemplateMapper messageTemplateMapper;

	
	@Override
	@Cacheable(value="message_template_cache",key="#templateType.code+':'+#platform+':'+#businessType+':'+#lang")
	public MessageTemplateEntity findTemplate(TemplateTypeEnum templateType,String platform,String businessType,String lang) {
		MessageTemplateEntityCriteria example=new MessageTemplateEntityCriteria();
		example.createCriteria().andPlatformEqualTo(platform).andLangEqualTo(lang).andBusinessTypeEqualTo(businessType).andTypeEqualTo(templateType.getCode());
		List<MessageTemplateEntity> list = messageTemplateMapper.selectByExampleWithBLOBs(example);
		return Optional.ofNullable(list).filter(e->!CollectionUtils.isEmpty(e)).map(e->e.get(0)).orElse(null);
	}
	
	
	//不加缓存
	@Override
	public MessageTemplateEntity findSmsTemplate(String platform,String businessType,String lang) {
		//解决缓存失效问题
		return BeanLocator.getBean(this.getClass()).findTemplate(TemplateTypeEnum.SMS, platform, businessType, lang);
	}
	
	
	//不加缓存
	@Override
	public MessageTemplateEntity findEmailTemplate(String platform,String businessType,String lang) {
		//解决缓存失效问题
		return BeanLocator.getBean(this.getClass()).findTemplate(TemplateTypeEnum.EMAIL, platform, businessType, lang);
	}


	@Override
	@CacheEvict(value="message_template_cache",allEntries=true)  //清除所有缓存
	public void addMessageTemplate(@Valid AddMessageTemplateReq req) {
		MessageTemplateEntity record=new MessageTemplateEntity();
		record.setBusinessType(req.getBusinessType());
		record.setLang(req.getLang());
		record.setPlatform(req.getPlatform());
		record.setTemplate(req.getTemplate());
		record.setType(req.getType()==null?null:Integer.valueOf(req.getType()));
		record.setDescription(req.getDescription());
		messageTemplateMapper.insertSelective(record);
	}


	@Override
	@CacheEvict(value="message_template_cache",allEntries=true)  //清除所有缓存
	public void updateMessageTemplateReq(@Valid UpdateMessageTemplateReq req) {
		MessageTemplateEntity record=new MessageTemplateEntity();
		record.setBusinessType(req.getBusinessType());
		record.setLang(req.getLang());
		record.setPlatform(req.getPlatform());
		record.setTemplate(req.getTemplate());
		record.setType(req.getType()==null?null:Integer.valueOf(req.getType()));
		record.setDescription(req.getDescription());
		MessageTemplateEntityCriteria example=new MessageTemplateEntityCriteria();
		example.createCriteria().andIdEqualTo(req.getId());
		messageTemplateMapper.updateByExampleSelective(record, example);
	}


	@Override
	@CacheEvict(value="message_template_cache",allEntries=true)  //清除所有缓存
	public void delMessageTemplateReq(@Valid DelMessageTemplateReq req) {
		MessageTemplateEntity record=new MessageTemplateEntity();
		record.setDelete(true);
		MessageTemplateEntityCriteria example=new MessageTemplateEntityCriteria();
		example.createCriteria().andIdEqualTo(req.getId());
		messageTemplateMapper.updateByExampleSelective(record, example);
	}


	@Override
	@Cacheable(value="message_template_cache",key="#req.pageNum+':'+#req.pageSize")
	public PageData<MessageTemplateResp> listMessageTemplate(@Valid ListMessageTemplateReq req) {
		PageHelper.startPage(req.getPageNum(),req.getPageSize());
		MessageTemplateEntityCriteria example=new MessageTemplateEntityCriteria();
		example.createCriteria().andDeleteEqualTo(false);
		List<MessageTemplateEntity> list =messageTemplateMapper.selectByExampleWithBLOBs(example);
		PageInfo<MessageTemplateEntity> page=new PageInfo<>(list);
		PageData<MessageTemplateResp> pageData=new PageData<>(page.getPageNum(), page.getPageSize(), page.getTotal());
		pageData.setList(list.stream().map(e->entity2MessageTemplateResp(e)).collect(Collectors.toList()));
		return pageData;
	}

	
	private MessageTemplateResp entity2MessageTemplateResp(MessageTemplateEntity entity){
		if(entity==null) {
			return null;
		}
		MessageTemplateResp resp=new MessageTemplateResp();
		resp.setBusinessType(entity.getBusinessType());
		resp.setCreateTime(entity.getCreateTime());
		resp.setId(entity.getId());
		resp.setLang(entity.getLang());
		resp.setPlatform(entity.getPlatform());
		resp.setTemplate(entity.getTemplate());
		resp.setType(entity.getType());
		resp.setUpdateTime(entity.getUpdateTime());
		resp.setDescription(entity.getDescription());
		return resp;
	}
	
	
}
