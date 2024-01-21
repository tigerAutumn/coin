package com.qkwl.common.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.alibaba.fastjson.JSON;


/**
 * 
 * @author huangjinfeng
 */
public class HttpRequestUtils {
	
	private final static Logger logger = LoggerFactory.getLogger(HttpRequestUtils.class); 

	public static boolean isAjax(ServletRequest request) {
		String header = ((HttpServletRequest) request).getHeader("X-Requested-With");
		boolean isAjax = "XMLHttpRequest".equals(header) ? true : false;
		return isAjax;
	}

	public static boolean isJsonContent(ServletRequest request) {
		String contentType = ((HttpServletRequest) request).getHeader("Content-type");
		if (StringUtils.isNotBlank(contentType)) {
			if (StringUtils.indexOfIgnoreCase(contentType, "application/json") >= 0) {
				return true;
			}
		}
		return false;
	}

	public static boolean isHTML(ServletRequest request) {
		HttpServletRequest req = (HttpServletRequest) request;
		String servletPath = req.getServletPath();
		String contextType = req.getContentType();
		String method = req.getMethod();
		return isHTML(servletPath, method, contextType);
	}

	public static boolean isHTML(String servletPath, String method, String contextType) {
		String urlExtension = StringUtils.substringAfterLast(servletPath, ".");
		return ("HTML".equalsIgnoreCase(urlExtension) || "HTM".equalsIgnoreCase(urlExtension))
				&& "GET".equalsIgnoreCase(method) && StringUtils.containsIgnoreCase(contextType, "text/html");
	}

	public static String readJsonReq(ServletRequest request) {
		BufferedReader br = null;
		try {
			br = new BufferedReader(new InputStreamReader(request.getInputStream(), "utf-8"));
			StringBuilder sb = new StringBuilder();
			String temp;
			while ((temp = br.readLine()) != null) {
				sb.append(temp);
			}
			return sb.toString();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return "";
	}

	public static void writeJsonResp(Object obj, ServletResponse response) {
		HttpServletResponse httpResponse = (HttpServletResponse) response;
		httpResponse.setCharacterEncoding("UTF-8");
		httpResponse.setContentType("application/json; charset=utf-8");
		httpResponse.setStatus(200);

		PrintWriter writer = null;
		try {
			writer = response.getWriter();
			writer.write(JSON.toJSONString(obj));
			writer.flush();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (writer != null) {
				writer.close();
			}
		}
	}

	public static void writeJsonResp(Object obj, ServletResponse response, int httpStatusCode) {
		HttpServletResponse httpResponse = (HttpServletResponse) response;
		httpResponse.setCharacterEncoding("UTF-8");
		httpResponse.setContentType("application/json; charset=utf-8");
		httpResponse.setStatus(httpStatusCode);

		PrintWriter writer = null;
		try {
			writer = response.getWriter();
			writer.write(JSON.toJSONString(obj));
			writer.flush();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (writer != null) {
				writer.close();
			}
		}
	}
	
	public static String getIPAddress() {
		HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
		return getIPAddress(request);
	}
	
	
    public static String getIPAddress(HttpServletRequest request) {
      String ip = null;

      String ipAddresses = request.getHeader("HTTP_X_CONNECTING_IP");
      if (ipAddresses == null || ipAddresses.length() == 0 || "unknown".equalsIgnoreCase(ipAddresses)) {
          ipAddresses = request.getHeader("X-Forwarded-For");
      }
      if (ipAddresses == null || ipAddresses.length() == 0 || "unknown".equalsIgnoreCase(ipAddresses)) {
          //Proxy-Client-IP：apache 服务代理
          ipAddresses = request.getHeader("Proxy-Client-IP");
      }

      if (ipAddresses == null || ipAddresses.length() == 0 || "unknown".equalsIgnoreCase(ipAddresses)) {
          //WL-Proxy-Client-IP：weblogic 服务代理
          ipAddresses = request.getHeader("WL-Proxy-Client-IP");
      }

      if (ipAddresses == null || ipAddresses.length() == 0 || "unknown".equalsIgnoreCase(ipAddresses)) {
          //HTTP_CLIENT_IP：有些代理服务器
          ipAddresses = request.getHeader("HTTP_CLIENT_IP");
      }

      if (ipAddresses == null || ipAddresses.length() == 0 || "unknown".equalsIgnoreCase(ipAddresses)) {
          //X-Real-IP：nginx服务代理
          ipAddresses = request.getHeader("X-Real-IP");
      }

      //有些网络通过多层代理，那么获取到的ip就会有多个，一般都是通过逗号（,）分割开来，并且第一个ip为客户端的真实IP
      if (ipAddresses != null && ipAddresses.length() != 0) {
          ip = ipAddresses.split(",")[0];
      }

      
      logger.error(">>>>>>>>>>ip地址:{}",ip);
      return ip;
  }
	

/*
	public static void main(String[] args) {
		System.out.println(isHTML("/idpstat.html", "get", "text/html;charset=utf-8"));
		System.out.println(isHTML("/idpstat.htm", "get", "text/html;charset=utf-8"));
		System.out.println(isHTML("/idpstat.html", "get", "text/html;charset=utf-8"));
	}
	*/

}
