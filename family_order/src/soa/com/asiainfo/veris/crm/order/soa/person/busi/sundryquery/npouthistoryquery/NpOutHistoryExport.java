
package com.asiainfo.veris.crm.order.soa.person.busi.sundryquery.npouthistoryquery;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSExportTaskExecutor;

public class NpOutHistoryExport extends CSExportTaskExecutor
{
    public IDataset executeExport(IData param, Pagination arg1) throws Exception
    {

        // super.transImpexpData(param);
        // NpOutHistoryQueryBean bean = (NpOutHistoryQueryBean) BeanManager.createBean(NpOutHistoryQueryBean.class);
        return CSAppCall.call("SS.NpOutHistoryQuerySVC.getOutNpInfos", param);

    }
}
