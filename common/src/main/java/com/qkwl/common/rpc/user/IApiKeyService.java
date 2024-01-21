package com.qkwl.common.rpc.user;

import java.util.List;

import javax.validation.Valid;

import com.qkwl.common.dto.AdminUpdateApiKeyReq;
import com.qkwl.common.dto.CreateApiKeyResp;
import com.qkwl.common.dto.ListApiKeyReq;
import com.qkwl.common.dto.ListApiKeyResp;
import com.qkwl.common.dto.MyApiKeyResp;
import com.qkwl.common.dto.UpdateApiKeyReq;
import com.qkwl.common.dto.Enum.ApiKeyStatusEnum;
import com.qkwl.common.entity.ApiKeyEntity;
import com.qkwl.common.util.PageData;

public interface IApiKeyService{

	/**
	 * @param accessKey 
	 * @return 
	 * 
	 */
	ApiKeyEntity selectByAccessKey(String accessKey);

	/**
	 * @param fid
	 * @param ip
	 * @param types
	 * @param remark
	 * @return
	 */
	CreateApiKeyResp createApiKey(Integer userId, String ip, String types, String remark);

	/**
	 * @param fid
	 * @return
	 */
	List<MyApiKeyResp> myApiKey(Integer userId);

	/**
	 * @param fid
	 * @param id
	 */
	void deleteApiKey(Integer userId, Integer apiKeyId);

	/**
	 * @param fid
	 * @param id
	 * @param ip
	 * @param types
	 * @param remark
	 */
	void updateApiKey(UpdateApiKeyReq req,Integer userId,String ip);

	/**
	 * 
	 */
	void updateExpireKeyStatus();

	/**
	 * @param req
	 * @return
	 */
	PageData<ListApiKeyResp> listApiKey(@Valid ListApiKeyReq req);

	/**
	 * @param id
	 * @param normal
	 */
	void updateStatus(Integer apiKeyId, ApiKeyStatusEnum normal);

	/**
	 * @param id
	 * @return
	 */
	ApiKeyEntity findById(Integer id);

	/**
	 * @param req
	 */
	void update(AdminUpdateApiKeyReq req);

	/**
	 * 根据用户id随机取一条
	 * @param uid
	 * @return
	 */
	ApiKeyEntity findByUid(Integer uid);
   
}