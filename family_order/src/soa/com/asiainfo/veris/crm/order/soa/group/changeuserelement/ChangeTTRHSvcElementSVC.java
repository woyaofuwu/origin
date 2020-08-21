
package com.asiainfo.veris.crm.order.soa.group.changeuserelement;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.base.GroupOrderService;

public class ChangeTTRHSvcElementSVC extends GroupOrderService
{
    public IDataset crtTrade(IData data) throws Exception
    {
        ChangeTTRHSvcElement ctse = new ChangeTTRHSvcElement();
        return ctse.crtTrade(data);
    }
}
