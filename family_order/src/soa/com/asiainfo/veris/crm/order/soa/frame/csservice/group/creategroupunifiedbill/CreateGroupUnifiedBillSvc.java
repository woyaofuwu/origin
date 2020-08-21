
package com.asiainfo.veris.crm.order.soa.frame.csservice.group.creategroupunifiedbill;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.bizctrl.BizCtrlType;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.base.GroupOrderService;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.util.GrpInvoker;

public class CreateGroupUnifiedBillSvc extends GroupOrderService
{
    private static final long serialVersionUID = 4666950305555768204L;

    public IDataset createGroupMember(IData inparam) throws Exception
    {
        inparam.put("RULE_BIZ_TYPE_CODE", "chkBeforeForGrp");
        inparam.put("RULE_BIZ_KIND_CODE", "GrpUnifiedBillOpen");
        IDataset obj = GrpInvoker.ivkProduct(inparam, BizCtrlType.CreateUnifiedBill, "CreateClass");
        return obj;
    }

    @Override
    public void setIntercept() throws Exception
    {
        // 设置拦截器,根据不同的in_mode_code trans数据
        setMethodIntercept(CreateGroupUnifiedBillTransData.class.getName());
    }
}
