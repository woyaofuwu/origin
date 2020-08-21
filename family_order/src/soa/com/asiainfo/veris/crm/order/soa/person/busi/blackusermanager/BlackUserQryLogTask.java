
package com.asiainfo.veris.crm.order.soa.person.busi.blackusermanager;

import com.ailk.biz.impexp.ExportTaskExecutor;
import com.ailk.common.data.IData;  
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall; 

public class BlackUserQryLogTask extends ExportTaskExecutor
{
    @Override
    public IDataset executeExport(IData param, Pagination pagination) throws Exception
    {
    	IData input = param.subData("cond", true); 
    	  
        IDataset dataset = CSAppCall.call("SS.BlackUserManageSVC.qryBlackUserLog", input); 
        return dataset;
    } 
}
