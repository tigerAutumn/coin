package com.qkwl.common.coin.driver;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.ParseException;
import java.util.ArrayList;
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

/**
 * BTCDriver
 *
 * @author hwj
 */
public class EOSDriver implements CoinDriver {
	
	private static final Logger logger = LoggerFactory.getLogger(EOSDriver.class);

    private CoinUtils coinUtils = null;
    private String passWord = null;
    private Integer coinSort = null;
    private String sendAccount = null;
    private String contractAccount = null;
    private String shortName = null;
    private String walletLink = null;
    private String chainLink = null;
    private String walletAccount = null;
    

    public EOSDriver(String accessKey, String secretKey, String walletLink, String chainLink, String pass, Integer coinSort, String sendAccount,String contractAccount,String shortName,String walletAccount) {
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
        String result = coinUtils.goEOS(chainLink,"/v1/chain/get_currency_balance", "{\"account\":\"" + sendAccount + "\",\"code\":\""+contractAccount+"\",\"symbol\":\""+shortName+"\"}");
        if("[]".equals(result)) {
        	return BigDecimal.ZERO;
        }
        if(result != null) {
        	List<String> parse = JSONArray.parseArray(result, String.class);
        	result = parse.get(0).replace(" "+shortName, "");
        }
        return new BigDecimal(result);
    }

    @Override
    public FPool getNewAddress(String time) {
    	return null;
    }

    @Override
    public boolean walletLock() {
        if (passWord == null || passWord.length() <= 0) {
            return false;
        }
        logger.info("eos请求lock");
        String goEOS = coinUtils.goEOS(walletLink,"/v1/wallet/lock", "\""+walletAccount+"\"");
        logger.info("eos请求lock，返回："+goEOS);
        return true;
    }

    @Override
    public boolean walletPassPhrase(int times) {
        if (passWord == null || passWord.length() <= 0) {
            return false;
        }
        String data = "[\""+ walletAccount +"\",\"" + passWord + "\"]";
        logger.info("eos请求unlock");
        String goEOS = coinUtils.goEOS(walletLink,"/v1/wallet/unlock", data);
        logger.info("eos请求unlock，返回："+goEOS);
        if(StringUtils.isEmpty(goEOS)) {
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
    	String result = coinUtils.goEOS(chainLink,"/v1/history/get_actions", "{\"pos\":" + from + ",\"offset\":"+count+",\"account_name\":\"" + sendAccount + "\"}");
    	if(StringUtils.isEmpty(result)) {
    		return null;
    	}
    	JSONObject parseObject = JSONObject.parseObject(result);
    	if(!parseObject.containsKey("actions")) {
    	   return null;
    	}
    	List<TxInfo> txInfos = new ArrayList<TxInfo>();
    	JSONArray jsonArray = parseObject.getJSONArray("actions");
    	//Integer lastBlock = parseObject.getInteger("last_irreversible_block");
    	for (Object object : jsonArray) {
    		JSONObject txObject = null;
    		try {
    			txObject = JSON.parseObject(object.toString());
    			TxInfo txInfo = new TxInfo();
    			JSONObject actionTrace = txObject.getJSONObject("action_trace");
				JSONObject data = actionTrace.getJSONObject("act");
				String token = data.getString("account");
				if(!contractAccount.equals(token)) {
					continue;
				}
				String string = data.getString("data");
				if(string.indexOf("{") == -1) {
					continue;
				}			
				data = data.getJSONObject("data");
				String fromAccount = data.getString("from");
				if(sendAccount.equals(fromAccount)) {
					continue;
				}
				string = data.getString("memo");
				if(StringUtils.isEmpty(string) || !StringUtils.isNumeric(string)) {
					continue;
				}
				String[] quantity = data.getString("quantity").split(" ");
				if(!shortName.equals(quantity[1])) {
					continue;
				}
				BigInteger integer = txObject.getBigInteger("block_num");
				txInfo.setBlockNumber(integer);
				txInfo.setTxid(actionTrace.getString("trx_id"));
				txInfo.setAddress(sendAccount);
				txInfo.setMemo(string);
				txInfo.setBlockNumber(integer);
				//txInfo.setConfirmations(lastBlock - integer);
				txInfo.setAmount(new BigDecimal(quantity[0]));
				txInfo.setShortName(quantity[1]);
				txInfos.add(txInfo);
			} catch (Exception e) {
				logger.error("EOS.listTransactions 异常"+ object.toString(), e);
				continue;
			}
        }
    	return txInfos;
    }

    //仅仅确认高度
    @Override
    public TxInfo getTransaction(String txId) {
        String json = coinUtils.goEOS(chainLink,"/v1/history/get_transaction", "{\"id\":\"" +txId+ "\"}");
        if(StringUtils.isEmpty(json)) {
        	return null;
        }
        JSONObject parseObject = JSON.parseObject(json);
        if(!parseObject.containsKey("trx")) {
        	return null;
        }
        try {
        	TxInfo txInfo = new TxInfo();
        	BigInteger blockNum = parseObject.getBigInteger("block_num");
        	BigInteger lastBlock = parseObject.getBigInteger("last_irreversible_block");

        	txInfo.setBlockNumber(blockNum);
			txInfo.setConfirmations(lastBlock.subtract(blockNum).intValue());

            return txInfo;
		} catch (Exception e) {
			logger.error("EOS.getTransaction 异常,params:{txId:"+txId+"}",e);
		}
        return null;
        
    }

    @Override
    public String sendToAddress(String to, BigDecimal amount, String comment, BigDecimal fee) {
    	return null;
    }

    @Override
    public String sendToAddress(String address, BigDecimal amount, String comment, BigDecimal fee, String memo) {
    	try {
    	amount = amount.setScale(4, BigDecimal.ROUND_HALF_UP);
    	String data = "{\"code\": \""+contractAccount+"\", \"action\": \"transfer\", \"args\": {\"from\": \"" + sendAccount + "\", \"to\": \"" + address + "\", \"quantity\": \""+amount.toString() + " "+shortName+"\", \"memo\": \""+memo+"\"}}";
    	logger.info("eos请求abi_json_to_bin,参数："+data);
    	String abi_json_to_bin = coinUtils.goEOS(chainLink,"/v1/chain/abi_json_to_bin", data);
    	logger.info("eos请求abi_json_to_bin,返回："+abi_json_to_bin);
    	JSONObject abiJsonToBin = JSON.parseObject(abi_json_to_bin);
    	
    	logger.info("eos请求get_info");
    	String get_info = coinUtils.goEOS(chainLink,"/v1/chain/get_info", "");
    	logger.info("eos请求get_info,返回："+get_info);
        JSONObject getInfo = JSON.parseObject(get_info);
        
        
        String data1 = "{\"block_num_or_id\": "+getInfo.getInteger("head_block_num")+"}";
        logger.info("eos请求get_block,参数："+data1);
        String get_block = coinUtils.goEOS(chainLink,"/v1/chain/get_block", "{\"block_num_or_id\": "+getInfo.get("head_block_num")+"}");
        logger.info("eos请求get_block,返回："+get_block);
        JSONObject bliockInfo = JSON.parseObject(get_block);
        Date parseDate;
		
		parseDate = DateUtils.parseDate(bliockInfo.getString("timestamp"), new String[] {"yyyy-MM-dd'T'HH:mm:ss.S"});
		
        parseDate = DateUtils.addHours(parseDate, 1);
        
        Integer i = bliockInfo.getInteger("block_num");
        Integer integer = i & 0x0000ffff;
        String tx = "{\"expiration\":\""+ DateFormatUtils.format(parseDate, "yyyy-MM-dd'T'HH:mm:ss") + "\","
        			+"\"ref_block_num\": "+ integer + ","
        			+"\"ref_block_prefix\": "+ bliockInfo.get("ref_block_prefix") + ","
        			+"\"max_net_usage_words\":  0,"
        			+"\"max_kcpu_usage\":  0,"
        			+"\"delay_sec\":  0,"
        			+ "\"signatures\":  [],"
        			+ "\"context_free_actions\":  [],"
        			+ "\"context_free_data\":  [],"
        			+ "\"actions\":  [{"
        							+ "\"account\": \""+  contractAccount  +"\","
        							+ "\"name\": \"transfer\","
        							+ "\"authorization\":"
        										+ " [{"
        											+ "\"actor\": \""+ sendAccount +"\","
        											+ "\"permission\": \"active\""
        										+ "}],"
        							+ "\"data\": \""+ abiJsonToBin.getString("binargs") +"\"}]" 
        							
        			+ "}";
        
        walletPassPhrase(30);
        logger.info("eos请求get_public_keys");
        String public_key = coinUtils.goEOS(walletLink,"/v1/wallet/get_public_keys", ""); //["EOS6MRyAjQq8ud7hVNYcfnVPJqcVpscN5So8BhtHuGYqET5GDW5CV"]
        logger.info("eos请求get_public_keys,返回"+public_key);
        
        String available_key = "{\"transaction\":"+tx 
        						+ ",\"available_keys\":"+public_key 
        						+"}";
        
        logger.info("eos请求get_required_keys，参数："+available_key );
        String get_required_keys = coinUtils.goEOS(chainLink,"/v1/chain/get_required_keys", available_key);
        logger.info("eos请求get_required_keys，返回："+get_required_keys );
        
        String sign_key = JSON.parseObject(get_required_keys).getString("required_keys"); //["EOS6MRyAjQq8ud7hVNYcfnVPJqcVpscN5So8BhtHuGYqET5GDW5CV"]
        
        //String  need_sign = "[" + tx + "," + sign_key +",\""+0000000000000000000000000000000000000000000000000000000000000000+"\"]";
        String  need_sign = "[" + tx + "," + sign_key +",\"\"]";
        if(getInfo.containsKey("chain_id") && getInfo.getString("chain_id") != null) {
        	 need_sign = "[" + tx + "," + sign_key +",\""+getInfo.getString("chain_id")+"\"]";
        }
        
        logger.info("eos请求sign_transaction，参数："+need_sign );
        String sign_transaction = coinUtils.goEOS(walletLink,"/v1/wallet/sign_transaction", need_sign);
        logger.info("eos请求sign_transaction，返回："+sign_transaction );
        
        walletLock();
        JSONObject parseObject = JSON.parseObject(sign_transaction);
        String  packed_trx ="{" 
							+ "\"compression\": \"none\", "
							+ "\"packed_context_free_data\": \"\","  
							+ "\"packed_trx\" : \""+ pack(parseObject) + "\"," 
							+ "\"signatures\" : "+ parseObject.getString("signatures") 
							+"}";
        
        
        logger.info("eos请求push_transaction，参数："+packed_trx );
        String push_transaction = coinUtils.goEOS(chainLink,"/v1/chain/push_transaction", packed_trx);
        logger.info("eos请求push_transaction，返回："+push_transaction );
        
        JSONObject parseObject2 = JSON.parseObject(push_transaction);
        
        if(parseObject2.containsKey("transaction_id")) {
        	return parseObject2.getString("transaction_id");
        }
        
        
    	} catch (ParseException e) {
    		logger.error("eos.sendToAddress异常,参数：{address："+address+",amount:"+amount +",memo:"+memo +"}",e);
		}
        return null;
    }
    
    private static String pack(JSONObject signTransaction) {

		try {
			StringBuilder sb = new StringBuilder();
	    	//String packed_trx ;
			Date parseDate;
			parseDate = CoinCommentUtils.fromISODate(signTransaction.getString("expiration"));
			Integer time = (int) (parseDate.getTime()/1000);
			sb.append(ByteUtil.packInt(time));
			sb.append(ByteUtil.packShort(signTransaction.getShort("ref_block_num")));
			sb.append(ByteUtil.packInt(signTransaction.getInteger("ref_block_prefix")));
			sb.append(ByteUtil.packShortVariable(signTransaction.getShort("max_net_usage_words")));
			sb.append(ByteUtil.packShortVariable(signTransaction.getShort("max_cpu_usage_ms")));
			sb.append(ByteUtil.packShortVariable(signTransaction.getShort("delay_sec")));
			sb.append(ByteUtil.packShortVariable((short) 0));
			JSONArray actions = signTransaction.getJSONArray("actions");
			sb.append(ByteUtil.packShortVariable((short) actions.size()));
			for (int i = 0; i < actions.size(); i++) {
				JSONObject jsonobject = actions.getJSONObject(i);
				sb.append(ByteUtil.packLong(ByteUtil.stringToName(jsonobject.getString("account"))));
				sb.append(ByteUtil.packLong(ByteUtil.stringToName(jsonobject.getString("name"))));
				JSONArray authorizations = jsonobject.getJSONArray("authorization");
				sb.append(ByteUtil.packShortVariable((short) authorizations.size()));
				for (int j = 0; j < authorizations.size(); j++) {
					JSONObject authorization = authorizations.getJSONObject(j);
					sb.append(ByteUtil.packLong(ByteUtil.stringToName(authorization.getString("actor"))));
					sb.append(ByteUtil.packLong(ByteUtil.stringToName(authorization.getString("permission"))));
				}
				String data = jsonobject.getString("data");
				if(data.length() % 2 == 1) {
					sb.append(ByteUtil.packShortVariable((short) (data.length() / 2 + 1)));
				}else {
					sb.append(ByteUtil.packShortVariable((short) (data.length() / 2)));
				}
				sb.append(data);
				if(i == actions.size() - 1) {
					sb.append("00");
				}
			}
			return sb.toString();
		} catch (Exception e) {
			logger.error("eos.pack异常 signTransaction:"+signTransaction,e);
		}
		return null;
	}

    public static void main(String[] args) {
		
/*    	String a = "0.010 EOS";
    	String[] split = a.split(" ");
    	for (String string : split) {
			System.out.println(string);
		}*/
    	
    	
    	String b = "{\r\n" + 
    			"	\"block_time\": \"2018-12-29T11:24:57.000\",\r\n" + 
    			"	\"block_num\": 34625369,\r\n" + 
    			"	\"action_trace\": {\r\n" + 
    			"		\"context_free\": false,\r\n" + 
    			"		\"elapsed\": 12,\r\n" + 
    			"		\"trx_id\": \"e09aa136821de025caa9366cdfa06f08b65d6e5257f6488d660c80164c414c6a\",\r\n" + 
    			"		\"block_time\": \"2018-12-29T11:24:57.000\",\r\n" + 
    			"		\"console\": \"\",\r\n" + 
    			"		\"block_num\": 34625369,\r\n" + 
    			"		\"producer_block_id\": \"021057598e171146cf4af5afa6005d993ff2cfcd61210112bf0423df9e825e12\",\r\n" + 
    			"		\"act\": {\r\n" + 
    			"			\"authorization\": [{\r\n" + 
    			"				\"actor\": \"goldioioioio\",\r\n" + 
    			"				\"permission\": \"active\"\r\n" + 
    			"			}],\r\n" + 
    			"			\"data\": \"401d75d451972265401da66a3a8a326d2267240100000000044647494f000000fe01596f75277665206265656e2073656e742066726f7a656e20746f6b656e73206f6620474f4c442e494f2c204647494f202866726f7a656e2047494f292e20474f4c442e494f20697320612044455820262044414320536973746572636861696e206f6620454f532e2042696720426f6e757320696e20746865206669727374207068617365206f6e2047494f2053414c45207768696368206973204c4956452e20446f6e2774206d697373206f7574203525202d20524546455252414c206f6e20746865204c4f434b44524f502e204265636f6d6520612042502e204a6f696e2074656c656772616d2068747470733a2f2f742e6d652f474f4c445f494f\",\r\n" + 
    			"			\"name\": \"transfer\",\r\n" + 
    			"			\"account\": \"goldioioioio\"\r\n" + 
    			"		},\r\n" + 
    			"		\"inline_traces\": [],\r\n" + 
    			"		\"receipt\": {\r\n" + 
    			"			\"receiver\": \"hotcoineosio\",\r\n" + 
    			"			\"code_sequence\": 8,\r\n" + 
    			"			\"abi_sequence\": 6,\r\n" + 
    			"			\"recv_sequence\": 220,\r\n" + 
    			"			\"auth_sequence\": [\r\n" + 
    			"				[\"goldioioioio\", 404684]\r\n" + 
    			"			],\r\n" + 
    			"			\"act_digest\": \"04d7722fb390a55930a17ed1e559ad8b0989bd7590e595d491d70c77d3992655\",\r\n" + 
    			"			\"global_sequence\": 3435004386\r\n" + 
    			"		},\r\n" + 
    			"		\"account_ram_deltas\": []\r\n" + 
    			"	},\r\n" + 
    			"	\"account_action_seq\": 81,\r\n" + 
    			"	\"global_action_seq\": 3435004386\r\n" + 
    			"}";
    	
    	
    	
    	JSONObject txObject = JSON.parseObject(b.toString());
		TxInfo txInfo = new TxInfo();
		JSONObject actionTrace = txObject.getJSONObject("action_trace");
		JSONObject data = actionTrace.getJSONObject("act");
		
		String string = data.getString("data");
		
		if(string.indexOf("{") != -1) {
			data = data.getJSONObject(string);
		}else{
			System.out.println(string);
		}
		
		
		//String fromAccount = data.getString("from");
    	//System.out.println(fromAccount);
    	
		/*try {
			String a = "{\"expiration\":\"2018-09-03T04:11:47\",\"ref_block_num\":27475,\"ref_block_prefix\":3773960086,\"max_net_usage_words\":0,\"max_cpu_usage_ms\":0,\"delay_sec\":0,\"context_free_actions\":[],\"actions\":[{\"account\":\"eosio.token\",\"name\":\"transfer\",\"authorization\":[{\"actor\":\"hotcoineosio\",\"permission\":\"active\"}],\"data\":\"401da66a3a8a326db055c2545dddf235010000000000000004454f5300000000dc01e4bc9fe69db0e68891e698afe8b081e68891e59ca8e593aae9878ce68891e59ca8e5b9b2e4bb80e4b988e591a2e4bc9fe69db0e5958ae5958ae5958ae5958ae5958ae5958ae5958ae5958ae5958ae5958ae5958ae5958ae5958ae5958ae5958ae5958ae5958ae5958ae5958ae5958ae5958ae5958ae5958ae5958ae5958ae5958ae5958ae5958ae5958ae5958ae5958ae5958ae5958ae5958ae5958ae5958ae5958ae5958ae5958ae5958ae5958ae5958ae5958ae5958ae5958ae5958ae5958ae5958ae5958ae5958ae5958ae5958ae5958ae5958ae5958ae5a5b332\"}],\"transaction_extensions\":[],\"signatures\":[\"SIG_K1_Kh6bMaytwyjhU4QEoKrLZ3RrYgYJAir3UVua8998wHwkoxgsesTGxcavQVfeGTmyJieJ4NZK944D84NgDhXWnFKzNamEau\"],\"context_free_data\":[]}"; 
			JSONObject parseObject = JSON.parseObject(a);
			String pack = pack(parseObject);
			System.out.println(pack);
		} catch (Exception e) { 
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
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
	public ReturnResult sendToAddress(String to, BigDecimal amount,BigDecimal fee, String nonce, String gasPrice, String gas ,String memo) {
		try {
	    	amount = amount.setScale(4, BigDecimal.ROUND_HALF_UP);
	    	String data = "{\"code\": \""+contractAccount+"\", \"action\": \"transfer\", \"args\": {\"from\": \"" + sendAccount + "\", \"to\": \"" + to + "\", \"quantity\": \""+amount.toString() + " "+shortName+"\", \"memo\": \""+memo+"\"}}";
	    	logger.info("eos请求abi_json_to_bin,参数："+data);
	    	String abi_json_to_bin = coinUtils.goEOS(chainLink,"/v1/chain/abi_json_to_bin", data);
	    	logger.info("eos请求abi_json_to_bin,返回："+abi_json_to_bin);
	    	if(StringUtils.isEmpty(abi_json_to_bin)) {
	    		return ReturnResult.FAILUER(403, "转币地址不存在或节点错误，请到区块浏览器上查询提现地址是否存在");
	    	}
	    	JSONObject abiJsonToBin = JSON.parseObject(abi_json_to_bin);
	    	
	    	logger.info("eos请求get_info");
	    	String get_info = coinUtils.goEOS(chainLink,"/v1/chain/get_info", "");
	    	logger.info("eos请求get_info,返回："+get_info);
	        JSONObject getInfo = JSON.parseObject(get_info);
	        
	        
	        String data1 = "{\"block_num_or_id\": "+getInfo.getInteger("head_block_num")+"}";
	        logger.info("eos请求get_block,参数："+data1);
	        String get_block = coinUtils.goEOS(chainLink,"/v1/chain/get_block", "{\"block_num_or_id\": "+getInfo.get("head_block_num")+"}");
	        logger.info("eos请求get_block,返回："+get_block);
	        JSONObject bliockInfo = JSON.parseObject(get_block);
	        Date parseDate;
			
			parseDate = DateUtils.parseDate(bliockInfo.getString("timestamp"), new String[] {"yyyy-MM-dd'T'HH:mm:ss.S"});
			
	        parseDate = DateUtils.addHours(parseDate, 1);
	        
	        Integer i = bliockInfo.getInteger("block_num");
	        Integer integer = i & 0x0000ffff;
	        String tx = "{\"expiration\":\""+ DateFormatUtils.format(parseDate, "yyyy-MM-dd'T'HH:mm:ss") + "\","
	        			+"\"ref_block_num\": "+ integer + ","
	        			+"\"ref_block_prefix\": "+ bliockInfo.get("ref_block_prefix") + ","
	        			+"\"max_net_usage_words\":  0,"
	        			+"\"max_kcpu_usage\":  0,"
	        			+"\"delay_sec\":  0,"
	        			+ "\"signatures\":  [],"
	        			+ "\"context_free_actions\":  [],"
	        			+ "\"context_free_data\":  [],"
	        			+ "\"actions\":  [{"
	        							+ "\"account\": \""+  contractAccount  +"\","
	        							+ "\"name\": \"transfer\","
	        							+ "\"authorization\":"
	        										+ " [{"
	        											+ "\"actor\": \""+ sendAccount +"\","
	        											+ "\"permission\": \"active\""
	        										+ "}],"
	        							+ "\"data\": \""+ abiJsonToBin.getString("binargs") +"\"}]" 
	        							
	        			+ "}";
	        
	        if(!walletPassPhrase(30)) {
	        	return ReturnResult.FAILUER(403, "密码错误");
	        }
	        logger.info("eos请求get_public_keys");
	        String public_key = coinUtils.goEOS(walletLink,"/v1/wallet/get_public_keys", ""); //["EOS6MRyAjQq8ud7hVNYcfnVPJqcVpscN5So8BhtHuGYqET5GDW5CV"]
	        logger.info("eos请求get_public_keys,返回"+public_key);
	        
	        String available_key = "{\"transaction\":"+tx 
	        						+ ",\"available_keys\":"+public_key 
	        						+"}";
	        
	        logger.info("eos请求get_required_keys，参数："+available_key );
	        String get_required_keys = coinUtils.goEOS(chainLink,"/v1/chain/get_required_keys", available_key);
	        logger.info("eos请求get_required_keys，返回："+get_required_keys );
	        
	        String sign_key = JSON.parseObject(get_required_keys).getString("required_keys"); //["EOS6MRyAjQq8ud7hVNYcfnVPJqcVpscN5So8BhtHuGYqET5GDW5CV"]
	        
	        //String  need_sign = "[" + tx + "," + sign_key +",\""+0000000000000000000000000000000000000000000000000000000000000000+"\"]";
	        String  need_sign = "[" + tx + "," + sign_key +",\"\"]";
	        if(getInfo.containsKey("chain_id") && getInfo.getString("chain_id") != null) {
	        	 need_sign = "[" + tx + "," + sign_key +",\""+getInfo.getString("chain_id")+"\"]";
	        }
	        
	        logger.info("eos请求sign_transaction，参数："+need_sign );
	        String sign_transaction = coinUtils.goEOS(walletLink,"/v1/wallet/sign_transaction", need_sign);
	        logger.info("eos请求sign_transaction，返回："+sign_transaction );
	        
	        walletLock();
	        JSONObject parseObject = JSON.parseObject(sign_transaction);
	        String  packed_trx ="{" 
								+ "\"compression\": \"none\", "
								+ "\"packed_context_free_data\": \"\","  
								+ "\"packed_trx\" : \""+ pack(parseObject) + "\"," 
								+ "\"signatures\" : "+ parseObject.getString("signatures") 
								+"}";
	        
	        
	        logger.info("eos请求push_transaction，参数："+packed_trx );
	        String push_transaction = coinUtils.goEOS(chainLink,"/v1/chain/push_transaction", packed_trx);
	        logger.info("eos请求push_transaction，返回："+push_transaction );
	        
	        JSONObject parseObject2 = JSON.parseObject(push_transaction);
	        
	        if(parseObject2.containsKey("transaction_id")) {
	        	return ReturnResult.SUCCESS(200, parseObject2.getString("transaction_id"));
	        }
    	} catch (ParseException e) {
    		logger.error("eos.sendToAddress异常,参数：{address："+ to +",amount:"+amount +",memo:"+memo +"}",e);
		}
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
