package com.asiainfo.veris.crm.order.soa.person.busi.score.scoreinterface.order.action;

import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.MainTradeData;
import com.asiainfo.veris.crm.order.soa.person.common.action.sms.PerSmsAction;

public class ScoreDonateSmsAction implements ITradeAction {
	
	@SuppressWarnings("rawtypes")
	@Override
	public void executeAction(BusiTradeData btd) throws Exception {
		BaseReqData reqData = btd.getRD();
		String serialNumber = reqData.getUca().getSerialNumber();

		MainTradeData mainTradeData = btd.getMainTradeData();
		String seScore = mainTradeData.getRsrvStr2(); // 积分余额
		String objScore = mainTradeData.getRsrvStr3(); // 受赠人积分余额
		String objSerialNumber = mainTradeData.getRsrvStr5(); // 受赠人手机号码
		String donatedScore = mainTradeData.getRsrvStr10(); // 转赠积分值

		StringBuilder smsContent = new StringBuilder();
		smsContent.append("尊敬的客户，您已成功办理和积分转赠业务，向");
		smsContent.append(objSerialNumber);
		smsContent.append("号码赠送和积分");
		smsContent.append(donatedScore);
		smsContent.append("分，您当前积分余额为");
		smsContent.append(seScore);
		smsContent.append("分。如有疑问请致电10086【中国移动】");
		
		IData smsData = new DataMap(); // 短信数据
		smsData.put("SMS_PRIORITY", "5000");// 优先级：老系统默认5000
		smsData.put("CANCEL_TAG", "0");// 返销标志：0-未返销，1-被返销，2-返销 不能为空
		smsData.put("SMS_TYPE_CODE", "20"); // 短信发送业务类型编码：20-业务通知
		smsData.put("SMS_KIND_CODE", "02"); // 01-短信催缴	02-短信通知
		smsData.put("FORCE_OBJECT", "10086");// 发送对象
		smsData.put("RECV_OBJECT", serialNumber);// 接收对象
		smsData.put("NOTICE_CONTENT", smsContent.toString());// 短信内容
		smsData.put("REMARK", "积分转赠业务短信通知");
		PerSmsAction.insTradeSMS(btd, smsData);

		StringBuilder objSmsContent = new StringBuilder();
		objSmsContent.append("尊敬的客户，");
		objSmsContent.append(serialNumber);
		objSmsContent.append("用户办理了积分转赠业务，向您赠送移动和积分");
		objSmsContent.append(donatedScore);
		objSmsContent.append("分，您当前积分余额为");
		objSmsContent.append(objScore);
		objSmsContent.append("分。如有疑问请致电10086【中国移动】");
		
		IData objSmsData = new DataMap(); // 短信数据
		objSmsData.put("SMS_PRIORITY", "5000"); // 优先级：老系统默认5000
		objSmsData.put("CANCEL_TAG", "0"); // 返销标志：0-未返销，1-被返销，2-返销 不能为空
		objSmsData.put("SMS_TYPE_CODE", "20"); // 短信发送业务类型编码：20-业务通知
		objSmsData.put("SMS_KIND_CODE", "02"); // 01-短信催缴	02-短信通知
		objSmsData.put("FORCE_OBJECT", "10086");// 发送对象
		objSmsData.put("RECV_OBJECT", objSerialNumber);// 接收对象
		objSmsData.put("NOTICE_CONTENT", objSmsContent.toString());// 短信内容
		objSmsData.put("REMARK", "积分转赠业务短信通知");
		PerSmsAction.insTradeSMS(btd, objSmsData);
	}
}
