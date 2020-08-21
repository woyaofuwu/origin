package com.asiainfo.veris.crm.order.soa.person.busi.wechatScore.order.buildrequest;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.impl.BaseBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.person.busi.wechatScore.order.trade.WeChatScoreSaleReqData;


public class BuilderWeChatScoreReqData extends BaseBuilder implements IBuilder{
	
	@Override
    public void buildBusiRequestData(IData param, BaseReqData brd) throws Exception
    {
		WeChatScoreSaleReqData reqData = (WeChatScoreSaleReqData) brd;
		
		reqData.setScoreOrderId(param.getString("OrderId",""));
		reqData.setAddDate(param.getString("AddDate",""));
		reqData.setAddTime(param.getString("AddTime",""));
		reqData.setMsisdn(param.getString("Msisdn",""));
		reqData.setGivePoint(param.getString("GivePoint",""));
		reqData.setPeriodOfValidity(param.getString("PeriodOfValidity",""));
		reqData.setActivityId(param.getString("ActivityId",""));
		reqData.setActivityTitle(param.getString("ActivityTitle",""));
		reqData.setRemarks(param.getString("Remarks",""));
    }
	
	@Override
    public BaseReqData getBlankRequestDataInstance()
    {
        // TODO Auto-generated method stub
        return new WeChatScoreSaleReqData();
    }
	
	
}
