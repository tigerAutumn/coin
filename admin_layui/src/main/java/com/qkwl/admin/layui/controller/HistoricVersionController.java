package com.qkwl.admin.layui.controller;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.qkwl.common.dto.common.Pagination;
import com.qkwl.common.dto.system.FSystemArgs;
import com.qkwl.common.dto.system.HistoricVersion;
import com.qkwl.common.rpc.admin.IAdminHistoricVersionService;
import com.qkwl.common.util.Constant;
import com.qkwl.common.util.ReturnResult;

@Controller
public class HistoricVersionController {

	@Autowired
	private IAdminHistoricVersionService adminHistoricVersionService;
	
	/**
	 * 分页大小
	 */	
 	private int numPerPage = Constant.adminPageSize;
 	
 	/**
 	 * 查询otc历史版本信息
 	 */
	@RequestMapping("/admin/historicVersionList")
	public ModelAndView historicVersionList(
			@RequestParam(value="pageNum",required=false,defaultValue="1") Integer currentPage,
			@RequestParam(value="orderField",required=false,defaultValue="create_time") String orderField,
			@RequestParam(value="orderDirection",required=false,defaultValue="desc") String orderDirection
			) throws Exception {
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("historicVersion/historicVersionList");
		// 定义查询条件
		Pagination<HistoricVersion> pageParam = new Pagination<HistoricVersion>(currentPage, numPerPage);
		HistoricVersion historicVersion = new HistoricVersion();
		
		pageParam.setOrderDirection(orderDirection);
		pageParam.setOrderField(orderField);

		//查询佣金列表
		Pagination<HistoricVersion> pagination = adminHistoricVersionService.selectHistoricVersionPageList(pageParam, historicVersion);

		modelAndView.addObject("historicVersionList", pagination);
		return modelAndView;
	}
	
	/**
	 * 删除历史版本
	 * @param id
	 * @return
	 */
	@RequestMapping("/admin/deleteHistoricVersion")
	@ResponseBody
	public ReturnResult deleteHistoricVersion(
			@RequestParam(value = "id", required = false,defaultValue="0") Integer id) {
		HistoricVersion historicVersion = new HistoricVersion();
		if (id > 0) {
			historicVersion = adminHistoricVersionService.selectHistoricVersionById(id);
		}
		if (historicVersion == null) {
			return ReturnResult.FAILUER("历史版本不存在!");
		}
		//删除历史版本
		return adminHistoricVersionService.deleteHistoricVersion(historicVersion);
	}
	
	/**
	 * 添加历史版本
	 * @return
	 */
	@RequestMapping("/admin/addHistoricVersion")
	public ModelAndView addHistoricVersion(){
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("historicVersion/addHistoricVersion");
		return modelAndView;
	}
	
	/**
     * 保存新增的币种信息
     */
    @RequestMapping("admin/saveHistoricVersion")
    @ResponseBody
    public ReturnResult saveHistoricVersion(
            @RequestParam(value = "version", required = false) String version,
            @RequestParam(value = "createTime", required = false) String createTime,
            @RequestParam(value = "androidUrl", required = false) String androidUrl,
            @RequestParam(value = "iosUrl", required = false) String iosUrl
            ) throws Exception {
    	HistoricVersion historicVersion = new HistoricVersion();
    	DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
	    Date time = format.parse(createTime);
    	historicVersion.setVersion(version);
    	historicVersion.setCreateTime(time);
    	historicVersion.setAndroidUrl(androidUrl);
    	historicVersion.setIosUrl(iosUrl);
        return adminHistoricVersionService.insertHistoricVersion(historicVersion);
    }
	
	/**
	 * 读取修改的历史版本信息
	 * @return
	 */
	@RequestMapping("/admin/changeHistoricVersion")
	public ModelAndView changeHistoricVersion(@RequestParam(value="id",required=false) Integer id){
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("historicVersion/changeHistoricVersion");
		if (id > 0) {
			HistoricVersion historicVersion = adminHistoricVersionService.selectHistoricVersionById(id);
			modelAndView.addObject("historicVersion", historicVersion);
		}
		return modelAndView;
	}
	
	/**
     * 修改的币种信息
     */
    @RequestMapping("admin/updateHistoricVersion")
    @ResponseBody
    public ReturnResult updateHistoricVersion(
    		@RequestParam(value = "id", required = true) Integer id,
    		@RequestParam(value = "version", required = false) String version,
            @RequestParam(value = "createTime", required = false) String createTime,
            @RequestParam(value = "androidUrl", required = false) String androidUrl,
            @RequestParam(value = "iosUrl", required = false) String iosUrl
            ) throws Exception {
    	HistoricVersion historicVersion = new HistoricVersion();
		if (id > 0) {
			historicVersion = adminHistoricVersionService.selectHistoricVersionById(id);
		}
		if (historicVersion == null) {
			return ReturnResult.FAILUER("历史版本不存在!");
		}
		DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
	    Date time = format.parse(createTime);
    	historicVersion.setVersion(version);
    	historicVersion.setCreateTime(time);
    	historicVersion.setAndroidUrl(androidUrl);
    	historicVersion.setIosUrl(iosUrl);
		
        return adminHistoricVersionService.updateHistoricVersion(historicVersion);
    }
}
