
package com.asiainfo.veris.crm.order.soa.person.busi.sundryquery.saleactive;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSExportTaskExecutor;

public class ExportSaleActive extends CSExportTaskExecutor
{

    @Override
    public IDataset executeExport(IData paramIData, Pagination paramPagination) throws Exception
    {
        // TODO Auto-generated method stub

        IDataset result = CSAppCall.call("SS.QuerySaleActiveSVC.querySaleActive", paramIData);

        return result;
    }

}
