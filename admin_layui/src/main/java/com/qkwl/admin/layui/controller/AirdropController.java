package com.qkwl.admin.layui.controller;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.qkwl.common.dto.airdrop.Airdrop;
import com.qkwl.common.dto.common.Pagination;
import com.qkwl.common.framework.redis.RedisHelper;
import com.qkwl.common.rpc.admin.IAdminAirdropService;
import com.qkwl.common.util.Constant;
import com.qkwl.common.util.ReturnResult;

@Controller
public class AirdropController {

	@Autowired
	private IAdminAirdropService adminAirdropService;
	
	/**
	 * 分页大小
	 */	
 	private int numPerPage = Constant.adminPageSize;
 	
 	@Autowired
	private RedisHelper redisHelper;
	
	/**
 	 * 查询空投活动信息
 	 */
	@RequestMapping("/admin/airdropActivityList")
	public ModelAndView getAirdropList(
			@RequestParam(value="pageNum",required=false,defaultValue="1") Integer currentPage,
			@RequestParam(value="orderField",required=false,defaultValue="id") String orderField,
			@RequestParam(value="orderDirection",required=false,defaultValue="desc") String orderDirection) throws Exception {
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("airdrop/airdropActivityList");
		// 定义查询条件
		Pagination<Airdrop> pageParam = new Pagination<Airdrop>(currentPage, numPerPage);
		//查询佣金列表
		Pagination<Airdrop> pagination = adminAirdropService.selectAirdrodPageList(pageParam);

		modelAndView.addObject("airdropList", pagination);
		return modelAndView;
	}
	
	/**
 	 * 新增空投活动信息
 	 */
	@RequestMapping("/admin/addAirdrop")
	public ModelAndView addAirdrop(
			@RequestParam(value="pageNum",required=false,defaultValue="1") Integer currentPage,
			@RequestParam(value="orderField",required=false,defaultValue="id") String orderField,
			@RequestParam(value="orderDirection",required=false,defaultValue="desc") String orderDirection) throws Exception {
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("airdrop/addAirdrop");
		//查询所有币种
		Map<Integer, String> coinTypeMap = redisHelper.getCoinTypeNameMap();
		modelAndView.addObject("coinTypeMap", coinTypeMap);
		return modelAndView;
	}
	
	/**
	 * 保存空投活动
	 * @param id
	 * @return
	 */
	@RequestMapping("airdrop/saveAirdrop")
	@ResponseBody
	public ReturnResult saveAirdrop(
			@RequestParam(value = "name", required = false) String name,
			@RequestParam(value = "title", required = false) String title,
			@RequestParam(value = "coinId", required = false) Integer coinId,
			@RequestParam(value = "minCount", required = false) BigDecimal minCount,
			@RequestParam(value = "airdropCoinId", required = false) Integer airdropCoinId,
			@RequestParam(value = "type", required = false) Integer type,
			@RequestParam(value = "countOrRate", required = false) BigDecimal countOrRate,
			@RequestParam(value = "airdropTime", required = false) String airdropTime,
			@RequestParam(value = "startTime", required = false) String startTime,
			@RequestParam(value = "snapshotTime", required = false) String snapshotTime,
			@RequestParam(value = "isOpen", required = false) Integer isOpen,
			@RequestParam(value = "status", required = false) Integer status
			) throws Exception {
		Airdrop airdrop = new Airdrop();
		airdrop.setName(name);
		airdrop.setTitle(title);
		airdrop.setCoinId(coinId);
		airdrop.setMinCount(minCount);
		airdrop.setAirdropCoinId(airdropCoinId);
		airdrop.setType(type);
		airdrop.setCountOrRate(countOrRate);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		if (airdropTime != null && !airdropTime.isEmpty()) {
			airdrop.setAirdropTime(sdf.parse(airdropTime));
		}
		if (startTime != null && !startTime.isEmpty()) {
			airdrop.setStartTime(sdf.parse(startTime));
		}
		if (snapshotTime != null && !snapshotTime.isEmpty()) {
			airdrop.setSnapshotTime(sdf.parse(snapshotTime));
		}
		airdrop.setSnapshotStatus(0);
		airdrop.setIsOpen(isOpen);
		airdrop.setStatus(status);
		//启用空投活动
		try {
			adminAirdropService.insertAirdrop(airdrop);
		} catch (Exception e) {
			return ReturnResult.FAILUER("新增失败！");
		}
		return ReturnResult.SUCCESS("新增成功！");
	}
	
	/**
	 * 启用空投活动
	 * @param id
	 * @return
	 */
	@RequestMapping("airdrop/enableAirdrop")
	@ResponseBody
	public ReturnResult enableAirdrop(
			@RequestParam(value = "id", required = false,defaultValue="0") Integer id) {
		Airdrop airdrop = new Airdrop();
		if (id > 0) {
			airdrop = adminAirdropService.selectAirdropById(id);
		}
		if (airdrop == null) {
			return ReturnResult.FAILUER("空投活动不存在!");
		}
		if (airdrop.getStatus() == 1) {
			return ReturnResult.FAILUER("空投活动已处于启用状态!");
		}
		//启用空投活动
		try {
			airdrop.setStatus(1);
			adminAirdropService.updateAirdrop(airdrop);
		} catch (Exception e) {
			return ReturnResult.FAILUER("空投活动启用失败!");
		}
		return ReturnResult.SUCCESS("空投活动启用成功！");
	}
	
	/**
	 * 禁用空投活动
	 * @param id
	 * @return
	 */
	@RequestMapping("airdrop/disableAirdrop")
	@ResponseBody
	public ReturnResult disableAirdrop(
			@RequestParam(value = "id", required = false,defaultValue="0") Integer id) {
		Airdrop airdrop = new Airdrop();
		if (id > 0) {
			airdrop = adminAirdropService.selectAirdropById(id);
		}
		if (airdrop == null) {
			return ReturnResult.FAILUER("空投活动不存在!");
		}
		if (airdrop.getStatus() == 0) {
			return ReturnResult.FAILUER("空投活动已处于禁用状态!");
		}
		//启用空投活动
		try {
			airdrop.setStatus(0);
			adminAirdropService.updateAirdrop(airdrop);
		} catch (Exception e) {
			return ReturnResult.FAILUER("空投活动禁用失败!");
		}
		return ReturnResult.SUCCESS("空投活动禁用成功！");
	}
	
	/**
	 * 删除空投活动
	 * @param id
	 * @return
	 */
	@RequestMapping("airdrop/deleteAirdrop")
	@ResponseBody
	public ReturnResult deleteAirdrop(
			@RequestParam(value = "id", required = false,defaultValue="0") Integer id) {
		Airdrop airdrop = new Airdrop();
		if (id > 0) {
			airdrop = adminAirdropService.selectAirdropById(id);
		}
		if (airdrop == null) {
			return ReturnResult.FAILUER("空投活动不存在!");
		}
		//删除空投活动
		try {
			adminAirdropService.deleteAirdrop(airdrop);
		} catch (Exception e) {
			return ReturnResult.FAILUER("空投活动删除失败!");
		}
		return ReturnResult.SUCCESS("空投活动删除成功！");
	}
	
	/**
 	 * 新增空投活动信息
 	 */
	@RequestMapping("/admin/updateAirdrop")
	public ModelAndView updateAirdrop(
			@RequestParam(value="id",required=false,defaultValue="1") Integer id
			) throws Exception {
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("airdrop/updateAirdrop");
		Airdrop airdrop = adminAirdropService.selectAirdropById(id);
		modelAndView.addObject("airdrop", airdrop);
		//查询所有币种
		Map<Integer, String> coinTypeMap = redisHelper.getCoinTypeNameMap();
		modelAndView.addObject("coinTypeMap", coinTypeMap);
		return modelAndView;
	}
	
	/**
	 * 保存空投活动
	 * @param id
	 * @return
	 */
	@RequestMapping("airdrop/saveUpdateAirdrop")
	@ResponseBody
	public ReturnResult saveUpdateAirdrop(
			@RequestParam(value = "id", required = false) Integer id,
			@RequestParam(value = "name", required = false) String name,
			@RequestParam(value = "title", required = false) String title,
			@RequestParam(value = "minCount", required = false) BigDecimal minCount,
			@RequestParam(value = "airdropCoinId", required = false) Integer airdropCoinId,
			@RequestParam(value = "type", required = false) Integer type,
			@RequestParam(value = "countOrRate", required = false) BigDecimal countOrRate,
			@RequestParam(value = "airdropTime", required = false) String airdropTime,
			@RequestParam(value = "startTime", required = false) String startTime,
			@RequestParam(value = "isOpen", required = false) Integer isOpen
			) throws Exception {
		Airdrop airdrop = adminAirdropService.selectAirdropById(id);
		airdrop.setName(name);
		airdrop.setTitle(title);
		airdrop.setMinCount(minCount);
		airdrop.setAirdropCoinId(airdropCoinId);
		airdrop.setType(type);
		airdrop.setCountOrRate(countOrRate);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		if (airdropTime != null && !airdropTime.isEmpty()) {
			airdrop.setAirdropTime(sdf.parse(airdropTime));
		}
		if (startTime != null && !startTime.isEmpty()) {
			airdrop.setStartTime(sdf.parse(startTime));
		}
		airdrop.setIsOpen(isOpen);
		//启用空投活动
		try {
			adminAirdropService.updateAirdrop(airdrop);
		} catch (Exception e) {
			return ReturnResult.FAILUER("更新失败！");
		}
		return ReturnResult.SUCCESS("更新成功！");
	}
}
