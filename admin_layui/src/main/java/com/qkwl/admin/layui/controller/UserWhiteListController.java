package com.qkwl.admin.layui.controller;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import com.qkwl.admin.layui.base.WebBaseController;
import com.qkwl.common.dto.Enum.UserWhiteListTypeEnum;
import com.qkwl.common.dto.admin.FAdmin;
import com.qkwl.common.dto.coin.SystemCoinType;
import com.qkwl.common.dto.common.Pagination;
import com.qkwl.common.dto.whiteList.UserWhiteList;
import com.qkwl.common.framework.redis.RedisHelper;
import com.qkwl.common.rpc.admin.IAdminWhiteListService;
import com.qkwl.common.util.Constant;
import com.qkwl.common.util.ReturnResult;


@Controller
public class UserWhiteListController extends WebBaseController {
	
	private static final Logger logger = LoggerFactory.getLogger(UserWhiteListController.class);
	
	@Autowired
	private IAdminWhiteListService adminWhiteListService;
	@Autowired
	private RedisHelper redisHelper;
	// 每页显示多少条数据
	private int numPerPage = Constant.adminPageSize;

	/**
	 * 查询白名单
	 * 
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("admin/whiteList")
	public ModelAndView walletList(
			@RequestParam(value="pageNum",required=false,defaultValue="1") Integer currentPage,
			@RequestParam(value="userId",required=false) Integer userId,
			@RequestParam(value="type",required=false) Integer type
			){
		try {
			ModelAndView modelAndView = new ModelAndView();
			modelAndView.setViewName("whiteList/userWhiteList");
			
			Map<Integer,String> typeMap = new HashMap<>();
			typeMap.put(0, "全部");
			for (UserWhiteListTypeEnum u : UserWhiteListTypeEnum.values()) {
				typeMap.put(u.getCode(), u.getDescribe());
			}
			modelAndView.addObject("typeMap",typeMap);
			
			Map<String,Object> param = new HashMap<>();
			if(userId != null) {
				param.put("userId", userId);
				modelAndView.addObject("userId",userId);
			}
			if(type != null && type != 0) {
				param.put("type", type);
				modelAndView.addObject("type",type);
			}
			Pagination<UserWhiteList> pageParam = new Pagination<UserWhiteList>(currentPage, numPerPage);
			// 排序条件
			pageParam.setOrderField("updatetime");
			pageParam.setOrderDirection("desc");
			pageParam.setParam(param);
			
			// 查询
			Pagination<UserWhiteList> pagination = adminWhiteListService.selectUserWhiteList(pageParam);
			modelAndView.addObject("UserWhiteList", pagination);
			return modelAndView;
		} catch (Exception e) {
			logger.error("查询白名单异常",e);
			return returnErrorModel("系统异常"+e.getMessage());
		}
	}
	
	
	/**
	 * 添加白名单页面
	 * 
	 * @return
	 */
	@RequestMapping("admin/addWhiteListPage")
	public ModelAndView addWhiteListPage(){
		try {
			ModelAndView modelAndView = new ModelAndView();
			modelAndView.setViewName("whiteList/addUserWhiteList");
			Map<Integer,String> typeMap = new HashMap<>();
			for (UserWhiteListTypeEnum u : UserWhiteListTypeEnum.values()) {
				typeMap.put(u.getCode(), u.getDescribe());
			}
			modelAndView.addObject("typeMap",typeMap);
			List<SystemCoinType> coinTypeListSystem = redisHelper.getCoinTypeListSystem();
			Map<Integer, String> collect = new HashMap<>();
			collect.put(0, "无");
			coinTypeListSystem.forEach((action) -> collect.put(action.getId(), action.getIsInnovateAreaCoin() ? action.getName() + "(创新区)" : action.getName()));
			 //= coinTypeListSystem.stream().collect(Collectors.toMap(SystemCoinType::getId,f -> f.getName()));
			modelAndView.addObject("coinTypeMap",collect);
			return modelAndView;
		} catch (Exception e) {
			logger.error("添加白名单页面异常",e);
			return returnErrorModel("系统异常"+e.getMessage());
		}
	}
	
	/**
	 * 添加白名单页面
	 * 
	 * @return
	 */
	@RequestMapping("admin/addWhiteList")
	@ResponseBody
	public ReturnResult addWhiteList(
			@RequestParam(value="coinId",required=false ,defaultValue="0") Integer coinId,
			@RequestParam(value="userId",required=true) Integer userId,
			@RequestParam(value="type",required=true) Integer type
			){
		try {
			HttpServletRequest request = sessionContextUtils.getContextRequest();
			FAdmin admin = (FAdmin) request.getSession().getAttribute("login_admin");
			UserWhiteList userWhiteList = new UserWhiteList();
			userWhiteList.setUserId(userId);
			if(coinId != 0) {
				userWhiteList.setCoinId(coinId);
			}
			userWhiteList.setType(type);
			Date date = new Date();
			userWhiteList.setUpdatetime(date);
			userWhiteList.setCreatetime(date);
			userWhiteList.setAdminId(admin.getFid());
			ReturnResult insert = adminWhiteListService.insert(userWhiteList);
			return insert;
		} catch (Exception e) {
			logger.error("添加白名单异常",e);
			return ReturnResult.FAILUER("系统异常");
		}
	}
	
	/**
	 * 添加白名单页面
	 * 
	 * @return
	 */
	@RequestMapping("admin/deleteWhiteList")
	@ResponseBody
	public ReturnResult deleteWhiteList(
			@RequestParam(value="id",required=true) Integer id
			){
		try {
			ReturnResult delete = adminWhiteListService.delete(id);
			return delete;
		} catch (Exception e) {
			logger.error("添加白名单异常",e);
			return ReturnResult.FAILUER("系统异常");
		}
	}
	
}
