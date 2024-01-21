package com.qkwl.admin.layui.controller;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.util.TextUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.hotcoin.coin.utils.CoinRpcUtlis;
import com.qkwl.admin.layui.base.WebBaseController;
import com.qkwl.admin.layui.utils.MQSend;
import com.qkwl.common.Enum.validate.LocaleEnum;
import com.qkwl.common.Enum.validate.PlatformEnum;
import com.qkwl.common.coin.CoinDriver;
import com.qkwl.common.coin.CoinDriverFactory;
import com.qkwl.common.dto.Enum.LogAdminActionEnum;
import com.qkwl.common.dto.Enum.SystemCoinSortEnum;
import com.qkwl.common.dto.Enum.SystemCoinStatusEnum;
import com.qkwl.common.dto.Enum.SystemCoinTypeEnum;
import com.qkwl.common.dto.Enum.SystemTradeStatusEnum;
import com.qkwl.common.dto.Enum.SystemTradeTypeNewEnum;
import com.qkwl.common.dto.Enum.UserFinancesStateEnum;
import com.qkwl.common.dto.admin.FAdmin;
import com.qkwl.common.dto.coin.SystemCoinInfo;
import com.qkwl.common.dto.coin.SystemCoinSetting;
import com.qkwl.common.dto.coin.SystemCoinType;
import com.qkwl.common.dto.common.Pagination;
import com.qkwl.common.dto.finances.FUserFinancesDTO;
import com.qkwl.common.dto.finances.FVirtualFinances;
import com.qkwl.common.framework.redis.RedisHelper;
import com.qkwl.common.rpc.admin.IAdminEntrustServer;
import com.qkwl.common.rpc.admin.IAdminSystemCoinTypeService;
import com.qkwl.common.rpc.admin.IAdminSystemTradeTypeService;
import com.qkwl.common.rpc.admin.IAdminUserFinances;
import com.qkwl.common.util.Constant;
import com.qkwl.common.util.HttpRequestUtils;
import com.qkwl.common.util.ReturnResult;

@Controller
public class SystemCoinTypeController extends WebBaseController {

    private static final Logger logger = LoggerFactory.getLogger(SystemCoinTypeController.class);

    @Autowired
    private IAdminSystemCoinTypeService adminSystemCoinTypeService;
    @Autowired
	private IAdminSystemTradeTypeService adminSystemTradeTypeService;
    @Autowired
    private MQSend mqSend;
    @Autowired
    private RedisHelper redisHelper;
    @Autowired
    private IAdminUserFinances adminUserFinances;
    @Autowired
    private IAdminEntrustServer adminEntrustServer;

    /**
     * 加载币种列表
     */
    @RequestMapping("admin/coinTypeList")
    public ModelAndView virtualCoinTypeList(
            @RequestParam(value = "pageNum", defaultValue = "1") Integer currentPage,
            @RequestParam(value = "keywords", required = false) String keywords,
            @RequestParam(value = "orderField", defaultValue = "gmt_create") String orderField,
            @RequestParam(value = "orderDirection", defaultValue = "desc") String orderDirection
    ) throws Exception {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("coin/coinTypeList");

        Pagination<SystemCoinType> page = new Pagination<>(currentPage, Constant.adminPageSize);
        page.setOrderDirection(orderDirection);
        page.setOrderField(orderField);
        page.setKeyword(keywords);

        SystemCoinType fVirtualCoinType = new SystemCoinType();

        page = adminSystemCoinTypeService.selectVirtualCoinList(page, fVirtualCoinType);

        if (keywords != null && !"".equals(keywords)) {
            modelAndView.addObject("keywords", keywords);
        }

        Map<Integer, String> platformMap = new LinkedHashMap<>();
        platformMap.put(0, "全平台");
        for (PlatformEnum platform : PlatformEnum.values()) {
            platformMap.put(platform.getCode(), platform.getValue());
        }
        modelAndView.addObject("platformMap", platformMap);

        modelAndView.addObject("page", page);
        return modelAndView;
    }

    /**
     * 加载币种信息列表
     */
    @RequestMapping("admin/coinInfoList")
    public ModelAndView virtualCoinInfoList(
            @RequestParam(value = "coinId",required = false) Integer coinId,
            @RequestParam(value = "lanName",required = false) String lanName
    ) throws Exception {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("coin/coinInfoList");
        Map<String,Object> params = new HashMap<>();
        if ( null != coinId && 0 != coinId){
            params.put("coinId",coinId);
        }
        if (!TextUtils.isEmpty(lanName) && !"0".equals(lanName)){
            params.put("lanName",lanName);
        }

        Pagination<SystemCoinInfo> systemCoinInfoPagination = adminSystemCoinTypeService.selectSystemCoinInfoList(params);
        List<SystemCoinInfo> page = (List<SystemCoinInfo>)  systemCoinInfoPagination.getData();
        List<SystemCoinType> coinTypeListAll = redisHelper.getCoinTypeListAll();

        Map<Integer,Object> coinMap = new LinkedHashMap<>();
        coinMap.put(0,"全部");
        for (SystemCoinType coinType : coinTypeListAll) {
            coinMap.put(coinType.getId(),coinType.getName());
        }
        Map<String, Object> localeMap = new LinkedHashMap<>();
        localeMap.put("0","全部");
        for (LocaleEnum localeEnum : LocaleEnum.values()) {
            localeMap.put(localeEnum.getCode()+"", localeEnum.getValue());
        }

        modelAndView.addObject("page", page);
        modelAndView.addObject("lanName", lanName);
        modelAndView.addObject("coinId",coinId);
        modelAndView.addObject("coinMap",coinMap);
        modelAndView.addObject("localeMap", localeMap);
        return modelAndView;
    }

    /**
     * 页面跳转
     */
    @RequestMapping("admin/goCoinInfoJSP")
    public ModelAndView goVirtualCoinInfoJSP(
            @RequestParam(value = "url", required = false) String url,
            @RequestParam(value = "id", defaultValue = "0") Integer id
    ) throws Exception {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName(url);
        if (id > 0) {
            SystemCoinInfo coinInfo = this.adminSystemCoinTypeService.selectSystemCoinInfo(id);
            modelAndView.addObject("coinInfo", coinInfo);
        }
        Map<String, Object> typeMap = new LinkedHashMap<>();
        for (LocaleEnum localeEnum : LocaleEnum.values()) {
            typeMap.put(localeEnum.getCode()+"", localeEnum.getValue());
        }
        List<SystemCoinType> coinTypeListAll = redisHelper.getCoinTypeListAll();
        Map<Integer,Object> coinMap = new LinkedHashMap<>();
        for (SystemCoinType coinType : coinTypeListAll) {
            coinMap.put(coinType.getId(),coinType.getName());
        }
        modelAndView.addObject("coinMap",coinMap);
        modelAndView.addObject("localeMap", typeMap);
        return modelAndView;
    }

    /**
     * 保存新增的币种信息
     */
    @RequestMapping("admin/saveCoinInfo")
    @ResponseBody
    public ReturnResult saveVirtualCoinInfo(
            @RequestParam(value = "coinId", required = true,defaultValue = "0") Integer coinId,
            @RequestParam(value = "nameZh", required = false) String nameZh,
            @RequestParam(value = "nameEn", required = false) String nameEn,
            @RequestParam(value = "nameShort", required = false) String nameShort,
            @RequestParam(value = "total", required = false) String total,
            @RequestParam(value = "price", required = false) String price,
            @RequestParam(value = "circulation", required = false) String circulation,
            @RequestParam(value = "linkWebsite", required = false) String linkWebsite,
            @RequestParam(value = "linkBlock", required = false) String linkBlock,
            @RequestParam(value = "linkWhitepaper", required = false) String linkWhitepaper,
            @RequestParam(value = "rechargeWarnWord", required = false) String rechargeWarnWord,
            @RequestParam(value = "info", required = false) String info,
            @RequestParam(value = "highlights", required = false) String highlights,
            @RequestParam(value = "scene", required = false) String scene,
            @RequestParam(value = "lanName", required = false,defaultValue = "0") String lanName,
            @RequestParam(value = "gmtRelease", required = false) String gmtRelease)
            throws Exception {
        if (coinId == 0){
            ReturnResult.FAILUER("选择币种");
        }
        if (TextUtils.isEmpty(lanName) || "0".equals(lanName)){
            ReturnResult.FAILUER("选择语言");
        }

        Map<String,Object> params = new HashMap<>();
        params.put("coinId",coinId);
        params.put("lanName",lanName);
        Pagination<SystemCoinInfo> systemCoinInfoList = this.adminSystemCoinTypeService.selectSystemCoinInfoList(params);
        if (systemCoinInfoList.getData() !=null && systemCoinInfoList.getData().size() > 0){
            return ReturnResult.FAILUER("当前币种的信息已经存在了");
        }

        SystemCoinInfo coinInfo = new SystemCoinInfo();
        coinInfo.setCoinId(coinId);
        coinInfo.setNameZh(nameZh);
        coinInfo.setNameEn(nameEn);
        coinInfo.setNameShort(nameShort);
        coinInfo.setTotal(total);
        coinInfo.setPrice(price);
        coinInfo.setCirculation(circulation);
        coinInfo.setLinkWebsite(linkWebsite);
        coinInfo.setLinkBlock(linkBlock);
        coinInfo.setLanName(lanName);
        coinInfo.setInfo(info);
        coinInfo.setGmtRelease(gmtRelease);
        coinInfo.setLinkWhitepaper(linkWhitepaper);
        coinInfo.setScene(scene);
        coinInfo.setHighlights(highlights);
        coinInfo.setRechargeWarnWord(rechargeWarnWord);
        if (this.adminSystemCoinTypeService.insertSystemCoinInfo(coinInfo)) {
            String ip = HttpRequestUtils.getIPAddress();
            FAdmin sessionAdmin = (FAdmin) sessionContextUtils.getContextRequest().getSession()
                    .getAttribute("login_admin");
            mqSend.SendAdminAction(sessionAdmin.getFagentid(), sessionAdmin.getFid(), LogAdminActionEnum.COIN_INFO_ADD, ip);
        }
        return ReturnResult.SUCCESS("新增成功！");
    }

    /**
     * 修改的币种信息
     */
    @RequestMapping("admin/updateCoinInfo")
    @ResponseBody
    public ReturnResult updateCoinType(
            @RequestParam(value = "id", required = true,defaultValue = "0") Integer id,
            @RequestParam(value = "coinId", required = true,defaultValue = "0") Integer coinId,
            @RequestParam(value = "nameZh", required = false) String nameZh,
            @RequestParam(value = "nameEn", required = false) String nameEn,
            @RequestParam(value = "nameShort", required = false) String nameShort,
            @RequestParam(value = "total", required = false) String total,
            @RequestParam(value = "price", required = false) String price,
            @RequestParam(value = "circulation", required = false) String circulation,
            @RequestParam(value = "linkWebsite", required = false) String linkWebsite,
            @RequestParam(value = "linkBlock", required = false) String linkBlock,
            @RequestParam(value = "linkWhitepaper", required = false) String linkWhitepaper,
            @RequestParam(value = "info", required = false) String info,
            @RequestParam(value = "highlights", required = false) String highlights,
            @RequestParam(value = "scene", required = false) String scene,
            @RequestParam(value = "rechargeWarnWord", required = false) String rechargeWarnWord,
            @RequestParam(value = "lanName", required = false) String lanName,
            @RequestParam(value = "gmtRelease", required = false) String gmtRelease) throws Exception {
        SystemCoinInfo coinInfo = adminSystemCoinTypeService.selectSystemCoinInfo(id);
        if (coinInfo == null) {
            return ReturnResult.FAILUER("币种信息不存在,请刷新重试！");
        }
        if (coinId == 0){
            ReturnResult.FAILUER("选择币种");
        }
        if (TextUtils.isEmpty(lanName) || "0".equals(lanName)){
            ReturnResult.FAILUER("选择语言");
        }

        Map<String,Object> params = new HashMap<>();
        params.put("coinId",coinId);
        params.put("lanName",lanName);
       /* Pagination<SystemCoinInfo> systemCoinInfoList = this.adminSystemCoinTypeService.selectSystemCoinInfoList(params);
        List<SystemCoinInfo> data = (List<SystemCoinInfo>) systemCoinInfoList.getData();
        if (data !=null && data.size() > 0 && data.get(0).getId() != id){
            return ReturnResult.FAILUER("当前币种的信息已经存在了");
        }*/
        coinInfo.setId(id);
        coinInfo.setCoinId(coinId);
        coinInfo.setNameZh(nameZh);
        coinInfo.setNameEn(nameEn);
        coinInfo.setNameShort(nameShort);
        coinInfo.setTotal(total);
        coinInfo.setPrice(price);
        coinInfo.setCirculation(circulation);
        coinInfo.setLinkWebsite(linkWebsite);
        coinInfo.setLinkBlock(linkBlock);
        coinInfo.setLanName(lanName);
        coinInfo.setInfo(info);
        coinInfo.setGmtRelease(gmtRelease);
        coinInfo.setLinkWhitepaper(linkWhitepaper);
        coinInfo.setScene(scene);
        coinInfo.setHighlights(highlights);
        coinInfo.setRechargeWarnWord(rechargeWarnWord);
        if (this.adminSystemCoinTypeService.updateSystemCoinInfo(coinInfo)) {
            String ip = HttpRequestUtils.getIPAddress();
            FAdmin sessionAdmin = (FAdmin) sessionContextUtils.getContextRequest().getSession()
                    .getAttribute("login_admin");
            mqSend.SendAdminAction(sessionAdmin.getFagentid(), sessionAdmin.getFid(), LogAdminActionEnum.COIN_INFO_ADD, ip);
        }
        return ReturnResult.SUCCESS("修改成功");
    }

    /**
     * 删除的币种信息
     */
    @RequestMapping("admin/deleteCoinInfo")
    @ResponseBody
    public ReturnResult deleteCoinInfo(
            @RequestParam(value = "id", required = true,defaultValue = "0") Integer id) throws Exception {
        if (adminSystemCoinTypeService.deleteSystemCoinInfo(id)){
            return ReturnResult.SUCCESS("删除成功");
        }
        return ReturnResult.FAILUER("删除失败");
    }
    
    
    
    
    @RequestMapping("admin/generateCoinAccount")
    @ResponseBody
    public ReturnResult generateCoinAccount(@RequestParam(value = "coinId", defaultValue = "0") Integer coinId) {
    	SystemCoinType selectVirtualCoinById = adminSystemCoinTypeService.selectVirtualCoinById(coinId);
    	if(selectVirtualCoinById.getIsSubCoin()) {
    		return ReturnResult.FAILUER("从属币种不允许生成客户钱包");
		}
    	adminSystemCoinTypeService.generateCoinAccount(coinId);
    	return ReturnResult.SUCCESS("正在生成中！");
    }
    
    

    /**
     * 页面跳转
     */
    @RequestMapping("admin/goCoinTypeJSP")
    public ModelAndView goVirtualCoinTypeJSP(
            @RequestParam(value = "url", required = false) String url,
            @RequestParam(value = "coinId", defaultValue = "0") Integer coinId
    ) throws Exception {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName(url);
        if (coinId > 0) {
            SystemCoinType coinType = this.adminSystemCoinTypeService.selectVirtualCoinById(coinId);
            modelAndView.addObject("coinType", coinType);
            modelAndView.addObject("allFees",
                    adminSystemCoinTypeService.selectSystemCoinSettingList(coinType.getId()));
        }
        Map<Integer, Object> typeMap = new LinkedHashMap<>();
        for (SystemCoinTypeEnum typeEnum : SystemCoinTypeEnum.values()) {
            typeMap.put(typeEnum.getCode(), typeEnum.getValue());
        }
        modelAndView.addObject("typeMap", typeMap);

        Map<Integer, Object> coinTypeMap = new LinkedHashMap<>();
        for (SystemCoinSortEnum coinTypeEnum : SystemCoinSortEnum.values()) {
            coinTypeMap.put(coinTypeEnum.getCode(), coinTypeEnum.getValue());
        }
        modelAndView.addObject("coinTypeMap", coinTypeMap);

        Map<Integer, String> platformMap = new LinkedHashMap<>();
        platformMap.put(0, "全平台");
        for (PlatformEnum platform : PlatformEnum.values()) {
            platformMap.put(platform.getCode(), platform.getValue());
        }
        modelAndView.addObject("platformMap", platformMap);
        return modelAndView;
    }



    /**
     * 保存新增的币种信息
     */
    @RequestMapping("admin/saveCoinType")
    @ResponseBody
    public ReturnResult saveVirtualCoinType(
    		@RequestParam(value = "id", required = false) Integer id,
            @RequestParam(value = "name", required = false) String fname,
            @RequestParam(value = "shortName", required = false) String fshortname,
            @RequestParam(value = "type", required = false) Integer ftype,
            @RequestParam(value = "coinType", required = false) Integer fcointype,
            @RequestParam(value = "platformId", required = false) Integer platformId,
            @RequestParam(value = "isWithdraw", required = false) String fiswithdraw,
            @RequestParam(value = "isRecharge", required = false) String fisrecharge,
            @RequestParam(value = "riskNum", required = false) String frisknum,
            @RequestParam(value = "rechargeMinLimit", required = false) String rechargeMinLimit,
            @RequestParam(value = "isPush", required = false) String fispush,
            @RequestParam(value = "isUseMemo", required = false) String isUseMemo,
            @RequestParam(value = "isOpenOtc", required = false) String isOpenOtc,
            @RequestParam(value = "isPlatformTransferFee", required = false) String isPlatformTransferFee,
            @RequestParam(value = "isInnovateAreaCoin", required = false) String isInnovateAreaCoin,
            @RequestParam(value = "isFinances", required = false) String fisfinances,
            @RequestParam(value = "assetId", required = false) String fassetid,
            @RequestParam(value = "symbol", required = false) String fsymbol,
            @RequestParam(value = "accessKey", required = false) String faccesskey,
            @RequestParam(value = "secrtKey", required = false) String fsecrtkey,
            @RequestParam(value = "ethAccount", required = false) String ethAccount,
            @RequestParam(value = "contractAccount", required = false) String contractAccount,
            @RequestParam(value = "contractWei", required = false) Integer contractWei,
            @RequestParam(value = "walletLink", required = false) String walletLink,
            @RequestParam(value = "chainLink", required = false) String chainLink,
            @RequestParam(value = "networkFee", required = false) String fnetworkfee,
            @RequestParam(value = "webLogo", required = false) String fweblogo,
            @RequestParam(value = "appLogo", required = false) String fapplogo,
            @RequestParam(value = "confirmations", required = false) Integer fconfirmations,
            @RequestParam(value = "explorerUrl", required = false) String explorerUrl,
            @RequestParam(value = "addressUrl", required = false) String addressUrl,
            @RequestParam(value = "sortId", required = false) Integer fsortid,
            @RequestParam(value = "walletAccount", required = false) String walletAccount,
            @RequestParam(value = "releaseLockingRatio", required = false) String releaseLockingRatio,
            @RequestParam(value = "dayReleaseRatio", required = false) String dayReleaseRatio,
            @RequestParam(value = "isSubCoin", required = false) String isSubCoin,
            @RequestParam(value = "linkCoin", required = false) String linkCoin,
            @RequestParam(value = "useNewWay", required = false) String useNewWay,
            @RequestParam(value = "walletUrl", required = false) String walletUrl
    		) {
    	try {
    		SystemCoinType coinType = new SystemCoinType();
    		coinType.setId(id);
            coinType.setName(fname);
            coinType.setShortName(fshortname);
            coinType.setType(ftype);
            coinType.setStatus(SystemCoinStatusEnum.ABNORMAL.getCode());
            coinType.setCoinType(fcointype);
            if (StringUtils.isNotBlank(fiswithdraw)) {
                coinType.setIsWithdraw(true);
            } else {
                coinType.setIsWithdraw(false);
            }
            if (StringUtils.isNotBlank(fisrecharge)) {
                coinType.setIsRecharge(true);
            } else {
                coinType.setIsRecharge(false);
            }
            if (StringUtils.isNotBlank(fispush)) {
                coinType.setIsPush(true);
            } else {
                coinType.setIsPush(false);
            }
            if (StringUtils.isNotBlank(isUseMemo)) {
                coinType.setIsUseMemo(true);
            } else {
                coinType.setIsUseMemo(false);
            }
            if (StringUtils.isNotBlank(isOpenOtc)) {
                coinType.setOpenOtc(true);
            } else {
                coinType.setOpenOtc(false);
            }
            if (StringUtils.isNotBlank(isPlatformTransferFee)) {
                coinType.setIsPlatformTransferFee(true);
            } else {
            	coinType.setIsPlatformTransferFee(false);
            }
            if (StringUtils.isNotBlank(fisfinances)) {
                coinType.setIsFinances(true);
            } else {
                coinType.setIsFinances(false);
            }
            if (StringUtils.isNotBlank(isSubCoin)) {
                coinType.setIsSubCoin(true);
            } else {
                coinType.setIsSubCoin(false);
            }
            if (StringUtils.isNotBlank(useNewWay)) {
                coinType.setUseNewWay(true);
            } else {
                coinType.setUseNewWay(false);
            }
            coinType.setRiskNum(StringUtils.isEmpty(frisknum) ? BigDecimal.ZERO : new BigDecimal(frisknum));
            coinType.setRechargeMinLimit(StringUtils.isEmpty(rechargeMinLimit) ? BigDecimal.ZERO : new BigDecimal(rechargeMinLimit));
            if (!StringUtils.isEmpty(fassetid)) {
                coinType.setAssetId(new BigInteger(fassetid));
            }
            coinType.setPlatformId(platformId);
            coinType.setSymbol(fsymbol);
            coinType.setAccessKey(faccesskey);
            coinType.setSecrtKey(fsecrtkey);
            coinType.setEthAccount(ethAccount);
            coinType.setContractAccount(contractAccount);
            coinType.setContractWei(contractWei);
            coinType.setWalletLink(walletLink);
            coinType.setChainLink(chainLink);
            coinType.setWalletAccount(walletAccount);
            coinType.setNetworkFee(StringUtils.isEmpty(fnetworkfee) ? BigDecimal.ZERO : new BigDecimal(fnetworkfee));
            coinType.setConfirmations(fconfirmations);
            coinType.setSortId(fsortid);
            coinType.setGmtCreate(new Date());
            coinType.setGmtModified(new Date());
            coinType.setVersion(0);
            coinType.setExplorerUrl(explorerUrl);
            coinType.setAddressUrl(addressUrl);
            coinType.setLinkCoin(linkCoin);
            coinType.setWalletUrl(walletUrl);
            //创新区
            if (StringUtils.isNotBlank(isInnovateAreaCoin)) {
                coinType.setIsInnovateAreaCoin(true);
            } else {
                coinType.setIsInnovateAreaCoin(false);
            }
            if(!StringUtils.isEmpty(releaseLockingRatio)) {
            	BigDecimal bigDecimal = new BigDecimal(releaseLockingRatio);
            	if(new BigDecimal(1).compareTo(bigDecimal) < 0) {
            		 return ReturnResult.FAILUER("冻结释放比例不能大于1");
            	}
            	coinType.setReleaseLockingRatio(bigDecimal);
            }else {
            	coinType.setReleaseLockingRatio(BigDecimal.ZERO);
            }
            
            if(!StringUtils.isEmpty(dayReleaseRatio)) {
            	BigDecimal bigDecimal = new BigDecimal(dayReleaseRatio);
            	if(new BigDecimal(1).compareTo(bigDecimal) < 0) {
            		 return ReturnResult.FAILUER("单日释放上限比例不能大于1");
            	}
            	coinType.setDayReleaseRatio(bigDecimal);
            }else {
            	coinType.setDayReleaseRatio(BigDecimal.ZERO);
            }
            // 获取币的logo
            if (fweblogo != null && !fweblogo.isEmpty()) {
                coinType.setWebLogo(fweblogo);
            }
            // 获取币的logo
            if (fapplogo != null && !fapplogo.isEmpty()) {
                coinType.setAppLogo(fapplogo);
            }
            if (this.adminSystemCoinTypeService.insert(coinType)) {
                String ip = HttpRequestUtils.getIPAddress();
                FAdmin sessionAdmin = (FAdmin) sessionContextUtils.getContextRequest().getSession()
                        .getAttribute("login_admin");
                mqSend.SendAdminAction(sessionAdmin.getFagentid(), sessionAdmin.getFid(), LogAdminActionEnum.COIN_ADD, ip);
            }
            return ReturnResult.SUCCESS("新增成功！");
		} catch (Exception e) {
			logger.error("新增币种异常",e);
			return ReturnResult.FAILUER("系统异常！");
		}
        
    }

    /**
     * 修改的币种信息
     */
    @RequestMapping("admin/updateCoinType")
    @ResponseBody
    public ReturnResult updateCoinType(
            @RequestParam(value = "id", required = false) Integer fid,
            @RequestParam(value = "name", required = false) String fname,
            @RequestParam(value = "shortName", required = false) String fshortname,
            @RequestParam(value = "type", required = false) Integer ftype,
            @RequestParam(value = "coinType", required = false) Integer fcointype,
            @RequestParam(value = "platformId", required = false) Integer platformId,
            @RequestParam(value = "isWithdraw", required = false) String fiswithdraw,
            @RequestParam(value = "isRecharge", required = false) String fisrecharge,
            @RequestParam(value = "riskNum", required = false) String frisknum,
            @RequestParam(value = "rechargeMinLimit", required = false) String rechargeMinLimit,
            @RequestParam(value = "isPush", required = false) String fispush,
            @RequestParam(value = "isUseMemo", required = false) String isUseMemo,
            @RequestParam(value = "isOpenOtc", required = false) String isOpenOtc,
            @RequestParam(value = "isPlatformTransferFee", required = false) String isPlatformTransferFee,
            @RequestParam(value = "isInnovateAreaCoin", required = false) String isInnovateAreaCoin,
            @RequestParam(value = "isFinances", required = false) String fisfinances,
            @RequestParam(value = "assetId", required = false) String fassetid,
            @RequestParam(value = "symbol", required = false) String fsymbol,
            @RequestParam(value = "accessKey", required = false) String faccesskey,
            @RequestParam(value = "secrtKey", required = false) String fsecrtkey,
            @RequestParam(value = "ethAccount", required = false) String ethAccount,
            @RequestParam(value = "contractAccount", required = false) String contractAccount,
            @RequestParam(value = "contractWei", required = false) Integer contractWei,
            @RequestParam(value = "walletLink", required = false) String walletLink,
            @RequestParam(value = "chainLink", required = false) String chainLink,
            @RequestParam(value = "networkFee", required = false) String fnetworkfee,
            @RequestParam(value = "webLogo", required = false) String fweblogo,
            @RequestParam(value = "appLogo", required = false) String fapplogo,
            @RequestParam(value = "confirmations", required = false) Integer fconfirmations,
            @RequestParam(value = "explorerUrl", required = false) String explorerUrl,
            @RequestParam(value = "addressUrl", required = false) String addressUrl,
            @RequestParam(value = "sortId", required = false) Integer fsortid,
            @RequestParam(value = "walletAccount", required = false) String walletAccount,
            @RequestParam(value = "releaseLockingRatio", required = false) String releaseLockingRatio,
            @RequestParam(value = "dayReleaseRatio", required = false) String dayReleaseRatio,
            @RequestParam(value = "isSubCoin", required = false) String isSubCoin,
            @RequestParam(value = "linkCoin", required = false) String linkCoin,
            @RequestParam(value = "useNewWay", required = false) String useNewWay,
            @RequestParam(value = "walletUrl", required = false) String walletUrl
            ) throws Exception {
        SystemCoinType coinType = adminSystemCoinTypeService.selectVirtualCoinById(fid);
        if (coinType == null) {
            return ReturnResult.FAILUER("币种不存在,请刷新重试！");
        }
        coinType.setName(fname);
        coinType.setShortName(fshortname);
        coinType.setType(ftype);
        coinType.setCoinType(fcointype);
        if (fiswithdraw != null && !fiswithdraw.isEmpty()) {
            coinType.setIsWithdraw(true);
        } else {
            coinType.setIsWithdraw(false);
        }
        if (fisrecharge != null && !fisrecharge.isEmpty()) {
            coinType.setIsRecharge(true);
        } else {
            coinType.setIsRecharge(false);
        }
        if (fispush != null && !fispush.isEmpty()) {
            coinType.setIsPush(true);
        } else {
            coinType.setIsPush(false);
        }
        if (isUseMemo != null && !isUseMemo.isEmpty()) {
            coinType.setIsUseMemo(true);
        } else {
            coinType.setIsUseMemo(false);
        }
        if (isOpenOtc != null && !isOpenOtc.isEmpty()) {
            coinType.setOpenOtc(true);
        } else {
            coinType.setOpenOtc(false);
        }
        if (isPlatformTransferFee != null && !isPlatformTransferFee.isEmpty()) {
            coinType.setIsPlatformTransferFee(true);
        } else {
        	coinType.setIsPlatformTransferFee(false);
        }
        if (fisfinances != null && !fisfinances.isEmpty()) {
            coinType.setIsFinances(true);
        } else {
            coinType.setIsFinances(false);
        }
        if (isSubCoin != null && !isSubCoin.isEmpty()) {
            coinType.setIsSubCoin(true);
        } else {
            coinType.setIsSubCoin(false);
        }
        if (StringUtils.isNotBlank(useNewWay)) {
            coinType.setUseNewWay(true);
        } else {
            coinType.setUseNewWay(false);
        }
        coinType.setRiskNum(StringUtils.isEmpty(frisknum) ? BigDecimal.ZERO : new BigDecimal(frisknum));
        coinType.setRechargeMinLimit(StringUtils.isEmpty(rechargeMinLimit) ? BigDecimal.ZERO : new BigDecimal(rechargeMinLimit));
        if (!StringUtils.isEmpty(fassetid)) {
            coinType.setAssetId(new BigInteger(fassetid));
        }
        coinType.setPlatformId(platformId);
        coinType.setSymbol(fsymbol);
        coinType.setAccessKey(faccesskey);
        coinType.setSecrtKey(fsecrtkey);
        coinType.setEthAccount(ethAccount);
        coinType.setContractWei(contractWei);
        coinType.setWalletAccount(walletAccount);
        coinType.setWalletLink(walletLink);
        coinType.setChainLink(chainLink);
        coinType.setNetworkFee(StringUtils.isEmpty(fnetworkfee) ? BigDecimal.ZERO : new BigDecimal(fnetworkfee));
        coinType.setConfirmations(fconfirmations);
        coinType.setSortId(fsortid);
        coinType.setGmtModified(new Date());
        coinType.setContractAccount(contractAccount);
        coinType.setExplorerUrl(explorerUrl);
        coinType.setAddressUrl(addressUrl);
        coinType.setLinkCoin(linkCoin);
        coinType.setWalletUrl(walletUrl);
        //创新区
        if (isInnovateAreaCoin != null && !isInnovateAreaCoin.isEmpty()) {
            coinType.setIsInnovateAreaCoin(true);
            Map<String,Object> param = new HashMap<>();
            param.put("status", SystemTradeStatusEnum.NORMAL.getCode());
            param.put("sellCoinId", fid);
            param.put("notInType", SystemTradeTypeNewEnum.INNOVATION_AREA.getCode());
            Integer selectSystemTradeTypeCount = adminSystemTradeTypeService.selectSystemTradeTypeCount(param);
            if(selectSystemTradeTypeCount == null || selectSystemTradeTypeCount > 0) {
            	return ReturnResult.FAILUER("请先在非创新区下线该币种交易对");
            }
        } else {
            coinType.setIsInnovateAreaCoin(false);
            Map<String,Object> param = new HashMap<>();
            param.put("status", SystemTradeStatusEnum.NORMAL.getCode());
            param.put("sellCoinId", fid);
            param.put("type", SystemTradeTypeNewEnum.INNOVATION_AREA.getCode());
            Integer selectSystemTradeTypeCount = adminSystemTradeTypeService.selectSystemTradeTypeCount(param);
            if(selectSystemTradeTypeCount == null || selectSystemTradeTypeCount > 0) {
            	return ReturnResult.FAILUER("请先在创新区下线对应交易对再修改该币属性");
            }
        }
        if(!StringUtils.isEmpty(releaseLockingRatio)) {
        	BigDecimal bigDecimal = new BigDecimal(releaseLockingRatio);
        	if(new BigDecimal(1).compareTo(bigDecimal) < 0) {
        		 return ReturnResult.FAILUER("冻结释放比例不能大于1");
        	}
        	coinType.setReleaseLockingRatio(bigDecimal);
        }else {
        	coinType.setReleaseLockingRatio(BigDecimal.ZERO);
        }
        
        if(!StringUtils.isEmpty(dayReleaseRatio)) {
        	BigDecimal bigDecimal = new BigDecimal(dayReleaseRatio);
        	if(new BigDecimal(1).compareTo(bigDecimal) < 0) {
        		 return ReturnResult.FAILUER("单日释放上限比例不能大于1");
        	}
        	coinType.setDayReleaseRatio(bigDecimal);
        }else {
        	coinType.setDayReleaseRatio(BigDecimal.ZERO);
        }
        
        // 获取币的logo
        if (fweblogo != null && !fweblogo.isEmpty()) {
            coinType.setWebLogo(fweblogo);
        }
        // 获取币的logo
        if (fapplogo != null && !fapplogo.isEmpty()) {
            coinType.setAppLogo(fapplogo);
        }
        if (this.adminSystemCoinTypeService.updateVirtualCoin(coinType)) {
            String ip = HttpRequestUtils.getIPAddress();
            FAdmin sessionAdmin = (FAdmin) sessionContextUtils.getContextRequest().getSession()
                    .getAttribute("login_admin");
            mqSend.SendAdminAction(sessionAdmin.getFagentid(), sessionAdmin.getFid(), LogAdminActionEnum.COIN_ADD, ip);
        }
        return ReturnResult.SUCCESS("修改成功");
    }

    /**
     * 虚拟币状态操作
     */
    @RequestMapping("admin/updateCoinStatus")
    @ResponseBody
    public ReturnResult deleteVirtualCoinType(
            @RequestParam(required = false) int coinId,
            @RequestParam(required = false) int status) throws Exception {
        SystemCoinType coinType = this.adminSystemCoinTypeService.selectVirtualCoinById(coinId);
        if (coinType == null) {
            return ReturnResult.FAILUER("币种不存在，请刷新重试！");
        }
        String msg = "";
        if (coinType.getStatus().equals(SystemCoinStatusEnum.NORMAL.getCode())) {
            if (status == SystemCoinStatusEnum.NORMAL.getCode()) {
                return ReturnResult.FAILUER("请勿重复操作！");
            }
            coinType.setStatus(SystemCoinStatusEnum.ABNORMAL.getCode());
            msg = "禁用成功";
        } else if (coinType.getStatus().equals(SystemCoinStatusEnum.ABNORMAL.getCode())) {
            if (status == SystemCoinStatusEnum.ABNORMAL.getCode()) {
                return ReturnResult.FAILUER("请勿重复操作！");
            }
            coinType.setStatus(SystemCoinStatusEnum.NORMAL.getCode());
            msg = "启用成功";
        }
        try {
            adminSystemCoinTypeService.updateVirtualCoinByEnabled(coinType);
            return ReturnResult.SUCCESS(msg);
        } catch (Exception e) {
            e.printStackTrace();
            return ReturnResult.FAILUER("操作失败！");
        }

    }

    /**
     * 钱包的测试
     */
    @RequestMapping("admin/testWallet")
    @ResponseBody
    public ReturnResult testWallet(
            @RequestParam(required = false) int fid) throws Exception {
        SystemCoinType coinType = this.adminSystemCoinTypeService.selectVirtualCoinById(fid);
        String accesskey = coinType.getAccessKey();
        String secretkey = coinType.getSecrtKey();
        String walletLink = coinType.getWalletLink();
        String chainLink = coinType.getChainLink();
        if (accesskey == null || secretkey == null || walletLink == null ) {
            return ReturnResult.FAILUER("钱包数据缺少，请检查配置信息！");
        }

        
        
        try {
        	BigDecimal balance = null;
        	if(coinType.getUseNewWay()) {
        		balance = CoinRpcUtlis.getBalance(coinType);
        	}else {
        		CoinDriver coinDriver = new CoinDriverFactory.Builder(coinType.getCoinType(), walletLink, chainLink)
                        .accessKey(accesskey)
                        .secretKey(secretkey)
                        .assetId(coinType.getAssetId())
                        .sendAccount(coinType.getEthAccount())
                        .contractAccount(coinType.getContractAccount())
                        .shortName(coinType.getShortName())
                        .contractWei(coinType.getContractWei())
                        .builder()
                        .getDriver();
                balance = coinDriver.getBalance();
        	}
            if (balance == null) {
                return ReturnResult.FAILUER("钱包连接失败，请检查配置信息！");
            }
            return ReturnResult.SUCCESS("测试成功，钱包余额:" + balance);
        } catch (Exception e) {
        	logger.error("查询余额异常",e);
        	return ReturnResult.FAILUER("钱包连接失败," + e.getMessage());
        }
        
    }

    /**
     * 修改币的手续费
     */
    @RequestMapping("admin/updateCoinFee")
    @ResponseBody
    public ReturnResult updateCoinFee(@RequestParam(required = false) int fid) throws Exception {
        HttpServletRequest request = sessionContextUtils.getContextRequest();

        try {
            List<SystemCoinSetting> coinSettingList = adminSystemCoinTypeService.selectSystemCoinSettingList(fid);
            for (SystemCoinSetting coinSetting : coinSettingList) {
                String withdrawMaxKey = "withdrawMax" + coinSetting.getId();
                String withdrawMinKey = "withdrawMin" + coinSetting.getId();
                String withdrawFeeKey = "withdrawFee" + coinSetting.getId();
                String withdrawTimesKey = "withdrawTimes" + coinSetting.getId();
                String withdrawDayLimitKey = "withdrawDayLimit" + coinSetting.getId();
                String isPercentageKey = "isPercentage" + coinSetting.getId();
                coinSetting.setWithdrawMax(new BigDecimal(request.getParameter(withdrawMaxKey)));
                coinSetting.setWithdrawMin(new BigDecimal(request.getParameter(withdrawMinKey)));
                coinSetting.setWithdrawFee(new BigDecimal(request.getParameter(withdrawFeeKey)));
                coinSetting.setWithdrawTimes(new Integer(request.getParameter(withdrawTimesKey)));
                coinSetting.setWithdrawDayLimit(new BigDecimal(request.getParameter(withdrawDayLimitKey)));
                coinSetting.setIsPercentage(!StringUtils.isEmpty(request.getParameter(isPercentageKey)));
                coinSetting.setGmtModified(new Date());
            }
            this.adminSystemCoinTypeService.updateSystemCoinSetting(coinSettingList);
            String ip = HttpRequestUtils.getIPAddress();
            FAdmin sessionAdmin = (FAdmin) sessionContextUtils.getContextRequest().getSession().getAttribute("login_admin");
            mqSend.SendAdminAction(sessionAdmin.getFagentid(), sessionAdmin.getFid(), LogAdminActionEnum.COIN_FREES, ip);
            return ReturnResult.SUCCESS("更新成功！");
        } catch (Exception e) {
        	logger.error("修改币种设置异常",e);
        	return ReturnResult.FAILUER("更新失败！"+e.getMessage());
        }
    }

    /**
     * 创建虚拟币钱包地址
     */
    @RequestMapping("admin/createWalletAddress")
    @ResponseBody
    public ReturnResult createWalletAddress(
            @RequestParam(required = false) int fid, @RequestParam(required = false, defaultValue = "0") int count,
            @RequestParam(required = false, defaultValue = "0") String passWord) throws Exception {
        if (count < 0 || count > 100000) {
            return ReturnResult.FAILUER("生成钱包地址数量错误！");
        }
        SystemCoinType coinType = adminSystemCoinTypeService.selectVirtualCoinById(fid);
//		if (!coinType.getIsWithdraw() || !coinType.getIsRecharge()) {
//			return ReturnResult.FAILUER("不允许充值和提现的虚拟币类型不能生成虚拟地址！");
//		}

        if (coinType.getCoinType().equals(SystemCoinSortEnum.ETC.getCode()) && !"JRRjus1uThrNZoEUZOV7".equals(passWord)){
            return ReturnResult.FAILUER("生成钱包地址密码错误！");
        }

        if (coinType.getCoinType().equals(SystemCoinSortEnum.ETH.getCode()) && !"efQdJBjYhFfbrIilHA8u".equals(passWord)){
            return ReturnResult.FAILUER("生成钱包地址密码错误");
        }
        if (coinType.getCoinType().equals(SystemCoinSortEnum.MOAC.getCode()) && !"efQdJBjYhFfbrIilHA8u".equals(passWord)){
            return ReturnResult.FAILUER("生成钱包地址密码错误");
        }
        if (coinType.getCoinType().equals(SystemCoinSortEnum.FOD.getCode()) && !"efQdJBjYhFfbrIilHA8u".equals(passWord)){
            return ReturnResult.FAILUER("生成钱包地址密码错误");
        }
        if (coinType.getCoinType().equals(SystemCoinSortEnum.VCC.getCode()) && !"Q4XpE0oiXIax5afR".equals(passWord)){
            return ReturnResult.FAILUER("生成钱包地址密码错误");
        }

        adminSystemCoinTypeService.createVirtualCoinAddress(coinType, count, passWord);
        String ip0 = HttpRequestUtils.getIPAddress();
        FAdmin sessionAdmin0 = (FAdmin) sessionContextUtils.getContextRequest().getSession()
                .getAttribute("login_admin");
        mqSend.SendAdminAction(sessionAdmin0.getFagentid(), sessionAdmin0.getFid(), LogAdminActionEnum.COIN_ADDRESS,
                ip0);
        return ReturnResult.SUCCESS("地址异步生成中，请点击【虚拟币可用地址列表】查看生成详情！");
    }


    @RequestMapping("admin/walletAddressList")
    public ModelAndView walletAddressList(
            @RequestParam(required = false, defaultValue = "1") Integer currentPage,
            @RequestParam(required = false) String keywords
    ) throws Exception {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("coin/walletAddressList");

        Pagination<Map<String, Object>> page = new Pagination<Map<String, Object>>(currentPage, Constant.adminPageSize);
        if (keywords != null && keywords.trim().length() > 0) {
            page.setKeyword(keywords);
            modelAndView.addObject("keywords", keywords);
        }

        page = adminSystemCoinTypeService.selectVirtualCoinAddressNumList(page);

        modelAndView.addObject("walletAddressList", page);
        return modelAndView;
    }

    /**
     * 存币理财列表
     */
    @RequestMapping("admin/virtualFinancesList")
    public ModelAndView virtualFinancesList(
            @RequestParam(value = "symbol", defaultValue = "0") Integer symbol) throws Exception {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("activity/virtualFinancesList");
        List<FVirtualFinances> financesList = adminSystemCoinTypeService.selectVirtualFinancesList(null, null);
        modelAndView.addObject("financesList", financesList);
        modelAndView.addObject("symbol", symbol);
        // 页面参数
        Map<Integer, String> coinMap = redisHelper.getCoinTypeNameMap();
        modelAndView.addObject("coinMap", coinMap);
        return modelAndView;
    }

    /**
     * 存币理财列表
     */
    @RequestMapping("admin/goVirtualFinancesJsp")
    public ModelAndView goVirtualFinancesJsp(@RequestParam(value = "fid", defaultValue = "0") Integer fid,
                                             @RequestParam(value = "url", defaultValue = "") String url,
                                             @RequestParam(value = "symbol", defaultValue = "0") Integer symbol) throws Exception {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName(url);
        if (fid != 0) {
            FVirtualFinances virtualFinances = adminSystemCoinTypeService.selectVirtualFinances(fid);
            modelAndView.addObject("finances", virtualFinances);
        }
        // 页面参数
        Map<Integer, String> coinMap = redisHelper.getCoinTypeNameMap();
        modelAndView.addObject("coinMap", coinMap);
        modelAndView.addObject("symbol", symbol);
        return modelAndView;
    }

    /**
     * 添加存币理财
     */
    @RequestMapping("admin/addVirtualFinances")
    @ResponseBody
    public ReturnResult addVirtualFinancesList(
            @RequestParam(value = "fcoinid", defaultValue = "0") Integer fcoinid,
            @RequestParam(value = "fname", defaultValue = "") String fname,
            @RequestParam(value = "fdays", defaultValue = "0") Integer fdays,
            @RequestParam(value = "frate", defaultValue = "0") BigDecimal frate) throws Exception {
        FVirtualFinances virtualFinances = new FVirtualFinances();
        virtualFinances.setFcoinid(fcoinid);
        virtualFinances.setFcreatetime(new Date());
        virtualFinances.setFdays(fdays);
        virtualFinances.setFname(fname);
        virtualFinances.setFrate(frate);
        virtualFinances.setFstate(1);
        virtualFinances.setVersion(0);
        boolean result = adminSystemCoinTypeService.insertVirtualFinances(virtualFinances);
        if (result) {
            return ReturnResult.SUCCESS("新增成功");
        } else {
            return ReturnResult.FAILUER("新增失败");
        }
    }

    @RequestMapping("admin/updateVirtualFinances")
    @ResponseBody
    public ReturnResult updateVirtualFinancesList(
            @RequestParam(value = "fid", defaultValue = "0") Integer fid,
            @RequestParam(value = "fname", defaultValue = "0") String fname,
            @RequestParam(value = "fdays", defaultValue = "0") Integer fdays,
            @RequestParam(value = "frate", defaultValue = "0") BigDecimal frate) throws Exception {
        FVirtualFinances virtualFinances = adminSystemCoinTypeService.selectVirtualFinances(fid);
        if (virtualFinances != null) {
            virtualFinances.setFdays(fdays);
            virtualFinances.setFname(fname);
            virtualFinances.setFrate(frate);
            virtualFinances.setFupdatetime(new Date());
        } else {
            return ReturnResult.FAILUER("修改失败,数据不存在!");
        }
        boolean result = adminSystemCoinTypeService.updateVirtualFinances(virtualFinances);
        if (result) {
            return ReturnResult.SUCCESS("修改成功");
        } else {
            return ReturnResult.FAILUER("修改失败");
        }
    }

    @RequestMapping("admin/deleteVirtualFinances")
    @ResponseBody
    public ReturnResult deleteVirtualFinancesList(
            @RequestParam(value = "fid", defaultValue = "0") Integer fid) throws Exception {
        FVirtualFinances virtualFinances = adminSystemCoinTypeService.selectVirtualFinances(fid);
        if (virtualFinances != null) {
            boolean result = adminSystemCoinTypeService.deleteVirtualFinances(fid);
            if (result) {
                return ReturnResult.SUCCESS("删除成功");
            } else {
                return ReturnResult.FAILUER("删除失败");
            }
        } else {
            return ReturnResult.FAILUER("删除失败,数据不存在");
        }
    }

    @RequestMapping("admin/stateVirtualFinances")
    @ResponseBody
    public ReturnResult stateVirtualFinancesList(@RequestParam(value = "fid", defaultValue = "0") Integer fid,
                                                 @RequestParam(value = "fstate", defaultValue = "0") Integer fstate) throws Exception {
        FVirtualFinances virtualFinances = adminSystemCoinTypeService.selectVirtualFinances(fid);
        if (virtualFinances != null) {
            virtualFinances.setFstate(fstate);
            virtualFinances.setFupdatetime(new Date());
            boolean result = adminSystemCoinTypeService.updateVirtualFinances(virtualFinances);
            if (result) {
                if (fstate == 2) {
                    return ReturnResult.SUCCESS("禁用成功");
                } else {
                    return ReturnResult.SUCCESS("启用成功");
                }
            } else {
                if (fstate == 2) {
                    return ReturnResult.FAILUER("禁用失败");
                } else {
                    return ReturnResult.FAILUER("启用失败");
                }
            }
        } else {
            return ReturnResult.FAILUER("操作失败,数据不存在");
        }
    }

    /**
     * 撤销存币理财
     */
    @RequestMapping("/admin/userFinancesRedeem")
    @ResponseBody
    public ReturnResult userFinancesRedeem(
            @RequestParam(value = "fid", defaultValue = "0") Integer fid) throws Exception {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("admin/comm/ajaxDone");

        FUserFinancesDTO userFinances = adminUserFinances.selectUserFinances(fid);
        if (userFinances == null) {
            return ReturnResult.FAILUER("记录不存在");
        }
        if (!userFinances.getFstate().equals(UserFinancesStateEnum.FROZEN.getCode())) {
            return ReturnResult.FAILUER("赎回失败，所选记录状态为" + userFinances.getFstate_s());
        }
        try {
            if (adminUserFinances.updateUserFinances(fid)) {
                return ReturnResult.SUCCESS("赎回成功");
            } else {
                return ReturnResult.FAILUER("赎回失败");
            }
        } catch (Exception ex) {
            return ReturnResult.FAILUER(ex.getMessage());
        }
    }
    /**
     * 撤销存币理财
     */
    @RequestMapping("/admin/userFinancesRedeemBatch")
    @ResponseBody
    public ReturnResult userFinancesRedeemBatch(
            @RequestParam(value = "ids", required = true) String ids) throws Exception {
        String[] idString = ids.split(",");
        Integer errCount = 0;
        for (String id : idString) {
            FUserFinancesDTO userFinances = adminUserFinances.selectUserFinances(Integer.valueOf(id));
            if (userFinances == null) {
                errCount++;
                continue;
            }
            if (!userFinances.getFstate().equals(UserFinancesStateEnum.FROZEN.getCode())) {
                errCount++;
                continue;
            }
            try {
                if (!adminUserFinances.updateUserFinances(Integer.valueOf(id))) {
                    errCount++;
                }
            } catch (Exception ex) {
                errCount++;
            }
        }
        return ReturnResult.SUCCESS((errCount > 0 ? ("部分赎回成功，失败" + errCount + "条") : "批量赎回成功"));
    }

    
    /**
     * 虚拟币状态操作
     */
    //@RequestMapping("admin/coinSwitch")
    @ResponseBody
    public ReturnResult coinSwitch(
            @RequestParam(required = false) int oldCoinId,
            @RequestParam(required = false) int newCoinId) throws Exception {
        SystemCoinType oldcoinType = this.adminSystemCoinTypeService.selectVirtualCoinById(oldCoinId);
        SystemCoinType newcoinType = this.adminSystemCoinTypeService.selectVirtualCoinById(newCoinId);
        if (oldcoinType == null || newcoinType == null) {
            return ReturnResult.FAILUER("币种不存在，请刷新重试！");
        }
        if (oldcoinType.getStatus().equals(SystemCoinStatusEnum.NORMAL.getCode())) {
        	return ReturnResult.FAILUER("请先禁用！"+ oldcoinType.getName());
        } 
        if (newcoinType.getStatus().equals(SystemCoinStatusEnum.NORMAL.getCode())) {
        	return ReturnResult.FAILUER("请先禁用！"+ newcoinType.getName());
        }
        try {
            String coinSwitch = adminSystemCoinTypeService.coinSwitch(oldCoinId, newCoinId);
            if(StringUtils.isEmpty(coinSwitch)) {
            	return ReturnResult.SUCCESS("执行成功！");
            }else {
            	return ReturnResult.FAILUER(coinSwitch);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ReturnResult.FAILUER("操作失败！");
        }
    }
    
    /**
     * 创新区下架后解冻钱包
     */
    @RequestMapping("/admin/unfreezeInnovateArea")
    @ResponseBody
    public ReturnResult unfreezeInnovateArea(
            @RequestParam(required = false) int coinId) throws Exception {
        SystemCoinType coinType = this.adminSystemCoinTypeService.selectVirtualCoinById(coinId);
        if (coinType == null) {
            return ReturnResult.FAILUER("币种不存在，请刷新重试！");
        }
        if (coinType.getStatus().equals(SystemCoinStatusEnum.ABNORMAL.getCode())) {
        	return ReturnResult.FAILUER("币种被禁用，请刷新重试！");
		}
        if (coinType.getIsInnovateAreaCoin()) {
        	return ReturnResult.FAILUER("币种未下架创新区！");
		}
        //查询委单
        int count = adminEntrustServer.selectFEntrustCount(coinId);
        if (count > 0) {
        	return ReturnResult.FAILUER("币种还存在委单，请先撤销！");
		}
        try {
            boolean flag = adminSystemCoinTypeService.unfreezeInnovateArea(coinId);
            if (flag) {
            	return ReturnResult.SUCCESS("操作成功！");
			}
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ReturnResult.FAILUER("操作失败！");
    }
    
}
