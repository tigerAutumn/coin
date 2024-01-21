package com.qkwl.web.front.controller;


import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.qkwl.common.dto.Enum.SystemTradeTypeEnum;
import com.qkwl.common.dto.Enum.SystemTradeTypeNewEnum;
import com.qkwl.common.dto.coin.SystemTradeType;
import com.qkwl.common.dto.coin.SystemTradeTypeVO;
import com.qkwl.common.framework.redis.RedisHelper;
import com.qkwl.common.model.KeyValues;
import com.qkwl.common.rpc.push.PushService;
import com.qkwl.common.util.I18NUtils;
import com.qkwl.common.util.ModelMapperUtils;
import com.qkwl.common.util.ReturnResult;
import com.qkwl.web.front.controller.base.JsonBaseController;
import com.qkwl.web.permission.annotation.PassToken;
import com.qkwl.web.utils.WebConstant;

import io.swagger.annotations.ApiOperation;
import springfox.documentation.annotations.ApiIgnore;

@Controller
@ApiIgnore
public class FrontIndexJsonController extends JsonBaseController {
	
	private static final Logger logger = LoggerFactory.getLogger(FrontIndexJsonController.class);

    @Autowired
    private RedisHelper redisHelper;
    @Autowired
    private PushService pushService;

    @ResponseBody
    @PassToken
    @ApiOperation("")
	@RequestMapping(value="/index_json",method = {RequestMethod.GET,RequestMethod.POST})
    public ReturnResult indexs() {
        List<SystemTradeType> tradeTypeList = redisHelper.getTradeTypeList(WebConstant.BCAgentId);
        // 交易分类
        Map<Integer, Object> typeMap = new LinkedHashMap<>();

        Map<Integer, String> SymbolMap = new LinkedHashMap<>();
        for (SystemTradeTypeEnum typeEnum : SystemTradeTypeEnum.values()) {
            typeMap.put(typeEnum.getCode(), typeEnum.getValue());
            SymbolMap.put(typeEnum.getCode(), typeEnum.getSymbol());
        }

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("typeFirst", SystemTradeTypeEnum.GAVC.getCode()); 
        jsonObject.put("tradeTypeList", ModelMapperUtils.mapper(tradeTypeList, SystemTradeTypeVO.class)); 
        jsonObject.put("typeMap", typeMap); 
        jsonObject.put("SymbolMap", SymbolMap); 
        // bank info
        return ReturnResult.SUCCESS(jsonObject);
    }
    
    @ResponseBody
    @ApiOperation("")
	@RequestMapping(value="/index_json_new",method = {RequestMethod.GET,RequestMethod.POST})
    public ReturnResult indexsNew() {
    		List<SystemTradeType> tradeTypeList = redisHelper.getTradeTypeList(WebConstant.BCAgentId);
            // 交易分类
            Map<Integer, Object> typeMap = new LinkedHashMap<>();
            Map<Integer, String> SymbolMap = new LinkedHashMap<>();
            for (SystemTradeTypeNewEnum typeEnum : SystemTradeTypeNewEnum.values()) {
	                typeMap.put(typeEnum.getCode(), typeEnum.getValue());
	                SymbolMap.put(typeEnum.getCode(), typeEnum.getSymbol());
            }
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("typeFirst", SystemTradeTypeNewEnum.GAVC.getCode()); 
            jsonObject.put("tradeTypeList", ModelMapperUtils.mapper(tradeTypeList, SystemTradeTypeVO.class)); 
            jsonObject.put("typeMap", typeMap); 
            jsonObject.put("SymbolMap", SymbolMap); 
            // bank info
            return ReturnResult.SUCCESS(jsonObject);
    }

    @ResponseBody
    @PassToken
    @ApiOperation("")
	@RequestMapping(value="/articles_json",method = {RequestMethod.GET,RequestMethod.POST})
    public ReturnResult getarticles(@RequestParam(required = false, defaultValue = "1") Integer locale) throws Exception {
        //en,cn,tw
        String localeStr = "zh_TW"; 
        switch (locale) {
            case 1:
                localeStr = "zh_TW"; 
                break;  //繁体
            case 2:
                localeStr = "en_US"; 
                break;
            case 3:
                localeStr = "zh_CN"; 
                break;
        }

        List<KeyValues> articles = pushService.getArticles(localeStr);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("articles", articles); 
        // bank info
        return ReturnResult.SUCCESS(jsonObject);
    }

}
