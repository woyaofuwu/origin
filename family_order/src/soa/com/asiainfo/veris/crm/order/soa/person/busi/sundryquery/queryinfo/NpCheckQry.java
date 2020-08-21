package com.asiainfo.veris.crm.order.soa.person.busi.sundryquery.queryinfo;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSExportTaskExecutor;

public class NpCheckQry extends CSExportTaskExecutor{
	
	public IDataset executeExport(IData data, Pagination page) throws Exception
	{    	
		IData param = data.subData("cond", true);
	
	    IDataset result = CSAppCall.call("SS.NpCheckSVC.queryImportData", param);
	    return result;
	} 

}
