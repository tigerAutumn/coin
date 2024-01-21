package com.qkwl.common.coin.driver;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
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
import com.qkwl.common.util.ReturnResult;

/**
 * BTCDriver
 *
 * @author HWJ
 */
public class RUBYDriver implements CoinDriver {
	
	private static final Logger logger = LoggerFactory.getLogger(RUBYDriver.class);

    private CoinUtils coinUtils = null;
    private String passWord = null;
    private Integer coinSort = null;
    private String accessKey = null;
    private String secretKey = null;
    private String walletLink = null;
    private String chainLink = null;

    public RUBYDriver(String accessKey, String secretKey, String walletLink,String chainLink ,String pass, Integer coinSort) {
        coinUtils = new CoinUtils(accessKey, secretKey, walletLink, chainLink);
        this.passWord = pass;
        this.coinSort = coinSort;
        this.accessKey = accessKey;
        this.secretKey = secretKey;
        this.walletLink = walletLink;
        this.chainLink = chainLink;
    }

    
    
    
    
    @Override
    public BigDecimal getBalance() {
    	String url = "http://" + accessKey + ":" + secretKey + "@" + walletLink;
    	String postData = "{\"jsonrpc\": \"2.0\", \"id\":\"showallbals\", \"method\": \"showallbals\", \"configs\":[]}";
    	String result = coinUtils.goPost(url,postData);
    	if(StringUtils.isEmpty(result)) {
        	return null;
        }
        JSONObject jo = JSON.parseObject(result);
        JSONObject jsonObject = jo.getJSONObject("result");
        JSONArray jsonArray = jsonObject.getJSONArray("total");
        if(jsonArray == null || jsonArray.size() == 0) {
        	return null;
        }
        for (Object object : jsonArray) {
			JSONObject parseObject = JSON.parseObject(object.toString());
			String string = parseObject.getString("assetref");
			if(StringUtils.isEmpty(string)) {
				return parseObject.getBigDecimal("qty");
			}
		}
        return null;
    }

    @Override
    public FPool getNewAddress(String time) {
    	String url = "http://" + accessKey + ":" + secretKey + "@" + walletLink;
    	String postData = "{\"jsonrpc\": \"2.0\", \"id\":\"addnewaddr\", \"method\": \"addnewaddr\", \"configs\":[]}";
    	String result = coinUtils.goPost(url,postData);
    	if(StringUtils.isEmpty(result)) {
        	return null;
        }
        JSONObject resultJson = JSON.parseObject(result);
        String result1 = resultJson.getString("result");
        if (StringUtils.isEmpty(result1)) {
            return null;
        }
        return new FPool(result1);
    }

    @Override
    public boolean walletLock() {
    	String url = "http://" + accessKey + ":" + secretKey + "@" + walletLink;
    	String postData = "{\"jsonrpc\": \"2.0\", \"id\":\"walletlock\", \"method\": \"walletlock\", \"configs\":[]}";
    	String result = coinUtils.goPost(url,postData);
    	if(StringUtils.isEmpty(result)) {
    		return false;
    	}
    	JSONObject parseObject = JSON.parseObject(result);
    	if(!StringUtils.isEmpty(parseObject.getString("error"))) {
    		return false;
    	}
    	
        return true;
    }

    @Override
    public boolean walletPassPhrase(int times) {
    	String url = "http://" + accessKey + ":" + secretKey + "@" + walletLink;
    	String postData = "{\"jsonrpc\": \"2.0\", \"id\":\"walletpass\", \"method\": \"walletpass\", \"configs\":[" + coinUtils.methodParam(passWord,times) + "]}";
    	String result = coinUtils.goPost(url,postData);
    	if(StringUtils.isEmpty(result)) {
    		return false;
    	}
    	JSONObject parseObject = JSON.parseObject(result);
    	String string = parseObject.getString("error");
    	if(!StringUtils.isEmpty(string)) {
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
    	//int i = count + from;
    	String url = "http://" + accessKey + ":" + secretKey + "@" + walletLink;
    	String postData = "{\"jsonrpc\": \"2.0\", \"id\":\"showwalletdeals\", \"method\": \"showwalletdeals\", \"configs\":[" + count + "," + from + "]}";
    	JSONObject parseObject = JSON.parseObject(coinUtils.goPost(url,postData));
        if (parseObject == null || StringUtils.isEmpty(parseObject.toString())) {
            return null;
        }
        List<TxInfo> txInfos = new ArrayList<TxInfo>();
        JSONArray jsonArray = parseObject.getJSONArray("result");
        if(jsonArray == null || jsonArray.size() == 0) {
        	return txInfos;
        }
        for(int j = 0 ; j < jsonArray.size() ; j++) {
            JSONObject txObject = jsonArray.getJSONObject(j);
            BigDecimal bigDecimal = txObject.getJSONObject("balance").getBigDecimal("amount");
            if(bigDecimal.ZERO.compareTo(bigDecimal) > 0) {
            	continue;
            }
            if(txObject.getBoolean("valid") == null || !txObject.getBoolean("valid")) {
            	continue;
            }
            JSONArray addressArray = txObject.getJSONArray("myaddresses");
            if(addressArray == null || addressArray.size() == 0) {
            	continue;
            }
            String txid = txObject.getString("txid");
            long time = Long.parseLong(txObject.get("time").toString() + "000");
            Date date = new Date(time);
            Integer confirmations = txObject.getInteger("confirmations");
            JSONArray voutArray = txObject.getJSONArray("vout");
            for(Object voutObj : voutArray) {
            	JSONObject parseObject2 = JSON.parseObject(voutObj.toString());
            	if(parseObject2.getBoolean("ismine") == null || !parseObject2.getBoolean("ismine")) {
            		continue;
            	}
            	String string = (String) parseObject2.getJSONArray("addresses").get(0);
            	TxInfo txInfo = new TxInfo();
            	txInfo.setAddress(string);
            	txInfo.setAmount(parseObject2.getBigDecimal("amount"));
            	txInfo.setTxid(txid + "_" + string);
            	txInfo.setTime(date);
            	txInfo.setConfirmations(confirmations);
            	txInfos.add(txInfo);
            }
        }
        //Collections.reverse(txInfos);
        return txInfos;
    }

    @Override
    public TxInfo getTransaction(String txId) {
    	String[] split = txId.split("_");
    	if(split.length != 2) {
    		return null;
    	}
    	String url = "http://" + accessKey + ":" + secretKey + "@" + walletLink;
    	String postData = "{\"jsonrpc\": \"2.0\", \"id\":\"showaddrdeal\", \"method\": \"showaddrdeal\", \"configs\":[" + coinUtils.methodParam(split[1] ,split[0]) + "]}";
    	JSONObject parseObject = JSON.parseObject(coinUtils.goPost(url,postData));
        if (parseObject == null || StringUtils.isEmpty(parseObject.toString())) {
            return null;
        }
        Integer integer = parseObject.getJSONObject("result").getInteger("confirmations");
        if(integer == null) {
        	return null;
        }
        TxInfo txInfo = new TxInfo();
        txInfo.setConfirmations(integer);
        return txInfo;
    }

    @Override
    public String sendToAddress(String to, BigDecimal amount, String comment, BigDecimal fee) {
    	walletPassPhrase(30);
    	String url = "http://" + accessKey + ":" + secretKey + "@" + walletLink;
    	String postData = "{\"jsonrpc\": \"2.0\", \"id\":\"send\", \"method\": \"send\", \"configs\":[" + coinUtils.methodParam(to ,amount) + "]}";
    	JSONObject parseObject = JSON.parseObject(coinUtils.goPost(url,postData));
    	walletLock();
        if (parseObject == null || StringUtils.isEmpty(parseObject.toString())) {
            return null;
        }
        String result = parseObject.getString("result");
        if (StringUtils.isEmpty(result) || "null".equals(result) ) {
            System.out.println("BTC sendToAddress error --->"+parseObject.toString());
            return null;
        }
        return result;
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
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public BigDecimal estimatesmartfee(Integer time) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ReturnResult sendToAddress(String to, BigDecimal amount, BigDecimal fee,String nonce, String gasPrice, String gas,String memo){
		if(!walletPassPhrase(30)) {
			return ReturnResult.FAILUER(403,"密码错误");
		}
    	String url = "http://" + accessKey + ":" + secretKey + "@" + walletLink;
    	String postData = "{\"jsonrpc\": \"2.0\", \"id\":\"send\", \"method\": \"send\", \"configs\":[" + coinUtils.methodParam(to ,amount) + "]}";
    	JSONObject parseObject = JSON.parseObject(coinUtils.goPost(url,postData));
    	walletLock();
        if (parseObject == null || StringUtils.isEmpty(parseObject.toString())) {
            return null;
        }
        String result = parseObject.getString("result");
        if (StringUtils.isEmpty(result) || "null".equals(result) ) {
            System.out.println("BTC sendToAddress error --->"+parseObject.toString());
            return null;
        }
        return ReturnResult.SUCCESS(200 ,result);
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
		// TODO Auto-generated method stub
		return null;
	}
}
