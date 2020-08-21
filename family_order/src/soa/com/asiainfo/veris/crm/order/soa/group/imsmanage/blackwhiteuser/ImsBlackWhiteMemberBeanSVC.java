
package com.asiainfo.veris.crm.order.soa.group.imsmanage.blackwhiteuser;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.base.GroupOrderService;

public class ImsBlackWhiteMemberBeanSVC extends GroupOrderService
{
    private static final long serialVersionUID = 1L;

    public IDataset crtTrade(IData inparam) throws Exception
    {
        ImsBlackWhiteGrpBean centrex = new ImsBlackWhiteGrpBean();
        return centrex.crtTrade(inparam);
    }

    public IDataset crtTradeForMeb(IData inparam) throws Exception
    {
        ImsBlackWhiteMemberBean centrex = new ImsBlackWhiteMemberBean();
        return centrex.crtTrade(inparam);
    }
}
