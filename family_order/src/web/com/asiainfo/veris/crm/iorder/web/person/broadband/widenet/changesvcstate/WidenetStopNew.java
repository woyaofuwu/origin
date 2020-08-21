package com.asiainfo.veris.crm.iorder.web.person.broadband.widenet.changesvcstate;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.exception.WidenetException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.bcf.exception.CSViewException;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class WidenetStopNew extends PersonBasePage {
	public void loadChildInfo(IRequestCycle cycle) throws Exception {
		IData data = getData();
		IDataset dataset = CSViewCall.call(this, "CS.WidenetInfoQuerySVC.getUserWidenetInfo", data);
		IData wideInfo = dataset.first();
		if (wideInfo == null) {
			CSViewException.apperr(WidenetException.CRM_WIDENET_1, data.getString("USER_ID"));
		} else {
			wideInfo.put("WIDE_TYPE", wideInfo.getString("RSRV_STR2"));
			setWideInfo(wideInfo);
		}
	}

	/**
	 * 业务类型初始化
	 * @author yuyj3
	 * @param clcle
	 * @throws Exception
	 */
	public void onInitTrade(IRequestCycle clcle) throws Exception {
		IData data = getData();
		IData param = new DataMap();

		if ("SCHOOL".equals(data.getString("WIDE_TYPE"))) {
			if ("0".equals(data.getString("TAG"))) {
				param.put("TRADE_TYPE_CODE", "632");// 报停
			} else if ("1".equals(data.getString("TAG"))) {
				param.put("TRADE_TYPE_CODE", "633");// 报开
			}
		} else {
			if ("0".equals(data.getString("TAG"))) {
				param.put("TRADE_TYPE_CODE", "603");// 报停
			} else if ("1".equals(data.getString("TAG"))) {
				param.put("TRADE_TYPE_CODE", "604");// 报开
			}
		}

		setInfo(param);
	}

	/**
	 * @param requestCycle
	 * @description:服务状态变更业务提交类
	 * @author: chenzm
	 */
	public void onTradeSubmit(IRequestCycle requestCycle) throws Exception {
		IData data = getData();
		data.put("SERIAL_NUMBER", data.getString("AUTH_SERIAL_NUMBER"));
		IDataset dataset = CSViewCall.call(this, "SS.ChangeWidenetSvcStateRegSVC.tradeReg", data);
		setAjax(dataset);
	}

	public abstract void setInfo(IData info);

	public abstract void setWideInfo(IData wideInfo);
}
