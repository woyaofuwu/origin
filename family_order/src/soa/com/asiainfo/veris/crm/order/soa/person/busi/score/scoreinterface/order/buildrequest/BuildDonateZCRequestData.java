package com.asiainfo.veris.crm.order.soa.person.busi.score.scoreinterface.order.buildrequest;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.impl.BaseBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.AcctCall;
import com.asiainfo.veris.crm.order.soa.person.busi.score.scoreinterface.order.requestdata.ScoreDonateZCRequestData;

public class BuildDonateZCRequestData extends BaseBuilder implements IBuilder {
	
	 @Override
	    public void buildBusiRequestData(IData param, BaseReqData brd) throws Exception{
		    IDataUtil.chkParam(param, "SERIAL_NUMBER");
	        IDataUtil.chkParam(param, "OBJECT_SERIAL_NUMBER");
	        IDataUtil.chkParam(param, "DONATE_SCORE");
	        //IDataUtil.chkParam(param, "SCORE_TYPE_CODE");
	        IDataUtil.chkParam(param, "A_TRADE_TYPE_CODE");
	        IDataUtil.chkParam(param, "B_TRADE_TYPE_CODE");
	        ScoreDonateZCRequestData reqData = (ScoreDonateZCRequestData) brd;
	        reqData.setSERIAL_NUMBER(param.getString("SERIAL_NUMBER",""));
	        reqData.setOBJECT_SERIAL_NUMBER(param.getString("OBJECT_SERIAL_NUMBER", ""));
	        reqData.setDONATE_SCORE(param.getString("DONATE_SCORE", ""));
	        reqData.setA_TRADE_TYPE_CODE(param.getString("A_TRADE_TYPE_CODE", ""));
	        reqData.setB_TRADE_TYPE_CODE(param.getString("B_TRADE_TYPE_CODE", ""));
	        String strValidDate = param.getString("VALID_DATE");
	        if (strValidDate != null && strValidDate.trim().length() > 0)
            {
                int seconds = (60 * 60 * 24) * (new Integer(strValidDate));
                String strEndDate = SysDateMgr.getOtherSecondsOfSysDate(seconds);
                strEndDate = strEndDate.substring(0, 10);
                strEndDate = strEndDate + SysDateMgr.END_DATE;
                reqData.setVALID_DATE(strEndDate);
            }else{
            	reqData.setVALID_DATE(param.getString("VALID_DATE", ""));
            }
	        reqData.setSCORE_TYPE_CODE(param.getString("SCORE_TYPE_CODE", ""));
	        IData ldata = AcctCall.queryUserScoreone(reqData.getUca().getUserId());
	        String lscore = "0";// 转让人用户积分
	        if (IDataUtil.isNotEmpty(ldata))
	        {
	            lscore = ldata.getString("SUM_SCORE");
	        }
	        reqData.setSCORE(lscore);
	 }
	 
	 @Override
	    public BaseReqData getBlankRequestDataInstance(){
		 return new ScoreDonateZCRequestData();
	 }

}
