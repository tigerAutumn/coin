package com.qkwl.common.http;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;

import org.apache.http.conn.ssl.SSLConnectionSocketFactory;

/**
 * 修改过期类
 * @author ZKF
 */
//public class SSLSocketFactory extends SSLSocketFactory {
public class MySSLSocketFactory extends SSLConnectionSocketFactory {

	static {
		mySSLSocketFactory = new MySSLSocketFactory(createSContext());
	}

	private static MySSLSocketFactory mySSLSocketFactory = null;

	private static SSLContext createSContext() {
		SSLContext sslcontext = null;
		try {
			sslcontext = SSLContext.getInstance("SSL");
			sslcontext.init(null, new TrustManager[] { new MyX509TrustManager() }, null);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return sslcontext;
	}

	private MySSLSocketFactory(SSLContext sslContext) {
		super(sslContext);
		//this.setHostnameVerifier(ALLOW_ALL_HOSTNAME_VERIFIER);
	}

	public static MySSLSocketFactory getInstance() {
		if (mySSLSocketFactory != null) {
			return mySSLSocketFactory;
		} else {
			return mySSLSocketFactory = new MySSLSocketFactory(createSContext());
		}
	}
}