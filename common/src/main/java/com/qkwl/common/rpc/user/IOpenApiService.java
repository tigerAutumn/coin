package com.qkwl.common.rpc.user;

import java.util.List;

import javax.validation.Valid;

import com.qkwl.common.dto.ListOpenApiReq;
import com.qkwl.common.dto.ListOpenApiResp;
import com.qkwl.common.dto.SaveOpenApiReq;
import com.qkwl.common.dto.UpdateOpenApiReq;
import com.qkwl.common.entity.OpenApiEntity;
import com.qkwl.common.util.PageData;

public interface IOpenApiService{

	/**
	 * @param requestURI
	 * @return
	 */
	OpenApiEntity findByUrl(String requestURI);

	/**
	 * @param req
	 * @return
	 */
	PageData<ListOpenApiResp> listOpenApi(@Valid ListOpenApiReq req);

	/**
	 * @param id
	 */
	void delete(Integer id);

	/**
	 * @param id
	 * @return
	 */
	OpenApiEntity findById(Integer id);

	/**
	 * @param req
	 */
	void save(SaveOpenApiReq req);

	/**
	 * @param req
	 */
	void update(UpdateOpenApiReq req);

	/**
	 * @param urlPattern
	 * @return
	 */
	OpenApiEntity findByUrlPattern(String urlPattern);
   
}