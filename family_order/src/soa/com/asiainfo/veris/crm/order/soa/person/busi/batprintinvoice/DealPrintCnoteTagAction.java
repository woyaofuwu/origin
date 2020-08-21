package com.asiainfo.veris.crm.order.soa.person.busi.batprintinvoice;

import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;

public class DealPrintCnoteTagAction implements ITradeAction{

	@Override
	public void executeAction(BusiTradeData btd) throws Exception {
		
		String inModeCode = btd.getMainTradeData().getInModeCode();
		
		if(!"SD".equals(inModeCode)){
			//return;
		}
		
		IData tradeInfo = new DataMap();
		tradeInfo.put("TRADE_ID", btd.getTradeId());
		tradeInfo.put("USER_ID", btd.getMainTradeData().getUserId());
		tradeInfo.put("SERIAL_NUMBER", btd.getMainTradeData().getSerialNumber());
		tradeInfo.put("PRINT_TAG", "0");
		tradeInfo.put("TRADE_TYPE_CODE", btd.getTradeTypeCode());
		tradeInfo.put("UPDATE_TIME", SysDateMgr.getSysTime());
		tradeInfo.put("UPDATE_STAFF_ID", CSBizBean.getVisit().getStaffId());
		tradeInfo.put("UPDATE_DEPART_ID", CSBizBean.getVisit().getDepartId());
		tradeInfo.put("UPDATE_DEPART_ID", CSBizBean.getVisit().getDepartId());
		tradeInfo.put("ACCEPT_DATE", btd.getMainTradeData().getExecTime());	
		
		Dao.insert("TF_B_CNOTE_PRINT_TAG", tradeInfo, Route.CONN_CRM_CG);

	}

}
