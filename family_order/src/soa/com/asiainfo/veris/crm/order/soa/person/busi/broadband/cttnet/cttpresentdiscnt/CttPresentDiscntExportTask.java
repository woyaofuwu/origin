
package com.asiainfo.veris.crm.order.soa.person.busi.broadband.cttnet.cttpresentdiscnt;

import com.ailk.biz.impexp.ExportTaskExecutor;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.DiscntInfoQry;

public class CttPresentDiscntExportTask extends ExportTaskExecutor
{
    @Override
    public IDataset executeExport(IData data, Pagination arg1) throws Exception
    {
        IDataset detial = DiscntInfoQry.queryPresentInfos(data.getString("ACCT_ID"), data.getString("PRESENT_SERIAL_NUMBER"), data.getString("START_DATE"), data.getString("END_DATE"), null);
        return detial;
    }

}
