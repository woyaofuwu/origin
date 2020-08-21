package com.asiainfo.veris.crm.order.soa.person.busi.uopinterface.util;
import java.io.IOException;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;
 
public class RSAUtil {
	
	static String publicKeyStr = "MFwwDQYJKoZIhvcNAQEBBQADSwAwSAJBAJa5B0z0Xju8wsXiUrMXBubGeqs1XCjjxaiTVn/np+7QZtoRjeHCKZEZPKqDMjRzgQEB4y6du5FfstNd9ZJWSbcCAwEAAQ==";
	static String privateKeyStr = "MIIBVAIBADANBgkqhkiG9w0BAQEFAASCAT4wggE6AgEAAkEAlrkHTPReO7zCxeJSsxcG5sZ6qzVcKOPFqJNWf+en7tBm2hGN4cIpkRk8qoMyNHOBAQHjLp27kV+y0131klZJtwIDAQABAkBz1RMVQyBAjJqrdXnVBLdbF+IiPcnr6tB283Sgq6nVz8SbePLm2Hoj/6otHX2gEOzcC3fbk+svkd2CRtgZN8eRAiEA6+gW6U7mLT5Al7RdKJLMOANQ/vGvOoMbP5djujSNVKMCIQCjj4R3TBRZS7DQkWOTikJ34UGUMFkJHVPTP174kWVz3QIhAMvwSF5JpxNVzeFYO3Dn90Q1GcBMj3+198hxx/UhmUyhAiAsMkK8Lb2t52sO4MqEeq69UAv1bB8S/G7DPXN/PWKprQIgG7beWNXhqWVCYGNZXhWdsTOMiDu5ZYi16dB690FdLcs=";

	//将Base64编码后的公钥转换成PublicKey对象
	public static PublicKey string2PublicKey(String pubStr) throws Exception{
		byte[] keyBytes = base642Byte(pubStr);
		X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
		KeyFactory keyFactory = KeyFactory.getInstance("RSA");
		PublicKey publicKey = keyFactory.generatePublic(keySpec);
		return publicKey;
	}
	
	//将Base64编码后的私钥转换成PrivateKey对象
	public static PrivateKey string2PrivateKey(String priStr) throws Exception{
		byte[] keyBytes = base642Byte(priStr);
		PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
		KeyFactory keyFactory = KeyFactory.getInstance("RSA");
		PrivateKey privateKey = keyFactory.generatePrivate(keySpec);
		return privateKey;
	}
	
	//公钥加密
	public static byte[] publicEncrypt(byte[] content, PublicKey publicKey) throws Exception{
		Cipher cipher = Cipher.getInstance("RSA");
		cipher.init(Cipher.ENCRYPT_MODE, publicKey);
		byte[] bytes = cipher.doFinal(content);
		return bytes;
	}
	
	//私钥解密
	public static byte[] privateDecrypt(byte[] content, PrivateKey privateKey) throws Exception{
		Cipher cipher = Cipher.getInstance("RSA");
		cipher.init(Cipher.DECRYPT_MODE, privateKey);
		byte[] bytes = cipher.doFinal(content);
		return bytes;
	}
	
	//字节数组转Base64编码
	public static String byte2Base64(byte[] bytes){
		BASE64Encoder encoder = new BASE64Encoder();
		return encoder.encode(bytes);
	}
	
	//Base64编码转字节数组
	public static byte[] base642Byte(String base64Key) throws IOException{
		BASE64Decoder decoder = new BASE64Decoder();
		return decoder.decodeBuffer(base64Key);
	}
	/**
	 * 
	 * @Description：加密
	 * @param:@param srcString
	 * @param:@return
	 * @param:@throws Exception
	 * @return String
	 * @throws
	 * @Author :tanzheng
	 * @date :2018-9-20下午02:56:27
	 */
	public static String encrypt(String srcString) throws Exception{
		PublicKey publicKey = RSAUtil.string2PublicKey(publicKeyStr);
		//用公钥加密
		byte[] publicEncrypt = RSAUtil.publicEncrypt(srcString.getBytes(),
				publicKey);
		//加密后的内容Base64编码
		return RSAUtil.byte2Base64(publicEncrypt);
		
	}
	/**
	 * 
	 * @Description：解密
	 * @param:@param byte2Base64
	 * @param:@return
	 * @param:@throws Exception
	 * @return String
	 * @throws
	 * @Author :tanzheng
	 * @date :2018-9-20下午02:56:42
	 */
	public static String decrypt(String byte2Base64) throws Exception{
		PrivateKey privateKey = RSAUtil.string2PrivateKey(privateKeyStr);
		//加密后的内容Base64解码
		byte[] base642Byte = RSAUtil.base642Byte(byte2Base64);
		//用私钥解密
		byte[] privateDecrypt = RSAUtil.privateDecrypt(base642Byte,
				privateKey);
		//解密后的明文
		return new String(privateDecrypt);
	}
	
	public static void main(String[] args) {
		String message = "hello, i am infi, good night!";
		try {
		
			System.out.println("公钥加密并Base64编码的结果：" + encrypt(message));
			System.out.println("解密后的明文: " +decrypt(encrypt(message)));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
 