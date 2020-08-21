package com.asiainfo.veris.crm.iorder.web.person.changesvcstate;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

/**
 * 大客户担保开机（新）
 * @author xieyf5
 */
public abstract class VipAssureOpenNew extends PersonBasePage {
	public abstract void setInfo(IData info);

	public abstract void setAssuerTimesList(IDataset datasIDataset);

	public abstract void setBusiInfo(IData data);

	/**
	 * 页面初始化加载参数
	 * @param cycle
	 * @throws Exception
	 */
	public void onInitTrade(IRequestCycle cycle) throws Exception {
		IData data = this.getData();
		String tradeTypeCode = data.getString("TRADE_TYPE_CODE", "492");
		IData info = new DataMap();
		info.put("TRADE_TYPE_CODE", tradeTypeCode);
		setInfo(info);
	}

	/**
	 * 查询大客户信息
	 * @param requestCycle
	 * @throws Exception
	 */
	public void queryVipAssureSnInfo(IRequestCycle requestCycle) throws Exception {
		IData pageData = getData();
		pageData.put(Route.ROUTE_EPARCHY_CODE, pageData.getString("EPARCHY_CODE"));
		IDataset vipAssureSnInfos = CSViewCall.call(this, "SS.ChangeSvcStateSVC.queryVipAssureSnInfo", pageData);
		if (IDataUtil.isNotEmpty(vipAssureSnInfos)) {
			IData tempData = vipAssureSnInfos.getData(0);
			setBusiInfo(tempData.getData("BUSI_INFO"));
			setAssuerTimesList(tempData.getDataset("OPEN_HOURS"));
		}
	}

	/**
	 * 提交后处理函数
	 * @param requestCycle
	 * @throws Exception
	 */
	public void onTradeSubmit(IRequestCycle requestCycle) throws Exception {
		IData data = getData();
		if (StringUtils.isEmpty(data.getString("SERIAL_NUMBER", ""))) {
			data.put("SERIAL_NUMBER", data.getString("AUTH_SERIAL_NUMBER"));
		}
		IDataset dataset = CSViewCall.call(this, "SS.ChangeSvcStateRegSVC.tradeReg", data);
		setAjax(dataset);
	}
}
