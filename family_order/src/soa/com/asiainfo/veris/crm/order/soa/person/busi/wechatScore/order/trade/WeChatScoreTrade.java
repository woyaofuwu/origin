package com.asiainfo.veris.crm.order.soa.person.busi.wechatScore.order.trade;

import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.pub.exception.ScoreException;
import com.asiainfo.veris.crm.order.pub.exception.WeChatException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.ITrade;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.BaseTrade;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.AcctCall;
import com.asiainfo.veris.crm.order.soa.person.busi.wechatScore.WeChatScoreBean;

public class WeChatScoreTrade extends BaseTrade implements ITrade{

	@Override
	public void createBusiTradeData(BusiTradeData bd) throws Exception {
		
		WeChatScoreSaleReqData reqData = (WeChatScoreSaleReqData) bd.getRD();
		String orderId=reqData.getScoreOrderId();
		String addDate=reqData.getAddDate();
		String addTime=reqData.getAddTime();
		String msisdn=reqData.getMsisdn();
		String givePoint=reqData.getGivePoint();
		String periodOfValidity=reqData.getPeriodOfValidity();
		String activityId=reqData.getActivityId();
		String activityTitle=reqData.getActivityTitle();
		String remarks=reqData.getRemarks();
		
		UcaData uca=bd.getRD().getUca();
		
		String serialNumber=uca.getSerialNumber();
		String userId=uca.getUserId();
		
		//调整了顺序，先写日志表，再调账务。避免调账务成功后写表失败导致积分赠送了但结果返回是失败的情况
		//账务调用成功将平台的数据添加到请求的数据表当中
		WeChatScoreBean bean = BeanManager.createBean(WeChatScoreBean.class);
		IData param=new DataMap();
		param.put("ORDER_ID", orderId);
		param.put("SERIAL_NUMBER", serialNumber);
		param.put("USER_ID", userId);
		param.put("ADD_MONTH", SysDateMgr.getSysDate().substring(5, 7));
		param.put("ADD_DATE", addDate);
		param.put("ADD_TIME", addTime);
		param.put("ADD_WHOLE_DATE", SysDateMgr.getSysTime());
		param.put("GIVE_POINT", givePoint);
		param.put("PERIOD_OF_VALIDITY", periodOfValidity);
		param.put("ACTIVITY_ID", activityId);
		param.put("ACTIVITY_TITLE", activityTitle);
		param.put("REMARKS", remarks);
		param.put("TRADE_ID", bd.getTradeId());
		bean.saveWeChatScore(param);
		
		
		//调用账务的接口办理积分的增加
		IData acctScoreParam=new DataMap();
		acctScoreParam.put("TRADE_ID", bd.getTradeId());
		acctScoreParam.put("SERIAL_NUMBER", msisdn);
		acctScoreParam.put("SCORE_CHANGED", givePoint);
		acctScoreParam.put("END_DATE", periodOfValidity);
		acctScoreParam.put("TRADE_TYPE_CODE", bd.getTradeTypeCode());
		
		//如果返回结果失败
		IData result=AcctCall.giftEThumbScore(acctScoreParam);
		if(result==null){
			CSAppException.apperr(WeChatException.CRM_WECHAT_0,"账务处理积分失败！");
		}else if(!result.getString("X_RESULTCODE","").equals("0")){
			CSAppException.apperr(WeChatException.CRM_WECHAT_0,result.getString("X_RESULTINFO",""));
		}
	}
}
