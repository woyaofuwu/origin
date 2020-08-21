
package com.asiainfo.veris.crm.order.soa.person.busi.imslandline.order.action.finish;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.digest.DigestUtils;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.codec.binary.Base64;
import com.alibaba.fastjson.JSONObject;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeFinishAction;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeWideNetInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.auth.AbilityEncrypting;
import com.asiainfo.veris.crm.order.soa.person.busi.uopinterface.util.RSAUtil;

/**
 * 营业界面办理家庭IMS开户同步信息到杭研软终端
 * 
 * @author wangsc10 20190107
 */
public class ImsAccountAction implements ITradeFinishAction
{
	private static final String PRIVATEKEY = "f2fsdx5g9nm4bctz";//海南
    private static final String PROVINCE = "46";//海南
    
	public void executeAction(IData mainTrade) throws Exception
    {
		//从家庭IMS开户（新）页面开户办理了只能音箱的才需要走这个Action
        String phone = mainTrade.getString("RSRV_STR1","");//手机号码
    	if(!phone.equals("") && phone.startsWith("KD_")){
    		phone = phone.replace("KD_", "");
    	}
        String serialNumber = mainTrade.getString("SERIAL_NUMBER","");//固话码号
        String tradeId = mainTrade.getString("TRADE_ID");
        String toRZD = mainTrade.getString("RSRV_STR7","");//是否需要同步到杭研
        String tradeStaffId = mainTrade.getString("TRADE_STAFF_ID","");//工号
        String tradeTypeCode = mainTrade.getString("TRADE_TYPE_CODE","");
        String bizType = "";
        String password = "";
        if(tradeTypeCode.equals("6800")){
        	bizType = "1001";//必选	String	开通1001
        	password = TradeWideNetInfoQry.queryTradeWideNet(tradeId).first().getString("ACCT_PASSWD");//获取明文密码  必选	String	ims密码，bizType为 1002 销户时可不填
        }else if(tradeTypeCode.equals("6805")){
        	bizType = "1002";//必选	String	销户1002
        }
        String productId = mainTrade.getString("PRODUCT_ID","");
    	if(!tradeStaffId.equals("ITFZYC35") && productId.equals("84018059")){
    		String url = "https://devhjgh.komect.com:30443/fy/receiver/api/public/ims/synData/v2";
        	String province = "";//必选	String	省码46
        	String sign = "";//必选	String	签名
        	String content = "";//必选	String	业务参数（加密）
        	
        	String region = "";//可选	String	区号，不带0
        	String msisdn = phone;//必选	String	手机号
        	String idcard = "";//可选	String	身份证
        	String broadband = "KD_" + phone;//必选	String	宽带账号
        	String imsNum = serialNumber;//必选	String	ims码号
        	String imsAccount = serialNumber;//必选	String	Ims账号
        	String comboId = productId;//必选	String	云固话套餐id，用户前台展示套餐介绍、资费等信息（套餐介绍、资费等可由线下提供），bizType为 1002 销户、1003 修改密码时可不填
        	String type = "ims";//可选	String	固定填：ims

        	JSONObject jsonObject = new JSONObject();
        	jsonObject.put("region", region);
        	jsonObject.put("msisdn", msisdn);
        	jsonObject.put("idcard", idcard);
        	jsonObject.put("broadband", broadband);
        	jsonObject.put("imsNum", imsNum);
        	jsonObject.put("imsAccount", imsAccount);
            jsonObject.put("password", RSAUtil.encrypt(password));
            jsonObject.put("comboId", comboId);
            jsonObject.put("type", type);//固定值
            String jsonString = jsonObject.toJSONString();
             
            String paramContent = enCiper(PRIVATEKEY, jsonString);//aes 加密
            //进行md5加密生成sign
            String baowen = new StringBuilder()
                    .append("bizType").append(bizType)
                    .append("content").append(paramContent)
                    .append("province").append(PROVINCE)
                    .append(PRIVATEKEY)
                    .toString();

            String signString = DigestUtils.md5Hex(getContentBytes(baowen, "utf-8"));

            IData postBody = new  DataMap();
            postBody.put("bizType", bizType);
            postBody.put("province", PROVINCE);
            postBody.put("content", paramContent);
            postBody.put("sign", signString);
            System.out.println("");
            System.out.println(postBody.toString());
            
            imsToAbility(postBody);//调用一级能力平台，同步家庭IMS开户信息到杭研
    	}
    }
	
	private void imsToAbility(IData data) throws Exception {
		
		IData paramurl = new DataMap();
        paramurl.put("PARAM_NAME", "crm.ABILITY.TORZD");
        IDataset urls = Dao.qryBySql(AbilityEncrypting.getInterFaceSQL, paramurl, "cen");
        String url = "";
        
        if (urls != null && urls.size() > 0)
        {
           url = urls.getData(0).getString("PARAM_VALUE", "");
        }
        else
        {
           CSAppException.appError("-1", "软终端同步接口地址未在TD_S_BIZENV表中配置");
        }
        
        String apiAddress = url;
        IData stopResult = AbilityEncrypting.callAbilityPlatCommon(apiAddress,data);
        String resCode=stopResult.getString("resCode");
    	String X_RSPCODE="";
    	String X_RSPDESC="";
		if (!"00000".equals(resCode)) {
            X_RSPCODE = stopResult.getString("resCode");
            X_RSPDESC = stopResult.getString("resMsg");
            CSAppException.appError("-1", X_RSPCODE+"同步杭研信息调能力平台接口反馈结果显示失败:"+X_RSPDESC);
        }
	}
 
    private static byte[] getContentBytes(String content, String charset) {
        if (charset == null || "".equals(charset)) {
            return content.getBytes();
        }
        try {
            return content.getBytes(charset);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("MD5签名过程中出现错误,指定的编码集不对,您目前指定的编码集是:" + charset);
        }
    }


    private static String enCiper(String key, String data) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        byte[] raw = key.getBytes("utf-8");
        SecretKeySpec secretKeySpec = new SecretKeySpec(raw, "AES");
        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
        byte[] original = cipher.doFinal(data.getBytes(StandardCharsets.UTF_8));
        return new Base64().encodeAsString(original);
    }
	
}
