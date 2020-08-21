
package com.asiainfo.veris.crm.order.soa.frame.csservice.group.destroygroupuser;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.frame.bcf.bizctrl.BizCtrlType;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.base.GroupOrderService;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.util.GrpInvoker;

public class DestroyGroupUserSvc extends GroupOrderService
{
    private static final long serialVersionUID = -6806791566887797420L;

    public IDataset destroyGroupUser(IData inparam) throws Exception
    {
        IDataset obj = new DatasetList();
        // 获取入参,转换
        /*
         * String userId = inparam.getString("USER_ID"); String ifBooking = inparam.getString("IF_BOOKING"); String
         * reasonCode = inparam.getString("REASON_CODE"); String remark = inparam.getString("REMARK");
         */

        // 规则特殊参数baseinfo:custId,PRODUCT_ID,TRADE_TYPE_CODE,RULE_BIZ_TYPE_CODE,RULE_BIZ_KIND_CODE,IF_CENTRETYPE
        // RULE_EVNT_CODE必须为null,表示校验所有规则
        /*
         * inparam.put("RULE_BIZ_TYPE_CODE","chkBeforeForGrp"); inparam.put("RULE_BIZ_KIND_CODE","GrpUserDestory");
         * inparam.put("FEE", "0"); inparam.put("LEAVE_REAL_FEE", "0"); inparam.put("ID", inparam.getString("USER_ID"));
         * inparam.put("ID_TYPE", "1");
         */

        obj = GrpInvoker.ivkProduct(inparam, BizCtrlType.DestoryUser, "CreateClass");

        return obj;
    }
}
