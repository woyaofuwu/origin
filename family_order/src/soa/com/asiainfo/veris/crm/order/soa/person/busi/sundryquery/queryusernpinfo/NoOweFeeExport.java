
package com.asiainfo.veris.crm.order.soa.person.busi.sundryquery.queryusernpinfo;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSExportTaskExecutor;

public class NoOweFeeExport extends CSExportTaskExecutor
{

    public IDataset executeExport(IData param, Pagination arg1) throws Exception
    {

        return CSAppCall.call("SS.NoOweFeeSVC.getNpOutInfo", param);

    }

}
