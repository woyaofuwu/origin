package com.asiainfo.veris.crm.order.soa.person.busi.cpelist;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class CpeListBean  extends CSBizBean{
	
    public IDataset queryImportData(IData data, Pagination pagination) throws Exception
    {    	
    	return Dao.qryByCode("TD_B_CPE_LIST", "SEL_BY_CPE_CONDS", data, pagination);
    }

}
