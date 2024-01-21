package com.qkwl.admin.layui.controller;

import java.util.Collection;
import java.util.List;

import org.apache.commons.jexl2.Expression;
import org.apache.commons.jexl2.JexlEngine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSONArray;
import com.qkwl.admin.layui.utils.WebConstant;
import com.qkwl.common.dto.coin.SystemTradeType;
import com.qkwl.common.dto.coin.SystemTradeTypeRule;
import com.qkwl.common.dto.common.Pagination;
import com.qkwl.common.dto.traderule.Rectype;
import com.qkwl.common.dto.traderule.Ruleid;
import com.qkwl.common.framework.redis.RedisHelper;
import com.qkwl.common.rpc.admin.IAdminTradeTypeRulesService;
import com.qkwl.common.util.Constant;
import com.qkwl.common.util.ReturnResult;

@Controller
public class SystemTradeTypeRulesController {

	/**
	 * 分页大小
	 */	
 	private int numPerPage = Constant.adminPageSize;
 	
 	@Autowired
 	IAdminTradeTypeRulesService adminTradeTypeRulesService;
 	@Autowired
    private RedisHelper redisHelper;
 	
 	/**
 	 * 查询交易对规则信息
 	 */
	@RequestMapping("/admin/tradeTypeRulesList")
	public ModelAndView getTradeTypeRulesList(
			@RequestParam(value="pageNum",required=false,defaultValue="1") Integer currentPage
			) throws Exception {
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("tradeRules/tradeTypeRulesList");
		// 定义查询条件
		Pagination<SystemTradeTypeRule> pageParam = new Pagination<SystemTradeTypeRule>(currentPage, numPerPage);
		SystemTradeTypeRule systemTradeTypeRule = new SystemTradeTypeRule();
		
		//查询规则列表
		Pagination<SystemTradeTypeRule> pagination = adminTradeTypeRulesService.selectTradeTypeRulesPageList(pageParam, systemTradeTypeRule);
		Collection<SystemTradeTypeRule> data = pagination.getData();
		//类型
		String rectypeStr = redisHelper.getSystemArgs("rectype");
		List<Rectype> allRectype = JSONArray.parseArray(rectypeStr, Rectype.class);
		//规则id
		String ruleidStr = redisHelper.getSystemArgs("ruleid");
		List<Ruleid> allRuleid = JSONArray.parseArray(ruleidStr, Ruleid.class);
		for (SystemTradeTypeRule rule : data) {
			for (Rectype rectype : allRectype) {
				if (rule.getRectype().equals(rectype.getId())) {
					rule.setRectypeName(rectype.getName());
				}
			}
			for (Ruleid ruleid : allRuleid) {
				if (rule.getRuleid().equals(ruleid.getId())) {
					rule.setRuleidName(ruleid.getName());
				}
			}
		}
		
		modelAndView.addObject("tradeTypeRulesList", pagination);
		return modelAndView;
	}
	
	/**
	 * 删除规则
	 * @param id
	 * @return
	 */
	@RequestMapping("/admin/deleteTradeTypeRules")
	@ResponseBody
	public ReturnResult deleteTradeTypeRules(
			@RequestParam(value = "id", required = false,defaultValue="0") Integer id) {
		SystemTradeTypeRule tradeTypeRule = new SystemTradeTypeRule();
		if (id > 0) {
			tradeTypeRule = adminTradeTypeRulesService.selectTradeTypeRulesById(id);
		}
		if (tradeTypeRule == null) {
			return ReturnResult.FAILUER("规则不存在!");
		}
		//删除规则
		return adminTradeTypeRulesService.deleteTradeTypeRules(tradeTypeRule);
	}
	
	/**
	 * 开启规则
	 * @param id
	 * @return
	 */
	@RequestMapping("/admin/openTradeTypeRules")
	@ResponseBody
	public ReturnResult openTradeTypeRules(
			@RequestParam(value = "id", required = false,defaultValue="0") Integer id) {
		SystemTradeTypeRule tradeTypeRule = new SystemTradeTypeRule();
		if (id > 0) {
			tradeTypeRule = adminTradeTypeRulesService.selectTradeTypeRulesById(id);
		}
		if (tradeTypeRule == null) {
			return ReturnResult.FAILUER("规则不存在!");
		}
		tradeTypeRule.setStatus(1);
		//开启规则
		return adminTradeTypeRulesService.updateTradeTypeRules(tradeTypeRule);
	}
	
	/**
	 * 禁用规则
	 * @param id
	 * @return
	 */
	@RequestMapping("/admin/closeTradeTypeRules")
	@ResponseBody
	public ReturnResult closeTradeTypeRules(
			@RequestParam(value = "id", required = false,defaultValue="0") Integer id) {
		SystemTradeTypeRule tradeTypeRule = new SystemTradeTypeRule();
		if (id > 0) {
			tradeTypeRule = adminTradeTypeRulesService.selectTradeTypeRulesById(id);
		}
		if (tradeTypeRule == null) {
			return ReturnResult.FAILUER("规则不存在!");
		}
		tradeTypeRule.setStatus(0);
		//禁用规则
		return adminTradeTypeRulesService.updateTradeTypeRules(tradeTypeRule);
	}
	
	/**
	 * 添加规则
	 * @return
	 */
	@RequestMapping("/admin/addTradeTypeRules")
	public ModelAndView addTradeTypeRule(){
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("tradeRules/addTradeTypeRule");
		// 查询未被禁用的交易对
		List<SystemTradeType> allTradeType = redisHelper.getTradeTypeList(WebConstant.BCAgentId);
		modelAndView.addObject("allTradeType", allTradeType);
		//类型
		String rectype = redisHelper.getSystemArgs("rectype");
		if (rectype != null) {
			List<Rectype> allRectype = JSONArray.parseArray(rectype, Rectype.class);
        	modelAndView.addObject("allRectype", allRectype);
        }
		//规则id
		String ruleid = redisHelper.getSystemArgs("ruleid");
		if (ruleid != null) {
			List<Ruleid> allRuleid = JSONArray.parseArray(ruleid, Ruleid.class);
        	modelAndView.addObject("allRuleid", allRuleid);
        }
		return modelAndView;
	}
	
	/**
     * 保存新增的规则信息
     */
    @RequestMapping("admin/saveTradeTypeRules")
    @ResponseBody
    public ReturnResult saveTradeTypeRules(
    		@RequestParam(value = "tradeid", required = true) Integer tradeid,
            @RequestParam(value = "rectype", required = true) String rectype,
            @RequestParam(value = "ruleid", required = true) String ruleid,
            @RequestParam(value = "exp", required = true) String exp,
            @RequestParam(value = "status", required = true) Integer status,
            @RequestParam(value = "resultcode", required = true) Integer resultcode
            ) throws Exception {
    	//校验exp的jexl表达式
    	try {
    		JexlEngine jexl = new JexlEngine();
    		Expression expr = jexl.createExpression(exp);
		} catch (Exception e) {
			return ReturnResult.FAILUER("限制规则语法错误！");
		}
    	
    	SystemTradeTypeRule tradeTypeRule = new SystemTradeTypeRule();
    	tradeTypeRule.setTradeid(tradeid);
    	tradeTypeRule.setRectype(rectype);
    	tradeTypeRule.setRuleid(ruleid);
    	tradeTypeRule.setExp(exp);
    	tradeTypeRule.setStatus(status);
    	tradeTypeRule.setResultcode(resultcode);
        return adminTradeTypeRulesService.insertTradeTypeRules(tradeTypeRule);
    }
	
	/**
	 * 读取修改的交易对规则
	 * @return
	 */
	@RequestMapping("/admin/changeTradeTypeRules")
	public ModelAndView changeTradeTypeRules(@RequestParam(value="id",required=false) Integer id){
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("/tradeRules/changeTradeTypeRule");
		if (id > 0) {
			SystemTradeTypeRule tradeTypeRule = adminTradeTypeRulesService.selectTradeTypeRulesById(id);
			modelAndView.addObject("tradeTypeRule", tradeTypeRule);
		}
		// 查询所有交易对
		List<SystemTradeType> allTradeType = redisHelper.getTradeTypeList(WebConstant.BCAgentId);
		modelAndView.addObject("allTradeType", allTradeType);
		//类型
		String rectype = redisHelper.getSystemArgs("rectype");
		if (rectype != null) {
			List<Rectype> allRectype = JSONArray.parseArray(rectype, Rectype.class);
        	modelAndView.addObject("allRectype", allRectype);
        }
		//规则id
		String ruleid = redisHelper.getSystemArgs("ruleid");
		if (ruleid != null) {
			List<Ruleid> allRuleid = JSONArray.parseArray(ruleid, Ruleid.class);
        	modelAndView.addObject("allRuleid", allRuleid);
        }
		return modelAndView;
	}
	
	/**
     * 修改的交易对规则
     */
    @RequestMapping("admin/updateTradeTypeRules")
    @ResponseBody
    public ReturnResult updateTradeTypeRules(
    		@RequestParam(value = "id", required = true,defaultValue = "0") Integer id,
    		@RequestParam(value = "tradeid", required = true) Integer tradeid,
            @RequestParam(value = "rectype", required = true) String rectype,
            @RequestParam(value = "ruleid", required = true) String ruleid,
            @RequestParam(value = "exp", required = true) String exp,
            @RequestParam(value = "status", required = true) Integer status,
            @RequestParam(value = "resultcode", required = true) Integer resultcode
            ) throws Exception {
    	//校验exp的jexl表达式
    	try {
    		JexlEngine jexl = new JexlEngine();
    		Expression expr = jexl.createExpression(exp);
		} catch (Exception e) {
			return ReturnResult.FAILUER("限制规则语法错误！");
		}
    	
    	SystemTradeTypeRule tradeTypeRule = new SystemTradeTypeRule();
		if (id > 0) {
			tradeTypeRule = adminTradeTypeRulesService.selectTradeTypeRulesById(id);
		}
		if (tradeTypeRule == null) {
			return ReturnResult.FAILUER("规则不存在!");
		}
		tradeTypeRule.setTradeid(tradeid);
    	tradeTypeRule.setRectype(rectype);
    	tradeTypeRule.setRuleid(ruleid);
    	tradeTypeRule.setExp(exp);
    	tradeTypeRule.setStatus(status);
    	tradeTypeRule.setResultcode(resultcode);
		
        return adminTradeTypeRulesService.updateTradeTypeRules(tradeTypeRule);
    }
}
