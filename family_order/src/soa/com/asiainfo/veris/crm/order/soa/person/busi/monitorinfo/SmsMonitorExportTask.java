
package com.asiainfo.veris.crm.order.soa.person.busi.monitorinfo;

import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.Pagination;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSExportTaskExecutor;

public class SmsMonitorExportTask extends CSExportTaskExecutor
{
    public void chkDataByStaticValue(IData data, String colName, String typeId) throws Exception
    {
        String value = data.getString(colName);
        if (StringUtils.isBlank(value))
        {
            return;
        }

        String dataName = StaticUtil.getStaticValue(typeId, value);
        data.put(colName, dataName);
    }

    public IDataset executeExport(IData data, Pagination page) throws Exception
    {
        getVisit().setStaffId(data.getString("STAFF_ID"));
        IDataset result = CSAppCall.call("SS.MonitorInfoQuerySVC.queryUncheckSmsInfo", data);
        if (IDataUtil.isNotEmpty(result))
        {
            for (int i = 0, size = result.size(); i < size; i++)
            {
                IData temp = result.getData(i);
                chkDataByStaticValue(temp, "DATA_TYPE", "MONITORFILE_DATATYPE");
                chkDataByStaticValue(temp, "REASON_CODE", "HARASSPHONE_REASONCODE");
                chkDataByStaticValue(temp, "PROCESS_TAG", "HARASSPHONE_PROCESSTAG");
            }
        }
        return result;
    }
}
