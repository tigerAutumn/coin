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
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * BTCDriver
 *
 * @author jany
 */
public class MULTIDriver implements CoinDriver {
	
	private static final Logger logger = LoggerFactory.getLogger(MULTIDriver.class);

    private CoinUtils coinUtils = null;
    private String passWord = null;
    private Integer coinSort = null;
    private String shortName = null;
    private String sendAccount = null;
    private String contractAccount = null;

    public MULTIDriver(String accessKey, String secretKey, String walletLink,String chainLink ,String pass, Integer coinSort,String shortName,String sendAccount, String contractAccount) {
        coinUtils = new CoinUtils(accessKey, secretKey, walletLink, chainLink);
        this.passWord = pass;
        this.coinSort = coinSort;
        this.shortName = shortName;
        this.sendAccount = sendAccount;
        this.contractAccount = contractAccount;
    }

    @Override
    public BigDecimal getBalance() {
        JSONObject resultJson = coinUtils.go("getaddressbalances", "[\""+sendAccount+"\"]");
        JSONArray jsonArray = resultJson.getJSONArray("result");
        if(jsonArray == null || jsonArray.size() == 0) {
        	return null;
        }
        for (int i = 0; i < jsonArray.size(); i++) {
        	JSONObject jsonObject = jsonArray.getJSONObject(i);
        	if(contractAccount.equals(jsonObject.getString("assetref"))) {
        		return jsonObject.getBigDecimal("qty");
        	}
		}
        return BigDecimal.ZERO;
    }

    @Override
    public FPool getNewAddress(String time) {
        JSONObject resultJson = coinUtils.go("getnewaddress", "[]");
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
        coinUtils.go("walletlock", "[]");
        return true;
    }

    @Override
    public boolean walletPassPhrase(int times) {
        if (passWord == null || passWord.length() <= 0) {
            return false;
        }
        JSONObject go = coinUtils.go("walletpassphrase", "[\"" + passWord + "\"," + times + "]");
        if(StringUtils.isEmpty(go.getString("error"))) {
        	logger.error("解锁失败："+go.getString("error"));
        	return true;
        }
        return false;
    }

    @Override
    public boolean setTxFee(BigDecimal fee) {
        JSONObject resultJson = coinUtils.go("settxfee", "[" + MathUtils.decimalFormat(fee) + "]");
        String result = resultJson.getString("result");
        if (StringUtils.isEmpty(result) || result.equals("null")) {
            return false;
        }
        return true;
    }

    @Override
    public List<TxInfo> listTransactions(int count, int from) {
        JSONObject resultJson = coinUtils.go("listwallettransactions", "[" + count + "," + from + ",false,true]");
        JSONArray jsonArray = resultJson.getJSONArray("result");
        if (jsonArray == null || jsonArray.size() == 0) {
            return null;
        }
        List<TxInfo> txInfos = new ArrayList<TxInfo>();
        for (int i = 0;i < jsonArray.size() ; i++) {
        	try {
	            JSONObject txObject = jsonArray.getJSONObject(i);
	            Boolean valid = txObject.getBoolean("valid");
	            if(valid == null || !valid) {
	            	continue;
	            }
	            String txid = txObject.getString("txid");
	            JSONArray myaddressesList = txObject.getJSONArray("myaddresses");
	            JSONArray voutList = txObject.getJSONArray("vout");
	            for (int j = 0; j < voutList.size() ; j++) {
	            	JSONObject vout = voutList.getJSONObject(j);
	            	JSONArray addresses = vout.getJSONArray("addresses");
	            	if(addresses == null || addresses.size() == 0) {
	            		continue;
	            	}
	            	String address = addresses.getString(0);
	            	boolean contains = myaddressesList.contains(address);
	            	//如果不是我们的地址或者是提现地址就不处理
	            	if(!contains || sendAccount.equals(address)) {
	            		continue;
	            	}
	            	//避免汇集出现的情况
	            	JSONArray assetss = vout.getJSONArray("assets");
	            	if(assetss == null || assetss.size() == 0) {
	            		continue;
	            	}
	            	JSONObject assets = assetss.getJSONObject(0);
	            	if(!"transfer".equals(assets.getString("type"))) {
	            		continue;
	            	}
	            	if(!contractAccount.equals(assets.getString("assetref"))) {
	            		continue;
	            	}
	            	BigDecimal bigDecimal = assets.getBigDecimal("qty");
	            	
	            	TxInfo txInfo = new TxInfo();
	            	txInfo.setAddress(address);
	            	txInfo.setAmount(bigDecimal);
	            	txInfo.setTxid(txid);
	            	txInfo.setVout(vout.getInteger("n"));
	            	txInfo.setConfirmations(txObject.getInteger("confirmations"));
	            	txInfos.add(txInfo);
				}
        	} catch (Exception e) {
				logger.error("解析tx异常,tx:"+jsonArray.getString(i),e);
			}
        }
        return txInfos;
    }

    @Override
    public TxInfo getTransaction(String txId) {
        JSONObject json = coinUtils.go("getwallettransaction", "[\"" + txId + "\"]");
        String result = json.getString("result");
        if (StringUtils.isEmpty(result) || "null".equals(result)) {
            return null;
        }
        JSONObject resultJson = json.getJSONObject("result");
        TxInfo txInfo = new TxInfo();
        //仅仅查确认数
        txInfo.setConfirmations(resultJson.getInteger("confirmations"));
        return txInfo;
    }
    
    @Override
	public ReturnResult sendToAddress(String to, BigDecimal amount,BigDecimal fee, String nonce, String gasPrice, String gas ,String memo){
        JSONObject resultJson = coinUtils.go("sendassetfrom", "[\"" + sendAccount + "\",\"" + to + "\",\""+ contractAccount +"\"," + amount + "]");
        //walletLock();
        logger.info("MULTI.sendToAddress resultJson:"+resultJson.toString());
        /*if (fee.compareTo(BigDecimal.ZERO) >0 && !setTxFee(BigDecimal.valueOf(0.0001))) {
        }*/
        String result = resultJson.getString("result");
        if (StringUtils.isEmpty(result) || "null".equals(result) ) {
            System.out.println("MULTI.sendToAddress error --->"+resultJson.toString());
            return null;
        }
        return ReturnResult.SUCCESS(200 ,result);
	}
    
    @Override
	public ReturnResult sendToAddress(String from, String to, BigDecimal amount, String comment, BigDecimal fee,
			Integer propertyid,BigDecimal satoshiPerByte) {
		return null;
	}

    @Override
    public String sendToAddress(String to, BigDecimal amount, String comment, BigDecimal fee) {
    	JSONObject resultJson = coinUtils.go("sendassetfrom", "[\"" + sendAccount + "\",\"" + to + "\",\""+ contractAccount +"\"," + amount + "]");
        //walletLock();
        logger.info("MULTI.sendToAddress resultJson:"+resultJson.toString());
        /*if (fee.compareTo(BigDecimal.ZERO) >0 && !setTxFee(BigDecimal.valueOf(0.0001))) {
        }*/
        String result = resultJson.getString("result");
        if (StringUtils.isEmpty(result) || "null".equals(result) ) {
            System.out.println("MULTI.sendToAddress error --->"+resultJson.toString());
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
	public BigDecimal getBalance(String address) {
		JSONObject resultJson = coinUtils.go("getaddressbalances", "[\""+address+"\"]");
        JSONArray jsonArray = resultJson.getJSONArray("result");
        for (int i = 0; i < jsonArray.size(); i++) {
        	JSONObject jsonObject = jsonArray.getJSONObject(i);
        	if(contractAccount.equals(jsonObject.getString("assetref"))) {
        		return jsonObject.getBigDecimal("qty");
        	}
		}
        return BigDecimal.ZERO;
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
		JSONObject resultJson = coinUtils.go("sendassetfrom", "[\"" + from + "\",\"" + to + "\",\""+ contractAccount +"\"," + amount + "]");
        //walletLock();
        logger.info("MULTI.sendToAddress resultJson:"+resultJson.toString());
        /*if (fee.compareTo(BigDecimal.ZERO) >0 && !setTxFee(BigDecimal.valueOf(0.0001))) {
        }*/
        String result = resultJson.getString("result");
        if (StringUtils.isEmpty(result) || "null".equals(result) ) {
            System.out.println("MULTI.sendToAddress error --->"+resultJson.toString());
            return null;
        }
		return result;
	}

	@Override
	public List<TxInfo> getBlock(BigInteger blockNum) {
		// TODO Auto-generated method stub
		return null;
	}
}
