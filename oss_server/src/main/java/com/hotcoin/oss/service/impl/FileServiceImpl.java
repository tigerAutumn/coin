package com.hotcoin.oss.service.impl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Maps;
import com.hotcoin.oss.conf.FileDirType;
import com.hotcoin.oss.service.IFileService;
import com.hotcoin.oss.utils.I18NUtils;
import com.hotcoin.oss.utils.OSSUtil;
import com.hotcoin.oss.vo.ResponseMessage;
import com.qkwl.common.util.RespData;

/**
 * 文件管理实现类
 * @author peiqin
 *
 */
@Service
public class FileServiceImpl implements IFileService{

    private final Logger logger = LoggerFactory.getLogger(FileServiceImpl.class);

	@Override
	public String upload(InputStream inputStream, String uniqueCode, String fileName, String fileDir, Boolean isOriginNameStore) {

        ResponseMessage<Map<String, String>> responseMessage = new ResponseMessage<>();
        
        Map<String, String> responseMap = Maps.newHashMap();
        
        try {
		
        	//hsf框架序列化inputStream有问题，需要重新转换
	        ByteArrayOutputStream swapStream = new ByteArrayOutputStream();  
	        byte[] buff = new byte[100];  
	        int rc = 0;  
	        while ((rc = inputStream.read(buff, 0, 100)) > 0) {  
	            swapStream.write(buff, 0, rc);  
	        }  
	        byte[] in2b = swapStream.toByteArray();  
			
			InputStream is = new ByteArrayInputStream(in2b);
	
	        
	        String fileDirPath = "";
	        if(StringUtils.isNotBlank(fileDir)){
	            if(null != FileDirType.fileDirMap.get(fileDir)){
	                fileDirPath = String.valueOf(FileDirType.fileDirMap.get(fileDir));
	            }
	        }
	
	        if(StringUtils.isBlank(fileDirPath)){
	            responseMessage = responseMessage.error(responseMap, "非法请求");
	            return JSON.toJSONString(responseMessage);
	        }
	        if(StringUtils.isNotBlank(uniqueCode)){
	            fileDirPath = fileDirPath + "/" + uniqueCode.trim();
	        }

            String filePath = OSSUtil.uploadFile(is, fileDirPath, OSSUtil.OSS_BUCKET_NAME, 
            		fileName, isOriginNameStore);
            if(StringUtils.isNotBlank(filePath)){
                logger.info("文件上传成功:{}", filePath);
                responseMap.put("filePathUrl", filePath);
                responseMessage = responseMessage.success(responseMap);
                return JSON.toJSONString(responseMessage);
            }
        } catch (Exception e) {
            logger.info("rpc upload file exception:{}", e.getMessage());
        }

        responseMessage = responseMessage.error(responseMap, I18NUtils.getString("FileUploadController.uploadFailFileUploadController.uploadFail"));
        return JSON.toJSONString(responseMessage);
	}

	@Override
	public String resourceUrl(String fileDir, String fileName) {
		
		StringBuffer sb = new StringBuffer();

        String fileDirPath = "";
        if(StringUtils.isNotBlank(fileDir)){
            if(null != FileDirType.fileDirMap.get(fileDir)){
                fileDirPath = String.valueOf(FileDirType.fileDirMap.get(fileDir));
            }
        }

        if(StringUtils.isBlank(fileDirPath) || StringUtils.isBlank(fileName)){

            return JSON.toJSONString(RespData.error(null, "非法请求"));
        }
        
        sb.append(fileDirPath).append("/").append(fileName);
        
        URL url = OSSUtil.getOSSClient().generatePresignedUrl(OSSUtil.OSS_BUCKET_NAME, sb.toString(), OSSUtil.OSS_URL_EXPIRATION);
        if (url == null) {
            logger.error("{}", "获取oss文件URL失败");
            throw new RuntimeException("获取oss文件URL失败");
        }
       
        return JSON.toJSONString(RespData.ok(url.toString()));
	}
	
}
