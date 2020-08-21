
package com.asiainfo.veris.crm.order.soa.group.internetofthings;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class SuspendResumeWlwServiceSVC extends CSBizService
{

    private static final long serialVersionUID = 1L;

    public final IDataset crtTrade(IData map) throws Exception
    {
        SuspendResumeWlwServiceBean bean = new SuspendResumeWlwServiceBean();
        return bean.crtTrade(map);
    }

}
