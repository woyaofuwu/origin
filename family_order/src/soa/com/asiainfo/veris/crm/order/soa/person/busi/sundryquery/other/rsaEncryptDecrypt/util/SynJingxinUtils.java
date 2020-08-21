
package com.asiainfo.veris.crm.order.soa.person.busi.sundryquery.other.rsaEncryptDecrypt.util;

import java.util.Map;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.ailk.service.client.request.Wade3ClientRequest;
import com.ailk.service.server.hessian.wade3tran.Wade3DataTran;
import com.alibaba.fastjson.JSON;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;

/**
 * 
 * @author tanzheng
 * 同步京信平台工具类
 *
 */
public final class SynJingxinUtils
{
	protected static Logger log = Logger.getLogger(SynJingxinUtils.class);
	static String userPrivateKey = "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBANBJlGnUaO0vTe/Yv1TZ0kl7qDhxbgw/ir+9XwQHsdnjg+aYAnwf/95vi+9Wn/2aOmMVl/6E4tpKBNkt/yMzK/xgNVv64lDMpuFCy+xXQfq4ti34q7yd80gRzEJTOGVSU4qMsedsxnZJ7MrOekfb04vkh2458UxZ4xofKSpZnWe1AgMBAAECgYA9cYdDlPKDDZJbL8B5gmxDWyQrymJhYJlIqM+Pu8lecnxlmPAat8HeV1+bch1uTYHt2t/kqETXikBV5Pi/5IeTum+DVfYZaDzY4Y9Gx8XC45UVqOcuWtTDVV0ihUOeLqKgoA/cgqPrW8fg8nhPaIFfoYnIZ4jTt00igVrWIsdhHQJBAO1dqB6GKNeVwhOjXJ+4ClNf7CKCrCCateK2OxVjayb63l1Vy3NHzZs9NLB2FiXd6iW5ggvCo5UpD9WcfW9SR8sCQQDgo4ie4v2RAoqOFd971qb0Diqp9keI6ubgJYkmUCGqalVdJ/TMnP7WE5cyzWOHNM5DyMyAlOu+KS33iiqR5h5/AkBvOjHri8Zbr2twNF7U4TLFeu92BHGQHa8Ze3cYQKwXafsFFYzuijkIg5fLEKq0N9besp0D0cNBxCoNWPVWUuIdAkAHq834jwvVrd6JZ5nQIs5kuy8dyrk6pbrooJ+EXWwo46Syn7CuZBYXvWeGZpklftokWnERWwSJ4+Ib9+3oOZGFAkEArbySLX8XmubkiOvZRCesZ8DPoBk/yZ1GgicBnC1ldVokxafXlQPqUPWAaYcY6SO5Ss99yT8yWK/t3IhKnHvzqg==";
	static String wxtPublicKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDR6/BmJ3/Axz5T1zNGjFH05Y22wtzSRpoWBu4uu40YOIhTg90+Ea7j73q3cviAIanhdUKOprV1D6EmtktenQ7on0tFKcdITmJrO8W+k8yhUfF1GuY/n5Y9xa/+ijAu0IdVbS21gErdoMGPWRVRgv8ZRChVK1S4eXRBPqhTsZt74wIDAQAB";
	static String appId = "216309912337342464";
    /**
     * 获取京信平台url 
     * @return
     * @throws Exception
     */
    public static String getJingxinUrl() throws Exception
    {
        return getSettingForJingxin().getString("PARA_CODE17", "");
    }
    public static IData getSettingForJingxin() throws Exception {
    	
    	//PARA_CODE23:私钥，PARA_CODE24:公钥,PARA_CODE1:appId,PARA_CODE3:url
		return CommparaInfoQry.getCommByParaAttr("CSM","314","ZZZZ").first();
		
	}
    /**
     * 获取京信平台私钥参数
     * @return
     * @throws Exception
     */
    public static String getJingxinPrivateKey() throws Exception
    {
        return getSettingForJingxin().getString("PARA_CODE24", userPrivateKey);
    }

    
    /**
     * 获取京信平台公钥参数
     * @return
     * @throws Exception
     */
    public static String getJingxinPublicKey() throws Exception
    {
    	return getSettingForJingxin().getString("PARA_CODE23", wxtPublicKey);
    }
    /**
     * 
     * @return 获取京信平台appId
     * @throws Exception
     */
    public static String getJingxinAppId() throws Exception
    {
    	return getSettingForJingxin().getString("PARA_CODE1", appId);
    }
    /**
     * 获取签名
     * @param appId
     * @param timeStamp
     * @param userPrivateKey
     * @param wxtPublicKey
     * @return
     * @throws Exception
     */
	public static String getSign(IData data, String userPrivateKey) throws Exception {
		String json = JSON.toJSONString(data);
		return  RSAUtil.sign(json, userPrivateKey);
		
	}
	/**
	 * 数据加密
	 * @param json
	 * @param publicKey
	 * @return
	 * @throws Exception
	 */
    public static String getData(IData data,String publicKey) throws Exception{
    	String json = JSON.toJSONString(data);
    	return RSAUtil.encrypt(json, publicKey);
    }

   /**
    * 调用第三方接口
    * @param svcName
    * @param inparams
    * @return
    * @throws Exception
    */
    public static IData post(String svcName, IData inparams) throws Exception
    {
    	 log.info("同步京信平台svcName:"+svcName);
    	 log.info("同步京信平台inparams:"+inparams);
    	 String sign = getSign(inparams,getJingxinPrivateKey());
    	 String data = getData(inparams,getJingxinPublicKey());
    	 String appId = getJingxinAppId();
    	 String url = getJingxinUrl()+svcName;
    	 String inparams2String = "data="+data+"&appId="+appId+"&sign="+sign;
    	 inparams2String = inparams2String.replace("+", "%2B");
         String result = Wade3ClientRequest.request(url, svcName, inparams2String, "UTF-8"); 
         log.info("调用京信平台返回结果："+result);
         IData returnData =   Wade3DataTran.wade3To4DataMap(JSON.parseObject(result, Map.class));
         //如果存在value值，则进行验签并解密
         if("1".equals(returnData.getString("code"))){
        	 throw new Exception(returnData.getString("message"));
         }
		 String valueString = returnData.getString("value");
         
         if(data!=null && StringUtils.isNotBlank(valueString)){
        	 Map<String, String> map = JSON.parseObject(valueString, Map.class);
 			String returnSign = map.get("sign");
 			String data2 = map.get("data");
 			String decrypt = RSAUtil.decrypt(data2, userPrivateKey);
        	 if (RSAUtil.verify(decrypt, returnSign, wxtPublicKey)) {
 				System.out.println("签名验证成功");
 				returnData.put("value",decrypt);
 			} else {
 				log.error("验签失败");
 				throw new Exception("验签失败");
 			}
         }
         
         return returnData;
    }
    
    
    
}
