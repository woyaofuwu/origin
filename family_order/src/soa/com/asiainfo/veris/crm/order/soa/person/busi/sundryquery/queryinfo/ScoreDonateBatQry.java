package com.asiainfo.veris.crm.order.soa.person.busi.sundryquery.queryinfo;

//import com.ailk.bizservice.base.CSExportTaskExecutor;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSExportTaskExecutor;

public class ScoreDonateBatQry extends CSExportTaskExecutor{

public IDataset executeExport(IData data, Pagination page) throws Exception
	{    	
		//IData param = data.subData("cond", false);
	
	    IDataset result = CSAppCall.call("SS.BatScoreDonateSVC.queryImportData", data);
	    return result;
	}   
}
