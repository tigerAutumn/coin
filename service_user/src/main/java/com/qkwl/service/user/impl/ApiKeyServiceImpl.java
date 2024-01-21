package com.qkwl.service.user.impl;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.qkwl.common.Enum.validate.BusinessTypeEnum;
import com.qkwl.common.Enum.validate.LocaleEnum;
import com.qkwl.common.Enum.validate.PlatformEnum;
import com.qkwl.common.crypto.MD5Util;
import com.qkwl.common.dto.AdminUpdateApiKeyReq;
import com.qkwl.common.dto.CreateApiKeyResp;
import com.qkwl.common.dto.ListApiKeyReq;
import com.qkwl.common.dto.ListApiKeyResp;
import com.qkwl.common.dto.MyApiKeyResp;
import com.qkwl.common.dto.UpdateApiKeyReq;
import com.qkwl.common.dto.Enum.ApiKeyStatusEnum;
import com.qkwl.common.dto.Enum.ApiKeyTypeEnum;
import com.qkwl.common.dto.Enum.ErrorCodeEnum;
import com.qkwl.common.dto.user.FUser;
import com.qkwl.common.dto.validate.ValidateParamInfo;
import com.qkwl.common.entity.ApiKeyEntity;
import com.qkwl.common.entity.ApiKeyEntityCriteria;
import com.qkwl.common.entity.ApiKeyEntityCriteria.Criteria;
import com.qkwl.common.exceptions.BizException;
import com.qkwl.common.framework.redis.RedisHelper;
import com.qkwl.common.redis.MemCache;
import com.qkwl.common.redis.RedisConstant;
import com.qkwl.common.result.Result;
import com.qkwl.common.rpc.user.IApiKeyService;
import com.qkwl.common.rpc.user.IUserService;
import com.qkwl.common.rpc.v2.UserSecurityService;
import com.qkwl.common.util.Constant;
import com.qkwl.common.util.GUIDUtils;
import com.qkwl.common.util.PageData;
import com.qkwl.service.user.dao.ApiKeyMapper;

@Service("apiKeyService")
public class ApiKeyServiceImpl implements IApiKeyService{
	
	private final Logger logger=LoggerFactory.getLogger(this.getClass());
	
	@Resource
	private ApiKeyMapper apiKeyMapper;
	@Autowired
	private UserSecurityService userSecurityService;
	@Autowired
	private IUserService userService;
	@Autowired
	private MemCache memCache;
	@Autowired
	private RedisHelper redisHelper;

	@Override
	@Cacheable(value="api_key_cache")
	public ApiKeyEntity selectByAccessKey(String accessKey) {
		logger.info("缓存测试：走数据库，access key:{}",accessKey);
		ApiKeyEntity apiKeyEntity=null;
		ApiKeyEntityCriteria example=new ApiKeyEntityCriteria();
		example.or().andAccessKeyEqualTo(accessKey);
		List<ApiKeyEntity> list = apiKeyMapper.selectByExample(example);
		if(!CollectionUtils.isEmpty(list)) {
			apiKeyEntity=list.get(0);
		}
		return apiKeyEntity;
	}
	
	@Override
	public CreateApiKeyResp createApiKey(Integer userId, String ip, String types, String remark) {
		String accessKey=GUIDUtils.getGUIDString().toLowerCase();
		String secretKey= MD5Util.md5("hotcoin.top" + accessKey.toUpperCase()+System.currentTimeMillis()).toUpperCase();
		memCache.tryFairLock(Constant.DISTRIBUTED_LOCK_DB, "createApiKey:"+userId, 3000, ()->{
		//每个用户最多创建5个
		ApiKeyEntityCriteria example=new ApiKeyEntityCriteria();
		example.createCriteria().andUidEqualTo(userId);
		long count = apiKeyMapper.countByExample(example);
		if(count>=5) {
			throw new BizException(ErrorCodeEnum.CREATE_API_KEY_MAX_5);
		}
		ApiKeyEntity record=new ApiKeyEntity();
		record.setAccessKey(accessKey);
		record.setIp(ip);
		record.setRemark(remark);
		record.setSecretKey(secretKey);
		record.setStatus(ApiKeyStatusEnum.NORMAL.getCode());
		record.setTypes(types);
		record.setUid(userId);
		record.setRate(Constant.API_KEY_DEFAULT_RATE);
		apiKeyMapper.insertSelective(record);
		return null;
		});
		
		
		CreateApiKeyResp resp=new CreateApiKeyResp();
		resp.setAccessKey(accessKey);
		resp.setIp(ip);
		resp.setSecretKey(secretKey);
		resp.setRate(Constant.API_KEY_DEFAULT_RATE.toString());
		resp.setTypesStr(Stream.of(types.split(",")).map(e->ApiKeyTypeEnum.getByCode(Integer.valueOf(e)).getDesc()).collect(Collectors.joining(",")));
		return resp;
	}

	@Override
	public List<MyApiKeyResp> myApiKey(Integer userId) {
		ApiKeyEntityCriteria example=new ApiKeyEntityCriteria();
		example.setOrderByClause("create_time desc");
		example.or().andUidEqualTo(userId);
		List<ApiKeyEntity> list = apiKeyMapper.selectByExample(example);
		return list.stream().map(e->entity2MyApiKeyResp(e)).collect(Collectors.toList());
	}
	
	private MyApiKeyResp entity2MyApiKeyResp(ApiKeyEntity entity) {
		if(entity==null) {
			return null;
		}
		MyApiKeyResp resp=new MyApiKeyResp();
		resp.setId(entity.getId());
		resp.setTypes(entity.getTypes());
		resp.setAccessKey(entity.getAccessKey());
		resp.setCreateTime(entity.getCreateTime());
		resp.setIp(entity.getIp());
		resp.setRemark(entity.getRemark());
		resp.setStatusStr(ApiKeyStatusEnum.getEnumByCode(entity.getStatus()).getDesc());
		resp.setTypesStr(Stream.of(entity.getTypes().split(",")).map(e->ApiKeyTypeEnum.getByCode(Integer.valueOf(e)).getDesc()).collect(Collectors.joining(",")));
		resp.setRemainPeriod(getRemainPeriod(entity));
		resp.setRate(entity.getRate().toString());
		return resp;
	}

	private Integer getRemainPeriod(ApiKeyEntity entity) {
		Integer remainPerid=null;
		ApiKeyStatusEnum e = ApiKeyStatusEnum.getEnumByCode(entity.getStatus());
		switch (e) {
		case OVERDUE:
			remainPerid=0;
			break;
		case DISABLE:
		case NORMAL:
			if(StringUtils.isBlank(entity.getIp())) {
				remainPerid=Constant.REMAIN_PERIOD-(int)((System.currentTimeMillis()-entity.getCreateTime().getTime())/(1000*60*60*24));
			}
			break;
		default:
			break;
		}
		return remainPerid;
	}

	@Override
	@CacheEvict(value="api_key_cache",key ="#root.target.findById(#apiKeyId).getAccessKey()",beforeInvocation = true)
	public void deleteApiKey(Integer userId, Integer apiKeyId) {
		ApiKeyEntityCriteria example=new ApiKeyEntityCriteria();
		example.or().andIdEqualTo(apiKeyId).andUidEqualTo(userId);
		int row = apiKeyMapper.deleteByExample(example);
		if(row<=0) {
			throw new BizException(ErrorCodeEnum.DELETE_API_KEY_FAILED);
		}
	}

	@Override
	@CacheEvict(value="api_key_cache",key ="#root.target.findById(#req.id).getAccessKey()")
	public void updateApiKey(UpdateApiKeyReq req,Integer userId, String ip) {
		
		FUser fuser = userService.selectUserById(userId);
		if(fuser==null) {
			throw new BizException(ErrorCodeEnum.UNKNOWN_ACCOUNT);
		}
		
		//验证验证码
		ValidateParamInfo paramInfo = new ValidateParamInfo();
		paramInfo.setCode(req.getMobileValidCode());
		paramInfo.setTotpCode(req.getGoogleValidCode());
		paramInfo.setIp(ip);
		paramInfo.setPlatform(PlatformEnum.BC);
		paramInfo.setBusinessType(BusinessTypeEnum.UPDATE_API_KEY);
		paramInfo.setLocale(LocaleEnum.getLanEnum());
		paramInfo.setEmailCode(req.getMailValidCode());
		paramInfo.setEmailBusinessType(BusinessTypeEnum.UPDATE_API_KEY);
		Result validResult = userSecurityService.validateUserSercuritySetting(fuser, paramInfo);
		
		if(!validResult.getSuccess()) {
			throw new BizException(ErrorCodeEnum.DEFAULT);
		} 
		
		ApiKeyEntity record=new ApiKeyEntity();
		record.setIp(StringUtils.defaultString(req.getIp()));
		record.setTypes(req.getTypes());
		record.setRemark(req.getRemark());
		ApiKeyEntityCriteria example=new ApiKeyEntityCriteria();
		example.or().andUidEqualTo(userId).andIdEqualTo(req.getId());
		int row = apiKeyMapper.updateByExampleSelective(record, example);
		if(row<=0) {
			throw new BizException(ErrorCodeEnum.UPDATE_API_KEY_FAILED);
		}
	}

	@Override
	@CacheEvict(value="api_key_cache",allEntries = true )
	public void updateExpireKeyStatus() {
		logger.info("开始检查过期api key");
		apiKeyMapper.updateExpireKeyStatus();
	}

	@Override
	public PageData<ListApiKeyResp> listApiKey(ListApiKeyReq req) {
		ApiKeyEntityCriteria example=new ApiKeyEntityCriteria();
		Criteria createCriteria = example.createCriteria();
		if(StringUtils.isNotEmpty(req.getUid()))
		{
			createCriteria.andUidEqualTo(Integer.valueOf(req.getUid()));
		}
		if(!"0".equals(req.getStatus())) {
			createCriteria.andStatusEqualTo(Integer.valueOf(req.getStatus()));
		}
		PageHelper.startPage(req.getPageNum(), req.getPageSize());
		List<ApiKeyEntity> list = apiKeyMapper.selectByExample(example);
		PageInfo<ApiKeyEntity> pageInfo = PageInfo.of(list);
		PageData<ListApiKeyResp> pageData = new PageData<>(pageInfo.getPageNum(), pageInfo.getPageSize(), pageInfo.getTotal());
		pageData.setList(list.stream().map(e -> entity2ListApiKeyResp(e)).collect(Collectors.toList()));
		return pageData;
	}
	
	private ListApiKeyResp entity2ListApiKeyResp(ApiKeyEntity entity) {
		if (entity == null) {
			return null;
		}
		ListApiKeyResp resp = new ListApiKeyResp();
		resp.setAccessKey(entity.getAccessKey());
		resp.setCreateTime(entity.getCreateTime());
		resp.setId(entity.getId());
		resp.setIp(entity.getIp());
		resp.setRemainPeriod(getRemainPeriod(entity));
		resp.setRemark(entity.getRemark());
		resp.setStatus(entity.getStatus());
		resp.setUid(entity.getUid());
		resp.setRate(entity.getRate().toPlainString());
		resp.setStatusStr(ApiKeyStatusEnum.getEnumByCode(entity.getStatus()).getDesc());
		resp.setTypesStr(Stream.of(entity.getTypes().split(",")).map(e->ApiKeyTypeEnum.getByCode(Integer.valueOf(e)).getDesc()).collect(Collectors.joining(",")));
		return resp;
	}

	@Override
	@CacheEvict(value="api_key_cache",key ="#root.target.findById(#apiKeyId).getAccessKey()")
	public void updateStatus(Integer apiKeyId, ApiKeyStatusEnum e) {
		ApiKeyEntity record=new ApiKeyEntity();
		record.setStatus(e.getCode());
		ApiKeyEntityCriteria example=new ApiKeyEntityCriteria();
		example.createCriteria().andIdEqualTo(apiKeyId).andStatusNotEqualTo(ApiKeyStatusEnum.OVERDUE.getCode());
		int rows = apiKeyMapper.updateByExampleSelective(record, example);
		if(rows<=0) {
			throw new BizException(ErrorCodeEnum.UPDATE_API_KEY_STATUS_FAILED);
		}
	}
	
	public static void main(String[] args) {
		String accessKey=GUIDUtils.getGUIDString().toLowerCase();
		String secretKey= MD5Util.md5("hotcoin.top" + accessKey.toUpperCase()+System.currentTimeMillis()).toUpperCase();
		System.out.println("accessKey:"+accessKey);
		System.out.println("secretkey:"+secretKey);
	}

	@Override
	public ApiKeyEntity findById(Integer id) {
		ApiKeyEntity apiKeyEntity=null;
		ApiKeyEntityCriteria example=new ApiKeyEntityCriteria();
		example.createCriteria().andIdEqualTo(id);
		List<ApiKeyEntity> selectByExample = apiKeyMapper.selectByExample(example);
		if(!CollectionUtils.isEmpty(selectByExample)) {
			apiKeyEntity=selectByExample.get(0);
		}
		return apiKeyEntity;
	}
	
	@Override
	public ApiKeyEntity findByUid(Integer uid) {
		ApiKeyEntity apiKeyEntity=null;
		ApiKeyEntityCriteria example=new ApiKeyEntityCriteria();
		example.createCriteria().andUidEqualTo(uid).andStatusEqualTo(ApiKeyStatusEnum.NORMAL.getCode());
		List<ApiKeyEntity> selectByExample = apiKeyMapper.selectByExample(example);
		if(!CollectionUtils.isEmpty(selectByExample)) {
			apiKeyEntity=selectByExample.get(0);
		}
		return apiKeyEntity;
	}
	

	@Override
	@CacheEvict(value="api_key_cache",key ="#root.target.findById(#req.id).getAccessKey()")
	public void update(AdminUpdateApiKeyReq req) {
		ApiKeyEntity record=new ApiKeyEntity();
		record.setRate(new BigDecimal(req.getRate()));
		ApiKeyEntityCriteria example=new ApiKeyEntityCriteria();
		example.createCriteria().andIdEqualTo(req.getId());
		apiKeyMapper.updateByExampleSelective(record, example);
	}
   
}