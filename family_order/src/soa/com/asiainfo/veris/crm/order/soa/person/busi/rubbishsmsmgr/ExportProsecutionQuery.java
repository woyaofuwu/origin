
package com.asiainfo.veris.crm.order.soa.person.busi.rubbishsmsmgr;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSExportTaskExecutor;

public class ExportProsecutionQuery extends CSExportTaskExecutor
{

    @Override
    public IDataset executeExport(IData data, Pagination page) throws Exception
    {
        IData param = data.subData("cond", true);
        data.putAll(param);
        getVisit().setStaffId(data.getString("STAFF_ID"));
        return CSAppCall.call("SS.ProsecutionQuerySVC.queryProsecution", param);
    }

}
