package com.qkwl.common.coin.driver;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

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


public class NEODriver implements CoinDriver {
	
	private static final Logger logger = LoggerFactory.getLogger(NEODriver.class);

    private CoinUtils coinUtils = null;
    private String passWord = null;
    private Integer coinSort = null;
    private String sendAccount = null;
    private String contractAccount = null;
    private int contractWei = 0;
    private String walletLink = null;
    private String chainLink = null;
    private String shortName = null;
    

    public NEODriver(String walletLink,String chainLink, String pass, Integer coinSort, String sendAccount, String contractAccount, int contractWei,String shortName) {
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
    }

    @Override
    public Integer getCoinSort() {
        return this.coinSort;
    }

    @Override
    public BigInteger getBestHeight() {
    	String postData = "{\"jsonrpc\": \"2.0\", \"id\":\"getblockcount\", \"method\": \"getblockcount\", \"params\":[]}";
    	String result = coinUtils.goPost(chainLink, postData);
    	if(StringUtils.isEmpty(result)) {
         	return null;
        }
    	JSONObject jo = JSON.parseObject(result);
        return jo.getBigInteger("result");
    }

    @Override
    public BigDecimal getBalance() {
    	//如果是主链合约
    	if("NEO".equals(shortName) || "GAS".equals(shortName)) {
    		String postData = "{\"jsonrpc\": \"2.0\", \"id\":\"getaccountstate\", \"method\": \"getaccountstate\", \"params\":[\""+ sendAccount +"\"]}";
        	String result = coinUtils.goPost(chainLink, postData);
        	if(StringUtils.isEmpty(result)) {
             	return null;
             }
             JSONObject jo = JSON.parseObject(result);
             JSONObject jsonObject = jo.getJSONObject("result");
             JSONArray jsonArray = jsonObject.getJSONArray("balances");
             for (int i = 0; i < jsonArray.size(); i++) {
            	 JSONObject balance = jsonArray.getJSONObject(i);
            	 if(contractAccount.equals(balance.getString("asset"))) {
            		 return balance.getBigDecimal("value");
            	 }
			}
            return BigDecimal.ZERO;
    	}
    	
    	//合约
    	String postData = "{\"jsonrpc\": \"2.0\", \"id\":\"invokefunction\", \"method\": \"invokefunction\", \"params\":[\""+ contractAccount +"\",\"balanceOf\",[{\"type\":\"Hash160\",\"value\":\""+ addressToScriptHash(sendAccount) +"\"}]]}";
    	String result = coinUtils.goPost(chainLink, postData);
    	if(StringUtils.isEmpty(result)) {
         	return null;
		}
		JSONObject jo = JSON.parseObject(result);
		JSONObject jsonObject = jo.getJSONObject("result");
		JSONArray jsonArray = jsonObject.getJSONArray("stack");
		JSONObject balance = jsonArray.getJSONObject(0);
		String newStr = balance.getString("value").replaceAll("^(0+)", "");
		BigDecimal hexToBigDecimal = coinUtils.HexToBigDecimal("0x"+newStr, contractWei);
		return hexToBigDecimal;
    }

    
    /**
	 * neo 地址转换为scriptHash
	 * @param address
	 * @return
	 */
	public String addressToScriptHash(String address) {
		String scriptHash = null;
		try {
			String script = Numeric.toHexStringNoPrefix(Base58.newInstance().decode(address));
			if (script.substring(0, 2).equals("17")) {
				scriptHash = script.substring(2, 42);
			}
			
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return scriptHash;
	}
    
	
	/**
	 * neo scriptHash转地址
	 * @param script
	 * @return
	 */
/*	public String scriptToAddress(String script) {
		String address = null;
		try {
			script = "17"+script;
			String checkSum = Numeric.toHexStringNoPrefix(Digest.sha256(Digest.sha256(Numeric.hexStringToByteArray(script))));
			checkSum = checkSum.substring(0, 8);
			script = script +checkSum;
			address = Base58.newInstance().encode(Numeric.hexStringToByteArray(script));
			
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return address;
	}*/
    
    
    @Override
    public FPool getNewAddress(String uId) {
    	if(!"NEO".equals(shortName)){
    		return null;
    	}
    	String postData = "{\"jsonrpc\": \"2.0\", \"id\":\"getnewaddress\", \"method\": \"getnewaddress\", \"params\":[]}";
    	String result = coinUtils.goPost(walletLink, postData);
    	JSONObject resultJson = JSON.parseObject(result);
        result = resultJson.getString("result");
        if (StringUtils.isEmpty(result) || result.equals("null")) {
            return null;
        }
        return new FPool(result);
    }

    @Override
    public String sendToAddress(String to, String amount, String nonce) {
        return null;
    }
    
    @Override
    public String sendToAddress(String address, BigDecimal account, String comment, BigDecimal fee) {
    	BigDecimal setScale = account.setScale(contractWei);
    	String postData = "{\"jsonrpc\": \"2.0\", \"id\":\"sendfrom\", \"method\": \"sendfrom\", \"params\":[\""+contractAccount + "\",\"" + sendAccount + "\",\"" + address + "\","  + setScale.toString() + "]}";
    	String result = coinUtils.goPost(walletLink, postData);
    	if(StringUtils.isEmpty(result)) {
         	return null;
        }
    	JSONObject resultJson = JSON.parseObject(result);
    	JSONObject jsonObject = resultJson.getJSONObject("result");
    	if(jsonObject == null || !jsonObject.containsKey("txid")) {
    		return null;
    	}
    	return jsonObject.getString("txid");
    }
    

	@Override
	public List<TxInfo> getBlock(BigInteger blockNum) {
		String postData = "{\"jsonrpc\": \"2.0\", \"id\":\"getblock\", \"method\": \"getblock\", \"params\":["+ blockNum +",1]}";
    	String result = coinUtils.goPost(chainLink, postData);
    	if(StringUtils.isEmpty(result)) {
         	return null;
        }
    	JSONObject resultJson = JSON.parseObject(result);
    	
    	if(!resultJson.containsKey("result")) {
    		logger.error("postdata:"+postData+",result:"+result);
    		return null;
    	}
    	
    	JSONObject jsonObject = resultJson.getJSONObject("result");
    	
    	if(!jsonObject.containsKey("tx")) {
    		logger.error("postdata:"+postData+",result:"+result);
    		return null;
    	}
    	
    	JSONArray jsonArray = jsonObject.getJSONArray("tx");
    	List<TxInfo> txList = new ArrayList<>();
    	for (int i = 0; i < jsonArray.size(); i++) {
    		JSONObject jsonObject2 = jsonArray.getJSONObject(i);
    		String txid = jsonObject2.getString("txid");
    		if(StringUtils.isEmpty(txid)){
				continue;
			}
    		String type = jsonObject2.getString("type");
    		if("ContractTransaction".equals(type) || "InvocationTransaction".equals(type)) {//合约交易 NEO GAS
    			JSONArray voutList = jsonObject2.getJSONArray("vout");
    			for (int j = 0; j < voutList.size(); j++) {
    				JSONObject txjsonObject = voutList.getJSONObject(j);
    				TxInfo tx  = new TxInfo();
    				tx.setTxid(txid);
    				tx.setVout(txjsonObject.getInteger("n"));
    				tx.setAmount(txjsonObject.getBigDecimal("value"));
    				tx.setAddress(txjsonObject.getString("address"));
    				tx.setContract(txjsonObject.getString("asset"));
    				txList.add(tx);
				}
    		}
		}
    	return txList;
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
        return true;
    }

    @Override
    public boolean walletPassPhrase(int times) {
        return true;
    }

    @Override
    public boolean setTxFee(BigDecimal fee) {
        return true;
    }

    @Override
    public List<TxInfo> listTransactions(int count, int from) {
        return null;
    }

    @Override
    public TxInfo getTransaction(String txId) {
    	
    	String[] split = txId.split("_");
    	
    	String postData = "{\"jsonrpc\": \"2.0\", \"id\":\"getrawtransaction\", \"method\": \"getrawtransaction\", \"params\":[\""+ split[0] +"\",1]}";
    	String result = coinUtils.goPost(chainLink, postData);
    	if(StringUtils.isEmpty(result)) {
         	return null;
        }
    	JSONObject resultJson = JSON.parseObject(result);
    	JSONObject jsonObject = resultJson.getJSONObject("result");
    	Integer confirmations = jsonObject.getInteger("confirmations");
    	
    	TxInfo tx = new TxInfo();
    	tx.setTxid(txId);
    	tx.setConfirmations(confirmations);
    	
    	JSONArray jsonArray = jsonObject.getJSONArray("vout");
    	for (int i = 0; i < jsonArray.size(); i++) {
    		JSONObject jsonObject2 = jsonArray.getJSONObject(i);
    		String string = jsonObject2.getString("n");
    		if(!StringUtils.isEmpty(string) && string.equals(split[1])) {
    			tx.setAmount(jsonObject2.getBigDecimal("value"));
    			tx.setAddress(jsonObject2.getString("address"));
    			return tx;
    		}
		}
    	return tx;
    }

    
    @Override
    public String sendToAddress(String address, BigDecimal amount, String comment, BigDecimal fee, String memo) {
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
		//如果是主链合约
    	if("NEO".equals(shortName) || "GAS".equals(shortName)) {
    		String postData = "{\"jsonrpc\": \"2.0\", \"id\":\"getaccountstate\", \"method\": \"getaccountstate\", \"params\":[\""+ address +"\"]}";
        	String result = coinUtils.goPost(chainLink, postData);
        	if(StringUtils.isEmpty(result)) {
             	return null;
             }
             JSONObject jo = JSON.parseObject(result);
             JSONObject jsonObject = jo.getJSONObject("result");
             JSONArray jsonArray = jsonObject.getJSONArray("balances");
             for (int i = 0; i < jsonArray.size(); i++) {
            	 JSONObject balance = jsonArray.getJSONObject(i);
            	 if(contractAccount.equals(balance.getString("asset"))) {
            		 return balance.getBigDecimal("value");
            	 }
			}
    	}
    	
    	//合约
    	String postData = "{\"jsonrpc\": \"2.0\", \"id\":\"invokefunction\", \"method\": \"invokefunction\", \"params\":[\""+ contractAccount +"\",\"balanceOf\",[{\"type\":\"Hash160\",\"value\":\""+ addressToScriptHash(address) +"\"}]]}";
    	String result = coinUtils.goPost(chainLink, postData);
    	if(StringUtils.isEmpty(result)) {
         	return null;
         }
         JSONObject jo = JSON.parseObject(result);
         JSONObject jsonObject = jo.getJSONObject("result");
         JSONArray jsonArray = jsonObject.getJSONArray("stack");
         for (int i = 0; i < jsonArray.size(); i++) {
        	 JSONObject balance = jsonArray.getJSONObject(i);
        	 String newStr = balance.getString("value").replaceAll("^(0+)", "");
        	 BigDecimal hexToBigDecimal = coinUtils.HexToBigDecimal("0x"+newStr, contractWei);
        	 return hexToBigDecimal;
		}
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
		BigDecimal setScale = amount.setScale(contractWei);
    	String postData = "{\"jsonrpc\": \"2.0\", \"id\":\"sendfrom\", \"method\": \"sendfrom\", \"params\":[\""+contractAccount + "\",\"" + from + "\",\"" + to + "\","  + setScale.toString() + "]}";
    	String result = coinUtils.goPost(walletLink, postData);
    	if(StringUtils.isEmpty(result)) {
         	return null;
        }
    	JSONObject resultJson = JSON.parseObject(result);
    	JSONObject jsonObject = resultJson.getJSONObject("result");
    	if(jsonObject == null || !jsonObject.containsKey("txid")) {
    		return null;
    	}
    	return jsonObject.getString("txid");
	}

    
}
