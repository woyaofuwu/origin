
package com.asiainfo.veris.crm.order.soa.group.task.exp;

import com.ailk.biz.impexp.ExportTaskExecutor;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;

public class ExportLineWorkformdata extends ExportTaskExecutor
{

    @Override
    public IDataset executeExport(IData data, Pagination pag) throws Exception
    {
        IData params = new DataMap();
        params.put("GROUP_ID", data.getString("cond_GROUP_ID",""));
        params.put("SERIAL_NUMBER", data.getString("cond_SERIAL_NUMBER",""));
        params.put("INSTANCE_NUMBER", data.getString("cond_INSTANCE_NUMBER",""));
        params.put("STAFF_ID", data.getString("cond_STAFF_ID",""));
        params.put("CITY_CODE", data.getString("cond_CITY_CODE",""));

        IDataset output = CSAppCall.call("SS.LineWorkformdataSVC.qryLineWorkformdata", params);

        return output;
    }

}
