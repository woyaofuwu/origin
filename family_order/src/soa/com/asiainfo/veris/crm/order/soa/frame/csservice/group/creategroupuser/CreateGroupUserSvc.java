
package com.asiainfo.veris.crm.order.soa.frame.csservice.group.creategroupuser;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.frame.bcf.bizctrl.BizCtrlType;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.base.GroupOrderService;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.util.GrpInvoker;

public class CreateGroupUserSvc extends GroupOrderService
{
    private static final long serialVersionUID = -2488728747213183001L;

    public IDataset createGroupUser(IData inparam) throws Exception
    {
        IDataset obj = new DatasetList();

        obj = GrpInvoker.ivkProduct(inparam, BizCtrlType.CreateUser, "CreateClass");

        return obj;
    }
}
