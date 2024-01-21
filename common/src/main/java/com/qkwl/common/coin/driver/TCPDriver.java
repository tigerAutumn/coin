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
import com.qkwl.common.util.DesUtils;
import com.qkwl.common.util.ReturnResult;
import com.qkwl.common.util.Utils;

/**
 * BTCDriver
 *
 * @author hwj
 */
public class TCPDriver implements CoinDriver {
	
	private static final Logger logger = LoggerFactory.getLogger(TCPDriver.class);

    private CoinUtils coinUtils = null;
    private String passWord = null;
    private Integer coinSort = null;
    private String sendAccount = null;
    private String contractAccount = null;
    private String shortName = null;
    private String walletLink = null;
    private String chainLink = null;
    private String walletAccount = null;
    

    public TCPDriver(String accessKey, String secretKey, String walletLink, String chainLink, String pass, Integer coinSort, String sendAccount,String contractAccount,String shortName,String walletAccount) {
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
    		String result = coinUtils.goGet(walletLink+"?type=balance&sender="+sendAccount);
            if(StringUtils.isEmpty(result)) {
            	return null;
            }
            JSONObject jo = JSON.parseObject(result);
            Integer i = jo.getInteger("status");
            if(i == null || !i.equals(1)) {
            	return null;
            }
            return jo.getJSONObject("return").getBigDecimal("balance");
		} catch (Exception e) {
			logger.error("查询余额异常",e);
		}
        return null;
    }
    
    @Override
	public BigDecimal getBalance(String address) {
    	try {
    		String result = coinUtils.goGet(walletLink+"?type=balance&sender="+address);
            if(StringUtils.isEmpty(result)) {
            	return null;
            }
            JSONObject jo = JSON.parseObject(result);
            Integer i = jo.getInteger("status");
            if(i == null || !i.equals(1)) {
            	return null;
            }
            return jo.getJSONObject("return").getBigDecimal("balance");
		} catch (Exception e) {
			logger.error("查询余额异常",e);
		}
        return null;
	}

    @Override
    public FPool getNewAddress(String time) {
    	String result = coinUtils.goGet(walletLink+"?type=create&value=1");
    	try {
        	if(StringUtils.isEmpty(result)) {
            	return null;
            }
            JSONObject jo = JSON.parseObject(result);
            Integer i = jo.getInteger("status");
            if(i == null || !i.equals(1)) {
            	return null;
            }
            
            JSONArray jsonArray = jo.getJSONArray("return");
            
            if(jsonArray == null || jsonArray.size() == 0) {
            	return null;
            }
            JSONObject ad = jsonArray.getJSONObject(0);
            FPool fPool = new FPool(ad.getString("address"));
            fPool.setSecret(ad.getString("privatekey"));
            return fPool;
		} catch (Exception e) {
			logger.error("新增地址异常"+result,e);
			return null;
		}
    }

    @Override
	public String registaddress(String addr,BigDecimal fee) {
		return null;
	}
    
    @Override
	public ReturnResult sendToAddress(String to, BigDecimal amount,BigDecimal fee, String nonce, String gasPrice, String gas,String memo) {
    	try {
    		if(!passWord.startsWith("0x")) {
				passWord = DesUtils.decrypt(passWord, "TCPencod");
			}
		} catch (Exception e) {
			return ReturnResult.FAILUER(403,"密码错误");
		}
		amount = amount.setScale(6, BigDecimal.ROUND_HALF_UP);
		String url = walletLink+"?type=transfer&value=" + amount.toString() +"&sender=" + sendAccount  + "&receiver=" + to +"&privatekey="+ passWord ;
		String result = coinUtils.goGet(url);
		if(StringUtils.isEmpty(result)) {
        	return null;
        }
        JSONObject jo = JSON.parseObject(result);
        Integer i = jo.getInteger("status");
        if(!i.equals(1)) {
        	return null;
        }
        JSONObject jsonObject = jo.getJSONObject("return");
        int intValue = jsonObject.getIntValue("code");
        if(intValue != 1) {
        	logger.error("转账异常，url:"+url +",return:" + result);
        	return null;
        }
        return ReturnResult.SUCCESS(200 ,jsonObject.getString("hash"));
	}
    
    @Override
	public List<TxInfo> listTransactionsByAddress(int pageSize, int page, String address) {
    	try {
    		String result = coinUtils.goGet(walletLink+"?type=balance&sender="+address);
        	if(StringUtils.isEmpty(result)) {
            	return null;
            }
            JSONObject jo = JSON.parseObject(result);
            Integer i = jo.getInteger("status");
            if(i == null || !i.equals(1)) {
            	return null;
            }
            
            JSONObject jsonObject = jo.getJSONObject("return");
            i = jsonObject.getInteger("code");
            if(i == null || !i.equals(1)) {
            	return null;
            }
            
            BigDecimal bigDecimal = jsonObject.getBigDecimal("balance");
            if(bigDecimal.compareTo(new BigDecimal(0.01))  <= 0 ) {
            	return null;
            }
            
            JSONArray jsonArray = jsonObject.getJSONArray("transaction");
            if(jsonArray == null || jsonArray.size() == 0) {
            	return null;
            }
            
            List<TxInfo> txInfos = new ArrayList<TxInfo>();
            for(int j = 0 ; j<jsonArray.size() ; j++) {
            	JSONObject tx = jsonArray.getJSONObject(j);
            	String string = tx.getString("sender");
            	if(sendAccount.equals(string)) {
            		continue;
            	}
            	
            	String to = tx.getString("transferto");
            	if(!to.equals(address)) {
            		continue;
            	}
            	
            	TxInfo txinfo = new TxInfo();
            	
            	txinfo.setAddress(to);
            	txinfo.setAmount(tx.getBigDecimal("value"));
            	txinfo.setTxid(tx.getString("hash") + "_" + to);
            	txInfos.add(txinfo);
            }
            return txInfos;
		} catch (Exception e) {
			logger.error("tcp查询充值记录异常",e);
		}
		return null;
	}

	@Override
	public String sendToAddress(String from, String to, BigDecimal amount, String memo, BigDecimal fee, String pass) {
		try {
			amount = amount.setScale(6, BigDecimal.ROUND_HALF_UP);
			String url = walletLink+"?type=transfer&value=" + amount.toString() +"&sender=" + from  + "&receiver=" + to +"&privatekey="+ pass ;
			String result = coinUtils.goGet(url);
			if(StringUtils.isEmpty(result)) {
            	return null;
            }
            JSONObject jo = JSON.parseObject(result);
            Integer i = jo.getInteger("status");
            if(!i.equals(1)) {
            	return null;
            }
            JSONObject jsonObject = jo.getJSONObject("return");
            int intValue = jsonObject.getIntValue("code");
            if(intValue != 1) {
            	logger.error("转账异常，url:"+url +",return:" + result);
            	return null;
            }
            return jsonObject.getString("hash");
		} catch (Exception e) {
			logger.error("tcp.sendToAddress ERROR",e);
		}
		return null;
	}
    
	
    @Override
    public String sendToAddress(String to, BigDecimal amount, String comment, BigDecimal fee) {
    	try {
			amount = amount.setScale(6, BigDecimal.ROUND_HALF_UP);
			if(!passWord.startsWith("0x")) {
				passWord = DesUtils.decrypt(passWord, "TCPencod");
			}
			String url = walletLink+"?type=transfer&value=" + amount.toString() +"&sender=" + sendAccount  + "&receiver=" + to +"&privatekey="+ passWord ;
			String result = coinUtils.goGet(url);
			if(StringUtils.isEmpty(result)) {
            	return null;
            }
            JSONObject jo = JSON.parseObject(result);
            Integer i = jo.getInteger("status");
            if(!i.equals(1)) {
            	return null;
            }
            JSONObject jsonObject = jo.getJSONObject("return");
            int intValue = jsonObject.getIntValue("code");
            if(intValue != 1) {
            	logger.error("转账异常，url:"+url +",return:" + result);
            	return null;
            }
            return jsonObject.getString("hash");
		} catch (Exception e) {
			logger.error("tcp.sendToAddress ERROR",e);
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
