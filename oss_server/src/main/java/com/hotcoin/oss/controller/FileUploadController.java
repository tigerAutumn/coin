package com.hotcoin.oss.controller;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.hotcoin.oss.conf.FileDirType;
import com.hotcoin.oss.service.IFileService;
import com.hotcoin.oss.utils.ByteToInputStream;
import com.hotcoin.oss.utils.I18NUtils;
import com.hotcoin.oss.utils.OSSUtil;
import com.hotcoin.oss.vo.ResponseMessage;

@RestController
@RequestMapping("/oss_server")
public class FileUploadController {
	
	@Autowired
	private IFileService fileService;
	
    private final Logger logger = LoggerFactory.getLogger(FileUploadController.class);

    private static HashSet<String> photoFormat =new HashSet<>();
    private static HashSet<String> voidFormat =new HashSet<>();
    @PostConstruct
    public void register(){
        photoFormat.add(".jpg");
        photoFormat.add(".jpeg");
        photoFormat.add(".gif");
        photoFormat.add(".png");
        voidFormat.add(".mp4");
        voidFormat.add(".wmv");
        voidFormat.add(".ogg");
        voidFormat.add(".m");
    }

    /**
     * 文件上传
     *
     * @param request
     */
    @RequestMapping(value = "/upload", method = {RequestMethod.POST, RequestMethod.GET})
    @ResponseBody
    public String uploadBlog(@RequestParam("file") MultipartFile file,@RequestParam(value ="uniqueCode", required=false)String uniqueCode,@RequestParam("fileDir")String fileDir,HttpServletRequest request) {

        logger.info("============>文件上传");
        ResponseMessage<Map<String,String>> responseMessage = new ResponseMessage<>();
        Map<String,String> filePathMap = new HashMap<String,String>(1);
        String fileDirPath = "";
        if(StringUtils.isNotBlank(fileDir)){
            if(null != FileDirType.fileDirMap.get(fileDir)){
                fileDirPath = String.valueOf(FileDirType.fileDirMap.get(fileDir));
            }
        }
        if(StringUtils.isBlank(fileDirPath)){
            logger.error("upload remotHost:{},remotePort:{}",request.getRemoteHost(),request.getRemotePort());
            responseMessage = responseMessage.error(filePathMap,"主机："+request.getRemoteHost()+"非法请求");
            return JSON.toJSONString(responseMessage);
        }
        if(StringUtils.isNotBlank(uniqueCode)){
            fileDirPath =fileDirPath+"/"+uniqueCode.trim();
        }
        filePathMap.put("filePathUrl",fileDirPath);

        try {
            if (null != file) {
                String filename = file.getOriginalFilename();
                if (StringUtils.isNotBlank(filename.trim())) {
                    String suffixName = filename.substring(filename.lastIndexOf("."));
                    logger.info("上传的后缀名为：" + suffixName);
                    Long imgSize = 0L;
                    if(photoFormat.contains(suffixName.toLowerCase())){
                        imgSize = Long.valueOf(5 * 1024 *1024);
                    }
                    if(voidFormat.contains(suffixName.toLowerCase())){
                        imgSize = Long.valueOf(50 * 1024 *1024);
                    }
                    logger.info("file imagesize:"+imgSize);
                    if(imgSize.equals(0L)){
                        logger.info("文件格式错误：" + suffixName);
                        responseMessage = responseMessage.error(filePathMap, I18NUtils.getString("FileUploadController.suffixName.error"));
                        return JSON.toJSONString(responseMessage);
                    }

                    Long fileSize = file.getSize();
                    if(fileSize>imgSize){
                        responseMessage = responseMessage.error(filePathMap, "文件大小超过限制");
                        logger.info("文件大小超过限制："+imgSize);
                        return JSON.toJSONString(responseMessage);
                    }

                   /* File newFile = new File(filename);
                    FileOutputStream os = new FileOutputStream(newFile);
                    os.write(file.getBytes());
                    os.close();
                    file.transferTo(newFile);*/
                    //上传到OSS
                    String uploadUrl = OSSUtil.upload(file, fileDirPath);
                     logger.info("文件上传成功"+uploadUrl);
                    // String uploadUrl ="http://tubaowangtestbucket.oss-ap-southeast-1.aliyuncs.com/test/2018-11-24/80147cd7-9610-41a7-b86a-abb448c188b0.jpeg";
                    filePathMap.put("filePathUrl",uploadUrl);
                    responseMessage = responseMessage.success(filePathMap);
                    return JSON.toJSONString(responseMessage);
                }
            }
        } catch (Exception ex) {
            logger.info("upload file exception:{}",ex.getMessage());
        }
        responseMessage = responseMessage.error(filePathMap,I18NUtils.getString("FileUploadController.uploadFailFileUploadController.uploadFail"));
        return JSON.toJSONString(responseMessage);
    }

    @RequestMapping(value = "/uploadBase64", method = {RequestMethod.POST, RequestMethod.GET},produces = "application/json; charset=utf-8")
    @ResponseBody
    public String updateUserPassword(@RequestParam("file") String image,@RequestParam(value ="uniqueCode", required=false)String uniqueCode,@RequestParam(value ="fileExtensName", required=false, defaultValue=".jpeg")String fileExtensName,@RequestParam("fileDir")String fileDir,HttpServletRequest request){

        ResponseMessage<Map<String,String>> responseMessage = new ResponseMessage<>();
        Map<String,String> filePathMap = new HashMap<String,String>(1);
        String fileDirPath = "";
        if(StringUtils.isNotBlank(fileDir)){
            if(null != FileDirType.fileDirMap.get(fileDir)){
                fileDirPath = String.valueOf(FileDirType.fileDirMap.get(fileDir));
            }
        }

        if(StringUtils.isBlank(fileDirPath)){
            logger.error("upload remotHost:{},remotePort:{}",request.getRemoteHost(),request.getRemotePort());
            responseMessage = responseMessage.error(filePathMap,"主机："+request.getRemoteHost()+"非法请求");
            return JSON.toJSONString(responseMessage);
        }
        if(StringUtils.isNotBlank(uniqueCode)){
            fileDirPath =fileDirPath+"/"+uniqueCode.trim();
        }
        filePathMap.put("filePathUrl",fileDirPath);
        JSONObject responseJson = new JSONObject();

        String header ="data:image";
        String[] imageArr=image.split(",");
        if(imageArr[0].contains(header)) {//是img的

            // 去掉头部
            image=imageArr[1];
            // 修改图片
            //BASE64Decoder decoder = new BASE64Decoder();
            //Base64 base64 = new Base64();
            try {
                //byte[] decodedBytes = decoder.decodeBuffer(image); // 将字符串格式的image转为二进制流（biye[])的decodedBytes
                byte[] decodedBytes = Base64.decodeBase64(image);
                InputStream picInputStream = ByteToInputStream.byte2Input(decodedBytes);
                if(StringUtils.isBlank(fileExtensName)){
                    fileExtensName = ".jpeg";
                }
                String filePath = OSSUtil.uploadFileByInputStream(picInputStream, fileDirPath, OSSUtil.OSS_BUCKET_NAME, fileExtensName);
                if(StringUtils.isNotBlank(filePath)){
                    logger.info("文件上传成功"+filePath);
                    filePathMap.put("filePathUrl",filePath);
                    responseMessage = responseMessage.success(filePathMap);
                    return JSON.toJSONString(responseMessage);
                }
            } catch (Exception e) {
                logger.info("uploadBase64 file exception:{}",e.getMessage());
            }
        }

        responseMessage = responseMessage.error(filePathMap,I18NUtils.getString("FileUploadController.uploadFailFileUploadController.uploadFail"));
        return JSON.toJSONString(responseMessage);
    }

    @RequestMapping(value = "/v2/upload", method = {RequestMethod.POST})
    public String upload(@RequestParam("file") MultipartFile file, @RequestParam(value ="uniqueCode", required=false)String uniqueCode,
    		@RequestParam("fileDir") String fileDir, 
    		@RequestParam(value = "isOriginNameStore", required = true, defaultValue = "false") Boolean isOriginNameStore) throws IOException{
    	return fileService.upload(file.getInputStream(), uniqueCode, file.getOriginalFilename(), fileDir, isOriginNameStore);
    }

    @RequestMapping(value = "/getResourceUrl", method = {RequestMethod.POST})
    public String resourceUrl(@RequestParam("fileDir") String fileDir, @RequestParam("fileName") String fileName){
    	return fileService.resourceUrl(fileDir, fileName);
    }
}
