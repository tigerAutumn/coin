package com.hotcoin.coin.utils;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hotcoin.coin.entity.DepositRecordVo;
import com.hotcoin.coin.entity.WithDrawRecordVo;
import com.qkwl.common.dto.capital.FVirtualCapitalOperationDTO;
import com.qkwl.common.dto.coin.FPool;
import com.qkwl.common.dto.coin.SystemCoinType;
import com.qkwl.common.exceptions.BCException;


public class CoinRpcUtlis {
	private static final Logger logger = LoggerFactory.getLogger(CoinRpcUtlis.class);
	
	private final static Integer SUCCESS = new Integer(200); 
	
	private final static Integer PROGRESS_SUCCESS = new Integer(201); 
	
	
	public static BigDecimal getBalance(SystemCoinType systemCoinType) throws Exception {
		if(systemCoinType == null) {
			return null;
		}
		JSONObject jo = new JSONObject();
		jo.put("symbol", systemCoinType.getShortName());
		jo.put("address", "");
		jo.put("memo", "");
		String url = systemCoinType.getWalletUrl() + "/wallet/core/getBalance";
		String postDate = jo.toJSONString();
		JSONObject goPost = goPost(url, postDate);
		Integer code = goPost.getInteger("code");
		if(!SUCCESS.equals(code)) {
			logger.error("请求:{},参数:{},返回{}",url,postDate,goPost.toString());
			throw new BCException(code,goPost.getString("msg"));
		}
		return goPost.getBigDecimal("data");
	}
	
	
	public static List<FPool> createAddress(SystemCoinType systemCoinType, Integer number) throws Exception {
		if(systemCoinType == null || number == null) {
			return null;
		}
		JSONObject jo = new JSONObject();
		jo.put("symbol", systemCoinType.getShortName());
		jo.put("number", number);
		String url = systemCoinType.getWalletUrl() + "/wallet/core/createAddress";
		String postDate = jo.toJSONString();
		JSONObject goPost = goPost(url, postDate);
		Integer code = goPost.getInteger("code");
		if(!SUCCESS.equals(code)) {
			logger.error("请求:{},参数:{},返回{}",url,postDate,goPost.toString());
			throw new BCException(code,goPost.getString("msg"));
		}
		
		JSONArray jsonArray = goPost.getJSONArray("data");
		List<FPool> list = new ArrayList<FPool>();
		for (int i = 0; i < jsonArray.size(); i++) {
			String string = jsonArray.getString(i);
			list.add(new FPool(string));
		}
		return list;
	}
	
	
	public static String sendToAddress(SystemCoinType systemCoinType,FVirtualCapitalOperationDTO record) throws Exception {
		if(systemCoinType == null || record == null) {
			return null;
		}
		WithDrawRecordVo withDrawRecordVo = new WithDrawRecordVo();
		withDrawRecordVo.setOrderId(record.getFid().toString());
		withDrawRecordVo.setAmount(record.getFamount().doubleValue());
		withDrawRecordVo.setTo(record.getFwithdrawaddress());
		withDrawRecordVo.setMemo(record.getMemo());
		withDrawRecordVo.setSymbol(systemCoinType.getShortName());
		String url = systemCoinType.getWalletUrl() + "/wallet/core/sendTo";
		String postDate =  JSON.toJSONString(withDrawRecordVo);
		JSONObject goPost = goPost(url, postDate);
		Integer code = goPost.getInteger("code");
		if(!SUCCESS.equals(code)) {
			logger.error("请求:{},参数:{},返回{}",url,postDate,goPost.toString());
			throw new BCException(code,goPost.getString("msg"));
		}
		return goPost.getString("data");
	}
	
	
	public static BigInteger getBlockNumber(SystemCoinType systemCoinType) throws Exception {
		if(systemCoinType == null) {
			return null;
		}
		JSONObject jo = new JSONObject();
		jo.put("symbol", systemCoinType.getShortName());
		String url = systemCoinType.getWalletUrl() + "/wallet/core/getBlockNumber";
		String postDate =  jo.toJSONString();
		JSONObject goPost = goPost(url, postDate);
		Integer code = goPost.getInteger("code");
		if(!SUCCESS.equals(code)) {
			logger.error("请求:{},参数:{},返回{}",url,postDate,goPost.toString());
			throw new BCException(code,goPost.getString("msg"));
		}
		return goPost.getBigInteger("data");
	}
	
	
	public static FVirtualCapitalOperationDTO getDepositRecord(SystemCoinType systemCoinType,FVirtualCapitalOperationDTO record) throws Exception {
		if(systemCoinType == null || record == null) {
			return null;
		}
		JSONObject jo = new JSONObject();
		jo.put("symbol", systemCoinType.getShortName());
		String[] split = record.getFuniquenumber().split("_");
		if(split.length == 2) {
			jo.put("txIndex", split[1]);
		}else {
			jo.put("txIndex", 0);
		}
		jo.put("hash", split[0]);
		String url = systemCoinType.getWalletUrl() + "/wallet/core/getDepositRecord";
		String postDate =  jo.toJSONString();
		JSONObject goPost = goPost(url, postDate);
		Integer code = goPost.getInteger("code");
		if(!SUCCESS.equals(code)) {
			logger.error("请求:{},参数:{},返回{}",url,postDate,goPost.toString());
			throw new BCException(code,goPost.getString("msg"));
		}
		DepositRecordVo drv = goPost.getObject("data", DepositRecordVo.class);
		record.setFamount(drv.getAmount());
		record.setFblocknumber(drv.getBlockNumber());
		record.setFconfirmations(drv.getConfirmNumber());
		record.setFrechargeaddress(drv.getTo());
		record.setMemo(drv.getMemo());
		return record;
	}
	
	
	
	
	

	
	
	private static JSONObject goPost(String url ,String postData) {
		try {
			URL obj = new URL(url);
			//authenticator();
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();
			con.addRequestProperty("Content-Type","application/json;charset=UTF-8");
			con.setRequestMethod("POST");
			con.setConnectTimeout(60 * 1000);
			con.setReadTimeout(120 * 1000);
			con.setDoOutput(true);
			DataOutputStream wr = new DataOutputStream(con.getOutputStream());
			wr.writeBytes(postData);
			wr.flush();
			wr.close();
			int responseCode = con.getResponseCode();
			if (responseCode != 200) {
				logger.error("Coin goPost failed : {}_{}", responseCode,  postData);
				return null;
			}
			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream(),"UTF-8"));
			StringWriter writer = new StringWriter();
            char[] chars = new char[1024];
            int count = 0;
            while ((count = in.read(chars)) > 0) {
                writer.write(chars, 0, count);
            }
            in.close();
            //String string = new String(writer.toString().getBytes(),"utf-8");
            JSONObject parseObject = JSON.parseObject(writer.toString());
            return parseObject;
		} catch (Exception e) {
			logger.error("Coin goPost error {}，{} ", url, postData, e);
			return null;
		}
	}
	
}
