
package com.asiainfo.veris.crm.order.soa.person.busi.bank;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.service.client.request.Wade3ClientRequest;
import com.ailk.service.server.hessian.wade3tran.Wade3DataTran;
import com.asiainfo.veris.crm.order.pub.exception.AcctDayException;
import com.asiainfo.veris.crm.order.pub.exception.BankPaymentManageException;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bre.query.BreQryForCommparaOrTag;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserBankMainSignInfoQry;

import java.io.InputStreamReader;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.PostMethod;

import java.net.URISyntaxException;
import java.io.UnsupportedEncodingException;
import java.io.File;
import java.net.URI;
import com.sun.jersey.api.client.*;
import com.sun.jersey.core.util.MultivaluedMapImpl;
import java.security.Signature;
import java.security.PrivateKey;
import java.security.Security;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import sun.misc.BASE64Encoder;
import sun.misc.BASE64Decoder;
import java.security.KeyFactory;
import java.security.spec.EncodedKeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.interfaces.RSAPrivateKey;

import javax.ws.rs.core.MediaType;

public class BankIntfConnectBean extends CSBizBean
{
   
	private static Logger logger = Logger.getLogger(BankIntfConnectBean.class);

    public IData bankBindRegister(IData param) throws Exception
    {
    	IData result = new DataMap();
    	String bankCard = param.getString("RSRV_STR1","");
    	if("".equals(bankCard)){
    		CSAppException.apperr(CrmCommException.CRM_COMM_103,"获取不到银行卡号参数！");
    	}
    	
    	if("".equals(param.getString("SERIAL_NUMBER",""))){
    		CSAppException.apperr(CrmCommException.CRM_COMM_103,"请传SERIAL_NUMBER参数！");
    	}
    	
    	param.put("RSRV_STR1", bankCard);
    	if (logger.isDebugEnabled())
        {
    		logger.debug("AAAAAAAAAAAA" + param.toString());
        }
    	
    	result = Register(param);
    	if (logger.isDebugEnabled())
        {
    		logger.debug("AAAAAAAAAAAA" + param.toString());
        }
    	
    	return result;
    }
    
    public  IData Register(IData param) throws Exception {
		
		IData result = new DataMap();
    	result.put("X_RESULTCODE", "0");
		result.put("X_RESULTINFO", "OK");
		
	    IData retMap = getBankUrl(param);
    	String strUrl = retMap.getString("PARA_CODE25");
    	Client client = Client.create();
	    URI u = new URI(strUrl);
	    WebResource resource = client.resource(u);
	    
	    IData params = new DataMap();
	    //venderId:10;onlTransPwd:11;svrId:13;infSource:14;ADDR:25
		params.put("appId", retMap.getString("PARA_CODE10",""));
		//params.put("onlTransPwd", retMap.getString("PARA_CODE11",""));
		IData data = new DataMap();
		data.put("mobile", param.getString("SERIAL_NUMBER"));
		//data.put("openService", "215");
		data.put("oprTp", "1");
		data.put("svrTp", "22");
		String strbindCardNo = param.getString("RSRV_STR1","").length() + param.getString("RSRV_STR1",""); //银行卡千前面带上银行卡长度
		data.put("cardNo", strbindCardNo);
		//data.put("pass_word", param.getString("RSRV_STR3",""));
		//data.put("needAuth", "0");
		data.put("indUsrId",param.getString("SERIAL_NUMBER","-1"));//应银联要求，将原传USER_id字段，改为传手机号码
		data.put("svrId",retMap.getString("PARA_CODE13",""));
		data.put("infSource",retMap.getString("PARA_CODE14",""));
		//String data = "{\"openService\":\"215\", \"password\":\"aaa111\", \"mobile\":\"13562264413\", \"bindCardNo\":\"13562264413\"}";
		params.put("data", data);
		
		//签名信息---对请求报文中的“请求数据(data)”的签名，使用接入系统的私钥签名（要求将对应公钥提交给持卡人增值服务运营方），公钥证书格式要求使用PKCS#8 
//		try{
//			File priKey = new File(this.getClass().getClassLoader().getResource("hnyd.pk8").getFile());
//			String testSign= sign(data.toString(), priKey);
//			params.put("signToken",  testSign);
//		}catch (Exception e){
//			CSAppException.apperr(CrmCommException.CRM_COMM_103, "读取pk8/hnyd.pk8的私钥生成签名信息报错！");
//		}
		
		String strResult = "";
		try{
			if(logger.isDebugEnabled()){
				logger.debug("BANK_PARAM++++++++++++++" + params.toString());
			}
			
			strResult = resource.type(MediaType.APPLICATION_JSON).post(String.class, params.toString());
			if(logger.isDebugEnabled()){
				logger.debug("BANK_RESULT+++++++++++++" + strResult);
			}
			
		} catch (Exception e){
			CSAppException.apperr(CrmCommException.CRM_COMM_103, "调银联接口异常！");
		}
		
		//返回结果
		IData rtData = new DataMap(strResult);
		if("000000".equals(rtData.getString("respCd"))){
			result.put("X_RESULTCODE", "0");
			result.put("X_RESULTINFO", rtData.getString("msg",""));
			
		}
		else if("500101".equals(rtData.getString("respCd")))
		{
			//已绑定过的银联卡，视为绑定成功
			result.put("X_RESULTCODE", "0");
			result.put("X_RESULTINFO", rtData.getString("msg",""));
//			result.put("X_RESULTCODE", "-1");
//			result.put("X_RESULTINFO", rtData.getString("msg"));
//			CSAppException.apperr(CrmCommException.CRM_COMM_103, "[" + rtData.getString("respCd") + "]-该银行卡号权益已经绑定过，重复绑定！" + result.getString("X_RESULTINFO"));
		}
		else
		{
			result.put("X_RESULTCODE", "-1");
			result.put("X_RESULTINFO", rtData.getString("msg"));
			CSAppException.apperr(CrmCommException.CRM_COMM_103, "[" + rtData.getString("respCd") + "]银联接口返回报错-" + result.getString("X_RESULTINFO"));
		}

		return result;
	}


    public IData UnbankBindRegister(IData param) throws Exception
    {
    	IData result = new DataMap();
    	String bankCard = param.getString("RSRV_STR1","");
    	if("".equals(bankCard)){
    		CSAppException.apperr(CrmCommException.CRM_COMM_103,"获取不到银行卡号参数！");
    	}
    	
    	if("".equals(param.getString("USER_ID",""))){
    		CSAppException.apperr(CrmCommException.CRM_COMM_103,"请传USER_ID参数！");
    	}
    	
    	param.put("RSRV_STR1", bankCard);
    	if (logger.isDebugEnabled())
        {
    		logger.debug("AAAAAAAAAAAA" + param.toString());
        }
    	
    	result = UnRegister(param);
    	if (logger.isDebugEnabled())
        {
    		logger.debug("AAAAAAAAAAAA" + param.toString());
        }
    	
    	return result;
    }
	
public  IData UnRegister(IData param) throws Exception {
		
		IData result = new DataMap();
    	result.put("X_RESULTCODE", "0");
		result.put("X_RESULTINFO", "OK");
		
	    IData retMap = getBankUrl(param);
	    String strQryUrl = retMap.getString("PARA_CODE23");
	    Client Qryclient = Client.create();
	    URI uri = new URI(strQryUrl);
	    WebResource Qryresource = Qryclient.resource(uri);
	    IData reqData = new DataMap();
	    IData Qrydata = new DataMap();
	    
	    //拼查询银联用户信息接口请求参数
	    reqData.put("appId", retMap.getString("PARA_CODE10",""));
	    Qrydata.put("mobile", param.getString("SERIAL_NUMBER"));
	    reqData.put("data", Qrydata);
	    
		String QryResult = "";
		try{
			if(logger.isDebugEnabled()){
				logger.debug("BANK_PARAM++++++++++++++" + reqData.toString());
			}
			
			QryResult = Qryresource.type(MediaType.APPLICATION_JSON).post(String.class, reqData.toString());
			if(logger.isDebugEnabled()){
				logger.debug("BANK_RESULT+++++++++++++" + QryResult);
			}
			
		} catch (Exception e){
			CSAppException.apperr(CrmCommException.CRM_COMM_103, "调银联接口异常！");
		}
		
		//返回结果
		IData QryRes = new DataMap(QryResult);
    	String strUrl = retMap.getString("PARA_CODE24");
    	Client client = Client.create();
	    URI u = new URI(strUrl);
	    WebResource resource = client.resource(u);
	    
	    
	    
	    IData params = new DataMap();
	    //venderId:10;onlTransPwd:11;svrId:13;infSource:14;ADDR:25
		params.put("appId", retMap.getString("PARA_CODE10",""));
		//params.put("onlTransPwd", retMap.getString("PARA_CODE11",""));
		IData data = new DataMap();
		//data.put("mobile", param.getString("SERIAL_NUMBER"));
		//data.put("openService", "215");
		//data.put("oprTp", "1");
		//data.put("svrTp", "22");
		String strbindCardNo = param.getString("RSRV_STR1","").length() + param.getString("RSRV_STR1",""); //银行卡千前面带上银行卡长度
		data.put("cardNo", strbindCardNo);
		//data.put("pass_word", param.getString("RSRV_STR3",""));
		//data.put("needAuth", "0");
		data.put("userId",QryRes.getData("data").getString("userId","-1"));
		//data.put("svrId",retMap.getString("PARA_CODE13",""));
		//data.put("infSource",retMap.getString("PARA_CODE14",""));
		//String data = "{\"openService\":\"215\", \"password\":\"aaa111\", \"mobile\":\"13562264413\", \"bindCardNo\":\"13562264413\"}";
		params.put("data", data);
		
		//签名信息---对请求报文中的“请求数据(data)”的签名，使用接入系统的私钥签名（要求将对应公钥提交给持卡人增值服务运营方），公钥证书格式要求使用PKCS#8 
		try{
			File priKey = new File(this.getClass().getClassLoader().getResource("hnyd.pk8").getFile());
			String testSign= sign(data.toString(), priKey);
			params.put("signToken",  testSign);
		}catch (Exception e){
			CSAppException.apperr(CrmCommException.CRM_COMM_103, "读取pk8/hnyd.pk8的私钥生成签名信息报错！");
		}
		
		String strResult = "";
		try{
			if(logger.isDebugEnabled()){
				logger.debug("BANK_PARAM++++++++++++++" + params.toString());
			}
			
			strResult = resource.type(MediaType.APPLICATION_JSON).post(String.class, params.toString());
			if(logger.isDebugEnabled()){
				logger.debug("BANK_RESULT+++++++++++++" + strResult);
			}
			
		} catch (Exception e){
			CSAppException.apperr(CrmCommException.CRM_COMM_103, "调银联接口异常！");
		}
		
		//返回结果
		IData rtData = new DataMap(strResult);
		if("000000".equals(rtData.getString("respCd"))){
			result.put("X_RESULTCODE", "0");
			result.put("X_RESULTINFO", rtData.getString("msg",""));
			
		}
		else if("100150".equals(rtData.getString("respCd")))
		{
			result.put("X_RESULTCODE", "-1");
			result.put("X_RESULTINFO", rtData.getString("msg"));
			CSAppException.apperr(CrmCommException.CRM_COMM_103, "[" + rtData.getString("respCd") + "]-服务状态编码不对！" + result.getString("X_RESULTINFO"));
		}
		else if("300000".equals(rtData.getString("respCd")))
		{
			result.put("X_RESULTCODE", "-1");
			result.put("X_RESULTINFO", rtData.getString("msg"));
			CSAppException.apperr(CrmCommException.CRM_COMM_103, "[" + rtData.getString("respCd") + "]-参数不可为空！" + result.getString("X_RESULTINFO"));
		}
		else if("300002".equals(rtData.getString("respCd")))
		{
			result.put("X_RESULTCODE", "-1");
			result.put("X_RESULTINFO", rtData.getString("msg"));
			CSAppException.apperr(CrmCommException.CRM_COMM_103, "[" + rtData.getString("respCd") + "]-参数值无效！" + result.getString("X_RESULTINFO"));
		}
		else if("300200".equals(rtData.getString("respCd")))
		{
			result.put("X_RESULTCODE", "-1");
			result.put("X_RESULTINFO", rtData.getString("msg"));
			CSAppException.apperr(CrmCommException.CRM_COMM_103, "[" + rtData.getString("respCd") + "]-用户不存在！" + result.getString("X_RESULTINFO"));
		}
		else if("300508".equals(rtData.getString("respCd")))
		{
			result.put("X_RESULTCODE", "-1");
			result.put("X_RESULTINFO", rtData.getString("msg"));
			CSAppException.apperr(CrmCommException.CRM_COMM_103, "[" + rtData.getString("respCd") + "]-卡不存在！" + result.getString("X_RESULTINFO"));
		}
		else if("300521".equals(rtData.getString("respCd")))
		{
			result.put("X_RESULTCODE", "-1");
			result.put("X_RESULTINFO", rtData.getString("msg"));
			CSAppException.apperr(CrmCommException.CRM_COMM_103, "[" + rtData.getString("respCd") + "]-钱包服务开通异常，解绑卡操作频繁！" + result.getString("X_RESULTINFO"));
		}
		else if("999999".equals(rtData.getString("respCd")))
		{
			result.put("X_RESULTCODE", "-1");
			result.put("X_RESULTINFO", rtData.getString("msg"));
			CSAppException.apperr(CrmCommException.CRM_COMM_103, "[" + rtData.getString("respCd") + "]-系统未知异常！" + result.getString("X_RESULTINFO"));
		}
		else
		{
			result.put("X_RESULTCODE", "-1");
			result.put("X_RESULTINFO", rtData.getString("msg"));
			CSAppException.apperr(CrmCommException.CRM_COMM_103, "[" + rtData.getString("respCd") + "]银联接口返回报错-" + result.getString("X_RESULTINFO"));
		}

		return result;
	}

	public String sign(String str2Sign,File privateKeyFile) throws Exception {
		byte[] sign=null;
		try {
			byte[] data = str2Sign.getBytes("UTF-8");
			Signature signature = Signature.getInstance("SHA1withRSA");
			PrivateKey privateKey = getPrivateKey(privateKeyFile);
			if(privateKey == null ){
				privateKey = getPrivateKey(null);
			}
			if(privateKey ==  null)
			{
				throw new Exception();
			}
			// 初始化签名，由私钥构建
			signature.initSign(privateKey);
			signature.update(data);
			sign =  signature.sign();
		} catch (Exception e) {
			CSAppException.apperr(CrmCommException.CRM_COMM_103, "204:签名出错！");
		}
		try {
			return byteArr2HexStr(sign);
		} catch (Exception e) {
			CSAppException.apperr(CrmCommException.CRM_COMM_103,"205:将byte数组转换为表示16进制值的字符串出错！");
		}
		return null;
	}
	
	/**
	 * 网厅手机号码和银行卡绑定接口
	 * @author wukw3
	 * @param pd
	 * @param inparam
	 * @return resultInfo
	 * @throws Exception
	 */
	public IData bankNetBind(IData inparam) throws Exception {
		IData resultInfo = new DataMap();
		IData tempData = new DataMap();
		
/*		if("".equals(inparam.getString("LOGIN_PASSWD",""))){
			CSAppException.apperr(CrmCommException.CRM_COMM_103,"206:登录密码为必传");
		}
		
		String password = inparam.getString("LOGIN_PASSWD","");
		if(password.length() < 6 || password.length() > 20){
			CSAppException.apperr(CrmCommException.CRM_COMM_103,"请输入最小6位，最大20位的密码！");
		}
		
		String reg = "^[0-9]*$";
		if(password.matches(reg)){
			CSAppException.apperr(CrmCommException.CRM_COMM_103,"密码不能全是数字！");
		}
		reg = "^[A-Za-z]+$";
		if(password.matches(reg)){
			CSAppException.apperr(CrmCommException.CRM_COMM_103,"密码不能全是字母！");
		}
		reg = "^[A-Za-z0-9]+$";
		if(!password.matches(reg)){
			CSAppException.apperr(CrmCommException.CRM_COMM_103,"密码由字母和数字组成，不能全为字母和数字！");
		}*/
		
		if("".equals(inparam.getString("BANK_NAME",""))){
			CSAppException.apperr(CrmCommException.CRM_COMM_103,"206:银行开户行为必传");
		}
		
		if("".equals(inparam.getString("BANK_CARD_NO",""))){
			CSAppException.apperr(CrmCommException.CRM_COMM_103,"206:银行卡号为必传");
		}
		tempData.put("BANK_CARD", inparam.getString("BANK_CARD_NO",""));
		tempData.put("PASS_WORD", inparam.getString("LOGIN_PASSWD",""));
		
		tempData.putAll(inparam);
		
//		bankBindBean bean = new bankBindBean();
//		setBaseBean(bean);
//		pd.setData(tempData);
//		
//		TradeData td = getTradeData(pd, inparam);
//		td.setBaseCommInfo(tempData);
//		resultInfo = super.execute(pd, td);
		
		return resultInfo;
	}
	

	/**  
	   * 将byte数组转换为表示16进制值的字符串， 如：byte[]{8,18}转换为：0813， 和public static byte[]  
	   * hexStr2ByteArr(String strIn) 互为可逆的转换过程  
	   *   
	   * @param arrB  
	   *            需要转换的byte数组  
	   * @return 转换后的字符串  
	   * @throws Exception  
	   *             本方法不处理任何异常，所有异常全部抛出  
	   */
	  public static String byteArr2HexStr(byte[] arrB) throws Exception {
	    int iLen = arrB.length;
	    // 每个byte用两个字符才能表示，所以字符串的长度是数组长度的两倍   
	    StringBuffer sb = new StringBuffer(iLen * 2);
	    for (int i = 0; i < iLen; i++) {
	      int intTmp = arrB[i];
	      // 把负数转换为正数   
	      while (intTmp < 0) {
	        intTmp = intTmp + 256;
	      }
	      // 小于0F的数需要在前面补0   
	      if (intTmp < 16) {
	        sb.append("0");
	      }
	      sb.append(Integer.toString(intTmp, 16));
	    }
	    return sb.toString();
	  }
	  
	  private PrivateKey getPrivateKey(File priKeyFile){
			File file = priKeyFile;
			
			try {
				BufferedReader br = new BufferedReader(new FileReader(file));
				String s = br.readLine();
				String str = "";
				s = br.readLine();
				while (s.charAt(0) != '-'){
					str += s + "\r";
					s = br.readLine();
				}
		
				BASE64Decoder base64decoder = new BASE64Decoder();
				byte[] b = base64decoder.decodeBuffer(str);
				KeyFactory keyFactory = KeyFactory.getInstance("RSA");
				EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(b);
				RSAPrivateKey priKey = (RSAPrivateKey)keyFactory.generatePrivate(privateKeySpec);
				return priKey;
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}
	  
	  /**
		 * 获取网银接口参数
		 * @author wukw3
		 * @param PageData,TradeData
		 * @return IData
		 * @exception
		 */
		public IData getBankUrl(IData input) throws Exception {
			
			IData outParam = new DataMap();
			IData iParamAttr = new DataMap();
			
			IDataset tempInfos = BreQryForCommparaOrTag.getCommpara("CSM", 203, "1", input.getString("EPARCHY_CODE"));
			if ( tempInfos != null && tempInfos.size() > 0)
			{
				IData tempInfo = new DataMap();
				tempInfo = (IData) tempInfos.get(0);
				outParam.put("OcxVersion", tempInfo.getString("PARA_CODE3"));//版本号
				outParam.put("Separator", tempInfo.getString("PARA_CODE6"));
				outParam.put("PARA_CODE10", tempInfo.getString("PARA_CODE10"));//venderId商户ID
				outParam.put("PARA_CODE11", tempInfo.getString("PARA_CODE11"));//onlTransPwd是密码
				outParam.put("PARA_CODE13", tempInfo.getString("PARA_CODE13"));//svrId是增值服务标识码
				outParam.put("PARA_CODE14", tempInfo.getString("PARA_CODE14"));//infSource是业务来源
				outParam.put("PARA_CODE23", tempInfo.getString("PARA_CODE23"));//查询银联用户信息地址
				outParam.put("PARA_CODE24", tempInfo.getString("PARA_CODE24"));//解绑连接地址
				outParam.put("PARA_CODE25", tempInfo.getString("PARA_CODE25"));//连接地址
				return outParam;
			}
			else
			{
				CSAppException.apperr(CrmCommException.CRM_COMM_103,"203:获取网银参数失败！");
				return null;
			}
		}

}
