package com.qkwl.service.otc.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.qkwl.common.dto.Enum.ErrorCodeEnum;
import com.qkwl.common.dto.Enum.UserStatusEnum;
import com.qkwl.common.dto.Enum.otc.OtcAppealStatusEnum;
import com.qkwl.common.dto.Enum.otc.OtcOrderStatusEnum;
import com.qkwl.common.dto.capital.FUserBankinfoDTO;
import com.qkwl.common.dto.chat.ChatMessage;
import com.qkwl.common.dto.coin.SystemTradeType;
import com.qkwl.common.dto.market.TickerData;
import com.qkwl.common.dto.otc.*;
import com.qkwl.common.dto.user.FUser;
import com.qkwl.common.dto.wallet.UserOtcCoinWallet;
import com.qkwl.common.exceptions.BCException;
import com.qkwl.common.framework.mq.OtcSendHelper;
import com.qkwl.common.framework.redis.RedisHelper;
import com.qkwl.common.match.MathUtils;
import com.qkwl.common.properties.ChatProperties;
import com.qkwl.common.result.Result;
import com.qkwl.common.rpc.otc.IOtcAdvertService;
import com.qkwl.common.rpc.otc.IOtcCoinWalletService;
import com.qkwl.common.rpc.otc.IOtcUserService;
import com.qkwl.common.rpc.otc.OtcOrderService;
import com.qkwl.common.util.Constant;
import com.qkwl.common.util.DateUtils;
import com.qkwl.common.util.I18NUtils;
import com.qkwl.common.util.SnowFlakeId;
import com.qkwl.common.util.Utils;
import com.qkwl.service.otc.dao.*;
import com.qkwl.service.otc.req.SendSmsReq;
import okhttp3.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.MessageFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
@Service("otcOrderService")
public class OtcOrderServiceImpl implements OtcOrderService{
	private static final Logger logger = LoggerFactory.getLogger(OtcOrderServiceImpl.class);
	@Autowired
	private FUserMapper userMapper;
	@Autowired
	private OtcOrderMapper otcOrderMapper;
	@Autowired
	private OtcOrderLogMapper otcOrderLogMapper;
	@Autowired
	private OtcAdvertMapper otcAdvertMapper;
	@Autowired
	private IOtcCoinWalletService otcCoinWalletService;
	@Autowired
	private IOtcUserService otcUserService;
	@Autowired
	private FUserBankinfoMapper fUserBankinfoMapper;
	@Autowired
	private OtcAppealMapper otcAppealMapper;
	@Autowired
	private RedisHelper redisHelper;
	@Autowired
	private IOtcAdvertService otcAdverService;
	@Autowired
	private ChatProperties chatProperties;
	@Autowired
	private ChatMessageMapper chatMessageMapper; 
	@Autowired
	private OtcSendHelper otcSendHelper;
	@Autowired
    private UserOtcCoinWalletMapper userOtcCoinWalletMapper;
	
	@Override
	@Transactional(value="flexibleTransMgr" ,isolation = Isolation.REPEATABLE_READ, propagation=Propagation.REQUIRED, rollbackFor = Exception.class)
	public Result createOrder(OtcOrder otcOrder) throws BCException {
		try {
			if(otcOrder.getUserId() == null || otcOrder.getUserId() <= 0) {
				return Result.param("userId is null");
			}
			
			if(otcOrder.getAdId() == null || otcOrder.getAdId() <= 0) {
				return Result.param("adId is null");
			}
			
			if(otcOrder.getTotalAmount() == null || otcOrder.getTotalAmount().compareTo(BigDecimal.ZERO) <= 0) {
				return Result.param("TotalAmount is null");
			}
			//查询广告是否存在
			OtcAdvert otcAdvert = otcAdvertMapper.selectAdvertByIdLock(otcOrder.getAdId().intValue());
			
			//订单用户
			FUser user = userMapper.selectByPrimaryKey(otcOrder.getUserId());
			if(user == null) {
				return Result.param("user is not found");
			}
			
			if(!user.getFhasrealvalidate()) {
				return Result.failure("请先完成实名认证！");
			}
			
			String key = "otc_cancel_limit_"+user.getFid();
	        //从redis获取count
	        String countStr = redisHelper.get(key);
	        if(StringUtils.isNotEmpty(countStr)) {
	        	int count = Integer.parseInt(countStr);
	            if(count >= 3) {
	            	return Result.failure("取消三次订单后，您 被限制下订单！");
	            }
	        }
	        
	    	//计算总价
			BigDecimal totalAmount = otcOrder.getTotalAmount();
			//1、判断交易金额是否在广告设置的最小和最大限额区间内
			if(totalAmount.compareTo(otcAdvert.getMinAmount()) < 0
					|| totalAmount.compareTo(otcAdvert.getMaxAmount()) > 0) {
				return Result.failure("金额错误，总价应在广告限额区间内！");
			}
			
			
			//前置验证
			Result check = check(otcAdvert,user,otcOrder);
			if(!check.getSuccess()) {
				return check;
			}
			
			String adCoinName = otcAdverService.getCoinName(otcAdvert.getCoinId());
			String symbol = adCoinName+"_GAVC";
			String[] symbols = symbol.split("_");
	        List<SystemTradeType> tradeTypeList = redisHelper.getTradeTypeList(0);
	        SystemTradeType systemTradeType = new SystemTradeType();
	        for (SystemTradeType tradeType : tradeTypeList) {
	            if (tradeType.getSellShortName().toLowerCase().equals(symbols[0].toLowerCase())
	                    && tradeType.getBuyShortName().toLowerCase().equals(symbols[1].toLowerCase())) {
	            	systemTradeType = tradeType;
	            }
	        }
	        
	        int coinDigit = Constant.OTC_COUNT_DIGIT;
	        int priceDigit = Constant.OTC_AMOUNT_DIGIT;
	        if (otcAdvert.getCoinId().equals(9)) {
				priceDigit = Constant.OTC_PRICE_DIGIT;
			}
			
			//如果是固定价格则取广告固定价格
			if(otcAdvert.getPriceType().equals(Constant.OTC_FIXED_PRICE)) {
				otcOrder.setPrice(otcAdvert.getFixedPrice());
			}else {
				//浮动市场 1 平均 2热币
				String coinName = otcAdverService.getCoinName(otcAdvert.getCoinId());
				String currencyName = otcAdverService.getCurrencyName(otcAdvert.getCurrencyId());
				if(otcAdvert.getFloatMarket().equals(1)) {
					BigDecimal price = otcAdverService.getAveragePrice(coinName,currencyName);
					otcOrder.setPrice(price);
				}else if(otcAdvert.getFloatMarket().equals(2)) {
					BigDecimal price = otcAdverService.getHotcoinPrice(otcAdvert.getCoinId());
					otcOrder.setPrice(price);
				}
				
				//如果设置了最低成交价格，在价格波动到最低价格之下时则成交价格就是设置的最低成交价格
				if(otcAdvert.getAcceptablePrice()!=null) {
					if(otcAdvert.getAcceptablePrice().compareTo(BigDecimal.ZERO) > 0 && otcOrder.getPrice().compareTo(otcAdvert.getAcceptablePrice())<0) {
						otcOrder.setPrice(otcAdvert.getAcceptablePrice());
					}
				}
			}
			
			BigDecimal price = MathUtils.toScaleNum(otcOrder.getPrice(), priceDigit);
			otcOrder.setPrice(price);
		
			//广告手续费
			BigDecimal fee = otcAdvert.getFeeRate(); 
			//数量=总价除以价格
			BigDecimal amount = MathUtils.toScaleNum(MathUtils.div(otcOrder.getTotalAmount(), otcOrder.getPrice()),coinDigit);
			//计算手续费
			BigDecimal adFee = MathUtils.toScaleNum(MathUtils.mul(amount, fee), coinDigit);
			//如果是售出广告还需要冻结订单用户的虚拟币
//				if(otcAdvert.getSide().equals(Constant.OTC_SELL)) {
//					//冻结钱包对应的虚拟币
//					Result result = otcCoinWalletService.userOtcFrozen(otcAdvert.getUserId(), otcAdvert.getCoinId(), amount);
//					if(!result.getSuccess()) {
//						return Result.failure(result.getMsg());
//					}
//				}else 
			
			String beforeData = "";
			String afterData = "";
			if(otcAdvert.getSide().equals(Constant.OTC_BUY)) {
				UserOtcCoinWallet wallet = userOtcCoinWalletMapper.select(otcOrder.getUserId(), otcAdvert.getCoinId());
				if (MathUtils.compareTo(MathUtils.sub(wallet.getTotal(), amount), BigDecimal.ZERO) < 0) {
					return Result.failure("钱包余额不够");
				}
				beforeData = JSONObject.toJSONString(wallet);
				//冻结钱包对应的虚拟币
				Result result = otcCoinWalletService.userOtcFrozen(otcOrder.getUserId(), otcAdvert.getCoinId(), amount);
				if(!result.getSuccess()) {
					return Result.failure(result.getMsg());
				}
				wallet.setTotal(MathUtils.sub(wallet.getTotal(), amount));
				wallet.setFrozen(MathUtils.add(wallet.getFrozen(), amount));
				afterData = JSONObject.toJSONString(wallet);
			} else {
				OtcAdvert advert = otcAdvertMapper.selectAdvertById(otcOrder.getAdId().intValue());
				beforeData = JSONObject.toJSONString(advert);
				advert.setVisiableVolume(MathUtils.sub(otcAdvert.getVisiableVolume(), amount));
				advert.setFrozenVolume(MathUtils.add(amount, otcAdvert.getFrozenVolume()));
				afterData = JSONObject.toJSONString(advert);
			}
			
			otcOrder.setAmount(amount);

			//2、购买数量不能大于广告剩余数量，广告购买或出售都适用此判断
			if(otcOrder.getAmount().compareTo(otcAdvert.getVisiableVolume())>0) {
				throw new BCException("数量错误，大于广告剩余可售数量！");
			}
			
			otcOrder.setAdUserId(otcAdvert.getUserId());
			otcOrder.setStatus(OtcOrderStatusEnum.WAIT.getCode().byteValue());
			otcOrder.setTotalAmount(totalAmount);
			SnowFlakeId orderNoWorker = new SnowFlakeId(1, 1);
			otcOrder.setOrderNo(orderNoWorker.nextId()+"");
			otcOrder.setCreateTime(new Date());
			otcOrder.setUpdateTime(new Date());
			//获取广告最大支付时间（分钟）
			int maxPaytime = otcAdvert.getMaxPaytime()*60000;
			//当前时间加上限制时间
			long limit = System.currentTimeMillis()+maxPaytime;
			otcOrder.setLimitTime(new Timestamp(limit));
			otcOrder.setExtendTime(false);
			otcOrder.setUserEvaluation(false);
			otcOrder.setAdUserEvaluation(false);
			otcOrder.setAdFee(adFee);
			
			//判断买卖方
			if(otcAdvert.getSide().equals(Constant.OTC_BUY)) {
				otcOrder.setBuyerId(otcOrder.getAdUserId());
				otcOrder.setSellerId(otcOrder.getUserId());
			}else if(otcAdvert.getSide().equals(Constant.OTC_SELL)) {
				otcOrder.setBuyerId(otcOrder.getUserId());
				otcOrder.setSellerId(otcOrder.getAdUserId());
			}
			String payCode = Utils.randomInteger(6);
			otcOrder.setPayCode(payCode);
			int result = otcOrderMapper.insertSelective(otcOrder);
			if(result <= 0) {
				throw new BCException("生成订单出错");
			}
			
			//更新广告的出售数量
			updateAdAmountSell(otcOrder.getAmount(), otcAdvert);
			
			//订单操作日志
			OtcOrderLog otcOrderLog = new OtcOrderLog();
			otcOrderLog.setBeforeData(beforeData);
			otcOrderLog.setAfterData(afterData);
			otcOrderLog.setBeforeStatus(OtcOrderStatusEnum.WAIT.getCode().byteValue());
			otcOrderLog.setAfterStatus(OtcOrderStatusEnum.WAIT.getCode().byteValue());
			otcOrderLog.setCreator((long)otcOrder.getUserId());
			otcOrderLog.setCreatorName(user.getFrealname());
			otcOrderLog.setCreateTime(new Date());
			otcOrderLog.setRemark(null);
			otcOrderLog.setOrderId(otcOrder.getId());
			if (otcOrderLogMapper.insertSelective(otcOrderLog) <= 0) {
				throw new BCException("生成订单流水出错");
			}
			
			//发送消息给买家
			//String msg = "订单数字币已经由系统锁定托管，买家请在订单有效期内付款并标记付款完成";
            String msg = I18NUtils.getString("OtcOrderService.OtcOrder.createMsg");
			
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("orderId", otcOrder.getId());
			
			//友盟推送
			try {
				otcSendHelper.SendOtcOrder(otcOrder.getBuyerId(), otcOrder.getSellerId(), otcOrder.getId());
				Date date = new Date();
				if(otcAdvert.getSide().equals("BUY")) {
					jsonObject.put("type", "SELL");
					//发消息
					sendMsg(msg, otcOrder.getUserId(), otcOrder.getAdUserId(), otcOrder.getId(),date);
					
					//发送问候语
					if(StringUtils.isNotEmpty(otcAdvert.getGreetings())) {
						sendMsg(otcAdvert.getGreetings(), otcOrder.getUserId(), otcOrder.getAdUserId(), otcOrder.getId(),"greetings",new Timestamp(date.getTime()+1000));
					}
					
					//给广告主发送短信gd_nopay模板
					sendSMS(otcOrder.getAdUserId(),adCoinName,amount,"gd_nopay",otcOrder.getOrderNo());
				}else if(otcAdvert.getSide().equals("SELL")) {
					jsonObject.put("type", "BUY");
					//发消息
					sendMsg(msg, otcOrder.getAdUserId(), otcOrder.getUserId(), otcOrder.getId(),date);
					
					//发送问候语
					if(StringUtils.isNotEmpty(otcAdvert.getGreetings())) {
						sendMsg(otcAdvert.getGreetings(), otcOrder.getAdUserId(), otcOrder.getUserId(), otcOrder.getId(),"greetings",new Timestamp(date.getTime()+1000));
					}
					
					//给广告主发送短信user_nopay模板
					sendSMS(otcOrder.getAdUserId(),adCoinName,amount,"user_nopay",otcOrder.getOrderNo());
				}
			} catch (Exception e) {
				throw new BCException("发送信息失败");
			}
			
			return Result.success(200,jsonObject);
		} catch (BCException e) {
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
			throw new BCException("创建订单异常");
		}
		
		
	}
	
	public void sendSMS(Integer receiver,String coinName,BigDecimal coinCount,
			String modelId,String orderNo) {
		String lang = I18NUtils.getCurrentLang();
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {

					FUser fUser = userMapper.selectByPrimaryKey(receiver);
					String areaCode = fUser.getFareacode();
					String phone = fUser.getFtelephone();
					if(StringUtils.isBlank(phone)){
						logger.error("uid:{} phone is null",receiver);
						return;
					}
					Map<String, Object> map = new HashMap<>();
					map.put("uid", receiver);
					map.put("orderNo", orderNo);
					map.put("coinName", coinName);
					map.put("coinCount", coinCount.stripTrailingZeros().toPlainString());
					if (!areaCode.equals("86")) {
						phone = areaCode+phone;
						if (phone.charAt(0) == '0') {
							phone = phone.substring(1, phone.length());
						}
					}
					map.put("mobile",phone);
					SendSmsReq sendSmsReq = new SendSmsReq();
					sendSmsReq.setParams(map);
					sendSmsReq.setBusinessType(modelId);
					sendSmsReq.setLang(lang);
					sendSmsReq.setPhone(phone);
					sendSmsReq.setPlatform("OTC");
					String url = chatProperties.getSmsUrl();
					RequestBody requestBody = FormBody.create(MediaType.parse("application/json; charset=utf-8")
			        , JSON.toJSONString(sendSmsReq));
					OkHttpClient client = new OkHttpClient.Builder()
			                .connectTimeout(10, TimeUnit.SECONDS)
			                .writeTimeout(10, TimeUnit.SECONDS)
			                .readTimeout(10, TimeUnit.SECONDS)
			                .build();
			        Response response=client.newCall(new Request.Builder()
						        .url(url).post(requestBody)
						        .build())
						        .execute();
			        if(response.code() == 200) {
			        	JSONObject binanceObject = JSON.parseObject(response.body().string());
						logger.info(binanceObject.toJSONString());
			        }
				} catch (Exception e) {
					logger.error("sendsms error,uid:{},orderNo:{},coinName:{},coinCount:{},ex:{}",receiver,orderNo,coinName,coinCount,orderNo,e);
				}
			}
		}).start();
	}
	
	public void sendMsg(String msg,Integer sender,Integer receiver,Long orderId,Date time) {
		this.sendMsg(msg, sender, receiver, orderId, "system",time);
	}
	
	public void sendMsg(String msg,Integer sender,Integer receiver,Long orderId,String msgType,Date time) {
//		new Thread(new Runnable() {
//			@Override
//			public void run() {
//				try {
//
//					String url = chatProperties.getUrl()+"?sender="+sender+"&receiver="+receiver
//							+"&orderId="+orderId+"&clientType="+chatProperties.getPlatform()+"&message="
//							+msg+"&messageType="+msgType;
//					OkHttpClient client = new OkHttpClient.Builder()
//			                .connectTimeout(10, TimeUnit.SECONDS)
//			                .writeTimeout(10, TimeUnit.SECONDS)
//			                .readTimeout(10, TimeUnit.SECONDS)
//			                .build();
//			        
//			        Response response=client.newCall(new Request.Builder()
//						        .url(url)
//						        .build())
//						        .execute();
//			        if(response.code() == 200) {
//			        	JSONObject binanceObject = JSON.parseObject(response.body().string());
//						System.out.println("返回-----"+binanceObject+"----消息内容---"+msg);
//			        }
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
//			}
//		}).start();
		
		OtcUserExt senderExt = otcUserService.getOtcUserExt(sender);
		OtcUserExt receiverExt = otcUserService.getOtcUserExt(receiver);
		
		ChatMessage chatMessage = new ChatMessage();
        chatMessage.setSender(sender.longValue());
        chatMessage.setReceiver(receiver.toString());
        chatMessage.setOrderId(orderId.toString());
        chatMessage.setMessage(msg);
        chatMessage.setExtendsJson("");
        chatMessage.setSendState(1);
        chatMessage.setMsgType(msgType);
        chatMessage.setSenderFnickname(senderExt.getNickname());
        String recvNickName = receiverExt.getNickname();
        chatMessage.setReceiverFnickname(recvNickName);
        chatMessage.setCreateTime(time);
        chatMessage.setSendTime(time);
        chatMessage.setSendType(1);
        chatMessageMapper.insert(chatMessage);
	}
	
	
	//买入卖出业务检查，
	public Result check(OtcAdvert otcAdvert,FUser user,OtcOrder param) 
	{
		//3、不能自己发的广告自己买卖
		if(otcAdvert.getUserId().equals(user.getFid())) {
			return Result.failure("您不能购买/售出自己发布的广告！");
		}
		
		//4、判断广告状态 正常状态才能买
		if(!otcAdvert.getStatus().equals(Constant.OTC_ADVERT_ON)) {
			return Result.failure("广告已经下架！");
		}
		
		//5、判断买方卖方用户状态 是否被禁用
		FUser adUser = userMapper.selectByPrimaryKey(otcAdvert.getUserId());
		if(!user.isOtcAction()) {
			return Result.failure(I18NUtils.getString("OtcOrderServiceImpl.1"));
		}
		
		if(!adUser.isOtcAction()) {
			return Result.failure(I18NUtils.getString("OtcOrderServiceImpl.2"));
		}
		
		//判断用户是否禁用
		if(user.getFstatus().equals(UserStatusEnum.FORBBIN_VALUE)) {
			return Result.failure("您的账户已被禁用！");
		}
		
		//6、判断是否符合广告要求（买家必须成交过几次）
		OtcUserExt otcUserExt = otcUserService.getOtcUserExt(user.getFid());
		if(otcUserExt != null && otcAdvert.getSuccessCount() > 0 && otcAdvert.getSuccessCount() > otcUserExt.getCmpOrders()){
			return Result.failure("您不符合广告成交要求，必须成交过"+otcAdvert.getSuccessCount()+"次！");
		}
		
		//7、判断广告状态 正常状态才能买（非冻结 18.12.12号新需求）
		if(!otcAdvert.getIsFrozen().equals(0)) {
			return Result.failure("广告已经被冻结");
		}
		
		//8、判断用户当前选择的支付方式是否存在，售出广告适用
		if(otcAdvert.getSide().equals(Constant.OTC_BUY)) {
			List<FUserBankinfoDTO> bankList = fUserBankinfoMapper.getBankInfoListByUser(user.getFid(), param.getPayment());
			if(bankList == null || bankList.size() ==0 ) {
				return Result.failure("您没有绑定对应的支付方式！");
			}
		}
		
		//9、如果广告限制了同时进行中的订单数
		Integer processingOrders = otcAdvertMapper.getProcessingOrders(otcAdvert.getId());
		if (otcAdvert.getMaxProcessing() > 0 && processingOrders >= otcAdvert.getMaxProcessing()) {
			return Result.failure("已超过当前广告支持的最大处理订单数！");
		}
		
		//10、
		if(!otcCoinWalletService.otcBalance(user.getFid(), otcAdvert.getCoinId())) {
			return Result.failure("您的资产已不平衡，已经被限制使用OTC功能！");
		}
		
		return Result.success();  
	}

	/**
	 * 买入售出时候需要更新广告数量
	 * @throws Exception 
	 */
	public void updateAdAmountSell(BigDecimal amount,OtcAdvert otcAdvert) throws BCException {
		////可用数量
		//BigDecimal visiableVolume;
		
		//成交数量
		//BigDecimal tradingVolume;
		
		//冻结数量
		//BigDecimal frozenVolume;
		//1、加冻结
		//2、减可用
		BigDecimal  frozen = MathUtils.add(amount, otcAdvert.getFrozenVolume());
		BigDecimal  visiable = MathUtils.sub(otcAdvert.getVisiableVolume(), amount);
		otcAdvert.setFrozenVolume(frozen);
		otcAdvert.setVisiableVolume(visiable);
		if(otcAdvertMapper.updateOtcAdvert(otcAdvert)<=0) {
			throw new BCException("updateAdAmountSell出错");
		}
	}
	
	/**
	 * 成交时需要更新广告数量
	 * @param amount
	 * @param otcAdvert
	 * @throws Exception 
	 */
	public void updateAdAmountSuccess(BigDecimal amount,OtcAdvert otcAdvert) throws BCException {
		////可用数量
		//BigDecimal visiableVolume;
		
		//成交数量
		//BigDecimal tradingVolume;
		
		//冻结数量
		//BigDecimal frozenVolume;
		//1、减冻结
		//2、成交加
		BigDecimal  frozen = MathUtils.sub(otcAdvert.getFrozenVolume(), amount);
		BigDecimal  trading = MathUtils.add(amount, otcAdvert.getTradingVolume());
		otcAdvert.setFrozenVolume(frozen);
		otcAdvert.setTradingVolume(trading);
		if(otcAdvertMapper.updateOtcAdvert(otcAdvert)<=0) {
			throw new BCException("updateAdAmountSuccess出错");
		}
	}
	
	/**
	 * 取消时需要更新广告数量
	 * @param amount
	 * @param otcAdvert
	 * @throws Exception 
	 */
	public void updateAdAmountCancel(BigDecimal amount,OtcAdvert otcAdvert) throws BCException {
		////可用数量
		//BigDecimal visiableVolume;
		
		//成交数量
		//BigDecimal tradingVolume;
		
		//冻结数量
		//BigDecimal frozenVolume;
		//1、减冻结
		//2、加可用
		BigDecimal  frozen = MathUtils.sub(otcAdvert.getFrozenVolume(), amount);
		BigDecimal  visiable = MathUtils.add(otcAdvert.getVisiableVolume(), amount);
		otcAdvert.setFrozenVolume(frozen);
		otcAdvert.setVisiableVolume(visiable);
		if(otcAdvertMapper.updateOtcAdvert(otcAdvert)<=0) {
			throw new BCException("updateAdAmountCancel出错");
		}
	}
	
	@Override
	public PageInfo<OtcOrder> listOrder(Map<String, Object> param,int pageNo,int pageSize) 
	{
		//条件查询订单，带分页
		PageHelper.startPage(pageNo, pageSize);
		List<OtcOrder> list = otcOrderMapper.listOrder(param);
		PageInfo<OtcOrder> pageInfo = new PageInfo<OtcOrder>(list);
		return pageInfo;
	}

	@Override
	public OtcOrder findById(Long id){
		return otcOrderMapper.selectByPrimaryKey(id);
	}
	
	@Override
	@Transactional(value="flexibleTransMgr" ,isolation = Isolation.REPEATABLE_READ, propagation=Propagation.REQUIRED, rollbackFor = Exception.class)
	public Result confirmOrder(OtcOrder otcOrder,FUser paramUser) throws BCException 
	{
		try {

			//确认打币
			if(otcOrder == null || otcOrder.getId().compareTo(0l) == 0) {
				return Result.param("otcOrder is null");
			}
			
			//判断是否存在订单
			OtcOrder order = this.findById(otcOrder.getId());
			if(order == null){
				return Result.param("otcOrder is null");
			}
			
			byte beforeStatus = order.getStatus();
			
			//只有完成支付的订单才能放币 ，不管售出还是买入都适用
			if (!order.getStatus().equals(OtcOrderStatusEnum.PAID.getCode().byteValue())) {
				return Result.failure("只能支付状态的订单才能确认！");
			}
			
			OtcAdvert otcAdvert = otcAdvertMapper.selectAdvertByIdLock(order.getAdId().intValue());
			
			if(!otcCoinWalletService.otcBalance(otcAdvert.getUserId(), otcAdvert.getCoinId())) {
				return Result.failure("您的资产已不平衡，已经被限制使用OTC功能！");
			}
			
			UserOtcCoinWallet buyWalletBefore = otcCoinWalletService.selectOtcWalletByUidAndType(order.getBuyerId(), otcAdvert.getCoinId());
	    	UserOtcCoinWallet sellWalletBefore = otcCoinWalletService.selectOtcWalletByUidAndType(order.getSellerId(), otcAdvert.getCoinId());
	    	String beforeData = JSONObject.toJSONString(buyWalletBefore) + JSONObject.toJSONString(sellWalletBefore) + JSONObject.toJSONString(otcAdvert);
			//买入广告，用户方看是售出
			if(otcAdvert.getSide().equals(Constant.OTC_BUY)) {
				//如果是买入广告则判断当前用户是否订单用户，则广告方打币
				if(!order.getUserId().equals(paramUser.getFid())) {
					return Result.failure("invalid otcOrder user");
				}
				OtcUserOrder otcUserOrder = new OtcUserOrder();
				otcUserOrder.setAmount(order.getAmount());
				otcUserOrder.setCoinId(otcAdvert.getCoinId());
				otcUserOrder.setFee(order.getAdFee());
				//卖出方是用户，广告主是买入
				otcUserOrder.setSellerId(paramUser.getFid());
				//买入方是广告主
				otcUserOrder.setBuyerId(otcAdvert.getUserId());
				otcUserOrder.setType(1);
				//扣广告主的手续费
				Result result = otcCoinWalletService.userOtcOrderDell(otcUserOrder);
				if(!result.getSuccess()) {
					System.out.println("orderId"+order.getId());
					return Result.failure(result.getMsg());
				}
			}else if(otcAdvert.getSide().equals(Constant.OTC_SELL)) {
				//打币如果是售出广告则需要判断当前用户是否是广告主
				if(!otcAdvert.getUserId().equals(paramUser.getFid())) {
					return Result.failure("invalid otcOrder user");
				}
				//当前为售卖广告,广告发布者为卖家,在发布广告的已经冻结了余额账户,先判断冻结账户余额是否充足
				OtcUserOrder otcUserOrder = new OtcUserOrder();
				otcUserOrder.setAmount(order.getAmount());
				otcUserOrder.setCoinId(otcAdvert.getCoinId());
				otcUserOrder.setFee(order.getAdFee());
				//卖出方是广告
				otcUserOrder.setSellerId(paramUser.getFid());
				//买入方是用户
				otcUserOrder.setBuyerId(order.getUserId());
				otcUserOrder.setType(2);
				Result result = otcCoinWalletService.userOtcOrderDell(otcUserOrder);
				if(!result.getSuccess()) {
					return Result.failure(result.getMsg());
				}
			}
			
			//更新订单状态
			order.setStatus(OtcOrderStatusEnum.COMPLETED.getCode().byteValue());
			if(otcOrderMapper.updateByPrimaryKeySelective(order) <= 0 ) {
				throw new BCException("confirmOrder出错");
			}
			
			//更新广告成交数量
			updateAdAmountSuccess(order.getAmount(), otcAdvert);
			
			BigDecimal buyTotal = MathUtils.add(buyWalletBefore.getTotal(), order.getAmount());
	    	BigDecimal sellFrozen = MathUtils.sub(sellWalletBefore.getFrozen(), order.getAmount());
	    	//扣手续费，发起方扣手续费
	    	if(order.getAdFee() != null && MathUtils.compareTo(order.getAdFee(), BigDecimal.ZERO) > 0) {
	    		if(otcAdvert.getSide().equals(Constant.OTC_BUY)) {
	    			buyTotal = MathUtils.sub(buyTotal, order.getAdFee());
	    		}else if(otcAdvert.getSide().equals(Constant.OTC_SELL)){
	    			sellFrozen = MathUtils.sub(sellFrozen, order.getAdFee());
	    		}
	    	}
	    	buyWalletBefore.setTotal(buyTotal);
	    	sellWalletBefore.setFrozen(sellFrozen);
	    	String afterData = JSONObject.toJSONString(buyWalletBefore) + JSONObject.toJSONString(sellWalletBefore) + JSONObject.toJSONString(otcAdvert);
					
			//更新卖家用户扩展信息，更新成交总金额 、成交笔数
			OtcUserExt otcAdUserExt = otcUserService.getOtcUserExt(order.getAdUserId());
			
			//历史成交额，根据需求需要换算成BTC
			//获取BTC实时成交价
			TickerData tickerData = redisHelper.getTickerData(8);
			BigDecimal btcAmount = MathUtils.div(order.getTotalAmount(), tickerData.getLast());
			BigDecimal toScaleAmount = MathUtils.toScaleNum(btcAmount, 6);
			
			//增加订单成交数
			int adCount = otcAdUserExt.getCmpOrders()+1;
			//增加订单成交总金额
			BigDecimal adTotalAmount = otcAdUserExt.getSuccAmt();
			otcAdUserExt.setSuccAmt(MathUtils.add(adTotalAmount, toScaleAmount));
			otcAdUserExt.setCmpOrders(adCount);
			otcUserService.update(otcAdUserExt);
			
			//更新买家用户扩展信息，更新成交总金额 、成交笔数
			OtcUserExt otcUserExt = otcUserService.getOtcUserExt(order.getUserId());
			//增加好评数
			int count = otcUserExt.getCmpOrders()+1;
			//增加订单成交总金额
			BigDecimal totalAmount = otcUserExt.getSuccAmt();
			otcUserExt.setSuccAmt(MathUtils.add(totalAmount, toScaleAmount));
			otcUserExt.setCmpOrders(count);
			otcUserService.update(otcUserExt);
			
			String coinName = otcAdverService.getCoinName(otcAdvert.getCoinId());
			
			//友盟推送
			try {
				otcSendHelper.SendOtcOrder(order.getBuyerId(), order.getSellerId(), order.getId());
				Date date = new Date();
				if(otcAdvert.getSide().equals(Constant.OTC_SELL)) {
					//String msg = otcAdUserExt.getNickname()+" 已移交数字币给 "+otcUserExt.getNickname()+" 交易完成，请给对方评价。";
                    String msg = I18NUtils.getString("OtcOrderService.OtcOrder.confirmMsg");
                    msg  =  MessageFormat.format(msg ,otcAdUserExt.getNickname(),otcUserExt.getNickname());
					sendMsg(msg, otcUserExt.getUserId(), otcAdUserExt.getUserId(), order.getId(),date);
					
					if(StringUtils.isNotEmpty(otcAdvert.getTag())) {
						sendMsg(otcAdvert.getTag(), otcUserExt.getUserId(), otcAdUserExt.getUserId(), order.getId(),"greetingsEnd",new Timestamp(date.getTime()+1000));
					}
					
					//发送短信gd_finish 发短信给购买的用户
					sendSMS(order.getUserId(), coinName,order.getAmount(),"gd_finish",order.getOrderNo());
				}else if(otcAdvert.getSide().equals(Constant.OTC_BUY)) {
					//String msg = otcUserExt.getNickname()+" 已移交数字币给 "+otcAdUserExt.getNickname()+" 交易完成，请给对方评价。";
                    String msg = I18NUtils.getString("OtcOrderService.OtcOrder.confirmMsg");
                    msg  =  MessageFormat.format(msg ,otcUserExt.getNickname(),otcAdUserExt.getNickname());
					sendMsg(msg, otcAdUserExt.getUserId(), otcUserExt.getUserId(), order.getId(),date);
					
					if(StringUtils.isNotEmpty(otcAdvert.getTag())) {
						sendMsg(otcAdvert.getTag(), otcAdUserExt.getUserId(), otcUserExt.getUserId(), order.getId(),"greetingsEnd",new Timestamp(date.getTime()+1000));
					}
					
					//发送短信user_finish 发短信给购买的广告主
					sendSMS(order.getAdUserId(),coinName,order.getAmount(),"user_finish",order.getOrderNo());
				}
			} catch (Exception e) {
				throw new BCException("发送信息失败");
			}
			
			OtcOrderLog otcOrderLog = new OtcOrderLog();
			otcOrderLog.setBeforeData(beforeData);
			otcOrderLog.setAfterData(afterData);
			otcOrderLog.setBeforeStatus(beforeStatus);
			otcOrderLog.setAfterStatus(order.getStatus());
			otcOrderLog.setCreator((long)otcOrder.getUserId());
			otcOrderLog.setCreatorName("");
			otcOrderLog.setCreateTime(new Date());
			otcOrderLog.setRemark("确认打币");
			otcOrderLog.setOrderId(order.getId());
			if (otcOrderLogMapper.insertSelective(otcOrderLog) <= 0) {
				throw new BCException("生成订单流水出错");
			}
			
			return Result.success();
		} catch (BCException e) {
			throw e;
		} catch (Exception e) {
			throw new BCException("确认打币 异常");
		}
	}

	@Override
	@Transactional(value="flexibleTransMgr" ,isolation = Isolation.REPEATABLE_READ, propagation=Propagation.REQUIRED, rollbackFor = Exception.class)
	public Result cancelOrder(OtcOrder otcOrder) throws BCException{
		try {

			if(otcOrder == null || otcOrder.getId().compareTo(0l) == 0) {
				return Result.param("otcOrder is null");
			}
			
			//判断是否存在订单
			OtcOrder order = this.findById(otcOrder.getId());
			if(order == null){
				return Result.param("otcOrder is null");
			}
			byte beforeStatus = order.getStatus();
			
			//订单是否为待处理状态
			if(!order.getStatus().equals(OtcOrderStatusEnum.WAIT.getCode().byteValue())){
				return Result.failure("invalid otcOrder status");
			}
			
			if(!order.getUserId().equals(otcOrder.getUserId())
					&& !order.getAdUserId().equals(otcOrder.getUserId())) {
				return Result.failure("invalid otcOrder user");
			}
			
			// 根据订单获取相应的广告对象 加锁
			//更新广告数量
			OtcAdvert advert = otcAdvertMapper.selectAdvertByIdLock(order.getAdId().intValue());
			
			String beforeData = "";
		    String afterData = "";
			//如果是购买虚拟币广告还需要解冻订单用户的虚拟币
			if(advert.getSide().equals(Constant.OTC_BUY)) {
				//查询订单用户的钱包余额
				BigDecimal amount = order.getAmount();
				UserOtcCoinWallet wallet = otcCoinWalletService.selectOtcWalletByUidAndType(order.getSellerId(), advert.getCoinId());
		        beforeData = JSONObject.toJSONString(wallet);
				//解冻钱包对应的虚拟币
				Result result = otcCoinWalletService.userOtcUnFrozen(order.getUserId(), advert.getCoinId(), amount);
				if(!result.getSuccess()) {
					return Result.failure(result.getMsg());
				}
				wallet.setTotal(MathUtils.add(wallet.getTotal(), amount));
		        wallet.setFrozen(MathUtils.sub(wallet.getFrozen(), amount));
		        afterData = JSONObject.toJSONString(wallet);
				//更新广告成交数量
				updateAdAmountCancel(order.getAmount(), advert);
			}else if(advert.getSide().equals(Constant.OTC_SELL)) {
				//出售广告下的购买订单被取消，订单冻结金额的返还，需先判断2个条件：
				//1.出售广告的状态    2. 出售广告的更新时间
				//一、出售广告的状态为上架，出售广告的更新时间 < 订单创建时间 ：订单冻结金额直接返还到广告冻结
				//二、出售广告的状态为上架，出售广告的更新时间 > 订单创建时间 ：订单冻结金额直接返还到用户账户
				//三、出售广告的状态为下架：订单冻结金额直接返还到用户账户
				//四、出售广告的状态为过期：订单冻结金额直接返还到用户账户
				if(order.getCreateTime().before(advert.getUpdateTime())) {
					//出售广告的更新时间 > 订单创建时间
					//查询订单用户的钱包余额
					BigDecimal amount = order.getAmount();
					BigDecimal totalAmount = MathUtils.add(amount,order.getAdFee());
					UserOtcCoinWallet wallet = otcCoinWalletService.selectOtcWalletByUidAndType(order.getSellerId(), advert.getCoinId());
			        beforeData = JSONObject.toJSONString(wallet);
					//解冻钱包对应的虚拟币
					Result result = otcCoinWalletService.userOtcUnFrozen(order.getAdUserId(), advert.getCoinId(), totalAmount);
					if(!result.getSuccess()) {
						return Result.failure(result.getMsg());
					}
					wallet.setTotal(MathUtils.add(wallet.getTotal(), totalAmount));
				    wallet.setFrozen(MathUtils.sub(wallet.getFrozen(), totalAmount));
				    afterData = JSONObject.toJSONString(wallet);
				}else if(order.getCreateTime().after(advert.getUpdateTime())) {
					//出售广告的更新时间 < 订单创建时间
					//更新广告成交数量
					beforeData = JSONObject.toJSONString(advert);
					updateAdAmountCancel(order.getAmount(), advert);
					afterData = JSONObject.toJSONString(advert);
				}
			}
			
			//更新订单状态
			order.setStatus(OtcOrderStatusEnum.CANCELLED.getCode().byteValue());
			if(otcOrderMapper.updateByPrimaryKeySelective(order) <= 0 ) {
				throw new BCException("取消订单出错");
			}
			
			//增加限制，每天只能取消3次
			//撤销过三次订单则需要提示用户不准下订单了。
			String key = "otc_cancel_limit_" + otcOrder.getUserId();
			int sec = DateUtils.getOffSeconds_abs(new Date(),DateUtils.getCurrentDay());
			redisHelper.getIncrByKey(key,sec);
			
			OtcUserExt otcUserExt = otcUserService.getOtcUserExt(order.getUserId());
			OtcUserExt otcAdUserExt = otcUserService.getOtcUserExt(order.getAdUserId());
			//发送消息
			try {
				if(advert.getSide().equals(Constant.OTC_SELL)) {
					//String msg = otcUserExt.getNickname()+" 已经取消订单，请勿再进行转帐或是数字币移交。";
                    String msg = I18NUtils.getString("OtcOrderService.OtcOrder.cancleMsg");
                    msg  =  MessageFormat.format(msg ,otcUserExt.getNickname());
					sendMsg(msg, otcUserExt.getUserId(), otcAdUserExt.getUserId(), order.getId(),new Date());
				}else if(advert.getSide().equals(Constant.OTC_BUY)) {
                    String msg = I18NUtils.getString("OtcOrderService.OtcOrder.cancleMsg");
                    msg  =  MessageFormat.format(msg ,otcAdUserExt.getNickname());
					sendMsg(msg, otcAdUserExt.getUserId(), otcUserExt.getUserId(), order.getId(),new Date());
				}
			} catch (Exception e) {
				throw new BCException("发送信息失败");
			}
			
			//订单操作日志
			OtcOrderLog otcOrderLog = new OtcOrderLog();
			otcOrderLog.setBeforeData(beforeData);
			otcOrderLog.setAfterData(afterData);
			otcOrderLog.setBeforeStatus(beforeStatus);
			otcOrderLog.setAfterStatus(order.getStatus());
			otcOrderLog.setCreator((long)otcOrder.getUserId());
			otcOrderLog.setCreatorName("");
			otcOrderLog.setCreateTime(new Date());
			otcOrderLog.setRemark("取消订单");
			otcOrderLog.setOrderId(order.getId());
			if (otcOrderLogMapper.insertSelective(otcOrderLog) <= 0) {
				throw new BCException("生成订单流水出错");
			}
			
			return Result.success();
		} catch (BCException e) {
			throw e;
		} catch (Exception e) {
			throw new BCException("取消订单异常");
		}
	}
	
	@Override
	@Transactional(value="flexibleTransMgr" ,isolation = Isolation.REPEATABLE_READ, propagation=Propagation.REQUIRED, rollbackFor = Exception.class)
	public Result payOrder(OtcOrder otcOrder) throws BCException{
		try {

			if(otcOrder == null || otcOrder.getId().compareTo(0l) == 0) {
				return Result.param("otcOrder is null");
			}
			
			//判断是否存在订单
			OtcOrder order = this.findById(otcOrder.getId());
			if(order == null){
				return Result.param("otcOrder is null");
			}
			byte beforeStatus = order.getStatus();
			
			//订单是否为待处理状态
			if(!order.getStatus().equals(OtcOrderStatusEnum.WAIT.getCode().byteValue())){
				return Result.failure("invalid otcOrder status");
			}
			
			if(!order.getUserId().equals(otcOrder.getUserId())
					&&!order.getAdUserId().equals(otcOrder.getUserId())) {
				return Result.failure("invalid otcOrder user");
			}
			
			//更新订单状态
			order.setStatus(OtcOrderStatusEnum.PAID.getCode().byteValue());
			
			//更新订单可申诉时间 2小时
//			int maxAppealtime = 2*3600000;
			int maxAppealtime = 120000;
			//当前时间加上限制时间
			long appealTime = new Date().getTime()+maxAppealtime;
			order.setAppealTime(new Timestamp(appealTime));
			if(otcOrderMapper.updateByPrimaryKeySelective(order) <= 0 ) {
				throw new BCException("支付订单出错");
			}
			OtcAdvert advert = otcAdverService.selectAdvertById(order.getAdId().intValue());
			OtcUserExt otcUserExt = otcUserService.getOtcUserExt(order.getUserId());
			OtcUserExt otcAdUserExt = otcUserService.getOtcUserExt(order.getAdUserId());
			
			String coinName = otcAdverService.getCoinName(advert.getCoinId());
			//发送消息
			try {
				if(advert.getSide().equals(Constant.OTC_SELL)) {
					//String msg = otcUserExt.getNickname()+" 已经付款，正在等待 "+otcAdUserExt.getNickname()+" 确认 ";
                    String msg = I18NUtils.getString("OtcOrderService.OtcOrder.payMsg");
                    msg  =  MessageFormat.format(msg ,otcUserExt.getNickname(),otcAdUserExt.getNickname());
					sendMsg(msg, otcUserExt.getUserId(), otcAdUserExt.getUserId(), order.getId(),new Date());
					
					//给出售广告方发短信user_pay
					sendSMS(otcAdUserExt.getUserId(),coinName,order.getAmount(),"user_pay",order.getOrderNo());
				}else if(advert.getSide().equals(Constant.OTC_BUY)) {
					//String msg = otcAdUserExt.getNickname()+" 已经付款，正在等待 "+otcUserExt.getNickname()+" 确认 ";
                    String msg = I18NUtils.getString("OtcOrderService.OtcOrder.payMsg");
                    msg  =  MessageFormat.format(msg ,otcAdUserExt.getNickname(),otcUserExt.getNickname());
					sendMsg(msg, otcAdUserExt.getUserId(), otcUserExt.getUserId(), order.getId(),new Date());
					
					//给购买广告方发短信gd_pay
					sendSMS(otcUserExt.getUserId(),coinName,order.getAmount(),"gd_pay",order.getOrderNo());
				}
			}  catch (Exception e) {
				throw new BCException("发送信息失败");
			}
			
			//订单操作日志
			OtcOrderLog otcOrderLog = new OtcOrderLog();
			otcOrderLog.setBeforeStatus(beforeStatus);
			otcOrderLog.setAfterStatus(order.getStatus());
			otcOrderLog.setCreator((long)otcOrder.getUserId());
			otcOrderLog.setCreatorName("");
			otcOrderLog.setCreateTime(new Date());
			otcOrderLog.setRemark("支付订单");
			otcOrderLog.setOrderId(order.getId());
			if(otcOrderLogMapper.insertSelective(otcOrderLog) <= 0 ) {
				throw new BCException("生成订单流水出错");
			}
			return Result.success();
		} catch (BCException e) {
			throw e;
		} catch (Exception e) {
			throw new BCException("确认支付异常");
		}
	}

	@Override
	public Result appealOrder(OtcOrder otcOrder,OtcAppeal otcAppeal) throws BCException {
		try {

			if(otcOrder == null || otcOrder.getId().compareTo(0l) == 0) {
				return Result.param("otcOrder is null");
			}
			
			//判断是否存在订单
			OtcOrder order = this.findById(otcOrder.getId());
			if(order == null){
				return Result.param("otcOrder is null");
			}
			byte beforeStatus = order.getStatus();
			
			//订单是否为已支付状态
			if(!order.getStatus().equals(OtcOrderStatusEnum.PAID.getCode().byteValue())){
				return Result.failure("invalid otcOrder status");
			}
			
			//如果不是订单用户或者广告用户
			if(!otcOrder.getUserId().equals(order.getUserId())
					&&!otcOrder.getUserId().equals(order.getAdUserId())) {
				return Result.failure("invalid otcOrder user");
			}
			
			//如果不存在申诉类型
			if(otcAppeal.getType()<0) {
				return Result.failure("invalid otcOrder user");
			}
			
			//查询当前订单，当前用户是否已经在申诉了
			OtcAppeal appeal = otcAppealMapper.selectByOrderId(order.getId(),otcOrder.getUserId());
			if(appeal != null) {
				return Result.failure("请不要重复申诉！");
			}
			
			otcAppeal.setCreateTime(new Date());
			otcAppeal.setOperateId(otcOrder.getUserId());
			otcAppeal.setOrderId(order.getId());
			otcAppeal.setOrderNo(order.getOrderNo());
			otcAppeal.setStatus(OtcAppealStatusEnum.WAIT.getCode().byteValue());
			otcAppeal.setUserId(otcOrder.getUserId());
			otcAppeal.setUpdateTime(new Date());
			//添加申诉
			if (otcAppealMapper.insert(otcAppeal) <= 0) {
				throw new BCException("添加申诉失败");
			}
			//修改订单状态为申诉状态
			//更新订单状态
			order.setStatus(OtcOrderStatusEnum.APPEAL.getCode().byteValue());
			if(otcOrderMapper.updateByPrimaryKeySelective(order) <= 0 ) {
				throw new BCException("更新订单失败");
			}
			
			OtcAdvert advert = otcAdverService.selectAdvertById(order.getAdId().intValue());
			OtcUserExt otcUserExt = otcUserService.getOtcUserExt(order.getUserId());
			OtcUserExt otcAdUserExt = otcUserService.getOtcUserExt(order.getAdUserId());
			//发送消息
			try {
                String msg = I18NUtils.getString("OtcOrderService.OtcOrder.appealMsg");
				if(advert.getSide().equals(Constant.OTC_SELL)) {
					//String msg = otcUserExt.getNickname()+" 已提起了申诉，等待平台客服介入处理！";
                    msg  =  MessageFormat.format(msg ,otcUserExt.getNickname());
					sendMsg(msg, otcUserExt.getUserId(), otcAdUserExt.getUserId(), order.getId(),new Date());
				}else if(advert.getSide().equals(Constant.OTC_BUY)) {
					//String msg = otcAdUserExt.getNickname()+" 已提起了申诉，等待平台客服介入处理！ ";
                    msg  =  MessageFormat.format(msg ,otcAdUserExt.getNickname());
					sendMsg(msg, otcAdUserExt.getUserId(), otcUserExt.getUserId(), order.getId(),new Date());
				}
			} catch (Exception e) {
				throw new BCException("发送信息失败");
			}
			
			//订单操作日志
			OtcOrderLog otcOrderLog = new OtcOrderLog();
			otcOrderLog.setBeforeStatus(beforeStatus);
			otcOrderLog.setAfterStatus(order.getStatus());
			otcOrderLog.setCreator((long)otcOrder.getUserId());
			otcOrderLog.setCreatorName("");
			otcOrderLog.setCreateTime(new Date());
			otcOrderLog.setRemark("申诉中");
			otcOrderLog.setOrderId(order.getId());
			if (otcOrderLogMapper.insertSelective(otcOrderLog) <= 0) {
				throw new BCException("生成订单流水出错");
			}
			return Result.success();
		
		} catch (BCException e) {
			throw e;
		} catch (Exception e) {
			throw new BCException("申诉订单异常");
		}
	}

	@Override
	public Result appealCancel(OtcOrder otcOrder) throws BCException {
		try {

			if(otcOrder == null || otcOrder.getId().compareTo(0l) == 0) {
				return Result.param("otcOrder is null");
			}
			
			//判断是否存在订单
			OtcOrder order = this.findById(otcOrder.getId());
			if(order == null){
				return Result.param("otcOrder is null");
			}
			
			//如果不是订单用户
			FUser user = userMapper.selectByPrimaryKey(otcOrder.getUserId());
			if(!order.getUserId().equals(user.getFid())
					&&!order.getAdUserId().equals(user.getFid())) {
				return Result.failure("invalid otcOrder user");
			}
			
			//查询对应的申诉
			OtcAppeal otcAppeal = otcAppealMapper.selectByOrderId(order.getId(),user.getFid());
			
			//判断申诉状态，只能在等待状态才能取消
			if(otcAppeal == null || !otcAppeal.getStatus().equals(OtcAppealStatusEnum.WAIT.getCode().byteValue())) {
				return Result.failure("invalid otcAppeal");
			}
			
			//设置为取消状态
			otcAppeal.setStatus(OtcAppealStatusEnum.CANCEL.getCode().byteValue());
			otcAppealMapper.updateByPrimaryKey(otcAppeal);
			
			//获取上次修改前的订单状态
			//查询最新一条
			List<OtcOrderLog> logs = otcOrderLogMapper.findByOrderId(order.getId());
	        if(logs == null || logs.size() <= 0){
	        	return Result.failure("invalid otcAppeal");
	        }
			OtcOrderLog otcOrderLog = logs.get(0);
			//更新订单状态
			order.setStatus(otcOrderLog.getBeforeStatus());
			if(otcOrderMapper.updateByPrimaryKeySelective(order) <= 0 ) {
				throw new BCException("更新订单失败");
			}
			
			OtcAdvert advert = otcAdverService.selectAdvertById(order.getAdId().intValue());
			OtcUserExt otcUserExt = otcUserService.getOtcUserExt(order.getUserId());
			OtcUserExt otcAdUserExt = otcUserService.getOtcUserExt(order.getAdUserId());
			//发送消息
			try {
                String msg = I18NUtils.getString("OtcOrderService.OtcOrder.appealCancel");
				if(advert.getSide().equals(Constant.OTC_SELL)) {
					//String msg = otcUserExt.getNickname()+" 已取消申诉！";
                    msg  =  MessageFormat.format(msg ,otcUserExt.getNickname());
					sendMsg(msg, otcUserExt.getUserId(), otcAdUserExt.getUserId(), order.getId(),new Date());
				}else if(advert.getSide().equals(Constant.OTC_BUY)) {
					//String msg = otcAdUserExt.getNickname()+" 已取消申诉！ ";
                    msg  =  MessageFormat.format(msg ,otcAdUserExt.getNickname());
					sendMsg(msg, otcAdUserExt.getUserId(), otcUserExt.getUserId(), order.getId(),new Date());
				}
			} catch (Exception e) {
				throw new BCException("发送信息失败");
			}
			return Result.success();
		
		} catch (BCException e) {
			throw e;
		} catch (Exception e) {
			throw new BCException("取消申诉订单异常");
		}
	}

	@Override
	public Result extendOrder(OtcOrder otcOrder) throws BCException {
		try {

			if(otcOrder == null || otcOrder.getId().compareTo(0l) == 0) {
				return Result.param("otcOrder is null");
			}
			
			//判断是否存在订单
			OtcOrder order = this.findById(otcOrder.getId());
			if(order == null){
				return Result.param("otcOrder is null");
			}
			
			//如果不是订单用户
			if(!otcOrder.getUserId().equals(order.getUserId())
					&& !otcOrder.getUserId().equals(order.getAdUserId())) {
				return Result.failure("invalid otcOrder user");
			}
			
			if(!order.getStatus().equals(OtcOrderStatusEnum.WAIT.getCode().byteValue())) {
				return Result.failure("订单状态不正确！");
			}
			
			//判断是否延长过时间了
			if(order.isExtendTime()) {
				return Result.failure("已经延长过订单有效期了！");
			}
			
			OtcAdvert otcAdvert = otcAdvertMapper.selectAdvertById(order.getAdId().intValue());
			int maxPaytime = otcAdvert.getMaxPaytime()*60000;
			//当前时间加上限制时间
			long limit = order.getLimitTime().getTime()+maxPaytime;
			order.setLimitTime(new Timestamp(limit));
			order.setExtendTime(true);
			//更新订单状态
			if(otcOrderMapper.updateByPrimaryKeySelective(order) <= 0 ) {
				return Result.failure("update otcOrder failure");
			}
			
			OtcUserExt otcUserExt = otcUserService.getOtcUserExt(order.getUserId());
			OtcUserExt otcAdUserExt = otcUserService.getOtcUserExt(order.getAdUserId());
			//发送消息
			try {
                String msg = I18NUtils.getString("OtcOrderService.OtcOrder.extendPayMsg");
				if(otcAdvert.getSide().equals(Constant.OTC_SELL)) {
					//String msg = otcUserExt.getNickname()+" 延长了付款期限至"+DateUtils.format(new Timestamp(limit), "yyyy-MM-dd HH:mm:ss");
                    msg  =  MessageFormat.format(msg ,otcUserExt.getNickname())+DateUtils.format(new Timestamp(limit), "yyyy-MM-dd HH:mm:ss");
					sendMsg(msg, otcUserExt.getUserId(), otcAdUserExt.getUserId(), order.getId(),new Date());
				}else if(otcAdvert.getSide().equals(Constant.OTC_BUY)) {
					//String msg = otcAdUserExt.getNickname()+" 延长了付款期限至"+DateUtils.format(new Timestamp(limit), "yyyy-MM-dd HH:mm:ss");
                    msg  =  MessageFormat.format(msg ,otcAdUserExt.getNickname())+DateUtils.format(new Timestamp(limit), "yyyy-MM-dd HH:mm:ss");
					sendMsg(msg, otcAdUserExt.getUserId(), otcUserExt.getUserId(), order.getId(),new Date());
				}
			} catch (Exception e) {
				throw new BCException("发送信息失败");
			}
			
			return Result.success();
		
		} catch (BCException e) {
			throw e;
		} catch (Exception e) {
			throw new BCException("延长订单时间");
		}
	}

	@Override
	public Result evaluation(Long orderId,Integer userId,Integer type) throws BCException {
		try {

			//评价商户由于不需要记录评论只记录好评数，所以直接操作对应的商家扩展表
			if(orderId == null || orderId < 0) {
				return Result.param("otcOrder is null");
			}
			
			//判断是否存在订单
			OtcOrder order = this.findById(orderId);
			if(order == null){
				return Result.param("otcOrder is null");
			}
			
			//判断订单状态是否已完成
			if(!order.getStatus().equals(OtcOrderStatusEnum.COMPLETED.getCode().byteValue())){
				return Result.param("otcOrder status faliure");
			}
			
			if(type <= 0) {
				return Result.param("type is null");
			}
			
			//如果不是订单用户
			if(!order.getUserId().equals(userId)
					&& !order.getAdUserId().equals(userId)) {
				return Result.failure("invalid otcOrder user");
			}
			
			int uid = 0;
			if(userId.equals(order.getUserId())) {
				uid = order.getAdUserId();
				if(order.isUserEvaluation()) {
					return Result.param("请不要重复提交评价！");
				}
				//更新订单状态
				order.setUserEvaluation(true);
				order.setUserEvaluationType(type);
			}else if(userId.equals(order.getAdUserId())) {
				uid = order.getUserId();
				if(order.isAdUserEvaluation()) {
					return Result.param("请不要重复提交评价！");
				}
				//更新订单状态
				order.setAdUserEvaluation(true);
				order.setAdUserEvaluationType(type);
			}
			
			OtcUserExt otcUserExt = otcUserService.getOtcUserExt(uid);
			switch (type) {
				case 1:
					//增加好评数
					int good = otcUserExt.getGoodEvaluation()+1;
					otcUserExt.setGoodEvaluation(good);
					otcUserService.update(otcUserExt);
					break;
				case 2:
					break;
				case 3:
					//增加差评数
					int bad = otcUserExt.getBadEvaluation()+1;
					otcUserExt.setBadEvaluation(bad);
					otcUserService.update(otcUserExt);
					break;
			}
			
			if(otcOrderMapper.updateByPrimaryKeySelective(order) <= 0 ) {
				throw new BCException("更新订单失败");
			}
			
			//发送消息
			try {
				OtcUserExt msgUser = otcUserService.getOtcUserExt(userId);
				//String msg = msgUser.getNickname()+" 已评价 ";
                String msg = I18NUtils.getString("OtcOrderService.OtcOrder.evaluationMsg");
                msg  =  MessageFormat.format(msg ,msgUser.getNickname());
				if(userId.equals(order.getUserId())) {
					sendMsg(msg, order.getUserId(), order.getAdUserId(), order.getId(),new Date());
				}else if(userId.equals(order.getAdUserId())) {
					sendMsg(msg, order.getAdUserId(), order.getUserId(), order.getId(),new Date());
				}
			} catch (Exception e) {
				throw new BCException("发送信息失败");
			}
			return Result.success();
		
		} catch (BCException e) {
			throw e;
		} catch (Exception e) {
			throw new BCException("评价商家异常");
		}
	}

	@Override
	public List<FUserBankinfoDTO> getBankInfoListByUser(Integer userId, Integer typeId) {
		return fUserBankinfoMapper.getBankInfoListByUser(userId, typeId);
	}

	@Override
	public OtcAppeal selectByOrderId(Long orderId,int userId) {
		return otcAppealMapper.selectByOrderId(orderId,userId);
	}

	@Override
	public int countOrder(Integer userId) {
		return otcOrderMapper.countOrder(userId);
	}
	
	@Override
	@Transactional(value="flexibleTransMgr" ,isolation = Isolation.REPEATABLE_READ, propagation=Propagation.REQUIRED, rollbackFor = Exception.class)
	public void putOffUnableOrders() throws Exception {
		//更改过期订单的状态
		List<OtcOrder> list = otcOrderMapper.expiredOrderList();
		if(list != null && list.size() > 0) {
			for(OtcOrder order : list) {
				OtcAdvert advert = otcAdvertMapper.selectAdvertById(order.getAdId().intValue());
				byte beforeStatus = order.getStatus();
				
				String beforeData = "";
				String afterData = "";
				//如果是出售虚拟币广告还需要解冻订单用户的虚拟币
				if(advert.getSide().equals(Constant.OTC_BUY)) {
					//查询订单用户的钱包余额
					BigDecimal amount = order.getAmount();
					UserOtcCoinWallet wallet = otcCoinWalletService.selectOtcWalletByUidAndType(order.getSellerId(), advert.getCoinId());
			        beforeData = JSONObject.toJSONString(wallet);
					//解冻钱包对应的虚拟币
					otcCoinWalletService.userOtcUnFrozen(order.getUserId(), advert.getCoinId(), amount);
					wallet.setTotal(MathUtils.add(wallet.getTotal(), amount));
			        wallet.setFrozen(MathUtils.sub(wallet.getFrozen(), amount));
			        afterData = JSONObject.toJSONString(wallet);
					//更新广告成交数量
					updateAdAmountCancel(order.getAmount(), advert);
				}else if(advert.getSide().equals(Constant.OTC_SELL)) {
					//出售广告下的购买订单被取消，订单冻结金额的返还，需先判断2个条件：
					//1.出售广告的状态    2. 出售广告的更新时间
					//一、出售广告的状态为上架，出售广告的更新时间 < 订单创建时间 ：订单冻结金额直接返还到广告冻结
					//二、出售广告的状态为上架，出售广告的更新时间 > 订单创建时间 ：订单冻结金额直接返还到用户账户
					//三、出售广告的状态为下架：订单冻结金额直接返还到用户账户
					//四、出售广告的状态为过期：订单冻结金额直接返还到用户账户
					if(order.getCreateTime().before(advert.getUpdateTime())) {
						//出售广告的更新时间 > 订单创建时间
						//查询订单用户的钱包余额
						BigDecimal amount = order.getAmount();
						BigDecimal totalAmount = MathUtils.add(amount,order.getAdFee());
						UserOtcCoinWallet wallet = otcCoinWalletService.selectOtcWalletByUidAndType(order.getSellerId(), advert.getCoinId());
				        beforeData = JSONObject.toJSONString(wallet);
						//解冻钱包对应的虚拟币
						otcCoinWalletService.userOtcUnFrozen(order.getAdUserId(), advert.getCoinId(), totalAmount);
						wallet.setTotal(MathUtils.add(wallet.getTotal(), totalAmount));
					    wallet.setFrozen(MathUtils.sub(wallet.getFrozen(), totalAmount));
					    afterData = JSONObject.toJSONString(wallet);
					}else {
						//出售广告的更新时间 <= 订单创建时间
						//更新广告成交数量
						beforeData = JSONObject.toJSONString(advert);
						updateAdAmountCancel(order.getAmount(), advert);
						afterData = JSONObject.toJSONString(advert);
					}
				}
				order.setStatus(OtcOrderStatusEnum.TIMEOUT.getCode().byteValue());
				otcOrderMapper.updateByPrimaryKeySelective(order);
				
				//String msg = "订单已过期，系统已自动取消，请勿转帐";
				String msg = I18NUtils.getString("OtcAdvertJob.11");
				if(advert.getSide().equals(Constant.OTC_SELL)) {
					sendMsg(msg, order.getUserId(), order.getAdUserId(), order.getId());
				}else if(advert.getSide().equals(Constant.OTC_BUY)) {
					sendMsg(msg, order.getAdUserId(), order.getUserId(), order.getId());
				}
				
				//订单操作日志
				OtcOrderLog otcOrderLog = new OtcOrderLog();
				otcOrderLog.setBeforeData(beforeData);
				otcOrderLog.setAfterData(afterData);
				otcOrderLog.setBeforeStatus(beforeStatus);
				otcOrderLog.setAfterStatus(order.getStatus());
				otcOrderLog.setCreator((long)order.getUserId());
				otcOrderLog.setCreatorName("");
				otcOrderLog.setCreateTime(new Date());
				otcOrderLog.setRemark("定时取消订单");
				otcOrderLog.setOrderId(order.getId());
				otcOrderLogMapper.insertSelective(otcOrderLog);
				
			}
		}
	}
	
	public void sendMsg(String msg,Integer sender,Integer receiver,Long orderId) {
		this.sendMsg(msg, sender, receiver, orderId, "system");
	}
	
	public void sendMsg(String msg,Integer sender,Integer receiver,Long orderId,String msgType) {
		OtcUserExt senderExt = otcUserService.getOtcUserExt(sender);
		OtcUserExt receiverExt = otcUserService.getOtcUserExt(receiver);
		
		ChatMessage chatMessage = new ChatMessage();
        chatMessage.setSender(sender.longValue());
        chatMessage.setReceiver(receiver.toString());
        chatMessage.setOrderId(orderId.toString());
        chatMessage.setMessage(msg);
        chatMessage.setExtendsJson("");
        chatMessage.setSendState(1);
        chatMessage.setMsgType(msgType);
        chatMessage.setSenderFnickname(senderExt.getNickname());
        String recvNickName = receiverExt.getNickname();
        chatMessage.setReceiverFnickname(recvNickName);
        long time = new Date().getTime()+100;
        chatMessage.setCreateTime(new Timestamp(time));
        chatMessage.setSendTime(new Timestamp(time));
        chatMessage.setSendType(1);
        chatMessageMapper.insert(chatMessage);
	}
	
	@Override
	public Integer countSuccBuyOrder(Integer userId) {
		return otcOrderMapper.countSuccBuyOrder(userId);
	}
	
	@Override
	public Integer countSuccSellOrder(Integer userId) {
		return otcOrderMapper.countSuccSellOrder(userId);
	}
	
	@Override
	public String getCompletionRate(Integer userId) {
		return otcOrderMapper.getCompletionRate(userId);
	}
	
	@Override
	public Integer getCmpOrders(Integer userId) {
		return otcOrderMapper.getCmpOrders(userId);
	}
	
	@Override
	public FUser getUser(Integer userId) {
		return userMapper.selectByPrimaryKey(userId);
	}
}
