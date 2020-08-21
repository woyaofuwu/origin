package com.asiainfo.veris.crm.order.web.person.broadband.widenet.addresscorrect;

import org.apache.log4j.Logger;
import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonQueryPage;

/**
 * 地址纠错
 * @author zhengkai5
 */
public abstract class AddressCorrect extends PersonQueryPage{
	
	static transient final Logger logger = Logger.getLogger(AddressCorrect.class);
	public abstract void setTrade(IData data);
	
	public void init(IRequestCycle cycle) throws Exception
    {
		IData data = getData();
		data.put("STAFF_NAME", this.getVisit().getStaffName());
		data.put("SERIAL_NUMBER", this.getVisit().getSerialNumber());
		setTrade(data);
    }
	public void onTradeSubmit(IRequestCycle cycle) throws Exception {
			
			IData data = getData();
			String time = SysDateMgr.getSysTime(); // 受理时间
			data.put("dateTime", time);
			
			data.put("BACK_USER", data.get("STAFF_NAME"));
			data.put("STAFF_NAME", this.getVisit().getStaffName());
			data.put("STAFF_ID", this.getVisit().getStaffId());
			data.put("DEPT_NAME",this.getVisit().getDepartName());
			data.put("DEPT_ID", this.getVisit().getDepartId());
			data.put("STAFF_SERIAL_NUMBER", this.getVisit().getSerialNumber());
			data.put("EPARCHY_CODE", this.getVisit().getStaffEparchyCode());
			data.put("CITY_CODE", this.getVisit().getCityCode());
			CSViewCall.call(this,"SS.AddressCorrectSVC.onTradeSubmit", data);
		}
}
