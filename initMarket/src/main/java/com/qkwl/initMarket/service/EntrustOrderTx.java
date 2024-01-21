package com.qkwl.initMarket.service;

//import com.eaio.uuid.UUID;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.qkwl.common.dto.Enum.*;
import com.qkwl.common.dto.api.FApiAuth;
import com.qkwl.common.dto.coin.SystemTradeType;
import com.qkwl.common.dto.coin.SystemTradeTypeVO;
import com.qkwl.common.dto.mq.MQEntrust;
import com.qkwl.common.dto.mq.MQEntrustState;
import com.qkwl.common.dto.wallet.UserCoinWallet;
import com.qkwl.common.dto.wallet.UserWalletBalanceLog;
import com.qkwl.common.framework.mq.MQOrderSendHelper;
import com.qkwl.common.framework.mq.MQSendHelper;
import com.qkwl.common.framework.redis.RedisHelper;
import com.qkwl.common.huobi.request.CreateOrderRequest;
import com.qkwl.common.huobi.response.Account;
import com.qkwl.common.match.MathUtils;
import com.qkwl.common.mq.MQConstant;
import com.qkwl.common.mq.MQTopic;
import com.qkwl.common.okhttp.ApiException;
import com.qkwl.common.okhttp.HBApiImpl;
import com.qkwl.common.redis.RedisConstant;
import com.qkwl.common.redis.RedisObject;
import com.qkwl.common.util.Utils;
import com.qkwl.initMarket.mapper.FEntrustMapper;
import com.qkwl.initMarket.mapper.InitMarketMapper;
import com.qkwl.initMarket.mapper.UserCoinWalletMapper;
import com.qkwl.initMarket.model.EntrustDO;
import com.qkwl.initMarket.model.InitMarket;

import net.sf.json.util.JSONUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.*;

/**
 * 委单接口实现
 *
 * @author LY
 */
@Service("entrustServerTx")
public class EntrustOrderTx {
    private static final Logger logger = LoggerFactory.getLogger(EntrustOrderTx.class);
    private static int TRY_TIMES = 2;
    @Autowired
    private FEntrustMapper entrustMapper;
    @Autowired
    private UserCoinWalletMapper userCoinWalletMapper;
//    @Autowired
//    private MQSendHelper entrustMQSend;
//    @Autowired
//    private MQOrderSendHelper OrderedMQSend;
    @Autowired
    private InitMarketMapper initMarketMapper;
//    @Autowired
//    private UserWalletBalanceLogMapper userWalletBalanceLogMapper;

    @Autowired
    private RedisHelper redisHelper;

    /**
     * 创建委单
     *
     * @param entrust 委单数据
     */
    @Transactional(value="flexibleTransMgr", isolation = Isolation.REPEATABLE_READ, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public BigInteger createEntrust(EntrustDO entrust) throws Exception {
        //火币下单接口
        /**
        long HBOrderID = 0;
        try {
            List<Account> accountList = HBApiImpl.getInstance().getAccountList();
            if (accountList != null && accountList.size() > 0){
                List<SystemTradeType> tradeTypeList = redisHelper.getTradeTypeList(0);
                String symbol = "";
                //获取交易对
                for (SystemTradeType type:tradeTypeList) {
                    if (type.getId() == entrust.getFtradeid()) {

                        symbol = type.getBuyShortName().toLowerCase() + type.getSellShortName().toLowerCase();
                    }
                }
                CreateOrderRequest  createOrderRequest = new CreateOrderRequest();
                createOrderRequest.symbol = symbol;
                createOrderRequest.accountId = String.valueOf(accountList.get(0).id);
                createOrderRequest.amount = entrust.getFcount().toString();
                createOrderRequest.price = entrust.getFprize().toString();

                if (entrust.getFtype() == EntrustTypeEnum.BUY.getCode()) {
                    createOrderRequest.type = CreateOrderRequest.OrderType.BUY_MARKET;
                }else {
                    createOrderRequest.type = CreateOrderRequest.OrderType.SELL_MARKET;
                }
                HBOrderID = HBApiImpl.getInstance().createOrder(createOrderRequest);
            }
        }catch (ApiException e){
            e.printStackTrace();
            throw new Exception("create order api failure");
        }
        //火币的订单ID
        entrust.setFhuobientrustid(new BigInteger(String.valueOf(HBOrderID)));
         **/


        UserWalletBalanceLog userWalletBalanceLog = new UserWalletBalanceLog();
        int trytimes = 0;
        BigDecimal oldTotalvalue = null;
        BigDecimal oldFrozenvalue = null;
        while (true ) {
            UserCoinWallet wallet = null;
            BigDecimal frozen = null;

            if (entrust.getFtype().equals(EntrustTypeEnum.BUY.getCode())) {
                wallet = userCoinWalletMapper.select(entrust.getFuid(), entrust.getFbuycoinid());
                if (wallet == null) {
                    throw new Exception("wallet is null");
                }
                if (wallet.getTotal().compareTo(entrust.getFamount()) < 0) {
                    throw new Exception("wallet total balance");
                }
                frozen = entrust.getFamount();
                oldTotalvalue = wallet.getTotal();
                oldFrozenvalue = wallet.getFrozen();

            } else if (entrust.getFtype().equals(EntrustTypeEnum.SELL.getCode())) {
                wallet = userCoinWalletMapper.select(entrust.getFuid(), entrust.getFsellcoinid());
                if (wallet == null) {
                    throw new Exception("wallet is null");
                }
                if (wallet.getTotal().compareTo(entrust.getFcount()) < 0) {
                    throw new Exception("wallet total balance");
                }
                frozen = entrust.getFcount();
                oldTotalvalue = wallet.getTotal();
                oldFrozenvalue = wallet.getFrozen();
            }
            if (wallet == null) {
                throw new Exception("wallet is null");
            }
            if (userCoinWalletMapper.updateFrozenCAS(frozen, new Date(), wallet.getId(),wallet.getUid(), wallet.getVersion()) <= 0) {
                trytimes = trytimes +1;
                if (trytimes == TRY_TIMES) {
                    logger.info("update wallet {} try the {} times! and fail!",wallet.getId(),trytimes);
                    throw new Exception("wallet update failure");
                }

                logger.info("update wallet {} try the {} times! ",wallet.getId(),trytimes);
                Thread.sleep(10);

            } else {
                logger.info("update wallet:{},changevalue:{}",wallet.getCoinId(),frozen);
                userWalletBalanceLog.setCoinId(wallet.getCoinId());
                userWalletBalanceLog.setChange(frozen);
                break;
            }
        }
        if (entrustMapper.insert(entrust) <= 0) {
            throw new Exception("entrust insert failure");
        }



        userWalletBalanceLog.setCreatedatestamp(new Date().getTime());
        userWalletBalanceLog.setCreatetime(new Date());
        userWalletBalanceLog.setDirection(UserWalletBalanceLogDirectionEnum.out.getValue());
        userWalletBalanceLog.setFieldId(UserWalletBalanceLogFieldEnum.total.getValue());
        userWalletBalanceLog.setSrcId(entrust.getFid().intValue());
        userWalletBalanceLog.setSrcType(UserWalletBalanceLogTypeEnum.Place_order.getCode());
        userWalletBalanceLog.setUid(entrust.getFuid());
        userWalletBalanceLog.setOldvalue(oldTotalvalue);

        logger.info("userwalletbalancelog_1:" + JSON.toJSONString(userWalletBalanceLog));
        //userWalletBalanceLogMapper.insert(userWalletBalanceLog);
        userWalletBalanceLog.setDirection(UserWalletBalanceLogDirectionEnum.in.getValue());
        userWalletBalanceLog.setFieldId(UserWalletBalanceLogFieldEnum.frozen.getValue());
        userWalletBalanceLog.setOldvalue(oldFrozenvalue);
        logger.info("userwalletbalancelog_2:" + JSON.toJSONString(userWalletBalanceLog));
        //userWalletBalanceLogMapper.insert(userWalletBalanceLog);


        // send MQ
        MQEntrustState mqBody = new MQEntrustState();
        mqBody.setTradeId(entrust.getFtradeid());
        mqBody.setBuyID(entrust.getFuid());
        mqBody.setBuyOrderId(entrust.getFid());
        mqBody.setBuyPrize(entrust.getFprize());
        mqBody.setCount(entrust.getFcount());
        mqBody.setStatetime(Utils.getTimestamp().getTime());
        String key;
        if (entrust.getFtype().equals(EntrustTypeEnum.BUY.getCode())) {
            mqBody.setType(EntrustChangeEnum.BUY);
            key = "TRADE_BUY_" + entrust.getFid();
        } else if (entrust.getFtype().equals(EntrustTypeEnum.SELL.getCode())) {
            mqBody.setType(EntrustChangeEnum.SELL);
            key = "TRADE_SELL_" + entrust.getFid();
        } else {
            throw new Exception("entrust type error");
        }

        if (isMemMatchTradeID(entrust.getFtradeid().toString())) {
            //logger.info("tradeid:{} is memMatch......",entrust.getFtradeid());

            //send entrust request to MQ
            MQEntrust mqEntrust = cpEntrust2MqEntrust(entrust);
            //从match端迁移到下单接口中。
            FApiAuth apiAuth = null;
            if (mqEntrust.getFsource() == EntrustSourceEnum.API.getCode()) {
                //检查是否是api的请求
                apiAuth = isApiAuth(entrust.getFuid());
               // logger.info("apiAuth:{}",JSON.toJSONString(apiAuth));
                if (apiAuth == null){
                    throw new Exception("Please check API configuration");
                }
                mqEntrust.setApiopenrate(apiAuth.getFopenrate());
                mqEntrust.setApirate(apiAuth.getFrate().toString());
               // logger.info("getFrate:{}",apiAuth.getFrate());
            }



            logger.info("mqEntrust:{}",JSON.toJSONString(mqEntrust));
            sendEntrustReq(mqEntrust);
        }
//        if (!entrustMQSend.send(MQTopic.ENTRUST_STATE, MQConstant.TAG_ENTRUST_STATE, key, mqBody)) {
//            throw new Exception("mq send failure");
//        }

        return entrust.getFid();
    }
    public boolean isMemMatchTradeID(String tradeid) {
        String matchGroupStr = redisHelper.get(RedisConstant.MATCH_GROUP);
        if (matchGroupStr == null ) {
            logger.info(RedisConstant.MATCH_GROUP + " not exist!");
            return false;
        }
        try {
            Map<String,String> matchgroupMap = JSON.parseObject(matchGroupStr,new TypeReference<Map<String, String>>(){});
            for (Map.Entry<String, String> entry :matchgroupMap.entrySet()) {
                String[] matchGroup = entry.getValue().split(",");
                Set<String> mySet = new HashSet<String>(Arrays.asList(matchGroup));
                logger.info("tradegroup:{},tradeset:{}",entry.getKey(),JSON.toJSONString(mySet));
                if (mySet.contains(tradeid)) {
                    return true;
                }
            }
        }
        catch (Exception e){

            logger.error("反序列化MatchGroup的时候报错："+e.getMessage());
            return false;
        }
        return false;
    }
    public FApiAuth isApiAuth(long fuid){
        RedisObject redisObject = redisHelper.getRedisObject(RedisConstant.IS_AUTH_API_KEY + fuid);
        if (redisObject == null){
            return null;
        }
        try {
            return JSONObject.parseObject(redisObject.getExtObject().toString(),FApiAuth.class);
        }catch (Exception e){
            logger.error("反序列化authApi的时候报错："+e.getMessage());
        }
        return null;
    }
    private void sendEntrustReq(MQEntrust mqEntrust) throws Exception {
        String key = mqEntrust.getFentrustid() + String.valueOf(mqEntrust.getFtype());
//        Schema<MQEntrust> schema = RuntimeSchema.getSchema(MQEntrust.class);
//        // Re-use (manage) this buffer to avoid allocating on every serialization
//        if (buffer == null ) {
//            buffer = LinkedBuffer.allocate(1024);
//        }
//
//        final byte[] protostuff;
//        try
//        {
//            protostuff = ProtostuffIOUtil.toByteArray(mqEntrust, schema, buffer);
//        }
//        finally
//        {
//            buffer.clear();
//        }

        //tagid 为交易对id。
        logger.debug("entrust req tradeid:" + mqEntrust.getFtradeid());
        logger.info("entrustmq topic:{}",MQTopic.ENTRUST_REQ);
//        if (!OrderedMQSend.send(MQTopic.ENTRUST_REQ, String.valueOf(mqEntrust.getFtradeid()), key, mqEntrust,String.valueOf(mqEntrust.getFtradeid()))) {
//            throw new Exception("mq send failure");
//        }
    }
    public MQEntrust cpEntrust2MqEntrust(EntrustDO entrust) {

        MQEntrust mqEntrust = new MQEntrust();
//        Timestamp tm = new Timestamp(entrust.getFcreatetime().getTime());
        mqEntrust.setCreatedate(entrust.getFcreatetime().getTime());
        mqEntrust.setFentrustid(entrust.getFid().toString());
        mqEntrust.setFreefee(false);
        mqEntrust.setFsource(entrust.getFsource());
        mqEntrust.setFstatus(entrust.getFstatus());
        mqEntrust.setFtradeid(entrust.getFtradeid());
        mqEntrust.setFtype(entrust.getFtype());
        mqEntrust.setFuid(entrust.getFuid());
        mqEntrust.setLeftcount(entrust.getFleftcount().toString());
        BigDecimal Prize = entrust.getFprize().setScale(10);
        mqEntrust.setPrize(Prize.toString());
        mqEntrust.setRecover(0);
        logger.debug("mqEntrust:" + mqEntrust.toString());
        mqEntrust.setSize(entrust.getFcount().toString());
        return mqEntrust;
    }

    /**
     * 撤单
     */

    public Boolean cancleEntrust(EntrustDO entrust) throws Exception {
//send entrust request to MQ
        logger.debug("before cancelEntrust.........");
        if (!isMemMatchTradeID(entrust.getFtradeid().toString())) {
            int result = entrustMapper.updateByfIdCAS(entrust);
            if (result == 0){
                return false;
            }
        }
        else {

            logger.info("cancel entrustid: " + entrust.getFid() + "tradeid: " + entrust.getFtradeid());
            //todo   增加 tag 表示具体交易对
            MQEntrust mqEntrust = cpEntrust2MqEntrust(entrust);
            mqEntrust.setFtype(EntrustTypeEnum.CANCEL.getCode());
            //撤单请求的创建时间取订单中的最后更新时间字段。
            mqEntrust.setCreatedate(entrust.getFlastupdattime().getTime());
            sendEntrustReq(mqEntrust);
            logger.debug("mqEntrust:{}",JSON.toJSONString(mqEntrust));
        }

        return true;


    }

    /**
     * 批量撤单
     * @param entrust
     * @return
     */
    @Transactional(value="flexibleTransMgr", isolation = Isolation.REPEATABLE_READ, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public Boolean cancelEntrustBatch(EntrustDO entrust) throws Exception {
        logger.debug("begin batch cancelEntrust.........");
//        if (entrustMapper.batchUpdate(entrust) == 0) {
//            logger.error("update f_entrust by ids"+ entrust.getIds() + "fail!" );
//            return false;
//        }


        MQEntrust mqEntrust = new MQEntrust();
        BigDecimal price = null;
        for (String id : entrust.getIds()) {
            mqEntrust.setFstatus(entrust.getFstatus());
            mqEntrust.setFtype(entrust.getFtype());
            mqEntrust.setFentrustid(id);
            mqEntrust.setFtype(EntrustTypeEnum.CANCEL.getCode());
            mqEntrust.setRecover(0);
            //todo   将tradeid更改为前端传递。临时从后台查询
            EntrustDO fentrust = entrustMapper.selectByFid(BigInteger.valueOf(Long.parseLong(id)));
            mqEntrust.setFtradeid(fentrust.getFtradeid());
            price= fentrust.getFprize().setScale(10);
            mqEntrust.setPrize(price.toString());
            sendEntrustReq(mqEntrust);
        }
        return true;
    }
    
    public void initMarket() {
    	//查询初始化数据
    	List<InitMarket> marketList = initMarketMapper.selectInitMarket();
    	for (InitMarket initMarket : marketList) {
    		EntrustDO entrust = new EntrustDO();
    		entrust.setFuid(initMarket.getFuid());
    	    entrust.setFtradeid(initMarket.getFtradeid());
    	    entrust.setFbuycoinid(initMarket.getFbuycoinid());
    	    entrust.setFsellcoinid(initMarket.getFsellcoinid());
    	    entrust.setFstatus(EntrustStateEnum.Going.getCode());
    	    entrust.setFtype(initMarket.getFtype());
    	    entrust.setFprize(initMarket.getFprice());
    	    entrust.setFcount(initMarket.getFcount());
    	    entrust.setFleftcount(initMarket.getFcount());
    	    entrust.setFamount(MathUtils.mul(initMarket.getFprice(), initMarket.getFcount()));
    	    entrust.setFsuccessamount(BigDecimal.ZERO);
    	    entrust.setFfees(BigDecimal.ZERO);
    	    entrust.setFleftfees(BigDecimal.ZERO);
    	    entrust.setFmatchtype(MatchTypeEnum.LIMITE.getCode());
    	    entrust.setFsource(EntrustSourceEnum.WEB.getCode());
    	    entrust.setFlastupdattime(Utils.getTimestamp());
    	    entrust.setFcreatetime(Utils.getTimestamp());
    	    entrust.setFlast(BigDecimal.ZERO);
    	    entrust.setFagentid(0);
    	    // 下单
    	    try {
    	    	BigInteger entrustId = createEntrust(entrust);
    	    	logger.info("委托成功，委单id："+entrustId);
    	    } catch (Exception ex) {
    	    	logger.error("create entrust is error, InitMarket:{}", JSON.toJSONString(initMarket), ex);
    	    }
		}
	}
}
