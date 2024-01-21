package com.qkwl.admin.layui.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.qkwl.common.dto.common.Pagination;
import com.qkwl.common.dto.dict.DictGroup;
import com.qkwl.common.dto.dict.DictItem;
import com.qkwl.common.dto.dict.DictItemAttr;
import com.qkwl.common.rpc.admin.IAdminDictService;
import com.qkwl.common.util.Constant;
import com.qkwl.common.util.ReturnResult;

@Controller
public class DictController {

	/**
	 * 分页大小
	 */	
 	private int numPerPage = Constant.adminPageSize;
 	
 	@Autowired
 	IAdminDictService adminDictService;
 	
 	/**
 	 * 查询字典组
 	 */
	@RequestMapping("/admin/dictGroupList")
	public ModelAndView dictGroupList(
			@RequestParam(value="pageNum",required=false,defaultValue="1") Integer currentPage
			) throws Exception {
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("dict/dictGroupList");
		// 定义查询条件
		Pagination<DictGroup> pageParam = new Pagination<DictGroup>(currentPage, numPerPage);
		
		//查询字典组列表
		Pagination<DictGroup> pagination = adminDictService.selectDictGroupPageList(pageParam);
		
		modelAndView.addObject("dictGroupList", pagination);
		return modelAndView;
	}
	
	/**
	 * 删除字典组
	 * @param Id
	 * @return
	 */
	@RequestMapping("/admin/deleteDictGroup")
	@ResponseBody
	public ReturnResult deleteDictGroup(
			@RequestParam(value = "Id", required = false,defaultValue="0") Integer Id) {
		DictGroup dictGroup = new DictGroup();
		if (Id > 0) {
			dictGroup = adminDictService.selectDictGroupById(Id);
		}
		if (dictGroup == null) {
			return ReturnResult.FAILUER("字典组不存在!");
		}
		//删除字典组
		return adminDictService.deleteDictGroup(dictGroup);
	}
	
	/**
	 * 开启字典组
	 * @param Id
	 * @return
	 */
	@RequestMapping("/admin/openDictGroup")
	@ResponseBody
	public ReturnResult openDictGroup(
			@RequestParam(value = "Id", required = false,defaultValue="0") Integer Id) {
		DictGroup dictGroup = new DictGroup();
		if (Id > 0) {
			dictGroup = adminDictService.selectDictGroupById(Id);
		}
		if (dictGroup == null) {
			return ReturnResult.FAILUER("字典组不存在!");
		}
		dictGroup.setStatus(1);
		//开启字典组
		return adminDictService.updateDictGroup(dictGroup);
	}
	
	/**
	 * 禁用字典组
	 * @param Id
	 * @return
	 */
	@RequestMapping("/admin/closeDictGroup")
	@ResponseBody
	public ReturnResult closeDictGroup(
			@RequestParam(value = "Id", required = false,defaultValue="0") Integer Id) {
		DictGroup dictGroup = new DictGroup();
		if (Id > 0) {
			dictGroup = adminDictService.selectDictGroupById(Id);
		}
		if (dictGroup == null) {
			return ReturnResult.FAILUER("字典组不存在!");
		}
		dictGroup.setStatus(0);
		//禁用字典组
		return adminDictService.updateDictGroup(dictGroup);
	}
	
	/**
	 * 添加字典组
	 * @return
	 */
	@RequestMapping("/admin/addDictGroup")
	public ModelAndView addDictGroup(){
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("dict/addDictGroup");
		return modelAndView;
	}
	
	/**
     * 保存新增的字典组
     */
    @RequestMapping("admin/saveDictGroup")
    @ResponseBody
    public ReturnResult saveDictGroup(
    		@RequestParam(value = "groupid", required = true) String groupid,
            @RequestParam(value = "groupname", required = true) String groupname
            ) throws Exception {
    	DictGroup dictGroup = new DictGroup();
    	dictGroup.setGroupid(groupid);
    	dictGroup.setGroupname(groupname);
		dictGroup.setStatus(0);
        return adminDictService.insertDictGroup(dictGroup);
    }
	
	/**
	 * 读取修改的字典组
	 * @return
	 */
	@RequestMapping("/admin/changeDictGroup")
	public ModelAndView changeDictGroup(@RequestParam(value="Id",required=false) Integer Id){
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("dict/changeDictGroup");
		if (Id > 0) {
			DictGroup dictGroup = adminDictService.selectDictGroupById(Id);
			modelAndView.addObject("dictGroup", dictGroup);
		}
		return modelAndView;
	}
	
	/**
     * 修改字典组
     */
    @RequestMapping("admin/updateDictGroup")
    @ResponseBody
    public ReturnResult updateDictGroup(
    		@RequestParam(value = "Id", required = true,defaultValue = "0") Integer Id,
    		@RequestParam(value = "groupid", required = true) String groupid,
            @RequestParam(value = "groupname", required = true) String groupname
            ) throws Exception {
    	DictGroup dictGroup = new DictGroup();
		if (Id > 0) {
			dictGroup = adminDictService.selectDictGroupById(Id);
		}
		if (dictGroup == null) {
			return ReturnResult.FAILUER("字典组不存在!");
		}
		dictGroup.setGroupid(groupid);
		dictGroup.setGroupname(groupname);
		
        return adminDictService.updateDictGroup(dictGroup);
    }
    
    /**
 	 * 查询字典定义
 	 */
	@RequestMapping("/admin/dictItemList")
	public ModelAndView dictItemList(
			@RequestParam(value="pageNum",required=false,defaultValue="1") Integer currentPage
			) throws Exception {
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("dict/dictItemList");
		// 定义查询条件
		Pagination<DictItem> pageParam = new Pagination<DictItem>(currentPage, numPerPage);
		
		//查询字典定义列表
		Pagination<DictItem> pagination = adminDictService.selectDictItemPageList(pageParam);
		
		modelAndView.addObject("dictItemList", pagination);
		return modelAndView;
	}
	
	/**
	 * 删除字典定义
	 * @param Id
	 * @return
	 */
	@RequestMapping("/admin/deleteDictItem")
	@ResponseBody
	public ReturnResult deleteDictItem(
			@RequestParam(value = "Id", required = false,defaultValue="0") Integer Id) {
		DictItem dictItem = new DictItem();
		if (Id > 0) {
			dictItem = adminDictService.selectDictItemById(Id);
		}
		if (dictItem == null) {
			return ReturnResult.FAILUER("字典定义不存在!");
		}
		//删除字典定义
		return adminDictService.deleteDictItem(dictItem);
	}
	
	/**
	 * 开启字典定义
	 * @param Id
	 * @return
	 */
	@RequestMapping("/admin/openDictItem")
	@ResponseBody
	public ReturnResult openDictItem(
			@RequestParam(value = "Id", required = false,defaultValue="0") Integer Id) {
		DictItem dictItem = new DictItem();
		if (Id > 0) {
			dictItem = adminDictService.selectDictItemById(Id);
		}
		if (dictItem == null) {
			return ReturnResult.FAILUER("字典定义不存在!");
		}
		dictItem.setStatus(1);
		//开启字典定义
		return adminDictService.updateDictItem(dictItem);
	}
	
	/**
	 * 禁用字典定义
	 * @param Id
	 * @return
	 */
	@RequestMapping("/admin/closeDictItem")
	@ResponseBody
	public ReturnResult closeDictItem(
			@RequestParam(value = "Id", required = false,defaultValue="0") Integer Id) {
		DictItem dictItem = new DictItem();
		if (Id > 0) {
			dictItem = adminDictService.selectDictItemById(Id);
		}
		if (dictItem == null) {
			return ReturnResult.FAILUER("字典定义不存在!");
		}
		dictItem.setStatus(0);
		//禁用字典定义
		return adminDictService.updateDictItem(dictItem);
	}
	
	/**
	 * 添加字典定义
	 * @return
	 */
	@RequestMapping("/admin/addDictItem")
	public ModelAndView addDictItem(){
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("dict/addDictItem");
		return modelAndView;
	}
	
	/**
     * 保存新增的字典定义
     */
    @RequestMapping("admin/saveDictItem")
    @ResponseBody
    public ReturnResult saveDictItem(
    		@RequestParam(value = "dictId", required = true) String dictId,
            @RequestParam(value = "dictName", required = true) String dictName,
            @RequestParam(value = "groupId", required = true) String groupId,
            @RequestParam(value = "sortOrder", required = true) Integer sortOrder
            ) throws Exception {
    	DictItem dictItem = new DictItem();
    	dictItem.setDictId(dictId);
    	dictItem.setDictName(dictName);
    	dictItem.setGroupId(groupId);
    	dictItem.setSortOrder(sortOrder);
		dictItem.setStatus(0);
        return adminDictService.insertDictItem(dictItem);
    }
	
	/**
	 * 读取修改的字典定义
	 * @return
	 */
	@RequestMapping("/admin/changeDictItem")
	public ModelAndView changeDictItem(@RequestParam(value="Id",required=false) Integer Id){
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("dict/changeDictItem");
		if (Id > 0) {
			DictItem dictItem = adminDictService.selectDictItemById(Id);
			modelAndView.addObject("dictItem", dictItem);
		}
		return modelAndView;
	}
	
	/**
     * 修改字典定义
     */
    @RequestMapping("admin/updateDictItem")
    @ResponseBody
    public ReturnResult updateDictItem(
    		@RequestParam(value = "Id", required = true,defaultValue = "0") Integer Id,
    		@RequestParam(value = "dictId", required = true) String dictId,
            @RequestParam(value = "dictName", required = true) String dictName,
            @RequestParam(value = "groupId", required = true) String groupId,
            @RequestParam(value = "sortOrder", required = true) Integer sortOrder
            ) throws Exception {
    	DictItem dictItem = new DictItem();
		if (Id > 0) {
			dictItem = adminDictService.selectDictItemById(Id);
		}
		if (dictItem == null) {
			return ReturnResult.FAILUER("字典定义不存在!");
		}
		dictItem.setDictId(dictId);
		dictItem.setDictName(dictName);
		dictItem.setGroupId(groupId);
		dictItem.setSortOrder(sortOrder);
		
        return adminDictService.updateDictItem(dictItem);
    }
    
    /**
 	 * 查询字典定义
 	 */
	@RequestMapping("/admin/dictItemAttrList")
	public ModelAndView dictItemAttrList(
			@RequestParam(value="pageNum",required=false,defaultValue="1") Integer currentPage
			) throws Exception {
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("dict/dictItemAttrList");
		// 定义查询条件
		Pagination<DictItemAttr> pageParam = new Pagination<DictItemAttr>(currentPage, numPerPage);
		
		//查询字典定义列表
		Pagination<DictItemAttr> pagination = adminDictService.selectDictItemAttrPageList(pageParam);
		
		modelAndView.addObject("dictItemAttrList", pagination);
		return modelAndView;
	}
	
	/**
	 * 删除字典定义
	 * @param Id
	 * @return
	 */
	@RequestMapping("/admin/deleteDictItemAttr")
	@ResponseBody
	public ReturnResult deleteDictItemAttr(
			@RequestParam(value = "Id", required = false,defaultValue="0") Integer Id) {
		DictItemAttr dictItemAttr = new DictItemAttr();
		if (Id > 0) {
			dictItemAttr = adminDictService.selectDictItemAttrById(Id);
		}
		if (dictItemAttr == null) {
			return ReturnResult.FAILUER("字典定义不存在!");
		}
		//删除字典定义
		return adminDictService.deleteDictItemAttr(dictItemAttr);
	}
	
	/**
	 * 添加字典定义
	 * @return
	 */
	@RequestMapping("/admin/addDictItemAttr")
	public ModelAndView addDictItemAttr(){
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("dict/addDictItemAttr");
		return modelAndView;
	}
	
	/**
     * 保存新增的字典属性
     */
    @RequestMapping("admin/saveDictItemAttr")
    @ResponseBody
    public ReturnResult saveDictItemAttr(
    		@RequestParam(value = "dictitemid", required = true) String dictitemid,
            @RequestParam(value = "langtype", required = true) String langtype,
            @RequestParam(value = "dictname", required = true) String dictname
            ) throws Exception {
    	DictItemAttr dictItemAttr = new DictItemAttr();
    	
        return adminDictService.insertDictItemAttr(dictItemAttr);
    }
	
	/**
	 * 读取修改的字典属性
	 * @return
	 */
	@RequestMapping("/admin/changeDictItemAttr")
	public ModelAndView changeDictItemAttr(@RequestParam(value="Id",required=false) Integer Id){
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("dict/changeDictItemAttr");
		if (Id > 0) {
			DictItemAttr dictItemAttr = adminDictService.selectDictItemAttrById(Id);
			modelAndView.addObject("dictItemAttr", dictItemAttr);
		}
		return modelAndView;
	}
	
	/**
     * 修改字典属性
     */
    @RequestMapping("admin/updateDictItemAttr")
    @ResponseBody
    public ReturnResult updateDictItemAttr(
    		@RequestParam(value = "Id", required = true,defaultValue = "0") Integer Id,
    		@RequestParam(value = "dictitemid", required = true) String dictitemid,
            @RequestParam(value = "langtype", required = true) String langtype,
            @RequestParam(value = "dictname", required = true) String dictname
            ) throws Exception {
    	DictItemAttr dictItemAttr = new DictItemAttr();
		if (Id > 0) {
			dictItemAttr = adminDictService.selectDictItemAttrById(Id);
		}
		if (dictItemAttr == null) {
			return ReturnResult.FAILUER("字典属性不存在!");
		}
		dictItemAttr.setDictitemid(dictitemid);
		dictItemAttr.setLangtype(langtype);
		dictItemAttr.setDictname(dictname);
		
        return adminDictService.updateDictItemAttr(dictItemAttr);
    }
}
