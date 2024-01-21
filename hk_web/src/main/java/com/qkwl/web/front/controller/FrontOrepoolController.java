package com.qkwl.web.front.controller;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageInfo;
import com.qkwl.common.dto.Enum.ErrorCodeEnum;
import com.qkwl.common.dto.Enum.orepool.OrepoolPlanStatusEnum;
import com.qkwl.common.dto.Enum.orepool.OrepoolPlanTypeEnum;
import com.qkwl.common.dto.Enum.orepool.OrepoolRecordStatusEnum;
import com.qkwl.common.dto.coin.SystemTradeType;
import com.qkwl.common.dto.orepool.OrepoolPlan;
import com.qkwl.common.dto.orepool.OrepoolRecord;
import com.qkwl.common.dto.user.FUser;
import com.qkwl.common.exceptions.BCException;
import com.qkwl.common.exceptions.BizException;
import com.qkwl.common.framework.redis.RedisHelper;
import com.qkwl.common.match.MathUtils;
import com.qkwl.common.result.Result;
import com.qkwl.common.rpc.orepool.IOrepoolService;
import com.qkwl.common.util.I18NUtils;
import com.qkwl.common.util.ReturnResult;
import com.qkwl.web.Handler.CheckRepeatSubmit;
import com.qkwl.web.front.controller.base.JsonBaseController;
import com.qkwl.web.permission.annotation.PassToken;

import io.swagger.annotations.ApiOperation;
import springfox.documentation.annotations.ApiIgnore;

@Controller
@ApiIgnore
@RequestMapping(value="/orepool",method = {RequestMethod.GET,RequestMethod.POST})
public class FrontOrepoolController extends JsonBaseController {

	private static final Logger logger = LoggerFactory.getLogger(FrontOrepoolController.class);
	@Autowired
	private IOrepoolService orepoolService;
	@Autowired
    private RedisHelper redisHelper;
	
	/**
     * 定期计划首页
     */
    @ResponseBody
	@PassToken
    @ApiOperation("")
	@RequestMapping(value="/fixedPlanIndex",method = {RequestMethod.GET,RequestMethod.POST})
    public ReturnResult fixedPlanIndex(
    		@RequestParam(required = false, defaultValue = "1") Integer currentPage,
    		@RequestParam(required = false, defaultValue = "10") Integer numPerPage) {
    		PageInfo<OrepoolPlan> info = orepoolService.getFixedPlanList(currentPage, numPerPage);
    		List<OrepoolPlan> list = info.getList();
    		//查询币种数量精度
    		for (OrepoolPlan orepoolPlan : list) {
    			Integer countDigit = getCountDigit(orepoolPlan.getCoinName());
				Integer planId = orepoolPlan.getId();
				//查询参与人数
				Integer personCount = orepoolService.getPersonCount(planId);
				orepoolPlan.setLockPerson(personCount);
				orepoolPlan.setLockPercent(MathUtils.toScaleNum(orepoolPlan.getLockPercent(), 4).stripTrailingZeros());
				orepoolPlan.setVisibleVolume(MathUtils.toScaleNum(orepoolPlan.getVisibleVolume(), countDigit).stripTrailingZeros());
				if (orepoolPlan.getStatus() == OrepoolPlanStatusEnum.not_start.getCode()) {
					orepoolPlan.setStatus(1);
				} else if (orepoolPlan.getStatus() == OrepoolPlanStatusEnum.start.getCode()) {
					orepoolPlan.setStatus(2);
				} else if (orepoolPlan.getStatus() == OrepoolPlanStatusEnum.end.getCode() || 
						orepoolPlan.getStatus() == OrepoolPlanStatusEnum.unlock.getCode()) {
					orepoolPlan.setStatus(3);
				} 
			}
	        return ReturnResult.SUCCESS(info);
    }
    
    /**
     * 活期计划首页
     */
    @ResponseBody
	@PassToken
    @ApiOperation("")
	@RequestMapping(value="/currentPlanIndex",method = {RequestMethod.GET,RequestMethod.POST})
    public ReturnResult currentPlanIndex(
    		@RequestParam(required = false, defaultValue = "1") Integer currentPage,
    		@RequestParam(required = false, defaultValue = "10") Integer numPerPage) {
    		PageInfo<OrepoolPlan> info = orepoolService.getCurrentPlanList(currentPage, numPerPage);
    		List<OrepoolPlan> list = info.getList();
    		for (OrepoolPlan orepoolPlan : list) {
    			Integer countDigit = getCountDigit(orepoolPlan.getCoinName());
    			orepoolPlan.setIncomeVolume(MathUtils.toScaleNum(orepoolPlan.getIncomeVolume(), countDigit).stripTrailingZeros());
			}
	        return ReturnResult.SUCCESS(info);
    }
    
    /**
     * 定期计划详情页
     */
    @ResponseBody
	@PassToken
    @ApiOperation("")
	@RequestMapping(value="/fixedPlanDetail",method = {RequestMethod.GET,RequestMethod.POST})
    public ReturnResult fiexdPlanDetail(
    		@RequestParam(required = true) Integer id) {
    		//根据计划id查询矿池计划
    		OrepoolPlan plan = orepoolService.getPlanById(id);
    		if (plan == null) {
    			return ReturnResult.FAILUER(I18NUtils.getString("FrontOrepoolController.4")); 
			}
    		Integer planId = plan.getId();
    		//查询参与人数
    		Integer personCount = orepoolService.getPersonCount(planId);
    		//查询用户账户余额
    		BigDecimal accountAmount = BigDecimal.ZERO;
    		FUser user = getUser();
    		if(user != null) {
    			accountAmount = orepoolService.getAccountBalance(user.getFid(), plan.getLockCoinId());
    		}
    		//查询币种数量精度
    		Integer countDigit = getCountDigit(plan.getCoinName());
    		
    		JSONObject result = new JSONObject();
    		result.put("id", plan.getId()); 
    		result.put("incomeRate", plan.getIncomeRate().stripTrailingZeros()); 
    		result.put("lockPeriod", plan.getLockPeriod()); 
    		result.put("lockPercent", MathUtils.toScaleNum(plan.getLockPercent(), 4).stripTrailingZeros()); 
    		result.put("volume", MathUtils.toScaleNum(plan.getVolume(), countDigit).stripTrailingZeros()); 
    		result.put("lockVolume", MathUtils.toScaleNum(plan.getLockVolume(), countDigit).stripTrailingZeros()); 
    		result.put("visibleVolume", MathUtils.toScaleNum(plan.getVisibleVolume(), countDigit).stripTrailingZeros()); 
    		result.put("maxAmount", MathUtils.toScaleNum(plan.getMaxAmount(), countDigit).stripTrailingZeros()); 
    		result.put("minAmount", MathUtils.toScaleNum(plan.getMinAmount(), countDigit).stripTrailingZeros()); 
    		Date endTime = plan.getEndTime();
    		result.put("lockTime", endTime); 
    		result.put("countTime", endTime); 
    		Calendar cal = Calendar.getInstance();
    		cal.setTime(endTime);
    		cal.add(Calendar.DAY_OF_YEAR, plan.getLockPeriod());
    		result.put("unlockTime", cal.getTime()); 
    		result.put("coinName", plan.getCoinName()); 
    		result.put("coinIcon", plan.getCoinIcon()); 
    		result.put("lockPerson", personCount); 
    		result.put("accountAmount", MathUtils.toScaleNum(accountAmount, countDigit).stripTrailingZeros()); 
    		result.put("countDigit", countDigit); 
    		result.put("lockCoinId", plan.getLockCoinId()); 
    		if (plan.getStatus() == OrepoolPlanStatusEnum.not_start.getCode()) {
    			result.put("status", 1); 
			} else if (plan.getStatus() == OrepoolPlanStatusEnum.start.getCode()) {
				result.put("status", 2); 
			} else if (plan.getStatus() == OrepoolPlanStatusEnum.end.getCode() || 
					plan.getStatus() == OrepoolPlanStatusEnum.unlock.getCode()) {
				result.put("status", 3); 
			} 
	        return ReturnResult.SUCCESS(result);
    }
    
    /**
     * 活期计划详情页
     */
    @ResponseBody
	@PassToken
    @ApiOperation("")
	@RequestMapping(value="/currentPlanDetail",method = {RequestMethod.GET,RequestMethod.POST})
    public ReturnResult currentPlanDetail(
    		@RequestParam(required = true) Integer id) {
    		//根据计划id查询矿池计划
    		OrepoolPlan plan = orepoolService.getPlanById(id);
    		if (plan == null) {
    			return ReturnResult.FAILUER(I18NUtils.getString("FrontOrepoolController.28")); 
			}
    		//查询用户账户余额
    		BigDecimal accountAmount = BigDecimal.ZERO;
    		FUser user = getUser();
    		if(user != null) {
    			accountAmount = orepoolService.getAccountBalance(user.getFid(), plan.getLockCoinId());
    		}
    		//查询币种数量精度
    		Integer countDigit = getCountDigit(plan.getCoinName());
    		
    		JSONObject result = new JSONObject();
    		result.put("id", plan.getId()); 
    		result.put("incomeRate", plan.getIncomeRate().stripTrailingZeros()); 
    		result.put("maxAmount", MathUtils.toScaleNum(plan.getMaxAmount(), 4).stripTrailingZeros()); 
    		result.put("minAmount", MathUtils.toScaleNum(plan.getMinAmount(), 4).stripTrailingZeros()); 
    		//到账日期为N+2 10点
    		Calendar cal = Calendar.getInstance();
    		cal.setTime(new Date());
    		cal.add(Calendar.DAY_OF_YEAR, 2);
    		cal.set(Calendar.HOUR_OF_DAY, 10);
    		cal.set(Calendar.MINUTE, 0);
    		cal.set(Calendar.SECOND, 0);
    		result.put("payTime", cal.getTime()); 
    		result.put("coinName", plan.getCoinName()); 
    		result.put("coinIcon", plan.getCoinIcon()); 
    		result.put("accountAmount", MathUtils.toScaleNum(accountAmount, countDigit).stripTrailingZeros()); 
    		result.put("countDigit", countDigit); 
    		result.put("lockCoinId", plan.getLockCoinId()); 
	        return ReturnResult.SUCCESS(result);
    }
    
    /**
     * 锁定
     */
    @ResponseBody
    @CheckRepeatSubmit
    @ApiOperation("")
	@RequestMapping(value="/lock",method = {RequestMethod.GET,RequestMethod.POST})
    public ReturnResult lock(
    		@RequestParam(required = true) Integer id,
    		@RequestParam(required = true) BigDecimal lockVolume) {
    	FUser user = getUser();
    	if(user == null) {
    		throw new BizException(ErrorCodeEnum.PLEASE_LOGIN);
    	}
		try {
			Result result = orepoolService.lockPlan(user.getFid(), id, lockVolume);
			if (result.getSuccess()) {
	            return ReturnResult.SUCCESS();
	        } else if (result.getCode().equals(Result.FAILURE)) {
	            logger.error("put off advert fail, {}", result.getMsg()); 
	            return ReturnResult.FAILUER(I18NUtils.getString(result.getMsg()));
	        }
		} catch (BCException e) {
			return ReturnResult.FAILUER(I18NUtils.getString(e.getMessage()));
		} catch (Exception e) {
			logger.error("锁仓异常! e:{}",e.getMessage()); 
			return ReturnResult.FAILUER(I18NUtils.getString("FrontOrepoolController.44")); 
		}
    	return ReturnResult.FAILUER(I18NUtils.getString("FrontOrepoolController.45")); 
    }
    
    /**
     * 解锁
     */
    @ResponseBody
    @CheckRepeatSubmit
    @ApiOperation("")
	@RequestMapping(value="/unlock",method = {RequestMethod.GET,RequestMethod.POST})
    public ReturnResult unlock(
    		@RequestParam(required = true) Integer id) {
    	FUser user = getUser();
    	if(user == null) {
    		throw new BizException(ErrorCodeEnum.PLEASE_LOGIN);
    	}
		try {
			Result result = orepoolService.unlockPlan(user.getFid(), id);
			if (result.getSuccess()) {
	            return ReturnResult.SUCCESS();
	        } else if (result.getCode().equals(Result.FAILURE)) {
	            logger.error("put off advert fail, {}", result.getMsg()); 
	            return ReturnResult.FAILUER(I18NUtils.getString(result.getMsg()));
	        }
		} catch (BCException e) {
			return ReturnResult.FAILUER(I18NUtils.getString(e.getMessage()));
		} catch (Exception e) {
			logger.error("解锁失败! e:{}",e.getMessage()); 
			return ReturnResult.FAILUER(I18NUtils.getString("FrontOrepoolController.49")); 
		}
    	return ReturnResult.FAILUER(I18NUtils.getString("FrontOrepoolController.50")); 
    }
    
    /**
     * 定期记录
     */
    @ResponseBody
    @ApiOperation("")
	@RequestMapping(value="/fixedPlanRecord",method = {RequestMethod.GET,RequestMethod.POST})
    public ReturnResult fixedPlanRecord(
    		@RequestParam(required = false, defaultValue = "1") Integer currentPage,
    		@RequestParam(required = false, defaultValue = "10") Integer numPerPage) {
    		FUser user = getUser();
    		if(user == null) {
    			return ReturnResult.FAILUER(ReturnResult.FAULURE_USER_NOT_LOGIN,I18NUtils.getString("FrontOrepoolController.51"));
    		}
    		Map<String, Object> param = new HashMap<>();
    		param.put("userId", user.getFid()); 
    		param.put("type", OrepoolPlanTypeEnum.fixed_plan.getCode()); 
    		PageInfo<OrepoolRecord> info = orepoolService.getRecordList(currentPage, numPerPage, param);
    		List<OrepoolRecord> list = info.getList();
    		for (OrepoolRecord orepoolRecord : list) {
    			//查询币种数量精度
        		Integer countDigit = getCountDigit(orepoolRecord.getCoinName());
    			orepoolRecord.setLockVolume(MathUtils.toScaleNum(orepoolRecord.getLockVolume(), countDigit).stripTrailingZeros());
    			orepoolRecord.setProfit(MathUtils.toScaleNum(orepoolRecord.getProfit(), countDigit).stripTrailingZeros());
    			orepoolRecord.setLockTime(orepoolRecord.getCreateTime());
    			Date endTime = orepoolRecord.getCountTime();
    			Calendar cal = Calendar.getInstance();
        		cal.setTime(endTime);
        		cal.add(Calendar.DAY_OF_YEAR, orepoolRecord.getLockPeriod());
        		orepoolRecord.setUnlockTime(cal.getTime());
			}
	        return ReturnResult.SUCCESS(info);
    }
    
    /**
     * 持币记录
     */
    @ResponseBody
    @ApiOperation("")
	@RequestMapping(value="/currentPlanRecord",method = {RequestMethod.GET,RequestMethod.POST})
    public ReturnResult currentPlanRecord(
    		@RequestParam(required = false, defaultValue = "2") Integer type,
    		@RequestParam(required = false, defaultValue = "1") Integer currentPage,
    		@RequestParam(required = false, defaultValue = "10") Integer numPerPage) {
    		FUser user = getUser();
    		if(user == null) {
    			return ReturnResult.FAILUER(ReturnResult.FAULURE_USER_NOT_LOGIN,I18NUtils.getString("FrontOrepoolController.56"));
    		}
    		Map<String, Object> param = new HashMap<>();
    		param.put("userId", user.getFid()); 
    		param.put("type", type); 
    		//查询记录列表
    		PageInfo<OrepoolRecord> info = orepoolService.getRecordList(currentPage, numPerPage, param);
    		List<OrepoolRecord> list = info.getList();
    		for (OrepoolRecord orepoolRecord : list) {
    			param.put("srcId", orepoolRecord.getId()); 
    			//查询币种数量精度
        		Integer countDigit = getCountDigit(orepoolRecord.getCoinName());
        		
        		orepoolRecord.setLockVolume(MathUtils.toScaleNum(orepoolRecord.getLockVolume(), countDigit).stripTrailingZeros());
        		
    			Date createTime = orepoolRecord.getCreateTime();
    			orepoolRecord.setLockTime(createTime);
    			//计息时间
    			Calendar cal = Calendar.getInstance();
    			cal.setTime(createTime);
        		cal.add(Calendar.DAY_OF_YEAR, 1);
        		cal.set(Calendar.HOUR_OF_DAY, 0);
        		cal.set(Calendar.MINUTE, 0);
        		cal.set(Calendar.SECOND, 0);
        		Date startCountTime = cal.getTime();
    			orepoolRecord.setCountTime(startCountTime);
    			
    			if (orepoolRecord.getStatus() == OrepoolRecordStatusEnum.unlock.getCode()) {
					orepoolRecord.setUnlockTime(orepoolRecord.getUpdateTime());
				}
    			//查询昨日收益
    			BigDecimal yesterdayProfit = orepoolService.getYesterdayProfit(param);
    			orepoolRecord.setYesterdayProfit(yesterdayProfit);
    			//查询累计收益
    			BigDecimal totalProfit = orepoolService.getTotalProfit(param);
    			orepoolRecord.setTotalProfit(totalProfit);
			}
	        return ReturnResult.SUCCESS(info);
    }
    
    private Integer getCountDigit(String coinName) {
		String symbol = coinName+"_GAVC"; 
	    String[] symbols = symbol.split("_"); 
        List<SystemTradeType> tradeTypeList = redisHelper.getTradeTypeList(0);
        SystemTradeType systemTradeType = new SystemTradeType();
        for (SystemTradeType tradeType : tradeTypeList) {
            if (tradeType.getSellShortName().toLowerCase().equals(symbols[0].toLowerCase())
                    && tradeType.getBuyShortName().toLowerCase().equals(symbols[1].toLowerCase())) {
              systemTradeType = tradeType;
            }
        }
        String digit = StringUtils.isEmpty(systemTradeType.getDigit()) ? "2#4" : systemTradeType.getDigit(); 
		String[] digits = digit.split("#"); 
        int countDigit = Integer.valueOf(digits[1]);
		return countDigit;
	}
}
