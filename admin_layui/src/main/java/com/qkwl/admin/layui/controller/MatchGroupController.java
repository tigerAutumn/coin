package com.qkwl.admin.layui.controller;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.qkwl.admin.layui.base.WebBaseController;
import com.qkwl.admin.layui.utils.WebConstant;
import com.qkwl.common.dto.coin.SystemTradeType;
import com.qkwl.common.dto.common.Pagination;
import com.qkwl.common.dto.matchGroup.MatchGroup;
import com.qkwl.common.framework.redis.RedisHelper;
import com.qkwl.common.rpc.admin.IAdminMatchGroupService;
import com.qkwl.common.util.Constant;
import com.qkwl.common.util.ReturnResult;

@Controller
public class MatchGroupController extends WebBaseController {

	@Autowired
	private IAdminMatchGroupService adminMatchGroupService;
	@Autowired
    private RedisHelper redisHelper;
	
	/**
	 * 分页大小
	 */	
 	private int numPerPage = Constant.adminPageSize;
	
	/**
 	 * 查询交易对组信息
 	 */
	@RequestMapping("/admin/matchGroupList")
	public ModelAndView getMatchGroupList(
			@RequestParam(value="pageNum",required=false,defaultValue="1") Integer currentPage,
			@RequestParam(value="orderField",required=false,defaultValue="id") String orderField,
			@RequestParam(value="orderDirection",required=false,defaultValue="asc") String orderDirection
			) throws Exception {
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("matchGroup/matchGroupList");
		// 定义查询条件
		Pagination<MatchGroup> pageParam = new Pagination<MatchGroup>(currentPage, numPerPage);
		MatchGroup matchGroup = new MatchGroup();
		
		pageParam.setOrderDirection(orderDirection);
		pageParam.setOrderField(orderField);

		//查询交易对列表
		Pagination<MatchGroup> pagination = adminMatchGroupService.selectMatchGroupPageList(pageParam, matchGroup);

		modelAndView.addObject("matchGroupList", pagination);
		return modelAndView;
	}
	
	/**
	 * 删除交易对组
	 * @param id
	 * @return
	 */
	@RequestMapping("/admin/deleteMatchGroup")
	@ResponseBody
	public ReturnResult deleteMatchGroup(
			@RequestParam(value = "id", required = false,defaultValue="0") Integer id) {
		MatchGroup matchGroup = new MatchGroup();
		if (id > 0) {
			matchGroup = adminMatchGroupService.selectMatchGroupById(id);
		}
		if (matchGroup == null) {
			return ReturnResult.FAILUER("交易对组不存在!");
		}
		//删除交易对组
		return adminMatchGroupService.deleteMatchGroup(matchGroup);
	}
	
	/**
	 * 添加交易对组
	 * @return
	 */
	@RequestMapping("/admin/addMatchGroup")
	public ModelAndView addMatchGroup(){
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("matchGroup/addMatchGroup");
		// 查询所有交易对
		List<SystemTradeType> allTradeType = redisHelper.getAllTradeTypeList(WebConstant.BCAgentId);
		//排除交易对组中已经存在的交易对
		List<MatchGroup> allGroup = adminMatchGroupService.selectAll();
		for (MatchGroup matchGroup : allGroup) {
			String[] split = matchGroup.getTradeIds().split(",");
			for (int i = 0; i < allTradeType.size(); i++) {
				if (Arrays.asList(split).contains(allTradeType.get(i).getId()+"")) {
					allTradeType.remove(i);
					i--;
				}
			}
		}
		modelAndView.addObject("allTradeType", allTradeType);
		return modelAndView;
	}
	
	/**
     * 保存新增的交易对组
     */
    @RequestMapping("admin/saveMatchGroup")
    @ResponseBody
    public ReturnResult saveMatchGroup(
            @RequestParam(value = "groupName", required = false) String groupName
            ) throws Exception {
    	MatchGroup group = adminMatchGroupService.selectMatchGroupByGroupName(groupName);
    	if (group != null) {
			return ReturnResult.FAILUER("交易对组名称重复!");
		}
    	// 解析业务类型
        HttpServletRequest request = sessionContextUtils.getContextRequest();
    	// 查询所有交易对
    	List<SystemTradeType> allTradeType = redisHelper.getAllTradeTypeList(WebConstant.BCAgentId);
    	String requestParam;
        String tradeIds = "";
    	for (SystemTradeType systemTradeType : allTradeType) {
            requestParam = request.getParameter("allTradeType[" + systemTradeType.getId() + "]");
            if (!StringUtils.isEmpty(requestParam)) {
            	if ("".equals(tradeIds)) {
            		tradeIds += systemTradeType.getId();
				} else {
					tradeIds += "," + systemTradeType.getId();
				}
            }
        }
    	MatchGroup matchGroup = new MatchGroup();
    	matchGroup.setGroupName(groupName);
    	matchGroup.setTradeIds(tradeIds);
    	Date now = new Date();
    	matchGroup.setCreateTime(now);
    	matchGroup.setUpdateTime(now);
        return adminMatchGroupService.insertMatchGroup(matchGroup);
    }
	
	/**
	 * 读取修改的交易对组
	 * @return
	 */
	@RequestMapping("/admin/changeMatchGroup")
	public ModelAndView changeMatchGroup(@RequestParam(value="id",required=false) Integer id){
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("/matchGroup/changeMatchGroup");
		// 查询所有交易对
    	List<SystemTradeType> allTradeType = redisHelper.getAllTradeTypeList(WebConstant.BCAgentId);
    	//排除其他交易对组中已经存在的交易对
		List<MatchGroup> allGroup = adminMatchGroupService.selectOthers(id);
		for (MatchGroup matchGroup : allGroup) {
			String[] split = matchGroup.getTradeIds().split(",");
			for (int i = 0; i < allTradeType.size(); i++) {
				if (Arrays.asList(split).contains(allTradeType.get(i).getId()+"")) {
					allTradeType.remove(i);
					i--;
				}
			}
		}
    	modelAndView.addObject("allTradeType", allTradeType);
		if (id > 0) {
			MatchGroup matchGroup = adminMatchGroupService.selectMatchGroupById(id);
			modelAndView.addObject("matchGroup", matchGroup);
		}
		return modelAndView;
	}
	
	/**
     * 修改的交易对组
     */
    @RequestMapping("admin/updateMatchGroup")
    @ResponseBody
    public ReturnResult updateMatchGroup(
    		@RequestParam(value = "id", required = true,defaultValue = "0") Integer id,
    		@RequestParam(value = "groupName", required = false) String groupName
            ) throws Exception {
    	// 解析业务类型
        HttpServletRequest request = sessionContextUtils.getContextRequest();
    	// 查询所有交易对
    	List<SystemTradeType> allTradeType = redisHelper.getAllTradeTypeList(WebConstant.BCAgentId);
    	String requestParam;
        String tradeIds = "";
    	for (SystemTradeType systemTradeType : allTradeType) {
            requestParam = request.getParameter("allTradeType[" + systemTradeType.getId() + "]");
            if (!StringUtils.isEmpty(requestParam)) {
            	if ("".equals(tradeIds)) {
            		tradeIds += systemTradeType.getId();
				} else {
					tradeIds += "," + systemTradeType.getId();
				}
            }
        }
    	MatchGroup matchGroup = new MatchGroup();
		if (id > 0) {
			matchGroup = adminMatchGroupService.selectMatchGroupById(id);
		}
		if (matchGroup == null) {
			return ReturnResult.FAILUER("交易对组不存在!");
		}
		matchGroup.setGroupName(groupName);
    	matchGroup.setTradeIds(tradeIds);
    	Date now = new Date();
    	matchGroup.setUpdateTime(now);
        return adminMatchGroupService.updateMatchGroup(matchGroup);
    }
}
