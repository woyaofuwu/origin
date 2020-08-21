package com.asiainfo.veris.crm.order.soa.person.busi.supplierManager;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class supplierManagerSVC extends CSBizService {

	public IDataset querySupplier(IData input) throws Exception {
		System.out.println("supplierManagerSVCxxxxxxxxxxxx13 " + input);  
		supplierManagerBean bean = (supplierManagerBean) BeanManager.createBean(supplierManagerBean.class);
		return bean.querySupplier(input, getPagination());
	}
	
	public IDataset querySupplierBySupplierId(IData input) throws Exception {
		System.out.println("supplierManagerSVCxxxxxxxxxxxx18 " + input);  
		supplierManagerBean bean = (supplierManagerBean) BeanManager.createBean(supplierManagerBean.class);
		return bean.querySupplierBySupplierId(input);
	}

	public IData addSupplier(IData input) throws Exception {
		IData retData = new DataMap();
		retData.put("X_RESULTCODE", "0");
		retData.put("X_RESULTINFO", "");

		supplierManagerBean bean = (supplierManagerBean) BeanManager.createBean(supplierManagerBean.class);

		IData param = new DataMap();
		param.put("SERIAL_NUMBER", input.getString("SERIAL_NUMBER", ""));
 
		bean.addSupplier(input);

		return retData;
	}

	public IData editSupplier(IData input) throws Exception {
		IData retData = new DataMap();
		retData.put("X_RESULTCODE", "0");
		retData.put("X_RESULTINFO", "");
		supplierManagerBean bean = (supplierManagerBean) BeanManager.createBean(supplierManagerBean.class);
		bean.editSupplier(input);
		return retData;
	}
	
	public IData delSupplier(IData input) throws Exception {
		IData retData = new DataMap();
		retData.put("X_RESULTCODE", "0");
		retData.put("X_RESULTINFO", "");		
		supplierManagerBean bean = (supplierManagerBean) BeanManager.createBean(supplierManagerBean.class);
		bean.delSupplier(input.getString("SUPPLIER_ID","").trim());
		return retData;
	}
	
}
