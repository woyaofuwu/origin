
package com.asiainfo.veris.crm.order.soa.group.modifymemdata;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.bizctrl.BizCtrlType;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.base.GroupOrderService;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.util.GrpInvoker;

public class ModifyMemDataSVC extends GroupOrderService
{
    private static final long serialVersionUID = 1L;

    public IDataset crtTrade(IData inParam) throws Exception
    {
        IDataset obj = GrpInvoker.ivkProduct(inParam, BizCtrlType.ModifyMember, "CreateClass");
        return obj;
    }
}
