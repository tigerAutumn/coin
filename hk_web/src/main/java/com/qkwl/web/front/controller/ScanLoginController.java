package com.qkwl.web.front.controller;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.qkwl.common.Enum.validate.PlatformEnum;
import com.qkwl.common.dto.Enum.ErrorCodeEnum;
import com.qkwl.common.dto.user.FUser;
import com.qkwl.common.dto.user.GenerateQRCodeTokenResp;
import com.qkwl.common.dto.user.LoginResponse;
import com.qkwl.common.dto.user.RequestUserInfo;
import com.qkwl.common.exceptions.BizException;
import com.qkwl.common.rpc.user.IScanLoginService;
import com.qkwl.common.util.HttpRequestUtils;
import com.qkwl.common.util.RespData;
import com.qkwl.common.util.SecurityUtil;
import com.qkwl.web.config.HkWebProperties;
import com.qkwl.web.front.controller.base.JsonBaseController;
import com.qkwl.web.permission.annotation.PassToken;
import com.qkwl.web.utils.WebConstant;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Controller
@RequestMapping("/scanLogin")
@Api(tags="扫码登录")
public class ScanLoginController extends JsonBaseController{
	
	private static final Logger logger = LoggerFactory.getLogger(ScanLoginController.class);

    @Autowired
    private IScanLoginService scanLoginService;
    @Autowired
    private HkWebProperties hkWebProperties;
    

    @ApiOperation(value = "生成扫码登录二维码token,前端获取到该token之后生成二维码图片")
	@PassToken
	@GetMapping("/generateQRCodeToken")
    @ResponseBody
	public RespData<GenerateQRCodeTokenResp> generateQRCodeToken(){
		GenerateQRCodeTokenResp  resp=scanLoginService.generateQRCodeToken();
		return RespData.ok(resp);
	}
	
    
    @ApiOperation(value="app扫码后将qrCodeToken与用户绑定")
    @PassToken
	@GetMapping("/determine")
	public void determine(@RequestParam String qrCodeToken,@RequestParam(required = false,defaultValue = "false") boolean  isHotCoinApp,HttpServletResponse response) throws IOException{
    	logger.error("isHotCoinApp:{}",isHotCoinApp);
    	if(isHotCoinApp) {
    		FUser fuser = getCurrentUserInfoByToken();
    		if(fuser==null) {
        		throw new BizException(ErrorCodeEnum.UNKNOWN_ACCOUNT);
        	}else {
        		scanLoginService.determine(fuser.getFid(),qrCodeToken);
        		HttpRequestUtils.writeJsonResp(RespData.ok(), response);
        	}
    	}else {
    		response.sendRedirect(hkWebProperties.getDownLoadUrl());
    	}
	}
    
    
    @ApiOperation(value="ajax定时请求是否有用户扫描了二维码")
    @PassToken
    @PostMapping("/login")
    @ResponseBody
	public RespData<LoginResponse> login(@RequestParam String qrCodeToken){
		// 登录参数
		RequestUserInfo requestUserInfo = new RequestUserInfo();
		requestUserInfo.setFagentid(WebConstant.BCAgentId);
		requestUserInfo.setPlatform(PlatformEnum.BC);
    	
		logger.info("qrCodeToken:{}",qrCodeToken);
    	LoginResponse resp = scanLoginService.scanLogin(qrCodeToken,HttpRequestUtils.getIPAddress(), super.getLanEnum(),requestUserInfo);
		sessionContextUtils.addContextToken("token",resp.getToken()); 
		//数据脱敏
		SecurityUtil.fuzzyUserData(resp.getUserinfo());
    	return RespData.ok(resp);
	}
    
}
