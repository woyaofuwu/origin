/**
 * @author tanzheng
 * @description 
 * @date 2019年5月24日
 */
package com.asiainfo.veris.crm.order.pub.util;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import org.apache.log4j.Logger;


/**
 * @author tz
 *
 */
public class MD5Util {
	private static Logger logger = Logger.getLogger(MD5Util.class);
	private static final String HEX_NUMS_STR="0123456789ABCDEF";   
    private static final Integer SALT_LENGTH = 0;   
	/**
	*@description 
	*@param
	*@return 
	*@author tanzheng
	*@date 2019年5月24日
	*/
	/**
	 * MD5 加密
	 */
	public static String getMD5Str(String str) {
	    MessageDigest messageDigest = null;
	    try {
	        messageDigest = MessageDigest.getInstance("MD5");
	        messageDigest.reset();
	        messageDigest.update(str.getBytes("UTF-8"));
	    } catch (NoSuchAlgorithmException e) {
	        System.exit(-1);
	    } catch (UnsupportedEncodingException e) {
	        e.printStackTrace();
	    }
	    byte[] byteArray = messageDigest.digest();
	    StringBuffer md5StrBuff = new StringBuffer();
	    for (int i = 0; i < byteArray.length; i++) {
	        if (Integer.toHexString(0xFF & byteArray[i]).length() == 1)
	            md5StrBuff.append("0").append(
	                    Integer.toHexString(0xFF & byteArray[i]));
	        else
	            md5StrBuff.append(Integer.toHexString(0xFF & byteArray[i]));
	    }
	    return md5StrBuff.toString();
	}
	 
	/** 
     * 获得加密后的16进制形式口令 
     * @param password 
     * @return 
     * @throws NoSuchAlgorithmException 
     * @throws UnsupportedEncodingException 
     */ 
    public static String getEncryptedPwd(String password)   
            throws NoSuchAlgorithmException, UnsupportedEncodingException {   
        //声明加密后的口令数组变量   
        byte[] pwd = null;   
        //随机数生成器   
        SecureRandom random = new SecureRandom();   
        //声明盐数组变量   
        byte[] salt = new byte[SALT_LENGTH];   
        //将随机数放入盐变量中   
        random.nextBytes(salt);   

        //声明消息摘要对象   
        MessageDigest md = null;   
        //创建消息摘要   
        md = MessageDigest.getInstance("MD5");   
        //将盐数据传入消息摘要对象   
        md.update(salt);   
        //将口令的数据传给消息摘要对象   
        md.update(password.getBytes("UTF-8"));   
        //获得消息摘要的字节数组   
        byte[] digest = md.digest();   

        //因为要在口令的字节数组中存放盐，所以加上盐的字节长度   
        pwd = new byte[digest.length + SALT_LENGTH];   
        //将盐的字节拷贝到生成的加密口令字节数组的前12个字节，以便在验证口令时取出盐   
        System.arraycopy(salt, 0, pwd, 0, SALT_LENGTH);   
        //将消息摘要拷贝到加密口令字节数组从第13个字节开始的字节   
        System.arraycopy(digest, 0, pwd, SALT_LENGTH, digest.length);   
        //将字节数组格式加密后的口令转化为16进制字符串格式的口令   
        return byteToHexString(pwd);   
    }   
	
    /** 
     * 将指定byte数组转换成16进制字符串 
     * @param b 
     * @return 
     */ 
    public static String byteToHexString(byte[] b) {   
        StringBuffer hexString = new StringBuffer();   
        for (int i = 0; i < b.length; i++) {   
            String hex = Integer.toHexString(b[i] & 0xFF);   
            if (hex.length() == 1) {   
                hex = '0' + hex;   
            }   
            hexString.append(hex.toUpperCase());   
        }   
        return hexString.toString();   
    }   
}
