/**
 * 
 */
package com.hotcoin.notice.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Resource;
import javax.validation.Valid;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.hotcoin.notice.Enum.SendTypeEnum;
import com.hotcoin.notice.dao.SmsConfigMapper;
import com.hotcoin.notice.dto.AddSmsConfigReq;
import com.hotcoin.notice.dto.DelSmsConfigReq;
import com.hotcoin.notice.dto.ListSmsConfigReq;
import com.hotcoin.notice.dto.SmsConfigResp;
import com.hotcoin.notice.dto.UpdateSmsConfigReq;
import com.hotcoin.notice.entity.SmsConfigEntity;
import com.hotcoin.notice.entity.SmsConfigEntityCriteria;
import com.hotcoin.notice.service.SmsConfigService;
import com.hotcoin.notice.util.PageData;

/**
 * @author huangjinfeng
 */
@Service(value = "smsConfigService")
public class SmsConfigServiceImpl implements SmsConfigService {
	
	
	@Resource
	private SmsConfigMapper smsConfigMapper;

	@Override
	@Cacheable(value = "sms_config_cache",key = "#sendType.code")
	public List<SmsConfigEntity> findByAction(SendTypeEnum sendType) {
		SmsConfigEntityCriteria example=new SmsConfigEntityCriteria();
		example.createCriteria().andActionEqualTo(sendType.getCode()).andEnableEqualTo(true);
		return smsConfigMapper.selectByExample(example);
	}

	@Override
	@CacheEvict(value = "sms_config_cache",allEntries = true)
	public void addSmsConfig(@Valid AddSmsConfigReq req) {
		SmsConfigEntity record=new SmsConfigEntity();
		record.setAction(req.getAction());
		record.setDescription(req.getDescription());
		record.setEnable(req.getEnable()==null?Boolean.FALSE:req.getEnable());
		record.setWeight(req.getWeight());
		record.setName(req.getName());
		smsConfigMapper.insertSelective(record);
	}

	@Override
	@CacheEvict(value = "sms_config_cache",allEntries = true)
	public void updateSmsConfig(@Valid UpdateSmsConfigReq req) {
		SmsConfigEntity record=new SmsConfigEntity();
		record.setAction(req.getAction());
		record.setDescription(req.getDescription());
		record.setEnable(req.getEnable()==null?Boolean.FALSE:req.getEnable());
		record.setWeight(req.getWeight());
		record.setName(req.getName());
		SmsConfigEntityCriteria example=new SmsConfigEntityCriteria();
		example.createCriteria().andIdEqualTo(req.getId());
		smsConfigMapper.updateByExampleSelective(record, example);
	}

	@Override
	@CacheEvict(value = "sms_config_cache",allEntries = true)
	public void delSmsConfig(@Valid DelSmsConfigReq req) {
		SmsConfigEntityCriteria example=new SmsConfigEntityCriteria();
		example.createCriteria().andIdEqualTo(req.getId());
		smsConfigMapper.deleteByExample(example);
	}

	@Override
	@Cacheable(value = "sms_config_cache",key="#req.pageNum+':'+#req.pageSize")
	public PageData<SmsConfigResp> listSmsConfig(@Valid ListSmsConfigReq req) {
		PageHelper.startPage(req.getPageNum(),req.getPageSize());
		List<SmsConfigEntity> list = smsConfigMapper.selectByExample(null);
		PageInfo<SmsConfigEntity> pageInfo=PageInfo.of(list);
		PageData<SmsConfigResp> pageData=new PageData<>(pageInfo.getPageNum(), pageInfo.getPages(), pageInfo.getTotal());
		pageData.setList(list.stream().map(e->entity2SmsConfigResp(e)).collect(Collectors.toList()));
		return pageData;
	}

	
	private SmsConfigResp entity2SmsConfigResp(SmsConfigEntity entity){
		if(entity==null) {
			return null;
		}
		
		SmsConfigResp resp=new SmsConfigResp();
		resp.setAction(entity.getAction());
		resp.setDescription(entity.getDescription());
		resp.setEnable(entity.getEnable());
		resp.setId(entity.getId());
		resp.setWeight(entity.getWeight());
		resp.setName(entity.getName());
		return resp;
	}
	

}
