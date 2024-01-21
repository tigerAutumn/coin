package com.qkwl.common.rpc.admin;

import java.util.Map;

import com.github.pagehelper.PageInfo;
import com.qkwl.common.dto.activity.ActivityConfig;
import com.qkwl.common.dto.activity.InnovationAreaActivity;
import com.qkwl.common.dto.activity.NetTradeRankActivity;
import com.qkwl.common.dto.activity.NetTradeRankStatistic;
import com.qkwl.common.dto.activity.RankActivityConfig;
import com.qkwl.common.dto.common.Pagination;
import com.qkwl.common.dto.wallet.UserWalletBalanceLog;
import com.qkwl.common.dto.wallet.UserWalletBalanceLogDto;
import com.qkwl.common.util.ReturnResult;

public interface IAdminActivityService {

	/**
	 * 分页查询活动
	 * @param pageParam 分页参数
	 * @param status 活动状态
	 * @return 分页查询记录列表
	 */
	public Pagination<ActivityConfig> selectActivityPageList(Pagination<ActivityConfig> pageParam, Integer status);
	
	/**
	 * 根据id查询活动
	 * @param 
	 * @return 活动记录
	 */
	public ActivityConfig selectActivityById(Integer id);
	
	/**
     * 新增活动
     *
     * @param activity 活动
     * @return true：成功，false：失败
     */
	public ReturnResult insertActivity(ActivityConfig activity);
	
	/**
	 * 删除活动
	 * @param commission
	 */
	public ReturnResult deleteActivity(ActivityConfig activity);
	
	/**
	 * 修改活动
	 * @param commission
	 */
	public ReturnResult updateActivity(ActivityConfig activity);
	
	/**
	 * 分页查询交易排行活动
	 * @param pageParam 分页参数
	 * @param status 活动状态
	 * @return 分页查询记录列表
	 */
	public Pagination<RankActivityConfig> selectRankActivityPageList(Pagination<RankActivityConfig> pageParam);
	
	/**
	 * 根据id查询活动
	 * @param 
	 * @return 活动记录
	 */
	public RankActivityConfig selectRankActivityById(Integer id);
	
	/**
     * 新增活动
     *
     * @param activity 活动
     * @return true：成功，false：失败
     */
	public ReturnResult insertRankActivity(RankActivityConfig activity);
	
	/**
	 * 修改活动
	 * @param commission
	 */
	public ReturnResult updateRankActivity(RankActivityConfig activity);
	
	
	/**
	 * 创新区奖励活动
	 * 2019年1月21日15:29:02
	 */
	/**
	 * 添加创新区活动
	 * @param innovationAreaActitity
	 */
	public void insertInnovationActivity(InnovationAreaActivity innovationAreaActitity);
	
	/**
	 * 更改创新区活动
	 */
	public void updateInnovationActivity(InnovationAreaActivity innovationAreaActitity);
	
	/**
	 * 查询创新区活动
	 */
	public PageInfo<InnovationAreaActivity> listInnovationActivity(Map<String, Object> params,int pageNum,int pageSize);
	
	
	public InnovationAreaActivity selectInnovationById(Long id);
	
	public PageInfo<UserWalletBalanceLogDto> listActivityLog(Map<String, Object> params,int pageNum,int pageSize);
	
	/**
	 * 分页查询交易排行活动
	 * @param pageParam 分页参数
	 * @param status 活动状态
	 * @return 分页查询记录列表
	 */
	public Pagination<NetTradeRankActivity> selectNetTradeRankActivityPageList(Pagination<NetTradeRankActivity> pageParam);
	
	/**
	 * 根据id查询活动
	 * @param 
	 * @return 活动记录
	 */
	public NetTradeRankActivity selectNetTradeRankActivityById(Integer id);
	
	/**
     * 新增活动
     *
     * @param activity 活动
     * @return true：成功，false：失败
     */
	public ReturnResult insertNetTradeRankActivity(NetTradeRankActivity activity);
	
	/**
	 * 修改活动
	 * @param commission
	 */
	public ReturnResult updateNetTradeRankActivity(NetTradeRankActivity activity);
	
	/**
	 * 分页查询交易排行活动
	 * @param pageParam 分页参数
	 * @param status 活动状态
	 * @return 分页查询记录列表
	 */
	public Pagination<NetTradeRankStatistic> selectNetTradeRankStatisticList(Pagination<NetTradeRankStatistic> pageParam, Integer activityId);
}
