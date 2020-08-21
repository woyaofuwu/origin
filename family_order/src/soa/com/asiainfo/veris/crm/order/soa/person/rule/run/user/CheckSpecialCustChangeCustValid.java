package com.asiainfo.veris.crm.order.soa.person.rule.run.user;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;

public class CheckSpecialCustChangeCustValid extends BreBase implements
		IBREScript {

	@Override
	public boolean run(IData databus, BreRuleParam ruleParam) throws Exception {
		String custId = databus.getString("CUST_ID");

		IData custInfo = UcaInfoQry.qryCustomerInfoByCustId(custId);
		String specialCustDate = custInfo.getString("RSRV_DATE1", "");
		
		if(!specialCustDate.trim().equals("")){
			//终止时间
			String validEndDate=SysDateMgr.addYears(specialCustDate, 2);
			
			//当前时间
			String curTime=SysDateMgr.getSysDate(SysDateMgr.PATTERN_STAND);
			
			if(curTime.compareTo(validEndDate)<=0){
				String tips = "业务拦截：此号码于"+SysDateMgr.decodeTimestamp(specialCustDate, SysDateMgr.PATTERN_STAND_YYYYMMDD)+
						"办理了单方特殊过户业务，在"+SysDateMgr.decodeTimestamp(validEndDate, SysDateMgr.PATTERN_STAND_YYYYMMDD)+
						"前不允许再次办理过户业务";
				BreTipsHelp.addNorTipsInfo(databus,
						BreFactory.TIPS_TYPE_FORCE_EXIT, "", tips);
				return true;
			}
		}
		
		return false;

	}
}
