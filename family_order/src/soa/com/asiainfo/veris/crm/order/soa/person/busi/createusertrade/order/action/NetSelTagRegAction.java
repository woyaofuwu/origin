package com.asiainfo.veris.crm.order.soa.person.busi.createusertrade.order.action;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.OtherTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;

public class NetSelTagRegAction implements ITradeAction {

	private static Logger logger = Logger.getLogger(NetSelTagRegAction.class);

	@Override
	public void executeAction(BusiTradeData btd) throws Exception {
		UcaData uca = btd.getRD().getUca();
		//String tradeId = btd.getRD().getTradeId();
		//String tradeTypeCode = btd.getTradeTypeCode();
		
		IData pageData = btd.getRD().getPageRequestData();
		logger.error("NetSelTagRegActionxxxxxxxxxxx26 " + pageData);
 		
		String occupyTypeCode = pageData.getString("OCCUPY_TYPE_CODE", "").trim();
		if ("1".equals(occupyTypeCode)) { //1  网上选号开户 ; 0 普通开户
			OtherTradeData otherTD = new OtherTradeData();
			otherTD.setModifyTag(BofConst.MODIFY_TAG_ADD);
			otherTD.setInstId(SeqMgr.getInstId());
			otherTD.setUserId(uca.getUserId());
			otherTD.setRsrvValueCode("NETSEL_TAG");
			otherTD.setRsrvValue("网上选号开户号码");
			otherTD.setRsrvStr1(uca.getSerialNumber());
//			otherTD.setRsrvStr2("trade_type_code="+tradeTypeCode);
//			otherTD.setRsrvStr3("trade_id="+tradeId);
			otherTD.setRsrvStr4("ITFWEB00");
			otherTD.setStartDate(SysDateMgr.getSysTime());
			otherTD.setEndDate(SysDateMgr.END_DATE_FOREVER);
			otherTD.setStaffId(CSBizBean.getVisit().getStaffId());
			otherTD.setDepartId(CSBizBean.getVisit().getDepartId());
			logger.error("NetSelTagRegActionxxxxxxxxxxx42 " + otherTD);
			btd.add(uca.getSerialNumber(), otherTD);
		}
	}
}
