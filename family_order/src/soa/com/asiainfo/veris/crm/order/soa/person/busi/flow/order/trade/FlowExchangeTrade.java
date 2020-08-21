
package com.asiainfo.veris.crm.order.soa.person.busi.flow.order.trade;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.OtherTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.ITrade;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.BaseTrade;
import com.asiainfo.veris.crm.order.soa.person.busi.flow.order.requestdata.FlowExchangeReqData;



public class FlowExchangeTrade extends BaseTrade implements ITrade
{
    @Override
    public void createBusiTradeData(BusiTradeData btd) throws Exception
    {
    	FlowExchangeReqData reqData = (FlowExchangeReqData) btd.getRD();
    	
        String strFmBalanceid = reqData.getFmbalanceid();
    	String strFmAcctID = reqData.getFmacctid();
        String strblance = reqData.getBalance();
        String strCommid = reqData.getCommid();
        String strChannelid = reqData.getChannelid();
        String strfmassettypeid = reqData.getFmassettypeid();
        String strInitflow = reqData.getInitflow();
        String strInitfee = reqData.getInitfee();
        String strRemark = reqData.getRemark();
        String strEffectivedate = reqData.getEffectivedate();
        String strExpiredate = reqData.getExpiredate();
        String strTransValue = reqData.getTransvalue();
        
        String strSerialNumber = btd.getMainTradeData().getSerialNumber();
        String strUserid = btd.getMainTradeData().getUserId();
        String sysdate = btd.getRD().getAcceptTime();
        String tradeTypeCode = btd.getMainTradeData().getTradeTypeCode();
        
        IData idParam = new DataMap();
		idParam.put("SERIAL_NUMBER", strSerialNumber);
		idParam.put("CHANNEL_ID", strChannelid);
		//idParam.put("USER_ID", strUserid);
		//idParam.put("COMM_ID", strCommid);
		//idParam.put("TRANS_VALUE", strTransValue);
		IData iddate = new DataMap();
		iddate.put("FM_BALANCE_ID", strFmBalanceid);
		iddate.put("COMM_ID", strCommid);
		iddate.put("DETAIL_VALUE", strTransValue);
		IDataset idsdate = new DatasetList(); 
		idsdate.add(iddate);
		idParam.put("BALANCE_LIST", idsdate);
		
		//调用账务接口
		IDataOutput idoAmOutFlowExchange = CSAppCall.callAcct("AM_OUT_flowExchange", idParam, false);
		
		IData idAmOutFlowExchange = idoAmOutFlowExchange.getHead();
		IDataset idsAmOutFlowExchange = idoAmOutFlowExchange.getData();
		String strXResultcode = idAmOutFlowExchange.getString("X_RESULTCODE");
		String strXResultinfo = idAmOutFlowExchange.getString("X_RESULTINFO");
		if(!"0".equals(strXResultcode))
		{
			 CSAppException.apperr(CrmCommException.CRM_COMM_13, "170412001:流量充值错误",strXResultinfo);
		}
		else
		{
			//String strXRecordnum = idAmOutFlowExchange.getString("X_RECORDNUM");
			//String strBusinessID = idsAmOutFlowExchange.first().getString("BUSINESS_ID");
			String strBusinessID = "0";
			if(IDataUtil.isNotEmpty(idsAmOutFlowExchange))
			{
				strBusinessID = idsAmOutFlowExchange.first().getString("BUSINESS_ID");
			}
			
			//生成台帐其它资料表
	        OtherTradeData otherTD = new OtherTradeData();
	        otherTD.setUserId(strUserid);
	        otherTD.setRsrvValueCode("FlowExchange");
	        otherTD.setRsrvValue("流量提取");

	        otherTD.setRsrvStr1(strSerialNumber);
	        otherTD.setRsrvStr2(strBusinessID);
	        otherTD.setRsrvStr3(strTransValue);
	        otherTD.setRsrvStr4(strFmBalanceid);
	        otherTD.setRsrvStr5(strFmAcctID);
	        otherTD.setRsrvStr6(strblance);
	        otherTD.setRsrvStr7(strCommid);
	        otherTD.setRsrvStr8(strChannelid);
	        otherTD.setRsrvStr9(strfmassettypeid);
	        otherTD.setRsrvStr10(strInitflow);
	        otherTD.setRsrvStr11(strInitfee);
	        otherTD.setRsrvStr12(strRemark);
	        
	        otherTD.setRsrvDate1(strEffectivedate);
	        otherTD.setRsrvDate2(strExpiredate);
	        
	        otherTD.setRsrvStr13(CSBizBean.getVisit().getStaffId());
	        otherTD.setRsrvStr14(sysdate);
	        otherTD.setRsrvStr15(CSBizBean.getVisit().getStaffName());
	        otherTD.setRsrvStr16(tradeTypeCode);
	        otherTD.setStartDate(SysDateMgr.getSysTime());
	        otherTD.setEndDate(SysDateMgr.getTheLastTime());
	        otherTD.setModifyTag(BofConst.MODIFY_TAG_ADD);
	        otherTD.setStaffId(CSBizBean.getVisit().getStaffId());
	        otherTD.setDepartId(CSBizBean.getVisit().getDepartId());
	        otherTD.setInstId(SeqMgr.getInstId());
	        btd.add(strSerialNumber, otherTD);
		}
    }
}
