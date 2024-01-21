package com.qkwl.service.admin.bc.impl;


import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.qkwl.common.dto.Enum.BankInfoStatusEnum;
import com.qkwl.common.dto.Enum.otc.OtcAppealResultEnum;
import com.qkwl.common.dto.Enum.otc.OtcAppealStatusEnum;
import com.qkwl.common.dto.Enum.otc.OtcOrderStatusEnum;
import com.qkwl.common.dto.Enum.otc.OtcUserTypeEnum;
import com.qkwl.common.dto.capital.FUserBankinfoDTO;
import com.qkwl.common.dto.chat.ChatMessage;
import com.qkwl.common.dto.market.TickerData;
import com.qkwl.common.dto.otc.OtcAdvert;
import com.qkwl.common.dto.otc.OtcAppeal;
import com.qkwl.common.dto.otc.OtcOrder;
import com.qkwl.common.dto.otc.OtcOrderLog;
import com.qkwl.common.dto.otc.OtcUserExt;
import com.qkwl.common.dto.otc.OtcUserOrder;
import com.qkwl.common.dto.otc.OtcUserTransfer;
import com.qkwl.common.dto.user.FUser;
import com.qkwl.common.dto.wallet.UserOtcCoinWallet;
import com.qkwl.common.framework.redis.RedisHelper;
import com.qkwl.common.match.MathUtils;
import com.qkwl.common.result.Result;
import com.qkwl.common.rpc.admin.IAdminOtcOrderService;
import com.qkwl.common.rpc.admin.IAdminOtcService;
import com.qkwl.common.rpc.admin.IAdminUserService;
import com.qkwl.common.rpc.admin.IMultiLangService;
import com.qkwl.common.util.Constant;
import com.qkwl.common.util.DateUtils;
import com.qkwl.common.util.ReturnResult;
import com.qkwl.common.util.Utils;
import com.qkwl.service.admin.bc.dao.ChatMessageMapper;
import com.qkwl.service.admin.bc.dao.FUserBankinfoMapper;
import com.qkwl.service.admin.bc.dao.MultiLangMapper;
import com.qkwl.service.admin.bc.dao.OtcAdvertMapper;
import com.qkwl.service.admin.bc.dao.OtcAppealMapper;
import com.qkwl.service.admin.bc.dao.OtcOrderLogMapper;
import com.qkwl.service.admin.bc.dao.OtcOrderMapper;
import com.qkwl.service.admin.bc.dao.OtcUserExtMapper;
import com.qkwl.service.admin.bc.dao.OtcUserTransferMapper;
import com.qkwl.service.admin.bc.dao.UserOtcCoinWalletMapper;

@Service("adminOtcOrderService")
public class AdminOtcOrderServiceImpl implements IAdminOtcOrderService {

	private static final Logger logger = LoggerFactory.getLogger(AdminOtcOrderServiceImpl.class);

	@Autowired
	private OtcOrderMapper otcOrderMapper;
	@Autowired
	private OtcAppealMapper otcAppealMapper;
	@Autowired
	private ChatMessageMapper chatMessageMapper;
	@Autowired
	private FUserBankinfoMapper userBankinfoMapper;
	@Autowired
	private OtcAdvertMapper otcAdvertMapper;
	@Autowired
	private UserOtcCoinWalletMapper userOtcCoinWalletMapper;
	@Autowired
	private OtcUserTransferMapper userOtcTransferMapper;
	@Autowired
	private OtcUserExtMapper otcUserExtMapper;
	@Autowired
	private IAdminUserService adminUserService;
	@Autowired
	private RedisHelper redisHelper;
	@Autowired
	private OtcOrderLogMapper otcOrderLogMapper;
	@Autowired
	private IMultiLangService multiLangService;
	@Autowired
	private IAdminOtcService adminOtcService;
	@Autowired
	private AdminOtcOrderServiceImplTX adminOtcOrderServiceImplTX;
	
	
	@Override
	public PageInfo<OtcOrder> listOrder(Map<String, Object> param,int pageNo,int pageSize) {
		try {
			//条件查询订单，带分页
			PageHelper.startPage(pageNo, pageSize);
			List<OtcOrder> list = otcOrderMapper.listOrder(param);
			
			list.forEach(e->{
			  e.getOtcPayment().setName(multiLangService.getMsg(e.getOtcPayment().getNameCode()));
			});
			
			
			
			PageInfo<OtcOrder> pageInfo = new PageInfo<OtcOrder>(list);
			return pageInfo;
		} catch (Exception e) {
			logger.error("查询订单异常",e);
			return null;
		}
		
	}

	@Override
	public OtcOrder findById(Long id) {
		return otcOrderMapper.selectByPrimaryKey(id);
	}

	@Override
	public OtcAppeal selectByOrderId(Long orderId) {
		try {
			List<Integer> ststuaList = new ArrayList<Integer>();
			ststuaList.add(OtcAppealStatusEnum.WAIT.getCode());
			ststuaList.add(OtcAppealStatusEnum.PROCESSING.getCode());
			List<OtcAppeal> selectByOrderId = otcAppealMapper.selectByOrderId(orderId,null,ststuaList);
			if(selectByOrderId != null && selectByOrderId.size() > 0) {
				return selectByOrderId.get(0);
			}
		} catch (Exception e) {
			logger.error("查询申诉异常",e);
		}
		return null;
	}

	@Override
	public List<ChatMessage> chatHistory(Integer orderId) {
		try {
			ChatMessage chatMessage = new ChatMessage();
			chatMessage.setOrderId(orderId.toString());
			List<ChatMessage> selectList = chatMessageMapper.selectList(chatMessage);
			return selectList;
		} catch (Exception e) {
			logger.error("查询聊天记录异常",e);
		}
		return null;
	}
	
	@Override
	public FUserBankinfoDTO selectUserPaymentByOrder(Integer orderId) {
		try {
			OtcOrder selectByPrimaryKey = otcOrderMapper.selectByPrimaryKey(orderId.longValue());
			if(selectByPrimaryKey == null) {
				return null;
			}

			OtcAdvert otcAdvert = otcAdvertMapper.selectAdvertById(selectByPrimaryKey.getAdId().intValue());
			// 查询对方的交易方式
			int userId = 0;
			// 购买广告则显示用户的支付方式
			if (otcAdvert.getSide().equals(Constant.OTC_BUY)) {
				userId = selectByPrimaryKey.getUserId();
			} else if (otcAdvert.getSide().equals(Constant.OTC_SELL)) {
				// 出售广告显示广告主的
				userId = otcAdvert.getUserId();
			}

			FUserBankinfoDTO selectByUserAndType = userBankinfoMapper.selectByUserAndType(userId,selectByPrimaryKey.getPayment(), BankInfoStatusEnum.NORMAL_VALUE, true);
			selectByUserAndType.setPaymentName(multiLangService.getMsg(selectByUserAndType.getPaymentNameCode()));
			
			return selectByUserAndType;
		} catch (Exception e) {
			logger.error("查询聊天记录异常",e);
		}
		return null;
	}
	
	
	@Override
	@Transactional(value = "flexibleTransMgr", isolation = Isolation.REPEATABLE_READ, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public ReturnResult submitOtcAppeal(OtcAppeal otcAppeal) throws Exception {
	    OtcAppeal obj = otcAppealMapper.selectByPrimaryKey(otcAppeal.getId());
	    
	    OtcOrder otcOrder = findById(obj.getOrderId());
	    // 申诉结果打币或者取消订单
	    if (otcAppeal.getResult().compareTo(OtcAppealResultEnum.PAID_TOKEN.getCode().byteValue()) == 0) {
	      // 打币
	      confirmOrder(otcOrder);
	    } else if (otcAppeal.getResult().compareTo(OtcAppealResultEnum.CANCEL.getCode().byteValue()) == 0) {
	      //取消
	      cancelOrder(otcOrder);
	    }

	    obj.setResult(otcAppeal.getResult());
	    obj.setWinSide(otcAppeal.getWinSide());
	    obj.setImageUrl(otcAppeal.getImageUrl());
	    obj.setRemark(otcAppeal.getRemark());
	    obj.setStatus(OtcAppealStatusEnum.COMPLETE.getCode().byteValue());
	    if (otcAppealMapper.updateByPrimaryKey(obj) <= 0) {
	      throw new Exception("申诉表修改失败");
	    };
	    return ReturnResult.SUCCESS("成功");
	}
	
	public Result userOtcOrderDell(OtcUserOrder order) {
		try {
			return adminOtcOrderServiceImplTX.userOtcOrderDellImpl(order);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return Result.failure("成交时更新钱包失败");
	}

	public void confirmOrder(OtcOrder otcOrder) throws Exception 
	{
	    //判断是否存在订单
	    OtcOrder order = this.findById(otcOrder.getId());
	    byte beforeStatus = order.getStatus();
	    
	    //订单是否为申诉状态
	    if(!order.getStatus().equals(OtcOrderStatusEnum.APPEAL.getCode().byteValue())){
	      throw new Exception("订单状态不为申诉中");
	    }
	    OtcAdvert otcAdvert = otcAdvertMapper.selectAdvertByIdLock(order.getAdId().intValue());
	    //检查otc资产平衡
	    if (!adminOtcService.otcBalance(otcOrder.getUserId(), otcAdvert.getCoinId())) {
	    	throw new Exception("订单用户otc账户不平衡");
	    }
	    if (!adminOtcService.otcBalance(otcOrder.getAdUserId(), otcAdvert.getCoinId())) {
	    	throw new Exception("广告用户otc账户不平衡");
	    }
	    
	    UserOtcCoinWallet buyWalletBefore = selectByUidAndType(order.getBuyerId(), otcAdvert.getCoinId());
    	UserOtcCoinWallet sellWalletBefore = selectByUidAndType(order.getSellerId(), otcAdvert.getCoinId());
    	String beforeData = JSONObject.toJSONString(buyWalletBefore) + JSONObject.toJSONString(sellWalletBefore) + JSONObject.toJSONString(otcAdvert);
	    
	    //买入广告，用户方看是售出
	    if(otcAdvert.getSide().equals(Constant.OTC_BUY)) {
	      //如果是买入广告则判断当前用户是否订单用户，则广告方打币
	      OtcUserOrder otcUserOrder = new OtcUserOrder();
	      otcUserOrder.setAmount(order.getAmount());
	      otcUserOrder.setCoinId(otcAdvert.getCoinId());
	      otcUserOrder.setFee(order.getAdFee());
	      //卖出方是用户，广告主是买入
	      otcUserOrder.setSellerId(order.getUserId());
	      //买入方是广告主
	      otcUserOrder.setBuyerId(otcAdvert.getUserId());
	      otcUserOrder.setType(1);
	      //扣广告主的手续费
	      Result result = userOtcOrderDell(otcUserOrder);
	      if(!result.getSuccess()) {
	        throw new Exception("更新钱包失败");
	      }
	    }else if(otcAdvert.getSide().equals(Constant.OTC_SELL)) {
	      //打币如果是售出广告则需要判断当前用户是否是广告主
	      //当前为售卖广告,广告发布者为卖家,在发布广告的已经冻结了余额账户,先判断冻结账户余额是否充足
	      OtcUserOrder otcUserOrder = new OtcUserOrder();
	      otcUserOrder.setAmount(order.getAmount());
	      otcUserOrder.setCoinId(otcAdvert.getCoinId());
	      otcUserOrder.setFee(order.getAdFee());
	      //卖出方是广告
	      otcUserOrder.setSellerId(otcAdvert.getUserId());
	      //买入方是用户
	      otcUserOrder.setBuyerId(order.getUserId());
	      otcUserOrder.setType(2);
	      Result result = userOtcOrderDell(otcUserOrder);
	      if(!result.getSuccess()) {
	        throw new Exception("更新钱包失败");
	      }
	    }
	    
	    //更新订单状态
	    order.setStatus(OtcOrderStatusEnum.COMPLETED.getCode().byteValue());
	    if(otcOrderMapper.updateByPrimaryKeySelective(order) <= 0 ) {
	      throw new Exception("confirmOrder出错");
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
	    OtcUserExt otcAdUserExt = getOtcUserExt(order.getAdUserId());
	    
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
	    if (otcUserExtMapper.updateByPrimaryKey(otcAdUserExt) <= 0) {
	      throw new Exception("卖家用户扩展表更新失败");
	    };
	    
	    //更新买家用户扩展信息，更新成交总金额 、成交笔数
	    OtcUserExt otcUserExt = getOtcUserExt(order.getUserId());
	    //增加好评数
	    int count = otcUserExt.getCmpOrders()+1;
	    //增加订单成交总金额
	    BigDecimal totalAmount = otcUserExt.getSuccAmt();
	    otcUserExt.setSuccAmt(MathUtils.add(totalAmount, toScaleAmount));
	    otcUserExt.setCmpOrders(count);
	    if (otcUserExtMapper.updateByPrimaryKey(otcUserExt) <= 0) {
	      throw new Exception("买家用户扩展表更新失败");
	    };
	    
	    OtcOrderLog otcOrderLog = new OtcOrderLog();
	    otcOrderLog.setBeforeData(beforeData);
	    otcOrderLog.setAfterData(afterData);
	    otcOrderLog.setBeforeStatus(beforeStatus);
	    otcOrderLog.setAfterStatus(order.getStatus());
	    otcOrderLog.setCreator((long)otcOrder.getUserId());
	    otcOrderLog.setCreatorName("");
	    otcOrderLog.setCreateTime(new Date());
	    otcOrderLog.setRemark("申诉确认打币");
	    otcOrderLog.setOrderId(order.getId());
	    if (otcOrderLogMapper.insertSelective(otcOrderLog) <= 0) {
	      throw new Exception("订单日志更新失败");
	    };
	    
	}
	
	public OtcUserExt getOtcUserExt(Integer userId) {
		try {
			//查询
			OtcUserExt otcUserExt = otcUserExtMapper.selectByUserId(userId);
			FUser user = adminUserService.selectById(userId);
			//如果otc扩展表没有就添加
			if(otcUserExt == null && user != null) {
				otcUserExt = new OtcUserExt();
				otcUserExt.setBadEvaluation(0);
				otcUserExt.setCmpOrders(0);
				otcUserExt.setGoodEvaluation(0);
				otcUserExt.setSuccAmt(BigDecimal.ZERO);
				otcUserExt.setSumAppeal(0);
				otcUserExt.setUserId(userId);
				otcUserExt.setWinAppeal(0);
				otcUserExt.setOtcUserType(OtcUserTypeEnum.Ordinary_Merchant.getCode());
				otcUserExt.setCreateTime(new Date());
				otcUserExt.setUpdateTime(new Date());
				otcUserExtMapper.insert(otcUserExt);
			}else if(user == null){
				return null;
			}
			otcUserExt.setGooglebind(user.getFgooglebind());
			otcUserExt.setIsmailbind(user.getFismailbind());
			otcUserExt.setIstelephonebind(user.getFistelephonebind());
			otcUserExt.setHasrealvalidate(user.getFhasrealvalidate());
			otcUserExt.setNickname(user.getFnickname());
			otcUserExt.setPhoto(user.getPhoto());
			otcUserExt.setRegistertime(user.getFregistertime());
			return otcUserExt;
		} catch (Exception e) {
			logger.error("查询用户otc扩展表异常",e);
		}
		return null;
	}
	
	/**
	 * 成交时需要更新广告数量
	 * @param amount
	 * @param otcAdvert
	 * @throws Exception 
	 */
	public void updateAdAmountSuccess(BigDecimal amount,OtcAdvert otcAdvert) throws Exception {
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
			throw new Exception("updateAdAmountSuccess出错");
		}
	}

	private UserOtcCoinWallet selectByUidAndType(Integer uid, Integer coinId) {

		UserOtcCoinWallet otcCoinWallet = userOtcCoinWalletMapper.select(uid, coinId);

		// 如果账号不存在，检查全部用户账户，创建缺失账户
		if (otcCoinWallet == null) {
			logger.info("selectByUidAndTypeAddLock otcCoinWallet is null insert one record = {}", uid);
			createLackWallet(uid, coinId);
			otcCoinWallet = userOtcCoinWalletMapper.select(uid, coinId);
		}
		return otcCoinWallet;
	}

	/**
	 * 缺失otc账户
	 */
	private void createLackWallet(Integer uid, Integer coinId) {
		logger.info("createLackWallet uid = {} ", uid);
		UserOtcCoinWallet otcCoinWallet = new UserOtcCoinWallet();
		otcCoinWallet.setTotal(BigDecimal.ZERO);
		otcCoinWallet.setFrozen(BigDecimal.ZERO);
		otcCoinWallet.setBorrow(BigDecimal.ZERO);
		otcCoinWallet.setIco(BigDecimal.ZERO);
		otcCoinWallet.setCoinId(coinId);
		otcCoinWallet.setUid(uid);
		otcCoinWallet.setGmtCreate(Utils.getTimestamp());
		otcCoinWallet.setGmtModified(Utils.getTimestamp());
		if (this.userOtcCoinWalletMapper.insert(otcCoinWallet) <= 0) {
			logger.error("注册用户otc钱包超时！");
		}
	}
	
	/**
	 * 取消时需要更新广告数量
	 * @param amount
	 * @param otcAdvert
	 * @throws Exception 
	 */
	public void updateAdAmountCancel(BigDecimal amount,OtcAdvert otcAdvert) throws Exception {
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
			throw new Exception("updateAdAmountCancel出错");
		}
	}
	
	public void cancelOrder(OtcOrder otcOrder) throws Exception{
	    if(otcOrder == null || otcOrder.getId().compareTo(0l) == 0) {
	        throw new Exception("订单为空");
	      }
	      
	      //判断是否存在订单
	      OtcOrder order = this.findById(otcOrder.getId());
	      if(order == null){
	        throw new Exception("订单为空");
	      }
	      byte beforeStatus = order.getStatus();
	      
	      //订单是否为申诉状态
	      if(!order.getStatus().equals(OtcOrderStatusEnum.APPEAL.getCode().byteValue())){
	        throw new Exception("订单状态不为申诉中");
	      }
	      
	      // 根据订单获取相应的广告对象 加锁
	      //更新广告数量
	      OtcAdvert advert = otcAdvertMapper.selectAdvertByIdLock(order.getAdId().intValue());
	      //检查otc资产平衡
//	      if (!adminOtcService.otcBalance(otcOrder.getUserId(), advert.getCoinId())) {
//	    	  throw new Exception("订单用户otc账户不平衡");
//	      }
//	      if (!adminOtcService.otcBalance(otcOrder.getAdUserId(), advert.getCoinId())) {
//	    	  throw new Exception("广告用户otc账户不平衡");
//	      }
	      
	      String beforeData = "";
	      String afterData = "";
	      //如果是购买虚拟币广告还需要解冻订单用户的虚拟币
	      if(advert.getSide().equals(Constant.OTC_BUY)) {
	        //查询订单用户的钱包余额
	        BigDecimal amount = order.getAmount();
	        UserOtcCoinWallet wallet = selectByUidAndType(order.getSellerId(), advert.getCoinId());
	        beforeData = JSONObject.toJSONString(wallet);
	        //解冻钱包对应的虚拟币
	        Result result = userOtcUnFrozen(order.getUserId(), advert.getCoinId(), amount);
	        if (!result.getSuccess()) {
	          throw new Exception("解冻钱包失败");
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
	          UserOtcCoinWallet wallet = selectByUidAndType(order.getSellerId(), advert.getCoinId());
	          beforeData = JSONObject.toJSONString(wallet);
	          //解冻钱包对应的虚拟币
	          Result result = userOtcUnFrozen(order.getAdUserId(), advert.getCoinId(), totalAmount);
	          if (!result.getSuccess()) {
	            throw new Exception("解冻钱包失败");
	          }
	          wallet.setTotal(MathUtils.add(wallet.getTotal(), totalAmount));
		      wallet.setFrozen(MathUtils.sub(wallet.getFrozen(), totalAmount));
		      afterData = JSONObject.toJSONString(wallet);
	        }else if(order.getCreateTime().after(advert.getUpdateTime())) {
	          //出售广告的更新时间 < 订单创建时间
	          beforeData = JSONObject.toJSONString(advert);
	          //更新广告成交数量
	          updateAdAmountCancel(order.getAmount(), advert);
	          afterData = JSONObject.toJSONString(advert);
	        }
	      }
	      
	      //更新订单状态
	      order.setStatus(OtcOrderStatusEnum.CANCELLED.getCode().byteValue());
	      if(otcOrderMapper.updateByPrimaryKeySelective(order) <= 0 ) {
	        throw new Exception("取消订单出错");
	      }
	      
	      //增加限制，每天只能取消3次
	      //撤销过三次订单则需要提示用户不准下订单了。
	      String key = "otc_cancel_limit_" + otcOrder.getUserId();
	      int sec = DateUtils.getOffSeconds_abs(new Date(),DateUtils.getCurrentDay());
	      redisHelper.getIncrByKey(key,sec);
	      
//	      OtcUserExt otcUserExt = getOtcUserExt(order.getUserId());
//	      OtcUserExt otcAdUserExt = getOtcUserExt(order.getAdUserId());
//	      //发送消息
//	      if(advert.getSide().equals(Constant.OTC_SELL)) {
//	        String msg = otcUserExt.getNickname()+" 已经取消订单，请勿再进行转帐或是数字币移交。";
//	        sendMsg(msg, otcUserExt.getId(), otcAdUserExt.getId(), order.getId());
//	      }else if(advert.getSide().equals(Constant.OTC_BUY)) {
//	        String msg = otcAdUserExt.getNickname()+" 已经取消订单，请勿再进行转帐或是数字币移交。";
//	        sendMsg(msg, otcAdUserExt.getId(), otcUserExt.getId(), order.getId());
//	      }
	      
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
	        throw new Exception("入订单日志失败");
	      };
	      
	}
	
	public Result userOtcUnFrozen(Integer uid,Integer coinId, BigDecimal amount) {
		try {
			adminOtcOrderServiceImplTX.userOtcUnFrozenImpl(uid,coinId,amount);
			return Result.success();
		} catch (Exception e) {
			logger.info("userOtcUnFrozen ex:{}", e);
			return Result.failure("取消订单时解冻失败");
		}
	}
	
}
