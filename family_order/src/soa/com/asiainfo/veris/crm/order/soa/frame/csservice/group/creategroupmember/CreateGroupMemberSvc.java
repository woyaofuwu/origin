
package com.asiainfo.veris.crm.order.soa.frame.csservice.group.creategroupmember;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.bizctrl.BizCtrlType;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.base.GroupOrderService;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.util.GrpInvoker;

public class CreateGroupMemberSvc extends GroupOrderService
{
    private static final long serialVersionUID = -3543119973140596648L;

    public IDataset createGroupMember(IData inparam) throws Exception
    {

        // 规则特殊参数baseinfo:custId,PRODUCT_ID,TRADE_TYPE_CODE,RULE_BIZ_TYPE_CODE,RULE_BIZ_KIND_CODE,IF_CENTRETYPE
        // RULE_EVNT_CODE必须为null,表示校验所有规则
        // 此处只能根据成员sn查成员userid,规则中需求的数据不是最小集合,无法梳理
        // inparam.put("RULE_BIZ_TYPE_CODE", "chkBeforeForGrp");
        // inparam.put("RULE_BIZ_KIND_CODE", "GrpMebOrder");

        /*
         * String serialNumber=inparam.getString("SERIAL_NUMBER"); IDataset userInfos =
         * BizQuery.getNormalUserAndProductInfoBySn(serialNumber, MofficeInfoQry.getOfficeBySN(serialNumber)); if
         * (IDataUtil.isEmpty(userInfos)) { CSAppException.apperr(BofException.CRM_BOF_002); } IData userInfo =
         * userInfos.getData(0); inparam.put("USER_ID_B",userInfo.getString("USER_ID"));
         * inparam.put("BRAND_CODE_B",userInfo.getString("BRAND_CODE"));
         * inparam.put("EPARCHY_CODE_B",userInfo.getString("EPARCHY_CODE"));
         */

        IDataset obj = GrpInvoker.ivkProduct(inparam, BizCtrlType.CreateMember, "CreateClass");
        return obj;
    }

}
