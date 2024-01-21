package com.qkwl.service.admin.bc.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.qkwl.common.dto.activity.ActivityConfig;
import com.qkwl.common.dto.activity.InnovationAreaActivity;
import com.qkwl.common.dto.activity.NetTradeRankActivity;
import com.qkwl.common.dto.activity.NetTradeRankStatistic;
import com.qkwl.common.dto.activity.RankActivityConfig;
import com.qkwl.common.dto.coin.SystemTradeType;
import com.qkwl.common.dto.common.Pagination;
import com.qkwl.common.dto.wallet.UserWalletBalanceLogDto;
import com.qkwl.common.rpc.admin.IAdminActivityService;
import com.qkwl.common.util.ReturnResult;
import com.qkwl.service.admin.bc.dao.ActivityConfigMapper;
import com.qkwl.service.admin.bc.dao.InnovationAreaActivityMapper;
import com.qkwl.service.admin.bc.dao.NetTradeRankActivityMapper;
import com.qkwl.service.admin.bc.dao.NetTradeRankStatisticMapper;
import com.qkwl.service.admin.bc.dao.SystemTradeTypeMapper;
import com.qkwl.service.admin.bc.dao.UserWalletBalanceLogMapper;

@Service("adminActivityService")
public class AdminActivityServiceImpl implements IAdminActivityService {

	private static final Logger logger = LoggerFactory.getLogger(AdminActivityServiceImpl.class);
	
	@Autowired
	private ActivityConfigMapper activityConfigMapper;
	@Autowired
	private SystemTradeTypeMapper systemTradeTypeMapper;
	@Autowired
	private InnovationAreaActivityMapper innovationAreaActivityMapper;
	@Autowired
	private UserWalletBalanceLogMapper userWalletBalanceLogMapper;
	@Autowired
	private NetTradeRankActivityMapper netTradeRankActivityMapper;
	@Autowired
	private NetTradeRankStatisticMapper netTradeRankStatisticMapper;
	
	@Override
	public Pagination<ActivityConfig> selectActivityPageList(Pagination<ActivityConfig> pageParam, Integer status) {
		// 组装查询条件数据
		Map<String,Object> map = new HashMap<String, Object>();
		map.put("offset", pageParam.getOffset());
		map.put("limit", pageParam.getPageSize());
		map.put("orderField", pageParam.getOrderField());
		map.put("orderDirection", pageParam.getOrderDirection());
		
		map.put("status", status);
		// 查询总返佣数
		int count = activityConfigMapper.countActivityListByParam(map);
		if(count > 0) {
			// 查询返佣列表
			List<ActivityConfig> activityList = activityConfigMapper.getActivityPageList(map);
			// 设置返回数据
			pageParam.setData(activityList);
		}
		pageParam.setTotalRows(count);
		return pageParam;
	}
	
	@Override
	public ActivityConfig selectActivityById(Integer id) {
		return activityConfigMapper.getActivityById(id);
	}
	
	@Override
	public ReturnResult deleteActivity(ActivityConfig activity) {
		try {
			//删除活动
			activityConfigMapper.deleteActivity(activity);
			return ReturnResult.SUCCESS("删除活动成功！");
		} catch (Exception e) {
			logger.error("删除活动异常，id："+activity.getId(),e);
			return ReturnResult.FAILUER("删除活动失败！");
		}
	}
	
	@Override
	public ReturnResult insertActivity(ActivityConfig activity) {
		try {
			//删除活动
			activityConfigMapper.insertActivity(activity);
			return ReturnResult.SUCCESS("新增活动成功！");
		} catch (Exception e) {
			logger.error("新增活动异常，id："+activity.getId(),e);
			return ReturnResult.FAILUER("新增活动失败！");
		}
	}
	
	@Override
	public ReturnResult updateActivity(ActivityConfig activity) {
		try {
			//删除活动
			activityConfigMapper.updateActivity(activity);
			return ReturnResult.SUCCESS("修改活动成功！");
		} catch (Exception e) {
			logger.error("删除活动异常，id："+activity.getId(),e);
			return ReturnResult.FAILUER("修改活动失败！");
		}
	}
	
	@Override
	public Pagination<RankActivityConfig> selectRankActivityPageList(Pagination<RankActivityConfig> pageParam) {
		// 组装查询条件数据
		Map<String,Object> map = new HashMap<String, Object>();
		map.put("offset", pageParam.getOffset());
		map.put("limit", pageParam.getPageSize());
		
		// 查询总交易排行数
		int count = activityConfigMapper.countRankActivityListByParam(map);
		if(count > 0) {
			// 查询交易排行列表
			List<RankActivityConfig> activityList = activityConfigMapper.getRankActivityPageList(map);
			for (RankActivityConfig rankActivityConfig : activityList) {
				SystemTradeType tradeType = systemTradeTypeMapper.selectTradeType(rankActivityConfig.getTradeId());
				String tradeName = tradeType.getSellShortName() + "/" + tradeType.getBuyShortName();
				rankActivityConfig.setTradeName(tradeName);
			}
			// 设置返回数据
			pageParam.setData(activityList);
		}
		pageParam.setTotalRows(count);
		return pageParam;
	}
	
	@Override
	public RankActivityConfig selectRankActivityById(Integer id) {
		return activityConfigMapper.getRankActivityById(id);
	}
	
	@Override
	public ReturnResult insertRankActivity(RankActivityConfig activity) {
		try {
			//删除活动
			activityConfigMapper.insertRankActivity(activity);
			return ReturnResult.SUCCESS("新增活动成功！");
		} catch (Exception e) {
			logger.error("新增活动异常，id："+activity.getId(),e);
			return ReturnResult.FAILUER("新增活动失败！");
		}
	}
	
	@Override
	public ReturnResult updateRankActivity(RankActivityConfig activity) {
		try {
			//删除活动
			activityConfigMapper.updateRankActivity(activity);
			return ReturnResult.SUCCESS("修改活动成功！");
		} catch (Exception e) {
			logger.error("删除活动异常，id："+activity.getId(),e);
			return ReturnResult.FAILUER("修改活动失败！");
		}
	}

	@Override
	public void insertInnovationActivity(InnovationAreaActivity innovationAreaActitity) {
		try {
			innovationAreaActivityMapper.insert(innovationAreaActitity);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void updateInnovationActivity(InnovationAreaActivity innovationAreaActitity) {
		innovationAreaActivityMapper.updateByPrimaryKeySelective(innovationAreaActitity);
	}

	@Override
	public PageInfo<InnovationAreaActivity> listInnovationActivity(Map<String, Object> params,int pageNum,int pageSize) {
		try {
			PageHelper.startPage(pageNum, pageSize);
			List<InnovationAreaActivity> list = innovationAreaActivityMapper.listInnovationActivity(params);
			PageInfo<InnovationAreaActivity> pageInfo = new PageInfo<InnovationAreaActivity>(list);
			return pageInfo;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public InnovationAreaActivity selectInnovationById(Long id) {
		return innovationAreaActivityMapper.selectByPrimaryKey(id);
	}

	@Override
	public PageInfo<UserWalletBalanceLogDto> listActivityLog(Map<String, Object> params, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		List<UserWalletBalanceLogDto> list = userWalletBalanceLogMapper.selectList(params);
		PageInfo<UserWalletBalanceLogDto> pageInfo = new PageInfo<UserWalletBalanceLogDto>(list);
		return pageInfo;
	}
	
	@Override
	public Pagination<NetTradeRankActivity> selectNetTradeRankActivityPageList(Pagination<NetTradeRankActivity> pageParam) {
		// 组装查询条件数据
		Map<String,Object> map = new HashMap<String, Object>();
		map.put("offset", pageParam.getOffset());
		map.put("limit", pageParam.getPageSize());
		
		// 查询总交易排行数
		int count = netTradeRankActivityMapper.countByParam(map);
		if(count > 0) {
			// 查询交易排行列表
			List<NetTradeRankActivity> activityList = netTradeRankActivityMapper.getPageList(map);
			Date now = new Date();
			for (NetTradeRankActivity activity : activityList) {
				SystemTradeType tradeType = systemTradeTypeMapper.selectTradeType(activity.getTradeId());
				String tradeName = tradeType.getSellShortName() + "/" + tradeType.getBuyShortName();
				activity.setTradeName(tradeName);
				if (now.before(activity.getStartTime())) {
					activity.setStatus(1);
				} else if (now.after(activity.getEndTime())) {
					activity.setStatus(3);
				} else {
					activity.setStatus(2);
				}
			}
			// 设置返回数据
			pageParam.setData(activityList);
		}
		pageParam.setTotalRows(count);
		return pageParam;
	}
	
	@Override
	public NetTradeRankActivity selectNetTradeRankActivityById(Integer id) {
		return netTradeRankActivityMapper.getById(id);
	}
	
	@Override
	public ReturnResult insertNetTradeRankActivity(NetTradeRankActivity activity) {
		try {
			//删除活动
			netTradeRankActivityMapper.insertNetTradeRankActivity(activity);
			return ReturnResult.SUCCESS("新增活动成功！");
		} catch (Exception e) {
			logger.error("新增活动异常，id："+activity.getId(),e);
			return ReturnResult.FAILUER("新增活动失败！");
		}
	}
	
	@Override
	public ReturnResult updateNetTradeRankActivity(NetTradeRankActivity activity) {
		try {
			//删除活动
			netTradeRankActivityMapper.updateNetTradeRankActivity(activity);
			return ReturnResult.SUCCESS("修改活动成功！");
		} catch (Exception e) {
			logger.error("删除活动异常，id："+activity.getId(),e);
			return ReturnResult.FAILUER("修改活动失败！");
		}
	}
	
	@Override
	public Pagination<NetTradeRankStatistic> selectNetTradeRankStatisticList(Pagination<NetTradeRankStatistic> pageParam, Integer activityId) {
		// 组装查询条件数据
		Map<String,Object> map = new HashMap<String, Object>();
		map.put("offset", pageParam.getOffset());
		map.put("limit", pageParam.getPageSize());
		map.put("activityId", activityId);
		NetTradeRankActivity activity = netTradeRankActivityMapper.getById(activityId);
		map.put("minPosition", activity.getMinPosition());
		
		// 查询总交易排行数
		int count = netTradeRankStatisticMapper.countByParam(map);
		if(count > 0) {
			// 查询交易排行列表
			List<NetTradeRankStatistic> statisticList = netTradeRankStatisticMapper.getPageList(map);
			// 设置返回数据
			pageParam.setData(statisticList);
		}
		pageParam.setTotalRows(count);
		return pageParam;
	}
}
