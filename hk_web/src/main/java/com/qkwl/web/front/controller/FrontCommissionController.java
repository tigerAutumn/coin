package com.qkwl.web.front.controller;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.qkwl.common.dto.activity.ActivityConfig;
import com.qkwl.common.dto.activity.RankActivityConfig;
import com.qkwl.common.dto.commission.Commission;
import com.qkwl.common.dto.commission.CommissionRankList;
import com.qkwl.common.dto.commission.Invitee;
import com.qkwl.common.dto.common.Pagination;
import com.qkwl.common.dto.statistic.EBankRank;
import com.qkwl.common.dto.user.FUser;
import com.qkwl.common.framework.redis.RedisHelper;
import com.qkwl.common.redis.RedisConstant;
import com.qkwl.common.rpc.commission.ICommissionService;
import com.qkwl.common.rpc.user.IUserService;
import com.qkwl.common.util.AccountUtil;
import com.qkwl.common.util.Constant;
import com.qkwl.common.util.I18NUtils;
import com.qkwl.common.util.ReturnResult;
import com.qkwl.web.front.controller.base.JsonBaseController;
import com.qkwl.web.permission.annotation.PassToken;

import io.swagger.annotations.ApiOperation;
import springfox.documentation.annotations.ApiIgnore;

@Controller
@ApiIgnore
public class FrontCommissionController extends JsonBaseController {

	private static final Logger logger = LoggerFactory.getLogger(FrontCommissionController.class);
	
	@Autowired
	private ICommissionService commissionService;
	@Autowired
	private IUserService userService;
	@Autowired
    private RedisHelper redisHelper;
	
	/**
	 * 分页大小
	 */	
 	private int numPerPage = 10;
	
	/**
	 * 首页返佣排行榜
	 */
	@ApiOperation("")
	@RequestMapping(value="/commission/indexRankList",method = {RequestMethod.GET,RequestMethod.POST})
	@ResponseBody
    public ReturnResult getIndexRankList(){
		//查询活动是否进行中
		ActivityConfig activity = commissionService.selectActivityById(Constant.COMMISSION_ACTIVITY);
		JSONObject jsonObject = new JSONObject();
		List<CommissionRankList> rankList = new ArrayList<>();
		if (activity == null) {
			logger.info("======================返佣活动不存在======================"); 
			jsonObject.put("isStart", "N");  //$NON-NLS-2$
		} else {
			Date startTime = activity.getStartTime();
			Date endTime = activity.getEndTime();
			Date today = new Date();
			if (today.after(endTime) || today.before(startTime)) {
				logger.info("======================返佣活动不在进行中======================"); 
				jsonObject.put("isStart", "N");  //$NON-NLS-2$
			} else {
				//从redis中获取排行榜
				String rankListStr = redisHelper.get(RedisConstant.COMMISSION + "indexRankList"); 
				if (rankListStr != null) {
					JSONObject obj = JSON.parseObject(rankListStr);
					rankList = JSONArray.parseArray(obj.getJSONArray("extObject").toJSONString(), 
							CommissionRankList.class);
				}
				jsonObject.put("isStart", "Y");  //$NON-NLS-2$
			}
		}
		if (rankList.size() == 0) {
			for (int i = 0; i < 3; i++) {
				CommissionRankList commission = new CommissionRankList();
				commission.setOrderNumber(i+1);
				commission.setInviterLoginname("--"); 
				commission.setSumCommission(BigDecimal.ZERO);
				rankList.add(commission);
			}
		}
		jsonObject.put("list", rankList); 
		return ReturnResult.SUCCESS(jsonObject);
    }
	
	/**
	 * 详情页返佣排行榜
	 */
	@ApiOperation("")
	@RequestMapping(value="/commission/detailRankList",method = {RequestMethod.GET,RequestMethod.POST})
	@ResponseBody
	public ReturnResult getDetailRankList(
			@RequestParam(required = false, defaultValue = "1") Integer currentPage
			) throws Exception {
		//查询活动是否进行中
		ActivityConfig activity = commissionService.selectActivityById(Constant.COMMISSION_ACTIVITY);
		JSONObject jsonObject = new JSONObject();
		int rankCount = 0;
		List<CommissionRankList> rankList = new ArrayList<>();
		if (activity == null) {
			logger.info("======================返佣活动不存在======================"); 
		} else {
			Date startTime = activity.getStartTime();
			Date endTime = activity.getEndTime();
			Date today = new Date();
			if (today.after(endTime) || today.before(startTime)) {
				logger.info("======================返佣活动不在进行中======================"); 
			} else {
				logger.info("======================活动正在进行中===================="); 
				//从redis中获取排行榜
				String rankListStr = ""; 
				switch (currentPage) {
				case 1:
					rankListStr = redisHelper.get(RedisConstant.COMMISSION + "firstRankList"); 
					break;
				case 2:
					rankListStr = redisHelper.get(RedisConstant.COMMISSION + "secondRankList"); 
					break;
//				case 3:
//					rankListStr = redisHelper.get(RedisConstant.COMMISSION + "thirdRankList");
//					break;
//				case 4:
//					rankListStr = redisHelper.get(RedisConstant.COMMISSION + "fourthRankList");
//					break;
//				case 5:
//					rankListStr = redisHelper.get(RedisConstant.COMMISSION + "fifthRankList");
//					break;
				default:
					break;
				}
				String rankCountStr = redisHelper.get(RedisConstant.COMMISSION + "rankCount"); 
				if (!StringUtils.isEmpty(rankCountStr)) {
					rankCount = Integer.parseInt(rankCountStr);
				} 
				
				JSONObject obj = JSON.parseObject(rankListStr);
				rankList = JSONArray.parseArray(obj.getJSONArray("extObject").toJSONString(), 
						CommissionRankList.class);
			}
		}
		jsonObject.put("total", rankCount); 
		jsonObject.put("page", currentPage); 
		jsonObject.put("list", rankList); 
		return ReturnResult.SUCCESS(jsonObject);
	}
	
	/**
	 * 邀请码
	 */
	@ApiOperation("")
	@RequestMapping(value="/commission/inviterCode",method = {RequestMethod.GET,RequestMethod.POST})
	@ResponseBody
    public ReturnResult getInviterCode() throws Exception {
		JSONObject jsonObject = new JSONObject();
		FUser userInfo = getCurrentUserInfoByToken();
		FUser user = userService.selectUserByFid(userInfo.getFid());
		String inviterCode = ""; 
		if (StringUtils.isEmpty(user.getFintrocode())) {
			//邀请码首位为字母
			String chars = "abcdefghijklmnopqrstuvwxyz"; 
			char charAt = chars.charAt((int)(Math.random() * 26));
			//后5位为uid的16进制转换
			String hex = String.format("%05x", userInfo.getFid()); 
			inviterCode = charAt + hex;
			//插入推荐码
			user.setFintrocode(inviterCode);
			userService.updateUserIntroCode(user);
		} else {
			inviterCode = user.getFintrocode();
		}
		jsonObject.put("inviterCode", inviterCode); 
        ReturnResult result = ReturnResult.SUCCESS(jsonObject);
        return result;
	}
	
	/**
	 * 首页邀请记录
	 */
	@ApiOperation("")
	@RequestMapping(value="/commission/indexInvitationRecord",method = {RequestMethod.GET,RequestMethod.POST})
	@ResponseBody
    public ReturnResult getIndexInvitationRecord() throws Exception {
		JSONObject jsonObject = new JSONObject();
		FUser userInfo = getCurrentUserInfoByToken();
		// 查询总用户数
		Integer totalCount = userService.countUserList(userInfo.getFid());
		//查询邀请记录
    	List<Invitee> list = userService.selectUserByIntroId(userInfo.getFid());
    	for (Invitee invitee : list) {
			invitee.setFloginname(AccountUtil.blurAccount(invitee.getFloginname()));
		}
    	jsonObject.put("indexInvitationCount", totalCount); 
    	jsonObject.put("list", list); 
        ReturnResult result = ReturnResult.SUCCESS(jsonObject);
        return result;
    }
	
	/**
	 * 详情页邀请记录
	 */
	@ApiOperation("")
	@RequestMapping(value="/commission/detailInvitationRecord",method = {RequestMethod.GET,RequestMethod.POST})
	@ResponseBody
    public ReturnResult getDetailInvitationRecord(
    		@RequestParam(required = false) String loginname,
    		@RequestParam(required = false) String time,
    		@RequestParam(required = false, defaultValue = "1") Integer currentPage,
    		@RequestParam(required = false, defaultValue = "10") Integer pageSize
    		) throws Exception {
        JSONObject jsonObject = new JSONObject();
        FUser userInfo = getCurrentUserInfoByToken();
        // 定义查询条件
        Pagination<Invitee> pageParam = new Pagination<Invitee>(currentPage, pageSize);
        Invitee invitee = new Invitee();
        
        invitee.setFintrouid(userInfo.getFid());
        if (!StringUtils.isEmpty(loginname)) {
        	invitee.setFloginname(loginname);
        	jsonObject.put("loginname", loginname); 
		}
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd"); 
        if (!StringUtils.isEmpty(time)){
        	invitee.setFregistertime(format.parse(time));
			jsonObject.put("time", time); 
		}
        
        //分页查询邀请记录
        Pagination<Invitee> pagination = userService.selectUserPageList(pageParam, invitee);
        for (Invitee inviteeInterator : pagination.getData()) {
        	inviteeInterator.setFloginname(AccountUtil.blurAccount(inviteeInterator.getFloginname()));
		}
        jsonObject.put("total", pagination.getTotalRows()); 
        jsonObject.put("page", currentPage); 
        jsonObject.put("list", pagination.getData()); 
        ReturnResult result = ReturnResult.SUCCESS(jsonObject);
        return result;
    }
	
	/**
	 * 首页返佣记录
	 */
	@ApiOperation("")
	@RequestMapping(value="/commission/indexCommissionRecord",method = {RequestMethod.GET,RequestMethod.POST})
	@ResponseBody
    public ReturnResult getIndexCommissionRecord() throws Exception {
        JSONObject jsonObject = new JSONObject();
        FUser userInfo = getCurrentUserInfoByToken();
    	//查询总佣金
    	BigDecimal commissionAmount = commissionService.selectAmountByIntroId(userInfo.getFid());
		ReturnResult result;
		if(null != commissionAmount) {
			jsonObject.put("indexCommissionAmount", commissionAmount); 
			//查询返佣记录
			List<Commission> list = commissionService.selectCommissionByIntroId(userInfo.getFid());
			for (Commission commission : list) {
				commission.setInviteeLoginname(AccountUtil.blurAccount(commission.getInviteeLoginname()));
			}
			jsonObject.put("list", list); 
		}else{
			jsonObject.put("indexCommissionAmount", BigDecimal.ZERO); 
			jsonObject.put("list", new ArrayList<Commission>(0)); 
		}
		result = ReturnResult.SUCCESS(jsonObject);
        return result;
    }
	
	/**
	 * 详情页返佣记录
	 */
	@ApiOperation("")
	@RequestMapping(value="/commission/detailCommissionRecord",method = {RequestMethod.GET,RequestMethod.POST})
	@ResponseBody
    public ReturnResult getDetailCommissionRecord(
    		@RequestParam(required = false) String loginname,
    		@RequestParam(required = false) String time,
    		@RequestParam(required = false, defaultValue = "1") Integer currentPage,
    		@RequestParam(required = false, defaultValue = "10") Integer pageSize
    		) throws Exception {
        JSONObject jsonObject = new JSONObject();
        FUser userInfo = getCurrentUserInfoByToken();
        // 定义查询条件
  		Pagination<Commission> pageParam = new Pagination<Commission>(currentPage, pageSize);
  		Commission commission = new Commission();
  		//参数判断
         commission.setInviterId(userInfo.getFid());
         if (!StringUtils.isEmpty(loginname)) {
         	commission.setInviteeLoginname(loginname);
         	jsonObject.put("loginname", loginname); 
 		}
         DateFormat format = new SimpleDateFormat("yyyy-MM-dd"); 
         if (!StringUtils.isEmpty(time)){
 			commission.setMerchandiseTime(format.parse(time));
 			jsonObject.put("time", time); 
 		}
 		
         //分页查询返佣记录
 		Pagination<Commission> pagination = commissionService.selectCommissionPageList(pageParam, commission);
 		for (Commission commissionInterator : pagination.getData()) {
 			commissionInterator.setInviteeLoginname(AccountUtil.blurAccount(commissionInterator.getInviteeLoginname()));
		}
 		jsonObject.put("total", pagination.getTotalRows()); 
 		jsonObject.put("page", currentPage); 
 		jsonObject.put("list", pagination.getData()); 
        ReturnResult result = ReturnResult.SUCCESS(jsonObject);
        return result;
    }
	
	/**
	 * 查询返佣比例
	 * 
	 */
	@PassToken
	@ApiOperation("")
	@RequestMapping(value="/commission/getCommissionRate",method = {RequestMethod.GET,RequestMethod.POST})
	@ResponseBody
    public ReturnResult getCommissionRate(
    		) throws Exception {
        JSONObject jsonObject = new JSONObject();
        String commissionRate = commissionService.getCommissionRate();
 		jsonObject.put("commissionRate", commissionRate); 
        ReturnResult result = ReturnResult.SUCCESS(jsonObject);
        return result;
    }
	

	/**
     * EBank交易大赛
     * @param currentPage
     * @return
     * @throws Exception
     */
    @ResponseBody
    @ApiOperation("")
	@RequestMapping(value="/activity/ebankRank",method = {RequestMethod.GET,RequestMethod.POST})
    public ReturnResult getEbankRank(
    		@RequestParam(required = false, defaultValue = "1") Integer currentPage
    		) throws Exception {
    	JSONObject jsonObject = new JSONObject();
    	//从redis中查询更新时间
    	String updateTime = redisHelper.get(RedisConstant.EBANK_RANK + "updateTime"); 
    	if (updateTime == null) {
    		Date now = new Date();
    		updateTime = now.getTime() + ""; 
		}
		jsonObject.put("updateTime", Long.parseLong(updateTime)); 
    	//从redis中获取排行榜
    	List<EBankRank> rankList = new ArrayList<EBankRank>();
    	String rankListStr = null;
    	rankListStr = redisHelper.get(RedisConstant.EBANK_RANK + "list"); 
    	if (rankListStr == null) {
			jsonObject.put("total", 0); 
			jsonObject.put("list", rankList); 
		} else {
			JSONObject obj = JSON.parseObject(rankListStr);
			rankList = JSONArray.parseArray(obj.getJSONArray("extObject").toJSONString(), 
					EBankRank.class);
			List<EBankRank> list = new ArrayList<EBankRank>();
			
			for (int i = 0; i < rankList.size(); i++) {
				rankList.get(i).setSort(i+1);
			}
			if (currentPage == 1) {
				if (rankList.size() < 25) {
					list.addAll(rankList);
				} else {
					for (int i = 0; i < 25; i++) {
						list.add(rankList.get(i));
					}
				}
			} else {
				for (int i = 25; i < rankList.size(); i++) {
					list.add(rankList.get(i));
				}
			}
			jsonObject.put("total", rankList.size()); 
			jsonObject.put("list", list); 
		}
    	return ReturnResult.SUCCESS(jsonObject);
    }
    
    /**
     * EBank交易大赛
     * @param currentPage
     * @return
     * @throws Exception
     */
    @ResponseBody
    @ApiOperation("")
	@RequestMapping(value="/activity/ebankTime",method = {RequestMethod.GET,RequestMethod.POST})
    public ReturnResult getEbankTime(@RequestParam(required = false, defaultValue = "1") Integer currentPage) throws Exception {
    	//查询ebank交易赛活动
    	RankActivityConfig activity = commissionService.selectRankActivity(Constant.EBANK_RANK_ACTIVITY);
    	if (activity == null) {
    		return ReturnResult.FAILUER(I18NUtils.getString("FrontCommissionController.55")); 
		}
    	JSONObject jsonObject = new JSONObject();
		jsonObject.put("startTime", activity.getStartTime()); 
    	jsonObject.put("endTime", activity.getEndTime()); 
    	return ReturnResult.SUCCESS(jsonObject);
    }
}
