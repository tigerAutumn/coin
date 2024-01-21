package com.qkwl.service.validate.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;

/**
 * @ProjectName: service_validate
 * @Package: com.qkwl.service.validate.util
 * @ClassName: IOCloseUtils
 * @Author: hf
 * @Description:
 * @Date: 2019/7/23 12:45
 * @Version: 1.0
 */

public class IOCloseUtils {
    private static final Logger logger = LoggerFactory.getLogger(IOCloseUtils.class);
    public static void closeIOStream( BufferedReader in) {
        try {
            if (in != null) {
                in.close();
            }
        } catch (Exception e) {
            logger.error("close io stream fail->[{}]",e);
        }
    }
}
