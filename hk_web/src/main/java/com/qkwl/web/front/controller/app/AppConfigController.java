package com.qkwl.web.front.controller.app;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.qkwl.common.util.RespData;
import com.qkwl.web.config.AppProperties;
import com.qkwl.web.permission.annotation.PassToken;

import springfox.documentation.annotations.ApiIgnore;

@Controller
@ApiIgnore
public class AppConfigController {

	@Autowired
    private AppProperties appProperties;
	
	/**
	 * 获取app全局配置
	 * @return
	 */
	@ResponseBody
	@PassToken
	@RequestMapping(value="/v2/getConfig")
    public RespData<AppProperties> getConfig(
    		@RequestParam(required = false, defaultValue="") String version,
    		@RequestParam(required = true) Integer type) {
		if (version.equals(appProperties.getVersion())) {
			AppProperties properties = null;
			return RespData.ok(properties);
		}
		if (type == 1) {
			appProperties.setBuglyAppId(appProperties.getBuglyAppIdAndroid());
		} else {
			appProperties.setBuglyAppId(appProperties.getBuglyAppIdIOS());
		}
		return RespData.ok(appProperties);
	}
}
