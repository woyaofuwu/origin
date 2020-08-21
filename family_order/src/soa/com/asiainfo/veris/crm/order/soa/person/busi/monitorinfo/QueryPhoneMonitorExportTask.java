
package com.asiainfo.veris.crm.order.soa.person.busi.monitorinfo;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.pub.consts.PersonConst;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSExportTaskExecutor;

public class QueryPhoneMonitorExportTask extends CSExportTaskExecutor
{

    public IDataset executeExport(IData data, Pagination page) throws Exception
    {
        String type = data.getString("QUERY_TYPE");
        getVisit().setStaffId(data.getString("STAFF_ID"));
        if (PersonConst.QUERY_TYPE_SUMMARY_BETWEEN.equals(type))
        {
            data.put("config", "export/monitorinfo/HarassPhoneSummaryList.xml");
        }
        else if (PersonConst.QUERY_TYPE_SHEET_BETWEEN.equals(type))
        {
            data.put("config", "export/monitorinfo/HarassPhoneSheetList.xml");
        }
        else
        {
            data.put("config", "export/monitorinfo/HarassPhoneList.xml");
        }
        return CSAppCall.call("SS.MonitorInfoQuerySVC.queryMonitorInfo", data);
    }
}
