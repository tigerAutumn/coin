package com.qkwl.common.rpc.oss;

import java.io.InputStream;

import javax.servlet.http.HttpServletRequest;

/**
 * 文件服务
 * @author peiqin
 *
 */
public interface IFileService {

	/**
	 * 上传文件
	 * @param file	文件流
	 * @param uniqueCode 自定义编号
	 * @param fileName	文件名
	 * @param fileDir	文件目录
	 * @param isOriginNameStore	是否原名存储
	 * @return
	 */
	public String upload(InputStream file, String uniqueCode, String fileName,String fileDir, Boolean isOriginNameStore);
	
	/**
	 * 获取资源地址
	 * @param fileDir
	 * @param fileName
	 * @return
	 */
	public String resourceUrl(String fileDir, String fileName);

}
