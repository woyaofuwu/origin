package com.asiainfo.veris.crm.order.web.person.supplierManager;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonQueryPage;

public abstract class supplierManager extends PersonQueryPage {

	public void querySupplier(IRequestCycle cycle) throws Exception {
		IData param = getData("cond",true);

		IDataOutput outPut = CSViewCall.callPage(this, "SS.supplierManagerSVC.querySupplier", param, getPagination("pageNav"));
		IDataset results = outPut.getData();
		for (int i = 0; i < results.size(); i++) {
			IData data = results.getData(i);
			if (data.getString("STATE", "").trim().equals("U")) {
				data.put("STATE", "生效");
			} else {
				data.put("STATE", "失效");
			}
		}
		
		setInfos(results);
		setPageCount(outPut.getDataCount());
		setCondition(param);
	}
	
	public void deleteSupplier(IRequestCycle cycle) throws Exception {
		IData pageData = getData();
		IDataset result = CSViewCall.call(this, "SS.supplierManagerSVC.delSupplier", pageData);
		this.setAjax(result);
	}
	
	public abstract void setCondition(IData condition);
	
	public abstract void setInfo(IData info);
	
	public abstract void setInfos(IDataset infos);
	
	public abstract void setPageCount(long count);
	
}
