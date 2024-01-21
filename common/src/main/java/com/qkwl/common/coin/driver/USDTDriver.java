package com.qkwl.common.coin.driver;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.qkwl.common.coin.CoinDriver;
import com.qkwl.common.coin.CoinUtils;
import com.qkwl.common.coin.TxInfo;
import com.qkwl.common.dto.coin.FPool;
import com.qkwl.common.exceptions.BCException;
import com.qkwl.common.match.MathUtils;
import com.qkwl.common.util.ReturnResult;

/**
 * WICCDriver
 *
 * @author hwj
 */
public class USDTDriver implements CoinDriver {
	
	private static final Logger logger = LoggerFactory.getLogger(USDTDriver.class);

    private CoinUtils coinUtils = null;
    private String passWord = null;
    private Integer coinSort = null;
    private String sendAccount = null;
    

    public USDTDriver(String accessKey, String secretKey, String walletLink, String chainLink, String pass, Integer coinSort,String sendAccount) {
        coinUtils = new CoinUtils(accessKey, secretKey, walletLink, chainLink);
        this.passWord = pass;
        this.coinSort = coinSort;
        this.sendAccount = sendAccount;
    }

    @Override
    public BigDecimal getBalance() {
        JSONObject resultJson = coinUtils.goUSDT("omni_getbalance", "[\""+ sendAccount +"\",31]");
        if(resultJson == null || !resultJson.containsKey("result") ) {
        	return null;
        }
        if(!resultJson.containsKey("result")) {
        	logger.error("USDTDriver.getBalance 错误"+resultJson.toString());
        	return null;
        }
        JSONObject object = (JSONObject) resultJson.get("result");
        if(object == null || !object.containsKey("balance")) {
        	logger.error("USDTDriver.getBalance 错误"+resultJson.toString());
        	return null;
        }
        String result = object.getString("balance");
        return new BigDecimal(result);
    }
    
    
    @Override
	public BigDecimal getBalance(String address) {
    	JSONObject resultJson = coinUtils.goUSDT("omni_getbalance", "[\""+ address +"\",31]");
        if(resultJson == null || !resultJson.containsKey("result") ) {
        	return null;
        }
        if(!resultJson.containsKey("result")) {
        	logger.error("USDTDriver.getBalance 错误"+resultJson.toString());
        	return null;
        }
        JSONObject object = (JSONObject) resultJson.get("result");
        if(object == null || !object.containsKey("balance")) {
        	logger.error("USDTDriver.getBalance 错误"+resultJson.toString());
        	return null;
        }
        String result = object.get("balance").toString();
        return new BigDecimal(result);
	}


    @Override
    public FPool getNewAddress(String time) {
        JSONObject resultJson = coinUtils.goUSDT("getnewaddress", "[\"" + time + "\"]");
        String result = resultJson.getString("result");
        if (StringUtils.isEmpty(result) || result.equals("null")) {
            return null;
        }
        return new FPool(result);
    }

    @Override
    public boolean walletLock() {
        if (passWord == null || passWord.length() <= 0) {
            return false;
        }
        coinUtils.goUSDT("walletlock", "[]");
        return true;
    }

    @Override
    public boolean walletPassPhrase(int times) {
        if (passWord == null || passWord.length() <= 0) {
            return false;
        }
        coinUtils.goUSDT("walletpassphrase", "[\"" + passWord + "\"," + times + "]");
        return true;
    }

    @Override
    public boolean setTxFee(BigDecimal fee) {
        JSONObject resultJson = coinUtils.goUSDT("settxfee", "[" + MathUtils.decimalFormat(fee) + "]");
        String result = resultJson.getString("result");
        if (StringUtils.isEmpty(result) || result.equals("null")) {
            return false;
        }
        return true;
    }
    
    

    @Override
    public List<TxInfo> listTransactions(int count, int from) {
    	int i = 0;
    	JSONObject resultJson = null;
    	if( from < 500) {
    		i = count + from;
    		resultJson = coinUtils.goUSDT("omni_listtransactions", "[\"*\"," + i + "]");
    	}else {
    		resultJson = coinUtils.goUSDT("omni_listtransactions", "[\"*\"," + count + "," + from + "]");
    	}
        String result = resultJson.getString("result");
        if (StringUtils.isEmpty(result)) {
            return null;
        }
        List<TxInfo> txInfos = new ArrayList<TxInfo>();
        JSONArray jsonArray = JSONArray.parseArray(result);
        
        /*for(int j = from ; j < jsonArray.size() ; j++) {
        	JSONObject txObject = JSON.parseObject(jsonArray.getString(j));*/

        for (Object object : jsonArray) {
        	JSONObject txObject = JSON.parseObject(object.toString());
            
            if(txObject.getInteger("propertyid") != 31) {
            	continue;
            }
            String referenceaddress = txObject.getString("referenceaddress");
            if(referenceaddress.equals(sendAccount) || txObject.getString("sendingaddress").equals(sendAccount)) {
            	continue;
            }
            if(!txObject.containsKey("valid")) {
            	continue;
            }
            Boolean valid = txObject.getBoolean("valid");
            if(valid == null || !valid) {
            	continue;
            }
            TxInfo txInfo = new TxInfo();
            txInfo.setTxid(txObject.get("txid").toString());
            txInfo.setAddress(referenceaddress);
            txInfo.setAmount(new BigDecimal(txObject.get("amount").toString()));
            txInfo.setCategory("receive");
            if(txObject.containsKey("blocktime")) {
                long time = Long.parseLong(txObject.get("blocktime").toString() + "000");
                txInfo.setTime(new Date(time));
            }
            if (txObject.get("confirmations") != null && txObject.get("confirmations").toString().trim().length() > 0) {
                txInfo.setConfirmations(Integer.parseInt(txObject.get("confirmations").toString()));
            } else {
                txInfo.setConfirmations(0);
            }
            txInfos.add(txInfo);
        }
        Collections.reverse(txInfos);
        return txInfos;
    }

    
	/**
	 * 获取交易详情 
	 * @param txId
	 * @return
	 */
    @Override
    public TxInfo getTransaction(String txId) {
        JSONObject json = coinUtils.goUSDT("omni_gettransaction", "[\"" + txId + "\"]");
        String result = json.getString("result");
        if (StringUtils.isEmpty(result)) {
            return null;
        }
        JSONObject resultJson = json.getJSONObject("result");
        TxInfo txInfo = new TxInfo();
        txInfo.setAddress(resultJson.get("referenceaddress").toString());
        txInfo.setAmount(new BigDecimal(resultJson.get("amount").toString()));
        if (resultJson.get("confirmations") != null && resultJson.get("confirmations").toString().trim().length() > 0) {
            txInfo.setConfirmations(Integer.parseInt(resultJson.get("confirmations").toString()));
        } else {
            txInfo.setConfirmations(0);
        }
        
        if(resultJson.containsKey("blocktime")) {
            long time = Long.parseLong(resultJson.get("blocktime").toString() + "000");
            txInfo.setTime(new Date(time));
        }
        txInfo.setTxid(resultJson.get("txid").toString());
        return txInfo;
    }

    @Override
    public String sendToAddress(String to, BigDecimal amount, String comment, BigDecimal fee) {
        JSONObject resultJson = null;
        amount = amount.setScale(4, BigDecimal.ROUND_HALF_UP);
        resultJson = coinUtils.goUSDT("omni_send", "[\""+sendAccount+"\",\"" + to + "\",31,\"" + amount.toString() + "\"]");
        logger.info("USDT.sendToAddress resultJson:"+resultJson.toString());
       /* if (fee.compareTo(BigDecimal.ZERO) == 1 && !setTxFee(BigDecimal.valueOf(0.0001))) {
            // 设置失败也无所谓
        }*/
        try {
	        if(resultJson.containsKey("result")) {
	        	String resultobject = resultJson.getString("result");
	        	if(StringUtils.isEmpty(resultobject) || "null".equals(resultobject)) {
	        		logger.error("USDT.sendToAddress错误{to："+to+",amount:"+ amount +",fee:"+fee+"},返回："+resultJson.toString());
	                return null;
	        	}
	        	return resultobject;
	        }else {
	        	logger.error("USDT.sendToAddress错误{to："+to+",amount:"+ amount +",fee:"+fee+"},返回："+resultJson.toString());
	            return null;
	        }
        } catch (Exception e) {
			logger.error("USDT.sendToAddress异常{to："+to+",amount:"+ amount +",fee:"+fee+"},返回："+resultJson.toString(),e);
			return null;
		}

    }
    
    @Override
	public String sendToAddress(String from, String to, BigDecimal amount, String comment, BigDecimal fee) {
		return null;
	}
    
	@Override
	public ReturnResult sendToAddress(String from, String to, BigDecimal amount, String comment, BigDecimal highestFee,
			Integer propertyid,BigDecimal satoshiPerByte) {
		
		JSONObject resultJson = null;
		String param = null;
		//highestFee = highestFee.setScale(8, BigDecimal.ROUND_HALF_UP);
		try {
			//尝试转账，如果转账成功就直接返回，如果转不成功说明改地址的btc不多
			param = "[\""+from+"\",\"" + to + "\",31,\"" + amount + "\"]";
			resultJson = coinUtils.goUSDT("omni_send", param);
			if(!StringUtils.isEmpty(resultJson.getString("result"))) {
				return ReturnResult.SUCCESS(resultJson.getString("result"));
			}
			
			//如果转账失败就构造交易
			//1.查询两个地址未花费的txhash
			param = "[1,999999,[\""+from+"\"]]";
			//
			resultJson = coinUtils.goUSDT("listunspent", param);
			JSONArray fromTxlist = resultJson.getJSONArray("result");
			if(fromTxlist == null || fromTxlist.size() == 0) {
				logger.error("listunspent:"+param);
				logger.error("listunspent return:"+resultJson);
				return ReturnResult.FAILUER(300, "转出地址查不到可花费的手续费");
			}
			param = "[0,999999,[\""+to+"\"]]";
			//
			resultJson = coinUtils.goUSDT("listunspent", param);
			JSONArray toTxlist = resultJson.getJSONArray("result");
			if(toTxlist == null || toTxlist.size() == 0) {
				logger.error("listunspent:"+param);
				logger.error("listunspent return:"+resultJson);
				return ReturnResult.FAILUER(300, "转入地址查不到可花费的手续费");
			}
			
			//2 构造发送代币类型和代币数量数据（payload）
			param = "[31,\""+amount+"\"]";
			//
			resultJson =  coinUtils.goUSDT("omni_createpayload_simplesend", param);
			String payload =resultJson.getString("result");
			if(StringUtils.isEmpty(payload)) {
				logger.error("omni_createpayload_simplesend:"+param);
				logger.error("omni_createpayload_simplesend returm:"+resultJson);
				return ReturnResult.FAILUER(300, "构造payload失败");
			}
			
			//3,构造交易基本数据（transaction base）
			BigDecimal feeCount = BigDecimal.ZERO;
			JSONArray txList = new JSONArray();
			JSONArray txDetailList = new JSONArray();
			
			//由于from地址btc不多，所以只拿一个txhash做转账，多了反而需要更高手续费
			for (Object object : fromTxlist) {
				JSONObject jo = (JSONObject) object;
				BigDecimal txAmount = jo.getBigDecimal("amount");
				JSONObject j = new JSONObject();
				JSONObject o = new JSONObject();
				String txid = jo.getString("txid");
				String scriptPubKey = jo.getString("scriptPubKey");
				int vout = jo.getIntValue("vout");
				j.put("txid", txid);
				j.put("vout", vout);
				txList.add(j);
				o.put("txid", txid);
				o.put("vout", vout);
				o.put("value", txAmount);
				o.put("scriptPubKey", scriptPubKey);
				txDetailList.add(o);
				break;
			}
			
			//btc交易bytes粗略计算公式  size = inputsNum * 148 + outputsNum * 34 + 10 (+/-) 40
			//输入最少两个，输出最少两个（to地址花了手续费和收到btc，from转出btc），大部分情况下是三个（to地址收到0.00000546，to地址花了手续费，from地址花了btc转账），此处按3个算
			BigDecimal feeNeed = MathUtils.mul(new BigDecimal("0.00000260") ,satoshiPerByte); // ( (1 *148 + 3 * 34 +10) / 100000000 ) * satoshiPerByte
			BigDecimal feePerInput = MathUtils.mul(new BigDecimal("0.00000148"),satoshiPerByte); //( 148 / 100000000 ) * satoshiPerByte
			
			for (Object object : toTxlist) {
				JSONObject jo = (JSONObject) object;
				BigDecimal txAmount = jo.getBigDecimal("amount");
				//如果增加这个tx输入后导致我们需要交更多的手续费那就宁可不要
				if(txAmount.compareTo(feePerInput) <= 0) { 
					continue;
				}
				JSONObject j = new JSONObject();
				JSONObject o = new JSONObject();
				String txid = jo.getString("txid");
				String scriptPubKey = jo.getString("scriptPubKey");
				int vout = jo.getIntValue("vout");
				j.put("txid", txid);
				j.put("vout", vout);
				txList.add(j);
				o.put("txid", txid);
				o.put("vout", vout);
				o.put("value", txAmount);
				o.put("scriptPubKey", scriptPubKey);
				txDetailList.add(o);
				feeCount = MathUtils.add(feeCount, txAmount);
				//每增加一个输入就需要增加更多的手续费
				feeNeed = MathUtils.add(feeNeed, feePerInput);
				if(feeCount.compareTo(feeNeed) > 0) {
					break;
				}
			}
			
			//如果所有的交易手续费都不够支付的话就返回
			if(feeCount.compareTo(feeNeed) < 0) {
				return ReturnResult.FAILUER(300, "from和to的btc不足以支付转账手续费,需要手续费："+feeNeed);
			}
			
			param =  "["+txList.toString()+",{}]";
			logger.warn("createrawtransaction:"+param);
			resultJson = coinUtils.goUSDT("createrawtransaction",param);
			String rawtransaction = resultJson.getString("result");
			if(StringUtils.isEmpty(rawtransaction)) {
				logger.error("createrawtransaction return:"+resultJson);
				return ReturnResult.FAILUER(300, "构造交易错误");
			}
			
			//4 在交易数据中加上omni代币数据
			param =  "[\""+rawtransaction+"\",\""+payload+"\"]";
			resultJson = coinUtils.goUSDT("omni_createrawtx_opreturn",param);
			String opreturn = resultJson.getString("result");
			if(StringUtils.isEmpty(opreturn)) {
				logger.error("omni_createrawtx_opreturn："+param);
				logger.error("omni_createrawtx_opreturn return:"+resultJson);
				return ReturnResult.FAILUER(300, " 在交易数据中加上omni代币数据错误");
			}
			
			//5在交易数据上加上接收地址
			param = "[\""+opreturn+"\",\""+to+"\"]";
			resultJson = coinUtils.goUSDT("omni_createrawtx_reference", param);
			String reference = resultJson.getString("result");
			if(StringUtils.isEmpty(reference)) {
				logger.error("omni_createrawtx_reference:" + param);
				logger.error("omni_createrawtx_reference return:"+resultJson);
				return ReturnResult.FAILUER(300, "交易数据上加上接收地址错误");
			}
			
			//6 在交易数据上指定矿工费用
			feeNeed = feeNeed.setScale(8, BigDecimal.ROUND_HALF_UP);
			param = "[\""+reference+"\","+txDetailList.toString()+",\""+ to +"\","+feeNeed.toString()+"]";
			logger.warn("omni_createrawtx_change:"+param);
			resultJson = coinUtils.goUSDT("omni_createrawtx_change", param);
			String change = resultJson.getString("result");
			if(StringUtils.isEmpty(change)) {
				logger.error("omni_createrawtx_change return:"+resultJson);
				return ReturnResult.FAILUER(300, "交易数据上指定矿工费用错误");
			}
			//7 签名
			param = "[\""+change+"\"]";
			resultJson = coinUtils.goUSDT("signrawtransaction", param);
			JSONObject sign = resultJson.getJSONObject("result");
			if(sign == null || !sign.containsKey("hex") || !sign.getBoolean("complete")) {
				logger.error("signrawtransaction:"+param);
				logger.error("signrawtransaction return"+resultJson);
				return ReturnResult.FAILUER(300, "签名错误");
			}
			
			//8 广播
			param = "[\""+sign.getString("hex")+"\"]";
			resultJson = coinUtils.goUSDT("sendrawtransaction", param);
			String transaction = resultJson.getString("result");
			if(StringUtils.isEmpty(transaction)) {
				logger.error("sendrawtransaction:"+param);
				logger.error("sendrawtransaction return:"+resultJson);
				return ReturnResult.FAILUER(300, "广播错误");
			}
			return ReturnResult.SUCCESS(transaction);
		} catch (Exception e) {
			logger.error("USDT构造交易异常{to："+to+",amount:"+ amount +",fee:"+highestFee+"}",e);
		}
		return null;
	}
    
	@Override
	public BigDecimal estimatesmartfee(Integer time) {
		JSONObject json = coinUtils.goUSDT("estimatesmartfee", "[" + time + "]");
		if(!json.containsKey("result")) {
			logger.error("USDT.estimatesmartfee错误"+json.toJSONString());
			return null;
		}
		JSONObject jsonObject = json.getJSONObject("result");
		if(jsonObject == null || "null".equals(jsonObject.toString())) {
			logger.error("USDT.estimatesmartfee错误"+json.toJSONString());
			return null;
		}
		if(jsonObject.containsKey("feerate")) {
			return jsonObject.getBigDecimal("feerate");
		}
		return null;
	}

    @Override
    public String sendToAddress(String address, BigDecimal amount, String comment, BigDecimal fee, String memo) {
        return null;
    }

    @Override
    public String sendToAddress(String to, String amount, String nonce) {
        return null;
    }

    @Override
    public String getETCSHA3(String str) {
        return null;
    }

    @Override
    public Integer getCoinSort() {
        return this.coinSort;
    }

    @Override
    public BigInteger getBestHeight() { return null; }

    @Override
    public Integer getTransactionCount() {
        return null;
    }

	@Override
	public String sendToAddressAccelerate(String to, String amount, String nonce,String gasPrice) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getTransactionByHash(String transactionHash) {
		return null;
	}

	

	@Override
	public JSONArray listaddress() {
		return null;
	}

	@Override
	public String registaddress(String addr,BigDecimal fee) {
		return null;
	}
	
	
	public static void main(String[] args) {
		JSONObject jsonObject = new JSONObject();
		System.out.println(jsonObject.getString("result"));
	}

	@Override
	public ReturnResult sendToAddress(String to, BigDecimal amount,BigDecimal fee, String nonce, String gasPrice, String gas,String memo){
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<TxInfo> listTransactionsByAddress(int pageSize, int page,String address) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String sendToAddress(String from, String to, BigDecimal amount, String memo, BigDecimal fee,
			String password) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<TxInfo> getBlock(BigInteger blockNum) {
		// TODO Auto-generated method stub
		return null;
	}



	

	
}
