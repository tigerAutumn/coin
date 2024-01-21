package com.qkwl.admin.layui.controller;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageInfo;
import com.qkwl.admin.layui.base.WebBaseController;
import com.qkwl.admin.layui.utils.FormatUtils;
import com.qkwl.admin.layui.utils.MQSend;
import com.qkwl.common.Excel.XlsExport;
import com.qkwl.common.dto.Enum.CapitalOperationInOutTypeEnum;
import com.qkwl.common.dto.Enum.CapitalOperationInStatus;
import com.qkwl.common.dto.Enum.CapitalOperationOutStatus;
import com.qkwl.common.dto.Enum.EntrustStateEnum;
import com.qkwl.common.dto.Enum.EntrustTypeEnum;
import com.qkwl.common.dto.Enum.ExcelExportStatusEnum;
import com.qkwl.common.dto.Enum.LogAdminActionEnum;
import com.qkwl.common.dto.Enum.LogUserActionEnum;
import com.qkwl.common.dto.Enum.OperationlogEnum;
import com.qkwl.common.dto.Enum.SystemCoinTypeEnum;
import com.qkwl.common.dto.Enum.UserPushStateEnum;
import com.qkwl.common.dto.Enum.UserWalletBalanceLogDirectionEnum;
import com.qkwl.common.dto.Enum.UserWalletBalanceLogTypeEnum;
import com.qkwl.common.dto.Enum.VirtualCapitalOperationInStatusEnum;
import com.qkwl.common.dto.Enum.VirtualCapitalOperationOutStatusEnum;
import com.qkwl.common.dto.Enum.VirtualCapitalOperationTypeEnum;
import com.qkwl.common.dto.admin.FAdmin;
import com.qkwl.common.dto.c2c.UserC2CEntrust;
import com.qkwl.common.dto.capital.AssetImbalance;
import com.qkwl.common.dto.capital.DepositFrozenImbalance;
import com.qkwl.common.dto.capital.FUserPushDTO;
import com.qkwl.common.dto.capital.IncrementBean;
import com.qkwl.common.dto.coin.SystemCoinType;
import com.qkwl.common.dto.common.Pagination;
import com.qkwl.common.dto.entrust.FEntrust;
import com.qkwl.common.dto.entrust.FEntrustHistory;
import com.qkwl.common.dto.excel.ExcelExportTask;
import com.qkwl.common.dto.log.FLogUserAction;
import com.qkwl.common.dto.otc.OtcUserTransfer;
import com.qkwl.common.dto.user.FUser;
import com.qkwl.common.dto.wallet.UserCoinWallet;
import com.qkwl.common.framework.redis.RedisHelper;
import com.qkwl.common.match.MathUtils;
import com.qkwl.common.rpc.admin.IAdminC2CService;
import com.qkwl.common.rpc.admin.IAdminCommissionService;
import com.qkwl.common.rpc.admin.IAdminEntrustServer;
import com.qkwl.common.rpc.admin.IAdminExcelExportTaskService;
import com.qkwl.common.rpc.admin.IAdminIncrementalQueryService;
import com.qkwl.common.rpc.admin.IAdminLogService;
import com.qkwl.common.rpc.admin.IAdminRewardCodeService;
import com.qkwl.common.rpc.admin.IAdminStatisticsService;
import com.qkwl.common.rpc.admin.IAdminUserCapitalService;
import com.qkwl.common.rpc.admin.IAdminUserFinances;
import com.qkwl.common.rpc.admin.IAdminUserService;
import com.qkwl.common.util.Constant;
import com.qkwl.common.util.DateUtils;
import com.qkwl.common.util.HttpRequestUtils;
import com.qkwl.common.util.ReturnResult;

/**
 * Created by ZKF on 2017/4/8.
 */
@Controller
public class AdvancedQueryController extends WebBaseController {
	private static final Logger logger = LoggerFactory.getLogger(AdvancedQueryController.class);

	@Autowired
	private IAdminUserService adminUserService;
	@Autowired
	private IAdminUserCapitalService adminUserCapitalService;
	@Autowired
	private IAdminEntrustServer adminEntrustServer;
	@Autowired
	private IAdminStatisticsService adminStatisticsService;
	@Autowired
	private IAdminRewardCodeService adminRewardCodeService;
	@Autowired
	private IAdminLogService adminLogService;
	@Autowired
	private IAdminUserFinances adminUserFinances;
	@Autowired
	private IAdminC2CService adminC2CService;
	@Autowired
	private RedisHelper redisHelper;
	@Autowired
	private MQSend mqSend;
	@Autowired
	private IAdminCommissionService adminCommissionService;
	@Autowired
	private IAdminIncrementalQueryService adminIncrementalQueryService;
	@Autowired
	private IAdminExcelExportTaskService adminExcelExportTaskService;
	@Resource(name = "taskExecutor")
	private Executor executor;
	@Value("${excel.path}")
	private String excelRootPath;

	@RequestMapping("admin/usercapital_new")
	public ModelAndView usercapitalNew(
			@RequestParam(value = "keyword", required = false, defaultValue = "0") Integer fuid,
			// @RequestParam(value = "rwbegindate",required=false) Date rwbegindate,
			// @RequestParam(value = "rwenddate",required=false) Date rwenddate,
			@RequestParam(value = "showRW", required = false, defaultValue = "true") Boolean showRW,
			@RequestParam(value = "showTrade", required = false, defaultValue = "true") Boolean showTrade,
			@RequestParam(value = "showBalance", required = false, defaultValue = "true") Boolean showBalance) {
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("query/usercapital_new");
		// 判断用户UID
		if (fuid == 0) {
			modelAndView.addObject("showRW", showRW);
			modelAndView.addObject("showTrade", showTrade);
			modelAndView.addObject("showBalance", showBalance);
			return modelAndView;
		}

		FUser user = adminUserService.selectById(fuid);
		// 判断用户是否存在
		if (user == null) {
			modelAndView.addObject("showRW", showRW);
			modelAndView.addObject("showTrade", showTrade);
			modelAndView.addObject("showBalance", showBalance);
			return modelAndView;
		}

		Date nowDate = new Date();

		// 用户钱包
		Map<Integer, UserCoinWallet> walletMap = adminUserCapitalService.selectUserVirtualWallet(fuid);

		// 增量数据
		Map<Integer, IncrementBean> selectUserIncrement = adminIncrementalQueryService.selectUserIncrement(fuid);

		// 该币为gset，btc，eth，usdt时，在该交易区，该币去购买别的币所减少的数量
		Map<Integer, FEntrust> mainCoinBuyPartDealCost = adminEntrustServer.selectCurrentTotalAmountByType(fuid, 
				true,EntrustTypeEnum.BUY.getCode(), EntrustStateEnum.PartDeal.getCode(), null, null, nowDate);
		Map<Integer, FEntrust> mainCoinBuyGoingCost = adminEntrustServer.selectCurrentTotalAmountByType(fuid, 
				true,EntrustTypeEnum.BUY.getCode(), EntrustStateEnum.Going.getCode(), null, null, nowDate);

		// 该币为gset，btc，eth，usdt时，在该交易区，该币卖出的收入
		Map<Integer, FEntrust> mainCoinSellPartDealIncome = adminEntrustServer.selectCurrentTotalAmountByType(fuid,
				true, EntrustTypeEnum.SELL.getCode(), EntrustStateEnum.PartDeal.getCode(), null, null, nowDate);

		// 该币为交易币种，买入该币的收入
		Map<Integer, FEntrust> tradeCoinBuyPartDealIncome = adminEntrustServer.selectCurrentTotalAmountByType(fuid,
				false, EntrustTypeEnum.BUY.getCode(), EntrustStateEnum.PartDeal.getCode(), null, null, nowDate);

		// 该币为交易币种，卖出该币的支出
		Map<Integer, FEntrust> tradeCoinSellPartDealCost = adminEntrustServer.selectCurrentTotalAmountByType(fuid,
				false, EntrustTypeEnum.SELL.getCode(), EntrustStateEnum.PartDeal.getCode(), null, null, nowDate);
		Map<Integer, FEntrust> tradeCoinSellGoingCost = adminEntrustServer.selectCurrentTotalAmountByType(fuid, 
				false,EntrustTypeEnum.SELL.getCode(), EntrustStateEnum.Going.getCode(), null, null, nowDate);

		// 虚拟币充提
		Map<Integer, BigDecimal> VirtualWalletRecharge = adminUserCapitalService.selectVirtualWalletTotalAmountMap(fuid,
				VirtualCapitalOperationTypeEnum.COIN_IN.getCode(), VirtualCapitalOperationInStatusEnum.SUCCESS, null,
				null, nowDate, false);
		Map<Integer, BigDecimal> VirtualWalletWithdraw = adminUserCapitalService.selectVirtualWalletTotalAmountMap(fuid,
				VirtualCapitalOperationTypeEnum.COIN_OUT.getCode(),
				VirtualCapitalOperationOutStatusEnum.OperationSuccess, null, null, nowDate, null);

		// 虚拟币充值冻结
		Map<Integer, BigDecimal> VirtualWalletRechargeFrozen = adminUserCapitalService
				.selectVirtualWalletTotalAmountMap(fuid, VirtualCapitalOperationTypeEnum.COIN_IN.getCode(),
						VirtualCapitalOperationInStatusEnum.SUCCESS, null, null, nowDate, true);

		// 虚拟币提现冻结
		Map<Integer, BigDecimal> WaitForOperationFrozen = adminUserCapitalService.selectVirtualWalletTotalAmountMap(
				fuid, VirtualCapitalOperationTypeEnum.COIN_OUT.getCode(),
				VirtualCapitalOperationOutStatusEnum.WaitForOperation, null, null, nowDate, null);
		
		Map<Integer, BigDecimal> OperationLockFrozen = adminUserCapitalService.selectVirtualWalletTotalAmountMap(fuid,
				VirtualCapitalOperationTypeEnum.COIN_OUT.getCode(), VirtualCapitalOperationOutStatusEnum.OperationLock,
				null, null, nowDate, null);
		Map<Integer, BigDecimal> LockOrderFrozen = adminUserCapitalService.selectVirtualWalletTotalAmountMap(fuid,
				VirtualCapitalOperationTypeEnum.COIN_OUT.getCode(), VirtualCapitalOperationOutStatusEnum.LockOrder,
				null, null, nowDate, null);

		// 手工充值
		Map<Integer, BigDecimal> rechargeWorkMap = adminUserCapitalService
				.selectAdminRechargeVirtualWalletTotalAmount(fuid, OperationlogEnum.AUDIT, null, nowDate);
		Map<Integer, BigDecimal> frozenWorkMap = adminUserCapitalService
				.selectAdminRechargeVirtualWalletTotalAmount(fuid, OperationlogEnum.FFROZEN, null, nowDate);

		// 虚拟币兑换
		Map<Integer, BigDecimal> rewardCodeMap = adminRewardCodeService.selectWalletTotalAmount(fuid, null, null);

		// 返回数据集
		JSONArray array = new JSONArray();
		JSONArray trade = new JSONArray();
		JSONArray balance = new JSONArray();
		JSONArray forzenBalance = new JSONArray();
		// JSONObject balanceObj = new JSONObject();
		JSONArray c2clist = new JSONArray();
		JSONArray depositFrozenList = new JSONArray();
		JSONArray depositFrozenTotalList = new JSONArray();
		JSONArray borrowList = new JSONArray();

		// 虚拟币钱包列表
		List<UserCoinWallet> vwalletList = new ArrayList<UserCoinWallet>();

		// 币种
		List<SystemCoinType> coinList = redisHelper.getCoinTypeListSystem();

		// SystemCoinType cny = redisHelper.getCoinTypeShortNameSystem("CNY");

		// c2c查询
		Map<Integer, UserC2CEntrust> statisticsRechargeWithdrawTotal = adminC2CService
				.statisticsRechargeWithdrawTotal(fuid, null);
		// 空投
		Map<Integer, BigDecimal> sumAirdropRecord = adminUserCapitalService.sumAirdropRecord(fuid);
		// otc
		Map<Integer, OtcUserTransfer> sumOtcTransfer = adminUserCapitalService.sumOtcTransfer(fuid);
		// 资金流水表
		List<Integer> typeList = new ArrayList<>();
		typeList.add(UserWalletBalanceLogTypeEnum.Freezing_of_Innovation_Zone.getCode());
		typeList.add(UserWalletBalanceLogTypeEnum.Dividend_of_Innovation_Zone.getCode());
		typeList.add(UserWalletBalanceLogTypeEnum.Reward_of_Innovation_Zone.getCode());
		typeList.add(UserWalletBalanceLogTypeEnum.Orepool_lock.getCode());
		typeList.add(UserWalletBalanceLogTypeEnum.Orepool_unlock.getCode());
		typeList.add(UserWalletBalanceLogTypeEnum.Orepool_income.getCode());
		typeList.add(UserWalletBalanceLogTypeEnum.Innovation_unfrozen.getCode());
		typeList.add(UserWalletBalanceLogTypeEnum.Innovation_lock.getCode());
		typeList.add(UserWalletBalanceLogTypeEnum.Innovation_unlock.getCode());
		typeList.add(UserWalletBalanceLogTypeEnum.Airdrop_Candy.getCode());
		typeList.add(UserWalletBalanceLogTypeEnum.CLOSE_POSITION.getCode());
		typeList.add(UserWalletBalanceLogTypeEnum.SEND_RED_ENVELOPE.getCode());
		typeList.add(UserWalletBalanceLogTypeEnum.RECEIVE_RED_ENVELOPE.getCode());
		typeList.add(UserWalletBalanceLogTypeEnum.RETURN_RED_ENVELOPE.getCode());
		typeList.add(UserWalletBalanceLogTypeEnum.DEDUCT_RED_ENVELOPE.getCode());
		Map<String, BigDecimal> sumUserWalletBalanceLogMap = adminUserCapitalService.sumUserWalletBalanceLog(fuid,
				typeList, null, nowDate);
		// 循环币种
		for (SystemCoinType coin : coinList) {
			// 查询虚拟钱包
			UserCoinWallet vwallet = walletMap.get(coin.getId());
			if (vwallet == null) {
				continue;
			}
			vwallet.setCoinName(coin.getName());
			vwalletList.add(vwallet);

			// 充提统计
			BigDecimal recharge = BigDecimal.ZERO;
			// BigDecimal zsjfRecharge = BigDecimal.ZERO;
			BigDecimal withdraw = BigDecimal.ZERO;
			BigDecimal withdrawFrozen = BigDecimal.ZERO;

			// PUSH资产
			BigDecimal userPushCoinIn = BigDecimal.ZERO;
			BigDecimal userPushCoinOut = BigDecimal.ZERO;
			BigDecimal userPushCoinFrozen = BigDecimal.ZERO;

			// 买卖交易
			BigDecimal buycount = BigDecimal.ZERO;
			BigDecimal buyamount = BigDecimal.ZERO;
			BigDecimal sellcount = BigDecimal.ZERO;
			BigDecimal sellamount = BigDecimal.ZERO;
			BigDecimal frozenamount = BigDecimal.ZERO;
			BigDecimal frozencount = BigDecimal.ZERO;
			BigDecimal frozencountCoin = BigDecimal.ZERO;
			BigDecimal fee = BigDecimal.ZERO;

			BigDecimal vip6RMB = BigDecimal.ZERO;

			// 佣金
			BigDecimal commission = BigDecimal.ZERO;

			// 币币交易
			BigDecimal coinTradeBuy = BigDecimal.ZERO;
			BigDecimal coinTradeSell = BigDecimal.ZERO;
			BigDecimal coinTradeFee = BigDecimal.ZERO;

			FEntrust currentbuy = null;
			FEntrust currentsell = null;
			FEntrust currentcoinBuy = null;
			FEntrust currentcoinSell = null;
			FEntrust currentSelfCoinBuy = null;
			FEntrust currentSelfCoinSell = null;

			// C2C
			BigDecimal c2cRecharge = BigDecimal.ZERO;
			BigDecimal c2cWithdraw = BigDecimal.ZERO;
			BigDecimal c2cWithdrawFrozen = BigDecimal.ZERO;

			// 理财
			BigDecimal financesCountSend = BigDecimal.ZERO;
			BigDecimal frozenFinances = BigDecimal.ZERO;

			// 空投
			BigDecimal airdrop = sumAirdropRecord.get(coin.getId()) == null ? BigDecimal.ZERO
					: sumAirdropRecord.get(coin.getId());

			// otc
			BigDecimal otcIn = BigDecimal.ZERO;
			BigDecimal otcOut = BigDecimal.ZERO;
			BigDecimal otcFrozen = BigDecimal.ZERO;

			IncrementBean userDayIncrement = getIncrementBean(selectUserIncrement.get(coin.getId()));

			// 创新区交易解冻
			BigDecimal userWalletBalanceLog = sumUserWalletBalanceLogMap
					.get(coin.getId().toString() + UserWalletBalanceLogDirectionEnum.in.getValue() + UserWalletBalanceLogTypeEnum.Freezing_of_Innovation_Zone.getCode());
			BigDecimal unfreeze = userWalletBalanceLog == null ? BigDecimal.ZERO : userWalletBalanceLog;

			// 创新区交易分红
			BigDecimal userWalletBalanceLogDividend = sumUserWalletBalanceLogMap
					.get(coin.getId().toString() + UserWalletBalanceLogDirectionEnum.in.getValue() + UserWalletBalanceLogTypeEnum.Dividend_of_Innovation_Zone.getCode());
			BigDecimal dividend = userWalletBalanceLogDividend == null ? BigDecimal.ZERO : userWalletBalanceLogDividend;

			// 创新区交易奖励
			BigDecimal userWalletBalanceLogReward = sumUserWalletBalanceLogMap
					.get(coin.getId().toString() + UserWalletBalanceLogDirectionEnum.in.getValue() + UserWalletBalanceLogTypeEnum.Reward_of_Innovation_Zone.getCode());
			BigDecimal reward = userWalletBalanceLogReward == null ? BigDecimal.ZERO : userWalletBalanceLogReward;

			// 矿池锁定
			BigDecimal userWalletBalanceLogOrepoolLock = sumUserWalletBalanceLogMap
					.get(coin.getId().toString() + UserWalletBalanceLogDirectionEnum.out.getValue() + UserWalletBalanceLogTypeEnum.Orepool_lock.getCode());
			BigDecimal orepoolLock = userWalletBalanceLogOrepoolLock == null ? BigDecimal.ZERO
					: userWalletBalanceLogOrepoolLock;

			// 矿池解锁
			BigDecimal userWalletBalanceLogOrepoolUnlock = sumUserWalletBalanceLogMap
					.get(coin.getId().toString() + UserWalletBalanceLogDirectionEnum.in.getValue() + UserWalletBalanceLogTypeEnum.Orepool_unlock.getCode());
			BigDecimal orepoolUnlock = userWalletBalanceLogOrepoolUnlock == null ? BigDecimal.ZERO
					: userWalletBalanceLogOrepoolUnlock;

			// 矿池收益
			BigDecimal userWalletBalanceLogOrepoolIncome = sumUserWalletBalanceLogMap
					.get(coin.getId().toString() + UserWalletBalanceLogDirectionEnum.in.getValue() + UserWalletBalanceLogTypeEnum.Orepool_income.getCode());
			BigDecimal orepoolIncome = userWalletBalanceLogOrepoolIncome == null ? BigDecimal.ZERO
					: userWalletBalanceLogOrepoolIncome;
			
			// 创新区存币解冻
			BigDecimal userWalletBalanceLogInnovationUnfrozen = sumUserWalletBalanceLogMap
					.get(coin.getId().toString() + UserWalletBalanceLogDirectionEnum.in.getValue() + UserWalletBalanceLogTypeEnum.Innovation_unfrozen.getCode());
			BigDecimal depositUnfrozen = userWalletBalanceLogInnovationUnfrozen == null ? BigDecimal.ZERO
					: userWalletBalanceLogInnovationUnfrozen;
			// 创新区存币锁定
			BigDecimal userWalletBalanceLogInnovationLock = sumUserWalletBalanceLogMap
					.get(coin.getId().toString() + UserWalletBalanceLogDirectionEnum.out.getValue() + UserWalletBalanceLogTypeEnum.Innovation_lock.getCode());
			BigDecimal innovationLock = userWalletBalanceLogInnovationLock == null ? BigDecimal.ZERO
					: userWalletBalanceLogInnovationLock;
			// 创新区存币解锁
			BigDecimal userWalletBalanceLogInnovationUnlock = sumUserWalletBalanceLogMap
					.get(coin.getId().toString() + UserWalletBalanceLogDirectionEnum.in.getValue() + UserWalletBalanceLogTypeEnum.Innovation_unlock.getCode());
			BigDecimal innovationUnlock = userWalletBalanceLogInnovationUnlock == null ? BigDecimal.ZERO
					: userWalletBalanceLogInnovationUnlock;
			//空投糖果
			BigDecimal userWalletBalanceLogAirdropCandy = sumUserWalletBalanceLogMap
					.get(coin.getId().toString() + UserWalletBalanceLogDirectionEnum.in.getValue() + UserWalletBalanceLogTypeEnum.Airdrop_Candy.getCode());
			BigDecimal airdropCandy = userWalletBalanceLogAirdropCandy == null ? BigDecimal.ZERO
					: userWalletBalanceLogAirdropCandy;
			airdrop = MathUtils.add(airdrop, airdropCandy);
			
			//平仓转出
			BigDecimal userWalletBalanceLogClosePositionOut = sumUserWalletBalanceLogMap
					.get(coin.getId().toString() + UserWalletBalanceLogDirectionEnum.out.getValue() + UserWalletBalanceLogTypeEnum.CLOSE_POSITION.getCode());
			BigDecimal closePositionOut = userWalletBalanceLogClosePositionOut == null ? BigDecimal.ZERO
					: userWalletBalanceLogClosePositionOut;
			
			//平仓转入
			BigDecimal userWalletBalanceLogClosePositionIn = sumUserWalletBalanceLogMap
					.get(coin.getId().toString() + UserWalletBalanceLogDirectionEnum.in.getValue() + UserWalletBalanceLogTypeEnum.CLOSE_POSITION.getCode());
			BigDecimal closePositionIn = userWalletBalanceLogClosePositionIn == null ? BigDecimal.ZERO
					: userWalletBalanceLogClosePositionIn;
			
			//发红包
			BigDecimal userWalletBalanceLogSendRedEnvelope = sumUserWalletBalanceLogMap
					.get(coin.getId().toString() + UserWalletBalanceLogDirectionEnum.out.getValue() + UserWalletBalanceLogTypeEnum.SEND_RED_ENVELOPE.getCode());
			BigDecimal sendRedEnvelope = userWalletBalanceLogSendRedEnvelope == null ? BigDecimal.ZERO
					: userWalletBalanceLogSendRedEnvelope;
			
			//收红包
			BigDecimal userWalletBalanceLogReceiveRedEnvelope = sumUserWalletBalanceLogMap
					.get(coin.getId().toString() + UserWalletBalanceLogDirectionEnum.in.getValue() + UserWalletBalanceLogTypeEnum.RECEIVE_RED_ENVELOPE.getCode());
			BigDecimal receiveRedEnvelope = userWalletBalanceLogReceiveRedEnvelope == null ? BigDecimal.ZERO
					: userWalletBalanceLogReceiveRedEnvelope;
			
			
			//退回红包
			BigDecimal userWalletBalanceLogReturnRedEnvelope = sumUserWalletBalanceLogMap
					.get(coin.getId().toString() + UserWalletBalanceLogDirectionEnum.in.getValue() + UserWalletBalanceLogTypeEnum.RETURN_RED_ENVELOPE.getCode());
			BigDecimal returnRedEnvelope = userWalletBalanceLogReturnRedEnvelope == null ? BigDecimal.ZERO
					: userWalletBalanceLogReturnRedEnvelope;
			
			//红包扣钱
			BigDecimal userWalletBalanceLogDeductRedEnvelope = sumUserWalletBalanceLogMap
					.get(coin.getId().toString() + UserWalletBalanceLogDirectionEnum.out.getValue() + UserWalletBalanceLogTypeEnum.DEDUCT_RED_ENVELOPE.getCode());
			BigDecimal deductRedEnvelope = userWalletBalanceLogDeductRedEnvelope == null ? BigDecimal.ZERO
					: userWalletBalanceLogDeductRedEnvelope;


			if (Constant.CNY_COIN_NAME.equals(coin.getShortName().toUpperCase())) {
				currentbuy = getEntrust(mainCoinBuyPartDealCost.get(coin.getId()));
				currentsell = getEntrust(mainCoinSellPartDealIncome.get(coin.getId()));
				FEntrust frozenEntrust = getEntrust(mainCoinBuyGoingCost.get(coin.getId()));
				frozenamount = MathUtils.sub(currentbuy.getFamount(), currentbuy.getFsuccessamount());
				
				// 币币交易-交易币种-卖-减少
				currentcoinSell = getEntrust(tradeCoinSellPartDealCost.get(coin.getId()));
				//FEntrust currentcoinSellGoing = getEntrust(tradeCoinSellGoingCost.get(coin.getId()));

				// 交易冻结金额
				frozenamount = MathUtils.add(frozenamount, frozenEntrust.getFamount());
				// 市价单冻结
				// buy
				frozenamount = MathUtils.add(frozenamount, currentbuy.getFleftfunds());
				frozenamount = MathUtils.add(frozenamount, frozenEntrust.getFleftfunds());
				/*// sell
				frozenamount = MathUtils.add(frozenamount, currentcoinSell.getFleftfunds());
				frozenamount = MathUtils.add(frozenamount, currentcoinSellGoing.getFleftfunds());*/
				// 人民币充提统计
				recharge = adminUserCapitalService.selectWalletTotalAmount(fuid,
						CapitalOperationInOutTypeEnum.IN.getCode(), CapitalOperationInStatus.Come, null, nowDate);
				withdraw = adminUserCapitalService.selectWalletTotalAmount(fuid,
						CapitalOperationInOutTypeEnum.OUT.getCode(), CapitalOperationOutStatus.OperationSuccess, null,
						nowDate);

				// 冻结
				BigDecimal wf = adminUserCapitalService.selectWalletTotalAmount(fuid,
						CapitalOperationInOutTypeEnum.OUT.getCode(), CapitalOperationOutStatus.WaitForOperation, null,
						nowDate);
				withdrawFrozen = adminUserCapitalService.selectWalletTotalAmount(fuid,
						CapitalOperationInOutTypeEnum.OUT.getCode(), CapitalOperationOutStatus.OperationLock, null,
						nowDate);
				
				
				// 充提冻结
				withdrawFrozen = MathUtils.add(withdrawFrozen, wf);

				// 用法币购买别的币所花费的
				buyamount = MathUtils.add( currentbuy.getFsuccessamount(),userDayIncrement.getTradeCost());
				
				
				// 卖别的币所获得的法币
				sellamount = MathUtils.add( currentsell.getFsuccessamount(),userDayIncrement.getTradeIncome());
				// 总共交易卖的手续费
				fee = MathUtils.add( currentsell.getFfees(),userDayIncrement.getTradeFee());

				// 返佣
				commission = adminCommissionService.selectIssuedAmountByIntroId(fuid);
				if (commission == null) {
					commission = BigDecimal.ZERO;
				}
			} else {

				// 币币交易-主币种-买-减少
				currentSelfCoinBuy = getEntrust(mainCoinBuyPartDealCost.get(coin.getId()));
				FEntrust currentSelfCoinBuyGoing = getEntrust(mainCoinBuyGoingCost.get(coin.getId()));
				// 币币交易-主币种-卖-增加
				currentSelfCoinSell = getEntrust(mainCoinSellPartDealIncome.get(coin.getId()));
				// 币币交易-交易币种-买-增加
				currentcoinBuy = getEntrust(tradeCoinBuyPartDealIncome.get(coin.getId()));
				// 币币交易-交易币种-卖-减少
				currentcoinSell = getEntrust(tradeCoinSellPartDealCost.get(coin.getId()));
				FEntrust currentcoinSellGoing = getEntrust(tradeCoinSellGoingCost.get(coin.getId()));

				frozencountCoin = MathUtils.add(
						MathUtils.sub(currentSelfCoinBuy.getFamount(), currentSelfCoinBuy.getFsuccessamount()),
						MathUtils.sub(currentSelfCoinBuyGoing.getFamount(),
								currentSelfCoinBuyGoing.getFsuccessamount()));
				// 交易冻结数量
				frozencountCoin = MathUtils.add(frozencountCoin,
						MathUtils.add(currentcoinSell.getFleftcount(), currentcoinSellGoing.getFleftcount()));
				// 市价单冻结
				// buy
				frozencountCoin = MathUtils.add(frozencountCoin, currentSelfCoinBuy.getFleftfunds());
				frozencountCoin = MathUtils.add(frozencountCoin, currentSelfCoinBuyGoing.getFleftfunds());
				/*// sell
				frozencountCoin = MathUtils.add(frozencountCoin, currentcoinSell.getFleftfunds());
				frozencountCoin = MathUtils.add(frozencountCoin, currentcoinSellGoing.getFleftfunds());*/

				// 币币交易-花费
				coinTradeBuy = MathUtils.add(MathUtils.add(MathUtils.add(
							currentSelfCoinBuy.getFsuccessamount(),
							currentSelfCoinBuyGoing.getFsuccessamount()),
							currentcoinSell.getFcount()),
							userDayIncrement.getTradeCost());
				// 币币交易-收入
				coinTradeSell = MathUtils.add(MathUtils.add(
								currentSelfCoinSell.getFsuccessamount(),
								currentcoinBuy.getFcount()),
								userDayIncrement.getTradeIncome());

				coinTradeFee = MathUtils.add(MathUtils.add(MathUtils.add(
								currentSelfCoinSell.getFfees(),
								currentSelfCoinBuyGoing.getFfees()),
								currentcoinBuy.getFfees()),
								userDayIncrement.getTradeFee());
			}

			// =====================================================>20181214修改

			// 虚拟币充值
			BigDecimal rechargeV = VirtualWalletRecharge.get(coin.getId()) == null ? BigDecimal.ZERO
					: VirtualWalletRecharge.get(coin.getId());

			// 虚拟币充值冻结
			BigDecimal rechargeFrozen = VirtualWalletRechargeFrozen.get(coin.getId()) == null ? BigDecimal.ZERO
					: VirtualWalletRechargeFrozen.get(coin.getId());

			// 虚拟币充值加人民币充值
			recharge = MathUtils.add(rechargeV, recharge);
			// 虚拟币提现
			BigDecimal withdrawV = VirtualWalletWithdraw.get(coin.getId()) == null ? BigDecimal.ZERO
					: VirtualWalletWithdraw.get(coin.getId());
			// 虚拟币提现加人民币提现
			withdraw = MathUtils.add(withdrawV, withdraw);

			// 冻结
			BigDecimal wf = WaitForOperationFrozen.get(coin.getId()) == null ? BigDecimal.ZERO
					: WaitForOperationFrozen.get(coin.getId());
			withdrawFrozen = MathUtils.add(withdrawFrozen, wf);
			wf = OperationLockFrozen.get(coin.getId()) == null ? BigDecimal.ZERO
					: OperationLockFrozen.get(coin.getId());
			withdrawFrozen = MathUtils.add(withdrawFrozen, wf);
			wf = LockOrderFrozen.get(coin.getId()) == null ? BigDecimal.ZERO : LockOrderFrozen.get(coin.getId());
			// 充提冻结
			withdrawFrozen = MathUtils.add(withdrawFrozen, wf);

			// =====================================================>20181214修改end

			// 手工充值
			BigDecimal rechargeWork = rechargeWorkMap.get(coin.getId()) == null ? BigDecimal.ZERO
					: rechargeWorkMap.get(coin.getId());
			// 手工充值冻结
			BigDecimal frozenWork = frozenWorkMap.get(coin.getId()) == null ? BigDecimal.ZERO
					: frozenWorkMap.get(coin.getId());

			// 虚拟币兑换
			BigDecimal rewardCoin = rewardCodeMap.get(coin.getId()) == null ? BigDecimal.ZERO
					: rewardCodeMap.get(coin.getId());


			// c2c
			if (statisticsRechargeWithdrawTotal != null) {
				UserC2CEntrust statisticsEntrust = statisticsRechargeWithdrawTotal.get(coin.getId());
				if (statisticsEntrust != null) {
					c2cRecharge = statisticsEntrust.getRecharge();
					c2cWithdraw = statisticsEntrust.getWithdraw();
					c2cWithdrawFrozen = statisticsEntrust.getWithdrawFrozen();
				}
			}
			JSONObject c2cObj = new JSONObject();
			c2cObj.put("type", coin.getName());
			c2cObj.put("recharge", c2cRecharge);
			c2cObj.put("withdraw", c2cWithdraw);
			c2clist.add(c2cObj);

			// otc
			OtcUserTransfer otcUserTransfer = sumOtcTransfer.get(coin.getId());
			if (otcUserTransfer != null) {
				otcIn = otcUserTransfer.getAmountIn();
				otcOut = otcUserTransfer.getAmountOut();
				otcFrozen = otcUserTransfer.getAmountFrozen();
			}

			// 判断是否显示充提
			if (showRW) {
				// 虚拟币充提统计
				JSONObject coinObj = new JSONObject();
				coinObj.put("type", coin.getName());
				coinObj.put("recharge", recharge);
				coinObj.put("withdraw", withdraw);
				coinObj.put("rechargeFrozen", rechargeFrozen);
				array.add(coinObj);
			}

			// 创新区冻结平衡
			JSONObject depositFrozen = new JSONObject();
			JSONObject depositFrozenTotal = new JSONObject();
			BigDecimal depositFrozenTemp = MathUtils.add(rechargeFrozen, otcFrozen);
			// 冻结总数是否平衡
			Boolean isdepositFrozenTotal = vwallet.getDepositFrozenTotal().compareTo(depositFrozenTemp) == 0;
			depositFrozenTotal.put("type", coin.getName());
			depositFrozenTotal.put("rechargeFrozen", rechargeFrozen);
			depositFrozenTotal.put("otcTransferFrozen", otcFrozen);
			depositFrozenTotal.put("result", depositFrozenTemp);
			depositFrozenTotal.put("walletResult", vwallet.getDepositFrozenTotal());
			depositFrozenTotal.put("isdepositFrozenTotal", isdepositFrozenTotal);
			depositFrozenTotalList.add(depositFrozenTotal);
			depositFrozenTemp = MathUtils.sub(depositFrozenTemp, unfreeze); //交易解冻
			depositFrozenTemp = MathUtils.sub(depositFrozenTemp, depositUnfrozen); //存币解冻
			// 冻结数量是否平衡
			Boolean isdepositFrozen = vwallet.getDepositFrozen().compareTo(depositFrozenTemp) == 0;
			depositFrozen.put("type", coin.getName());
			depositFrozen.put("rechargeFrozen", rechargeFrozen);
			depositFrozen.put("otcTransferFrozen", otcFrozen);
			depositFrozen.put("unfreeze", unfreeze);
			depositFrozen.put("depositUnfrozen", depositUnfrozen);
			depositFrozen.put("result", depositFrozenTemp);
			depositFrozen.put("walletResult", vwallet.getDepositFrozen());
			depositFrozen.put("isdepositFrozen", isdepositFrozen);
			depositFrozenList.add(depositFrozen);

			// 理财平衡
			JSONObject borrow = new JSONObject();
			BigDecimal borrowCalculation = MathUtils.sub(orepoolLock, orepoolUnlock);
			borrowCalculation = MathUtils.add(borrowCalculation, innovationLock);
			borrowCalculation = MathUtils.sub(borrowCalculation, innovationUnlock);
			Boolean isBorrowBalance = vwallet.getBorrow().compareTo(borrowCalculation) == 0;
			borrow.put("type", coin.getName());
			borrow.put("orepoolLock", orepoolLock);
			borrow.put("orepoolUnlock", orepoolUnlock);
			borrow.put("innovationLock", innovationLock);
			borrow.put("innovationUnlock", innovationUnlock);
			borrow.put("result", borrowCalculation);
			borrow.put("walletResult", vwallet.getBorrow());
			borrow.put("isBorrowBalance", isBorrowBalance);
			borrowList.add(borrow);

			// 判断是否显示交易
			if (showTrade) {
				JSONObject tradeObj = new JSONObject();
				tradeObj.put("type", coin.getName());
				tradeObj.put("buycount", buycount);
				tradeObj.put("buyamount", buyamount);
				tradeObj.put("sellcount", sellcount);
				tradeObj.put("sellamount", sellamount);

				trade.add(tradeObj);
			}

			// 判断是否显示平衡
			if (showBalance) {

				BigDecimal rechargeCoin = recharge;
				BigDecimal withdrawCoin = withdraw;
				BigDecimal frozenCoin = withdrawFrozen;

				// 充值+手工充值+虚拟币兑换-体现
				BigDecimal freeplan = MathUtils.add(rechargeCoin, rechargeWork);
				freeplan = MathUtils.add(freeplan, rewardCoin);
				freeplan = MathUtils.sub(freeplan, withdrawCoin);

				freeplan = MathUtils.add(freeplan, userPushCoinIn);
				freeplan = MathUtils.sub(freeplan, userPushCoinOut);
				freeplan = MathUtils.add(freeplan, financesCountSend); // 理财
				freeplan = MathUtils.sub(freeplan, vip6RMB);
				freeplan = MathUtils.add(freeplan, commission); // 返佣
				freeplan = MathUtils.sub(freeplan, frozenCoin); // 法币充提冻结
				freeplan = MathUtils.sub(freeplan, userPushCoinFrozen);
				freeplan = MathUtils.sub(freeplan, frozenFinances); // 理财

				// c2c
				freeplan = MathUtils.add(freeplan, c2cRecharge);
				freeplan = MathUtils.sub(freeplan, c2cWithdraw);
				freeplan = MathUtils.sub(freeplan, c2cWithdrawFrozen);

				// otc
				freeplan = MathUtils.add(freeplan, otcIn);
				freeplan = MathUtils.sub(freeplan, otcOut);

				// 创新区解冻
				freeplan = MathUtils.add(freeplan, unfreeze);
				//存币解冻
				freeplan = MathUtils.add(freeplan, depositUnfrozen);
				
				// 创新区分红
				freeplan = MathUtils.add(freeplan, dividend);
				// 创新区奖金
				freeplan = MathUtils.add(freeplan, reward);

				// 矿池
				// 矿池锁定
				freeplan = MathUtils.sub(freeplan, orepoolLock);
				// 矿池解锁
				freeplan = MathUtils.add(freeplan, orepoolUnlock);
				// 矿池收益
				freeplan = MathUtils.add(freeplan, orepoolIncome);
				// 创新区存币锁定
				freeplan = MathUtils.sub(freeplan, innovationLock);
				// 创新区存币解锁
				freeplan = MathUtils.add(freeplan, innovationUnlock);
				
				//平仓扣币
				freeplan = MathUtils.sub(freeplan, closePositionOut);
				
				//平仓入币
				freeplan = MathUtils.add(freeplan, closePositionIn);
				
				//发红包
				freeplan = MathUtils.sub(freeplan, sendRedEnvelope);
				
				//收红包
				freeplan = MathUtils.add(freeplan, receiveRedEnvelope);
				
				//退红包
				freeplan = MathUtils.add(freeplan, returnRedEnvelope);

				BigDecimal frozenplan = MathUtils.add(frozenWork, frozenCoin); // 用于统计法币交易冻结
				BigDecimal frozenTrade = BigDecimal.ZERO;
				if (Constant.CNY_COIN_NAME.equals(coin.getShortName().toUpperCase())) {
					frozenTrade = frozenamount; // 法币交易冻结的金额
					frozenplan = MathUtils.add(frozenplan, frozenamount);
					freeplan = MathUtils.sub(freeplan, buyamount); // 用法币购买别的币所花费的
					freeplan = MathUtils.add(freeplan, sellamount);// 卖别的币所获得的法币
					freeplan = MathUtils.sub(freeplan, fee); // 法币卖的手续费
				} else {
					frozenTrade = frozencount; // 0
					frozenplan = MathUtils.add(frozenplan, frozencount);
					freeplan = MathUtils.sub(freeplan, coinTradeBuy); // 虚拟币交易的花费
					freeplan = MathUtils.add(freeplan, coinTradeSell); // 虚拟币交易的收入
					freeplan = MathUtils.sub(freeplan, coinTradeFee); // 虚拟币交易的手续费
					freeplan = MathUtils.sub(freeplan, frozencountCoin);// 虚拟币交易导致冻结的数量
				}
				frozenplan = MathUtils.add(frozenplan, userPushCoinFrozen);
				frozenplan = MathUtils.add(frozenplan, frozenFinances);
				frozenplan = MathUtils.add(frozenplan, frozencountCoin);// 虚拟币交易导致冻结的数量
				frozenplan = MathUtils.add(frozenplan, c2cWithdrawFrozen);
				frozenplan = MathUtils.add(frozenplan, sendRedEnvelope);
				frozenplan = MathUtils.sub(frozenplan, deductRedEnvelope);
				frozenplan = MathUtils.sub(frozenplan, returnRedEnvelope);
				
				
				
				freeplan = MathUtils.sub(freeplan, frozenTrade);

				freeplan = MathUtils.add(freeplan, airdrop);
				boolean isFreeBalance = freeplan.compareTo(vwallet.getTotal()) == 0;
				JSONObject balanceCoinObj = new JSONObject();
				balanceCoinObj.put("type", coin.getName());
				balanceCoinObj.put("recharge", rechargeCoin);
				balanceCoinObj.put("withdraw", withdrawCoin);
				balanceCoinObj.put("rechargeWork", rechargeWork);
				balanceCoinObj.put("rewardCoin", rewardCoin);
				if (Constant.CNY_COIN_NAME.equals(coin.getShortName().toUpperCase())) {
					balanceCoinObj.put("buy", buyamount);
					balanceCoinObj.put("sell", sellamount);
				} else {
					balanceCoinObj.put("buy", buycount);
					balanceCoinObj.put("sell", sellcount);
				}
				balanceCoinObj.put("fees", fee);
				balanceCoinObj.put("vip6", vip6RMB);
				balanceCoinObj.put("pushin", userPushCoinIn);
				balanceCoinObj.put("pushout", userPushCoinOut);
				balanceCoinObj.put("financesCountSend", financesCountSend);
				balanceCoinObj.put("frozenCoin", frozenCoin);
				balanceCoinObj.put("frozenTrade", frozenTrade);
				balanceCoinObj.put("pushfrozen", userPushCoinFrozen);
				balanceCoinObj.put("frozenFinances", frozenFinances);
				balanceCoinObj.put("coinTradeBuy", coinTradeBuy);
				balanceCoinObj.put("coinTradeSell", coinTradeSell);
				balanceCoinObj.put("c2cRecharge", c2cRecharge);
				balanceCoinObj.put("c2cWithdraw", c2cWithdraw);
				balanceCoinObj.put("c2cWithdrawFrozen", c2cWithdrawFrozen);
				balanceCoinObj.put("coinTradeFee", coinTradeFee);
				balanceCoinObj.put("frozencountCoin", frozencountCoin);
				balanceCoinObj.put("commission", commission);
				balanceCoinObj.put("airdrop", airdrop);
				balanceCoinObj.put("otcIn", otcIn);
				balanceCoinObj.put("otcOut", otcOut);
				balanceCoinObj.put("unfreeze", unfreeze);
				balanceCoinObj.put("dividend", dividend);
				balanceCoinObj.put("reward", reward);
				balanceCoinObj.put("orepoolLock", orepoolLock);
				balanceCoinObj.put("orepoolUnlock", orepoolUnlock);
				balanceCoinObj.put("orepoolIncome", orepoolIncome);
				balanceCoinObj.put("innovationLock", innovationLock);
				balanceCoinObj.put("innovationUnlock", innovationUnlock);
				balanceCoinObj.put("depositUnfrozen", depositUnfrozen);
				balanceCoinObj.put("closePositionOut", closePositionOut);
				balanceCoinObj.put("closePositionIn", closePositionIn);
				balanceCoinObj.put("sendRedEnvelope", sendRedEnvelope);
				balanceCoinObj.put("receiveRedEnvelope", receiveRedEnvelope);
				balanceCoinObj.put("returnRedEnvelope", returnRedEnvelope);
				
				// 统计
				balanceCoinObj.put("freeplan", freeplan);
				balanceCoinObj.put("free", vwallet.getTotal());
				balanceCoinObj.put("isFreeBalance", isFreeBalance);

				JSONObject balanceFrozenObj = new JSONObject();
				boolean isFrozenBalance = frozenplan.compareTo(vwallet.getFrozen()) == 0;
				// 冻结
				balanceFrozenObj.put("type", coin.getName());
				balanceFrozenObj.put("pushfrozen", userPushCoinFrozen);
				balanceFrozenObj.put("frozenCoin", frozenCoin);
				balanceFrozenObj.put("frozenWork", frozenWork);
				balanceFrozenObj.put("frozenTrade", frozenTrade);
				balanceFrozenObj.put("frozenFinances", frozenFinances);
				balanceFrozenObj.put("frozenTradeCoin", frozencountCoin);
				balanceFrozenObj.put("c2cWithdrawFrozen", c2cWithdrawFrozen);
				balanceFrozenObj.put("sendRedEnvelope", sendRedEnvelope);
				balanceFrozenObj.put("deductRedEnvelope", deductRedEnvelope);
				balanceFrozenObj.put("returnRedEnvelope", returnRedEnvelope);
				balanceFrozenObj.put("frozenplan", frozenplan);
				balanceFrozenObj.put("frozen", vwallet.getFrozen());
				balanceFrozenObj.put("isFrozenBalance", isFrozenBalance);

				balance.add(balanceCoinObj);
				forzenBalance.add(balanceFrozenObj);
				if (!isFreeBalance) {
					logger.error("用户" + fuid + "存在不平衡:" + balanceCoinObj.toJSONString());
				}
			}

		}

		// 数据返回
		modelAndView.addObject("showRW", showRW);
		modelAndView.addObject("showTrade", showTrade);
		modelAndView.addObject("showBalance", showBalance);
		modelAndView.addObject("keyword", fuid);
		modelAndView.addObject("vwalletList", vwalletList);
		modelAndView.addObject("rwList", array);
		modelAndView.addObject("tradeList", trade);
		modelAndView.addObject("balanceCoin", balance);
		modelAndView.addObject("balanceFrozen", forzenBalance);
		modelAndView.addObject("c2clist", c2clist);
		modelAndView.addObject("depositFrozenList", depositFrozenList);
		modelAndView.addObject("depositFrozenTotalList", depositFrozenTotalList);
		modelAndView.addObject("borrowList", borrowList);
		return modelAndView;
	}

	public static IncrementBean getIncrementBean(IncrementBean ib) {
		if(ib != null) {
			return ib;
		}
		ib = new IncrementBean();
		ib.setTradeCost(BigDecimal.ZERO);
		ib.setTradeFee(BigDecimal.ZERO);
		ib.setTradeIncome(BigDecimal.ZERO);
		return ib;
	}

	public FEntrust getEntrust(FEntrust f) {
		if (f != null) {
			return f;
		} else {
			FEntrust eh = new FEntrust();
			eh.setFamount(BigDecimal.ZERO);
			eh.setFcount(BigDecimal.ZERO);
			eh.setFfees(BigDecimal.ZERO);
			eh.setFlast(BigDecimal.ZERO);
			eh.setFleftcount(BigDecimal.ZERO);
			eh.setFleftfees(BigDecimal.ZERO);
			eh.setFprize(BigDecimal.ZERO);
			eh.setFsuccessamount(BigDecimal.ZERO);
			eh.setFleftfunds(BigDecimal.ZERO);
			return eh;
		}
	}

	@RequestMapping("admin/usercapital")
	public ModelAndView usercapital(@RequestParam(value = "keyword", required = false, defaultValue = "0") Integer fuid,
			@RequestParam(value = "rwbegindate", required = false) Date rwbegindate,
			@RequestParam(value = "rwenddate", required = false) Date rwenddate,
			@RequestParam(value = "showRW", required = false, defaultValue = "true") Boolean showRW,
			@RequestParam(value = "showTrade", required = false, defaultValue = "true") Boolean showTrade,
			@RequestParam(value = "showBalance", required = false, defaultValue = "true") Boolean showBalance) {
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("query/usercapital");
		// 判断用户UID
		if (fuid == 0) {
			modelAndView.addObject("showRW", showRW);
			modelAndView.addObject("showTrade", showTrade);
			modelAndView.addObject("showBalance", showBalance);
			return modelAndView;
		}

		FUser user = adminUserService.selectById(fuid);
		// 判断用户是否存在
		if (user == null) {
			modelAndView.addObject("showRW", showRW);
			modelAndView.addObject("showTrade", showTrade);
			modelAndView.addObject("showBalance", showBalance);
			return modelAndView;
		}

		// 返回数据集
		JSONArray array = new JSONArray();
		JSONArray trade = new JSONArray();
		JSONArray balance = new JSONArray();
		JSONArray forzenBalance = new JSONArray();
		// JSONObject balanceObj = new JSONObject();
		JSONArray c2clist = new JSONArray();
		JSONArray depositFrozenList = new JSONArray();
		JSONArray depositFrozenTotalList = new JSONArray();
		JSONArray borrowList = new JSONArray();

		// 虚拟币钱包列表
		List<UserCoinWallet> vwalletList = new ArrayList<UserCoinWallet>();

		// 币种
		List<SystemCoinType> coinList = redisHelper.getCoinTypeListSystem();

		// SystemCoinType cny = redisHelper.getCoinTypeShortNameSystem("CNY");

		// c2c查询
		Map<Integer, UserC2CEntrust> statisticsRechargeWithdrawTotal = adminC2CService
				.statisticsRechargeWithdrawTotal(fuid, null);

		// 空投
		Map<Integer, BigDecimal> sumAirdropRecord = adminUserCapitalService.sumAirdropRecord(fuid);

		// otc
		Map<Integer, OtcUserTransfer> sumOtcTransfer = adminUserCapitalService.sumOtcTransfer(fuid);

		// 资金流水表
		List<Integer> typeList = new ArrayList<>();
		typeList.add(UserWalletBalanceLogTypeEnum.Freezing_of_Innovation_Zone.getCode());
		typeList.add(UserWalletBalanceLogTypeEnum.Dividend_of_Innovation_Zone.getCode());
		typeList.add(UserWalletBalanceLogTypeEnum.Reward_of_Innovation_Zone.getCode());
		typeList.add(UserWalletBalanceLogTypeEnum.Orepool_lock.getCode());
		typeList.add(UserWalletBalanceLogTypeEnum.Orepool_unlock.getCode());
		typeList.add(UserWalletBalanceLogTypeEnum.Orepool_income.getCode());
		typeList.add(UserWalletBalanceLogTypeEnum.Innovation_unfrozen.getCode());
		typeList.add(UserWalletBalanceLogTypeEnum.Innovation_lock.getCode());
		typeList.add(UserWalletBalanceLogTypeEnum.Innovation_unlock.getCode());
		typeList.add(UserWalletBalanceLogTypeEnum.Airdrop_Candy.getCode());
		typeList.add(UserWalletBalanceLogTypeEnum.CLOSE_POSITION.getCode());
		typeList.add(UserWalletBalanceLogTypeEnum.SEND_RED_ENVELOPE.getCode());
		typeList.add(UserWalletBalanceLogTypeEnum.RECEIVE_RED_ENVELOPE.getCode());
		typeList.add(UserWalletBalanceLogTypeEnum.RETURN_RED_ENVELOPE.getCode());
		typeList.add(UserWalletBalanceLogTypeEnum.DEDUCT_RED_ENVELOPE.getCode());
		Map<String, BigDecimal> sumUserWalletBalanceLogMap = adminUserCapitalService.sumUserWalletBalanceLog(fuid,
				typeList, null, null);

		// 循环币种
		for (SystemCoinType coin : coinList) {
			// 查询虚拟钱包
			UserCoinWallet vwallet = adminUserCapitalService.selectUserVirtualWallet(fuid, coin.getId());
			if (vwallet == null) {
				continue;
			}
			vwallet.setCoinName(coin.getName());
			vwalletList.add(vwallet);

			// 充提统计
			BigDecimal recharge = BigDecimal.ZERO;
			// BigDecimal zsjfRecharge = BigDecimal.ZERO;
			BigDecimal withdraw = BigDecimal.ZERO;
			BigDecimal withdrawFrozen = BigDecimal.ZERO;

			// PUSH资产
			BigDecimal userPushCoinIn = BigDecimal.ZERO;
			BigDecimal userPushCoinOut = BigDecimal.ZERO;
			BigDecimal userPushCoinFrozen = BigDecimal.ZERO;

			// 买卖交易
			BigDecimal buycount = BigDecimal.ZERO;
			BigDecimal buyamount = BigDecimal.ZERO;
			BigDecimal sellcount = BigDecimal.ZERO;
			BigDecimal sellamount = BigDecimal.ZERO;
			BigDecimal frozenamount = BigDecimal.ZERO;
			BigDecimal frozencount = BigDecimal.ZERO;
			BigDecimal frozencountCoin = BigDecimal.ZERO;
			BigDecimal fee = BigDecimal.ZERO;

			BigDecimal vip6RMB = BigDecimal.ZERO;

			// 佣金
			BigDecimal commission = BigDecimal.ZERO;

			// 币币交易
			BigDecimal coinTradeBuy = BigDecimal.ZERO;
			BigDecimal coinTradeSell = BigDecimal.ZERO;
			BigDecimal coinTradeFee = BigDecimal.ZERO;
			FEntrustHistory buy = null;
			FEntrustHistory sell = null;
			FEntrustHistory coinBuy = null;
			FEntrustHistory coinSell = null;
			FEntrustHistory coinSelfBuy = null;
			FEntrustHistory coinSelfSell = null;

			FEntrust currentbuy = null;
			FEntrust currentsell = null;
			FEntrust currentcoinBuy = null;
			FEntrust currentcoinSell = null;
			FEntrust currentSelfCoinBuy = null;
			FEntrust currentSelfCoinSell = null;

			// C2C
			BigDecimal c2cRecharge = BigDecimal.ZERO;
			BigDecimal c2cWithdraw = BigDecimal.ZERO;
			BigDecimal c2cWithdrawFrozen = BigDecimal.ZERO;

			// 理财
			BigDecimal financesCountSend = BigDecimal.ZERO;
			BigDecimal frozenFinances = BigDecimal.ZERO;

			// 空投
			BigDecimal airdrop = sumAirdropRecord.get(coin.getId()) == null ? BigDecimal.ZERO
					: sumAirdropRecord.get(coin.getId());

			// otc
			BigDecimal otcIn = BigDecimal.ZERO;
			BigDecimal otcOut = BigDecimal.ZERO;
			BigDecimal otcFrozen = BigDecimal.ZERO;

			// 创新区交易解冻
			BigDecimal userWalletBalanceLogFreezing = sumUserWalletBalanceLogMap
					.get(coin.getId().toString() + UserWalletBalanceLogDirectionEnum.in.getValue() + UserWalletBalanceLogTypeEnum.Freezing_of_Innovation_Zone.getCode());
			BigDecimal unfreeze = userWalletBalanceLogFreezing == null ? BigDecimal.ZERO : userWalletBalanceLogFreezing;

			// 创新区交易分红
			BigDecimal userWalletBalanceLogDividend = sumUserWalletBalanceLogMap
					.get(coin.getId().toString() + UserWalletBalanceLogDirectionEnum.in.getValue() + UserWalletBalanceLogTypeEnum.Dividend_of_Innovation_Zone.getCode());
			BigDecimal dividend = userWalletBalanceLogDividend == null ? BigDecimal.ZERO : userWalletBalanceLogDividend;

			// 创新区交易奖励
			BigDecimal userWalletBalanceLogReward = sumUserWalletBalanceLogMap
					.get(coin.getId().toString() + UserWalletBalanceLogDirectionEnum.in.getValue() + UserWalletBalanceLogTypeEnum.Reward_of_Innovation_Zone.getCode());
			BigDecimal reward = userWalletBalanceLogReward == null ? BigDecimal.ZERO : userWalletBalanceLogReward;

			// 矿池锁定
			BigDecimal userWalletBalanceLogOrepoolLock = sumUserWalletBalanceLogMap
					.get(coin.getId().toString() + UserWalletBalanceLogDirectionEnum.out.getValue() + UserWalletBalanceLogTypeEnum.Orepool_lock.getCode());
			BigDecimal orepoolLock = userWalletBalanceLogOrepoolLock == null ? BigDecimal.ZERO
					: userWalletBalanceLogOrepoolLock;

			// 矿池解锁
			BigDecimal userWalletBalanceLogOrepoolUnlock = sumUserWalletBalanceLogMap
					.get(coin.getId().toString() + UserWalletBalanceLogDirectionEnum.in.getValue() + UserWalletBalanceLogTypeEnum.Orepool_unlock.getCode());
			BigDecimal orepoolUnlock = userWalletBalanceLogOrepoolUnlock == null ? BigDecimal.ZERO
					: userWalletBalanceLogOrepoolUnlock;

			// 矿池收益
			BigDecimal userWalletBalanceLogOrepoolIncome = sumUserWalletBalanceLogMap
					.get(coin.getId().toString() + UserWalletBalanceLogDirectionEnum.in.getValue()+ UserWalletBalanceLogTypeEnum.Orepool_income.getCode());
			BigDecimal orepoolIncome = userWalletBalanceLogOrepoolIncome == null ? BigDecimal.ZERO
					: userWalletBalanceLogOrepoolIncome;
			
			// 创新区存币解冻
			BigDecimal userWalletBalanceLogInnovationUnfrozen = sumUserWalletBalanceLogMap
					.get(coin.getId().toString() + UserWalletBalanceLogDirectionEnum.in.getValue() + UserWalletBalanceLogTypeEnum.Innovation_unfrozen.getCode());
			BigDecimal depositUnfrozen = userWalletBalanceLogInnovationUnfrozen == null ? BigDecimal.ZERO
					: userWalletBalanceLogInnovationUnfrozen;
			// 创新区存币锁定
			BigDecimal userWalletBalanceLogInnovationLock = sumUserWalletBalanceLogMap
					.get(coin.getId().toString() + UserWalletBalanceLogDirectionEnum.out.getValue() + UserWalletBalanceLogTypeEnum.Innovation_lock.getCode());
			BigDecimal innovationLock = userWalletBalanceLogInnovationLock == null ? BigDecimal.ZERO
					: userWalletBalanceLogInnovationLock;
			// 创新区存币解锁
			BigDecimal userWalletBalanceLogInnovationUnlock = sumUserWalletBalanceLogMap
					.get(coin.getId().toString() + UserWalletBalanceLogDirectionEnum.in.getValue() + UserWalletBalanceLogTypeEnum.Innovation_unlock.getCode());
			BigDecimal innovationUnlock = userWalletBalanceLogInnovationUnlock == null ? BigDecimal.ZERO
					: userWalletBalanceLogInnovationUnlock;
			//空投糖果
			BigDecimal userWalletBalanceLogAirdropCandy = sumUserWalletBalanceLogMap
					.get(coin.getId().toString() + UserWalletBalanceLogDirectionEnum.in.getValue() + UserWalletBalanceLogTypeEnum.Airdrop_Candy.getCode());
			BigDecimal airdropCandy = userWalletBalanceLogAirdropCandy == null ? BigDecimal.ZERO
					: userWalletBalanceLogAirdropCandy;
			airdrop = MathUtils.add(airdrop, airdropCandy);
			
			
			//平仓转出
			BigDecimal userWalletBalanceLogClosePositionOut = sumUserWalletBalanceLogMap
					.get(coin.getId().toString() + UserWalletBalanceLogDirectionEnum.out.getValue() + UserWalletBalanceLogTypeEnum.CLOSE_POSITION.getCode());
			BigDecimal closePositionOut = userWalletBalanceLogClosePositionOut == null ? BigDecimal.ZERO
					: userWalletBalanceLogClosePositionOut;
			
			//平仓转入
			BigDecimal userWalletBalanceLogClosePositionIn = sumUserWalletBalanceLogMap
					.get(coin.getId().toString() + UserWalletBalanceLogDirectionEnum.in.getValue() + UserWalletBalanceLogTypeEnum.CLOSE_POSITION.getCode());
			BigDecimal closePositionIn = userWalletBalanceLogClosePositionIn == null ? BigDecimal.ZERO
					: userWalletBalanceLogClosePositionIn;
			
			
			//发红包
			BigDecimal userWalletBalanceLogSendRedEnvelope = sumUserWalletBalanceLogMap
					.get(coin.getId().toString() + UserWalletBalanceLogDirectionEnum.out.getValue() + UserWalletBalanceLogTypeEnum.SEND_RED_ENVELOPE.getCode());
			BigDecimal sendRedEnvelope = userWalletBalanceLogSendRedEnvelope == null ? BigDecimal.ZERO
					: userWalletBalanceLogSendRedEnvelope;
			
			//收红包
			BigDecimal userWalletBalanceLogReceiveRedEnvelope = sumUserWalletBalanceLogMap
					.get(coin.getId().toString() + UserWalletBalanceLogDirectionEnum.in.getValue() + UserWalletBalanceLogTypeEnum.RECEIVE_RED_ENVELOPE.getCode());
			BigDecimal receiveRedEnvelope = userWalletBalanceLogReceiveRedEnvelope == null ? BigDecimal.ZERO
					: userWalletBalanceLogReceiveRedEnvelope;
			
			
			//退回红包
			BigDecimal userWalletBalanceLogReturnRedEnvelope = sumUserWalletBalanceLogMap
					.get(coin.getId().toString() + UserWalletBalanceLogDirectionEnum.in.getValue() + UserWalletBalanceLogTypeEnum.RETURN_RED_ENVELOPE.getCode());
			BigDecimal returnRedEnvelope = userWalletBalanceLogReturnRedEnvelope == null ? BigDecimal.ZERO
					: userWalletBalanceLogReturnRedEnvelope;
			
			//红包扣钱
			BigDecimal userWalletBalanceLogDeductRedEnvelope = sumUserWalletBalanceLogMap
					.get(coin.getId().toString() + UserWalletBalanceLogDirectionEnum.out.getValue() + UserWalletBalanceLogTypeEnum.DEDUCT_RED_ENVELOPE.getCode());
			BigDecimal deductRedEnvelope = userWalletBalanceLogDeductRedEnvelope == null ? BigDecimal.ZERO
					: userWalletBalanceLogDeductRedEnvelope;
			

			if (Constant.CNY_COIN_NAME.equals(coin.getShortName().toUpperCase())) {
				buy = adminEntrustServer.selectTotalAmountByType(fuid, coin.getId(), null,
						EntrustTypeEnum.BUY.getCode(), rwbegindate, rwenddate);
				sell = adminEntrustServer.selectTotalAmountByType(fuid, coin.getId(), null,
						EntrustTypeEnum.SELL.getCode(), rwbegindate, rwenddate);
				currentbuy = adminEntrustServer.selectCurrentTotalAmountByType(fuid, coin.getId(), null,
						EntrustTypeEnum.BUY.getCode(), EntrustStateEnum.PartDeal.getCode(), rwbegindate, rwenddate);
				currentsell = adminEntrustServer.selectCurrentTotalAmountByType(fuid, coin.getId(), null,
						EntrustTypeEnum.SELL.getCode(), EntrustStateEnum.PartDeal.getCode(), rwbegindate, rwenddate);

				frozenamount = MathUtils.sub(currentbuy.getFamount(), currentbuy.getFsuccessamount());
				FEntrust frozenEntrust = adminEntrustServer.selectCurrentTotalAmountByType(fuid, coin.getId(), null,
						EntrustTypeEnum.BUY.getCode(), EntrustStateEnum.Going.getCode(), rwbegindate, rwenddate);
				// 交易冻结金额
				frozenamount = MathUtils.add(frozenamount, frozenEntrust.getFamount());
				
				// 市价单冻结
				// buy
				frozenamount = MathUtils.add(frozenamount, currentbuy.getFleftfunds());
				frozenamount = MathUtils.add(frozenamount, frozenEntrust.getFleftfunds());

				// 人民币充提统计
				recharge = adminUserCapitalService.selectWalletTotalAmount(fuid,
						CapitalOperationInOutTypeEnum.IN.getCode(), CapitalOperationInStatus.Come, rwbegindate,
						rwenddate);
				withdraw = adminUserCapitalService.selectWalletTotalAmount(fuid,
						CapitalOperationInOutTypeEnum.OUT.getCode(), CapitalOperationOutStatus.OperationSuccess,
						rwbegindate, rwenddate);

				// 冻结
				BigDecimal wf = adminUserCapitalService.selectWalletTotalAmount(fuid,
						CapitalOperationInOutTypeEnum.OUT.getCode(), CapitalOperationOutStatus.WaitForOperation,
						rwbegindate, rwenddate);
				withdrawFrozen = adminUserCapitalService.selectWalletTotalAmount(fuid,
						CapitalOperationInOutTypeEnum.OUT.getCode(), CapitalOperationOutStatus.OperationLock,
						rwbegindate, rwenddate);
				// 充提冻结
				withdrawFrozen = MathUtils.add(withdrawFrozen, wf);

				// PUSH资产
				FUserPushDTO userPushIn = adminUserCapitalService.selectUserPushBalance(null, user.getFshowid(), null,
						UserPushStateEnum.PAYSUCCEED.getCode(), rwbegindate, rwenddate);
				FUserPushDTO userPushOut = adminUserCapitalService.selectUserPushBalance(user.getFshowid(), null, null,
						UserPushStateEnum.PAYSUCCEED.getCode(), rwbegindate, rwenddate);
				userPushCoinIn = userPushOut == null ? BigDecimal.ZERO : userPushOut.getFamount();
				userPushCoinOut = userPushIn == null ? BigDecimal.ZERO : userPushIn.getFamount();

				// 用法币购买别的币所花费的
				buyamount = MathUtils.add(buy.getFsuccessamount(), currentbuy.getFsuccessamount());
				// 卖别的币所获得的法币
				sellamount = MathUtils.add(sell.getFsuccessamount(), currentsell.getFsuccessamount());
				// 总共交易卖的手续费
				fee = MathUtils.add(sell.getFfees(), currentsell.getFfees());

				// vip6购买
				FLogUserAction userAction = adminLogService.selectVip6ByUser(fuid,
						LogUserActionEnum.BUY_VIP6.getCode());
				if (userAction != null) {
					vip6RMB = BigDecimal.valueOf(3888D);
				}

				// 返佣
				commission = adminCommissionService.selectIssuedAmountByIntroId(fuid);
				if (commission == null) {
					commission = BigDecimal.ZERO;
				}
			} else {

				// 币币交易-主币种-买-减少
				coinSelfBuy = adminEntrustServer.selectTotalAmountByType(fuid, coin.getId(), null,
						EntrustTypeEnum.BUY.getCode(), rwbegindate, rwenddate);
				// 币币交易-主币种-卖-增加
				coinSelfSell = adminEntrustServer.selectTotalAmountByType(fuid, coin.getId(), null,
						EntrustTypeEnum.SELL.getCode(), rwbegindate, rwenddate);
				// 币币交易-交易币种-买-增加
				coinBuy = adminEntrustServer.selectTotalAmountByType(fuid, null, coin.getId(),
						EntrustTypeEnum.BUY.getCode(), rwbegindate, rwenddate);
				// 币币交易-交易币种-卖-减少
				coinSell = adminEntrustServer.selectTotalAmountByType(fuid, null, coin.getId(),
						EntrustTypeEnum.SELL.getCode(), rwbegindate, rwenddate);

				// 币币交易-主币种-买-减少
				currentSelfCoinBuy = adminEntrustServer.selectCurrentTotalAmountByType(fuid, coin.getId(), null,
						EntrustTypeEnum.BUY.getCode(), EntrustStateEnum.PartDeal.getCode(), rwbegindate, rwenddate);
				FEntrust currentSelfCoinBuyGoing = adminEntrustServer.selectCurrentTotalAmountByType(fuid, coin.getId(),
						null, EntrustTypeEnum.BUY.getCode(), EntrustStateEnum.Going.getCode(), rwbegindate, rwenddate);
				// 币币交易-主币种-卖-增加
				currentSelfCoinSell = adminEntrustServer.selectCurrentTotalAmountByType(fuid, coin.getId(), null,
						EntrustTypeEnum.SELL.getCode(), EntrustStateEnum.PartDeal.getCode(), rwbegindate, rwenddate);
				// 币币交易-交易币种-买-增加
				currentcoinBuy = adminEntrustServer.selectCurrentTotalAmountByType(fuid, null, coin.getId(),
						EntrustTypeEnum.BUY.getCode(), EntrustStateEnum.PartDeal.getCode(), rwbegindate, rwenddate);
				// 币币交易-交易币种-卖-减少
				currentcoinSell = adminEntrustServer.selectCurrentTotalAmountByType(fuid, null, coin.getId(),
						EntrustTypeEnum.SELL.getCode(), EntrustStateEnum.PartDeal.getCode(), rwbegindate, rwenddate);
				FEntrust currentcoinSellGoing = adminEntrustServer.selectCurrentTotalAmountByType(fuid, null,
						coin.getId(), EntrustTypeEnum.SELL.getCode(), EntrustStateEnum.Going.getCode(), rwbegindate,
						rwenddate);

				frozencountCoin = MathUtils.add(
						MathUtils.sub(currentSelfCoinBuy.getFamount(), currentSelfCoinBuy.getFsuccessamount()),
						MathUtils.sub(currentSelfCoinBuyGoing.getFamount(),
								currentSelfCoinBuyGoing.getFsuccessamount()));
				// 交易冻结数量
				frozencountCoin = MathUtils.add(frozencountCoin,
						MathUtils.add(currentcoinSell.getFleftcount(), currentcoinSellGoing.getFleftcount()));
				
				// 市价单冻结
				// buy
				frozencountCoin = MathUtils.add(frozencountCoin, currentSelfCoinBuy.getFleftfunds());
				frozencountCoin = MathUtils.add(frozencountCoin, currentSelfCoinBuyGoing.getFleftfunds());

				// 币币交易-花费
				coinTradeBuy = MathUtils.add(
						MathUtils.add(coinSelfBuy.getFsuccessamount(),
								MathUtils.add(currentSelfCoinBuy.getFsuccessamount(),
										currentSelfCoinBuyGoing.getFsuccessamount())),
						MathUtils.add(coinSell.getFcount(), currentcoinSell.getFcount()));
				// 币币交易-收入
				coinTradeSell = MathUtils.add(
						MathUtils.add(coinSelfSell.getFsuccessamount(), currentSelfCoinSell.getFsuccessamount()),
						MathUtils.add(coinBuy.getFcount(), currentcoinBuy.getFcount()));

				coinTradeFee = MathUtils.add(
						MathUtils.add(coinSelfSell.getFfees(),
								MathUtils.add(currentSelfCoinSell.getFfees(), currentSelfCoinBuyGoing.getFfees())),
						MathUtils.add(coinBuy.getFfees(), currentcoinBuy.getFfees()));

				FUserPushDTO userFrozenPush = adminUserCapitalService.selectUserPushBalance(user.getFshowid(), null,
						coin.getId(), UserPushStateEnum.PAYWAIT.getCode(), rwbegindate, rwenddate);
				userPushCoinFrozen = userFrozenPush == null ? BigDecimal.ZERO : userFrozenPush.getFcount();
			}

			// =====================================================>20181214修改

			// 虚拟币充值
			BigDecimal rechargeV = adminUserCapitalService.selectVirtualWalletTotalAmount(fuid, coin.getId(),
					VirtualCapitalOperationTypeEnum.COIN_IN.getCode(), VirtualCapitalOperationInStatusEnum.SUCCESS,
					rwbegindate, rwenddate, false);

			// 虚拟币充值冻结
			BigDecimal rechargeFrozen = adminUserCapitalService.selectVirtualWalletTotalAmount(fuid, coin.getId(),
					VirtualCapitalOperationTypeEnum.COIN_IN.getCode(), VirtualCapitalOperationInStatusEnum.SUCCESS,
					rwbegindate, rwenddate, true);

			// 虚拟币充值加人民币充值
			recharge = MathUtils.add(rechargeV, recharge);
			// 虚拟币提现
			BigDecimal withdrawV = adminUserCapitalService.selectVirtualWalletTotalAmount(fuid, coin.getId(),
					VirtualCapitalOperationTypeEnum.COIN_OUT.getCode(),
					VirtualCapitalOperationOutStatusEnum.OperationSuccess, rwbegindate, rwenddate, null);
			// 虚拟币提现加人民币提现
			withdraw = MathUtils.add(withdrawV, withdraw);

			// 冻结
			BigDecimal wf = adminUserCapitalService.selectVirtualWalletTotalAmount(fuid, coin.getId(),
					VirtualCapitalOperationTypeEnum.COIN_OUT.getCode(),
					VirtualCapitalOperationOutStatusEnum.WaitForOperation, rwbegindate, rwenddate, null);
			withdrawFrozen = MathUtils.add(withdrawFrozen, wf);
			wf = adminUserCapitalService.selectVirtualWalletTotalAmount(fuid, coin.getId(),
					VirtualCapitalOperationTypeEnum.COIN_OUT.getCode(),
					VirtualCapitalOperationOutStatusEnum.OperationLock, rwbegindate, rwenddate, null);
			withdrawFrozen = MathUtils.add(withdrawFrozen, wf);
			wf = adminUserCapitalService.selectVirtualWalletTotalAmount(fuid, coin.getId(),
					VirtualCapitalOperationTypeEnum.COIN_OUT.getCode(), VirtualCapitalOperationOutStatusEnum.LockOrder,
					rwbegindate, rwenddate, null);
			// 充提冻结
			withdrawFrozen = MathUtils.add(withdrawFrozen, wf);

			// =====================================================>20181214修改end

			// 手工充值
			BigDecimal rechargeWork = adminUserCapitalService.selectAdminRechargeVirtualWalletTotalAmount(fuid,
					coin.getId(), OperationlogEnum.AUDIT, rwbegindate, rwenddate);
			BigDecimal frozenWork = adminUserCapitalService.selectAdminRechargeVirtualWalletTotalAmount(fuid,
					coin.getId(), OperationlogEnum.FFROZEN, rwbegindate, rwenddate);

			// 虚拟币兑换
			BigDecimal rewardCoin = adminRewardCodeService.selectWalletTotalAmount(fuid, coin.getId(), rwbegindate,
					rwenddate);

			// c2c
			if (statisticsRechargeWithdrawTotal != null) {
				UserC2CEntrust statisticsEntrust = statisticsRechargeWithdrawTotal.get(coin.getId());
				if (statisticsEntrust != null) {
					c2cRecharge = statisticsEntrust.getRecharge();
					c2cWithdraw = statisticsEntrust.getWithdraw();
					c2cWithdrawFrozen = statisticsEntrust.getWithdrawFrozen();
				}
			}
			JSONObject c2cObj = new JSONObject();
			c2cObj.put("type", coin.getName());
			c2cObj.put("recharge", c2cRecharge);
			c2cObj.put("withdraw", c2cWithdraw);
			c2clist.add(c2cObj);

			// otc
			OtcUserTransfer otcUserTransfer = sumOtcTransfer.get(coin.getId());
			if (otcUserTransfer != null) {
				otcIn = otcUserTransfer.getAmountIn();
				otcOut = otcUserTransfer.getAmountOut();
				otcFrozen = otcUserTransfer.getAmountFrozen();
			}

			// 判断是否显示充提
			if (showRW) {
				// 虚拟币充提统计
				JSONObject coinObj = new JSONObject();
				coinObj.put("type", coin.getName());
				coinObj.put("recharge", recharge);
				coinObj.put("withdraw", withdraw);
				coinObj.put("rechargeFrozen", rechargeFrozen);
				array.add(coinObj);
			}

			// 创新区冻结平衡
			JSONObject depositFrozen = new JSONObject();
			JSONObject depositFrozenTotal = new JSONObject();
			BigDecimal depositFrozenTemp = MathUtils.add(rechargeFrozen, otcFrozen);
			
			// 冻结总数是否平衡
			Boolean isdepositFrozenTotal = vwallet.getDepositFrozenTotal().compareTo(depositFrozenTemp) == 0;
			depositFrozenTotal.put("type", coin.getName());
			depositFrozenTotal.put("rechargeFrozen", rechargeFrozen);
			depositFrozenTotal.put("otcTransferFrozen", otcFrozen);
			depositFrozenTotal.put("result", depositFrozenTemp);
			depositFrozenTotal.put("walletResult", vwallet.getDepositFrozenTotal());
			depositFrozenTotal.put("isdepositFrozenTotal", isdepositFrozenTotal);
			depositFrozenTotalList.add(depositFrozenTotal);
			depositFrozenTemp = MathUtils.sub(depositFrozenTemp, unfreeze); //交易解冻
			depositFrozenTemp = MathUtils.sub(depositFrozenTemp, depositUnfrozen); //存币解冻
			// 冻结数量是否平衡
			Boolean isdepositFrozen = vwallet.getDepositFrozen().compareTo(depositFrozenTemp) == 0;
			depositFrozen.put("type", coin.getName());
			depositFrozen.put("rechargeFrozen", rechargeFrozen);
			depositFrozen.put("otcTransferFrozen", otcFrozen);
			depositFrozen.put("unfreeze", unfreeze);
			depositFrozen.put("depositUnfrozen", depositUnfrozen);
			depositFrozen.put("result", depositFrozenTemp);
			depositFrozen.put("walletResult", vwallet.getDepositFrozen());
			depositFrozen.put("isdepositFrozen", isdepositFrozen);
			depositFrozenList.add(depositFrozen);

			// 理财平衡
			JSONObject borrow = new JSONObject();
			BigDecimal borrowCalculation = MathUtils.sub(orepoolLock, orepoolUnlock);
			borrowCalculation = MathUtils.add(borrowCalculation, innovationLock);
			borrowCalculation = MathUtils.sub(borrowCalculation, innovationUnlock);
			Boolean isBorrowBalance = vwallet.getBorrow().compareTo(borrowCalculation) == 0;
			borrow.put("type", coin.getName());
			borrow.put("orepoolLock", orepoolLock);
			borrow.put("orepoolUnlock", orepoolUnlock);
			borrow.put("innovationLock", innovationLock);
			borrow.put("innovationUnlock", innovationUnlock);
			borrow.put("result", borrowCalculation);
			borrow.put("walletResult", vwallet.getBorrow());
			borrow.put("isBorrowBalance", isBorrowBalance);
			borrowList.add(borrow);

			// 判断是否显示交易
			if (showTrade) {
				JSONObject tradeObj = new JSONObject();
				tradeObj.put("type", coin.getName());
				tradeObj.put("buycount", buycount);
				tradeObj.put("buyamount", buyamount);
				tradeObj.put("sellcount", sellcount);
				tradeObj.put("sellamount", sellamount);

				trade.add(tradeObj);
			}

			// 判断是否显示平衡
			if (showBalance) {

				BigDecimal rechargeCoin = recharge;
				BigDecimal withdrawCoin = withdraw;
				BigDecimal frozenCoin = withdrawFrozen;

				// 充值+手工充值+虚拟币兑换-体现
				BigDecimal freeplan = MathUtils.add(rechargeCoin, rechargeWork);
				freeplan = MathUtils.add(freeplan, rewardCoin);
				freeplan = MathUtils.sub(freeplan, withdrawCoin);

				freeplan = MathUtils.add(freeplan, userPushCoinIn);
				freeplan = MathUtils.sub(freeplan, userPushCoinOut);
				freeplan = MathUtils.add(freeplan, financesCountSend); // 理财
				freeplan = MathUtils.sub(freeplan, vip6RMB);
				freeplan = MathUtils.add(freeplan, commission); // 返佣
				freeplan = MathUtils.sub(freeplan, frozenCoin); // 法币充提冻结
				freeplan = MathUtils.sub(freeplan, userPushCoinFrozen);
				freeplan = MathUtils.sub(freeplan, frozenFinances); // 理财

				// c2c
				freeplan = MathUtils.add(freeplan, c2cRecharge);
				freeplan = MathUtils.sub(freeplan, c2cWithdraw);
				freeplan = MathUtils.sub(freeplan, c2cWithdrawFrozen);

				// otc
				freeplan = MathUtils.add(freeplan, otcIn);
				freeplan = MathUtils.sub(freeplan, otcOut);

				// 创新区解冻
				freeplan = MathUtils.add(freeplan, unfreeze);
				// 存币解冻
				freeplan = MathUtils.add(freeplan, depositUnfrozen);
				// 创新区分红
				freeplan = MathUtils.add(freeplan, dividend);
				// 创新区奖金
				freeplan = MathUtils.add(freeplan, reward);

				// 矿池
				// 矿池锁定
				freeplan = MathUtils.sub(freeplan, orepoolLock);
				// 矿池解锁
				freeplan = MathUtils.add(freeplan, orepoolUnlock);
				// 矿池收益
				freeplan = MathUtils.add(freeplan, orepoolIncome);
				// 创新区存币锁定
				freeplan = MathUtils.sub(freeplan, innovationLock);
				// 创新区存币解锁
				freeplan = MathUtils.add(freeplan, innovationUnlock);
				
				//平仓
				freeplan = MathUtils.add(freeplan, closePositionIn);
				freeplan = MathUtils.sub(freeplan, closePositionOut);
				
				//发红包
				freeplan = MathUtils.sub(freeplan, sendRedEnvelope);
				
				//收红包
				freeplan = MathUtils.add(freeplan, receiveRedEnvelope);
				
				//退红包
				freeplan = MathUtils.add(freeplan, returnRedEnvelope);
				

				BigDecimal frozenplan = MathUtils.add(frozenWork, frozenCoin); // 用于统计法币交易冻结
				BigDecimal frozenTrade = BigDecimal.ZERO;
				if (Constant.CNY_COIN_NAME.equals(coin.getShortName().toUpperCase())) {
					frozenTrade = frozenamount; // 法币交易冻结的金额
					frozenplan = MathUtils.add(frozenplan, frozenamount);
					freeplan = MathUtils.sub(freeplan, buyamount); // 用法币购买别的币所花费的
					freeplan = MathUtils.add(freeplan, sellamount);// 卖别的币所获得的法币
					freeplan = MathUtils.sub(freeplan, fee); // 法币卖的手续费
				} else {
					frozenTrade = frozencount; // 0
					frozenplan = MathUtils.add(frozenplan, frozencount);
					freeplan = MathUtils.sub(freeplan, coinTradeBuy); // 虚拟币交易的花费
					freeplan = MathUtils.add(freeplan, coinTradeSell); // 虚拟币交易的收入
					freeplan = MathUtils.sub(freeplan, coinTradeFee); // 虚拟币交易的手续费
					freeplan = MathUtils.sub(freeplan, frozencountCoin);// 虚拟币交易导致冻结的数量
				}
				frozenplan = MathUtils.add(frozenplan, userPushCoinFrozen);
				frozenplan = MathUtils.add(frozenplan, frozenFinances);
				frozenplan = MathUtils.add(frozenplan, frozencountCoin);// 虚拟币交易导致冻结的数量
				frozenplan = MathUtils.add(frozenplan, c2cWithdrawFrozen);
				frozenplan = MathUtils.add(frozenplan, sendRedEnvelope);
				frozenplan = MathUtils.sub(frozenplan, deductRedEnvelope);
				frozenplan = MathUtils.sub(frozenplan, returnRedEnvelope);
				freeplan = MathUtils.sub(freeplan, frozenTrade);

				freeplan = MathUtils.add(freeplan, airdrop);
				boolean isFreeBalance = freeplan.compareTo(vwallet.getTotal()) == 0;
				JSONObject balanceCoinObj = new JSONObject();
				balanceCoinObj.put("type", coin.getName());
				balanceCoinObj.put("recharge", rechargeCoin);
				balanceCoinObj.put("withdraw", withdrawCoin);
				balanceCoinObj.put("rechargeWork", rechargeWork);
				balanceCoinObj.put("rewardCoin", rewardCoin);
				if (Constant.CNY_COIN_NAME.equals(coin.getShortName().toUpperCase())) {
					balanceCoinObj.put("buy", buyamount);
					balanceCoinObj.put("sell", sellamount);
				} else {
					balanceCoinObj.put("buy", buycount);
					balanceCoinObj.put("sell", sellcount);
				}
				balanceCoinObj.put("fees", fee);
				balanceCoinObj.put("vip6", vip6RMB);
				balanceCoinObj.put("pushin", userPushCoinIn);
				balanceCoinObj.put("pushout", userPushCoinOut);
				balanceCoinObj.put("financesCountSend", financesCountSend);
				balanceCoinObj.put("frozenCoin", frozenCoin);
				balanceCoinObj.put("frozenTrade", frozenTrade);
				balanceCoinObj.put("pushfrozen", userPushCoinFrozen);
				balanceCoinObj.put("frozenFinances", frozenFinances);
				balanceCoinObj.put("coinTradeBuy", coinTradeBuy);
				balanceCoinObj.put("coinTradeSell", coinTradeSell);
				balanceCoinObj.put("c2cRecharge", c2cRecharge);
				balanceCoinObj.put("c2cWithdraw", c2cWithdraw);
				balanceCoinObj.put("c2cWithdrawFrozen", c2cWithdrawFrozen);
				balanceCoinObj.put("coinTradeFee", coinTradeFee);
				balanceCoinObj.put("frozencountCoin", frozencountCoin);
				balanceCoinObj.put("commission", commission);
				balanceCoinObj.put("airdrop", airdrop);
				balanceCoinObj.put("otcIn", otcIn);
				balanceCoinObj.put("otcOut", otcOut);
				balanceCoinObj.put("unfreeze", unfreeze);
				balanceCoinObj.put("dividend", dividend);
				balanceCoinObj.put("reward", reward);
				balanceCoinObj.put("orepoolLock", orepoolLock);
				balanceCoinObj.put("orepoolUnlock", orepoolUnlock);
				balanceCoinObj.put("orepoolIncome", orepoolIncome);
				balanceCoinObj.put("innovationLock", innovationLock);
				balanceCoinObj.put("innovationUnlock", innovationUnlock);
				balanceCoinObj.put("depositUnfrozen", depositUnfrozen);
				balanceCoinObj.put("closePositionOut", closePositionOut);
				balanceCoinObj.put("closePositionIn", closePositionIn);
				balanceCoinObj.put("sendRedEnvelope", sendRedEnvelope);
				balanceCoinObj.put("receiveRedEnvelope", receiveRedEnvelope);
				balanceCoinObj.put("returnRedEnvelope", returnRedEnvelope);

				// 统计
				balanceCoinObj.put("freeplan", freeplan);
				balanceCoinObj.put("free", vwallet.getTotal());
				balanceCoinObj.put("isFreeBalance", isFreeBalance);

				JSONObject balanceFrozenObj = new JSONObject();
				boolean isFrozenBalance = frozenplan.compareTo(vwallet.getFrozen()) == 0;
				// 冻结
				balanceFrozenObj.put("type", coin.getName());
				balanceFrozenObj.put("pushfrozen", userPushCoinFrozen);
				balanceFrozenObj.put("frozenCoin", frozenCoin);
				balanceFrozenObj.put("frozenWork", frozenWork);
				balanceFrozenObj.put("frozenTrade", frozenTrade);
				balanceFrozenObj.put("frozenFinances", frozenFinances);
				balanceFrozenObj.put("frozenTradeCoin", frozencountCoin);
				balanceFrozenObj.put("c2cWithdrawFrozen", c2cWithdrawFrozen);
				balanceFrozenObj.put("sendRedEnvelope", sendRedEnvelope);
				balanceFrozenObj.put("deductRedEnvelope", deductRedEnvelope);
				balanceFrozenObj.put("returnRedEnvelope", returnRedEnvelope);
				balanceFrozenObj.put("frozenplan", frozenplan);
				balanceFrozenObj.put("frozen", vwallet.getFrozen());
				balanceFrozenObj.put("isFrozenBalance", isFrozenBalance);
				balance.add(balanceCoinObj);
				forzenBalance.add(balanceFrozenObj);
				if (!isFreeBalance) {
					logger.error("用户" + fuid + "存在不平衡:" + balanceCoinObj.toJSONString());
				}
			}

		}

		// 数据返回
		modelAndView.addObject("showRW", showRW);
		modelAndView.addObject("showTrade", showTrade);
		modelAndView.addObject("showBalance", showBalance);
		modelAndView.addObject("keyword", fuid);
		modelAndView.addObject("vwalletList", vwalletList);
		modelAndView.addObject("rwList", array);
		modelAndView.addObject("tradeList", trade);
		modelAndView.addObject("balanceCoin", balance);
		modelAndView.addObject("balanceFrozen", forzenBalance);
		modelAndView.addObject("c2clist", c2clist);
		modelAndView.addObject("depositFrozenList", depositFrozenList);
		modelAndView.addObject("depositFrozenTotalList", depositFrozenTotalList);
		modelAndView.addObject("borrowList", borrowList);
		return modelAndView;
	}

	@RequestMapping("admin/monthDataList")
	public ModelAndView monthDataList(@RequestParam(value = "month", required = false) String month)
			throws ParseException {
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("query/monthDataList");

		Date date = new Date();

		if (!StringUtils.isEmpty(month)) {
			date = DateUtils.parse(month, DateUtils.YYYY_MM_DD);
		} else {
			return modelAndView;
		}

		Map<String, Object> map = getBeginEndDate(date);

		JSONArray array = new JSONArray();

		List<SystemCoinType> coinList = redisHelper.getCoinTypeListSystem();
		for (SystemCoinType coin : coinList) {
			if (coin.getCoinType().equals(SystemCoinTypeEnum.CNY.getCode())) {
				JSONObject rmb = new JSONObject();

				BigDecimal RMBrecharge = adminStatisticsService.sumRWrmb(CapitalOperationInOutTypeEnum.IN.getCode(),
						map);
				BigDecimal RMBOnLinerecharge = adminStatisticsService.sumOtherRmb(map);
				RMBrecharge = MathUtils.add(RMBrecharge, RMBOnLinerecharge);

				BigDecimal RMBwithdraw = adminStatisticsService.sumRWrmb(CapitalOperationInOutTypeEnum.OUT.getCode(),
						map);

				BigDecimal RMBbuy = adminStatisticsService.sumBSrmb(EntrustTypeEnum.BUY.getCode(), map);
				BigDecimal RMBsell = adminStatisticsService.sumBSrmb(EntrustTypeEnum.SELL.getCode(), map);

				rmb.put("name", coin.getName());
				rmb.put("recharge", RMBrecharge);
				rmb.put("withdraw", RMBwithdraw);
				rmb.put("buy", RMBbuy);
				rmb.put("sell", RMBsell);
				array.add(rmb);
			} else {
				JSONObject obj = new JSONObject();

				BigDecimal CoinRecharge = adminStatisticsService
						.sumRWcoin(VirtualCapitalOperationTypeEnum.COIN_IN.getCode(), map, coin.getId());
				BigDecimal CoinWithdraw = adminStatisticsService
						.sumRWcoin(VirtualCapitalOperationTypeEnum.COIN_OUT.getCode(), map, coin.getId());

				BigDecimal CoinBuy = adminStatisticsService.sumBScoin(EntrustTypeEnum.BUY.getCode(), map, coin.getId());
				BigDecimal CoinSell = adminStatisticsService.sumBScoin(EntrustTypeEnum.SELL.getCode(), map,
						coin.getId());

				obj.put("name", coin.getName());
				obj.put("recharge", CoinRecharge);
				obj.put("withdraw", CoinWithdraw);
				obj.put("buy", CoinBuy);
				obj.put("sell", CoinSell);
				array.add(obj);
			}
		}

		modelAndView.addObject("monthDataList", array);
		modelAndView.addObject("month", month);
		return modelAndView;
	}

	/**
	 * 虚拟币钱包
	 *
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("admin/capitalStorage")
	public ModelAndView capitalStorage(@RequestParam(value = "ftype", defaultValue = "-1") Integer type,
			@RequestParam(value = "pageNum", defaultValue = "1") Integer currentPage,
			@RequestParam(value = "orderField", defaultValue = "total") String orderField,
			@RequestParam(value = "orderDirection", defaultValue = "desc") String orderDirection) throws Exception {
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("query/capitalStorage");
		// 搜索关键字
		Pagination<UserCoinWallet> pageParam = new Pagination<UserCoinWallet>(currentPage, Constant.adminPageSize);
		// 排序条件
		pageParam.setOrderField(orderField);
		pageParam.setOrderDirection(orderDirection);

		// 页面参数
		Map<Integer, String> typeMap = redisHelper.getCoinTypeNameSymbolMap();
		typeMap.put(-1, "全部");
		modelAndView.addObject("typeMap", typeMap);

		UserCoinWallet filterParam = new UserCoinWallet();
		// 虚拟币类型
		if (type < 0) {
			return modelAndView;
		} else {
			filterParam.setCoinId(type);
			modelAndView.addObject("ftype", type);
		}

		// 查询
		Pagination<UserCoinWallet> pagination = adminUserCapitalService.selectUserVirtualWalletListByCoin(pageParam,
				filterParam);

		modelAndView.addObject("virtualwalletList", pagination);
		return modelAndView;
	}

	// 导出列名
	private static enum ExportRecordFiled {
		UID, 登录名, 会员昵称, 会员真实姓名, 币种类型, 可用数量, 冻结数量, 锁仓冻结, 创新区充值冻结, 最后修改时间;
	}

	@RequestMapping("/admin/importCapitalStorage")
	@ResponseBody
	public ReturnResult importCapitalStorage(
			@RequestParam(value = "ftype", required = false, defaultValue = "0") Integer coinId,
			@RequestParam(value = "pageNum", required = false, defaultValue = "1") Integer currentPage) {

		final String tableName = "用户存币量";
		HttpServletRequest request = sessionContextUtils.getContextRequest();
		// 存储excel_export_task记录
		FAdmin sessionAdmin = (FAdmin) request.getSession().getAttribute("login_admin");
		ExcelExportTask excelExportTask = new ExcelExportTask();
		excelExportTask.setCreateTime(new Date());
		excelExportTask.setExcelFileName(null);
		excelExportTask.setOperator(sessionAdmin.getFname());
		excelExportTask.setStatus(ExcelExportStatusEnum.EXPORTING.getCode().byteValue());
		excelExportTask.setTableName(tableName);
		long excelExportTaskId = adminExcelExportTaskService.insertSelective(excelExportTask);
		excelExportTask.setId(excelExportTaskId);

		// 异步执行excel导出
		CompletableFuture.supplyAsync(() -> {
			XlsExport e = new XlsExport();
			int rowIndex = 0;

			// header
			e.createRow(rowIndex++);
			for (ExportRecordFiled filed : ExportRecordFiled.values()) {
				e.setCell(filed.ordinal(), filed.toString());
			}

			Pagination<UserCoinWallet> pageParam = new Pagination<UserCoinWallet>(currentPage, 100000);
			UserCoinWallet userCoinWallet = new UserCoinWallet();
			if (coinId > 0) {
				userCoinWallet.setCoinId(coinId);
			}

			// 查询用户持仓
			// 查询
			Pagination<UserCoinWallet> pagination = adminUserCapitalService.importUserVirtualWalletListByCoin(pageParam,
					userCoinWallet);

			Collection<UserCoinWallet> walletList = pagination.getData();
			for (UserCoinWallet element : walletList) {
				e.createRow(rowIndex++);
				for (ExportRecordFiled filed : ExportRecordFiled.values()) {
					switch (filed) {
					case UID:
						e.setCell(filed.ordinal(), element.getUid());
						break;
					case 登录名:
						e.setCell(filed.ordinal(), element.getLoginName());
						break;
					case 会员昵称:
						e.setCell(filed.ordinal(), element.getNickName());
						break;
					case 会员真实姓名:
						e.setCell(filed.ordinal(), element.getRealName());
						break;
					case 币种类型:
						e.setCell(filed.ordinal(), element.getCoinName());
						break;
					case 可用数量:
						e.setCell(filed.ordinal(), FormatUtils.toString10AndstripTrailingZeros(element.getTotal()));
						break;
					case 冻结数量:
						e.setCell(filed.ordinal(), FormatUtils.toString10AndstripTrailingZeros(element.getFrozen()));
						break;
					case 锁仓冻结:
						e.setCell(filed.ordinal(), FormatUtils.toString10AndstripTrailingZeros(element.getBorrow()));
						break;
					case 创新区充值冻结:
						e.setCell(filed.ordinal(),
								FormatUtils.toString10AndstripTrailingZeros(element.getDepositFrozen()));
						break;
					case 最后修改时间:
						e.setCell(filed.ordinal(), element.getGmtModified());
						break;
					default:
						break;
					}
				}
			}
			// 写入到文件
			String fileName = tableName.concat(DateFormatUtils.format(new Date(), "yyyyMMddHHmmss")).concat(".xls");

			e.exportXls(excelRootPath.concat(fileName));
			return fileName;
		}, executor).whenComplete((r, e) -> {
			// 导出成功
			if (r != null) {
				// 更新状态和文件名
				ExcelExportTask updateExcelExportTask = new ExcelExportTask();
				updateExcelExportTask.setId(excelExportTask.getId());
				updateExcelExportTask.setExcelFileName(r);
				updateExcelExportTask.setStatus(ExcelExportStatusEnum.FINISHED.getCode().byteValue());
				updateExcelExportTask.setUpdateTime(new Date());
				adminExcelExportTaskService.updateByIdSelective(updateExcelExportTask);
			} else {
				logger.info("导出失败",e);
				// 更新状态和文件名
				ExcelExportTask updateExcelExportTask = new ExcelExportTask();
				updateExcelExportTask.setId(excelExportTask.getId());
				updateExcelExportTask.setExcelFileName(r);
				updateExcelExportTask.setStatus(ExcelExportStatusEnum.FAILED.getCode().byteValue());
				updateExcelExportTask.setUpdateTime(new Date());
				adminExcelExportTaskService.updateByIdSelective(updateExcelExportTask);
			}

		});

		// e.exportXls(response);
		return ReturnResult.SUCCESS("请从目录(统计管理-报表导出)查看导出任务");
	}

	/**
	 * 用户列表
	 *
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("admin/userRegSort")
	public ModelAndView userRegSort(
			@RequestParam(value = "pageNum", required = false, defaultValue = "1") Integer currentPage,
			@RequestParam(value = "orderField", required = false, defaultValue = "fregistertime") String orderField,
			@RequestParam(value = "orderDirection", required = false, defaultValue = "desc") String orderDirection,
			@RequestParam(value = "startDate", required = false) String startDate,
			@RequestParam(value = "endDate", required = false) String endDate) throws Exception {
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("query/userRegSort");
		// 定义查询条件
		Pagination<FUser> pageParam = new Pagination<FUser>(currentPage, Constant.adminPageSize);
		FUser fUser = new FUser();
		// 参数判断
		if (!org.springframework.util.StringUtils.isEmpty(startDate)) {
			pageParam.setBegindate(startDate);
			modelAndView.addObject("startDate", startDate);
		}
		if (!org.springframework.util.StringUtils.isEmpty(endDate)) {
			pageParam.setEnddate(endDate);
			modelAndView.addObject("endDate", endDate);
		}
		pageParam.setOrderDirection(orderDirection);
		pageParam.setOrderField(orderField);

		Pagination<FUser> pagination = this.adminUserService.selectUserPageList(pageParam, fUser);

		modelAndView.addObject("userList", pagination);
		return modelAndView;
	}

	public Map<String, Object> getBeginEndDate(Date date) {

		Map<String, Object> map = new HashMap<String, Object>();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

		Calendar cal = Calendar.getInstance();
		cal.setTime(date);

		cal.set(Calendar.DAY_OF_MONTH, 1);
		String beginDate = sdf.format(cal.getTime());

		int i = cal.getActualMaximum(Calendar.DAY_OF_MONTH);

		cal.add(Calendar.DAY_OF_MONTH, i);
		String endDate = sdf.format(cal.getTime());

		map.put("beginDate", beginDate);
		map.put("endDate", endDate);

		return map;
	}

	@RequestMapping("capital/goCapitalPage")
	public ModelAndView userEdit(@RequestParam(value = "uid", required = false) Integer uid,
			@RequestParam(value = "url", required = false) String url) {
		ModelAndView modelAndView = new ModelAndView();
		if (uid > 0) {
			Map<Integer, String> coinMap = redisHelper.getCoinTypeNameMap();
			modelAndView.addObject("coinMap", coinMap);
			modelAndView.addObject("uid", uid);
		}
		modelAndView.setViewName(url);
		return modelAndView;
	}

	@RequestMapping("/admin/updateSubmitUserCapital")
	@ResponseBody
	public ReturnResult updateSubmitUserCapital(@RequestParam(value = "uid", required = true) Integer fid,
			@RequestParam(value = "fcoinid", required = true) Integer fcoinid,
			@RequestParam(value = "amount", required = true) BigDecimal amount) throws Exception {

		// 查询钱包
		if (fid == null || fcoinid == null || amount == null) {
			return ReturnResult.FAILUER("参数不合法");
		}
		// 更新钱包
		if (adminUserCapitalService.updateUserWallet(fid, fcoinid, amount)) {
			HttpServletRequest request = sessionContextUtils.getContextRequest();
			String ip = HttpRequestUtils.getIPAddress();
			FAdmin sessionAdmin = (FAdmin) request.getSession().getAttribute("login_admin");
			mqSend.SendAdminAction(sessionAdmin.getFagentid(), sessionAdmin.getFid(), fid,
					LogAdminActionEnum.MODIFY_CAPITAL_BALANCE, ip, "修改资金不平衡,fcoinid:" + fcoinid + ",amount:" + amount);

			return ReturnResult.SUCCESS("修改成功");
		}

		return ReturnResult.FAILUER("修改失败");
	}

	/**
	 * 资产不平衡页面
	 *
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("admin/assetInbalance")
	public ModelAndView getAssetInbalancePage(
			@RequestParam(value = "pageNum", required = false, defaultValue = "1") Integer pageNum,
			@RequestParam(value = "userId", required = false, defaultValue = "0") Integer userId,
			@RequestParam(value = "coinId", required = false, defaultValue = "0") Integer coinId,
			@RequestParam(value = "filterApi", required = false ,defaultValue = "on") String filterApi) {
		try {
			ModelAndView modelAndView = new ModelAndView();
			modelAndView.setViewName("query/assetImbalance");
			AssetImbalance assetImbalance = new AssetImbalance();
			if (userId > 0) {
				modelAndView.addObject("userId", userId);
				assetImbalance.setUserId(userId);
			}
			if (coinId > 0) {
				modelAndView.addObject("coinId", coinId);
				assetImbalance.setCoinId(coinId);
			}
			if (filterApi != null && filterApi.equals("on")) {
				assetImbalance.setFilterApi(true);
				modelAndView.addObject("filterApi",true);
			}else{
				assetImbalance.setFilterApi(false);
				modelAndView.addObject("filterApi",false);
			}
			
			List<SystemCoinType> coinTypeList = redisHelper.getCoinTypeListAll();
			Map<Integer, String> coinTypeMap = new HashMap<>();
			coinTypeMap.put(0, "全部");
			for (SystemCoinType systemCoinType : coinTypeList) {
				coinTypeMap.put(systemCoinType.getId(), systemCoinType.getName());
			}
			modelAndView.addObject("coinTypeMap", coinTypeMap);
			PageInfo<AssetImbalance> selectAssetImbalancePage = adminUserCapitalService
					.selectAssetImbalancePage(assetImbalance, pageNum, Constant.adminPageSize);
			modelAndView.addObject("AssetImbalanceList", selectAssetImbalancePage);
			return modelAndView;
		} catch (Exception e) {
			logger.error("访问capital/assetInbalance异常，userId：" + userId + ",coinId:" + coinId, e);
			ModelAndView modelAndView = new ModelAndView();
			modelAndView.setViewName("comm/404");
			return modelAndView;
		}
	}

	/**
	 * 资产不平衡页面
	 *
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("admin/depositFrozenImbalance")
	public ModelAndView depositFrozenInbalance(
			@RequestParam(value = "pageNum", required = false, defaultValue = "1") Integer pageNum,
			@RequestParam(value = "userId", required = false, defaultValue = "0") Integer userId,
			@RequestParam(value = "coinId", required = false, defaultValue = "0") Integer coinId) {
		try {
			ModelAndView modelAndView = new ModelAndView();
			modelAndView.setViewName("query/depositFrozenImbalance");

			DepositFrozenImbalance param = new DepositFrozenImbalance();
			if (userId > 0) {
				modelAndView.addObject("userId", userId);
				param.setUserId(userId);
			}
			if (coinId > 0) {
				modelAndView.addObject("coinId", coinId);
				param.setCoinId(coinId);
			}
			List<SystemCoinType> coinTypeList = redisHelper.getCoinTypeListAll();
			Map<Integer, String> coinTypeMap = new HashMap<>();
			coinTypeMap.put(0, "全部");
			for (SystemCoinType systemCoinType : coinTypeList) {
				coinTypeMap.put(systemCoinType.getId(), systemCoinType.getName());
			}
			modelAndView.addObject("coinTypeMap", coinTypeMap);
			PageInfo<DepositFrozenImbalance> selectdepositFrozenImbalancePage = adminUserCapitalService
					.selectdepositFrozenImbalancePage(param, pageNum, Constant.adminPageSize);
			modelAndView.addObject("depositFrozenImbalanceList", selectdepositFrozenImbalancePage);
			return modelAndView;
		} catch (Exception e) {
			logger.error("访问capital/assetInbalance异常，userId：" + userId + ",coinId:" + coinId, e);
			ModelAndView modelAndView = new ModelAndView();
			modelAndView.setViewName("comm/404");
			return modelAndView;
		}

	}

}
