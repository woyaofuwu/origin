package com.asiainfo.veris.crm.order.web.person.supplierManager;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonQueryPage;

public abstract class supplierManagerEdit extends PersonQueryPage {

	public void editSupplier(IRequestCycle cycle) throws Exception {
		IData pageData = getData("cond",true);
		IDataset result = CSViewCall.call(this, "SS.supplierManagerSVC.editSupplier", pageData);
		this.setAjax(result);
	}

	public void initEditPage(IRequestCycle cycle) throws Exception {
		IData param = getData();
		IData data = CSViewCall.call(this, "SS.supplierManagerSVC.querySupplierBySupplierId", param).first();

		if (data.getString("SUPPLIER_TYPE_ID").equals("M")) {
			data.put("SUPPLIER_TYPE_ID", "供应商");
		} else {
			data.put("SUPPLIER_TYPE_ID", "手机卖场");
		}
		
		if (data.getString("STATE").equals("U")) {
			data.put("STATE", "生效");
		} else {
			data.put("STATE", "失效");
		}

		setInfo(data);
	}

	public abstract void setInfo(IData info);

}
