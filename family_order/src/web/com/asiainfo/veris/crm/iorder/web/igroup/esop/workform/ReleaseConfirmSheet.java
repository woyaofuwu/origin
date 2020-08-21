package com.asiainfo.veris.crm.iorder.web.igroup.esop.workform;

import org.apache.tapestry.IRequestCycle;

import com.ailk.bizview.base.CSBasePage;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;

public abstract class ReleaseConfirmSheet extends CSBasePage  {
	public abstract void setInfos(IDataset infos);
	public abstract void setCondition(IData Condition);
	public abstract void setInfoCount(long count);
	
	/**
	 * 查询
	 * @throws Exception
	 */
	public void queryWorkform(IRequestCycle cycle) throws Exception{
		IData data = getData();
		IDataOutput output = CSViewCall.callPage(this, "SS.WorkformSubscribeSVC.qryWorkformSubscribeInfo", data ,this.getPagination("myQueryNav"));
		this.setInfoCount(output.getDataCount());
		this.setInfos(output.getData());
		this.setCondition(data);
	}

	/**
	 * 解绑
	 * @throws Exception
	 */
	public void release(IRequestCycle cycle) throws Exception{
		IData data = getData();
		if(data.getString("HISTORY_TAG").equals("0")) {
			CSViewCall.call(this, "SS.WorkformSubscribeSVC.updateWorkformSubscribeByIBSYSID", data);
		}
	}
}
