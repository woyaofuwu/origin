
package com.asiainfo.veris.crm.order.web.frame.csview.common.svcintf.datainfo.userinfo.userCreditInfo;

import com.ailk.biz.view.IBizCommon;
import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;

public class UserCreditInfoIntf
{

    /**
     * 查询用户信用度信息
     * 
     * @param bc
     * @param userId
     * @param routeId
     * @return
     * @throws Exception
     */
    public static IData qryUserCreditInfo(IBizCommon bc, String userId, String routeId) throws Exception
    {
        IData svcData = new DataMap();
        svcData.put("USER_ID", userId);
        svcData.put(Route.ROUTE_EPARCHY_CODE, routeId);

        IData userCreditData = CSViewCall.callone(bc, "CS.UserCreditInfoQrySVC.userCreditInfo", svcData);

        return userCreditData;
    }
}
