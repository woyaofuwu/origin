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
public abstract class OpenMobileNew extends PersonBasePage {
	public abstract void setInfo(IData info);

	/**
	 * 页面初始化加载参数
	 * @param cycle
	 * @throws Exception
	 */
	public void onInitTrade(IRequestCycle cycle) throws Exception {
		IData data = this.getData();
		String tradeTypeCode = data.getString("TRADE_TYPE_CODE", "133");
		String authType = data.getString("authType", "00");
		// 无线固话停开机类页面调用时会传TYPE_CODE参数，标识是从无线固话停开机页面调用
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
		
		//REQ201812130016 家庭IMS固话补充需求
		IData param = new DataMap();
		param.put("SERIAL_NUMBER", data.getString("SERIAL_NUMBER", ""));
		IData ImsInfo = CSViewCall.callone(this, "SS.ChangeSvcStateSVC.getIMSInfoBySerialNumber", param);
		if (IDataUtil.isNotEmpty(ImsInfo)) {
			param.put("SERIAL_NUMBER", ImsInfo.getString("SERIAL_NUMBER_B", ""));//IMS家庭固话   
			IData ImsStaInfo = CSViewCall.callone(this, "SS.ChangeSvcStateSVC.getImsStaInfoByIMS", param);
			if (IDataUtil.isNotEmpty(ImsStaInfo)) {
				if("1".equals(ImsStaInfo.getString("STATE_CODE", ""))||"4".equals(ImsStaInfo.getString("STATE_CODE", ""))){
					resultData.put("IMS_STOP", "Y");
				}				
			}
		}
		
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
	 * 手机报开关联开宽带校验
	 * @param cycle
	 * @throws Exception
	 */
	public void checkOpenWide(IRequestCycle cycle) throws Exception {
		IData data = getData();
		IData resultData = CSViewCall.callone(this, "SS.ChangeSvcStateSVC.checkOpenWide", data);
		if (IDataUtil.isNotEmpty(resultData)) {
			setAjax(resultData);
		}
	}

	/**
	 * 判断用户是否是手机报停状态
	 * @param cycle
	 * @throws Exception
	 */
	public void checkOpenIMS(IRequestCycle cycle) throws Exception {
		IData data = getData();
		IData resultData = CSViewCall.callone(this, "SS.ChangeSvcStateSVC.checkOpenIMS", data);
		if (IDataUtil.isNotEmpty(resultData)) {
			setAjax(resultData);
		}
	}
}
