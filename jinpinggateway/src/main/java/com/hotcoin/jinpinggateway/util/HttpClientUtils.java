package com.hotcoin.jinpinggateway.util;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 *
 * @author huangjinfeng
 */
public class HttpClientUtils {
	
	private static final  int CONNECT_TIMEOUT=2000;
	
	private static final int READ_TIMEOUT=2000;

	public static String send(String urlStr, String method, String content,	Map<String, String> headers) {
		URL url = null;
		HttpURLConnection connection = null;
		try {
			url = new URL(urlStr);
			// 新建连接实例
			connection = (HttpURLConnection) url.openConnection();
			if (headers != null) {
				for (Map.Entry<String,String> entry : headers.entrySet()) {
					connection.setRequestProperty(entry.getKey(), entry.getValue());
				}
			}
			// 设置连接超时时间，单位毫秒
			connection.setConnectTimeout(CONNECT_TIMEOUT);
			// 设置读取数据超时时间，单位毫秒
			connection.setReadTimeout(READ_TIMEOUT);
			// 是否打开输出流 true|false
			connection.setDoOutput(true);
			// 是否打开输入流true|false
			connection.setDoInput(true);
			// 提交方法POST|GET
			connection.setRequestMethod(method);
			// 是否缓存true|false
			connection.setUseCaches(false);
			// 打开连接端口
			connection.connect();
			// 打开输出流往对端服务器写数据
			DataOutputStream out = new DataOutputStream(connection.getOutputStream());
			// 写数据,也就是提交你的表单 name=xxx&pwd=xxx
			out.write(Optional.ofNullable(content).orElse("").getBytes(StandardCharsets.UTF_8));
			// 刷新
			out.flush();
			// 关闭输出流
			out.close();
			// 往对端写完数据对端服务器返回数据 ,以BufferedReader流来读取
			BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(),StandardCharsets.UTF_8));
			StringBuilder buffer = new StringBuilder();
			String line = "";
			while ((line = reader.readLine()) != null) {
				buffer.append(line);
			}
			reader.close();
			return buffer.toString();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (connection != null) {
				connection.disconnect();// 关闭连接
			}
		}
		return null;
	}
	
	public static String post(String urlStr, String content,Map<String,String> headers) {
		return send(urlStr, "POST", content, headers);
	}
	
	public static String get(String urlStr, String content,Map<String,String> headers) {
		return send(String.format("%s?%s",urlStr,content), "GET", null, headers);
	}
	
	public static String get(String urlStr, String content) {
		return get(urlStr, content, null);
	}
	
	public static String get(String urlStr,Map<String,String> param) {
		return get(urlStr,param, null);
	}
	
	public static String get(String urlStr,Map<String,String> param,Map<String,String> headers) {
		StringBuilder sb=new StringBuilder();
		if(param!=null) {
			try {
				for(Map.Entry<String, String> entry:param.entrySet()) {
					sb.append(URLEncoder.encode(entry.getKey(), "UTF-8")).append("=").append(URLEncoder.encode(entry.getValue(), "UTF-8")).append("&");
				}
			} catch (UnsupportedEncodingException e) {
				throw new RuntimeException("Unsupported encoding",e);
			}
		}
		return get(urlStr, sb.toString(), headers);
	}
	
	public static String postJSON(String urlStr, String content) {
		return postJSON(urlStr, content, null);
	}
	
	public static String postJSON(String urlStr, String content,Map<String,String> headers) {
		if(headers==null) {
			headers=new HashMap<>(0);
		}
		
		headers.put("Content-type", "application/json");
		return send(urlStr, "POST", content, headers);
	}

	public static void main(String[] args) {
		String result = postJSON("http://192.168.16.100:10000/admin/api/security/login.do","{\"loginName\":\"admin\",\"password\":\"c7122a1349c22cb3c009da3613d242ab\"}");
		System.out.println(result);
	}

}