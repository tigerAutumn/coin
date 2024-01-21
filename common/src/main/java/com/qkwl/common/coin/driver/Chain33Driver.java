package com.qkwl.common.coin.driver;

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

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Chain33Driver
 *
 * @author hwj
 */
public class Chain33Driver implements CoinDriver {
	
	private static final Logger logger = LoggerFactory.getLogger(Chain33Driver.class);

    private CoinUtils coinUtils = null;
    private String passWord = null;
    private Integer coinSort = null;
    private String shortName = null;
    private String walletLink = null;
    private String chainLink = null;
    private String sendAccount = null;
    private String contractAccount = null;
    private BigDecimal wei = null;
    private Random random = null;

    public Chain33Driver(String walletLink,String chainLink ,String pass, Integer coinSort,String sendAccount, String contractAccount ,String shortName, int contractWei) {
        coinUtils = new CoinUtils(walletLink, chainLink);
        this.walletLink = walletLink;
        this.chainLink = chainLink;
        this.passWord = pass;
        this.coinSort = coinSort;
        this.shortName = shortName;
        this.sendAccount = sendAccount;
        this.contractAccount = contractAccount;
        this.wei = BigDecimal.valueOf(Math.pow(10L, contractWei));
        this.random = new Random(1);
        
    }

    @Override
    public BigDecimal getBalance() {
    	/*if(StringUtils.isEmpty(contractAccount)) {
	    	String goPost = coinUtils.goPost(walletLink, "{ \"jsonrpc\":\"2.0\", \"id\":1,\"method\":\"Chain33.GetBalance\",\"params\":[{\"addresses\":[\""+sendAccount+"\"],\"execer\":\"coins\"}]}");
	        if(StringUtils.isEmpty(goPost)) {
	        	return null;
	        }
	        JSONObject resultJson = JSON.parseObject(goPost);
	        JSONArray jsonArray = resultJson.getJSONArray("result");
	        if(jsonArray.size() == 0) {
	        	return null;
	        }
	        JSONObject jsonObject = jsonArray.getJSONObject(0);
	        return MathUtils.div(jsonObject.getBigDecimal("balance"),wei);
    	}else{*/
    		String goPost = coinUtils.goPost(chainLink, "{\"id\":1,\"jsonrpc\":\"2.0\",\"method\":\"token.GetTokenBalance\",\"params\":[{\"addresses\":[\""+sendAccount+"\"],\"execer\":\"user.p."+contractAccount+".token\",\"tokenSymbol\":\""+shortName+"\"}]}");
	        if(StringUtils.isEmpty(goPost)) {
	        	return null;
	        }
	        JSONObject resultJson = JSON.parseObject(goPost);
	        JSONArray jsonArray = resultJson.getJSONArray("result");
	        if(jsonArray.size() == 0) {
	        	return null;
	        }
	        JSONObject jsonObject = jsonArray.getJSONObject(0);
	        return MathUtils.div(jsonObject.getBigDecimal("balance"),wei);
    	//}
    }

    @Override
    public FPool getNewAddress(String time) {
    	walletPassPhrase(30);
        String goPost = coinUtils.goPost(chainLink, "{ \"jsonrpc\":\"2.0\", \"id\":1,\"method\":\"Chain33.NewAccount\",\"params\":[{\"label\":\"" + time + random.nextInt(1000) + "\"}]}");
        if(StringUtils.isEmpty(goPost)) {
        	return null;
        }
        JSONObject parseObject = JSON.parseObject(goPost);
        JSONObject result = parseObject.getJSONObject("result");
        if(result == null || !result.containsKey("acc")) {
        	return null;
        }
        return new FPool(result.getJSONObject("acc").getString("addr"));
    }

    @Override
    public boolean walletLock() {
        coinUtils.goPost(chainLink, "{ \"jsonrpc\":\"2.0\", \"id\":1,\"method\":\"Chain33.Lock\",\"params\":[]}");
        return true;
    }

    @Override
    public boolean walletPassPhrase(int times) {
        if (passWord == null || passWord.length() <= 0) {
            return false;
        }
        String goPost = coinUtils.goPost(chainLink, "{ \"jsonrpc\":\"2.0\", \"id\":1,\"method\":\"Chain33.UnLock\",\"params\":[{\"passwd\":\""+passWord+"\",\"walletorticket\":false,\"timeout\":30}]}");
        if(StringUtils.isEmpty(goPost)) {
        	return false;
        }
        JSONObject parseObject = JSON.parseObject(goPost);
        Boolean isOk = parseObject.getJSONObject("result").getBoolean("isOK");
        if(isOk != null && isOk) {
        	return true;
        }
        return false;
    }

    @Override
    public boolean setTxFee(BigDecimal fee) {
        return true;
    }

    @Override
    public List<TxInfo> listTransactions(int count, int from) {
        return null;
    }

    //仅仅做确认数判断
    @Override
    public TxInfo getTransaction(String txId) {
    	String goPost = coinUtils.goPost(chainLink, "{ \"jsonrpc\":\"2.0\", \"id\":1,\"method\":\"Chain33.QueryTransaction\",\"params\":[{\"hash\":\"" + txId + "\"}]}");
    	if(StringUtils.isEmpty(goPost)) {
    		return null;
    	}
    	JSONObject result = JSON.parseObject(goPost).getJSONObject("result");
    	if(result == null) {
    		return null;
    	}
    	BigInteger height = result.getBigInteger("height");
    	BigInteger bestHeight = getBestHeight();
    	TxInfo tx = new TxInfo();
    	tx.setConfirmations(bestHeight.subtract(height).intValue());
        return tx;
    }
    
    @Override
	public ReturnResult sendToAddress(String to, BigDecimal amount,BigDecimal fee, String nonce, String gasPrice, String gas ,String memo){
    	amount = MathUtils.mul(amount, wei).setScale(0);
/*    	if(StringUtils.isEmpty(contractAccount)) {
	        if(!walletPassPhrase(30)) {
	        	return ReturnResult.FAILUER(403,"密码错误");
	        }
	        String goPost = coinUtils.goPost(walletLink, "{ \"jsonrpc\":\"2.0\", \"id\":1,\"method\":\"Chain33.SendToAddress\",\"params\":[{\"from\":\""+sendAccount+"\",\"to\":\""+to+"\",\"amount\":"+amount.toString()+",\"note\":\""+memo+"\"}]}");
	        walletLock();
	        logger.info("BTC.sendToAddress resultJson:"+goPost);
	        JSONObject parseObject = JSON.parseObject(goPost);
	        JSONObject jsonObject = parseObject.getJSONObject("result");
	        if(jsonObject == null || !jsonObject.containsKey("hash")) {
	        	return null;
	        }
	        String result = jsonObject.getString("hash");
	        if (StringUtils.isEmpty(result)) {
	            return null;
	        }
	        return ReturnResult.SUCCESS(200 ,result);
    	}else {*/
    		String requestData = "{ \"jsonrpc\":\"2.0\", \"id\":1,\"method\":\"Chain33.CreateRawTransaction\",\"params\":[{\"to\":\""+to+"\",\"amount\":"+amount.toString()+",\"fee\":100000,\"isToken\":true,\"tokenSymbol\":\""+shortName+"\",\"execName\":\"\"}]}";
    		String goPost = coinUtils.goPost(chainLink,requestData);
    		if(StringUtils.isEmpty(goPost)) {
        		return ReturnResult.FAILUER(409,"构造交易失败");
        	}
    		JSONObject parseObject = JSON.parseObject(goPost);
    		String txhex = parseObject.getString("result");
    		if(StringUtils.isEmpty(txhex)) {
    			logger.error("构造交易失败request：{}，return：{}",requestData,goPost);
        		return ReturnResult.FAILUER(409,"构造交易失败");
        	}
    		if(!walletPassPhrase(30)) {
 	        	return ReturnResult.FAILUER(403,"密码错误");
 	        }
    		requestData = "{ \"jsonrpc\":\"2.0\", \"id\":1,\"method\":\"Chain33.SignRawTx\",\"params\":[{\"addr\":\""+sendAccount+"\",\"txHex\":\""+txhex+"\",\"expire\":\"30m\"}]}";
 	        goPost = coinUtils.goPost(chainLink, requestData);
 	        if(StringUtils.isEmpty(goPost)) {
 	        	return ReturnResult.FAILUER(409,"签名交易失败");
	       	}
	   		parseObject = JSON.parseObject(goPost);
	   		String signTx = parseObject.getString("result");
	   		if(StringUtils.isEmpty(signTx)) {
	   			logger.error("签名交易失败request：{}，return：{}",requestData,goPost);
	       		return ReturnResult.FAILUER(409,"签名交易失败");
	       	}
 	        walletLock();
 	        //signTx = signTx.replace("{", "").replace("}", "");
 	        requestData = "{ \"jsonrpc\":\"2.0\", \"id\":1,\"method\":\"Chain33.SendTransaction\",\"params\":[{\"data\":\""+signTx+"\"}]}";
 	        goPost = coinUtils.goPost(chainLink, requestData);
 	        if(StringUtils.isEmpty(goPost)) {
       		   return ReturnResult.FAILUER(500,"广播交易失败，请到区块浏览器上确认交易是否上链");
 	        }
 	        parseObject = JSON.parseObject(goPost);
 	        String txhash = parseObject.getString("result");
	   		if(StringUtils.isEmpty(txhash)) {
	   			logger.error("广播交易失败request：{}，return：{}",requestData,goPost);
	       		return ReturnResult.FAILUER(409,"签名交易失败");
	       	}
 	        return ReturnResult.SUCCESS(200 ,txhash);
    	/*}*/
	}
    
    @Override
	public ReturnResult sendToAddress(String from, String to, BigDecimal amount, String comment, BigDecimal fee,
			Integer propertyid,BigDecimal satoshiPerByte) {
		return null;
	}

    @Override
    public String sendToAddress(String to, BigDecimal amount, String comment, BigDecimal fee) {
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
    public BigInteger getBestHeight() { 
    	String goPost = null;
    	/*if(StringUtils.isEmpty(contractAccount)) {
    		goPost =  coinUtils.goPost(walletLink, "{ \"jsonrpc\":\"2.0\", \"id\":1,\"method\":\"Chain33.GetLastHeader\",\"params\":[]}");
    	}else{*/
    		goPost =  coinUtils.goPost(chainLink, "{ \"jsonrpc\":\"2.0\", \"id\":1,\"method\":\"Chain33.GetLastHeader\",\"params\":[]}");
    	/*}*/
    	if(StringUtils.isEmpty(goPost)) {
    		return null;
    	}
    	JSONObject parseObject = JSON.parseObject(goPost);
    	JSONObject result = parseObject.getJSONObject("result");
    	if(result == null) {
    		return null;
    	}
    	return result.getBigInteger("height"); 
    }

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
	public String sendToAddress(String from, String to, BigDecimal amount, String comment, BigDecimal fee) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public JSONArray listaddress() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String registaddress(String addr,BigDecimal fee) {
		// TODO Auto-generated method stub
		return null;
	}

	

	@Override
	public BigDecimal getBalance(String address) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public BigDecimal estimatesmartfee(Integer time) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<TxInfo> listTransactionsByAddress(int pageSize, int page, String address) {
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
		String goPost = coinUtils.goPost(chainLink, "{ \"jsonrpc\":\"2.0\", \"id\":1,\"method\":\"Chain33.GetBlocks\",\"params\":[{\"start\":"+blockNum+",\"end\":"+blockNum+",\"isDetail\":true}]}");
		if(StringUtils.isEmpty(goPost)) {
			return null;
		}
		
		String execerTmp = "user.p." + contractAccount + ".token";
		
		JSONObject parseObject = JSON.parseObject(goPost);
		JSONObject result = parseObject.getJSONObject("result");
		if(result == null || !result.containsKey("items")) {
			return null;
		}
		JSONArray items = result.getJSONArray("items");
		List<TxInfo> txinfos = new ArrayList<>();
		for (int i = 0; i < items.size(); i++) {
			JSONObject jsonObject = items.getJSONObject(i);
			JSONArray txs = jsonObject.getJSONObject("block").getJSONArray("txs");
			for (int j = 0; j < txs.size(); j++) {
				JSONObject tx = txs.getJSONObject(j);
				String execer = tx.getString("execer");
				if(StringUtils.isEmpty(execer)) {
					continue;
				}
				if(!execerTmp.equals(execer)) {
					continue;
				}
				JSONObject transfer = tx.getJSONObject("payload").getJSONObject("transfer");
				if(transfer == null) {
					continue;
				}
				BigDecimal amount = MathUtils.div(transfer.getBigDecimal("amount"),wei);
				if(BigDecimal.ZERO.compareTo(amount) >= 0) {
					continue;
				}
				String txhash = tx.getString("hash");
				//String to = tx.getString("to");
				//可能是token判断
				//String cointoken = transfer.getString("cointoken");
				String to = transfer.getString("to");
				
				if(sendAccount.equals(to)) {
					continue;
				}
				TxInfo txinfo = new TxInfo();
				txinfo.setAddress(to);
				txinfo.setAmount(amount);
				txinfo.setBlockNumber(blockNum);
				txinfo.setTxid(txhash);
				txinfos.add(txinfo);
			}
			
		}
		return txinfos;
	}
}
