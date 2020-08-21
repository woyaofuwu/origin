
package com.asiainfo.veris.crm.order.soa.group.modifypayrelation;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.base.GroupOrderService;

public class VgpoPayRelationChgSVC extends GroupOrderService
{

    private static final long serialVersionUID = -588249234452871337L;

    public IDataset crtTrade(IData data) throws Exception
    {
        VgpoPayRelationChgBean vgpoPayRelationChgBean = new VgpoPayRelationChgBean();

        return vgpoPayRelationChgBean.crtTrade(data);
    }
}
