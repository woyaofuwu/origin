
package com.asiainfo.veris.crm.order.soa.person.busi.broadband.widenet.widepreregaudit;

import com.ailk.biz.impexp.ExportTaskExecutor;
import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;

public class WidePreRegAuditExportTask extends ExportTaskExecutor
{

    @Override
    public IDataset executeExport(IData paramIData, Pagination arg1) throws Exception
    {
        IDataset ibplatDs = CSAppCall.call("SS.WidePreRegAuditService.getWideNetBookList", paramIData);
        for (int i = 0, size = ibplatDs.size(); i < size; i++)
        {
            String WBBW = ibplatDs.getData(i).getString("WBBW", "");
            String PRE_CAUSE = ibplatDs.getData(i).getString("PRE_CAUSE", "");
            String REG_STATUS = ibplatDs.getData(i).getString("REG_STATUS", "");

            if (!"".equals(WBBW))
            {
                ibplatDs.getData(i).put("WBBW", StaticUtil.getStaticValue("WIDE_CAPACITY", WBBW));
            }
            if (!"".equals(PRE_CAUSE))
            {
                ibplatDs.getData(i).put("PRE_CAUSE", StaticUtil.getStaticValue("WIDE_PRE_CAUSE", PRE_CAUSE));
            }
            if (!"".equals(REG_STATUS))
            {
                ibplatDs.getData(i).put("REG_STATUS", StaticUtil.getStaticValue("WIDE_REG_STATUS", REG_STATUS));
            }
            
            
        }

        return ibplatDs;

    }

}
