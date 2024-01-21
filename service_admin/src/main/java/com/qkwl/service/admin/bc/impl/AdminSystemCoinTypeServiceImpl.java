package com.qkwl.service.admin.bc.impl;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import org.apache.commons.collections4.ListUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.hotcoin.coin.utils.CoinRpcUtlis;
import com.qkwl.common.coin.CoinDriver;
import com.qkwl.common.coin.CoinDriverFactory;
import com.qkwl.common.dto.Enum.UserWalletBalanceLogDirectionEnum;
import com.qkwl.common.dto.Enum.UserWalletBalanceLogFieldEnum;
import com.qkwl.common.dto.Enum.UserWalletBalanceLogTypeEnum;
import com.qkwl.common.dto.coin.FPool;
import com.qkwl.common.dto.coin.SystemCoinInfo;
import com.qkwl.common.dto.coin.SystemCoinSetting;
import com.qkwl.common.dto.coin.SystemCoinType;
import com.qkwl.common.dto.common.Pagination;
import com.qkwl.common.dto.finances.FVirtualFinances;
import com.qkwl.common.dto.wallet.UserCoinWallet;
import com.qkwl.common.dto.wallet.UserWalletBalanceLog;
import com.qkwl.common.exceptions.BCException;
import com.qkwl.common.match.MathUtils;
import com.qkwl.common.rpc.admin.IAdminSystemCoinTypeService;
import com.qkwl.common.util.Constant;
import com.qkwl.common.util.Utils;
import com.qkwl.service.admin.bc.comm.SystemRedisInit;
import com.qkwl.service.admin.bc.dao.FPoolMapper;
import com.qkwl.service.admin.bc.dao.FUserMapper;
import com.qkwl.service.admin.bc.dao.FVirtualFinancesMapper;
import com.qkwl.service.admin.bc.dao.SystemCoinInfoMapper;
import com.qkwl.service.admin.bc.dao.SystemCoinSettingMapper;
import com.qkwl.service.admin.bc.dao.SystemCoinTypeMapper;
import com.qkwl.service.admin.bc.dao.UserCoinWalletMapper;
import com.qkwl.service.admin.bc.dao.UserWalletBalanceLogMapper;

@Service("adminSystemCoinTypeService")
public class AdminSystemCoinTypeServiceImpl implements IAdminSystemCoinTypeService {

	private static final Logger logger = LoggerFactory.getLogger(AdminSystemCoinTypeServiceImpl.class);

	@Autowired
	private SystemCoinTypeMapper systemCoinTypeMapper;

	@Autowired
	private FPoolMapper poolMapper;
	@Autowired
	private SystemRedisInit systemRedisInit;
	@Autowired
	private FVirtualFinancesMapper virtualFinancesMapper;
	@Autowired
	private AdminVirtualCoinServiceTx adminVirtualCoinServiceTx;
	@Autowired
	private SystemCoinSettingMapper systemCoinSettingMapper;
	@Autowired
	private FUserMapper userMapper;
	@Autowired
	private SystemCoinInfoMapper systemCoinInfoMapper;
	@Autowired
	private UserCoinWalletMapper userCoinWalletMapper;
	@Autowired
	private UserWalletBalanceLogMapper userWalletBalanceLogMapper;
	
	//单线程池，仅用于为用户生成钱包使用,防止重复生成币种钱包
	private Executor executor=Executors.newSingleThreadExecutor();

	/**
	 * 获取虚拟币列表
	 * 
	 * @param page 分页实体对象
	 * @param type 虚拟币实体对象
	 * @return 分页实体对象
	 */
	@Override
	public Pagination<SystemCoinType> selectVirtualCoinList(Pagination<SystemCoinType> page, SystemCoinType type) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("offset", page.getOffset());
		map.put("limit", page.getPageSize());
		map.put("keyword", page.getKeyword());
		map.put("orderfield", page.getOrderField());
		map.put("orderdirection", page.getOrderDirection());

		int count = systemCoinTypeMapper.getSystemCoinTypeCount(map);
		if (count > 0) {
			List<SystemCoinType> list = systemCoinTypeMapper.getSystemCoinTypeList(map);
			// 查询总的用户数
			int totalUserAmount = userMapper.countUserListByParam(null);

			list.parallelStream().forEach(e -> {
				int coinUserAmount = userCoinWalletMapper.countUidByCoinId(e.getId());
				BigDecimal a = new BigDecimal(coinUserAmount).divide(new BigDecimal(totalUserAmount), 4,
						BigDecimal.ROUND_HALF_UP);
				DecimalFormat df = new DecimalFormat("0.00%");
				String percent = df.format(a);
				e.setCreateCoinAcountInfo(percent);
			});

			page.setData(list);
		}
		page.setTotalRows(count);

		return page;
	}

	/**
	 * 查询虚拟币基本信息
	 * 
	 * @param id 虚拟币ID
	 * @return 虚拟币实体对象
	 * @see com.qkwl.common.rpc.admin.IAdminSystemCoinTypeService#selectVirtualCoinById(int)
	 */
	@Override
	public SystemCoinType selectVirtualCoinById(int id) {
		return systemCoinTypeMapper.selectByPrimaryKey(id);
	}

	/**
	 * 新增虚拟币
	 * 
	 * @param coin 虚拟币实体对象
	 * @return true：成功，false：失败
	 * @see com.qkwl.common.rpc.admin.IAdminSystemCoinTypeService#insert(com.qkwl.common.dto.coin.SystemCoinType)
	 */
	@Override
	@Transactional(value = "flexibleTransMgr", isolation = Isolation.REPEATABLE_READ, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public boolean insert(SystemCoinType coin) {
			int result = systemCoinTypeMapper.insert(coin);
			if (result <= 0) {
				return false;
			}
			// 更新虚拟币手续费
			for (Integer level : Constant.VIP_LEVEL) {
				SystemCoinSetting setting = new SystemCoinSetting();
				setting.setCoinId(coin.getId());
				setting.setLevelVip(level);
				setting.setWithdrawMax(BigDecimal.ZERO);
				setting.setWithdrawMin(BigDecimal.ZERO);
				setting.setWithdrawFee(BigDecimal.ZERO);
				setting.setIsPercentage(true);
				setting.setWithdrawTimes(0);
				setting.setWithdrawDayLimit(BigDecimal.ZERO);
				setting.setGmtCreate(new Date());
				setting.setGmtModified(new Date());
				setting.setVersion(0);
				systemCoinSettingMapper.insert(setting);
			}
			// 分配虚拟币钱
			logger.info("开始为所有用户分配虚拟币");
			//如果是从币就没必要分配钱包
			if(!coin.getIsSubCoin()) {
				CompletableFuture.runAsync(() -> {
					long begintTime = System.currentTimeMillis();
					List<Integer> fidList = userMapper.selectAllFId();
					List<List<Integer>> partitions = ListUtils.partition(fidList, 1000);
					logger.info("总用户数:{}", fidList.size());
					logger.info("拆分成{}个分片进行插入", partitions.size());
					partitions.forEach(e -> {
						adminVirtualCoinServiceTx.insertCoinWalletBatch(coin.getId(), e);
					});
					long endtime = System.currentTimeMillis();
					logger.info("为用户分配虚拟钱包耗时:{}毫秒", endtime - begintTime);
				}, executor);
			}
			// 跟新redis中的虚拟币列表
			systemRedisInit.initSystemCoinType();
			systemRedisInit.initCoinSetting();
			return true;
	}

	/**
	 * 修改虚拟币基本信息
	 * 
	 * @param coin 虚拟币实体对象
	 * @return true：成功，false：失败
	 * @see com.qkwl.common.rpc.admin.IAdminSystemCoinTypeService#updateVirtualCoin(com.qkwl.common.dto.coin.SystemCoinType)
	 */
	@Override
	@Transactional(value = "flexibleTransMgr", isolation = Isolation.REPEATABLE_READ, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public boolean updateVirtualCoin(SystemCoinType coin) {
		try {
			int result = systemCoinTypeMapper.updateSystemCoinType(coin);
			if (result <= 0) {
				return false;
			}
			boolean updateFage = false;
			for (Integer level : Constant.VIP_LEVEL) {
				SystemCoinSetting feesList = systemCoinSettingMapper.selectSystemCoinSetting(coin.getId(), level);
				if (feesList == null) {
					SystemCoinSetting setting = new SystemCoinSetting();
					setting.setCoinId(coin.getId());
					setting.setLevelVip(level);
					setting.setWithdrawMax(BigDecimal.ZERO);
					setting.setWithdrawMin(BigDecimal.ZERO);
					setting.setWithdrawFee(BigDecimal.ZERO);
					setting.setWithdrawTimes(0);
					setting.setWithdrawDayLimit(BigDecimal.ZERO);
					setting.setGmtCreate(new Date());
					setting.setGmtModified(new Date());
					setting.setVersion(0);
					systemCoinSettingMapper.insert(setting);
					updateFage = true;
				}
			}
			// 跟新redis中的虚拟币列表
			systemRedisInit.initSystemCoinType();
			if (updateFage) {
				systemRedisInit.initCoinSetting();
			}
			return true;
		} catch (Exception e) {
			logger.error("修改币种异常", e);
			return false;
		}

	}

	/**
	 * 启用虚拟币钱包
	 * 
	 * @param coin 虚拟币实体对象
	 * @return true：成功，false：失败
	 */
	public boolean updateVirtualCoinByEnabled(SystemCoinType coin) {
		int result = systemCoinTypeMapper.updateSystemCoinTypeStatus(coin);
		if (result <= 0) {
			return false;
		}
		// 跟新redis中的虚拟币列表
		systemRedisInit.initSystemCoinType();
		return true;
	}

	/**
	 * 修改钱包链接
	 * 
	 * @param coin 虚拟币实体对象
	 * @return true：成功，false：失败
	 * @see com.qkwl.common.rpc.admin.IAdminSystemCoinTypeService#updateVirtualCoinWalletLink(com.qkwl.common.dto.coin.SystemCoinType)
	 */
	@Override
	public boolean updateVirtualCoinWalletLink(SystemCoinType coin) {
		int result = systemCoinTypeMapper.updateSystemCoinTypeLink(coin);
		if (result <= 0) {
			return false;
		}
		boolean updateFage = false;
		for (Integer level : Constant.VIP_LEVEL) {
			SystemCoinSetting feesList = systemCoinSettingMapper.selectSystemCoinSetting(coin.getId(), level);
			if (feesList == null) {
				SystemCoinSetting setting = new SystemCoinSetting();
				setting.setCoinId(coin.getId());
				setting.setLevelVip(level);
				setting.setWithdrawMax(BigDecimal.ZERO);
				setting.setWithdrawMin(BigDecimal.ZERO);
				setting.setWithdrawFee(BigDecimal.ZERO);
				setting.setWithdrawTimes(0);
				setting.setWithdrawDayLimit(BigDecimal.ZERO);
				setting.setGmtCreate(new Date());
				setting.setGmtModified(new Date());
				setting.setVersion(0);
				systemCoinSettingMapper.insert(setting);
				updateFage = true;
			}
		}
		// 跟新redis中的虚拟币列表
		systemRedisInit.initSystemCoinType();
		if (updateFage) {
			systemRedisInit.initCoinSetting();
		}
		return true;
	}

	/*************** 虚拟币地址操作 ****************/

	/**
	 * 查询虚拟币地址数量列表
	 * 
	 * @param page 分页实体对象
	 * @return 分页实体对象
	 * @see com.qkwl.common.rpc.admin.IAdminSystemCoinTypeService#selectVirtualCoinAddressNumList(com.qkwl.common.dto.common.Pagination)
	 */
	@Override
	public Pagination<Map<String, Object>> selectVirtualCoinAddressNumList(Pagination<Map<String, Object>> page) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("offset", page.getOffset());
		map.put("limit", page.getPageSize());
		map.put("keyword", page.getKeyword());
		int count = poolMapper.countVirtualCoinAddressNumList(map);
		if (count > 0) {
			List<Map<String, Object>> addressList = poolMapper.getVirtualCoinAddressNumList(map);
			page.setData(addressList);
		}
		page.setTotalRows(count);

		return page;
	}

	/**
	 * 生成虚拟币地址
	 * 
	 * @param coinType 虚拟币实体对象
	 * @param count    生成数量
	 * @param password 钱包密码
	 * @return 200添加成功,302钱包连接失败，请检查配置信息，303取地址受限，304钱包连接失败，请检查配置信息，未知错误
	 * @see com.qkwl.common.rpc.admin.IAdminSystemCoinTypeService#createVirtualCoinAddress(com.qkwl.common.dto.coin.SystemCoinType,
	 *      int, String)
	 */
	@Override
	public int createVirtualCoinAddress(SystemCoinType coinType, int count, String password) {
		if(coinType.getUseNewWay()) {
			return generateAddressNewWay(coinType, count);
		}else {
			return generateAddressOldWay(coinType, count, password);
		}
	}
	
	//旧方式生成用户地址
	private Integer generateAddressOldWay(SystemCoinType coinType, int count, String password) {
		// get CoinDriver
		CoinDriver coinDriver = new CoinDriverFactory.Builder(coinType.getCoinType(), coinType.getWalletLink(), coinType.getChainLink())
				.accessKey(coinType.getAccessKey()).secretKey(coinType.getSecrtKey()).pass(password).assetId(coinType.getAssetId())
				.sendAccount(coinType.getEthAccount()).contractAccount(coinType.getContractAccount())
				.shortName(coinType.getShortName()).builder().getDriver();

		FPool address = null;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:sss");
		try {
			for (int i = 0; i < count; i++) {
				address = coinDriver.getNewAddress(sdf.format(Utils.getTimestamp()));
				if (address == null) {
					continue;
				}
				try {
					address.setFcoinid(coinType.getId());
					adminVirtualCoinServiceTx.insertPoolInfo(address);
				} catch (Exception e) {
                  logger.error("358 exceptin",e);
				}
			}
			return 200;
		} catch (Exception e) {
		    logger.error("363 exceptin",e);
			return 304;
		} finally {
			try {
				coinDriver.walletLock();
			} catch (Exception e) {
              logger.error("369 exceptin",e);
				return 304;
			}
		}
	}
	
	//新方式生成用户地址
	private Integer generateAddressNewWay(SystemCoinType coinType, int count) {
		for (int i = 0; i < count; i = i+10) {
			try {
				List<FPool> createAddress = CoinRpcUtlis.createAddress(coinType, 10);
				if (createAddress == null || createAddress.size() == 0) {
					continue;
				}
				for (FPool address : createAddress) {
					address.setFcoinid(coinType.getId());
					adminVirtualCoinServiceTx.insertPoolInfo(address);
				}
			} catch (Exception e) {
				logger.error("生成地址异常",e);
			}
		}
		return 200;
	}
	
	
	
	
	
	

	@Override
	public boolean insertVirtualFinances(FVirtualFinances record) {
		if (virtualFinancesMapper.insert(record) <= 0) {
			return false;
		}
		systemRedisInit.initVirtualFinances();
		return true;
	}

	@Override
	public FVirtualFinances selectVirtualFinances(Integer fid) {
		return virtualFinancesMapper.selectByPrimaryKey(fid);
	}

	@Override
	public List<FVirtualFinances> selectVirtualFinancesList(Integer fcoinid, Integer fstate) {
		return virtualFinancesMapper.selectByCoinId(fcoinid, fstate);
	}

	@Override
	public boolean updateVirtualFinances(FVirtualFinances record) {
		if (virtualFinancesMapper.updateByPrimaryKey(record) <= 0) {
			return false;
		}
		systemRedisInit.initVirtualFinances();
		return true;
	}

	@Override
	public boolean deleteVirtualFinances(Integer fid) {
		if (virtualFinancesMapper.deleteByPrimaryKey(fid) <= 0) {
			return false;
		}
		systemRedisInit.initVirtualFinances();
		return true;
	}

	@Override
	public List<SystemCoinSetting> selectSystemCoinSettingList(Integer coinId) {
		return systemCoinSettingMapper.selectListByCoinId(coinId);
	}

	@Override
	@Transactional(value = "flexibleTransMgr", isolation = Isolation.REPEATABLE_READ, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	// @Transactional(isolation = Isolation.REPEATABLE_READ, propagation =
	// Propagation.REQUIRED, rollbackFor = Exception.class)
	public boolean updateSystemCoinSetting(List<SystemCoinSetting> records) throws BCException {
		try {
			if (records == null || records.size() == 0) {
				return false;
			}
			for (SystemCoinSetting systemCoinSetting : records) {
				if (systemCoinSettingMapper.updateByPrimaryKey(systemCoinSetting) < 0) {
					throw new BCException("");
				}
			}
			systemRedisInit.initCoinSetting();
			return true;
		} catch (Exception e) {
			logger.error("修改币种设置异常", e);
			throw new BCException(e.getMessage());
		}
	}

	@Override
	public boolean insertSystemCoinInfo(SystemCoinInfo coinInfo) {
		int insert = systemCoinInfoMapper.insert(coinInfo);
		if (insert > 0) {
			systemRedisInit.initCoinInfo();
			return true;
		}
		return false;
	}

	@Override
	public boolean updateSystemCoinInfo(SystemCoinInfo coinInfo) {
		int update = systemCoinInfoMapper.updateSystemCoinInfo(coinInfo);
		if (update > 0) {
			systemRedisInit.initCoinInfo();
			return true;
		}
		return false;
	}

	@Override
	public boolean deleteSystemCoinInfo(Integer id) {
		int delete = systemCoinInfoMapper.deleteByPrimaryKey(id);
		if (delete > 0) {
			systemRedisInit.initCoinInfo();
			return true;
		}
		return false;
	}

	@Override
	public SystemCoinInfo selectSystemCoinInfo(Integer primaryKey) {
		return systemCoinInfoMapper.selectByPrimaryKey(primaryKey);
	}

	@Override
	public Pagination<SystemCoinInfo> selectSystemCoinInfoList(Map<String, Object> params) {
		Set<Map.Entry<String, Object>> entries = params.entrySet();
		for (Map.Entry<String, Object> entry : entries) {
			System.out.println("key = " + entry.getKey() + " value = " + entry.getValue());
		}
		Pagination<SystemCoinInfo> systemCoinInfoPagination = new Pagination<>();
		systemCoinInfoPagination.setData(systemCoinInfoMapper.getSystemCoinInfoList(params));
		return systemCoinInfoPagination;
	}

	@Override
	public String coinSwitch(Integer oldCoinId, Integer newCoinId) {
		try {
			Integer selectCount = userCoinWalletMapper.selectCount(newCoinId);
			if (selectCount > 0) {
				return "操作失败，新币种已存在客户钱包。";
			}
			boolean creatCoinWallet = adminVirtualCoinServiceTx.creatCoinWallet(oldCoinId, newCoinId);
			if (creatCoinWallet) {
				return null;
			}
			return "执行失败";
		} catch (Exception e) {
			logger.error(
					"AdminSystemCoinTypeServiceImpl.coinSwitch 异常,oldCoinId:" + oldCoinId + ",newCoinId:" + newCoinId,
					e);
			return "系统异常";
		}
	}

	@Transactional(value = "xaTransMgr", isolation = Isolation.REPEATABLE_READ, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@Override
	public boolean unfreezeInnovateArea(Integer coinId) {
		try {
			List<UserCoinWallet> list = userCoinWalletMapper.selectUnfreezenByCoinId(coinId);
			for (UserCoinWallet wallet : list) {
				BigDecimal change = wallet.getDepositFrozen();
				wallet.setTotal(MathUtils.add(wallet.getTotal(), wallet.getDepositFrozen()));
				wallet.setDepositFrozen(BigDecimal.ZERO);
				// 更新钱包
				if (userCoinWalletMapper.updateByPrimaryKey(wallet) == 0) {
					logger.error("创新区解冻----用户账户钱包更新出错uid = " + wallet.getId());
					return false;
				}
				// 记录钱包操作日志
				UserWalletBalanceLog userWalletBalanceLog = new UserWalletBalanceLog();
				userWalletBalanceLog.setCoinId(wallet.getCoinId());
				userWalletBalanceLog.setChange(change);
				userWalletBalanceLog.setCreatedatestamp(new Date().getTime());
				userWalletBalanceLog.setCreatetime(new Date());
				userWalletBalanceLog.setDirection(UserWalletBalanceLogDirectionEnum.in.getValue());
				userWalletBalanceLog.setFieldId(UserWalletBalanceLogFieldEnum.total.getValue());
				userWalletBalanceLog.setSrcId(0);
				userWalletBalanceLog.setSrcType(UserWalletBalanceLogTypeEnum.Freezing_of_Innovation_Zone.getCode());
				userWalletBalanceLog.setUid(wallet.getUid());
				if (userWalletBalanceLogMapper.insert(userWalletBalanceLog) <= 0) {
					logger.error("创新区解冻----用户账户钱包新增日志出错id = " + wallet.getId());
					return false;
				}
			}
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	@Override
	public void generateCoinAccount(Integer coinId) {
		// 分配虚拟币钱
		logger.info("开始为部分用户分配虚拟币");
		CompletableFuture.runAsync(() -> {
			long begintTime = System.currentTimeMillis();
			// 查询已经生成该币种的所有用户
			List<Integer> haveCoinUidList = userCoinWalletMapper.findAllUidByCoinId(coinId);
			// 查询所有用户
			List<Integer> allUidList = userMapper.selectAllFId();
			logger.info("总用户数{}",allUidList.size());
			// 取差集
			allUidList.removeAll(haveCoinUidList);
			// 批量插入
			List<List<Integer>> partitions = ListUtils.partition(allUidList, 1000);
			logger.info("没有分配钱包的用户数:{}", allUidList.size());
			logger.info("没有分配钱包的用户拆分成{}个分片进行插入", partitions.size());
			partitions.forEach(e -> {
				adminVirtualCoinServiceTx.insertCoinWalletBatch(coinId, e);
			});
			long endtime = System.currentTimeMillis();
			logger.info("为用户分配虚拟钱包耗时:{}毫秒", endtime - begintTime);
		}, executor);
	}

}
