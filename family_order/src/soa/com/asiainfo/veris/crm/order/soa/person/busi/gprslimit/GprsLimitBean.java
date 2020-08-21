package com.asiainfo.veris.crm.order.soa.person.busi.gprslimit;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.AcctCall;

public class GprsLimitBean  extends CSBizBean{
	
    public IDataset queryImportData(IData data, Pagination pagination) throws Exception
    {    	
    	return Dao.qryByCodeParser("TF_F_USER_GRPSLIMIT", "SEL_BY_CONDS", data, pagination);
    }
    
    
    public IDataset queryLimitData(IData data, Pagination pagination) throws Exception
    {    	
    	return Dao.qryByCodeParser("TF_F_USER_OTHER", "SEL_BY_LIMIT_USER", data, pagination);
    }
    
    public IDataset queryDayData(IData data) throws Exception
    {    
    	String serialNumber = data.getString("SERIAL_NUMBER_DAY");
    	String qryDate = data.getString("QRY_DATE").replace("-", "");
    	IDataset  resultSet = AcctCall.QueryFreeGPRSPer(serialNumber, qryDate);
    	return resultSet;
    }
    
}
