
package com.asiainfo.veris.crm.order.soa.person.busi.sundryquery.terminalqry;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSExportTaskExecutor;

public class ExportTerminalBook extends CSExportTaskExecutor
{

    @Override
    public IDataset executeExport(IData pdata, Pagination page) throws Exception
    {
        // TODO Auto-generated method stub
        IData data = pdata.subData("cond", true);
        IDataset result = CSAppCall.call("SS.TerminalBookQuerySVC.queryTerminalBooks", data);
        return result;
    }

}
