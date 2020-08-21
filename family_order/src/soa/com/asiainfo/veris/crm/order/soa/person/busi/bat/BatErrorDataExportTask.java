
package com.asiainfo.veris.crm.order.soa.person.busi.bat;

import com.ailk.biz.impexp.ExportTaskExecutor;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.bat.BatDealInfoQry;

public class BatErrorDataExportTask extends ExportTaskExecutor
{
    @Override
    public IDataset executeExport(IData data, Pagination arg1) throws Exception
    {
        String paramStr = data.getString("PARAM");
        IDataset params = new DatasetList(paramStr);
        IDataset fails = BatDealInfoQry.queryFaildInfo(params.getData(0)); // bean.queryFaildInfo(pd,data);
        return fails;
    }

}
