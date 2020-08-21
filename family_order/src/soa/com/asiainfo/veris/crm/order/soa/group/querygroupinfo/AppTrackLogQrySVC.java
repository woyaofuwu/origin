
package com.asiainfo.veris.crm.order.soa.group.querygroupinfo;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.group.common.query.AppTrackLogQuery;

public class AppTrackLogQrySVC extends CSBizService
{
    private static final long serialVersionUID = -7776196081189535574L;

    public void delAppTrackLogs(IData iData) throws Exception
    {

        AppTrackLogQuery.delAppTrackLogs(iData);
    }

    public IDataset qryAppTrackLogs(IData param) throws Exception
    {
        return AppTrackLogQuery.qryAppTrackLogs(param, this.getPagination());
    }
}
