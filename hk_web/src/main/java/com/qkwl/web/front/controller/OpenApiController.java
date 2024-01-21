package com.qkwl.web.front.controller;

import java.util.List;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.qkwl.common.dto.CreateApiKeyReq;
import com.qkwl.common.dto.CreateApiKeyResp;
import com.qkwl.common.dto.MyApiKeyResp;
import com.qkwl.common.dto.UpdateApiKeyReq;
import com.qkwl.common.dto.Enum.ErrorCodeEnum;
import com.qkwl.common.dto.user.FUser;
import com.qkwl.common.exceptions.BizException;
import com.qkwl.common.rpc.user.IApiKeyService;
import com.qkwl.common.util.HttpRequestUtils;
import com.qkwl.common.util.RespData;
import com.qkwl.web.front.controller.base.JsonBaseController;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Controller
@RequestMapping("/openapi")
@Api(tags="API管理")
public class OpenApiController extends JsonBaseController{
	
	private static final Logger logger = LoggerFactory.getLogger(OpenApiController.class);

    @Autowired
    private IApiKeyService apiKeyService;
    

    @ApiOperation(value = "创建新的API Key")
	@PostMapping("/apikey/create")
    @ResponseBody
	public RespData<CreateApiKeyResp> createApiKey(@Valid  CreateApiKeyReq req){
		FUser user = getUser();
		if (user == null) {
			throw new BizException(ErrorCodeEnum.UNKNOWN_ACCOUNT);
        }
		CreateApiKeyResp resp=apiKeyService.createApiKey(user.getFid(),req.getIp(),req.getTypes(),req.getRemark());
		return RespData.ok(resp);
	}
    
    
    @ApiOperation(value = "我的API Key")
	@GetMapping("/apikey/mine")
    @ResponseBody
	public RespData<List<MyApiKeyResp>> myApiKey(){
		FUser user = getUser();
		if (user == null) {
			throw new BizException(ErrorCodeEnum.UNKNOWN_ACCOUNT);
        }
		List<MyApiKeyResp> list=apiKeyService.myApiKey(user.getFid());
		return RespData.ok(list);
	}
    
    
    @ApiOperation(value = "删除API Key")
	@DeleteMapping("/apikey/delete")
    @ResponseBody
	public RespData<Void> deleteApiKey(@RequestParam Integer id){
		FUser user = getUser();
		if (user == null) {
			throw new BizException(ErrorCodeEnum.UNKNOWN_ACCOUNT);
        }
		apiKeyService.deleteApiKey(user.getFid(),id);
		return RespData.ok();
	}
    
    
    @ApiOperation(value = "编辑API Key")
   	@PostMapping("/apikey/update")
       @ResponseBody
   	public RespData<Void> updateApiKey(@Valid  UpdateApiKeyReq req){
   		FUser user = getUser();
   		if (user == null) {
   			throw new BizException(ErrorCodeEnum.UNKNOWN_ACCOUNT);
           }
   		String ip = HttpRequestUtils.getIPAddress();
   		apiKeyService.updateApiKey(req,user.getFid(),ip);
   		return RespData.ok();
   	}
    
    
}
