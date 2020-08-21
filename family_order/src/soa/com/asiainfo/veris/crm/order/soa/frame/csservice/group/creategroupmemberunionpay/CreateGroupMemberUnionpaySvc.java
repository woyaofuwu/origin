
package com.asiainfo.veris.crm.order.soa.frame.csservice.group.creategroupmemberunionpay;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.bizctrl.BizCtrlType;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.util.GrpInvoker;

public class CreateGroupMemberUnionpaySvc extends CSBizService
{
    public IDataset changeGroupMemberUnionPay(IData inparam) throws Exception
    {
        IDataset obj = GrpInvoker.ivkProduct(inparam, BizCtrlType.ChangeMemberUnionPay, "CreateClass");
        return obj;
    }

    public IDataset createGroupMemberUnionPay(IData inparam) throws Exception
    {
        IDataset obj = GrpInvoker.ivkProduct(inparam, BizCtrlType.CreateMemberUnionPay, "CreateClass");
        return obj;
    }
}
