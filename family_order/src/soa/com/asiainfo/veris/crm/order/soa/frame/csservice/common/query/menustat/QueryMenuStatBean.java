package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.menustat;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;


public class QueryMenuStatBean  extends CSBizBean{

	public IDataset getMenus(IData data) throws Exception
    {    
		data.put("STAFF_ID", CSBizBean.getVisit().getStaffId());
		return Dao.qryByCode("TF_M_MENU_STAT", "SEL_BY_STAFF_ID", data);
    }
}
