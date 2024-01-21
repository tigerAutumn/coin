package com.qkwl.web.front.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.qkwl.common.dto.app.VersionUpgradeInfo;
import com.qkwl.common.dto.coin.SystemCoinSetting;
import com.qkwl.common.dto.coin.SystemCoinType;
import com.qkwl.common.dto.coin.SystemCoinTypeVO;
import com.qkwl.common.dto.coin.SystemTradeType;
import com.qkwl.common.dto.user.FUser;
import com.qkwl.common.dto.web.FAbout;
import com.qkwl.common.dto.web.FAboutType;
import com.qkwl.common.dto.web.FSystemLan;
import com.qkwl.common.framework.redis.RedisHelper;
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
public class FrontAboutJsonController extends JsonBaseController {

  @Autowired
  private RedisHelper redisHelper;

  /**
   * 关于我们
   */
  @ResponseBody
  @PassToken
  @ApiOperation("")
	@RequestMapping(value="/about/about_json",method = {RequestMethod.GET,RequestMethod.POST})
  public ReturnResult index(@RequestParam(required = false, defaultValue = "0") Integer id) {
    FSystemLan systemLan =
        redisHelper.getLanguageType(I18NUtils.getCurrentLang());
    if (systemLan == null) {
      return ReturnResult.FAILUER("");
    }
    // 获取当前语言下所有的about种类
    List<FAboutType> fabouttypes = redisHelper.getAboutTypeList(1, WebConstant.BCAgentId);
    // 查找当前语言下制定id下的详细信息
    FAbout fabout = redisHelper.getAbout(id, 1, WebConstant.BCAgentId);
    JSONObject jsonObject = new JSONObject();
    jsonObject.put("fabout", fabout);
    jsonObject.put("fabouttypes", fabouttypes);
    return ReturnResult.SUCCESS(jsonObject);
  }

  /**
   * 关于我们
   * 
   * @param type 1：安卓 2：ios
   */
  @ResponseBody
  @PassToken
  @ApiOperation("")
	@RequestMapping(value="/about/app_version_json",method = {RequestMethod.GET,RequestMethod.POST})
  public ReturnResult appVersionInfo(
      @RequestParam(required = false, defaultValue = "1") Integer type) {
    VersionUpgradeInfo appVersion = redisHelper.getAppVersion(type);
    return ReturnResult.SUCCESS(appVersion);
  }

  /**
   * 查询各个虚拟币提现手续费
   */
  @ResponseBody
  @PassToken
  @ApiOperation("")
	@RequestMapping(value="/about/withdraw_fee_json",method = {RequestMethod.GET,RequestMethod.POST})
  public ReturnResult withdrawfFee() {
    FUser user = getCurrentUserInfoByToken();
    if (user == null) {
      user = new FUser();
      user.setLevel(0);
    }
    // 币种查找
    List<SystemCoinType> coinTypeList = redisHelper.getCoinTypeList();
    if (coinTypeList == null) {
      return ReturnResult.FAILUER("");
    }
    JSONObject jsonObject = new JSONObject();
    JSONArray jsonArray = new JSONArray();
    for (SystemCoinType systemCoinType : coinTypeList) {
      JSONObject jo = new JSONObject();
      SystemCoinSetting coinSetting =
          redisHelper.getCoinSetting(systemCoinType.getId(), user.getLevel());
      if (coinSetting == null) {
        continue;
      }
      jo.put("logo", systemCoinType.getWebLogo());
      jo.put("shortName", systemCoinType.getShortName());
      jo.put("name", systemCoinType.getName());
      jo.put("withdrawMin", coinSetting.getWithdrawMin());
      jo.put("isPercentage", coinSetting.getIsPercentage());
      jo.put("withdrawFee", coinSetting.getWithdrawFee());
      jsonArray.add(jo);
    }
    // 币种查询手续费
    jsonObject.put("coinList", jsonArray);
    List<SystemTradeType> allTradeTypeList = redisHelper.getTradeTypeList(WebConstant.BCAgentId);
    JSONArray tradeArray = new JSONArray();
    for (SystemTradeType systemTradeType : allTradeTypeList) {
      JSONObject jo = new JSONObject();
      jo.put("tradeName",
          systemTradeType.getSellShortName() + "/" + systemTradeType.getBuyShortName()); //$NON-NLS-1$
      jo.put("buyFee", systemTradeType.getBuyFee());
      jo.put("sellFee", systemTradeType.getSellFee());
      tradeArray.add(jo);
    }
    // 币种查询手续费
    jsonObject.put("tradeList", tradeArray);
    jsonObject.put("rechargeFee", I18NUtils.getString("FrontAboutJsonController.17")); //$NON-NLS-2$
    return ReturnResult.SUCCESS(jsonObject);
  }

  /**
   * 币种列表
   */
  @ResponseBody
  @PassToken
  @ApiOperation("")
	@RequestMapping(value="/about/virtual_coin_json",method = {RequestMethod.GET,RequestMethod.POST})
  public ReturnResult coinList() {
	  List<SystemCoinType> coinTypeCoinList = redisHelper.getCoinTypeCoinList();
	  if(coinTypeCoinList == null || coinTypeCoinList.size() == 0) {
		  return ReturnResult.SUCCESS(coinTypeCoinList);
	  }
	  List<SystemCoinType> collect = coinTypeCoinList.parallelStream().filter(c -> {
				return !(!c.getIsSubCoin() && !StringUtils.isEmpty(c.getLinkCoin())); 
			}).collect(Collectors.toList());
	  if(collect == null || collect.size() == 0) {
		  return ReturnResult.SUCCESS(collect);
	  }
	  List<SystemCoinTypeVO> mapper =
        ModelMapperUtils.mapper(collect, SystemCoinTypeVO.class);
	  return ReturnResult.SUCCESS(mapper);
  }

}
