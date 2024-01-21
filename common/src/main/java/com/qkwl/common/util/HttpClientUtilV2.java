package com.qkwl.common.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.qkwl.common.http.MyX509TrustManager;

public class HttpClientUtilV2 {

	private static final Logger log = LoggerFactory.getLogger(HttpClientUtilV2.class);
	 
    /**
     * 向指定URL发送GET方法的请求
     * @param url   发送请求的URL
     * @param param 请求参数，请求参数应该是 name1=value1&name2=value2 的形式。
     * @return URL 所代表远程资源的响应结果
     */
    public static String sendGet(String url, String param) {
        String result = "";
        BufferedReader in = null;
        try {
            String urlNameString = url + "?" + param;
            URL realUrl = new URL(urlNameString);
            // 打开和URL之间的连接
            URLConnection connection = realUrl.openConnection();
            // 设置通用的请求属性
            connection.setRequestProperty("accept", "*/*");
            connection.setRequestProperty("connection", "Keep-Alive");
            connection.setRequestProperty("user-agent",
                    "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            // 建立实际的连接
            connection.connect();
            // 获取所有响应头字段
            Map<String, List<String>> map = connection.getHeaderFields();
            // 遍历所有的响应头字段
//            for (String key : map.keySet()) {
//                System.out.println(key + "--->" + map.get(key));
//            }
            // 定义 BufferedReader输入流来读取URL的响应
            in = new BufferedReader(new InputStreamReader(
                    connection.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
            log.error("发送GET请求出现异常！" + e);
            e.printStackTrace();
        }
        // 使用finally块来关闭输入流
        finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
        return result;
    }
    
    /* 
     * 处理https GET/POST请求 
     * 请求地址、请求方法、参数 
     * */ 
    @SuppressWarnings("static-access")
	public static String httpsRequest(String requestUrl, String requestMethod, Map<String, Object> paramMap){ 

	      String outputStr = "";
	      Iterator<String> it = paramMap.keySet().iterator();
	      while (it.hasNext()) {
	          String key = it.next();
	          outputStr += key + "=" + paramMap.get(key) + "&";
	      }
        
	      StringBuffer buffer=null; 
	      try{ 
	      //创建SSLContext 
	      SSLContext sslContext=SSLContext.getInstance("SSL"); 
	      TrustManager[] tm={new MyX509TrustManager()}; 
	      //初始化 
	      sslContext.init(null, tm, new java.security.SecureRandom());; 
	      //获取SSLSocketFactory对象 
	      SSLSocketFactory ssf=sslContext.getSocketFactory(); 
	      URL url=new URL(requestUrl); 
	      HttpsURLConnection conn=(HttpsURLConnection)url.openConnection(); 
	      conn.setDoOutput(true); 
	      conn.setDoInput(true); 
	      conn.setUseCaches(false); 
	      conn.setRequestMethod(requestMethod); 
	      //设置当前实例使用的SSLSoctetFactory 
	      conn.setSSLSocketFactory(ssf); 
	      conn.setDefaultHostnameVerifier(new javax.net.ssl.HostnameVerifier()  
	        {        
				@Override
				public boolean verify(String hostname, SSLSession session) {
					return true;
				}
	        });
	      conn.connect(); 
	      //往服务器端写内容 
	      if(null!=outputStr){ 
	        OutputStream os=conn.getOutputStream(); 
	        os.write(outputStr.getBytes("utf-8")); 
	        os.close(); 
	      } 
	      //读取服务器端返回的内容 
	      InputStream is=conn.getInputStream(); 
	      InputStreamReader isr=new InputStreamReader(is,"utf-8"); 
	      BufferedReader br=new BufferedReader(isr); 
	      buffer=new StringBuffer(); 
	      String line=null; 
	      while((line=br.readLine())!=null){ 
	        buffer.append(line); 
	      } 
	      }catch(Exception e){ 
	        e.printStackTrace(); 
	      } 
	      return buffer.toString(); 
    } 
 
    /**
     * 向指定 URL 发送POST方法的请求
     * @param url      发送请求的 URL
     * @param paramMap 请求参数
     * @return 所代表远程资源的响应结果
     */
    public static String sendPost(String url, Map<String, ?> paramMap) {
        PrintWriter out = null;
        BufferedReader in = null;
        String result = "";
        String param = "";
        Iterator<String> it = paramMap.keySet().iterator();
        while (it.hasNext()) {
            String key = it.next();
            param += key + "=" + paramMap.get(key) + "&";
        }
        try {
        	

        	//创建SSLContext 
	    	SSLContext sslContext=SSLContext.getInstance("SSL"); 
	    	TrustManager[] tm = {new MyX509TrustManager()}; 
	    	//初始化 
	    	sslContext.init(null, tm, new java.security.SecureRandom());; 
	    	//获取SSLSocketFactory对象 
	    	SSLSocketFactory ssf = sslContext.getSocketFactory(); 
        	
            URL realUrl = new URL(url);
            // 打开和URL之间的连接
            HttpsURLConnection conn = (HttpsURLConnection)realUrl.openConnection(); 
            conn.setDoOutput(true); 
            conn.setDoInput(true); 
            conn.setUseCaches(false); 
            conn.setRequestMethod("POST"); 
            //设置当前实例使用的SSLSoctetFactory 
            conn.setSSLSocketFactory(ssf); 
            conn.connect(); 
            // 获取URLConnection对象对应的输出流
            out = new PrintWriter(conn.getOutputStream());
            // 发送请求参数
            out.print(param);
            // flush输出流的缓冲
            out.flush();
            // 定义BufferedReader输入流来读取URL的响应
            in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        //使用finally块来关闭输出流、输入流
        finally {
            try {
                if (out != null) {
                    out.close();
                }
                if (in != null) {
                    in.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return result;
    }
}
