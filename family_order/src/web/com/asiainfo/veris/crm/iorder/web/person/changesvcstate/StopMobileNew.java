package com.asiainfo.veris.crm.iorder.web.person.changesvcstate;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;


/**
 * 停开机业务受理（新）
 * @author xieyf5
 */
public abstract class StopMobileNew extends PersonBasePage {
	public abstract void setInfo(IData info);

	/**
	 * 页面初始化加载参数
	 * @param cycle
	 * @throws Exception
	 */
	public void onInitTrade(IRequestCycle cycle) throws Exception {
		IData data = this.getData();
		String tradeTypeCode = data.getString("TRADE_TYPE_CODE", "131"); // 默认打开停机菜单
		String authType = data.getString("authType", "00");
		String typeCode = getRequest().getParameter("TYPE_CODE");
		IData info = new DataMap();
		info.put("TRADE_TYPE_CODE", tradeTypeCode);
		info.put("authType", authType);
		if (null == typeCode){
			typeCode = "";
		}
		info.put("typeCode",typeCode);
		setInfo(info);
	}

	/**
	 * Auth组件鉴权后初始化方法
	 * @param clcle
	 * @throws Exception
	 * @author yuyj3
	 */
	public void loadChildInfo(IRequestCycle cycle) throws Exception {
		IData data = getData();
		IData resultData = CSViewCall.callone(this, "SS.ChangeSvcStateSVC.loadChildInfo", data);
		if (IDataUtil.isNotEmpty(resultData)) {
			setAjax(resultData);
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

	/**
	 * 手机报停关联停宽带校验
	 * @param cycle
	 * @throws Exception
	 */
	public void checkStopWide(IRequestCycle cycle) throws Exception {
		IData data = getData();
		IData resultData = CSViewCall.callone(this, "SS.ChangeSvcStateSVC.checkStopWide", data);
		if (IDataUtil.isNotEmpty(resultData)) {
			setAjax(resultData);
		}
	}
}
