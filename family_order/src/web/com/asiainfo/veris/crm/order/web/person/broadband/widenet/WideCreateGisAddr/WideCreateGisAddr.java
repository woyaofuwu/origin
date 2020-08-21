package com.asiainfo.veris.crm.order.web.person.broadband.widenet.WideCreateGisAddr;

import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.log4j.Logger;
import org.apache.tapestry.IRequestCycle;
import com.ailk.biz.BizEnv;
import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.util.Utility;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class WideCreateGisAddr extends PersonBasePage{
	
	static Logger  logger=Logger.getLogger(WideCreateGisAddr.class);
	public abstract void setUrl(String url);
	public abstract void setEventUrl(String addr);
	public abstract void setOpenTag(String openTag);
	
	static String passKey="76D305FB4B7321B077AFC1B588B3F11A";

	public void init(IRequestCycle cycle) throws Exception{
	logger.debug("[WideCreateGisAddr]初始化方法获取参数开始");
	String cityCode=getVisit().getCityCode();
	String staffId=getVisit().getStaffId();
		if("".equals(cityCode))
			cityCode="HNHK";
		
        String verifyCode = RandomStringUtils.randomNumeric(4);

		 String time=new SimpleDateFormat("mmss").format(new Date());
		 String key=staffId+"&&"+time+verifyCode;
		 String passKeyValue=BizEnv.getEnvString("crm.gisAddr.key",passKey);
		 String token=encrypt(key, passKeyValue);
		 
		 logger.info("[WideCreateGisAddr]"+key+".........."+token);  
		String addr=BizEnv.getEnvString("crm.gisAddr.addr");//配置的IP与端口
		String url1=BizEnv.getEnvString("crm.gisAddr.url1");//配置的路径
		 if("".equals(addr))
			 Utility.error("GIS地址未配置！");
		// String url=addr+"/addrselection/addressSel/addressSelect.html?AREA_CODE="+cityCode+"&APPID=100&TOKEN="+token;
		String url=url1+"AREA_CODE="+cityCode+"&APPID=100&TOKEN="+token;

		 setUrl(url);
		 setEventUrl(addr);
		 
		 IData param=this.getData();
		 setOpenTag(param.getString("openTag","old"));
	}
	
	
	
	


	/**
     * 加密
     * 
     * @param content
     *            待加密内容 明文
     * @param key
     *            加密的 密钥
     * @return
     */
    public static String encrypt(String content, String key) {
        try {
            KeyGenerator kgen = KeyGenerator.getInstance("AES");
            SecureRandom random=SecureRandom.getInstance("SHA1PRNG");
            random.setSeed(key.getBytes());
            kgen.init(128, random);
            //kgen.init(128, new SecureRandom(key.getBytes()));
            SecretKey secretKey = kgen.generateKey();
            byte[] enCodeFormat = secretKey.getEncoded();
            SecretKeySpec secretKeySpec = new SecretKeySpec(enCodeFormat, "AES");
            Cipher cipher = Cipher.getInstance("AES");
            byte[] byteContent = content.getBytes("utf-8");
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
            byte[] byteRresult = cipher.doFinal(byteContent);
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < byteRresult.length; i++) {
                String hex = Integer.toHexString(byteRresult[i] & 0xFF);
                if (hex.length() == 1) {
                    hex = '0' + hex;
                }
                sb.append(hex.toUpperCase());
            }
            return sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    
    
}
