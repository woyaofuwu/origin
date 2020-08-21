
package com.asiainfo.veris.crm.order.soa.group.task.exp;

import com.ailk.biz.impexp.ExportTaskExecutor;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.soa.group.custmanager.CustManagerTJNumBean;

public class CustManagerTJNumExportTask extends ExportTaskExecutor
{

    @Override
    public IDataset executeExport(IData param, Pagination arg1) throws Exception
    {
        String mgrStraffId = param.getString("cond_MANAGER_STAFF_ID");
        String startData = param.getString("cond_IN_DATE_START");
        String endData = param.getString("cond_IN_DATE_END");
        String activcId = param.getString("cond_ACTIVE_ID");
        String tjNunber = param.getString("cond_TJNUMBER");

        IData inputParam = new DataMap();
        inputParam.put("MANAGER_STAFF_ID", mgrStraffId);
        inputParam.put("IN_DATE_START", startData);
        inputParam.put("IN_DATE_END", endData);
        inputParam.put("ACTIVE_ID", activcId);
        inputParam.put("TJNUMBER", tjNunber);
        IDataset result = CustManagerTJNumBean.queryCustManagerTjNums(inputParam);
        return result;
    }
}
