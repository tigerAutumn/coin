package com.qkwl.common.coin.driver;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.qkwl.common.coin.CoinDriver;
import com.qkwl.common.coin.CoinUtils;
import com.qkwl.common.coin.TxInfo;
import com.qkwl.common.dto.coin.FPool;
import com.qkwl.common.util.ByteUtil;
import com.qkwl.common.util.CoinCommentUtils;
import com.qkwl.common.util.ReturnResult;
import com.qkwl.common.util.Utils;

/**
 * BTCDriver
 *
 * @author hwj
 */
public class BWTDriver implements CoinDriver {
	
	private static final Logger logger = LoggerFactory.getLogger(BWTDriver.class);

    private CoinUtils coinUtils = null;
    private String passWord = null;
    private Integer coinSort = null;
    private String sendAccount = null;
    private String contractAccount = null;
    private String shortName = null;
    private String walletLink = null;
    private String chainLink = null;
    private String walletAccount = null;
    

    public BWTDriver(String accessKey, String secretKey, String walletLink, String chainLink, String pass, Integer coinSort, String sendAccount,String contractAccount,String shortName,String walletAccount) {
        coinUtils = new CoinUtils(accessKey, secretKey, walletLink , chainLink);
        this.passWord = pass;
        this.coinSort = coinSort;
        this.sendAccount = sendAccount;
        this.contractAccount = contractAccount;
        this.shortName = shortName;
        this.walletLink = walletLink;
        this.chainLink = chainLink;
        this.walletAccount = walletAccount;
    }

    @Override
    public BigDecimal getBalance() {
    	try {
    		String result = coinUtils.goGet(walletLink+"/v2/accounts/"+sendAccount+"/balances");
            if(StringUtils.isEmpty(result)) {
            	return null;
            }
            JSONObject jo = JSON.parseObject(result);
            JSONArray jsonArray = jo.getJSONArray("balances");
            for (Object object : jsonArray) {
            	JSONObject blance = JSON.parseObject(object.toString());
            	if(shortName.equals(blance.getString("currency"))) {
            		return new BigDecimal(blance.getString("value"));
            	}
    		}
		} catch (Exception e) {
			logger.error("查询余额异常",e);
		}
        return null;
    }
    
    @Override
	public BigDecimal getBalance(String address) {
    	try {
    		String result = coinUtils.goGet(walletLink+"/v2/accounts/"+address+"/balances");
            if(StringUtils.isEmpty(result)) {
            	return null;
            }
            JSONObject jo = JSON.parseObject(result);
            JSONArray jsonArray = jo.getJSONArray("balances");
            for (Object object : jsonArray) {
            	JSONObject blance = JSON.parseObject(object.toString());
            	if(shortName.equals(blance.getString("currency"))) {
            		return new BigDecimal(blance.getString("value"));
            	}
    		}
		} catch (Exception e) {
			logger.error("查询余额异常",e);
		}
        return null;
	}

    @Override
    public FPool getNewAddress(String time) {
    	String result = coinUtils.goGet(walletLink+"/v2/wallet/new");
    	try {
        	if(StringUtils.isEmpty(result)) {
            	return null;
            }
        	logger.info(result);
            JSONObject jo = JSON.parseObject(result);
            JSONObject jsonObject = jo.getJSONObject("wallet");
            String address = jsonObject.getString("address");
            FPool fPool = new FPool(address);
            fPool.setSecret(jsonObject.getString("secret"));
            String registaddress = registaddress(address, null);
            if(StringUtils.isEmpty(registaddress)) {
            	return null;
            }
            return fPool;
		} catch (Exception e) {
			logger.error("新增地址异常"+result,e);
			return null;
		}
    }

    @Override
	public String registaddress(String addr,BigDecimal fee) {
		try {
			String str = "{" + 
					"	\"secret\": \""+ passWord +"\"," + 
					"	\"client_id\": \""+ addr +"\"," + 
					"	\"payment\": {" + 
					"		\"source\": \""+sendAccount+"\"," + 
					"		\"destination\": \""+ addr +"\"," + 
					"		\"amount\": {" + 
					"			\"value\": \"12.00\"," + 
					"			\"currency\": \""+ shortName +"\"," + 
					"			\"issuer\": \"\"" + 
					"			},\r\n" + 
					"		\"choice\": \"\"," + 
					"		\"memos\": [\"registaddress\"]" + 
					"	}" + 
					"}";
			String goPost = coinUtils.goPost(walletLink + "/v2/accounts/"+ sendAccount +"/payments", str);
			if(StringUtils.isEmpty(goPost)) {
				return null;
			}
			JSONObject parseObject = JSON.parseObject(goPost);
			Boolean boolean1 = parseObject.getBoolean("success");
			//转账成功
			if(boolean1) {
				return parseObject.getString("hash");
			}
		} catch (Exception e) {
			logger.error("BIZ.sendToAddress ERROR",e);
			return null;
		}
		return null;
	}
    
    @Override
	public ReturnResult sendToAddress(String to, BigDecimal amount,BigDecimal fee, String nonce, String gasPrice, String gas,String memo) {
    	if(StringUtils.isEmpty(nonce)) {
    		nonce = com.qkwl.common.util.DateUtils.format(new Date(), com.qkwl.common.util.DateUtils.YYYYMMDDHHMMSS) + Utils.getRandomString(5);
    	}
		try {
			String str = "{" + 
					"	\"secret\": \""+ passWord +"\"," + 
					"	\"client_id\": \""+ nonce +"\"," + 
					"	\"payment\": {" + 
					"		\"source\": \""+sendAccount+"\"," + 
					"		\"destination\": \""+ to +"\"," + 
					"		\"amount\": {" + 
					"			\"value\": \""+amount+"\"," + 
					"			\"currency\": \""+ shortName +"\"," + 
					"			\"issuer\": \""+ contractAccount +"\"" + 
					"			}," + 
					"		\"choice\": \"\"," + 
					"		\"memos\": []" + 
					"	}" + 
					"}";
			String goPost = coinUtils.goPost(walletLink + "/v2/accounts/"+ sendAccount +"/payments", str);
			if(StringUtils.isEmpty(goPost)) {
				return null;
			}
			JSONObject parseObject = JSON.parseObject(goPost);
			Boolean boolean1 = parseObject.getBoolean("success");
			//转账成功
			if(boolean1) {
				return ReturnResult.SUCCESS(200 ,parseObject.getString("hash"));
			}
		} catch (Exception e) {
			logger.error("BIZ.sendToAddress ERROR",e);
			return ReturnResult.FAILUER(300, "系统异常");
		}
		return null;
	}
    
    @Override
	public List<TxInfo> listTransactionsByAddress(int pageSize, int page, String address) {
		String url = walletLink + "/v2/accounts/" + address + "/payments?forward=desc&results_per_page="+pageSize + "&page="+ page;
		try {
			String goGet = coinUtils.goGet(url);
			if(StringUtils.isEmpty(goGet)) {
				return null;
			}
			JSONObject parseObject = JSON.parseObject(goGet);
			if(!parseObject.getBoolean("success")) {
				logger.error("查询交易记录失败，url:"+url+",result:"+goGet);
				return null;
			}
			JSONArray payments = parseObject.getJSONArray("payments");
			List<TxInfo> txInfos = new ArrayList<TxInfo>();
	        for (Object object : payments) {
	            JSONObject txObject = JSON.parseObject(object.toString());
	            if(!"received".equals(txObject.getString("type"))) {
	            	continue;
	            }
	            JSONObject jsonObject = txObject.getJSONObject("amount");
	            if(!shortName.equals(jsonObject.getString("currency"))) {
	            	continue;
	            }
	            if(sendAccount.equals(txObject.getString("counterparty"))) {
	            	continue;
	            }
	            JSONArray jsonArray = txObject.getJSONArray("memos");
	            //地址激活的时候memo为registaddress
	            if(jsonArray != null && jsonArray.size() > 0 && "registaddress".equals(jsonArray.get(0).toString())) {
	            	continue;
	            }
	            
	            TxInfo txInfo = new TxInfo();
	            txInfo.setAddress(address);
	            txInfo.setAmount(new BigDecimal(jsonObject.getString("value")));
	            txInfo.setCategory(txObject.getString("type"));
	            txInfo.setTxid(txObject.getString("hash"));
	            txInfos.add(txInfo);
	        }
	        Collections.reverse(txInfos);
	        return txInfos;
		} catch (Exception e) {
			logger.error("查询充值记录异常"+url,e);
		}
		return null;
	}

	@Override
	public String sendToAddress(String from, String to, BigDecimal amount, String memo, BigDecimal fee, String pass) {
		String format = com.qkwl.common.util.DateUtils.format(new Date(), com.qkwl.common.util.DateUtils.YYYYMMDDHHMMSS);
		try {
			String str = "{" + 
					"	\"secret\": \""+ pass +"\"," + 
					"	\"client_id\": \""+ format + Utils.getRandomString(5) +"\"," + 
					"	\"payment\": {" + 
					"		\"source\": \""+ from +"\"," + 
					"		\"destination\": \""+ to +"\"," + 
					"		\"amount\": {" + 
					"			\"value\": \""+amount+"\"," + 
					"			\"currency\": \""+ shortName +"\"," + 
					"			\"issuer\": \""+ contractAccount +"\"" + 
					"			}," + 
					"		\"choice\": \"\"," + 
					"		\"memos\": [\""+format+"\"]" + 
					"	}" + 
					"}";
			String goPost = coinUtils.goPost(walletLink + "/v2/accounts/"+ from +"/payments", str);
			if(StringUtils.isEmpty(goPost)) {
				return null;
			}
			JSONObject parseObject = JSON.parseObject(goPost);
			Boolean boolean1 = parseObject.getBoolean("success");
			//转账成功
			if(boolean1) {
				return parseObject.getString("hash");
			}
		} catch (Exception e) {
			logger.error("BIZ.sendToAddress ERROR",e);
		}
		return null;
	}
    
	
    @Override
    public String sendToAddress(String to, BigDecimal amount, String comment, BigDecimal fee) {
    	String nonce = com.qkwl.common.util.DateUtils.format(new Date(), com.qkwl.common.util.DateUtils.YYYYMMDDHHMMSS) + Utils.getRandomString(5);
		try {
			String str = "{" + 
					"	\"secret\": \""+ passWord +"\"," + 
					"	\"client_id\": \""+ nonce +"\"," + 
					"	\"payment\": {" + 
					"		\"source\": \""+sendAccount+"\"," + 
					"		\"destination\": \""+ to +"\"," + 
					"		\"amount\": {" + 
					"			\"value\": \""+amount+"\"," + 
					"			\"currency\": \""+ shortName +"\"," + 
					"			\"issuer\": \""+ contractAccount +"\"" + 
					"			}," + 
					"		\"choice\": \"\"," + 
					"		\"memos\": []" + 
					"	}" + 
					"}";
			String goPost = coinUtils.goPost(walletLink + "/v2/accounts/"+ sendAccount +"/payments", str);
			logger.info(goPost);
			if(StringUtils.isEmpty(goPost)) {
				return null;
			}
			JSONObject parseObject = JSON.parseObject(goPost);
			Boolean boolean1 = parseObject.getBoolean("success");
			//转账成功
			if(boolean1) {
				return parseObject.getString("hash");
			}
		}catch (Exception e) {
			logger.error("转账异常",e);
		}
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

    //仅仅确认高度
    @Override
    public TxInfo getTransaction(String txId) {
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
	public ReturnResult sendToAddress(String from, String to, BigDecimal amount, String comment, BigDecimal fee,
			Integer propertyid,BigDecimal satoshiPerByte) {
		// TODO Auto-generated method stub
		return null;
	}
	
	

	@Override
	public BigDecimal estimatesmartfee(Integer time) {
		// TODO Auto-generated method stub
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
	public List<TxInfo> getBlock(BigInteger blockNum) {
		// TODO Auto-generated method stub
		return null;
	}

	
	
}
