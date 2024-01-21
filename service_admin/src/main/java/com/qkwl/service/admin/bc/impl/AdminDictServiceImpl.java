package com.qkwl.service.admin.bc.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.qkwl.common.dto.common.Pagination;
import com.qkwl.common.dto.dict.DictGroup;
import com.qkwl.common.dto.dict.DictItem;
import com.qkwl.common.dto.dict.DictItemAttr;
import com.qkwl.common.rpc.admin.IAdminDictService;
import com.qkwl.common.util.ReturnResult;
import com.qkwl.service.admin.bc.dao.DictGroupMapper;
import com.qkwl.service.admin.bc.dao.DictItemAttrMapper;
import com.qkwl.service.admin.bc.dao.DictItemMapper;

@Service("adminDictService")
public class AdminDictServiceImpl implements IAdminDictService {
	
	private static final Logger logger = LoggerFactory.getLogger(AdminDictServiceImpl.class);
	
	@Autowired
	DictGroupMapper dictGroupMapper;
	@Autowired
	DictItemMapper dictItemMapper;
	@Autowired
	DictItemAttrMapper dictItemAttrMapper;

	@Override
	public Pagination<DictGroup> selectDictGroupPageList(Pagination<DictGroup> pageParam) {
		// 组装查询条件数据
		Map<String,Object> map = new HashMap<String, Object>();
		map.put("offset", pageParam.getOffset());
		map.put("limit", pageParam.getPageSize());
		
		// 查询字典组数
		int count = dictGroupMapper.countDictGroupListByParam(map);
		if(count > 0) {
			// 查询字典组列表
			List<DictGroup> dictGroupList = dictGroupMapper.getDictGroupPageList(map);
			// 设置返回数据
			pageParam.setData(dictGroupList);
		}
		pageParam.setTotalRows(count);
		return pageParam;
	}

	@Override
	public DictGroup selectDictGroupById(Integer Id) {
		return dictGroupMapper.selectByPrimaryKey(Id);
	}

	@Override
	public ReturnResult insertDictGroup(DictGroup dictGroup) {
		try {
			//新增字典组
			dictGroupMapper.insert(dictGroup);
			return ReturnResult.SUCCESS("新增字典组成功！");
		} catch (Exception e) {
			logger.error("新增字典组异常，id："+dictGroup.getId(),e);
			return ReturnResult.FAILUER("新增字典组失败！");
		}
	}

	@Override
	public ReturnResult deleteDictGroup(DictGroup dictGroup) {
		try {
			//删除字典组
			dictGroupMapper.deleteByPrimaryKey(dictGroup.getId());
			return ReturnResult.SUCCESS("删除字典组成功！");
		} catch (Exception e) {
			logger.error("删除字典组异常，id："+dictGroup.getId(),e);
			return ReturnResult.FAILUER("删除字典组失败！");
		}
	}

	@Override
	public ReturnResult updateDictGroup(DictGroup dictGroup) {
		try {
			//更新字典组
			dictGroupMapper.updateByPrimaryKey(dictGroup);
			return ReturnResult.SUCCESS("修改字典组成功！");
		} catch (Exception e) {
			logger.error("更新字典组异常，id："+dictGroup.getId(),e);
			return ReturnResult.FAILUER("修改字典组失败！");
		}
	}

	@Override
	public Pagination<DictItem> selectDictItemPageList(Pagination<DictItem> pageParam) {
		// 组装查询条件数据
		Map<String,Object> map = new HashMap<String, Object>();
		map.put("offset", pageParam.getOffset());
		map.put("limit", pageParam.getPageSize());
		
		// 查询字典组数
		int count = dictItemMapper.countDictItemListByParam(map);
		if(count > 0) {
			// 查询字典定义列表
			List<DictItem> dictItemList = dictItemMapper.getDictItemPageList(map);
			// 设置返回数据
			pageParam.setData(dictItemList);
		}
		pageParam.setTotalRows(count);
		return pageParam;
	}

	@Override
	public DictItem selectDictItemById(Integer Id) {
		return dictItemMapper.selectByPrimaryKey(Id);
	}

	@Override
	public ReturnResult insertDictItem(DictItem dictItem) {
		try {
			//新增字典定义
			dictItemMapper.insert(dictItem);
			return ReturnResult.SUCCESS("新增字典定义成功！");
		} catch (Exception e) {
			logger.error("新增字典定义异常，id："+dictItem.getId(),e);
			return ReturnResult.FAILUER("新增字典定义失败！");
		}
	}

	@Override
	public ReturnResult deleteDictItem(DictItem dictItem) {
		try {
			//删除字典定义
			dictItemMapper.deleteByPrimaryKey(dictItem.getId());
			return ReturnResult.SUCCESS("删除字典定义成功！");
		} catch (Exception e) {
			logger.error("删除字典定义异常，id："+dictItem.getId(),e);
			return ReturnResult.FAILUER("删除字典定义失败！");
		}
	}

	@Override
	public ReturnResult updateDictItem(DictItem dictItem) {
		try {
			//更新字典定义
			dictItemMapper.updateByPrimaryKey(dictItem);
			return ReturnResult.SUCCESS("修改字典定义成功！");
		} catch (Exception e) {
			logger.error("删除字典定义异常，id："+dictItem.getId(),e);
			return ReturnResult.FAILUER("修改字典定义失败！");
		}
	}
	
	@Override
	public Pagination<DictItemAttr> selectDictItemAttrPageList(Pagination<DictItemAttr> pageParam) {
		// 组装查询条件数据
		Map<String,Object> map = new HashMap<String, Object>();
		map.put("offset", pageParam.getOffset());
		map.put("limit", pageParam.getPageSize());
		
		// 查询字典属性
		int count = dictItemAttrMapper.countDictItemAttrListByParam(map);
		if(count > 0) {
			// 查询字典属性列表
			List<DictItemAttr> dictItemAttrList = dictItemAttrMapper.getDictItemAttrPageList(map);
			// 设置返回数据
			pageParam.setData(dictItemAttrList);
		}
		pageParam.setTotalRows(count);
		return pageParam;
	}

	@Override
	public DictItemAttr selectDictItemAttrById(Integer Id) {
		return dictItemAttrMapper.selectByPrimaryKey(Id);
	}

	@Override
	public ReturnResult insertDictItemAttr(DictItemAttr dictItemAttr) {
		try {
			//新增字典组
			dictItemAttrMapper.insert(dictItemAttr);
			return ReturnResult.SUCCESS("新增字典属性成功！");
		} catch (Exception e) {
			logger.error("新增字典属性异常，id："+dictItemAttr.getId(),e);
			return ReturnResult.FAILUER("新增字典属性失败！");
		}
	}

	@Override
	public ReturnResult deleteDictItemAttr(DictItemAttr dictItemAttr) {
		try {
			//删除字典属性
			dictItemAttrMapper.deleteByPrimaryKey(dictItemAttr.getId());
			return ReturnResult.SUCCESS("删除字典属性成功！");
		} catch (Exception e) {
			logger.error("删除字典属性异常，id："+dictItemAttr.getId(),e);
			return ReturnResult.FAILUER("删除字典属性失败！");
		}
	}

	@Override
	public ReturnResult updateDictItemAttr(DictItemAttr dictItemAttr) {
		try {
			//更新字典属性
			dictItemAttrMapper.updateByPrimaryKey(dictItemAttr);
			return ReturnResult.SUCCESS("修改字典属性成功！");
		} catch (Exception e) {
			logger.error("删除字典属性异常，id："+dictItemAttr.getId(),e);
			return ReturnResult.FAILUER("修改字典属性失败！");
		}
	}
}
