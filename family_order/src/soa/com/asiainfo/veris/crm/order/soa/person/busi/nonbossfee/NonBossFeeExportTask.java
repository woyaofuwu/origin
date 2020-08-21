
package com.asiainfo.veris.crm.order.soa.person.busi.nonbossfee;

import com.ailk.biz.impexp.ExportTaskExecutor;
import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.Pagination;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.nonbossfee.NonBossFeeLogInfoQry;

public class NonBossFeeExportTask extends ExportTaskExecutor
{
    @Override
    public IDataset executeExport(IData param, Pagination pagination) throws Exception
    {
        IData input = param.subData("cond", true);
        
        String[] inputName=input.getNames();
        for(int i=0;i<inputName.length;i++){
        	param.put(inputName[i], input.getString(inputName[i]));
        }
        param.put(input.getString("FUZZY_QUERY"), input.getString("FUZZY_COMMENT"));
        IDataset dataset = NonBossFeeLogInfoQry.queryNonBossFeeLog(param, pagination, null);
        if (IDataUtil.isNotEmpty(dataset))
        { 
            for (int i = 0, len = dataset.size(); i < len; i++)
            {
            	String ticketType=dataset.getData(i).getString("TICKET_TYPE");
            	if(!"".equals(ticketType)){
            		dataset.getData(i).put("TICKET_TYPE", StaticUtil.getStaticValue("INVOICE_TYPE", ticketType));
            	} 
            	String ifCancel=dataset.getData(i).getString("IF_CENCER");
            	if(!"".equals(ifCancel)){
            		dataset.getData(i).put("IF_CENCER", StaticUtil.getStaticValue("CLUB_YESORNO", ifCancel));
            	}
            }
        }
        return dataset;
    }

}
