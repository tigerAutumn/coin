package com.hotcoin.oss.utils;

import com.aliyun.oss.ClientConfiguration;
import com.aliyun.oss.OSSClient;
import com.aliyun.oss.common.comm.Protocol;
import com.aliyun.oss.model.ObjectMetadata;
import com.aliyun.oss.model.PutObjectResult;
import com.fasterxml.jackson.annotation.JsonValue;
import com.hotcoin.oss.conf.FileDirType;
import com.hotcoin.oss.conf.OSSConfig;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.time.DateUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

public class OSSUtil {
    private static final Logger logger = LoggerFactory.getLogger(OSSUtil.class);

    //OSS 的地址
    private final static String OSS_END_POINT = OSSConfig.JAVA4ALL_END_POINT;
    //OSS 的key值
    private final static String OSS_ACCESS_KEY_ID = OSSConfig.JAVA4ALL_ACCESS_KEY_ID;
    //OSS 的secret值
    private final static String OSS_ACCESS_KEY_SECRET = OSSConfig.JAVA4ALL_ACCESS_KEY_SECRET;
    //OSS 的bucket名字
    public final static String OSS_BUCKET_NAME = OSSConfig.JAVA4ALL_BUCKET_NAME;
    //设置URL过期时间为10年
    public final static Date OSS_URL_EXPIRATION = DateUtils.addDays(new Date(), 365 * 10);

    private final static String fileHost = OSSConfig.JAVA4ALL_FILE_HOST;


    private volatile static OSSClient instance;

    private OSSUtil() {
    }

    /**
     * 单例
     * @return  OSS工具类实例
     */
    public static OSSClient getOSSClient() {
        if (instance == null) {
            synchronized (OSSUtil.class) {
                if (instance == null) {
                    ClientConfiguration conf = new ClientConfiguration();
                    conf.setProtocol(Protocol.HTTPS);
                    instance = new OSSClient(OSS_END_POINT, OSS_ACCESS_KEY_ID, OSS_ACCESS_KEY_SECRET);
                }
            }
        }
        return instance;
    }

    /**
     * 上传文件---去除URL中的？后的时间戳
     * @param file 文件
     * @param fileDir 上传到OSS上文件的路径
     * @return 文件的访问地址
     */
    public static String upload(MultipartFile file, String fileDir) {
        OSSUtil.createBucket();
        String fileName = OSSUtil.uploadFile(file, fileDir);
        String fileOssURL = OSSUtil.getImgUrl(fileName, fileDir);
        int firstChar = fileOssURL.indexOf("?");
        if (firstChar > 0) {
            fileOssURL = fileOssURL.substring(0, firstChar);
        }
        return fileOssURL;
    }



    /**
     * 当Bucket不存在时创建Bucket
     *
     * @throws com.aliyun.oss.OSSException 异常
     * @throws com.aliyun.oss.ClientException Bucket命名规则：
     *                         1.只能包含小写字母、数字和短横线，
     *                         2.必须以小写字母和数字开头和结尾
     *                         3.长度在3-63之间
     */
    private static void createBucket() {
        try {
            if (!OSSUtil.getOSSClient().doesBucketExist(OSS_BUCKET_NAME)) {//判断是否存在该Bucket，不存在时再重新创建
                OSSUtil.getOSSClient().createBucket(OSS_BUCKET_NAME);
            }
        } catch (Exception e) {
            logger.info("{}", "创建Bucket失败,请核对Bucket名称(规则：只能包含小写字母、数字和短横线，必须以小写字母和数字开头和结尾，长度在3-63之间)");
            //OSSCreateBucketRuntimeException
            throw new RuntimeException("创建Bucket失败,请核对Bucket名称(规则：只能包含小写字母、数字和短横线，必须以小写字母和数字开头和结尾，长度在3-63之间)");
        }
    }

    private static String uploadFile(MultipartFile file, String fileDir) {
        return uploadFileToBucketName(file,fileDir,OSS_BUCKET_NAME);
    }

    /**
     * 上传到OSS服务器  如果同名文件会覆盖服务器上的
     * @param file 文件
     * @param fileDir  上传到OSS上文件的路径
     * @return 文件的访问地址
     */
    private static String uploadFileToBucketName(MultipartFile file, String fileDir, String bucketName) {
        String fileName = String.format(
                "%s.%s",
                UUID.randomUUID().toString(),
                FilenameUtils.getExtension(file.getOriginalFilename()));
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

        String dateStr = format.format(new Date());
        try (InputStream inputStream = file.getInputStream()) {
            //创建上传Object的Metadata
            ObjectMetadata objectMetadata = new ObjectMetadata();
            objectMetadata.setContentLength(inputStream.available());
            objectMetadata.setCacheControl("no-cache");
            objectMetadata.setHeader("Pragma", "no-cache");
            objectMetadata.setContentType(getContentType(FilenameUtils.getExtension("." + file.getOriginalFilename())));
            //创建文件路径
            String fileUrl = fileDir + "/" + dateStr + "/" + fileName;
            objectMetadata.setContentDisposition("inline;filename=" + fileUrl);
            //上传文件
            PutObjectResult putResult = OSSUtil.getOSSClient().putObject(bucketName, fileUrl, inputStream, objectMetadata);

            //设置权限 这里是公开读
            //  OSSUtil.getOSSClient().setBucketAcl(bucketName, CannedAccessControlList.PublicRead);
            if (null != putResult) {
                logger.info("==========>OSS文件上传成功,OSS地址："+fileUrl);
                return fileUrl;
            }else{
                return null;
            }

        } catch (Exception e) {
            logger.error("上传文件失败:{}", e);
            //OssPutObjectRuntimeException
            throw new RuntimeException("上传文件失败");
        }
    }


    /**
     * 上传文件to oss
     * @param picInputStream	文件流
     * @param fileDir			文件目录
     * @param bucketName	
     * @param fileExtensName	文件名
     * @param isOriginNameStore	是否原名存储
     * @param isNeedDateAppender 是否需要加date
     * @return
     */
    public static String uploadFile(InputStream picInputStream, String fileDir, String bucketName, 
    		String fileExtensName, Boolean isOriginNameStore) {

        String fileName = isOriginNameStore ? fileExtensName : String.format("%s.%s", UUID.randomUUID().toString(), fileExtensName);
        try{

            //创建文件路径
            String fileUrl = "";
            if(!isOriginNameStore) {
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                String dateStr = format.format(new Date());
            	fileUrl = fileDir + "/" + dateStr + "/" + fileName;
            }else {
            	fileUrl = fileDir + "/" + fileName;
            }
        	
            //创建上传Object的Metadata
            ObjectMetadata objectMetadata = new ObjectMetadata();
            objectMetadata.setHeader("Pragma", "no-cache");
            objectMetadata.setContentLength(picInputStream.available());
            objectMetadata.setCacheControl("no-cache");
            objectMetadata.setContentType(getContentType(FilenameUtils.getExtension(fileExtensName)));
            objectMetadata.setContentDisposition("inline;filename=" + fileUrl);
            
            //上传文件
            PutObjectResult putResult = OSSUtil.getOSSClient().putObject(bucketName, fileUrl, picInputStream, objectMetadata);

            //设置权限 这里是公开读
            //  OSSUtil.getOSSClient().setBucketAcl(bucketName, CannedAccessControlList.PublicRead);
            if (null != putResult) {

                logger.info("=====uploadFileByInputStream=====>OSS文件上传成功,OSS地址：" +fileUrl);
                String fileOssURL = OSSUtil.getImgUrl(fileUrl, fileDir);
                int firstChar = fileOssURL.indexOf("?");
                if (firstChar > 0) {
                    fileOssURL = fileOssURL.substring(0, firstChar);
                }
                return fileOssURL;
            }else{
                return null;
            }

        } catch (Exception e) {
            logger.error("{}, exception:{}", "上传文件失败", e);
            //OssPutObjectRuntimeException
            throw new RuntimeException("上传文件失败");
        }
    }

    public static String uploadFileByInputStream(InputStream picInputStream, String fileDir,String bucketName,String fileExtensName) {
    	return uploadFile(picInputStream, fileDir, bucketName, fileExtensName, false);
    }

    /**
     	* 获得文件路径
     * @param fileUrl  文件的URL
     * @param fileDir  文件在OSS上的路径
     * @return 文件的路径
     */
    private static String getImgUrl(String fileUrl, String fileDir) {
        if (StringUtils.isBlank(fileUrl)) {
            logger.error("{}", "文件地址为空");
            throw new RuntimeException("文件地址为空");
        }
        String[] split = fileUrl.split("/");

        //获取oss图片URL失败
        URL url = OSSUtil.getOSSClient().generatePresignedUrl(OSS_BUCKET_NAME, fileUrl, OSS_URL_EXPIRATION);
        if (url == null) {
            logger.error("{}", "获取oss文件URL失败");
            //OSSGeneratePresignedUrlRuntimeException
            throw new RuntimeException("获取oss文件URL失败");
        }
        return url.toString();
    }

    /**
     * 判断OSS服务文件上传时文件的contentType
     *
     * @param FilenameExtension 文件后缀
     * @return 后缀
     */
    private static String getContentType(String FilenameExtension) {
        if (FilenameExtension.equalsIgnoreCase("bmp")) {
            return "image/bmp";
        }
        if (FilenameExtension.equalsIgnoreCase("gif")) {
            return "image/gif";
        }
        if (FilenameExtension.equalsIgnoreCase("jpeg") ||
                FilenameExtension.equalsIgnoreCase("jpg") ||
                FilenameExtension.equalsIgnoreCase("png")) {
            return "image/jpeg";
        }
        if (FilenameExtension.equalsIgnoreCase("html")) {
            return "text/html";
        }
        if (FilenameExtension.equalsIgnoreCase("txt")) {
            return "text/plain";
        }
        if (FilenameExtension.equalsIgnoreCase("vsd")) {
            return "application/vnd.visio";
        }
        if (FilenameExtension.equalsIgnoreCase("pptx") ||
                FilenameExtension.equalsIgnoreCase("ppt")) {
            return "application/vnd.ms-powerpoint";
        }
        if (FilenameExtension.equalsIgnoreCase("docx") ||
                FilenameExtension.equalsIgnoreCase("doc")) {
            return "application/msword";
        }
        if (FilenameExtension.equalsIgnoreCase("xml")) {
            return "text/xml";
        }
        if (FilenameExtension.equalsIgnoreCase("json")) {
        	return "application/json";
        }
        return "image/jpeg";

    }

    /**
     * 删除指定文件
     *
     * @param folder 文件名
     * @param key    文件名
     */
    public static void deleteOSSFileUitl(String folder, String key) {
        OSSClient ossClient = getOSSClient();
        ossClient.deleteObject(OSS_BUCKET_NAME, folder + key);
        logger.info("删除" + OSS_BUCKET_NAME + "下文件" + folder + key + "成功");
        System.out.println("删除" + OSS_BUCKET_NAME + "下文件" + folder + key + "成功");
    }

    public void deleteImg(String url,String folder) {
        OSSClient ossClient = getOSSClient();
        if (url == null || "".equals(url)) {
            return;
        }
        String[] paths = url.split("[.]");
        /**
         * 文件夹是否存在
         */
        if (!ossClient.doesObjectExist(OSS_BUCKET_NAME, folder)) {
            ossClient.putObject(OSS_BUCKET_NAME, folder, new ByteArrayInputStream(new byte[0]));
        }
        String[] name = paths[paths.length - 2].split("[/]");
        /**
         * 对象是否存在
         */
        if (ossClient
                .doesObjectExist(OSS_BUCKET_NAME,
                        folder + name[name.length - 1] + "." + paths[paths.length - 1])) {
            /**
             * 删除存在对象
             */
            ossClient
                    .deleteObject(OSS_BUCKET_NAME, folder + name[name.length - 1] + "." + paths[paths.length - 1]);
        }
        ossClient.shutdown();
    }

    /**
     * 上传文件测试
     * @param multipartFile 待上传的文件
     * @return  上传在OSS文件的访问路径
     * @throws Exception  上传异常
     */
    public String uploadTest(MultipartFile multipartFile) throws Exception {
        String uploadResult = null;
        try {
            uploadResult = OSSUtil.upload(multipartFile, "test");
        } catch (Exception e) {
            logger.error("call OSSUtil.upload Exception:{}", e.getMessage());
        }
        return uploadResult;
    }
}
