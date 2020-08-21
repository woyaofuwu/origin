
package com.asiainfo.veris.crm.order.soa.frame.csservice.group.changeuserelement;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.bizctrl.BizCtrlType;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.base.GroupOrderService;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.util.GrpInvoker;

public class ChangeUserElementSvc extends GroupOrderService
{
    private static final long serialVersionUID = 619975662940477683L;

    public IDataset changeUserElement(IData inparam) throws Exception
    {
        // 规则特殊参数baseinfo:custId,PRODUCT_ID,TRADE_TYPE_CODE,RULE_BIZ_TYPE_CODE,RULE_BIZ_KIND_CODE,IF_CENTRETYPE
        // RULE_EVNT_CODE必须为null,表示校验所有规则
        // inparam.put("RULE_BIZ_TYPE_CODE","chkBeforeForGrp");
        // inparam.put("RULE_BIZ_KIND_CODE","GrpUserChg");

        // inparam.put("ID", inparam.getString("USER_ID"));
        String busiType = inparam.getString("BUSI_CTRL_TYPE", BizCtrlType.ChangeUserDis);
        IDataset obj = GrpInvoker.ivkProduct(inparam, busiType, "CreateClass");
        return obj;
    }
}
