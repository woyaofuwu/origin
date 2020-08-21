
package com.asiainfo.veris.crm.order.soa.person.busi.monitorinfo;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSExportTaskExecutor;

public class SuspectManageExportTask extends CSExportTaskExecutor
{
    public IDataset executeExport(IData data, Pagination page) throws Exception
    {
        getVisit().setStaffId(data.getString("STAFF_ID"));
        return CSAppCall.call("SS.MonitorInfoQuerySVC.querySuspectInfos", data);
    }
}
