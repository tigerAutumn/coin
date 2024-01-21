package com.qkwl.common.coin;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.aliyun.openservices.shade.io.netty.util.internal.StringUtil;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.net.PasswordAuthentication;
import java.net.URL;
import java.nio.charset.Charset;

public class CoinUtils {

	private static final Logger logger =  LoggerFactory.getLogger(CoinUtils.class);

	// 用户名
	private String accessKey = null;
	// 密码
	private String secretKey = null;
	// 钱包
	private String walletLink = null;
	// 节点
	private String chainLink = null;
	// 加密
	private String HMAC_SHA1_ALGORITHM = "HmacSHA1";
	// Wei
	private static final String ETHWEI = "1000000000000000000";
    public static final String ETHWEI_8 = "100000000";

	public CoinUtils(String accessKey,String secretKey, String walletLink, String chainLink) {
		this.accessKey = accessKey;
		this.secretKey = secretKey;
		this.walletLink = walletLink;
		this.chainLink = chainLink;
	}
	
	public CoinUtils(String walletLink, String chainLink) {
		this.walletLink = walletLink;
		this.chainLink = chainLink;
	}
	
	public static String methodParam(Object... params) {
        StringBuffer strBuffer = new StringBuffer();
        for (int i = 0; i < params.length; i++) {
            if (i == 0) {
            	if(params[i] instanceof String) {
            		strBuffer.append("\"" + params[i] + "\"");
            	}else{
            		strBuffer.append( params[i] + "");
            	}
            } else {
            	if(params[i] instanceof String) {
            		strBuffer.append(",\"" + params[i] + "\"");
            	}else {
            		strBuffer.append("," + params[i] + "");
            	}
            }

        }
        return strBuffer.toString();
    }

	public JSONObject go(String method, String condition) {
		try {
			String result = "";
			String tonce = "" + (System.currentTimeMillis() * 1000);
			authenticator();

			String params = "tonce=" + tonce.toString() + "&accesskey=" + accessKey + "&requestmethod=post&id=1&method=" + method + "&params=" + condition;

			String hash = getSignature(params, secretKey);

			String url = "http://" + accessKey + ":" + secretKey + "@" + walletLink;
			URL obj = new URL(url);
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();
			String userpass = accessKey + ":" + hash;
			String basicAuth = "Basic " + DatatypeConverter.printBase64Binary(userpass.getBytes());

			// add reuqest header
			con.setRequestMethod("POST");
			con.setRequestProperty("Json-Rpc-Tonce", tonce.toString());
			con.setRequestProperty("Authorization", basicAuth);
			
			//设置connection timeout为3秒
			con.setConnectTimeout(3 * 1000);
			//设置read timeout为5秒
			con.setReadTimeout(10 * 1000);

			String postdata = "{\"method\":\"" + method + "\", \"params\":" + condition + ", \"id\": 1}";
			// Send post request
			con.setDoOutput(true);
			DataOutputStream wr = new DataOutputStream(con.getOutputStream());
			wr.writeBytes(postdata);
			wr.flush();
			wr.close();
			int responseCode = con.getResponseCode();
			if (responseCode != 200) {
				logger.error("Coin go failed : {}_{}", responseCode,  postdata);
				return JSON.parseObject("{\"result\":null,\"error\":" + responseCode + ",\"id\":2}");
			}
			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();
			inputLine = in.readLine();
			response.append(inputLine);
			in.close();
			result = response.toString();
			return JSON.parseObject(result);
			
			/*StringWriter writer = new StringWriter();
            char[] chars = new char[1024];
            int count = 0;
            while ((count = in.read(chars)) > 0) {
                writer.write(chars, 0, count);
            }
            return JSON.parseObject(writer.toString());*/
		} catch (Exception e) {
			logger.error("Coin go error {}，{} ", method, condition, e);
			return JSON.parseObject("{\"result\":null,\"error\":\""+e.getMessage()+"\",\"id\":3}");
		}
	}
	
	
	public String goPost(String url ,String postData) {
		try {
			String result = "";
			URL obj = new URL(url);
			authenticator();
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();
			con.addRequestProperty("Content-Type","application/json;charset=UTF-8");
			con.setRequestMethod("POST");
			con.setConnectTimeout(3 * 1000);
			con.setReadTimeout(10 * 1000);
			authenticator();
			/*if(!StringUtils.isEmpty(accessKey) && !StringUtils.isEmpty(secretKey)) {
				String tonce = "" + (System.currentTimeMillis() * 1000);
				String params = "tonce=" + tonce.toString() + "&accesskey=" + accessKey + "&requestmethod=post&id=1&method=" + method + "&params=" + condition;
				String hash = getSignature(params, secretKey);
				String userpass = accessKey + ":" + hash;
				String basicAuth = "Basic " + DatatypeConverter.printBase64Binary(userpass.getBytes());
				con.setRequestProperty("Authorization", basicAuth);
				con.setRequestProperty("Json-Rpc-Tonce", tonce.toString());
			}*/
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
			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			/*String inputLine;
			StringBuffer response = new StringBuffer();
			inputLine = in.readLine();
			response.append(inputLine);
			in.close();
			result = response.toString();
			return result;*/
			StringWriter writer = new StringWriter();
            char[] chars = new char[1024];
            int count = 0;
            while ((count = in.read(chars)) > 0) {
                writer.write(chars, 0, count);
            }
            in.close();
            return writer.toString();
		} catch (Exception e) {
			logger.error("Coin goPost error {}，{} ", url, postData, e);
			return null;
		}
	}
	
	public String goGet(String url) {
		try {
			String result = "";
			URL obj = new URL(url);
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();
			con.setRequestMethod("GET");
			con.setConnectTimeout(30 * 1000);
			con.setReadTimeout(60 * 1000);
			/*con.setDoOutput(true);
			DataOutputStream wr = new DataOutputStream(con.getOutputStream());
			wr.flush();
			wr.close();*/
			int responseCode = con.getResponseCode();
			if (responseCode != 200) {
				logger.error("Coin goGet failed : {}", responseCode);
				return null;
			}
			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			/*String inputLine;
			StringBuffer response = new StringBuffer();
			inputLine = in.readLine();
			response.append(inputLine);
			in.close();
			result = response.toString();
			return result;*/
			StringWriter writer = new StringWriter();
            char[] chars = new char[1024];
            int count = 0;
            while ((count = in.read(chars)) > 0) {
                writer.write(chars, 0, count);
            }
            in.close();
            String string = writer.toString();
            
            //logger.info("request:"+url + "return:" + string);
            
            return string;
		} catch (Exception e) {
			logger.error("Coin goGet error {} ", url, e);
			return null;
		}
	}
	
	public JSONObject goWICC(String method, String condition) {
		try {
			String result = "";
			String tonce = "" + (System.currentTimeMillis() * 1000);
			authenticator();

			String params = "tonce=" + tonce.toString() + "&accesskey=" + accessKey + "&requestmethod=post&id=1&method=" + method + "&params=" + condition;

			String hash = getSignature(params, secretKey);

			String url = "http://" + accessKey + ":" + secretKey + "@" + walletLink;
			URL obj = new URL(url);
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();
			String userpass = accessKey + ":" + hash;
			String basicAuth = "Basic " + DatatypeConverter.printBase64Binary(userpass.getBytes());

			// add reuqest header
			con.setRequestMethod("POST");
			con.setRequestProperty("Json-Rpc-Tonce", tonce.toString());
			con.setRequestProperty("Authorization", basicAuth);

			//设置connection timeout为3秒
			con.setConnectTimeout(3 * 1000);
			//设置read timeout为5秒
			con.setReadTimeout(10 * 1000);
			
			String postdata = "{\"method\":\"" + method + "\", \"params\":" + condition + ", \"id\": 1}";
			// Send post request
			con.setDoOutput(true);
			DataOutputStream wr = new DataOutputStream(con.getOutputStream());
			wr.writeBytes(postdata);
			wr.flush();
			wr.close();
			int responseCode = con.getResponseCode();
			if (responseCode != 200) {
				logger.error("Coin go failed : {}_{}", responseCode,  postdata);
				return JSON.parseObject("{\"result\":null,\"error\":" + responseCode + ",\"id\":2}");
			}
			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();
			inputLine = in.readLine();
			response.append(inputLine);
			in.close();
			result = response.toString();
			return JSON.parseObject(result);
		} catch (Exception e) {
			logger.error("Coin go error {}，{} ", method, condition, e);
			return JSON.parseObject("{\"result\":\"null\",\"error\":null,\"id\":3}");
		}
	}
	
	public JSONObject goETC(String method, String params) {
		try {
			String result = "";
			String url = "http://" + walletLink;
			URL obj = new URL(url);
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();

			// add reuqest header
			con.addRequestProperty("Content-Type","application/json");
			con.setRequestMethod("POST");
			
			//设置connection timeout为3秒
			con.setConnectTimeout(3 * 1000);
			//设置read timeout为5秒
			con.setReadTimeout(10 * 1000);
			
			String postdata = "{\"method\":\"" + method + "\", \"params\":" + params + ", \"id\": 1" + ", \"jsonrpc\": \"2.0\"}";

			//System.out.println("postdata:"+postdata);

			// Send post request
			con.setDoOutput(true);
			DataOutputStream wr = new DataOutputStream(con.getOutputStream());
			wr.writeBytes(postdata);
			wr.flush();
			wr.close();
			int responseCode = con.getResponseCode();
			if (responseCode != 200) {
				logger.error("Coin goETC failed : {}_{}", responseCode,  postdata);
				return JSON.parseObject("{\"result\":\"null\",\"error\":" + responseCode + ",\"id\":2}");
			}
			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();
			inputLine = in.readLine();
			response.append(inputLine);
			in.close();
			result = response.toString();
			return JSON.parseObject(result);
		} catch (Exception e) {
			logger.error("Coin goETC error {}，{} ", method, params, e);
			return JSON.parseObject("{\"result\":\"null\",\"error\":null,\"id\":3}");
		}
	}
	
	public JSONObject goETH(String method, String params) {
		try {
			String result = "";
			String url = "http://" + walletLink;
			URL obj = new URL(url);
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();
			con.addRequestProperty("Content-Type","application/json");
			con.setRequestMethod("POST");
			
			//设置connection timeout为3秒
			con.setConnectTimeout(3 * 1000);
			//设置read timeout为30秒
			con.setReadTimeout(30 * 1000);
			
			String postdata = "{\"method\":\"" + method + "\", \"params\":" + params + ", \"id\": 1, \"jsonrpc\": \"2.0\"}";
			con.setDoOutput(true);
			DataOutputStream wr = new DataOutputStream(con.getOutputStream());
			wr.writeBytes(postdata);
			wr.flush();
			wr.close();
			int responseCode = con.getResponseCode();
			if (responseCode != 200) {
				logger.error("Coin goETH failed : {}_{}", responseCode,  postdata);
				return JSON.parseObject("{\"result\":\"null\",\"error\":" + responseCode + ",\"id\":2}");
			}
			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			StringWriter writer = new StringWriter();
            char[] chars = new char[1024];
            int count = 0;
            while ((count = in.read(chars)) > 0) {
                writer.write(chars, 0, count);
            }
            String string = writer.toString();
            in.close();
			return JSON.parseObject(string);
		} catch (Exception e) {
			logger.error("Coin goETH error {}，{} ", method, params, e);
			return JSON.parseObject("{\"result\":\"null\",\"error\":null,\"id\":3}");
		}
	}
	
	public JSONObject goMOAC(String method, String params) {
		try {
			String result = "";
			String url = "http://" + walletLink;
			URL obj = new URL(url);
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();
			con.addRequestProperty("Content-Type","application/json");
			con.setRequestMethod("POST");
			
			//设置connection timeout为3秒
			con.setConnectTimeout(3 * 1000);
			//设置read timeout为5秒
			con.setReadTimeout(10 * 1000);
			
			String postdata = "{\"method\":\"" + method + "\", \"params\":" + params + ", \"id\": 1}";
			con.setDoOutput(true);
			DataOutputStream wr = new DataOutputStream(con.getOutputStream());
			wr.writeBytes(postdata);
			wr.flush();
			wr.close();
			int responseCode = con.getResponseCode();
			if (responseCode != 200) {
				logger.error("Coin goETH failed : {}_{}", responseCode,  postdata);
				return JSON.parseObject("{\"result\":\"null\",\"error\":" + responseCode + ",\"id\":2}");
			}
			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();
			inputLine = in.readLine();
			response.append(inputLine);
			in.close();
			result = response.toString();
			return JSON.parseObject(result);
		} catch (Exception e) {
			logger.error("Coin goETH error {}，{} ", method, params, e);
			return JSON.parseObject("{\"result\":\"null\",\"error\":null,\"id\":3}");
		}
	}
	
	public JSONObject goFOD(String method, String params) {
		try {
			String result = "";
			String url = "http://" + walletLink;
			URL obj = new URL(url);
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();
			con.addRequestProperty("Content-Type","application/json");
			con.setRequestMethod("POST");
			
			//设置connection timeout为3秒
			con.setConnectTimeout(3 * 1000);
			//设置read timeout为5秒
			con.setReadTimeout(10 * 1000);
			
			String postdata = "{\"method\":\"" + method + "\", \"params\":" + params + ", \"id\": 1}";
			con.setDoOutput(true);
			DataOutputStream wr = new DataOutputStream(con.getOutputStream());
			wr.writeBytes(postdata);
			wr.flush();
			wr.close();
			int responseCode = con.getResponseCode();
			if (responseCode != 200) {
				logger.error("Coin goETH failed : {}_{}", responseCode,  postdata);
				return JSON.parseObject("{\"result\":\"null\",\"error\":" + responseCode + ",\"id\":2}");
			}
			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();
			inputLine = in.readLine();
			response.append(inputLine);
			in.close();
			result = response.toString();
			return JSON.parseObject(result);
		} catch (Exception e) {
			logger.error("Coin goETH error {}，{} ", method, params, e);
			return JSON.parseObject("{\"result\":\"null\",\"error\":null,\"id\":3}");
		}
	}

	public JSONObject goETP(String method, String params) {
		try {
			String result = "";
			String url = "http://" + walletLink + "/rpc";
			URL obj = new URL(url);
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();

			// add reuqest header
			con.setRequestMethod("POST");
			
			//设置connection timeout为3秒
			con.setConnectTimeout(3 * 1000);
			//设置read timeout为5秒
			con.setReadTimeout(10 * 1000);
			
			String postdata = "{\"method\":\"" + method + "\", \"params\":" + params + "}";

			// Send post request
			con.setDoOutput(true);
			DataOutputStream wr = new DataOutputStream(con.getOutputStream());
			wr.writeBytes(postdata);
			wr.flush();
			wr.close();

			int responseCode = con.getResponseCode();

			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
			in.close();
			result = response.toString();

			if (responseCode != 200) {
				logger.error("Coin goETP failed : {}_{}", responseCode,  postdata);
				return JSON.parseObject("{\"result\":\"null\",\"error\":" + responseCode + ",\"id\":2}");
			}

            if (method.equals("getnewaddress") || method.equals("fetch-height")) {
                return JSON.parseObject("{\"result\":\"" + result + "\"}");
            }

			return JSON.parseObject(result);
		} catch (Exception e) {
			logger.error("Coin goETP error {}，{} ", method, params, e);
			return JSON.parseObject("{\"result\":\"null\",\"error\":null,\"id\":3}");
		}
	}

	
	public JSONObject goUSDT(String method, String params) {
		try {
			String result = "";
			String tonce = "" + (System.currentTimeMillis() * 1000);
			authenticator();
			//String params = "tonce=" + tonce.toString() + "&accesskey=" + accessKey + "&requestmethod=post&id=1&method=" + method + "&params=" + condition;
			//String hash = getSignature(params, secretKey);
			String url = "http://" + accessKey + ":" + secretKey + "@" +walletLink;
			URL obj = new URL(url);
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();
			// add reuqest header
			con.addRequestProperty("Content-Type","application/json");
			con.setRequestMethod("POST");
			
			//设置connection timeout为3秒
			con.setConnectTimeout(9 * 1000);
			//设置read timeout为5秒
			con.setReadTimeout(30 * 1000);
			
			
			String postdata = "{\"method\":\"" + method + "\", \"params\":" + params + ", \"id\": 1" + ", \"jsonrpc\": \"2.0\"}";
			// Send post request
			con.setDoOutput(true);
			DataOutputStream wr = new DataOutputStream(con.getOutputStream());
			wr.writeBytes(postdata);
			wr.flush();
			wr.close();
			int responseCode = con.getResponseCode();
			if (responseCode != 200) {
				logger.error("Coin goUSDT failed : {}_{}_{}", responseCode,  params, postdata);
				return JSON.parseObject("{\"result\":null,\"error\":" + responseCode + ",\"id\":2}");
			}
			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();
			inputLine = in.readLine();
			response.append(inputLine);
			in.close();
			result = response.toString();
			return JSON.parseObject(result);
		} catch (Exception e) {
			logger.error("Coin goUSDT error {}，{} ", method, params, e);
			return new JSONObject();
		}
	}

	public String goEOS(String methodnode,String method, String params) {
		try {
			String result = "";
			authenticator();
			String url = "http://" + methodnode + method;
			URL obj = new URL(url);
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();
			con.addRequestProperty("Content-Type","application/json;charset=UTF-8");
			//con.setRequestProperty("Charsert", "UTF-8");
			con.setRequestMethod("POST");
			
			//设置connection timeout为3秒
			con.setConnectTimeout(3 * 1000);
			//设置read timeout为5秒
			con.setReadTimeout(10 * 1000);
			
			con.setDoOutput(true);
			DataOutputStream wr = new DataOutputStream(con.getOutputStream());
			//wr.writeBytes(params);
			wr.write(params.getBytes("UTF-8"));
			wr.flush();
			wr.close();
			int responseCode = con.getResponseCode();
			if (responseCode < 200 || responseCode >= 300) {
				logger.error("Coin goEOS failed : {}_{}", responseCode,  params);
				return null;
			}
			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream(),"UTF-8"));
			String inputLine;
			StringBuffer response = new StringBuffer();
			inputLine = in.readLine();
			response.append(inputLine);
			in.close();
			result = response.toString();
			return result;
		} catch (Exception e) {
			logger.error("Coin goEOS error {}，{} ", method, params, e);
			return null;
		}
	}
	
	public JSONObject goGXS(String method, String params) {
		try {
			String result = "";
			String url = "http://" + walletLink + "/rpc";
			URL obj = new URL(url);
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();

			// add reuqest header
			con.setRequestMethod("POST");
			String postdata = "{\"method\":\"" + method + "\", \"params\":" + params + ", \"id\": 1}";

			// Send post request
			con.setDoOutput(true);
			
			
			//设置connection timeout为3秒
			con.setConnectTimeout(3 * 1000);
			//设置read timeout为5秒
			con.setReadTimeout(10 * 1000);
			
			DataOutputStream wr = new DataOutputStream(con.getOutputStream());
			wr.writeBytes(postdata);
			wr.flush();
			wr.close();

			int responseCode = con.getResponseCode();

			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();

			inputLine = in.readLine();
			response.append(inputLine);
			in.close();
			result = response.toString();

			if (responseCode != 200) {
				logger.error("Coin goGXS failed : {}_{}", responseCode,  result);
				return JSON.parseObject("{\"result\":\"null\",\"error\":" + responseCode + ",\"id\":2}");
			}

			if (method.equals("getnewaddress") || method.equals("fetch-height")) {
				return JSON.parseObject("{\"result\":\"" + result + "\"}");
			}

			return JSON.parseObject(result);
		} catch (Exception e) {
			logger.error("Coin goGXS error {}，{} ", method, params, e);
			return JSON.parseObject("{\"result\":\"null\",\"error\":null,\"id\":3}");
		}
	}
	
	private String getSignature(String data, String key) throws Exception {
		// get an hmac_sha1 key from the raw key bytes
		SecretKeySpec signingKey = new SecretKeySpec(key.getBytes(), HMAC_SHA1_ALGORITHM);

		// get an hmac_sha1 Mac instance and initialize with the signing key
		Mac mac = Mac.getInstance(HMAC_SHA1_ALGORITHM);
		mac.init(signingKey);

		// compute the hmac on input data bytes
		byte[] rawHmac = mac.doFinal(data.getBytes());
		return bytArrayToHex(rawHmac);
	}

	private String bytArrayToHex(byte[] a) {
		StringBuilder sb = new StringBuilder();
		for (byte b : a) {
			sb.append(String.format("%02x", b & 0xff));
		}
		return sb.toString();
	}

	// The easiest way to tell Java to use HTTP Basic authentication is to set a default Authenticator:
	private void authenticator() {
		if(!StringUtils.isEmpty(accessKey) && !StringUtils.isEmpty(secretKey)) {
			try {
				Authenticator.setDefault(new Authenticator() {
					@Override
					protected PasswordAuthentication getPasswordAuthentication() {
						return new PasswordAuthentication(accessKey, secretKey.toCharArray());
					}
				});
			} catch (Exception e) {
				logger.error("授权失败");
			}
		}
	}

	/**
	 * 大数:16进制转string
	 * @return
	 */
	public static String BigHexToString(String bigHex) {
		BigInteger bigInteger = new BigInteger(bigHex.substring(2), 16);
		return bigInteger.toString();
	}
	
	/**
	 * ETH余额:16进制 换算为10进制2位小数
	 * @param hexBalance
	 * @return
	 */
	public static String ETHBalanceHexToStr(String hexBalance) {
		BigInteger hexBalanceTmp = new BigInteger(hexBalance.substring(2), 16);
		BigDecimal balance = new BigDecimal(hexBalanceTmp.toString());
		BigDecimal balanceWei = new BigDecimal(ETHWEI);
		BigDecimal result = balance.divide(balanceWei,4,RoundingMode.DOWN);
		return result.toString();
	}
	

	/**
	 * ETH余额:16进制 换算为10进制2位小数
	 * @param hexBalance
	 * @return
	 */
	public static BigDecimal HexToBigDecimal(String hexBalance, int wei) {
		BigInteger hexBalanceTmp = new BigInteger(hexBalance.substring(2), 16);
		BigDecimal balance = new BigDecimal(hexBalanceTmp.toString());
		BigDecimal balanceWei =BigDecimal.valueOf((Math.pow(10L, wei)));
		BigDecimal result = balance.divide(balanceWei,4,RoundingMode.DOWN);
		return result;
	}
	
	
	/**
	 * ETH余额:16进制 换算为10进制2位小数
	 * @param hexBalance
	 * @return
	 */
	public static String ETHBalanceHexToStr(String hexBalance, int wei) {
		BigInteger hexBalanceTmp = new BigInteger(hexBalance.substring(2), 16);
		BigDecimal balance = new BigDecimal(hexBalanceTmp.toString());
		BigDecimal balanceWei = BigDecimal.valueOf(Math.pow(10L, wei));
		BigDecimal result = balance.divide(balanceWei,4,RoundingMode.DOWN);
		return result.toString();
	}
	
	/**
	 * ETH余额:10进制2位小数换算位16进制
	 * @param Balance
	 * @return
	 */
	public static String ToETHBalanceHex(String Balance) {
		BigDecimal balance = new BigDecimal(Balance);
		BigDecimal balanceWei = new BigDecimal(ETHWEI);
		BigInteger hexBalanceTmp = balance.multiply(balanceWei).toBigInteger();
		String resultStr = "0x" + hexBalanceTmp.toString(16);
		return resultStr;
	}

	/**
	 * ETH余额:10进制2位小数换算位16进制
	 * @param Balance
	 * @return
	 */
	public static String CntractToETHBalanceHex(String Balance, int wei) {
		BigDecimal balance = new BigDecimal(Balance);
		BigInteger hexBalanceTmp;
		if (wei == 0) {
			hexBalanceTmp = balance.toBigInteger();
		} else {
			BigDecimal balanceWei = BigDecimal.valueOf(Math.pow(10L, wei));
			hexBalanceTmp = balance.multiply(balanceWei).toBigInteger();
		}
        String resultStr = StringUtils.leftPad(hexBalanceTmp.toString(16), 64, "0");
		return resultStr;
	}
	
	public static void main(String[] args) {
		System.out.println(ToETHBalanceHex("9.99"));
	}
}
