package com.asiainfo.veris.crm.order.pub.util;

import java.security.NoSuchAlgorithmException;
import javax.crypto.KeyGenerator;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import org.apache.commons.codec.binary.Base64;

/**
 * by chenyw7
 */
public class HMACUtil {
	/**
	 * 定义加密方式 MAC算法可选以下多种算法
	 * 
	 * <pre>
	 * HmacMD5
	 * HmacSHA1
	 * HmacSHA256
	 * HmacSHA384
	 * HmacSHA512
	 * </pre>
	 */
	private final static String KEY_MAC = "HmacSHA256";

	/**
	 * 全局数组
	 */
	private final static String[] HEX_DIGITS = { "0", "1", "2", "3", "4", "5",
			"6", "7", "8", "9", "a", "b", "c", "d", "e", "f" };

	/**
	 * 构造函数
	 */
	public HMACUtil() {

	}

	/**
	 * BASE64 加密
	 * 
	 * @param key
	 *            需要加密的字节数组
	 * @return 字符串
	 * @throws Exception
	 */
	public static String encryptBase64(byte[] key) throws Exception {
		return new String(new Base64().encode(key));
	}

	/**
	 * BASE64 解密
	 * 
	 * @param key
	 *            需要解密的字符串
	 * @return 字节数组
	 * @throws Exception
	 */
	public static byte[] decryptBase64(String key) throws Exception {
		return Base64.decodeBase64(key.getBytes());
	}

	/**
	 * 初始化HMAC密钥
	 * 
	 * @return
	 */
	public static String init() {
		SecretKey key;
		String str = "";
		try {
			KeyGenerator generator = KeyGenerator.getInstance(KEY_MAC);
			key = generator.generateKey();
			str = encryptBase64(key.getEncoded());
		} catch (NoSuchAlgorithmException e) {
		} catch (Exception e) {
		}
		return str;
	}

	/**
	 * HMAC加密
	 * 
	 * @param data
	 *            需要加密的字节数组
	 * @param key
	 *            密钥
	 * @return 字节数组
	 */
	public static byte[] encryptHMAC(byte[] data, String key) {
		SecretKey secretKey;
		byte[] bytes = null;
		try {
			secretKey = new SecretKeySpec(decryptBase64(key), KEY_MAC);
			Mac mac = Mac.getInstance(secretKey.getAlgorithm());
			mac.init(secretKey);
			bytes = mac.doFinal(data);
		} catch (Exception e) {
		}
		return bytes;
	}

	/**
	 * HMAC加密
	 * 
	 * @param data
	 *            需要加密的字符串
	 * @param key
	 *            密钥
	 * @return 字符串
	 */
	public static String encryptHMAC(String data, String key) {
		if (data == null || "".equals(data)) {
			return null;
		}
		byte[] bytes = encryptHMAC(data.getBytes(), key);
		return byteArrayToHexString(bytes);
	}
	
	public static byte[] HMAC_SHA_256(String key, String SignBytes) throws Exception {
		SecretKeySpec secret_key = new SecretKeySpec(key.getBytes(), "HmacSHA256");
		Mac sha256_HMAC = Mac.getInstance(secret_key.getAlgorithm());
		sha256_HMAC.init(secret_key);
		byte[] digest = sha256_HMAC.doFinal(SignBytes.getBytes());
		return digest;
	}
	

	/**
	 * 将一个字节转化成十六进制形式的字符串
	 * 
	 * @param b
	 *            字节数组
	 * @return 字符串
	 */
	private static String byteToHexString(byte b) {
		int ret = b;
		if (ret < 0) {
			ret += 256;
		}
		int m = ret / 16;
		int n = ret % 16;
		return HEX_DIGITS[m] + HEX_DIGITS[n];
	}

	/**
	 * 转换字节数组为十六进制字符串
	 * 
	 * @param bytes
	 *            字节数组
	 * @return 十六进制字符串
	 */
	public static String byteArrayToHexString(byte[] bytes) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < bytes.length; i++) {
			sb.append(byteToHexString(bytes[i]));
		}
		return sb.toString();
	}
}
