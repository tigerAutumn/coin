package com.qkwl.admin.layui.controller;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.qkwl.common.dto.ListOpenApiReq;
import com.qkwl.common.dto.ListOpenApiResp;
import com.qkwl.common.dto.SaveOpenApiReq;
import com.qkwl.common.dto.UpdateOpenApiReq;
import com.qkwl.common.entity.OpenApiEntity;
import com.qkwl.common.rpc.user.IOpenApiService;
import com.qkwl.common.util.PageData;
import com.qkwl.common.util.RespData;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Controller
@RequestMapping("/openApi")
@Api(tags="开放api")
public class OpenApiController{
	
	private static final Logger logger = LoggerFactory.getLogger(OpenApiController.class);

    @Autowired
    private IOpenApiService openApiService;
    

    @ApiOperation(value = "open api列表")
    @RequestMapping("/list")
	public ModelAndView listOpenApi(@Valid ListOpenApiReq req){
    	ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("openApi/list");
		modelAndView.addObject("url",req.getUrl());
		PageData<ListOpenApiResp> pageData=openApiService.listOpenApi(req);
		modelAndView.addObject("pageData", pageData);
		return modelAndView;
	}
    
    
    @RequestMapping("/go")
    public ModelAndView goArticle(
            @RequestParam(value = "url", required = true) String url,
            @RequestParam(value = "id", required = false) Integer id){
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName(url);
        if (id != null) {
            OpenApiEntity openApi = openApiService.findById(id);
            modelAndView.addObject("openApi", openApi);
        }
        return modelAndView;
    }
    
    
    @ApiOperation(value = "刪除")
	@RequestMapping("/delete")
    @ResponseBody
	public RespData<Void> delete(@RequestParam Integer id){
    	openApiService.delete(id);
    	return RespData.ok();
	}
    
    
    @ApiOperation(value = "刪除")
	@RequestMapping("/save")
    @ResponseBody
	public RespData<Void> save(@Valid SaveOpenApiReq req){
    	openApiService.save(req);
    	return RespData.ok();
	}
    
    @ApiOperation(value = "修改")
	@RequestMapping("/update")
    @ResponseBody
	public RespData<Void> update(@Valid UpdateOpenApiReq req){
    	openApiService.update(req);
    	return RespData.ok();
	}
 
    
    
}
