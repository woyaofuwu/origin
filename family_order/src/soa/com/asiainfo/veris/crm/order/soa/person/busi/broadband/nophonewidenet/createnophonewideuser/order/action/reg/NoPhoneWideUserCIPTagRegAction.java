package com.asiainfo.veris.crm.order.soa.person.busi.broadband.nophonewidenet.createnophonewideuser.order.action.reg;

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

/**
 * 无手机宽带用户是否免人像比对登记
 * @author: wuwangfeng
 * @date: 2019-11-11
 */
public class NoPhoneWideUserCIPTagRegAction implements ITradeAction{
	
	private static Logger log = Logger.getLogger(NoPhoneWideUserCIPTagRegAction.class);

	@Override
	public void executeAction(BusiTradeData btd) throws Exception {
		UcaData uca = btd.getRD().getUca();
		
		IData pageData = btd.getRD().getPageRequestData();
		log.debug("NoPhoneWideUserCIPTagRegAction---------------------pageData--->" + pageData);
 		
		String cipTag = "0"; //0-还没有做人像比对, 1-已经做了人像比对
		Boolean isPortraitDiscrimination = pageData.getBoolean("IS_PORTRAIT_DISCRIMINATION"); //人像比对标记
		if (isPortraitDiscrimination) {
			cipTag = "1";
		}
		
		OtherTradeData otherTD = new OtherTradeData();
		
		otherTD.setModifyTag(BofConst.MODIFY_TAG_ADD);
		otherTD.setInstId(SeqMgr.getInstId());
		otherTD.setUserId(uca.getUserId());
		otherTD.setRsrvValueCode("NOPHONEWIDE_CIPTAG");
		otherTD.setRsrvValue("是否已做人像比对");
		otherTD.setRsrvStr1(cipTag);
		otherTD.setRsrvStr2(uca.getCustomer().getCustName());
		otherTD.setRsrvStr3(uca.getCustomer().getPsptId());
		otherTD.setStartDate(SysDateMgr.getSysTime());
		otherTD.setEndDate(SysDateMgr.END_DATE_FOREVER);
		otherTD.setStaffId(CSBizBean.getVisit().getStaffId());
		otherTD.setDepartId(CSBizBean.getVisit().getDepartId());
		
		btd.add(uca.getSerialNumber(), otherTD);
	}

}
