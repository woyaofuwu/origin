
package com.asiainfo.veris.crm.order.soa.person.busi.nonbossfee;

import com.ailk.biz.impexp.ExportTaskExecutor;
import com.ailk.common.data.IData;  
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall; 

public class NonBossFeeUserItemTask extends ExportTaskExecutor
{
    @Override
    public IDataset executeExport(IData param, Pagination pagination) throws Exception
    {
    	IData input = param.subData("cond", true);
    	input.put("EXC_TAG", "1");
        IDataset dataset = CSAppCall.call("SS.NonBossFeeUserItemMgrSVC.getNonBossFeeUserItems", input); 
        return dataset; 
    } 
}
