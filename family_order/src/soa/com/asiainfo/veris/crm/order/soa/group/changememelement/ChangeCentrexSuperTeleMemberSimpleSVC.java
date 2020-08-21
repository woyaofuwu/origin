
package com.asiainfo.veris.crm.order.soa.group.changememelement;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.base.GroupOrderService;

public class ChangeCentrexSuperTeleMemberSimpleSVC extends GroupOrderService
{

    private static final long serialVersionUID = 1L;

    public final IDataset crtTrade(IData map) throws Exception
    {
        ChangeCentrexSuperTeleMemberSimple bean = new ChangeCentrexSuperTeleMemberSimple();

        return bean.crtTrade(map);

    }

}
