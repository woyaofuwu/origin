
package com.asiainfo.veris.crm.order.soa.person.busi.nonbossfee;

import com.ailk.biz.impexp.ExportTaskExecutor;
import com.ailk.common.data.IData;  
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall; 

public class NonBossFeeItemTask extends ExportTaskExecutor
{
    @Override
    public IDataset executeExport(IData param, Pagination pagination) throws Exception
    {
    	IData input = param.subData("cond", true);
        IDataset dataset = CSAppCall.call("SS.NonBossFeeItemMgrSVC.getNonBossFeeItems", input); 
        return dataset; 
    } 
}
