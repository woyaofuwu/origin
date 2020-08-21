package com.asiainfo.veris.crm.order.soa.person.busi.np.npcheckimp;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class NpCheckImpBean extends CSBizBean{
	
    public IDataset queryImportData(IData data, Pagination pagination) throws Exception
    {    	
    	return Dao.qryByCodeParser("TF_B_NPCHECK", "SEL_BY_CONDS", data, pagination);
    }
    
    public boolean modifyData(IData data) throws Exception
    {   
    	IData param = new DataMap();
    	param.put("SERIAL_NUMBER", data.getString("SERIAL_NUMBER"));
    	param.put("CUST_NAME", data.getString("CUST_NAME"));
    	param.put("PSPT_TYPE_CODE", data.getString("PSPT_TYPE_CODE"));
    	param.put("PSPT_ID", data.getString("PSPT_ID"));
    	param.put("ACCEPT_DATE", SysDateMgr.getSysTime());
    	param.put("ACCEPT_MONTH", SysDateMgr.getCurMonth());
    	param.put("STATE", "0");
    	param.put("RSRV_STR1", CSBizBean.getVisit().getStaffId());
    	param.put("RSRV_STR2", CSBizBean.getVisit().getDepartName());
    	param.put("RSRV_STR3", SysDateMgr.getSysTime());
    	
    	
    	return Dao.insert("TF_B_NPCHECK", param);
    }

}
