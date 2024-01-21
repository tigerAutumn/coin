package com.qkwl.common.util;

import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;

/**
 * Description:
 *  des加密解密
 * Date: 2019/10/8 17:56
 * Created by luoyingxiong
 */
public class DesUtils {

    /**
     *  加密
     * @param content
     * @param keyBytes
     * @return
     */
    public static byte[] encrypt(byte[] content, byte[] keyBytes) {
        try {
            DESKeySpec keySpec = new DESKeySpec(keyBytes);
            String algorithm =  "DES";//指定使什么样的算法
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(algorithm);
            SecretKey key = keyFactory.generateSecret(keySpec);
            String transformation = "DES/CBC/PKCS5Padding"; //用什么样的转型方式
            Cipher cipher = Cipher.getInstance(transformation);
            cipher.init(Cipher.ENCRYPT_MODE, key, new IvParameterSpec(keySpec.getKey()));
            byte[] result = cipher.doFinal(content);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 解密
     * @param content
     * @param keyBytes
     * @return
     */
    public static byte[] decrypt(byte[] content, byte[] keyBytes) {

        try {
            DESKeySpec keySpec = new DESKeySpec(keyBytes);
            String algorithm = "DES";
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(algorithm );
            SecretKey key = keyFactory.generateSecret(keySpec);
            String transformation = "DES/CBC/PKCS5Padding";
            Cipher cipher = Cipher.getInstance(transformation );
            cipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(keyBytes));
            byte[] result = cipher.doFinal(content);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    
    
    
    static public String encrypt(String content, String key) {
    	if(key == null || key.length() != 8) {
    		return null;
    	}
    	byte[] encrypt = encrypt(content.getBytes(),key.getBytes());
    	return Base64.getEncoder().encodeToString(encrypt);
    }
    
    
    static public String decrypt(String content, String key) {
    	if(key == null || key.length() != 8) {
    		return null;
    	}
    	byte[] decode = Base64.getDecoder().decode(content);
		return new String(decrypt(decode, key.getBytes()));
    }
    
    public static void main(String[] args) {
    	System.out.println(DesUtils.encrypt("0xf1f14cf864001516265cc5c02b8257b06744ff27dfe3c755ebee45ff33133f29", "TCPencod"));
    	System.out.println(DesUtils.decrypt("WVttVT5KuZZrdrO8pMefshpIDTDFdhRX9mjpZGvjq32vWEdcm89jPOXlqudtze8QTkwfaYmmzc1PLMbq48AezA6cJ6IZ7ihR", "TCPencod"));
	}
    
}
