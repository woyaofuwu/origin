
package com.asiainfo.veris.crm.order.soa.group.task.exp;

import com.ailk.biz.impexp.ExportTaskExecutor;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.soa.group.grppaymark.BreakGrpPayMarkQry;

public class BreakGrpPayMarkExportTask extends ExportTaskExecutor
{

	@Override
    public IDataset executeExport(IData inParam, Pagination pagination) throws Exception
    {
        IDataset dataset = new DatasetList();
        IData param = new DataMap();
        String serialNumber = inParam.getString("SERIAL_NUM_GRP");
        param.put("SERIAL_NUMBER", serialNumber);
        dataset = BreakGrpPayMarkQry.expBreakGrpPayMarkInfo(param, pagination);
        return dataset;
    }
    
}
