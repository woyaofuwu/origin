package com.asiainfo.veris.crm.order.web.person.supplierManager;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonQueryPage;

public abstract class supplierManagerAdd extends PersonQueryPage {

	public void addSupplier(IRequestCycle cycle) throws Exception {
        IData pageData = getData("cond", true);
		IDataset result = CSViewCall.call(this, "SS.supplierManagerSVC.addSupplier", pageData);
		this.setAjax(result);
	}
	
	public abstract void setInfo(IData info);
	
}
