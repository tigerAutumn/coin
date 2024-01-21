package com.qkwl.common.util;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.security.cert.CertificateException;
import javax.security.cert.X509Certificate;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.ssl.TrustStrategy;
import org.springframework.web.multipart.MultipartFile;

import com.qkwl.common.http.MyX509TrustManager;

/**
 * http请求工具
 *
 */
public class HttpClientUtil {

	private final static String BOUNDARY = UUID.randomUUID().toString()
	      .toLowerCase().replaceAll("-", "");// 边界标识
	private final static String PREFIX = "--";// 必须存在
	private final static String LINE_END = "\r\n";

    public static String doGet(String httpurl) {
        HttpURLConnection connection = null;
        InputStream is = null;
        BufferedReader br = null;
        String result = null;// 返回结果字符串
        try {
            // 创建远程url连接对象
            URL url = new URL(httpurl);
            // 通过远程url连接对象打开一个连接，强转成httpURLConnection类
            connection = (HttpURLConnection) url.openConnection();
            // 设置连接方式：get
            connection.setRequestMethod("GET");
            // 设置连接主机服务器的超时时间：15000毫秒
            connection.setConnectTimeout(15000);
            // 设置读取远程返回的数据时间：60000毫秒
            connection.setReadTimeout(60000);
            // 发送请求
            connection.connect();
            // 通过connection连接，获取输入流
            if (connection.getResponseCode() == 200) {
                is = connection.getInputStream();
                // 封装输入流is，并指定字符集
                br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
                // 存放数据
                StringBuffer sbf = new StringBuffer();
                String temp = null;
                while ((temp = br.readLine()) != null) {
                    sbf.append(temp);
                    sbf.append("\r\n");
                }
                result = sbf.toString();
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // 关闭资源
            if (null != br) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (null != is) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            connection.disconnect();// 关闭远程连接
        }

        return result;
    }

    public static String doPost(String httpUrl, String param) {

        HttpURLConnection connection = null;
        InputStream is = null;
        OutputStream os = null;
        BufferedReader br = null;
        String result = null;
        try {
            URL url = new URL(httpUrl);
            // 通过远程url连接对象打开连接
            connection = (HttpURLConnection) url.openConnection();
            // 设置连接请求方式
            connection.setRequestMethod("POST");
            // 设置连接主机服务器超时时间：15000毫秒
            connection.setConnectTimeout(15000);
            // 设置读取主机服务器返回数据超时时间：60000毫秒
            connection.setReadTimeout(60000);

            // 默认值为：false，当向远程服务器传送数据/写数据时，需要设置为true
            connection.setDoOutput(true);
            // 默认值为：true，当前向远程服务读取数据时，设置为true，该参数可有可无
            connection.setDoInput(true);
            // 通过连接对象获取一个输出流
            os = connection.getOutputStream();
            // 通过输出流对象将参数写出去/传输出去,它是通过字节数组写出的
            os.write(param.getBytes());
            // 通过连接对象获取一个输入流，向远程读取
            if (connection.getResponseCode() == 200) {

                is = connection.getInputStream();
                br = new BufferedReader(new InputStreamReader(is, "UTF-8"));

                StringBuffer sbf = new StringBuffer();
                String temp = null;
                // 循环遍历一行一行读取数据
                while ((temp = br.readLine()) != null) {
                    sbf.append(temp);
                    sbf.append("\r\n");
                }
                result = sbf.toString();
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // 关闭资源
            if (null != br) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (null != os) {
                try {
                    os.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (null != is) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            // 断开与远程地址url的连接
            connection.disconnect();
        }
        return result;
    }
    
    public static String doPost(String url, Map<String, Object> paramMap) {

        //声明
//        ProtocolSocketFactory fcty = new MySecureProtocolSocketFactory();
//        //加入相关的https请求方式
//        Protocol.registerProtocol("https", new Protocol("https", fcty, 443));
        // 获取输入流
        InputStream is = null;
        BufferedReader br = null;
        String result = null;
        // 创建httpClient实例对象
        HttpClient httpClient = new HttpClient();
        // 设置httpClient连接主机服务器超时时间：15000毫秒
        httpClient.getHttpConnectionManager().getParams().setConnectionTimeout(15000);
        // 创建post请求方法实例对象
        PostMethod postMethod = new PostMethod(url);
        // 设置post请求超时时间
        postMethod.getParams().setParameter(HttpMethodParams.SO_TIMEOUT, 60000);

        NameValuePair[] nvp = null;
        // 判断参数map集合paramMap是否为空
        if (null != paramMap && paramMap.size() > 0) {// 不为空
            // 创建键值参数对象数组，大小为参数的个数
            nvp = new NameValuePair[paramMap.size()];
            // 循环遍历参数集合map
            Set<Entry<String, Object>> entrySet = paramMap.entrySet();
            // 获取迭代器
            Iterator<Entry<String, Object>> iterator = entrySet.iterator();

            int index = 0;
            while (iterator.hasNext()) {
                Entry<String, Object> mapEntry = iterator.next();
                // 从mapEntry中获取key和value创建键值对象存放到数组中
                try {
                    nvp[index] = new NameValuePair(mapEntry.getKey(),
                            new String(mapEntry.getValue().toString().getBytes("UTF-8"), "UTF-8"));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                index++;
            }
        }
        // 判断nvp数组是否为空
        if (null != nvp && nvp.length > 0) {
            // 将参数存放到requestBody对象中
            postMethod.setRequestBody(nvp);
        }
        // 执行POST方法
        try {
            int statusCode = httpClient.executeMethod(postMethod);
            // 判断是否成功
            if (statusCode != HttpStatus.SC_OK) {
                System.err.println("Method faild: " + postMethod.getStatusLine());
            }
            // 获取远程返回的数据
            is = postMethod.getResponseBodyAsStream();
            // 封装输入流
            br = new BufferedReader(new InputStreamReader(is, "UTF-8"));

            StringBuffer sbf = new StringBuffer();
            String temp = null;
            while ((temp = br.readLine()) != null) {
                sbf.append(temp).append("\r\n");
            }

            result = sbf.toString();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // 关闭资源
            if (null != br) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (null != is) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            // 释放连接
            postMethod.releaseConnection();
        }
        return result;
    }
    
    

	/**
     *  POST Multipart Request
     *  @Description:
     *  @param requestUrl 请求url
     *  @param requestText 请求参数
     *  @param requestFile 请求上传的文件
     *  @return
     *  @throws Exception
     */
    @SuppressWarnings("static-access")
	public static String uploadFileWithSSL(String requestUrl,
                                     Map<String, String> requestText, Map<String, MultipartFile> requestFile) throws Exception{
        HttpsURLConnection conn = null;
        InputStream input = null;
        OutputStream os = null;
        BufferedReader br = null;
        StringBuffer buffer = null;
        try {

	  	    //创建SSLContext 
	  	    SSLContext sslContext=SSLContext.getInstance("SSL"); 
	  	    TrustManager[] tm={new MyX509TrustManager()}; 
	  	    //初始化 
	  	    sslContext.init(null, tm, new java.security.SecureRandom());; 
	  	    //获取SSLSocketFactory对象 
	  	    SSLSocketFactory ssf = sslContext.getSocketFactory(); 
        	
            URL url = new URL(requestUrl);
            conn = (HttpsURLConnection) url.openConnection();

            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setUseCaches(false);
            conn.setConnectTimeout(1000 * 10);
            conn.setReadTimeout(1000 * 10);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "multipart/form-data");
            conn.setRequestProperty("Accept", "*/*");
            conn.setRequestProperty("Connection", "keep-alive");
            conn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");
            conn.setRequestProperty("Charset", "UTF-8");
            conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + BOUNDARY);
            conn.setSSLSocketFactory(ssf);
	  	    conn.setDefaultHostnameVerifier(new javax.net.ssl.HostnameVerifier()  
		    {        
				@Override
				public boolean verify(String hostname, SSLSession session) {
					return true;
				}
		    });
            conn.connect();

            // 往服务器端写内容 也就是发起http请求需要带的参数
            os = new DataOutputStream(conn.getOutputStream());
            // 请求参数部分
            writeParams(requestText, os);
            // 请求上传文件部分
            writeFile(requestFile, os);
            // 请求结束标志
            String endTarget = PREFIX + BOUNDARY + PREFIX + LINE_END;
            os.write(endTarget.getBytes());
            os.flush();

            // 读取服务器端返回的内容
            System.out.println("======================响应体=========================");
            System.out.println("ResponseCode:" + conn.getResponseCode()
                    + ",ResponseMessage:" + conn.getResponseMessage());
            if(conn.getResponseCode()==200){
                input = conn.getInputStream();
            }else{
                input = conn.getErrorStream();
            }

            br = new BufferedReader(new InputStreamReader( input, "UTF-8"));
            buffer = new StringBuffer();
            String line = null;
            while ((line = br.readLine()) != null) {
                buffer.append(line);
            }
            //......
            System.out.println("返回报文:" + buffer.toString());

        } catch (Exception e) {
            //log.error(e.getMessage(), e);
            throw new Exception(e);
        } finally {
            try {
                if (conn != null) {
                    conn.disconnect();
                    conn = null;
                }

                if (os != null) {
                    os.close();
                    os = null;
                }

                if (br != null) {
                    br.close();
                    br = null;
                }
            } catch (IOException ex) {
                //log.error(ex.getMessage(), ex);
                throw new Exception(ex);
            }
        }
        return buffer.toString();
    }


	/**
     *  POST Multipart Request
     *  @Description:
     *  @param requestUrl 请求url
     *  @param requestText 请求参数
     *  @param requestFile 请求上传的文件
     *  @return
     *  @throws Exception
     */
    public static String uploadFile(String requestUrl,
                                     Map<String, String> requestText, Map<String, MultipartFile> requestFile) throws Exception{
        HttpURLConnection conn = null;
        InputStream input = null;
        OutputStream os = null;
        BufferedReader br = null;
        StringBuffer buffer = null;
        try {
        	
            URL url = new URL(requestUrl);
            conn = (HttpURLConnection) url.openConnection();

            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setUseCaches(false);
            conn.setConnectTimeout(1000 * 10);
            conn.setReadTimeout(1000 * 10);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "multipart/form-data");
            conn.setRequestProperty("Accept", "*/*");
            conn.setRequestProperty("Connection", "keep-alive");
            conn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");
            conn.setRequestProperty("Charset", "UTF-8");
            conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + BOUNDARY);
            conn.connect();

            // 往服务器端写内容 也就是发起http请求需要带的参数
            os = new DataOutputStream(conn.getOutputStream());
            // 请求参数部分
            writeParams(requestText, os);
            // 请求上传文件部分
            writeFile(requestFile, os);
            // 请求结束标志
            String endTarget = PREFIX + BOUNDARY + PREFIX + LINE_END;
            os.write(endTarget.getBytes());
            os.flush();

            // 读取服务器端返回的内容
            System.out.println("======================响应体=========================");
            System.out.println("ResponseCode:" + conn.getResponseCode()
                    + ",ResponseMessage:" + conn.getResponseMessage());
            if(conn.getResponseCode()==200){
                input = conn.getInputStream();
            }else{
                input = conn.getErrorStream();
            }

            br = new BufferedReader(new InputStreamReader( input, "UTF-8"));
            buffer = new StringBuffer();
            String line = null;
            while ((line = br.readLine()) != null) {
                buffer.append(line);
            }
            //......
            System.out.println("返回报文:" + buffer.toString());

        } catch (Exception e) {
            //log.error(e.getMessage(), e);
            throw new Exception(e);
        } finally {
            try {
                if (conn != null) {
                    conn.disconnect();
                    conn = null;
                }

                if (os != null) {
                    os.close();
                    os = null;
                }

                if (br != null) {
                    br.close();
                    br = null;
                }
            } catch (IOException ex) {
                //log.error(ex.getMessage(), ex);
                throw new Exception(ex);
            }
        }
        return buffer.toString();
    }

    /**
     * 对post参数进行编码处理并写入数据流中
     * @throws Exception
     *
     * @throws IOException
     *
     * */
    private static void writeParams(Map<String, String> requestText,
                                    OutputStream os) throws Exception {
        try{
            String msg = "请求参数部分:\n";
            if (requestText == null || requestText.isEmpty()) {
                msg += "空";
            } else {
                StringBuilder requestParams = new StringBuilder();
                Set<Map.Entry<String, String>> set = requestText.entrySet();
                Iterator<Map.Entry<String, String>> it = set.iterator();
                while (it.hasNext()) {
                    Map.Entry<String, String> entry = it.next();
                    requestParams.append(PREFIX).append(BOUNDARY).append(LINE_END);
                    requestParams.append("Content-Disposition: form-data; name=\"")
                            .append(entry.getKey()).append("\"").append(LINE_END);
                    requestParams.append("Content-Type: text/plain; charset=utf-8")
                            .append(LINE_END);
                    requestParams.append("Content-Transfer-Encoding: 8bit").append(
                            LINE_END);
                    requestParams.append(LINE_END);// 参数头设置完以后需要两个换行，然后才是参数内容
                    requestParams.append(entry.getValue());
                    requestParams.append(LINE_END);
                }
                os.write(requestParams.toString().getBytes());
                os.flush();

                msg += requestParams.toString();
            }

            //System.out.println(msg);
        }catch(Exception e){
            //log.error("writeParams failed", e);
            throw new Exception(e);
        }
    }

    /**
     * 对post上传的文件进行编码处理并写入数据流中
     *
     * @throws IOException
     *
     * */
    private static void writeFile(Map<String, MultipartFile> requestFile,
                                  OutputStream os) throws Exception {
        InputStream is = null;
        try{
            String msg = "请求上传文件部分:\n";
            if (requestFile == null || requestFile.isEmpty()) {
                msg += "空";
            } else {
                StringBuilder requestParams = new StringBuilder();
                Set<Map.Entry<String, MultipartFile>> set = requestFile.entrySet();
                Iterator<Map.Entry<String, MultipartFile>> it = set.iterator();
                while (it.hasNext()) {
                    Map.Entry<String, MultipartFile> entry = it.next();
                    if(entry.getValue() == null){//剔除value为空的键值对
                        continue;
                    }
                    requestParams.append(PREFIX).append(BOUNDARY).append(LINE_END);
                    requestParams.append("Content-Disposition: form-data; name=\"")
                            .append(entry.getKey()).append("\"; filename=\"")
                            .append(entry.getValue().getName()).append("\"")
                            .append(LINE_END);
                    requestParams.append("Content-Type:")
                            .append(entry.getValue().getContentType())
                            .append(LINE_END);
                    requestParams.append("Content-Transfer-Encoding: 8bit").append(
                            LINE_END);
                    requestParams.append(LINE_END);// 参数头设置完以后需要两个换行，然后才是参数内容

                    os.write(requestParams.toString().getBytes());
                    os.write(entry.getValue().getBytes());

                    os.write(LINE_END.getBytes());
                    os.flush();

                    msg += requestParams.toString();
                }
            }
            //System.out.println(msg);
        }catch(Exception e){
            //log.error("writeFile failed", e);
            throw new Exception(e);
        }finally{
            try{
                if(is!=null){
                    is.close();
                }
            }catch(Exception e){
                //log.error("writeFile FileInputStream close failed", e);
                throw new Exception(e);
            }
        }
    }
}