
package com.asiainfo.veris.crm.order.soa.person.busi.flow.order.trade;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
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
import com.asiainfo.veris.crm.order.soa.person.busi.flow.order.requestdata.FlowChargeReqData;



public class FlowChargeTrade extends BaseTrade implements ITrade
{
    @Override
    public void createBusiTradeData(BusiTradeData btd) throws Exception
    {
    	FlowChargeReqData reqData = (FlowChargeReqData) btd.getRD();
    	String strAmount = reqData.getAmount();
    	Integer nAmount = Integer.parseInt(strAmount);
        String strTransfee = reqData.getTransfee();
        Long nTransfee = Long.parseLong(strTransfee);
        String strCommid = reqData.getCommid();
        String strChannelid = reqData.getChannelid();
        String strPeerbusinessid = btd.getTradeId();
        String strRemark = reqData.getRemark();
        String strEffectivedate = reqData.getEffectivedate();
        String strExpiredate = reqData.getExpiredate();
        String strTransNeeded = reqData.getTransneeded();
        String strCommnum = reqData.getCommnum();
        Integer nCommnum = Integer.parseInt(strCommnum);
        
        nAmount = nAmount * nCommnum;
        nTransfee = nTransfee * nCommnum;
        
        String strSerialNumber = btd.getMainTradeData().getSerialNumber();
        String strUserid = btd.getMainTradeData().getUserId();
        String sysdate = btd.getRD().getAcceptTime();
        String tradeTypeCode = btd.getMainTradeData().getTradeTypeCode();
        
        IData idParam = new DataMap();
		idParam.put("SERIAL_NUMBER", strSerialNumber);
		//idParam.put("USER_ID", strUserid);
		idParam.put("AMOUNT", nAmount);
		idParam.put("TRANS_FEE", nTransfee);
		idParam.put("COMM_ID", strCommid);
		idParam.put("CHANNEL_ID", strChannelid);
		idParam.put("PEER_BUSINESS_ID", strPeerbusinessid);
		idParam.put("REMARK", strRemark);
		idParam.put("EFFECTIVE_DATE", strEffectivedate);
		idParam.put("EXPIRE_DATE", strExpiredate);
		idParam.put("TRANS_NEEDED", strTransNeeded);
		idParam.put(Route.ROUTE_EPARCHY_CODE, "0898");
		//调用账务接口
		IDataOutput idoAmOutFlowCharge = CSAppCall.callAcct("AM_OUT_flowCharge", idParam, false);
		IData idAmOutFlowCharge = idoAmOutFlowCharge.getHead();
		IDataset idsAmOutFlowCharge = idoAmOutFlowCharge.getData();
		String strXResultcode = idAmOutFlowCharge.getString("X_RESULTCODE");
		String strXResultinfo = idAmOutFlowCharge.getString("X_RESULTINFO");
		if(!"0".equals(strXResultcode))
		{
			 CSAppException.apperr(CrmCommException.CRM_COMM_13, "170412001:流量充值错误",strXResultinfo);
		}
		else
		{
			String strXRecordnum = idAmOutFlowCharge.getString("X_RECORDNUM");
			String strBusinessID = "0";
			if(IDataUtil.isNotEmpty(idsAmOutFlowCharge))
			{
				strBusinessID = idsAmOutFlowCharge.first().getString("BUSINESS_ID");
			}
			//String strBusinessID = idsAmOutFlowCharge.first().getString("BUSINESS_ID");
			
			//生成台帐其它资料表
	        OtherTradeData otherTD = new OtherTradeData();
	        otherTD.setUserId(strUserid);
	        otherTD.setRsrvValueCode("FlowCharge");
	        otherTD.setRsrvValue("流量充值");

	        otherTD.setRsrvStr1(strSerialNumber);
	        otherTD.setRsrvStr2(strBusinessID);
	        otherTD.setRsrvStr3(strAmount);
	        otherTD.setRsrvStr4(strTransfee);
	        otherTD.setRsrvStr5(strCommnum);
	        otherTD.setRsrvStr6(strCommid);
	        otherTD.setRsrvStr7(strChannelid);
	        otherTD.setRsrvStr8(strRemark);
	        otherTD.setRsrvStr9(strTransNeeded);
	        otherTD.setRsrvStr10(strXRecordnum);
	        
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
