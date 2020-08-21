
package com.asiainfo.veris.crm.order.web.person.singlenumbermultidevice.changedevicestatus;

import org.apache.tapestry.IRequestCycle;

import com.ailk.bizview.base.CSViewCall;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.web.frame.bcf.exception.CSViewException;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class ChangeDeviceStatus extends PersonBasePage
{
	public abstract void setRelationList(IDataset relationList);

	/**
	 * 当前有效的副设备列表查询供展示
	 * @param cycle
	 * @throws Exception
	 */
	public void queryAuxDevInfos(IRequestCycle cycle) throws Exception {
		IData pageData = getData();
		IData input = getData("AUTH", true);
		String pageName = pageData.getString("page");
		
		if("oneNoMultiTerminal.OneNoMultiTerminalSuspend".equals(pageName)){
			input.put("OPR_FLAG", "1");//业务暂停
		}else{
			input.put("OPR_FLAG", "2");//业务恢复
		}
		IDataset relationList = CSViewCall.call(this,"SS.SingleNumMultiDeviceStatusChangeSVC.queryAuxDevInfos", input);
		if(null==relationList||relationList.isEmpty()){
			CSViewException.apperr(CrmCommException.CRM_COMM_103,"没有查询到该号码可操作的副设备信息");
		}
		this.setRelationList(relationList);
	}
	/**
	 * 选中的副设备列表恢复/暂停
	 * @param cycle
	 * @throws Exception
	 */
	public void onTradeSubmit(IRequestCycle cycle) throws Exception {
		IData pageData = getData();

		IDataset relationList = new DatasetList(pageData.getString("AUXCODES"));
		IData input = getData("AUTH", true);
		String pageName = pageData.getString("page");
		if("oneNoMultiTerminal.OneNoMultiTerminalSuspend".equals(pageName)){
			input.put("OPR_FLAG", "1");//业务暂停
		}else{
			input.put("OPR_FLAG", "2");//业务恢复
		}
		pageData.putAll(input);
		pageData.put("AUX_CODES", relationList);
		
		IDataset returnDataset = CSViewCall.call(this, "SS.SingleNumMultiDeviceStatusChangeRegSVC.tradeReg", pageData);
        this.setAjax(returnDataset);
	}
}