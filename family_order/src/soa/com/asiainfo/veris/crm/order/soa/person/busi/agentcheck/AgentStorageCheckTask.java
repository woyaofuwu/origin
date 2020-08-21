
package com.asiainfo.veris.crm.order.soa.person.busi.agentcheck;

import com.ailk.biz.impexp.ExportTaskExecutor;
import com.ailk.common.data.IData; 
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall; 

public class AgentStorageCheckTask extends ExportTaskExecutor
{
    @Override
    public IDataset executeExport(IData param, Pagination pagination) throws Exception
    {
        IData cond = new DataMap();
         
        String departInfo=param.getString("AGENT_DEPART_ID1");
        String departId=departInfo.substring(0,5); 
        cond.put("DEPART_ID", departId);
        cond.put("START_TIME", param.getString("START_TIME",""));
        cond.put("END_TIME", param.getString("END_TIME",""));
        IDataset dataset = CSAppCall.call("SS.AgentStorageCheckSVC.checkCardStorage", cond);
        return dataset;
    } 
}
