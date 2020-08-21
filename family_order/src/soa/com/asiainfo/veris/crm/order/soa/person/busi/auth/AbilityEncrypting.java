package com.asiainfo.veris.crm.order.soa.person.busi.auth;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.KeyFactory;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;
import javax.crypto.Cipher;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.log4j.Logger;
import com.ailk.biz.util.StaticUtil;
import com.ailk.bizcommon.util.IDataUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.codec.binary.Base64;
import com.ailk.org.apache.commons.lang3.ArrayUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;

/**
 * <pre>
 * 功能：httpClient访问远程接口工具类
 * 日期：2016年12月29日 上午16:18:28
 * </pre>
 */
@SuppressWarnings("deprecation")
public class AbilityEncrypting {

    private static transient Logger logger = Logger.getLogger(AbilityEncrypting.class);
	
	//用户提供，获取tocken的能力接口url，接入指南中提供了此地址，请根据实际情况修改url
	 public static StringBuilder getInterFaceSQL;
	    static
	    {
	        getInterFaceSQL = new StringBuilder().append("select t.* FROM td_s_bizenv t where t.param_name = :PARAM_NAME and t.state= 'U' ");
	    }

	//用户提供，用户的私钥
	public static final SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
	
	/**
	 * 方法体说明：向远程接口发起请求，返回字符串类型结果
	 * @param url 接口地址
	 * @param requestMethod 请求类型
	 * @param params 传递参数
	 * @return String 返回结果
	 */
	public static String httpRequest(String url, String requestBody) {
		// 接口返回结果
		String methodResult = null;
		try {
			HttpClient client = new HttpClient();
			PostMethod post = new PostMethod(url);
			post.addRequestHeader("Content-Type","application/json;charset=utf-8");
			post.setRequestBody(requestBody);
			client.executeMethod(post);
			methodResult = post.getResponseBodyAsString();
		} catch (UnsupportedEncodingException e) {
			logger.error("不支持的编码格式", e);
		} catch (IOException e) {
			logger.error("IO异常", e);
		}
		return methodResult;
	}


	/**
	 * 获取动态tocken字符串
	 * @return
	 */
	public static String getTockenStr(String appId, String staticToken) throws Exception{
		IData paramurl=new DataMap();
		paramurl.put("PARAM_NAME", "crm.accesstoken");
        IDataset urls = Dao.qryBySql(getInterFaceSQL, paramurl, "cen");
        String tockerUrl = "";
        if (urls != null && urls.size() > 0)
        {
        	tockerUrl = urls.getData(0).getString("PARAM_VALUE", "");
        }
        else
        {
            CSAppException.appError("-1", "crm.accesstoken接口地址未在TD_S_BIZENV表中配置");
        }
		String tmpStr = "{\"appId\":\""+ appId +"\", \"staticToken\":\"" +staticToken+ "\"}";
		String result = httpRequest(tockerUrl, tmpStr);
		return result.substring(result.indexOf("access_token") + 15, result.indexOf("access_token") + 55);
	}
	
	/**
	 * 将字符串类型的key转换为Key对象
	 * @return 
	 * @return
	 */
	public static PrivateKey getPrivateKey(String key){
		byte[] keyBytes;
        keyBytes = Base64.decodeBase64(key);
        final PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory;
        try {
            keyFactory = KeyFactory.getInstance("RSA");
            final PrivateKey privateKey = keyFactory.generatePrivate(keySpec);
            return privateKey;
        } catch (final Exception e) {
        	logger.error("获取私钥出错", e);
        }
        return null;
	}
	
	/**
	 * 获取sign
	 * @param tockenStr
	 * @return
	 */
	public static String getSign(String appId, String timestamp, String messageId,
			String tockenStr, String sessionId, String content, String userAuthorizationCode, 
			String userPhoneNumber, String bIPCode, String version, String nodeId){
		//组装输入参数map
		IData map=new DataMap();
		map.put("appId", appId.trim());
		map.put("timestamp", timestamp.trim());
		map.put("messageId", messageId.trim());
		map.put("access_token", tockenStr.trim());
		map.put("sessionId", sessionId.trim());
		map.put("content", content.trim());
		//以下几项为非必填项
		if (userAuthorizationCode != null && !("".equals(userAuthorizationCode.trim()))) {
			map.put("userAuthorizationCode", userAuthorizationCode);
		}
		if (userPhoneNumber != null && !("".equals(userPhoneNumber.trim()))) {
			map.put("userPhoneNumber", userPhoneNumber);
		}
		if (bIPCode != null && !("".equals(bIPCode.trim()))) {
			map.put("bIPCode", bIPCode);
		}
		if (version != null && !("".equals(version.trim()))) {
			map.put("version", version);
		}
		if (nodeId != null && !("".equals(nodeId.trim()))) {
			map.put("nodeId", nodeId);
		}
		logger.debug("-----map:----- "+map);
		//给map排序后转换为字符串
		StringBuilder sb = new StringBuilder();
        final Map<String, Object> sortMap = new TreeMap<String, Object>(new Comparator<String>() {
            @Override
            public int compare(final String str1, final String str2) {
                return str1.compareTo(str2);
            }
        });
        sortMap.putAll(map);
        logger.debug("-----sortMap:----- "+sortMap);
        for (final Map.Entry<String, Object> entry : sortMap.entrySet()) {
            sb = sb.append(entry.getKey()).append(entry.getValue());
        }
        String dest =  sb.toString();
        logger.debug("-----dest:----- "+dest);
        //sha256加密
        MessageDigest md = null;
        String strDes = null;
        try {
            md = MessageDigest.getInstance("SHA-256");
            try {
            	byte[] src = md.digest(dest.getBytes("UTF-8"));
            	logger.debug("-----src:----- "+src);
            	final StringBuilder stringBuilder = new StringBuilder("");
                for (int i = 0; i < src.length; i++) {
                    final int v = src[i] & 0xFF;
                    final String hv = Integer.toHexString(v);
                    if (hv.length() < 2) {
                        stringBuilder.append(0);
                    }
                    stringBuilder.append(hv);
                }
                strDes = stringBuilder.toString();
                logger.debug("-----strDes:----- "+strDes);
            } catch (final UnsupportedEncodingException e) {
            	logger.error("出现不支持的编码格式", e);
            }
        } catch (final NoSuchAlgorithmException e) {
        	logger.error("无法识别的算法", e);
        }
        //私钥加密
        byte[] enBytes = null;
        byte[] data;
        try {
        	IDataset paramInfos = CommparaInfoQry.getCommparaInfoByCode("CSM", "9999", "ABILITY_PRIVATE_KEY", "1", "ZZZZ");
        	if(IDataUtil.isEmpty(paramInfos)){
        	 CSAppException.appError("-1", "ABILITY_PRIVATE_KEY在COMPARA表中未配置！");	
        	}
        	String privateKey=paramInfos.getData(0).getString("PARA_CODE24");
            data = strDes.getBytes("UTF-8");
            logger.debug("-----privateKey:----- "+privateKey+"-----data加密-----"+data);
            final Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.ENCRYPT_MODE, getPrivateKey(privateKey));
            for (int i = 0; i < data.length; i += 64) {
            	// 注意要使用2的倍数，否则会出现加密后的内容再解密时为乱码
                final byte[] doFinal = cipher.doFinal(ArrayUtils.subarray(data, i, i + 64));
                enBytes = ArrayUtils.addAll(enBytes, doFinal);
            }
            logger.debug("-----enBytes:----- "+enBytes);
            try {
            	 logger.debug("----- Base64.encodeBase64String(enBytes):----- "+ Base64.encodeBase64String(enBytes));
                return Base64.encodeBase64String(enBytes);
            } catch (final Exception e) {
            	logger.error("Base64出错", e);
            }
        } catch (final Exception e) {
        	logger.error("私钥加密出错", e);
        }
        return "";
	}
	//call 能力平台（公用）
    public final static IData callAbilityPlatCommon( String apiAddress,IData data) throws Exception{
        //类型
        String formatType="json";
        //time
        SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMddHHmmss"); //精确到毫秒
        String timestamp = fmt.format(new Date()); // 时间戳，自动生成
        IData ret=new DataMap();
        String content= data.toString();
        try{
        // 唯一流水32位以内
        String sessionId = UUID.randomUUID().toString().replaceAll("-", "");// 请求流水号，自动生成
        String appId=StaticUtil.getStaticValue("ABILITY_APP_ID", "1");// 应用ID
        String messageId=SysDateMgr.getSysDateYYYYMMDDHHMMSS()+timestamp+SeqMgr.getLogId().substring(12);// 业务流水号（32位）
        String staticToken=StaticUtil.getStaticValue("ABILITY_STATIC_TOKEN", "1");// 应用的静态tocken
        String access_token=AbilityEncrypting.getTockenStr(appId,staticToken);
     // 以下5项为非必填项,如果不填请给""或者null
		String userAuthorizationCode = ""; // 用户授权码，申请用户授权时，或者需要用户授权时填写
	    String userPhoneNumber = ""; // 用户手机号
		String bIPCode = ""; // 业务流程编码
		String version = ""; // 业务流程版本号，若填写bIPCode，该字段必填
		String nodeId = ""; // 本次能力在流程中所处的节点编码，若填bIPCode，该字段必填
        String sign = AbilityEncrypting.getSign(appId, timestamp, messageId, access_token, sessionId, content,
				userAuthorizationCode, userPhoneNumber, bIPCode, version, nodeId);
        //组合参数
//        String publicParam="&appId="+appId+"&access_token="+access_token+"&sign="+sign+"&timestamp="+timestamp+
//                           "&messageId="+messageId+"&sessionId="+sessionId;
//        String requestUrl = apiAddress+publicParam;
        IData input=new DataMap();
        input.put("appId", appId);
        input.put("access_token", access_token);
        input.put("sign", sign);
        input.put("timestamp", timestamp);
        input.put("messageId", messageId);
        input.put("sessionId", sessionId);
        input.put("content", data);  
        logger.debug("-----input:----- "+input);
        String inputStr =input.toString();
        logger.debug("inputStr: "+inputStr);
        String result = httpRequest(apiAddress, inputStr);
        logger.debug("-----result:----- "+result);
        ret=new DataMap(result); 
        }catch(Exception ex){
        	 throw new Exception("API:get CityCode failed:" + ex.getMessage());
        }
        return ret;
    }
  /**
     * 获取动态请求url字符串
     * CRM权益中心相关需求
     * @return
     */
    public static String getApiAddress(String apiAddrConf) throws Exception{
        String abilityurl = "";
        IData param1 = new DataMap();
        param1.put("PARAM_NAME", apiAddrConf);
        IDataset abilityurls = Dao.qryBySql(getInterFaceSQL, param1, "cen");
        if (abilityurls != null && abilityurls.size() > 0) {
            abilityurl = abilityurls.getData(0).getString("PARAM_VALUE", "");
        } else {
            CSAppException.appError("-1", apiAddrConf+"接口地址未在TD_S_BIZENV表中配置");
        }
        return abilityurl;
    }
}
    
    	
   
//	public static void main(String[] args) {
//		LOGGER.info("测试：");
//		//用户提供，远程调用的一些报文参数，用户填写
//		//以下6项为必填项
//		String appId = "";									//应用ID，用户提供
//		String timestamp = formatter.format(new Date().getTime());				//时间戳，自动生成
//		String messageId = "80e5adaec47d41748628e1715d903ad0";					//业务流水号，用户提供
//		String staticToken = "";												//应用的静态tocken，用户提供
//		String sessionId = UUID.randomUUID().toString().replaceAll("-", "");	//请求流水号，自动生成		
//		String content = "";													//报文内容，用户提供
//		//以下5项为非必填项,如果不填请给""或者null
//		String userAuthorizationCode = "";										//用户授权码，申请用户授权时，或者需要用户授权时填写
//		String userPhoneNumber = "";											//用户手机号
//		String bIPCode = "";													//业务流程编码
//		String version = "";													//业务流程版本号，若填写bIPCode，该字段必填
//		String nodeId = "";														//本次能力在流程中所处的节点编码，若填bIPCode，该字段必填
//		
//		//获取tocken字符串
//		String tockenStr = CallDemo.getTockenStr(appId, staticToken);
//		
//		LOGGER.info("动态授权返回：" + tockenStr);
//		
//		//获取sign字符串
//		String signStr = CallDemo.getSign(appId, timestamp, messageId,
//				tockenStr, sessionId, content, userAuthorizationCode, 
//				userPhoneNumber, bIPCode, version, nodeId);
//		
//		//用户提供，接口url
//		final String url = "";
//		
//		//封装调用参数json字符串
//		String inputStr = "{\"appId\":\""+ appId +"\","
//				+ " \"timestamp\":\""+ timestamp +"\","
//				+ " \"messageId\":\""+ messageId +"\","
//				+ " \"access_token\":\"" + tockenStr + "\","
//				+ " \"sessionId\":\"" + sessionId + "\", "
//				+ " \"sign\":\"" + signStr + "\","
//				//以下几项请根据报文的实际情况打开注释，如果填写了相应的非必填项，只打开对应的行即可
//				//+ " \"userAuthorizationCode\":\"" + userAuthorizationCode + "\","
//				//+ " \"userPhoneNumber\":\"" + userPhoneNumber + "\","
//				//+ " \"bIPCode\":\"" + bIPCode + "\","
//				//+ " \"version\":\"" + version + "\","
//				//+ " \"nodeId\":\"" + nodeId + "\","
//				//+ " \"publicKey\":\"" + PUBLIC_KEY + "\","
//				+ " \"content\":"+ content +"}";
//		LOGGER.info("入参报文：" + inputStr);
//		//远程调用
//		String result = CallDemo.httpRequest(url, inputStr);  
//		LOGGER.info("能力调用返回：" + result);
//}
