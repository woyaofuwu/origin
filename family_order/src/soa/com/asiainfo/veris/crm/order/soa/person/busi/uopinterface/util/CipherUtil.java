package com.asiainfo.veris.crm.order.soa.person.busi.uopinterface.util;

import com.ailk.org.apache.commons.codec.binary.Base64;

import javax.crypto.Cipher;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.Security;

/**
 * The Class CipherUtil.
 *
 * @字符串的加密解密，主要用于参数传递
 */
public class CipherUtil {

    /** The str default key. */
    private static String strDefaultKey = "";

    /** The encrypt cipher. */
    private Cipher encryptCipher = null;

    /** The decrypt cipher. */
    private Cipher decryptCipher = null;

    /**
     * 默认构造方法，使用默认密钥.
     *
     * @throws Exception the exception
     */
    public CipherUtil() throws Exception {
        this(strDefaultKey);
    }

    /**
     * 指定密钥构造方法.
     *
     * @param strKey            指定的密钥
     * @throws Exception the exception
     */
    public CipherUtil(String strKey) throws Exception {
        Security.addProvider(new com.sun.crypto.provider.SunJCE());
        Key key = getKey(strKey.getBytes());

        encryptCipher = Cipher.getInstance("AES/ECB/PKCS5Padding");

        encryptCipher.init(Cipher.ENCRYPT_MODE, key);

        decryptCipher= Cipher.getInstance("AES/ECB/PKCS5Padding");

        decryptCipher.init(Cipher.DECRYPT_MODE, key);
    }

    /**
     * method is "AES/ECB/PKCSSPadding"
     * @param key
     * @return
     */
    /**
     * @param data
     * @return
     * @throws Exception
     */
    public String deCiper(String data) throws Exception {
    	try {
    		byte[] original = decryptCipher.doFinal(new Base64().decode(data));
    		return new String(original, StandardCharsets.UTF_8);			
		} catch (Exception e) {
			throw new Exception("实名制信息解密异常！");
		}
    }

    /**
     * @param data
     * @return
     * @throws Exception
     */
    public String enCiper(String data) throws Exception {
        byte[] original = encryptCipher.doFinal(data.getBytes(StandardCharsets.UTF_8));
        return new Base64().encodeAsString(original);
    }

    /**
     * 从指定字符串生成密钥，密钥所需的字节数组长度为16位 不足16位时后面补0，超出16位只取前16位.
     *
     * @param arrBTmp            构成该字符串的字节数组
     * @return 生成的密钥
     * @throws Exception the exception
     */
    private Key getKey (byte[] arrBTmp) throws Exception {
        // 创建一个空的16位字节数组（默认值为0）
        //byte[] arrB = new byte[16];

        // 将原始字节数组转换为16位
        /*for (int i = 0; i < arrBTmp.length && i < arrB.length; i++) {
            arrB[i] = arrBTmp[i];
        }*/
        
        byte[] arrB = new byte[]{115, 119, 90, 36, 82, 81, 65, 113, 119, 83, 35, 82, 84, 89, 116, 114};

        // 生成密钥
        Key key = new javax.crypto.spec.SecretKeySpec(arrB, "AES");

        return key;
    }

    public static void main (String[] args) throws Exception {
        CipherUtil desPlus = new CipherUtil("未知");
        String encryptStr = desPlus.enCiper("吴王丰");
        System.out.println(encryptStr);
        String decryptStr = desPlus.deCiper(encryptStr);
        System.out.println(decryptStr);
    }
}
