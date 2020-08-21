
package com.asiainfo.veris.crm.order.soa.group.modifyuserdata;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.bizctrl.BizCtrlType;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.base.GroupOrderService;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.util.GrpInvoker;

public class ModifyUserDataSVC extends GroupOrderService
{
    private static final long serialVersionUID = 1L;

    public IDataset crtTrade(IData inParam) throws Exception
    {
        // TradeBaseBean tradeBaseBean = new ModifyUserData();
        //
        // return tradeBaseBean.crtTrade(inParam);
        IDataset obj = GrpInvoker.ivkProduct(inParam, BizCtrlType.ModifyUser, "CreateClass");
        return obj;
    }
}
