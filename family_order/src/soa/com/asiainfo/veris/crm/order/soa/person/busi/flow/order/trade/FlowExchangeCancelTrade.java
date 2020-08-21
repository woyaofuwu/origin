
package com.asiainfo.veris.crm.order.soa.person.busi.flow.order.trade;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
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
import com.asiainfo.veris.crm.order.soa.person.busi.flow.order.requestdata.FlowExchangeCancelReqData;



public class FlowExchangeCancelTrade extends BaseTrade implements ITrade
{
    @Override
    public void createBusiTradeData(BusiTradeData btd) throws Exception
    {
    	FlowExchangeCancelReqData reqData = (FlowExchangeCancelReqData) btd.getRD();
    	String discntname = reqData.getDiscntname();  
    	String discntcode = reqData.getDiscntcode();
		String itemtype = reqData.getItemtype();
		String itemvalue = reqData.getItemvalue();
		String balance = reqData.getBalance();	
		String startdate = reqData.getStartdate();
		String enddate = reqData.getEnddate();
		String carryovertag = reqData.getCarryovertag();
		String detailitem = reqData.getDetailitem();
		String userbegindate = reqData.getUserbegindate();
		String userenddate = reqData.getUserenddate();
		String resinsid = reqData.getResinsid();
		String remark = reqData.getRemark();
		String returnvalue = reqData.getReturnvalue();
    	
        String strSerialNumber = btd.getMainTradeData().getSerialNumber();
        String strUserid = btd.getMainTradeData().getUserId();
        String sysdate = btd.getRD().getAcceptTime();
        String tradeTypeCode = btd.getMainTradeData().getTradeTypeCode();
        
        IData idParam = new DataMap();
		//idParam.put("SERIAL_NUMBER", strSerialNumber);
		idParam.put("USER_ID", strUserid);
		idParam.put("RES_INS_ID", resinsid);
		idParam.put("RETURN_VALUE", returnvalue);
		//idParam.put(Route.ROUTE_EPARCHY_CODE, "0898");
		//调用账务接口
		IDataOutput idoAmRollBack = CSAppCall.callAcct("AM_RollBack", idParam, false);
		IData idAmRollBackHead = idoAmRollBack.getHead();
		IDataset idsAmRollBack = idoAmRollBack.getData();
		String strXResultcode = idAmRollBackHead.getString("X_RESULTCODE");
		String strXResultinfo = idAmRollBackHead.getString("X_RESULTINFO");
		if(!"0".equals(strXResultcode))
		{
			 CSAppException.apperr(CrmCommException.CRM_COMM_13, "170412001:流量充值错误",strXResultinfo);
		}
		else
		{
			//String strXRecordnum = idAmRollBackHead.getString("X_RECORDNUM");
			String strBusinessID = "0";
			if(IDataUtil.isNotEmpty(idsAmRollBack))
			{
				strBusinessID = idsAmRollBack.first().getString("BUSINESS_ID");
			}
			
			//生成台帐其它资料表
	        OtherTradeData otherTD = new OtherTradeData();
	        otherTD.setUserId(strUserid);
	        otherTD.setRsrvValueCode("FlowExchangeCancel");
	        otherTD.setRsrvValue("流量提取返销");

	        otherTD.setRsrvStr1(strSerialNumber);
	        otherTD.setRsrvStr2(strBusinessID);
	        otherTD.setRsrvStr3(returnvalue);
	        otherTD.setRsrvStr4(resinsid);
	        otherTD.setRsrvStr5(discntname);
	        otherTD.setRsrvStr6(discntcode);
	        otherTD.setRsrvStr7(itemtype);
	        otherTD.setRsrvStr8(itemvalue);
	        otherTD.setRsrvStr9(balance);
	        otherTD.setRsrvStr10(carryovertag);
	        otherTD.setRsrvStr11(detailitem);
	        otherTD.setRsrvStr12(remark);
	        
	        otherTD.setRsrvDate1(startdate);
	        otherTD.setRsrvDate2(enddate);
	        
	        otherTD.setRsrvDate1(userbegindate);
	        otherTD.setRsrvDate2(userenddate);
	        
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
