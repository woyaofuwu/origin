package com.asiainfo.veris.crm.order.soa.person.busi.supplierManager;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class supplierManagerBean extends CSBizBean {

	public IDataset querySupplier(IData data, Pagination page) throws Exception {	
		return Dao.qryByCode("RES_SUPPLIER", "SEL_BY_SUPPLIER_NAME", data, page, Route.CONN_RES);
	}
		
	public IDataset querySupplierBySupplierId(IData data) throws Exception {
		IData params = new DataMap();
		params.put("SUPPLIER_ID", data.getString("SUPPLIER_ID", "").trim());
		return Dao.qryByCode("RES_SUPPLIER", "SEL_BY_SUPPLIER_ID", params, Route.CONN_RES);
	}	
	
	public void addSupplier(IData inparams) throws Exception {
		
//		System.out.println("supplierManagerBeanxxxxxxxxxxxx23 " + inparams);
		
		String sql = " SELECT RES_SUPPLIER$SEQ.NEXTVAL SUPPLIER_ID  from dual t  ";
		String supplier_id = Dao.qryBySql(new StringBuilder(sql), new DataMap(), Route.CONN_RES).getData(0).getString("SUPPLIER_ID", "").trim();
		
		inparams.put("MGMT_DISTRICT", "ZZZZ");
		inparams.put("DONE_TIME", SysDateMgr.getSysTime());
		inparams.put("VALID_TIME", SysDateMgr.getSysTime());
		inparams.put("EXPIRE_TIME", "2050-12-31 23:59:59");
		inparams.put("OP_ID", this.getVisit().getStaffId());
		inparams.put("ORG_ID", this.getVisit().getDepartId());
		inparams.put("STATE", "U");//生效
		inparams.put("SUPPLIER_ID", supplier_id);

//		System.out.println("supplierManagerBeanxxxxxxxxxxxx37 " + inparams);
		Dao.insert("RES_SUPPLIER", inparams, Route.CONN_RES);
		
	}

	public void editSupplier(IData inparams) throws Exception {
//		System.out.println("supplierManagerBeanxxxxxxxxxxxx43 " + inparams);
		
		inparams.put("DONE_TIME", SysDateMgr.getSysTime());
		inparams.put("OP_ID", this.getVisit().getStaffId());
		inparams.put("ORG_ID", this.getVisit().getDepartId());
		
		IData date = Dao.qryByCode("RES_SUPPLIER", "SEL_BY_SUPPLIER_ID", inparams, Route.CONN_RES).first();		
		date.putAll(inparams);		
		Dao.update("RES_SUPPLIER", date, new String[] { "SUPPLIER_ID" }, Route.CONN_RES);
	}

	public void delSupplier(String supplierId) throws Exception {

		IData inparams = new DataMap();
		inparams.put("SUPPLIER_ID", supplierId);
		
		inparams.put("STATE", "E");//失效		
		inparams.put("EXPIRE_TIME", SysDateMgr.getSysTime());		
		inparams.put("DONE_TIME", SysDateMgr.getSysTime());
		inparams.put("OP_ID", this.getVisit().getStaffId());
		inparams.put("ORG_ID", this.getVisit().getDepartId());
		
		IData date = Dao.qryByCode("RES_SUPPLIER", "SEL_BY_SUPPLIER_ID", inparams, Route.CONN_RES).first();		
		date.putAll(inparams);	
		
		Dao.update("RES_SUPPLIER", date, new String[] { "SUPPLIER_ID" }, Route.CONN_RES);
	}

}
