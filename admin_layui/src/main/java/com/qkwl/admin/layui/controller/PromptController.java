package com.qkwl.admin.layui.controller;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.qkwl.admin.layui.base.WebBaseController;
import com.qkwl.common.framework.redis.RedisHelper;
import com.qkwl.common.properties.OssConstantProperties;
import com.qkwl.common.redis.MemCache;
import com.qkwl.common.util.HttpClientUtil;
import com.qkwl.common.util.HttpClientUtilV2;
import com.qkwl.common.util.RespData;
import com.qkwl.common.util.ReturnResult;

@Controller
public class PromptController extends WebBaseController {

	private static final Logger logger = LoggerFactory.getLogger(PromptController.class);
	
	@Autowired
    private RedisHelper redisHelper;
	
	@Autowired
	private MemCache memCache;
	
	@Autowired
	private OssConstantProperties ossConstantProperties;
	
	/**
	 * 升级判断接口
	 */
	@CrossOrigin
	@RequestMapping(value = "/prompt/getPrompt")
	@ResponseBody
    public ReturnResult getPrompt() {
		JSONObject jsonObject = new JSONObject();
		
		Map<String, Object> params = Maps.newHashMap();
		params.put("fileDir", "public");
		params.put("fileName", "prompt.json");

		String result = null;

		if(ossConstantProperties.getOssServerUrl().contains("https")) {
			result = HttpClientUtilV2.httpsRequest(ossConstantProperties.getOssServerUrl() + "/oss_server/getResourceUrl", "POST", params);
		}else {
			result = HttpClientUtil.doPost(ossConstantProperties.getOssServerUrl() + "/oss_server/getResourceUrl", params);
		}
		
		RespData respData = JSON.parseObject(result, RespData.class);
		
		String pathUrl = "";
		
		if(respData.getData() instanceof String && StringUtils.isNotBlank((pathUrl = (String) respData.getData()))) {
	    	String resultJson = HttpClientUtil.doGet(pathUrl);
			if (StringUtils.isNotBlank(resultJson)) {
				JSONObject jo = JSONObject.parseObject(resultJson);
				
				jsonObject.put("isUpdating", jo.get("isUpdating") != null ? String.valueOf(jo.get("isUpdating")): null);
				jsonObject.put("startTime", jo.get("startTime") != null ? String.valueOf(jo.get("startTime")): null);
				jsonObject.put("endTime", jo.get("endTime") != null ? String.valueOf(jo.get("endTime")): null);
			}else {
				jsonObject.put("isUpdating", "0");
			}
		}else {
			jsonObject.put("isUpdating", "0");
		}
		return ReturnResult.SUCCESS(jsonObject);
    }
	
	
	/**
	 * 维护状态页面
	 */
	@RequestMapping(value = "/prompt/promptList")
	public ModelAndView getUpdatingPrompt() {
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("prompt/promptList");
		
		Map<String, Object> params = Maps.newHashMap();
		params.put("fileDir", "public");
		params.put("fileName", "prompt.json");
		
		String result = null;

		if(ossConstantProperties.getOssServerUrl().contains("https")) {
			result = HttpClientUtilV2.httpsRequest(ossConstantProperties.getOssServerUrl() + "/oss_server/getResourceUrl", "POST", params);
		}else {
			result = HttpClientUtil.doPost(ossConstantProperties.getOssServerUrl() + "/oss_server/getResourceUrl", params);
		}
		
		RespData respData = JSON.parseObject(result, RespData.class);
		
		logger.info("serverUrl:{}, result:{}", ossConstantProperties.getOssServerUrl(), JSONObject.toJSONString(result));
		
		String pathUrl = "";
		
		if(respData.getData() instanceof String && StringUtils.isNotBlank((pathUrl = (String) respData.getData()))) {
	    	String resultJson = HttpClientUtil.doGet(pathUrl);
			if (StringUtils.isNotBlank(resultJson)) {
				JSONObject jo = JSONObject.parseObject(resultJson);
				
				modelAndView.addObject("isUpdating", jo.get("isUpdating") != null ? String.valueOf(jo.get("isUpdating")): null);
				modelAndView.addObject("startTime", jo.get("startTime") != null ? String.valueOf(jo.get("startTime")): null);
				modelAndView.addObject("endTime", jo.get("endTime") != null ? String.valueOf(jo.get("endTime")): null);
			}
		}
		return modelAndView;
	}
	
	
	/**
	 * 修改维护状态页面
	 */
	@RequestMapping(value = "/prompt/updatePrompt")
	public ModelAndView updatePrompt() {
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("prompt/updatePrompt");


		Map<String, Object> params = Maps.newHashMap();
		params.put("fileDir", "public");
		params.put("fileName", "prompt.json");
		

		String result = null;

		if(ossConstantProperties.getOssServerUrl().contains("https")) {
			result = HttpClientUtilV2.httpsRequest(ossConstantProperties.getOssServerUrl() + "/oss_server/getResourceUrl", "POST", params);
		}else {
			result = HttpClientUtil.doPost(ossConstantProperties.getOssServerUrl() + "/oss_server/getResourceUrl", params);
		}
		RespData respData = JSON.parseObject(result, RespData.class);
		
		String pathUrl = "";
		
		if(respData.getData() instanceof String && StringUtils.isNotBlank((pathUrl = (String) respData.getData()))) {
	    	String resultJson = HttpClientUtil.doGet(pathUrl);
			if (StringUtils.isNotBlank(resultJson)) {
				JSONObject jo = JSONObject.parseObject(resultJson);
				
				modelAndView.addObject("isUpdating", jo.get("isUpdating") != null ? String.valueOf(jo.get("isUpdating")): null);
				modelAndView.addObject("startTime", jo.get("startTime") != null ? String.valueOf(jo.get("startTime")): null);
				modelAndView.addObject("endTime", jo.get("endTime") != null ? String.valueOf(jo.get("endTime")): null);
			}
		}

		return modelAndView;
	}
	
	/**
	 * 保存修改
	 */
	@RequestMapping("prompt/savePrompt")
    @ResponseBody
    public ReturnResult savePrompt(
            @RequestParam(value = "isUpdating", required = false) String isUpdating,
            @RequestParam(value = "startTime", required = true) String startTime,
            @RequestParam(value = "endTime", required = false) String endTime,
            HttpServletRequest request
            ) throws Exception {
		
		Map<String, String> map = Maps.newHashMap();
		map.put("isUpdating", isUpdating);
		map.put("startTime", startTime);
		map.put("endTime", endTime);

		MultipartFile file = new MockMultipartFile("prompt.json", "prompt.json", "application/json", JSON.toJSONString(map).getBytes());

		Map<String, String> params = Maps.newHashMap();
		params.put("fileDir", "public");
		params.put("fileName", "prompt.json");
		params.put("isOriginNameStore", Boolean.TRUE.toString());
		
		Map<String,MultipartFile> fileParams = Maps.newHashMap();
		fileParams.put("file", file);

		String result = null;

		if(ossConstantProperties.getOssServerUrl().contains("https")) {
			result = HttpClientUtil.uploadFileWithSSL(ossConstantProperties.getOssServerUrl() + "/oss_server/v2/upload", params, fileParams);
		}else {
			result = HttpClientUtil.uploadFile(ossConstantProperties.getOssServerUrl() + "/oss_server/v2/upload", params, fileParams);
		}
		
		
        return ReturnResult.SUCCESS("更新成功", result);
    }
	
}
