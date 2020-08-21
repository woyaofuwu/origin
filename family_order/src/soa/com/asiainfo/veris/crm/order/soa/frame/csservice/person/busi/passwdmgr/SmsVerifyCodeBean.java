package com.asiainfo.veris.crm.order.soa.frame.csservice.person.busi.passwdmgr;

import org.apache.commons.lang.RandomStringUtils;

import com.ailk.biz.util.StaticUtil;
import com.ailk.cache.memcache.util.SharedCache;
import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.sms.SmsSend;

public class SmsVerifyCodeBean extends CSBizBean{
	
	private final String SMS_VERIFY_CODE_TMPALET = "尊敬的客户，您的验证码为：VERIFY_CODE,有效期10分钟。中国移动。";
	private final String SMS_VERIFY_CODE_WIDENET_TMPALET = "尊敬的客户，您好，您办理的TRADE_TYPE确认码为:VERIFY_CODE,有效期5分钟。中国移动。";
	public static String VERIFY_CODE_CACHE_KEY = "com.ailk.csservice.person.busi.passwdmgr.SmsVerifyCodeBean_";
	
	/**
	 * 发送短信验证码
	 * @param input
	 * @return
	 * @throws Exception
	 */
	public IData sendSmsVerifyCode(IData input) throws Exception{
		IData data = new DataMap();
		String serialNumber = input.getString("SERIAL_NUMBER");
		
		String tradeTypeCode = input.getString("TRADE_TYPE_CODE");
		
		//生成短信验证码
		String verifyCode = RandomStringUtils.randomNumeric(6);
		
		String msg = this.SMS_VERIFY_CODE_TMPALET.replaceAll("VERIFY_CODE", verifyCode);
		String remark = "换卡验证码短信";
		String time_flg="0"; //用于标识 开户确认码短信发送时间 5分钟
		int validMinutes = 10;
		if ("600".equals(tradeTypeCode) || "4800".equals(tradeTypeCode) || "6800".equals(tradeTypeCode) || "606".equals(tradeTypeCode))
		{
			String tradeTypeName = StaticUtil.getStaticValue(getVisit(), "TD_S_TRADETYPE", "TRADE_TYPE_CODE", "TRADE_TYPE", tradeTypeCode);
			if("6800".equals(tradeTypeCode))
				tradeTypeName="和家固话开户";
			msg = this.SMS_VERIFY_CODE_WIDENET_TMPALET.replaceAll("VERIFY_CODE", verifyCode);
			msg = msg.replaceAll("TRADE_TYPE", tradeTypeName);
			remark = "宽带产品开户确认码";
			validMinutes = 5;
			time_flg="1";
		}
		
		
		//发送短信通知
		IData inparam = new DataMap();
        inparam.put("NOTICE_CONTENT", msg);
        inparam.put("RECV_OBJECT", serialNumber);
        inparam.put("RECV_ID", serialNumber);
        inparam.put("REFER_STAFF_ID", getVisit().getStaffId());
        inparam.put("REFER_DEPART_ID", getVisit().getDepartId());
        inparam.put("REMARK", remark);
        SmsSend.insSms(inparam);
        //保存短信验证码
        SharedCache.set(SmsVerifyCodeBean.VERIFY_CODE_CACHE_KEY+serialNumber, verifyCode, 60*validMinutes);
		
		data.put("TIME_FLG", time_flg);
        data.put("RESULT_CODE", 0);
        return data;
	}

}
