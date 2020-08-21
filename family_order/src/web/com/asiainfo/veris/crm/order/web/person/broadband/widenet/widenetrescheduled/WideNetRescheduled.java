package com.asiainfo.veris.crm.order.web.person.broadband.widenet.widenetrescheduled;

import org.apache.tapestry.IRequestCycle;
import org.jsoup.helper.StringUtil;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.bcf.exception.CSViewException;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonQueryPage;

/**
 * 宽带改期
 * @author zhengkai5
 */
public abstract class WideNetRescheduled extends PersonQueryPage {
	public void queryWideNetInfo(IRequestCycle cycle) throws Exception {
		IData data = getData();

		String KDSerialNumber = (String) data.get("SERIAL_NUMBER");

		if (!"KD_".equals(KDSerialNumber.substring(0, 3))) {
			KDSerialNumber = "KD_" + KDSerialNumber;
		}

		IDataset wideNetInfos = CSViewCall.call(this,
				"SS.WideNetRescheduledSVC.queryWideNetInfo", data);

		if (IDataUtil.isNotEmpty(wideNetInfos)) {
			IData wideNetInfo = wideNetInfos.getData(0);
			
			// 预约施工时间只能选择48小时之后
			String minDate = SysDateMgr.getAddHoursDate(
					SysDateMgr.getSysTime(), 48); // SysDateMgr.addDays(2);
			wideNetInfo.put("MIN", minDate);

			String widenetType = (String) wideNetInfo.get("RSRV_STR2");
			wideNetInfo.put("WIDE_TYPE", widenetType);

			this.setInfo(wideNetInfo);
		} else {
			CSViewException.apperr(CrmCommException.CRM_COMM_103,
					"该用户服务号码不存在可更改施工时间的订单信息！");
		}
	}

	// 提交宽带改期时间
	public void onTradeSubmit(IRequestCycle cycle) throws Exception {
		IData data = getData();
		String KDSerialNumber = (String) data.get("SERIAL_NUMBER");
		if (!"KD_".equals(KDSerialNumber.substring(0, 3))) {
			KDSerialNumber = "KD_" + KDSerialNumber;
		}
		
		IDataset wideNetInfos = CSViewCall.call(this,
				"SS.WideNetRescheduledSVC.queryWideNetInfo", data);
		String suggestData = wideNetInfos.getData(0).getString("SUGGEST_DATE");
		
		if(!StringUtil.isBlank(suggestData)){
			suggestData= suggestData.substring(0, 10);
			String sysData = SysDateMgr.getSysTime();
			sysData = sysData.substring(0, 10);
			if(suggestData == sysData || sysData.equals(suggestData))
			{
				CSViewException.apperr(CrmCommException.CRM_COMM_103,
				"预约当天，不允许修改预约时间！");
			}
		}

		// 调用Pbossti提供的接口
		String staffId = getVisit().getStaffId(); // 受理员工编号
		String time = SysDateMgr.getSysTime(); // 受理时间
		String tradeId = data.getString("TRADE_ID"); // tradeID
		String SUGGEST_DATE = data.getString("SUGGEST_DATE"); // 预约修改时间
		String reasonFlag = data.getString("REASON_FLAG"); // 是否客户原因
		String reason = data.getString("REASON"); // 修改的原因说明

		data.put("TRADE_STAFF_ID", staffId);
		data.put("ACCEPT_DATE", time);
		data.put("SUBSCRIBE_ID", tradeId);
		data.put("SUGGEST_DATE", SUGGEST_DATE);
		data.put("REASON_FLAG", reasonFlag);
		data.put("REASON", reason);

		IDataset dataset = CSViewCall.call(this,
				"SS.WideNetRescheduledSVC.onTradeSubmit", data);
		this.setAjax(dataset);
	}

	public abstract void setInfo(IData info);

	public abstract void setWideInfo(IData info);
}
