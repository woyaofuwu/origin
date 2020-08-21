
package com.asiainfo.veris.crm.order.soa.frame.csservice.group.createOutNetNumber;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.base.GroupOrderService;

public class CreateOutNetNumberSVC extends GroupOrderService
{

    private static final long serialVersionUID = -1016763713016358994L;

    public IDataset createOutNetNumber(IData inparam) throws Exception
    {
        CreateOutNetNumber createOutNetNumber = new CreateOutNetNumber();

        return createOutNetNumber.crtTrade(inparam);
    }
}
