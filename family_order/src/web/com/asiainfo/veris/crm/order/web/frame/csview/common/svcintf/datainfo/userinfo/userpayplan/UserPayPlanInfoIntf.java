
package com.asiainfo.veris.crm.order.web.frame.csview.common.svcintf.datainfo.userinfo.userpayplan;

import com.ailk.biz.view.IBizCommon;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;

public class UserPayPlanInfoIntf
{

    /**
     * 查询集团成员付费计划信息
     * 
     * @param bc
     * @param userId
     * @param userIdA
     * @return
     * @throws Exception
     */
    public static IDataset getGrpMemPayPlanByUserId(IBizCommon bc, String userId, String userIdA, String routeId) throws Exception
    {
        IData inparam = new DataMap();
        inparam.put("USER_ID", userId);
        inparam.put("USER_ID_A", userIdA);
        inparam.put(Route.ROUTE_EPARCHY_CODE, routeId);
        return CSViewCall.call(bc, "CS.UserPayPlanInfoQrySVC.getGrpMemPayPlanByUserId", inparam);
    }

    /**
     * 查询集团付费计划信息
     * 
     * @param bc
     * @param userId
     * @param userIdA
     * @return
     * @throws Exception
     */
    public static IDataset qryPayPlanInfosByGrpUserIdForGrp(IBizCommon bc, String userId, String userIdA) throws Exception
    {
        IData inparam = new DataMap();
        inparam.put("USER_ID", userId);
        inparam.put("USER_ID_A", userIdA);
        return CSViewCall.call(bc, "CS.UserPayPlanInfoQrySVC.getPayPlanInfosByUserIdForGrp", inparam);
    }
}
