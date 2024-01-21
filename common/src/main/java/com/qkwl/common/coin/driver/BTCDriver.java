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
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * BTCDriver
 *
 * @author jany
 */
public class BTCDriver implements CoinDriver {
	
	private static final Logger logger = LoggerFactory.getLogger(BTCDriver.class);

    private CoinUtils coinUtils = null;
    private String passWord = null;
    private Integer coinSort = null;
    private String shortName = null;

    public BTCDriver(String accessKey, String secretKey, String walletLink,String chainLink ,String pass, Integer coinSort,String shortName) {
        coinUtils = new CoinUtils(accessKey, secretKey, walletLink, chainLink);
        this.passWord = pass;
        this.coinSort = coinSort;
        this.shortName = shortName;
    }

    @Override
    public BigDecimal getBalance() {
        JSONObject resultJson = coinUtils.go("getbalance", "[]");
        String result = resultJson.getString("result");
        if (StringUtils.isEmpty(result) || result.equals("null")) {
            return null;
        }
        return new BigDecimal(result);
    }

    @Override
    public FPool getNewAddress(String time) {
        JSONObject resultJson = coinUtils.go("getnewaddress", "[\"" + time + "\"]");
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
        if("GOD".equals(shortName) || "BCHSV".equals(shortName)) {
        	return true;
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
        JSONObject resultJson = coinUtils.go("listtransactions", "[\"*\"," + count + "," + from + "]");
        String result = resultJson.getString("result");
        if (StringUtils.isEmpty(result) || result.equals("null")) {
            return null;
        }
        List<TxInfo> txInfos = new ArrayList<TxInfo>();
        JSONArray jsonArray = JSONArray.parseArray(result);
        for (Object object : jsonArray) {
            JSONObject txObject = JSON.parseObject(object.toString());
            if (!txObject.get("category").toString().equals("receive")) {
                continue;
            }
            TxInfo txInfo = new TxInfo();
            //txInfo.setAccount(txObject.getString("account"));
            txInfo.setAddress(txObject.getString("address"));
            txInfo.setAmount(new BigDecimal(txObject.getString("amount")));
            txInfo.setCategory(txObject.get("category").toString());
            if (!StringUtils.isEmpty(txObject.getString("confirmations"))) {
                txInfo.setConfirmations(Integer.parseInt(txObject.getString("confirmations")));
            } else {
                txInfo.setConfirmations(0);
            }
            txInfo.setVout(txObject.getInteger("vout"));
            long time = Long.parseLong(txObject.getString("time") + "000");
            txInfo.setTime(new Date(time));
            txInfo.setTxid(txObject.getString("txid"));
            txInfos.add(txInfo);
        }
        Collections.reverse(txInfos);
        return txInfos;
    }

    @Override
    public TxInfo getTransaction(String txId) {
        JSONObject json = coinUtils.go("gettransaction", "[\"" + txId + "\"]");
        String result = json.getString("result");
        if (StringUtils.isEmpty(result) || "null".equals(result)) {
            return null;
        }
        JSONObject resultJson = json.getJSONObject("result");
        JSONArray detailsArray = JSONArray.parseArray(resultJson.get("details").toString());
        TxInfo txInfo = new TxInfo();
        for (Object object : detailsArray) {
            JSONObject detailsObject = JSON.parseObject(object.toString());
            if (!detailsObject.get("category").toString().equals("receive")) {
                continue;
            }
            //txInfo.setAccount(detailsObject.get("account").toString());
            txInfo.setAddress(detailsObject.getString("address"));
            txInfo.setAmount(new BigDecimal(detailsObject.getString("amount")));
            break;
        }
        txInfo.setCategory("receive");
        if (!StringUtils.isEmpty(resultJson.getString("confirmations"))) {
            txInfo.setConfirmations(Integer.parseInt(resultJson.getString("confirmations")));
        } else {
            txInfo.setConfirmations(0);
        }
        long time = Long.parseLong(resultJson.getString("time"));
        txInfo.setTime(new Date(time));
        txInfo.setTxid(resultJson.getString("txid"));
        return txInfo;
    }
    
    @Override
	public ReturnResult sendToAddress(String to, BigDecimal amount,BigDecimal fee, String nonce, String gasPrice, String gas ,String memo){
    	if (fee.compareTo(BigDecimal.ZERO) >0 && !setTxFee(fee)) {
            return null;
        }
        if(!walletPassPhrase(30)) {
        	return ReturnResult.FAILUER(403,"密码错误");
        }
        JSONObject resultJson = coinUtils.go("sendtoaddress", "[\"" + to + "\"," + amount + "," + "\"" + memo + "\"]");
        walletLock();
        logger.info("BTC.sendToAddress resultJson:"+resultJson.toString());
        if (fee.compareTo(BigDecimal.ZERO) >0 && !setTxFee(BigDecimal.valueOf(0.0001))) {
            // 设置失败也无所谓
        }
        String result = resultJson.getString("result");
        if (StringUtils.isEmpty(result) || "null".equals(result) ) {
            System.out.println("BTC sendToAddress error --->"+resultJson.toString());
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
        if (fee.compareTo(BigDecimal.ZERO) >0 && !setTxFee(fee)) {
            return null;
        }
        walletPassPhrase(30);
        JSONObject resultJson = coinUtils.go("sendtoaddress", "[\"" + to + "\"," + amount + "," + "\"" + comment + "\"]");
        walletLock();
        logger.info("BTC.sendToAddress resultJson:"+resultJson.toString());
        if (fee.compareTo(BigDecimal.ZERO) >0 && !setTxFee(BigDecimal.valueOf(0.0001))) {
            // 设置失败也无所谓
        }
        String result = resultJson.getString("result");
        if (StringUtils.isEmpty(result) || "null".equals(result) ) {
            System.out.println("BTC sendToAddress error --->"+resultJson.toString());
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
		// TODO Auto-generated method stub
		return null;
	}
}
