
package com.asiainfo.veris.crm.order.soa.group.task.exp;

import com.ailk.biz.impexp.ExportTaskExecutor;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.AsynDealVisitUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;

public class VpmnPrintInvoiceExportTask extends ExportTaskExecutor
{

    @Override
    public IDataset executeExport(IData param, Pagination arg1) throws Exception
    {
        String today = SysDateMgr.getSysDate();
        String serialNumber = param.getString("cond_SERIAL_NUMBER", "");
        String staffIdOne = param.getString("cond_STAFF_ID", "");

        String staffIdTwo = getVisit().getStaffId();

        if (staffIdOne == null || staffIdOne.equals(""))
        {

            staffIdOne = staffIdTwo;

        }
        String startDate = param.getString("cond_START_DATE");

        if (startDate == null || startDate.equals(""))
        {

            startDate = today;

        }
        String endDate = param.getString("cond_END_DATE");
        if (endDate == null || endDate.equals(""))
        {

            endDate = today;

        }
        IData date = new DataMap();

        date.put("START_DATE", startDate);
        date.put("END_DATE", endDate);
        date.put("STAFF_ID", staffIdOne);
        date.put("SERIAL_NUMBER", serialNumber);

        AsynDealVisitUtil.dealVisitInfo(param);

        IDataset dataOutput = CSAppCall.call("SS.BookTradeSVC.queryPrints", date);

        return dataOutput;
    }

}
