package com.asiainfo.veris.crm.order.web.person.highvaluegarden;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonQueryPage;

public abstract class GardenDeviceInfoAdd extends PersonQueryPage {
	
	public abstract void setAccountInfos(IDataset accountInfos);
	public abstract void setCond(IData cond);
	public abstract void setInfo(IData info);
	public abstract void setPlatInfos(IDataset platInfos);
	public abstract void setPopuInfo(IData popuInfo);

	/**
	 * 
	 * @param cycle
	 * @throws Exception
	 */
	public void batGardenInfo(IRequestCycle cycle) throws Exception {
		IData indata = getData();

		// 上传文件处理
		String filePath = indata.getString("FILE_PATH");
		
		IData param = new DataMap();
		param.put("FILE_PATH", filePath);
		param.put("CREATE_STAFF_ID", this.getVisit().getStaffId());
		param.put("UPDATE_DEPART_ID", this.getVisit().getDepartId());
		IDataset resultDataset = CSViewCall.call(this, "SS.GardenDeviceInfoSVC.gardenInfoInsert", param);
		String result = resultDataset.getData(0).getString("result");
		setAjax("result", result);
	}

}
