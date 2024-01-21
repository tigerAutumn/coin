package com.qkwl.admin.layui.controller;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.qkwl.common.dto.AdminUpdateApiKeyReq;
import com.qkwl.common.dto.ListApiKeyReq;
import com.qkwl.common.dto.ListApiKeyResp;
import com.qkwl.common.dto.UpdateOpenApiReq;
import com.qkwl.common.dto.Enum.ApiKeyStatusEnum;
import com.qkwl.common.entity.ApiKeyEntity;
import com.qkwl.common.entity.OpenApiEntity;
import com.qkwl.common.rpc.user.IApiKeyService;
import com.qkwl.common.util.PageData;
import com.qkwl.common.util.RespData;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Controller
@RequestMapping("/userApi")
@Api(tags="API用户管理")
public class UserApiController{
	
	private static final Logger logger = LoggerFactory.getLogger(UserApiController.class);

    @Autowired
    private IApiKeyService apiKeyService;
    

    @ApiOperation(value = "API用户管理列表")
    @RequestMapping("/list")
	public ModelAndView listApiKey(@Valid ListApiKeyReq req){
    	ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("userApi/list");
		modelAndView.addObject("uid",req.getUid());
		modelAndView.addObject("status",req.getStatus());
		PageData<ListApiKeyResp> pageData=apiKeyService.listApiKey(req);
		modelAndView.addObject("pageData", pageData);
		return modelAndView;
	}
    
    
    @ApiOperation(value = "启用")
    @RequestMapping("/enable")
    @ResponseBody
	public RespData<Void> enable(@RequestParam Integer id){
    	apiKeyService.updateStatus(id,ApiKeyStatusEnum.NORMAL);
		return RespData.ok();
	}
    
    
    @ApiOperation(value = "禁用")
	@RequestMapping("/disable")
    @ResponseBody
	public RespData<Void> disable(@RequestParam Integer id){
    	apiKeyService.updateStatus(id,ApiKeyStatusEnum.DISABLE);
    	return RespData.ok();
	}
    
    @ApiOperation(value = "跳转")
    @RequestMapping("/go")
    public ModelAndView goArticle(
            @RequestParam(value = "url", required = true) String url,
            @RequestParam(value = "id", required = false) Integer id){
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName(url);
        if (id != null) {
            ApiKeyEntity apiKeyEntity = apiKeyService.findById(id);
            modelAndView.addObject("apiKey", apiKeyEntity);
        }
        return modelAndView;
    }
    
    
    
    @ApiOperation(value = "修改")
	@RequestMapping("/update")
    @ResponseBody
	public RespData<Void> update(@Valid AdminUpdateApiKeyReq req){
    	apiKeyService.update(req);
    	return RespData.ok();
	}
 
    
    
}
