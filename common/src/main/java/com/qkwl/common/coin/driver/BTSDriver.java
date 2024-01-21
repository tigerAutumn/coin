package com.qkwl.common.coin.driver;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.web3j.utils.Numeric;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.qkwl.common.coin.CoinDriver;
import com.qkwl.common.coin.CoinUtils;
import com.qkwl.common.coin.TxInfo;
import com.qkwl.common.dto.coin.FPool;
import com.qkwl.common.util.ReturnResult;

import io.github.novacrypto.base58.Base58;


public class BTSDriver implements CoinDriver {
	
	private static final Logger logger = LoggerFactory.getLogger(BTSDriver.class);

    private CoinUtils coinUtils = null;
    private String passWord = null;
    private Integer coinSort = null;
    private String sendAccount = null;
    private String contractAccount = null;
    private int contractWei = 0;
    private String walletLink = null;
    private String chainLink = null;
    private String shortName = null;
    private String walletAccount = null;
    
    

    public BTSDriver(String walletLink,String chainLink, String pass, Integer coinSort, String sendAccount, String contractAccount, int contractWei,String shortName,String walletAccount) {
        coinUtils = new CoinUtils(walletLink, chainLink);
        this.passWord = pass;
        this.coinSort = coinSort;
        this.sendAccount = sendAccount;
        this.contractAccount = contractAccount;
        if(contractWei != 0) {
        	this.contractWei = contractWei;
        }
        this.walletLink = walletLink;
        this.chainLink = chainLink;
        this.shortName = shortName;
        this.walletAccount = walletAccount;
    }

    @Override
    public Integer getCoinSort() {
        return this.coinSort;
    }

    @Override
    public BigInteger getBestHeight() {
    	return null;
    }

    @Override
    public BigDecimal getBalance() {
    	String postData = "{\"jsonrpc\": \"2.0\",\"method\": \"call\",\"params\": [0,\"list_account_balances\",[\""+ sendAccount +"\"]],\"id\": 1}";
		String result = coinUtils.goPost(walletLink + "/rpc", postData);
		try {
			JSONObject parseObject = JSON.parseObject(result);
			if(parseObject.containsValue("error") && !StringUtils.isEmpty(parseObject.getString("error"))) {
				logger.error("bts查询钱包异常postdate：" + postData + ",result:" + result);
				return null;
			}
			JSONArray jsonArray = parseObject.getJSONArray("result");
			for (int i=0;i<jsonArray.size();i++) {
				JSONObject jsonObject = jsonArray.getJSONObject(i);
				if(contractAccount.equals(jsonObject.getString("asset_id"))) {
					BigDecimal bigDecimal = jsonObject.getBigDecimal("amount");
					BigDecimal balanceWei = BigDecimal.valueOf(Math.pow(10L,contractWei));
					return bigDecimal.divide(balanceWei);
				}
			}
			return BigDecimal.ZERO;
		} catch (Exception e) {
			logger.error("查询钱包异常",e);
			return null;
		}
    }
    
    @Override
    public FPool getNewAddress(String uId) {
    	return null;
    }

    @Override
    public String sendToAddress(String to, String amount, String nonce) {
        return null;
    }
    
    @Override
    public String sendToAddress(String address, BigDecimal account, String comment, BigDecimal fee) {
    	return null;
    }
    

	@Override
	public List<TxInfo> getBlock(BigInteger blockNum) {
    	return null;
	}

    
	@Override
	public ReturnResult sendToAddress(String to, BigDecimal amount,BigDecimal fee, String nonce, String gasPrice, String gas,String memo) {
		return null;
	}
    
    @Override
    public String sendToAddressAccelerate(String to, String amount, String nonce,String gasPrice) {
        return null;
    }
    
    @Override
    public String getTransactionByHash(String transactionHash) {
        return null;
    }
    
    @Override
    public boolean walletLock() {
    	String postData = "{\"jsonrpc\": \"2.0\",\"method\": \"call\",\"params\": [ 0,\"lock\", []],\"id\": 1}";
    	String result = coinUtils.goPost(walletLink + "/rpc", postData);
        return true;
    }

    @Override
    public boolean walletPassPhrase(int times) {
    	String postData = "{\"jsonrpc\": \"2.0\",\"method\": \"call\",\"params\": [ 0,\"unlock\", [\"" +passWord + "\"]],\"id\": 1}";
    	String result = coinUtils.goPost(walletLink + "/rpc", postData);
    	JSONObject parseObject = JSON.parseObject(result);
    	if(parseObject.containsKey("error") && !StringUtils.isEmpty(parseObject.getString("error"))) {
    		return false;
    	}
        return true;
    }

    @Override
    public boolean setTxFee(BigDecimal fee) {
        return true;
    }

    @Override
    public List<TxInfo> listTransactions(int count, int from) {
    	String postData = "{\"jsonrpc\":\"2.0\",\"method\":\"call\",\"params\":[0,\"get_account_history_by_operations\",[\""+ sendAccount +"\",[0]," + from + "," + count + "]],\"id\":1}";
    	String result = coinUtils.goPost(walletLink + "/rpc", postData);
    	if(StringUtils.isEmpty(result)) {
         	return null;
        }
    	JSONObject resultJson = JSON.parseObject(result);
    	if(resultJson.containsKey("error") && StringUtils.isEmpty(resultJson.getString("error"))) {
    		logger.error("bts查询钱包异常postdate：" + postData + ",result:" + result);
    		return null;
    	}
    	JSONArray jsonArray = resultJson.getJSONObject("result").getJSONArray("details");
    	List<TxInfo> list = new ArrayList<>();
    	BigDecimal balanceWei = BigDecimal.valueOf(Math.pow(10L,contractWei));
    	for (int i = 0; i < jsonArray.size(); i++) {
    		try {
    			JSONObject txobj = jsonArray.getJSONObject(i);
    			String memo = txobj.getString("memo");
				if(StringUtils.isEmpty(memo) || !StringUtils.isNumeric(memo)) {
					continue;
				}
				JSONObject opDetails = txobj.getJSONObject("op");
        		JSONObject op = opDetails.getJSONArray("op").getJSONObject(1);
        		if(!walletAccount.equals(op.getString("to"))) {
        			continue;
        		}
        		JSONObject amountObj = op.getJSONObject("amount");
        		if(!contractAccount.equals(amountObj.getString("asset_id"))) {
        			continue;
        		}
        		
        		
        		BigDecimal amount = amountObj.getBigDecimal("amount").divide(balanceWei);
        		
        		TxInfo tx = new TxInfo();
        		tx.setAddress(sendAccount);
        		tx.setMemo(memo);
        		tx.setTxid(txobj.getString("transaction_id"));
        		tx.setBlockNumber(opDetails.getBigInteger("block_num"));
        		tx.setAmount(amount);
        		tx.setShortName(shortName);
        		list.add(tx);
			} catch (Exception e) {
				logger.error("充值数据解析异常",e);
				continue;
			}
		}
        return list;
    }

    @Override
    public TxInfo getTransaction(String txId) {
    	
    	String[] split = txId.split("_");
    	txId = split[0];
    	Integer blockNum = Integer.valueOf(split[1]);
    	
    	//查询最大不可逆区块
    	String postData = "{\"jsonrpc\": \"2.0\",\"method\": \"call\",\"params\": [ 0,\"get_dynamic_global_properties\", []],\"id\": 1}";
    	String result = coinUtils.goPost(chainLink + "/rpc", postData);
    	if(StringUtils.isEmpty(result)) {
         	return null;
        }
    	JSONObject resultJson = JSON.parseObject(result);
    	if(resultJson.containsKey("error") && StringUtils.isEmpty(resultJson.getString("error"))) {
    		logger.error("查询区块报错   postdate：" + postData + ",result:" + result);
    		return null;
    	}
    	JSONObject jsonObject = resultJson.getJSONObject("result");
		JSONObject globalProperties = jsonObject;
    	Integer headBlockNumber = globalProperties.getInteger("head_block_number");
    	//最大不会滚数
    	Integer lastIrreversibleBlockNum = globalProperties.getInteger("last_irreversible_block_num");
    	
    	//如果没达到最大不会滚数就不处理
    	if(blockNum.compareTo(lastIrreversibleBlockNum) >= 1) {
    		TxInfo txInfo = new TxInfo();
    		txInfo.setConfirmations(0);
    		return txInfo;
    	} 
    	
    	//确认交易整的存在
    	postData = "{\"jsonrpc\": \"2.0\",\"method\": \"call\",\"params\": [ 0,\"get_block\", [" +blockNum + "]],\"id\": 1}";
    	result = coinUtils.goPost(chainLink + "/rpc", postData);
    	if(StringUtils.isEmpty(result)) {
         	return null;
        }
    	resultJson = JSON.parseObject(result);
    	if(resultJson.containsKey("error") && StringUtils.isEmpty(resultJson.getString("error"))) {
    		logger.error("查询区块报错   postdate：" + postData + ",result:" + result);
    		return null;
    	}
    	jsonObject = resultJson.getJSONObject("result");
    	JSONArray jsonArray = jsonObject.getJSONArray("transaction_ids");
    	int i = 0;
    	boolean isExist = false;
    	for (;i<jsonArray.size();i++) {
			if(txId.equals(jsonArray.getString(i))) {
				isExist = true;
				break;
			}
		}
    	if(!isExist) {
    		return null;
    	}
    	TxInfo txInfo = new TxInfo();
		txInfo.setConfirmations(headBlockNumber - blockNum);
		return txInfo;
    }

    
    @Override
    public String sendToAddress(String address, BigDecimal amount, String comment, BigDecimal fee, String memo) {
    	amount = amount.setScale(0);
    	String postData = "{\"jsonrpc\": \"2.0\",\"method\": \"call\",\"params\": [ 0,\"transfer2\", [\"" +sendAccount + "\",\""+address+"\","+amount.toString()+",\""+ shortName +"\",\""+memo+"\",true]],\"id\": 1}";
    	String result = coinUtils.goPost(walletLink + "/rpc", postData);
    	if(StringUtils.isEmpty(result)) {
         	return null;
        }
    	JSONObject resultJson = JSON.parseObject(result);
    	if(resultJson.containsKey("error") && StringUtils.isEmpty(resultJson.getString("error"))) {
    		logger.error("bts转账报错   postdate：" + postData + ",result:" + result);
    		return null;
    	}
    	JSONArray jsonArray = resultJson.getJSONArray("result");
    	for (int i = 0; i < jsonArray.size(); i++) {
    		String string = jsonArray.getString(i);
    		if(string.startsWith("{")) {
    			continue;
    		}
    		return string;
		}
    	return null;
    }

    @Override
    public String getETCSHA3(String str) {
        return null;
    }

    @Override
    public Integer getTransactionCount() {
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
	public ReturnResult sendToAddress(String from, String to, BigDecimal amount, String comment, BigDecimal fee,
			Integer propertyid,BigDecimal satoshiPerByte) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public BigDecimal getBalance(String address) {
		return null;
	}

	@Override
	public BigDecimal estimatesmartfee(Integer time) {
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
		return null;
	}

    
}
