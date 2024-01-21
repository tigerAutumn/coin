package com.qkwl.web.front.controller.v2;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;

import javax.validation.Valid;

import com.qkwl.common.dto.Enum.*;
import com.qkwl.web.utils.ApiUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.qkwl.common.dto.NewCoinCountdownResp;
import com.qkwl.common.dto.TradeInfoResp;
import com.qkwl.common.dto.Enum.EntrustStateEnum;
import com.qkwl.common.dto.Enum.ErrorCodeEnum;
import com.qkwl.common.dto.Enum.OrderTypeEnum;
import com.qkwl.common.dto.Enum.SystemTradeStatusEnum;
import com.qkwl.common.dto.Enum.SystemTradeTypeEnum;
import com.qkwl.common.dto.Enum.SystemTradeTypeNewEnum;
import com.qkwl.common.dto.coin.SystemCoinType;
import com.qkwl.common.dto.coin.SystemTradeType;
import com.qkwl.common.dto.common.DeviceInfo;
import com.qkwl.common.dto.common.Pagination;
import com.qkwl.common.dto.entrust.FEntrust;
import com.qkwl.common.dto.entrust.FEntrustHistory;
import com.qkwl.common.dto.entrust.FEntrustLog;
import com.qkwl.common.dto.market.FPeriod;
import com.qkwl.common.dto.market.TickerData;
import com.qkwl.common.dto.user.FUser;
import com.qkwl.common.dto.wallet.UserCoinWallet;
import com.qkwl.common.exceptions.BizException;
import com.qkwl.common.framework.redis.RedisHelper;
import com.qkwl.common.match.MathUtils;
import com.qkwl.common.redis.RedisConstant;
import com.qkwl.common.result.Result;
import com.qkwl.common.rpc.capital.IUserWalletService;
import com.qkwl.common.rpc.entrust.EntrustHistoryService;
import com.qkwl.common.rpc.entrust.EntrustOrderService;
import com.qkwl.common.rpc.entrust.IEntrustServer;
import com.qkwl.common.rpc.push.PushService;
import com.qkwl.common.util.Constant;
import com.qkwl.common.util.I18NUtils;
import com.qkwl.common.util.RespData;
import com.qkwl.common.util.ReturnResult;
import com.qkwl.common.util.Utils;
import com.qkwl.web.config.MqttProperties;
import com.qkwl.web.dto.GetOneTradeInfoReq;
import com.qkwl.web.elasticsearch.SystemTradeTypeRepository;
import com.qkwl.web.front.controller.base.JsonBaseController;
import com.qkwl.web.permission.annotation.PassToken;
import com.qkwl.web.utils.CnyUtils;
import com.qkwl.web.utils.WebConstant;

import io.swagger.annotations.ApiOperation;
import springfox.documentation.annotations.ApiIgnore;

/**
 * 订单相关接口，包括：下单、撤单、获取订单列表、当前订单、历史订单等。
 */
@RestController
@ApiIgnore
public class TradeApiV2Controller extends JsonBaseController {

	private static final Logger logger = LoggerFactory.getLogger(TradeApiV2Controller.class);

	@Autowired
	private RedisHelper redisHelper;

	@Autowired
	private PushService pushService;

	@Autowired
	private IUserWalletService userWalletService;

	@Autowired
	private EntrustHistoryService entrustHistoryService;

	@Autowired
	private IEntrustServer entrustServer;

	@Autowired
	private MqttProperties mqttProperties;

	@Autowired
	private EntrustOrderService entrustOrderService;

	@Autowired
	@Qualifier("systemTradeTypeRepository")
	private SystemTradeTypeRepository systemTradeTypeRepository;

	// web自选币种
	@ApiOperation("")
	@RequestMapping(value = "/v2/collect/list", method = { RequestMethod.GET, RequestMethod.POST })
	public ReturnResult MarketJsons() {
		FUser user = getUser();
		if (user == null) {
			return ReturnResult.FAILUER(ReturnResult.FAULURE_USER_NOT_LOGIN, I18NUtils.getString("com.public.error.10001"));
		}

		if (!StringUtils.isEmpty(user.getfFavoriteTradeList())) {
			JSONArray parseArray = JSON.parseArray(user.getfFavoriteTradeList());
			List<SystemTradeType> tradeTypeSort = redisHelper.getTradeTypeShareByTradeIds(parseArray, WebConstant.BCAgentId);

			JSONArray array = new JSONArray();
			for (SystemTradeType systemTradeType : tradeTypeSort) {
				JSONObject jsonObject = new JSONObject();
				jsonObject.put("tradeId", systemTradeType.getId());
				jsonObject.put("id", systemTradeType.getId());
				jsonObject.put("buySymbol", systemTradeType.getBuyShortName());
				jsonObject.put("buyShortName", systemTradeType.getBuyShortName());
				jsonObject.put("sellSymbol", systemTradeType.getSellShortName());
				jsonObject.put("sellShortName", systemTradeType.getSellShortName());
				jsonObject.put("image", systemTradeType.getSellWebLogo());
				jsonObject.put("digit", systemTradeType.getDigit());
				if (ApiUtils.isTradeOpen(systemTradeType)) {
				    jsonObject.put("isOpen","1");
                }
                else {
                    jsonObject.put("isOpen","0");
                }

				// 最新价格
				TickerData tickerData = pushService.getTickerData(systemTradeType.getId());
				if (tickerData == null) {
					jsonObject.put("p_new", 0);
					jsonObject.put("p_open", 0);
					jsonObject.put("cny", 0);
					jsonObject.put("total", 0d);
					jsonObject.put("buy", 0d);
					jsonObject.put("sell", 0d);
					jsonObject.put("rose", 0d);
				} else {
					jsonObject.put("p_new", tickerData.getLast());
					jsonObject.put("p_open", tickerData.getKai());
					// BTC交易区
					if (systemTradeType.getType().equals(1)) {
						jsonObject.put("cny", tickerData.getLast());
					} else if (systemTradeType.getType().equals(2)) {
						BigDecimal cny = getCny(8, tickerData.getLast());
						jsonObject.put("cny", cny);
					}
					// ETH交易区
					else if (systemTradeType.getType().equals(3)) {
						BigDecimal cny = getCny(11, tickerData.getLast());
						jsonObject.put("cny", cny);
					} else if (systemTradeType.getType().equals(4)) {
						BigDecimal cny = getCny(57, tickerData.getLast());
						jsonObject.put("cny", cny);
					} else if (systemTradeType.getType().equals(5)) {
						BigDecimal cny = getCnyByCoinId(systemTradeType.getBuyCoinId(), systemTradeType.getSellCoinId(), tickerData.getLast());
						jsonObject.put("cny", cny);
					}
					jsonObject.put("total", tickerData.getVol() == null ? 0d : tickerData.getVol());
					jsonObject.put("buy", tickerData.getBuy() == null ? 0d : tickerData.getLow());
					jsonObject.put("sell", tickerData.getSell() == null ? 0d : tickerData.getHigh());
					jsonObject.put("rose", tickerData.getChg() == null ? 0d : tickerData.getChg());
				}
				array.add(jsonObject);
			}
			return ReturnResult.SUCCESS(array);
		} else {
			// 如果是空的则返回空的币种列表
			List<SystemTradeType> tradeTypeSort = new ArrayList<>();
			return ReturnResult.SUCCESS(tradeTypeSort);
		}
	}

	public BigDecimal getCny(int tradeId, BigDecimal p_new) {
		// 取BTC/GSET交易对价格计算
		BigDecimal cny = redisHelper.getLastPrice(tradeId);

		// 当前交易对最新价格
		BigDecimal money = MathUtils.mul(p_new, cny);
		BigDecimal newMoney = MathUtils.toScaleNum(money, 2);
		if (newMoney.compareTo(BigDecimal.ZERO) <= 0) {
			return CnyUtils.validateCny(money);
		}
		return newMoney;
	}

	@PassToken
	@ApiOperation("")
	@RequestMapping(value = "/v2/trademarket", method = { RequestMethod.GET, RequestMethod.POST })
	public ReturnResult tradeMarket(@RequestParam(required = true, defaultValue = "0") Integer tradeId, @RequestParam(value = "type", required = false, defaultValue = "1") Integer type) {
		JSONObject jsonObject = new JSONObject();
		SystemTradeType systemTradeType = redisHelper.getTradeType(tradeId, WebConstant.BCAgentId);
		if (systemTradeType == null || systemTradeType.getStatus().equals(SystemTradeStatusEnum.ABNORMAL.getCode()) || !systemTradeType.getIsShare()) {
			systemTradeType = redisHelper.getTradeTypeFirst(type, WebConstant.BCAgentId);
			if (systemTradeType == null) {
				return ReturnResult.FAILUER("");
			}
			tradeId = systemTradeType.getId();
		}
		// 小数位处理(默认价格2位，数量4位)
		String digit = StringUtils.isEmpty(systemTradeType.getDigit()) ? "2#4" : systemTradeType.getDigit();
		String[] digits = digit.split("#");
		jsonObject.put("cnyDigit", Integer.valueOf(digits[0]));
		jsonObject.put("coinDigit", Integer.valueOf(digits[1]));
		FUser fuser = getCurrentUserInfoByToken();
		if (fuser != null) {
			jsonObject.put("isTelephoneBind", fuser.getFistelephonebind());
			jsonObject.put("tradePassword", fuser.getFtradepassword() == null);
			jsonObject.put("needTradePasswd", redisHelper.getNeedTradePassword(fuser.getFid()));
			jsonObject.put("login", true);

			List<UserCoinWallet> userCoinWallets = userWalletService.listUserCoinWallet(fuser.getFid());
			Iterator<UserCoinWallet> iterator = userCoinWallets.iterator();
			while (iterator.hasNext()) {
				UserCoinWallet wallet = (UserCoinWallet) iterator.next();
				if (!redisHelper.hasCoinId(wallet.getCoinId())) {
					iterator.remove();
				}
			}
			BigDecimal totalAssets = getTotalAssets(userCoinWallets);
			BigDecimal btcPrice = redisHelper.getLastPrice(8);
			BigDecimal btcAssets = MathUtils.div(totalAssets, btcPrice);

			jsonObject.put("netAssets", getNetAssets(userCoinWallets));
			jsonObject.put("totalAssets", totalAssets);
			jsonObject.put("btcAssets", btcAssets);
		} else {
			jsonObject.put("login", false);
		}

		jsonObject.put("isPlatformStatus", systemTradeType.getStatus() == SystemTradeStatusEnum.NORMAL.getCode());
//        jsonObject.put("coinInfo",redisHelper.getCoinInfo(systemTradeType.getSellCoinId(),super.getLanEnum().getCode()+""));
		return ReturnResult.SUCCESS(jsonObject);
	}

	// k线交易页委单和资产
	@ApiOperation("")
	@RequestMapping(value = "/v2/getEntruts", method = { RequestMethod.GET, RequestMethod.POST })
	public ReturnResult EntrutsJson(@RequestParam(required = false, defaultValue = "0") Integer symbol, @RequestParam(required = false, defaultValue = "6") Integer count, @RequestParam(required = false) Integer type) throws Exception {
		JSONObject result = new JSONObject();
		FUser fuser = super.getCurrentUserInfoByToken();
		if (null == fuser) {
			return ReturnResult.FAILUER(ReturnResult.FAULURE_USER_NOT_LOGIN, I18NUtils.getString("TradeApiV2Controller.33"));
		}
		SystemTradeType tradeType = redisHelper.getTradeType(symbol, WebConstant.BCAgentId);
		if (tradeType == null) {
			return ReturnResult.FAILUER(I18NUtils.getString("TradeApiV2Controller.34"));
		}
		SystemCoinType buyCoinType = pushService.getSystemCoinType(tradeType.getBuyCoinId());
		SystemCoinType sellCoinType = pushService.getSystemCoinType(tradeType.getSellCoinId());
		if (buyCoinType == null || sellCoinType == null) {
			return ReturnResult.FAILUER(I18NUtils.getString("TradeApiV2Controller.35"));
		}
			List<Integer> stateList = new ArrayList<>();
			// 未成交前6条
			FEntrust curEntrust = new FEntrust();
			curEntrust.setFuid(fuser.getFid());
			curEntrust.setFtradeid(symbol);
			curEntrust.setFagentid(fuser.getFagentid());
			curEntrust.setFtype(type);
			stateList.add(EntrustStateEnum.Going.getCode());
			stateList.add(EntrustStateEnum.PartDeal.getCode());
			stateList.add(EntrustStateEnum.WAITCancel.getCode());
			Pagination<FEntrust> curParam = new Pagination<>(0, count);
			curParam = this.entrustServer.listEntrust(curParam, curEntrust, stateList);
			JSONArray entrutsCur = new JSONArray();
			for (FEntrust fEntrust : curParam.getData()) {
				JSONObject entruts = new JSONObject();
				copyJsonEntrust(entruts,fEntrust);

				entruts.put("buysymbol", buyCoinType.getShortName());
				entruts.put("sellsymbol", sellCoinType.getShortName());
				entrutsCur.add(entruts);
			}
			result.put("entrutsCur", entrutsCur);
			// 成交前6条
			stateList.clear();
			stateList.add(EntrustStateEnum.AllDeal.getCode());
			stateList.add(EntrustStateEnum.Cancel.getCode());
			FEntrustHistory hisEntrust = new FEntrustHistory();
			hisEntrust.setFuid(fuser.getFid());
			hisEntrust.setFtradeid(symbol);
			hisEntrust.setFagentid(fuser.getFagentid());
			hisEntrust.setFtype(type);
			Pagination<FEntrustHistory> hisParam = new Pagination<>(0, count);
			hisParam = this.entrustHistoryService.listEntrustHistory(hisParam, hisEntrust, stateList);
			JSONArray entrutsHis = new JSONArray();
			for (FEntrustHistory fEntrust : hisParam.getData()) {
				JSONObject entruts = new JSONObject();
                copyJsonEntrustHis(entruts,fEntrust);

				entruts.put("buysymbol", buyCoinType.getShortName());
				entruts.put("sellsymbol", sellCoinType.getShortName());
				entrutsHis.add(entruts);
			}
			result.put("entrutsHis", entrutsHis);
			return ReturnResult.SUCCESS(I18NUtils.getString("TradeApiV2Controller.76"), result);
	}

	/**
	 * 批量撤单 2018年9月17日 web端新增批量撤单接口
	 */
	@ApiOperation("")
	@RequestMapping(value = "/v2/batch_cny_cancel", method = { RequestMethod.GET, RequestMethod.POST })
	public ReturnResult cancelEntrusts(@RequestParam(required = true) String ids, @RequestParam(required = true) int type) {
		FUser fuser = super.getCurrentUserInfoByToken();
			if (StringUtils.isNotEmpty(ids)) {
				String idsArr[] = ids.split(",");
				List<String> list = Arrays.asList(idsArr);
				Result result = entrustServer.cancelEntrustBatch(fuser.getFid(), list, type);
				if (result.getSuccess()) {
					return ReturnResult.SUCCESS(I18NUtils.getString("trade.cancel.order." + result.getCode()));
				} else if (result.getCode().equals(Result.PARAM)) {
					logger.error("tradeCoinBuy is param error, {}", result.getMsg());
				} else if (result.getCode() < 10000) {
					return ReturnResult.FAILUER(I18NUtils.getString("trade.cancel.order." + result.getCode().toString(), result.getData().toString()));
				} else {
					return ReturnResult.FAILUER(I18NUtils.getString("com.error." + result.getCode().toString(), result.getData().toString()));
				}
			}
		return ReturnResult.FAILUER(I18NUtils.getString("com.public.error.10000"));
	}

	/**
	 * @deprecated
	 * @param tradeId
	 * @param page
	 * @param pageSize
	 * @param type
	 * @param startTime
	 * @param endTime
	 * @return
	 * @throws Exception
	 */
	// 订单-当前委单列表
	@ApiOperation("")
	@RequestMapping(value = "/v2/curEntrutList", method = { RequestMethod.GET, RequestMethod.POST })
	public ReturnResult curEntrutList(@RequestParam(required = false) Integer tradeId,
                                      @RequestParam(required = false, defaultValue = "0") Integer page,
                                      @RequestParam(required = false, defaultValue = "10") Integer pageSize,
                                      @RequestParam(required = false) Integer type,
                                      @RequestParam(required = false) String startTime,
                                      @RequestParam(required = false) String endTime) throws Exception {
		FUser fuser = super.getCurrentUserInfoByToken();
		if (null == fuser) {
			throw new BizException(ErrorCodeEnum.PLEASE_LOGIN);
		}

			List<Integer> stateList = new ArrayList<>();
			// 未成交前6条
			FEntrust curEntrust = new FEntrust();
			curEntrust.setFuid(fuser.getFid());
			curEntrust.setFtradeid(tradeId);
			curEntrust.setFagentid(fuser.getFagentid());
			curEntrust.setFtype(type);
			stateList.add(EntrustStateEnum.Going.getCode());
			stateList.add(EntrustStateEnum.PartDeal.getCode());
			stateList.add(EntrustStateEnum.WAITCancel.getCode());
			Pagination<FEntrust> curParam = new Pagination<>(page, pageSize);
			curParam.setBegindate(startTime);
			curParam.setEnddate(endTime);
			curParam.setRedirectUrl("https://www.hotcoin.top");
			curParam = this.entrustServer.listEntrust(curParam, curEntrust, stateList);
			JSONArray entrutsCur = new JSONArray();

			JSONObject result = new JSONObject();
			if (curParam != null && curParam.getData() != null && curParam.getData().size() > 0) {
				for (FEntrust fEntrust : curParam.getData()) {
					JSONObject entruts = new JSONObject();
					copyJsonEntrust(entruts,fEntrust);
//					entruts.put("id", fEntrust.getFid());
//					entruts.put("time", Utils.dateFormat(new Timestamp(fEntrust.getFcreatetime().getTime())));
//					entruts.put("types", I18NUtils.getString("trade.enum.entrusttype" + fEntrust.getFtype())); //$NON-NLS-2$
//					entruts.put("source", fEntrust.getFsource_s());
//					entruts.put("price", fEntrust.getFprize());
//					entruts.put("count", fEntrust.getFcount());
//
//					entruts.put("amount", fEntrust.getFamount()); // 总价
//					entruts.put("ncount", fEntrust.getFleftcount()); // 未成交量
//
//					entruts.put("leftcount", MathUtils.sub(fEntrust.getFcount(), fEntrust.getFleftcount()));
//					entruts.put("last", fEntrust.getFlast());
//					entruts.put("successamount", fEntrust.getFsuccessamount());
//					entruts.put("fees", fEntrust.getFfees());
//					entruts.put("statusCode", fEntrust.getFstatus());
//					entruts.put("status", I18NUtils.getString("trade.enum.entruststate" + fEntrust.getFstatus())); //$NON-NLS-2$
//					entruts.put("type", fEntrust.getFtype());

					SystemCoinType buyCoinType = pushService.getSystemCoinType(fEntrust.getFbuycoinid());
					SystemCoinType sellCoinType = pushService.getSystemCoinType(fEntrust.getFsellcoinid());

					entruts.put("buysymbol", buyCoinType.getShortName());
					entruts.put("sellsymbol", sellCoinType.getShortName());
					entrutsCur.add(entruts);
				}
				result.put("count", curParam.getTotalRows());
			} else {
				result.put("count", 0);
			}

			result.put("list", entrutsCur);
			return ReturnResult.SUCCESS(I18NUtils.getString("TradeApiV2Controller.109"), result);
	}

	/**
	 * @deprecated
	 * @param tradeId
	 * @param page
	 * @param pageSize
	 * @param type
	 * @param startTime
	 * @param endTime
	 * @param status
	 * @return
	 * @throws Exception
	 */
	// 订单-历史委单列表
	@ApiOperation("")
	@RequestMapping(value = "/v2/historyEntrutList", method = { RequestMethod.GET, RequestMethod.POST })
	public ReturnResult historyEntrutList(
			@RequestParam(required = false) Integer tradeId,
			@RequestParam(required = false, defaultValue = "0") Integer page,
			@RequestParam(required = false, defaultValue = "10") Integer pageSize,
			@RequestParam(required = false) Integer type,
			@RequestParam(required = false) String startTime,
			@RequestParam(required = false) String endTime,
			@RequestParam(required = false) Integer status,
			@RequestParam(required = false) String limited) throws Exception {
		FUser fuser = super.getCurrentUserInfoByToken();
		if (null == fuser) {
			return ReturnResult.FAILUER(ReturnResult.FAULURE_USER_NOT_LOGIN, I18NUtils.getString("TradeApiV2Controller.111"));
		}
			// 成交前6条
			List<Integer> stateList = new ArrayList<>();
			if (status != null && status > 0) {
				stateList.add(status);
			} else {
				stateList = null;
			}

			FEntrustHistory hisEntrust = new FEntrustHistory();
			hisEntrust.setFuid(fuser.getFid());
			hisEntrust.setFtradeid(tradeId);
			hisEntrust.setFagentid(fuser.getFagentid());
			hisEntrust.setFtype(type);
			if(limited.equals("0")) {
				hisEntrust.setFmatchtype(MatchTypeEnum.LIMITE.getCode());
			}
			if (limited.equals("1")) {
				hisEntrust.setFmatchtype(MatchTypeEnum.MARKET.getCode());
			}
			Pagination<FEntrustHistory> hisParam = new Pagination<>(page, pageSize);
			hisParam.setBegindate(startTime);
			hisParam.setEnddate(endTime);
			hisParam.setRedirectUrl("https://www.hotcoin.top");
			hisParam = this.entrustHistoryService.listEntrustHistory(hisParam, hisEntrust, stateList);
			JSONArray entrutsHis = new JSONArray();

			if (hisParam != null && hisParam.getData() != null && hisParam.getData().size() > 0) {
				for (FEntrustHistory fEntrust : hisParam.getData()) {
					JSONObject entruts = new JSONObject();
                    copyJsonEntrustHis(entruts,fEntrust);
//					entruts.put("id", fEntrust.getFid());
//					entruts.put("time", Utils.dateFormat(new Timestamp(fEntrust.getFcreatetime().getTime())));
//					entruts.put("types", I18NUtils.getString("trade.enum.entrusttype" + fEntrust.getFtype())); //$NON-NLS-2$
//					entruts.put("source", fEntrust.getFsource_s());
//					entruts.put("price", fEntrust.getFprize());
//					entruts.put("count", fEntrust.getFcount());
//
//					entruts.put("amount", fEntrust.getFamount()); // 总价
//					entruts.put("ncount", fEntrust.getFleftcount()); // 未成交量
//
//					// 成交量

//					entruts.put("leftcount", successCount);
//					entruts.put("last", fEntrust.getFlast());
//					entruts.put("successamount", fEntrust.getFsuccessamount());
//					entruts.put("fees", fEntrust.getFfees());
//					entruts.put("statusCode", fEntrust.getFstatus());
//					entruts.put("status", I18NUtils.getString("trade.enum.entruststate" + fEntrust.getFstatus())); //$NON-NLS-2$
//					entruts.put("type", fEntrust.getFtype());
                    BigDecimal successCount = MathUtils.sub(fEntrust.getFcount(), fEntrust.getFleftcount());
					SystemCoinType buyCoinType = pushService.getSystemCoinType(fEntrust.getFbuycoinid());
					SystemCoinType sellCoinType = pushService.getSystemCoinType(fEntrust.getFsellcoinid());

					entruts.put("buysymbol", buyCoinType.getShortName());
					entruts.put("sellsymbol", sellCoinType.getShortName());

					// 成交均价
					BigDecimal averagePrice = MathUtils.div(fEntrust.getFsuccessamount(), successCount);
                    averagePrice =  MathUtils.toScaleNum(averagePrice,MathUtils.DEF_CNY_SCALE);
					entruts.put("last", averagePrice);
					entruts.put("averagePrice",averagePrice);

					List<FEntrustLog> fentrustlogs = entrustServer.getEntrustLog(fEntrust.getFentrustid());
					if (fentrustlogs != null && fentrustlogs.size() > 0) {
						JSONArray array = new JSONArray();
						for (FEntrustLog fEntrustLog : fentrustlogs) {
							JSONObject jsonObject = new JSONObject();
							jsonObject.put("price", fEntrustLog.getFprize());
							jsonObject.put("count", fEntrustLog.getFcount());
							jsonObject.put("amount", fEntrustLog.getFamount());
							jsonObject.put("fee", fEntrust.getFfees());
							jsonObject.put("time", Utils.dateFormat(new Timestamp(fEntrustLog.getFcreatetime().getTime())));
							array.add(jsonObject);
						}
						entruts.put("entrustLog", array);
					}

					entrutsHis.add(entruts);
				}
			}

			JSONObject result = new JSONObject();
			if (hisParam != null) {
				result.put("count", hisParam.getTotalRows());
			} else {
				result.put("count", 0);
			}

			result.put("list", entrutsHis);
			return ReturnResult.SUCCESS(I18NUtils.getString("TradeApiV2Controller.142"), result);
	}

	// 订单-历史委单列表
	@ApiOperation("")
	@RequestMapping(value = "/v2/entrustDetailList", method = { RequestMethod.GET, RequestMethod.POST })
	public ReturnResult entrustDetailList(@RequestParam(required = false) Integer tradeId, @RequestParam(required = false, defaultValue = "0") Integer page, @RequestParam(required = false, defaultValue = "10") Integer pageSize, @RequestParam(required = false) Integer type, @RequestParam(required = false) String startTime, @RequestParam(required = false) String endTime, @RequestParam(required = false) Integer status) {
		FUser fuser = super.getCurrentUserInfoByToken();
		if (null == fuser) {
			return ReturnResult.FAILUER(ReturnResult.FAULURE_USER_NOT_LOGIN, I18NUtils.getString("TradeApiV2Controller.144"));
		}
		try {
			// 成交前6条
			List<Integer> stateList = new ArrayList<>();
			if (status != null && status > 0) {
				stateList.add(status);
			} else {
				stateList = null;
			}

			FEntrustHistory hisEntrust = new FEntrustHistory();
			hisEntrust.setFuid(fuser.getFid());
			hisEntrust.setFtradeid(tradeId);
			hisEntrust.setFagentid(fuser.getFagentid());
			hisEntrust.setFtype(type);
			Pagination<FEntrustHistory> hisParam = new Pagination<>(page, pageSize);
			hisParam.setBegindate(startTime);
			hisParam.setEnddate(endTime);
			hisParam.setRedirectUrl("https://www.hotcoin.top");
			hisParam = this.entrustHistoryService.listEntrustHistory(hisParam, hisEntrust, stateList);
			JSONArray entrutsHis = new JSONArray();

			if (hisParam != null && hisParam.getData() != null && hisParam.getData().size() > 0) {
				for (FEntrustHistory fEntrust : hisParam.getData()) {
					JSONObject entruts = new JSONObject();
					entruts.put("id", fEntrust.getFid());
					entruts.put("time", Utils.dateFormat(new Timestamp(fEntrust.getFcreatetime().getTime())));
					entruts.put("types", I18NUtils.getString("trade.enum.entrusttype" + fEntrust.getFtype())); //$NON-NLS-2$
					entruts.put("source", fEntrust.getFsource_s());
					entruts.put("price", fEntrust.getFprize());
					entruts.put("count", fEntrust.getFcount());

					entruts.put("amount", fEntrust.getFamount()); // 总价
					entruts.put("ncount", fEntrust.getFleftcount()); // 未成交量

					entruts.put("leftcount", MathUtils.sub(fEntrust.getFcount(), fEntrust.getFleftcount()));
					entruts.put("last", fEntrust.getFlast());
					entruts.put("successamount", fEntrust.getFsuccessamount());
					entruts.put("fees", fEntrust.getFfees());
					entruts.put("statusCode", fEntrust.getFstatus());
					entruts.put("status", I18NUtils.getString("trade.enum.entruststate" + fEntrust.getFstatus())); //$NON-NLS-2$
					entruts.put("type", fEntrust.getFtype());
					SystemCoinType buyCoinType = pushService.getSystemCoinType(fEntrust.getFbuycoinid());
					SystemCoinType sellCoinType = pushService.getSystemCoinType(fEntrust.getFsellcoinid());

					entruts.put("buysymbol", buyCoinType.getShortName());
					entruts.put("sellsymbol", sellCoinType.getShortName());

					List<FEntrustLog> fentrustlogs = entrustServer.getEntrustLog(fEntrust.getFentrustid());
					if (fentrustlogs != null && fentrustlogs.size() > 0) {
						JSONArray array = new JSONArray();
						for (FEntrustLog fEntrustLog : fentrustlogs) {
							JSONObject jsonObject = new JSONObject();
							jsonObject.put("price", fEntrustLog.getFprize());
							jsonObject.put("count", fEntrustLog.getFcount());
							jsonObject.put("amount", fEntrustLog.getFamount());
							jsonObject.put("fee", fEntrust.getFfees());
							jsonObject.put("time", Utils.dateFormat(new Timestamp(fEntrustLog.getFcreatetime().getTime())));
							array.add(jsonObject);
						}
						entruts.put("entrustLog", array);
					}

					entrutsHis.add(entruts);
				}
			}

			JSONObject result = new JSONObject();
			if (hisParam != null) {
				result.put("count", hisParam.getTotalRows());
			} else {
				result.put("count", 0);
			}

			result.put("list", entrutsHis);
			return ReturnResult.SUCCESS(I18NUtils.getString("TradeApiV2Controller.174"), result);
		} catch (Exception e) {
			logger.error(e.getMessage());
			return ReturnResult.FAILUER(I18NUtils.getString("TradeApiV2Controller.175"));
		}
	}

	/**
	 * 获取深度数据
	 * 
	 * @param tradeId
	 * @return
	 * @throws Exception
	 */
	@PassToken
	@ApiOperation("")
	@RequestMapping(value = "/v2/fullDepth", method = { RequestMethod.GET, RequestMethod.POST })
	public ReturnResult fulldepth(@RequestParam(required = false, defaultValue = "0") int tradeId) {
		JSONObject jsonObject = new JSONObject();
		JSONObject returnObject = new JSONObject();
		if (tradeId == 0) {
			return ReturnResult.FAILUER(I18NUtils.getString("TradeApiV2Controller.176"));
		}

		DeviceInfo deviceInfo = getDeviceInfo();

		SystemTradeType tradeType = pushService.getSystemTradeType(tradeId);
		if (null == tradeType) {
			return ReturnResult.FAILUER(I18NUtils.getString("TradeApiV2Controller.177"));
		}

		if (!tradeType.getStatus().equals(SystemTradeStatusEnum.ABNORMAL.getCode())) {
			// 小数位处理(默认价格2位，数量4位)
			// 小数位处理(默认价格2位，数量4位)
			String digit = StringUtils.isEmpty(tradeType.getDigit()) ? "2#4" : tradeType.getDigit();
			String[] digits = digit.split("#");
			int cnyDigit = Integer.valueOf(digits[0]);
			int coinDigit = Integer.valueOf(digits[1]);

			JSONArray buyDepth = pushService.getBuyDepthJson(tradeId);
			// 数据过滤
			JSONArray newBuyDepth = new JSONArray();
			BigDecimal allCount = BigDecimal.ZERO;
			for (Object object : buyDepth) {
				JSONArray array = JSON.parseArray(object.toString());

				JSONArray newArray = new JSONArray();
				if (Double.valueOf(array.get(1).toString()) > 0d) {
					// 单价
					BigDecimal price = new BigDecimal(array.get(0).toString());
					// 数量
					BigDecimal count = new BigDecimal(array.get(1).toString());

					newArray.add(MathUtils.toScaleNum(price, cnyDigit));
					newArray.add(MathUtils.toScaleNum(count, coinDigit));
					allCount = MathUtils.add(allCount, count);
					newArray.add(MathUtils.toScaleNum(allCount, coinDigit));
					newBuyDepth.add(newArray);
				}
			}

			JSONArray sellDepth = pushService.getSellDepthJson(tradeId);
			List<Object> list = JSONArray.parseArray(sellDepth.toJSONString(), Object.class);
			// 数据过滤
			List<Object> newSellDepth = new JSONArray();
			BigDecimal allSellCount = BigDecimal.ZERO;
			for (Object object : list) {
				JSONArray array = JSON.parseArray(object.toString());
				if (Double.valueOf(array.get(1).toString()) > 0d) {
					JSONArray newArray = new JSONArray();
					if (Double.valueOf(array.get(1).toString()) > 0d) {
						// 单价
						BigDecimal price = new BigDecimal(array.get(0).toString());
						// 数量
						BigDecimal count = new BigDecimal(array.get(1).toString());

						newArray.add(MathUtils.toScaleNum(price, cnyDigit));
						newArray.add(MathUtils.toScaleNum(count, coinDigit));
						allSellCount = MathUtils.add(allSellCount, count);
						newArray.add(MathUtils.toScaleNum(allSellCount, coinDigit));
						newSellDepth.add(newArray);
					}
				}
			}
			List<Object> subNewBuyDepth;
			List<Object> subNewSellDepth;
			if (newBuyDepth.size() > 50) {
				subNewBuyDepth = newBuyDepth.subList(0, 50);
			} else {
				subNewBuyDepth = newBuyDepth;
			}
			if (newSellDepth.size() > 50) {
				subNewSellDepth = newSellDepth.subList(0, 50);
			} else {
				subNewSellDepth = newSellDepth;
			}

			// web端需要反转
			if (deviceInfo.getPlatform() == 1) {
				sort(subNewSellDepth);
			}

			jsonObject.put("buyDepth", subNewBuyDepth);
			jsonObject.put("sellDepth", subNewSellDepth);
			// logger.info("subNewBuyDepth
			// :{},subNewSellDepth:{}",subNewBuyDepth.size(),subNewSellDepth.size());
			TickerData tickerData = redisHelper.getTickerData(tradeId);
			if (tickerData == null) {
				returnObject.put("p_new", 0d);
				returnObject.put("p_open", 0d);
			} else {
				returnObject.put("p_new", tickerData.getLast());
				returnObject.put("p_open", tickerData.getKai());
			}

			if (tradeType.getType().equals(1)) {
				returnObject.put("cny", 1);
			}
			// BTC交易区
			else if (tradeType.getType().equals(2)) {
				BigDecimal cny = getCny(8, returnObject.getBigDecimal("p_new"));
				returnObject.put("cny", cny);
			}
			// ETH交易区
			else if (tradeType.getType().equals(3)) {
				BigDecimal cny = getCny(11, returnObject.getBigDecimal("p_new"));
				returnObject.put("cny", cny);
			} else if (tradeType.getType().equals(4)) {
				BigDecimal cny = getCny(57, returnObject.getBigDecimal("p_new"));
				returnObject.put("cny", cny);
			} else if (tradeType.getType().equals(5)) {
				BigDecimal cny = getCnyByCoinId(tradeType.getBuyCoinId(), tradeType.getSellCoinId(), returnObject.getBigDecimal("p_new"));
				returnObject.put("cny", cny);
			}

			jsonObject.put("date", Utils.getTimestamp().getTime() / 1000);
			returnObject.put("depth", jsonObject);
		}
		return ReturnResult.SUCCESS(returnObject);
	}

	public void sort(List<Object> list) {
		Collections.sort(list, new Comparator<Object>() {
			@Override
			public int compare(Object o1, Object o2) {
				JSONArray o1Array = JSON.parseArray(o1.toString());
				JSONArray o2Array = JSON.parseArray(o2.toString());
				// o1单价
				BigDecimal o1Price = new BigDecimal(o1Array.get(0).toString());
				// o2单价
				BigDecimal o2Price = new BigDecimal(o2Array.get(0).toString());

				if (o1Price.compareTo(o2Price) < 0) {
					return 1;
				} else if (o1Price.compareTo(o2Price) == 0) {
					return 0;
				} else {
					return -1;
				}
			}
		});
	}

	@ApiOperation("获取本周明星币")
	@GetMapping("/getStarCoinThisWeek")
	@PassToken
	public RespData<JSONObject> getStarCoinThisWeek() {
		List<SystemTradeType> list = redisHelper.getAllTradeTypeList(WebConstant.BCAgentId);
		List<TradeInfoResp> restList = new ArrayList<>(list.size());
		for (SystemTradeType systemTradeType : list) {
			if (systemTradeType.getStatus().equals(SystemTradeStatusEnum.ABNORMAL.getCode())) {
				continue;
			}
			FPeriod fPeriod = redisHelper.getLastKline(systemTradeType.getId(), 10080);
			TradeInfoResp resp = systemTradeType2TradeInfoResp(systemTradeType);
			if (fPeriod == null || fPeriod.getFkai().compareTo(BigDecimal.ZERO) == 0) {
				resp.setWeekChg(BigDecimal.ZERO);
			} else {
				resp.setWeekChg(fPeriod.getFshou().subtract(fPeriod.getFkai()).divide(fPeriod.getFkai(), 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100)));
			}
			restList.add(resp);
		}
		List<TradeInfoResp> collect = restList.stream().sorted(Comparator.comparing(TradeInfoResp::getWeekChg).reversed()).limit(10).collect(Collectors.toList());
		JSONObject json=new JSONObject();
		json.put("list", collect);
		json.put("topic", mqttProperties.getStarCoinThisWeekTopic());
		return RespData.ok(json);
	}
	
	
	@ApiOperation("获取成交额榜")
	@GetMapping("/getTopTurnover")
	@PassToken
	public RespData<JSONObject> getTopTurnover() {
		List<SystemTradeType> list = redisHelper.getAllTradeTypeList(WebConstant.BCAgentId);
		List<TradeInfoResp> restList = new ArrayList<>(list.size());
		for (SystemTradeType systemTradeType : list) {
			if (systemTradeType.getStatus().equals(SystemTradeStatusEnum.ABNORMAL.getCode())) {
				continue;
			}
			TradeInfoResp resp = systemTradeType2TradeInfoResp(systemTradeType);
			restList.add(resp);
		}
		List<TradeInfoResp> collect = restList.stream().sorted(Comparator.comparing(TradeInfoResp::getTotalAmount).reversed()).limit(10).collect(Collectors.toList());
		JSONObject json=new JSONObject();
		json.put("list", collect);
		json.put("topic", mqttProperties.getTop10TurnoverTopic());
		return RespData.ok(json);
	}
	

	@ApiOperation("根据交易区和交易类型获取交易对成交信息")
	@RequestMapping(value = "/v2/tradeInfo", method = { RequestMethod.GET, RequestMethod.POST })
	@PassToken
	public RespData<List<TradeInfoResp>> tradeInfo(Integer id, String type) {
		List<SystemTradeType> list = redisHelper.getAllTradeTypeList(WebConstant.BCAgentId);
		List<TradeInfoResp> restList = new ArrayList<>(list.size());
		for (SystemTradeType systemTradeType : list) {
			if ("level".equals(type) && !systemTradeType.isOpenLever()) {
				continue;
			}
			if (systemTradeType.getStatus().equals(SystemTradeStatusEnum.ABNORMAL.getCode()) || !systemTradeType.getType().equals(id)) {
				continue;
			}

			restList.add(systemTradeType2TradeInfoResp(systemTradeType));
		}
		return RespData.ok(restList);
	}

	@ApiOperation("根据sellName和buyName获取一条交易对信息,默认获取交易区1中BTC/USDT的交易对信息,如果状态异常则随机取出一条正常的交易对信息")
	@GetMapping(value = "/v2/getOneTradeInfo")
	@PassToken
	public RespData<TradeInfoResp> getOneTradeInfo(@Valid GetOneTradeInfoReq req) {
		List<SystemTradeType> list = redisHelper.getAllTradeTypeList(WebConstant.BCAgentId);
		SystemTradeType systemTradeType = list.stream().filter(e -> e.getSellShortName().equalsIgnoreCase(req.getSellShortName())).filter(e -> e.getBuyShortName().equalsIgnoreCase(req.getBuyShortName())).filter(e -> e.getType().equals(req.getType())).filter(e -> e.getStatus().equals(SystemTradeStatusEnum.NORMAL.getCode())).findFirst().orElse(list.stream().filter(e -> e.getStatus().equals(SystemTradeStatusEnum.NORMAL.getCode())).findFirst().get());
		return RespData.ok(systemTradeType2TradeInfoResp(systemTradeType));
	}

	private TradeInfoResp systemTradeType2TradeInfoResp(SystemTradeType systemTradeType) {
		TradeInfoResp tradeInfoResp = new TradeInfoResp();
		tradeInfoResp.setTradeId(systemTradeType.getId());
		tradeInfoResp.setId(systemTradeType.getId());
		tradeInfoResp.setBuySymbol(systemTradeType.getBuyShortName());
		tradeInfoResp.setSellSymbol(systemTradeType.getSellShortName());
		tradeInfoResp.setImage(systemTradeType.getSellWebLogo());
		tradeInfoResp.setSellShortName(systemTradeType.getSellShortName());
		tradeInfoResp.setBuyShortName(systemTradeType.getBuyShortName());
		tradeInfoResp.setDigit(systemTradeType.getDigit());
		tradeInfoResp.setType(systemTradeType.getType());
		if(!ApiUtils.isTradeOpen(systemTradeType)) {
			tradeInfoResp.setIsOpen("0");
		}
		else {
			tradeInfoResp.setIsOpen("1");
		}

		// 最新价格
		TickerData tickerData = pushService.getTickerData(systemTradeType.getId());
		tradeInfoResp.setP_new(Optional.ofNullable(tickerData).map(TickerData::getLast).orElse(BigDecimal.ZERO));
		tradeInfoResp.setP_open(Optional.ofNullable(tickerData).map(TickerData::getKai).orElse(BigDecimal.ZERO));
		tradeInfoResp.setTotal(Optional.ofNullable(tickerData).map(TickerData::getVol).orElse(BigDecimal.ZERO));
		tradeInfoResp.setTotalAmount(MathUtils.mul(tradeInfoResp.getP_new(), tradeInfoResp.getTotal()));
		tradeInfoResp.setBuy(Optional.ofNullable(tickerData).map(TickerData::getBuy).orElse(BigDecimal.ZERO));
		tradeInfoResp.setSell(Optional.ofNullable(tickerData).map(TickerData::getSell).orElse(BigDecimal.ZERO));
		tradeInfoResp.setRose(Optional.ofNullable(tickerData).map(TickerData::getChg).orElse(BigDecimal.ZERO));

		if (systemTradeType.getType().equals(SystemTradeTypeEnum.GAVC.getCode())) {
			tradeInfoResp.setCny(MathUtils.toScaleNum(tradeInfoResp.getP_new(), 2));
		} else if (systemTradeType.getType().equals(SystemTradeTypeEnum.BTC.getCode())) {
			tradeInfoResp.setCny(getCny(8, tradeInfoResp.getP_new()));
		} else if (systemTradeType.getType().equals(SystemTradeTypeEnum.ETH.getCode())) {
			tradeInfoResp.setCny(getCny(11, tradeInfoResp.getP_new()));
		} else if (systemTradeType.getType().equals(SystemTradeTypeEnum.USDT.getCode())) {
			tradeInfoResp.setCny(getCny(57, tradeInfoResp.getP_new()));
		} else if (systemTradeType.getType().equals(SystemTradeTypeEnum.INNOVATION_AREA.getCode())) {
			tradeInfoResp.setCny(getCnyByCoinId(systemTradeType.getBuyCoinId(), systemTradeType.getSellCoinId(), tradeInfoResp.getP_new()));
		}

		return tradeInfoResp;
	}

	// 创新区 买卖盘，最新成交
	@PassToken
	@ApiOperation("")
	@RequestMapping(value = "/v2/innovationTradeInfo", method = { RequestMethod.GET, RequestMethod.POST })
	public RespData<JSONObject> innovationTradeInfo() {
		List<SystemTradeType> list = redisHelper.getAllTradeTypeList(WebConstant.BCAgentId);
		JSONObject json = new JSONObject();
		json.put("topic", mqttProperties.getInnovateTopic());
		List<TradeInfoResp> restList = new ArrayList<>(list.size());
		for (SystemTradeType systemTradeType : list) {
			if (systemTradeType.getStatus().equals(SystemTradeStatusEnum.ABNORMAL.getCode()) || !systemTradeType.getType().equals(SystemTradeTypeNewEnum.INNOVATION_AREA.getCode())) {
				continue;
			}
			restList.add(systemTradeType2TradeInfoResp(systemTradeType));
		}
		json.put("list", restList);
		return RespData.ok(json);
	}

	@ApiOperation("")
	@RequestMapping(value = "/v2/tradeInfoRankingList", method = { RequestMethod.GET, RequestMethod.POST })
	@PassToken
	public ReturnResult tradeInfoRankingList(@RequestParam(value = "pageSize", defaultValue = "10") int pageSize, @RequestParam(value = "sortEnum", defaultValue = "1") int sortCode) {
		List<SystemTradeType> redisList = redisHelper.getAllTradeTypeList(WebConstant.BCAgentId);
		Integer abnormalCode = SystemTradeStatusEnum.ABNORMAL.getCode();
		List<SystemTradeType> filterList = redisList.stream().filter(t -> t.getStatus() != abnormalCode).collect(Collectors.toList());
		// JSONArray array = new JSONArray();
		// JSONArray array = new JSONArray();
		JSONObject json = new JSONObject();
		List<JSONObject> arrayList = new ArrayList<JSONObject>(filterList.size());
		for (SystemTradeType systemTradeType : filterList) {
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("tradeId", systemTradeType.getId());
			jsonObject.put("id", systemTradeType.getId());
			jsonObject.put("buySymbol", systemTradeType.getBuyShortName());
			jsonObject.put("sellSymbol", systemTradeType.getSellShortName());
			jsonObject.put("image", systemTradeType.getSellWebLogo());
			jsonObject.put("sellShortName", systemTradeType.getSellShortName());
			jsonObject.put("buyShortName", systemTradeType.getBuyShortName());
			jsonObject.put("type", systemTradeType.getType());
			// 最新价格
			TickerData tickerData = pushService.getTickerData(systemTradeType.getId());
			if (tickerData == null) {
				jsonObject.put("p_new", 0);
				jsonObject.put("p_open", 0);
				jsonObject.put("total", 0d);
				jsonObject.put("buy", 0d);
				jsonObject.put("sell", 0d);
				jsonObject.put("rose", 0d);
			} else {
				jsonObject.put("p_new", tickerData.getLast());
				jsonObject.put("p_open", tickerData.getKai());
				jsonObject.put("total", tickerData.getVol() == null ? 0d : tickerData.getVol());
				jsonObject.put("buy", tickerData.getBuy() == null ? 0d : tickerData.getLow());
				jsonObject.put("sell", tickerData.getSell() == null ? 0d : tickerData.getHigh());
				jsonObject.put("rose", tickerData.getChg() == null ? 0d : tickerData.getChg());
			}
			jsonObject.put("digit", systemTradeType.getDigit());

			arrayList.add(jsonObject);
		}

		List<JSONObject> sortArrayList = null;
		if (1 == sortCode) {
			sortArrayList = arrayList.stream().sorted((s1, s2) -> compareByDigitThenBuyShortName(s1, s2, 1)).collect(Collectors.toList());
			json.put("topic", mqttProperties.getTickerRankingAscTopic());
		} else {
			sortArrayList = arrayList.stream().sorted((s1, s2) -> compareByDigitThenBuyShortName(s1, s2, -1)).collect(Collectors.toList());
			json.put("topic", mqttProperties.getTickerRankingDescTopic());
		}
		List<JSONObject> returnList = null;
		if (sortArrayList.size() > pageSize) {
			returnList = sortArrayList.subList(0, pageSize);
		} else {
			returnList = sortArrayList;
		}

		JSONArray jsonArray = new JSONArray();
		for (JSONObject jsonObject : returnList) {
			jsonArray.add(jsonObject);
		}
		json.put("list", jsonArray);
		return ReturnResult.SUCCESS(json);
	}

	private static int compareByDigitThenBuyShortName(JSONObject lst, JSONObject rst, int sortCode) {
		BigDecimal lrose = new BigDecimal(lst.get("rose").toString());
		BigDecimal rrose = new BigDecimal(rst.get("rose").toString());
		if (1 == sortCode) {
			if (rrose.compareTo(lrose) == 0) {
				return rst.get("buySymbol").toString().compareTo(lst.get("buySymbol").toString()); //$NON-NLS-2$
			} else {
				return rrose.compareTo(lrose);
			}
		} else {
			if (lrose.compareTo(rrose) == 0) {
				return lst.get("buySymbol").toString().compareTo(rst.get("buySymbol").toString()); //$NON-NLS-2$
			} else {
				return lrose.compareTo(rrose);
			}
		}
	}

	// 实时交易
	@ApiOperation("")
	@RequestMapping(value = "/v2/realTimeTrade", method = { RequestMethod.GET, RequestMethod.POST })
	@PassToken
	public ReturnResult MarketJson(@RequestParam(required = false, defaultValue = "0") Integer tradeId, @RequestParam(required = false, defaultValue = "0") Integer successCount) {
		if (tradeId == 0 || successCount < 0) {
			return ReturnResult.FAILUER("");
		}
		if (successCount > 100 || successCount == 0) {
			successCount = 100;
		}
		// 获取虚拟币
		SystemTradeType tradeType = redisHelper.getTradeType(tradeId, WebConstant.BCAgentId);
		if (tradeType == null || tradeType.getStatus().equals(SystemTradeStatusEnum.ABNORMAL.getCode())) {
			return ReturnResult.FAILUER("");
		}

		String digit = StringUtils.isEmpty(tradeType.getDigit()) ? "2#4" : tradeType.getDigit();
		String[] digits = digit.split("#");
		int cnyDigit = Integer.valueOf(digits[0]);
		int coinDigit = Integer.valueOf(digits[1]);

		JSONObject jsonObject = new JSONObject();
		String arrayStr = redisHelper.getRedisData(RedisConstant.SUCCESSENTRUST_KEY + tradeId);
		JSONArray successArray = new JSONArray();
		if (StringUtils.isNotBlank(arrayStr)) {
			successArray = JSONArray.parseArray(arrayStr);
		}
		JSONArray newSuccessArray = new JSONArray();
		for (Object successObj : successArray) {
			JSONArray array = JSON.parseArray(successObj.toString());
			JSONArray newArray = new JSONArray();
			BigDecimal price = new BigDecimal(array.get(0).toString());
			BigDecimal count = new BigDecimal(array.get(1).toString());
			newArray.add(MathUtils.toScaleNum(price, cnyDigit).toString());
			newArray.add(MathUtils.toScaleNum(count, coinDigit).toString());
			newArray.add(array.get(2).toString());
			newArray.add(array.get(3).toString());
			if (array.size() >=6 ) {
				newArray.add(array.get(5).toString()); //timestamp
			}
			newSuccessArray.add(newArray);
		}
		// 最新成交
		jsonObject.put("trades", newSuccessArray);
		return ReturnResult.SUCCESS(jsonObject);
	}

	/**
	 * 币币钱包交易区选择
	 * 
	 * @param coinId
	 * @return
	 */
	@PassToken
	@ApiOperation("")
	@RequestMapping(value = "/v2/checkTradeArea", method = RequestMethod.POST)
	public ReturnResult checkTradeArea(@RequestParam(required = true) Integer coinId) {
		List<SystemTradeType> tradeTypeShareList = redisHelper.getTradeTypeShare(0);
		JSONArray jsonArray = new JSONArray();
		for (SystemTradeType systemTradeType : tradeTypeShareList) {
			if (systemTradeType.getSellCoinId().equals(coinId)) {
				String jsonStr = JSON.toJSONString(systemTradeType);
				JSONObject jsonObject = JSONObject.parseObject(jsonStr);
				jsonObject.put("tradeId", systemTradeType.getId());
				jsonObject.put("buySymbol", systemTradeType.getBuyShortName());
				jsonObject.put("sellSymbol", systemTradeType.getSellShortName());
				jsonArray.add(jsonObject);
			}
		}
		return ReturnResult.SUCCESS(jsonArray);
	}

	// 订单-当前委单列表
	@ApiOperation("")
	@RequestMapping(value = "/v2/curEntrutListNew", method = { RequestMethod.GET, RequestMethod.POST })
	public ReturnResult curEntrutListNew(@RequestParam(required = false) Integer tradeId, @RequestParam(required = false, defaultValue = "0") Integer page, @RequestParam(required = false, defaultValue = "10") Integer pageSize, @RequestParam(required = false) Integer type, @RequestParam(required = false) Integer orderType, @RequestParam(required = false) String startTime, @RequestParam(required = false) String endTime) throws Exception {
		FUser fuser = super.getCurrentUserInfoByToken();
		if (null == fuser) {
			return ReturnResult.FAILUER(ReturnResult.FAULURE_USER_NOT_LOGIN, I18NUtils.getString("TradeApiV2Controller.284"));
		}

			List<Integer> stateList = new ArrayList<>();
			// 未成交前6条
			FEntrust curEntrust = new FEntrust();

			if (orderType != null) {
				curEntrust.setFleverorder(OrderTypeEnum.LEVER.equals(OrderTypeEnum.getEnum(orderType)));
			}
			curEntrust.setFuid(fuser.getFid());
			curEntrust.setFtradeid(tradeId);
			curEntrust.setFagentid(fuser.getFagentid());
			curEntrust.setFtype(type);
			stateList.add(EntrustStateEnum.Going.getCode());
			stateList.add(EntrustStateEnum.PartDeal.getCode());
			stateList.add(EntrustStateEnum.WAITCancel.getCode());
			Pagination<FEntrust> curParam = new Pagination<>(page, pageSize);
			curParam.setBegindate(startTime);
			curParam.setEnddate(endTime);
			curParam.setRedirectUrl("https://www.hotcoin.top");
			curParam = entrustOrderService.listEntrust(curParam, curEntrust, stateList);
			JSONArray entrutsCur = new JSONArray();

			JSONObject result = new JSONObject();
			if (curParam != null && curParam.getData() != null && curParam.getData().size() > 0) {
				for (FEntrust fEntrust : curParam.getData()) {
					JSONObject entruts = new JSONObject();
					entruts.put("id", fEntrust.getFid());
					entruts.put("time", Utils.dateFormat(new Timestamp(fEntrust.getFcreatetime().getTime())));
					entruts.put("types", I18NUtils.getString("trade.enum.entrusttype" + fEntrust.getFtype())); //$NON-NLS-2$
					entruts.put("source", fEntrust.getFsource_s());
					entruts.put("price", fEntrust.getFprize());
					entruts.put("count", fEntrust.getFcount());

					entruts.put("amount", fEntrust.getFamount()); // 总价
					entruts.put("ncount", fEntrust.getFleftcount()); // 未成交量

					entruts.put("leftcount", MathUtils.sub(fEntrust.getFcount(), fEntrust.getFleftcount()));
					entruts.put("last", fEntrust.getFlast());
					entruts.put("successamount", fEntrust.getFsuccessamount());
					entruts.put("fees", fEntrust.getFfees());
					entruts.put("statusCode", fEntrust.getFstatus());
					entruts.put("status", I18NUtils.getString("trade.enum.entruststate" + fEntrust.getFstatus())); //$NON-NLS-2$
					entruts.put("type", fEntrust.getFtype());

					SystemCoinType buyCoinType = pushService.getSystemCoinType(fEntrust.getFbuycoinid());
					SystemCoinType sellCoinType = pushService.getSystemCoinType(fEntrust.getFsellcoinid());

					entruts.put("buysymbol", buyCoinType.getShortName());
					entruts.put("sellsymbol", sellCoinType.getShortName());
					entrutsCur.add(entruts);
				}
				result.put("count", curParam.getTotalRows());
			} else {
				result.put("count", 0);
			}

			result.put("list", entrutsCur);
			return ReturnResult.SUCCESS(I18NUtils.getString("TradeApiV2Controller.307"), result);
	}

	// 订单-历史委单列表
	@ApiOperation("")
	@RequestMapping(value = "/v2/historyEntrutListNew", method = { RequestMethod.GET, RequestMethod.POST })
	public ReturnResult historyEntrutListNew(@RequestParam(required = false) Integer tradeId, @RequestParam(required = false, defaultValue = "0") Integer page, @RequestParam(required = false, defaultValue = "10") Integer pageSize, @RequestParam(required = false) Integer type, @RequestParam(required = false) Integer orderType, @RequestParam(required = false) String startTime, @RequestParam(required = false) String endTime, @RequestParam(required = false) Integer status) {
		FUser fuser = super.getCurrentUserInfoByToken();
		if (null == fuser) {
			return ReturnResult.FAILUER(ReturnResult.FAULURE_USER_NOT_LOGIN, I18NUtils.getString("TradeApiV2Controller.309"));
		}
		// 成交前6条
		List<Integer> stateList = new ArrayList<>();
		if (status != null && status > 0) {
			stateList.add(status);
		} else {
			stateList = null;
		}

		FEntrustHistory hisEntrust = new FEntrustHistory();
		if (orderType != null) {
			hisEntrust.setFleverorder(OrderTypeEnum.LEVER.equals(OrderTypeEnum.getEnum(orderType)));
		}
		hisEntrust.setFuid(fuser.getFid());
		hisEntrust.setFtradeid(tradeId);
		hisEntrust.setFagentid(fuser.getFagentid());
		hisEntrust.setFtype(type);
		Pagination<FEntrustHistory> hisParam = new Pagination<>(page, pageSize);
		hisParam.setBegindate(startTime);
		hisParam.setEnddate(endTime);
		hisParam.setRedirectUrl("https://www.hotcoin.top");
		hisParam = this.entrustHistoryService.listEntrustHistory(hisParam, hisEntrust, stateList);
		JSONArray entrutsHis = new JSONArray();

		if (hisParam != null && hisParam.getData() != null && hisParam.getData().size() > 0) {
			for (FEntrustHistory fEntrust : hisParam.getData()) {
				JSONObject entruts = new JSONObject();
				entruts.put("id", fEntrust.getFid());
				entruts.put("time", Utils.dateFormat(new Timestamp(fEntrust.getFcreatetime().getTime())));
				entruts.put("types", I18NUtils.getString("trade.enum.entrusttype" + fEntrust.getFtype())); //$NON-NLS-2$
				entruts.put("source", fEntrust.getFsource_s());
				entruts.put("price", fEntrust.getFprize());
				entruts.put("count", fEntrust.getFcount());

				entruts.put("amount", fEntrust.getFamount()); // 总价
				entruts.put("ncount", fEntrust.getFleftcount()); // 未成交量

				entruts.put("leftcount", MathUtils.sub(fEntrust.getFcount(), fEntrust.getFleftcount()));
				entruts.put("last", fEntrust.getFlast());
				entruts.put("successamount", fEntrust.getFsuccessamount());
				entruts.put("fees", fEntrust.getFfees());
				entruts.put("statusCode", fEntrust.getFstatus());
				entruts.put("status", I18NUtils.getString("trade.enum.entruststate" + fEntrust.getFstatus())); //$NON-NLS-2$
				entruts.put("type", fEntrust.getFtype());
				SystemCoinType buyCoinType = pushService.getSystemCoinType(fEntrust.getFbuycoinid());
				SystemCoinType sellCoinType = pushService.getSystemCoinType(fEntrust.getFsellcoinid());

				entruts.put("buysymbol", buyCoinType.getShortName());
				entruts.put("sellsymbol", sellCoinType.getShortName());

				List<FEntrustLog> fentrustlogs = entrustServer.getEntrustLog(fEntrust.getFentrustid());
				if (fentrustlogs != null && fentrustlogs.size() > 0) {
					JSONArray array = new JSONArray();
					for (FEntrustLog fEntrustLog : fentrustlogs) {
						JSONObject jsonObject = new JSONObject();
						jsonObject.put("price", fEntrustLog.getFprize());
						jsonObject.put("count", fEntrustLog.getFcount());
						jsonObject.put("amount", fEntrustLog.getFamount());
						jsonObject.put("fee", fEntrust.getFfees());
						jsonObject.put("time", Utils.dateFormat(new Timestamp(fEntrustLog.getFcreatetime().getTime())));
						array.add(jsonObject);
					}
					entruts.put("entrustLog", array);
				}

				entrutsHis.add(entruts);
			}
		}

		JSONObject result = new JSONObject();
		if (hisParam != null) {
			result.put("count", hisParam.getTotalRows());
		} else {
			result.put("count", 0);
		}

		result.put("list", entrutsHis);
		return ReturnResult.SUCCESS(I18NUtils.getString("TradeApiV2Controller.0"), result);
	}

	@ApiOperation("")
	@RequestMapping(value = "/v2/getTradeTypeByTypeAndSellBuyName", method = { RequestMethod.GET, RequestMethod.POST })
	@PassToken
	public RespData<List<TradeInfoResp>> getTradeTypeByTypeAndSellBuyName(@RequestParam(required = true) int type, @RequestParam(required = true) String sellBuyName) throws Exception {
		BoolQueryBuilder query = QueryBuilders.boolQuery().must(QueryBuilders.termQuery("type", type)).mustNot(QueryBuilders.termQuery("status", SystemTradeStatusEnum.ABNORMAL.getCode())).must(QueryBuilders.multiMatchQuery(sellBuyName, "buyShortName", "sellShortName").fuzziness(1)); //$NON-NLS-4$

		Iterable<SystemTradeType> searchResult = systemTradeTypeRepository.search(query);

		List<TradeInfoResp> restList = new ArrayList<>();
		Iterator<SystemTradeType> iterator = searchResult.iterator();
		while (iterator.hasNext()) {
			SystemTradeType systemTradeType = iterator.next();

			restList.add(systemTradeType2TradeInfoResp(systemTradeType));
		}
		return RespData.ok(restList);
	}

	@ApiOperation("")
	@RequestMapping(value = "/v2/getTradeTypeBySellName", method = { RequestMethod.GET, RequestMethod.POST })
	@PassToken
	public ReturnResult getTradeTypeBySellName(String sellName) throws Exception {

		BoolQueryBuilder query = null;
		if (StringUtils.isBlank(sellName)) {
			query = QueryBuilders.boolQuery().mustNot(QueryBuilders.termQuery("status", SystemTradeStatusEnum.ABNORMAL.getCode()));
		} else {
			query = QueryBuilders.boolQuery().mustNot(QueryBuilders.termQuery("status", SystemTradeStatusEnum.ABNORMAL.getCode())).must(QueryBuilders.wildcardQuery("sellShortName", String.format("%s%s%s", "*", String.join("*", sellName.split("")), "*")));
		}
		Iterable<SystemTradeType> searchResult = systemTradeTypeRepository.search(query);

		JSONArray array = new JSONArray();
		Iterator<SystemTradeType> iterator = searchResult.iterator();
		while (iterator.hasNext()) {
			SystemTradeType systemTradeType = iterator.next();
			JSONObject jsonObject = new JSONObject();

			// 小数位处理(默认价格2位，数量4位)
			String digit = StringUtils.isEmpty(systemTradeType.getDigit()) ? "2#4" : systemTradeType.getDigit();
			String[] digits = digit.split("#");
			int cnyDigit = Integer.valueOf(digits[0]);
			int coinDigit = Integer.valueOf(digits[1]);

			// 最新价格
			TickerData tickerData = pushService.getTickerData(systemTradeType.getId());
			jsonObject.put("rose", tickerData.getChg() == null ? 0d : tickerData.getChg());
			jsonObject.put("tradeId", systemTradeType.getId());
			jsonObject.put("id", systemTradeType.getId());
			jsonObject.put("image", systemTradeType.getSellWebLogo());
			jsonObject.put("buy", tickerData.getBuy() == null ? 0d : tickerData.getLow());
			jsonObject.put("sell", tickerData.getSell() == null ? 0d : tickerData.getHigh());
			jsonObject.put("buySymbol", systemTradeType.getBuyShortName());
			jsonObject.put("sellSymbol", systemTradeType.getSellShortName());
			jsonObject.put("buyShortName", systemTradeType.getBuyShortName());
			jsonObject.put("sellShortName", systemTradeType.getSellShortName());
			jsonObject.put("type", systemTradeType.getType());
			jsonObject.put("total", tickerData.getVol() == null ? 0d : tickerData.getVol());
			jsonObject.put("p_new", MathUtils.toScaleNum(tickerData.getLast(), cnyDigit));
			jsonObject.put("p_open", Optional.ofNullable(tickerData).map(TickerData::getKai).orElse(BigDecimal.ZERO));
			jsonObject.put("digit", systemTradeType.getDigit());

			if (systemTradeType.getType().equals(SystemTradeTypeEnum.GAVC.getCode())) {
				BigDecimal cny = MathUtils.toScaleNum(jsonObject.getBigDecimal("p_new"), 2);
				jsonObject.put("cny", cny);
			}
			// BTC交易区
			else if (systemTradeType.getType().equals(SystemTradeTypeEnum.BTC.getCode())) {
				BigDecimal cny = getCny(8, jsonObject.getBigDecimal("p_new"));
				jsonObject.put("cny", cny);
			}
			// ETH交易区
			else if (systemTradeType.getType().equals(SystemTradeTypeEnum.ETH.getCode())) {
				BigDecimal cny = getCny(11, jsonObject.getBigDecimal("p_new"));
				jsonObject.put("cny", cny);
			} else if (systemTradeType.getType().equals(SystemTradeTypeEnum.USDT.getCode())) {
				BigDecimal cny = getCny(57, jsonObject.getBigDecimal("p_new"));
				jsonObject.put("cny", cny);
			} else if (systemTradeType.getType().equals(SystemTradeTypeEnum.INNOVATION_AREA.getCode())) {
				BigDecimal cny = getCnyByCoinId(systemTradeType.getBuyCoinId(), systemTradeType.getSellCoinId(), jsonObject.getBigDecimal("p_new"));
				jsonObject.put("cny", cny);
			}

			array.add(jsonObject);
		}

		return ReturnResult.SUCCESS(array);
	}
	private void copyJsonEntrust(JSONObject entruts,FEntrust fEntrust) {
        entruts.put("id", fEntrust.getFid());
        entruts.put("time", Utils.dateFormat(new Timestamp(fEntrust.getFcreatetime().getTime())));
        entruts.put("types", I18NUtils.getString("trade.enum.entrusttype" + fEntrust.getFtype())); //$NON-NLS-2$
        entruts.put("source", fEntrust.getFsource_s());
        entruts.put("price", fEntrust.getFprize());
        if(fEntrust.getFmatchtype().equals( MatchTypeEnum.MARKET.getCode())
                && fEntrust.getFtype().equals( EntrustTypeEnum.BUY.getCode())) {
        	logger.error("fEntrust:{}",JSON.toJSONString(fEntrust));
            entruts.put("amount", fEntrust.getFfunds());
            entruts.put("ncount", fEntrust.getFleftfunds()); // 未成交量
            entruts.put("successamount", fEntrust.getFsuccessamount());
        }
        else {
			logger.error("limited fEntrust:{}",JSON.toJSONString(fEntrust));
            entruts.put("amount", fEntrust.getFamount()); // 总价
            entruts.put("ncount", fEntrust.getFleftcount()); // 未成交量
            entruts.put("successamount", fEntrust.getFsuccessamount());
        }
        entruts.put("count", fEntrust.getFcount());
        BigDecimal successCount = MathUtils.sub(fEntrust.getFcount(),fEntrust.getFleftcount());
        entruts.put("successcount",successCount);
        entruts.put("leftcount",MathUtils.sub(fEntrust.getFcount(),fEntrust.getFleftcount()));
        //entruts.put("leftcount", fEntrust.getFleftcount());
        entruts.put("last", fEntrust.getFlast());
		BigDecimal averagePrice = MathUtils.div(fEntrust.getFsuccessamount(), successCount);
		averagePrice =  MathUtils.toScaleNum(averagePrice,MathUtils.DEF_CNY_SCALE);
		entruts.put("averagePrice",averagePrice);
        entruts.put("fees", fEntrust.getFfees());
        entruts.put("statusCode", fEntrust.getFstatus());
        entruts.put("status", I18NUtils.getString("trade.enum.entruststate" + fEntrust.getFstatus())); //$NON-NLS-2$
        entruts.put("type", fEntrust.getFtype());
        entruts.put("matchtype",fEntrust.getFmatchtype());
    }
    private  void copyJsonEntrustHis(JSONObject entruts,FEntrustHistory fEntrust) {
        entruts.put("id", fEntrust.getFid());
        entruts.put("time", Utils.dateFormat(new Timestamp(fEntrust.getFcreatetime().getTime())));
        entruts.put("types", I18NUtils.getString("trade.enum.entrusttype" + fEntrust.getFtype())); //$NON-NLS-2$
        entruts.put("source", fEntrust.getFsource_s());
        entruts.put("price", fEntrust.getFprize());
        if(fEntrust.getFmatchtype().equals( MatchTypeEnum.MARKET.getCode())
                && fEntrust.getFtype().equals( EntrustTypeEnum.BUY.getCode())) {
            entruts.put("amount", fEntrust.getFfunds());
            entruts.put("ncount", fEntrust.getFleftfunds()); // 未成交量
            entruts.put("successamount", fEntrust.getFsuccessamount());
        }
        else {
            entruts.put("amount", fEntrust.getFamount()); // 总价
            entruts.put("ncount", fEntrust.getFleftcount()); // 未成交量
            entruts.put("successamount", fEntrust.getFsuccessamount());
        }
        entruts.put("count", fEntrust.getFcount());
        BigDecimal successCount = MathUtils.sub(fEntrust.getFcount(),fEntrust.getFleftcount());
        entruts.put("successcount",successCount);
        entruts.put("leftcount",MathUtils.sub(fEntrust.getFcount(),fEntrust.getFleftcount()));
        entruts.put("last", fEntrust.getFlast());
		BigDecimal averagePrice = MathUtils.div(fEntrust.getFsuccessamount(), successCount);
		averagePrice =  MathUtils.toScaleNum(averagePrice,MathUtils.DEF_CNY_SCALE);
		entruts.put("averagePrice",averagePrice);
        entruts.put("fees", fEntrust.getFfees());
        entruts.put("statusCode", fEntrust.getFstatus());
        entruts.put("status", I18NUtils.getString("trade.enum.entruststate" + fEntrust.getFstatus())); //$NON-NLS-2$
        entruts.put("type", fEntrust.getFtype());
        entruts.put("matchtype",fEntrust.getFmatchtype());
    }

	
}
