
package com.asiainfo.veris.crm.order.web.frame.csview.common.svcintf.datainfo.userinfo.userotherinfo;

import com.ailk.biz.view.IBizCommon;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;

public class UserOtherInfoIntf
{
    /**
     * 通过USER_ID 和 RSRV_VALUE_CODE 查询userother信息列表
     * 
     * @param bc
     * @param userId
     * @param rsrvValueCode
     * @param routeId
     * @return
     * @throws Exception
     */
    public static IDataset qryUserOtherInfosByUserIdAndRsrvValueCode(IBizCommon bc, String userId, String rsrvValueCode, String routeId) throws Exception
    {
        IData inparam = new DataMap();
        inparam.put("USER_ID", userId);
        inparam.put("RSRV_VALUE_CODE", rsrvValueCode);
        inparam.put(Route.ROUTE_EPARCHY_CODE, routeId);
        return CSViewCall.call(bc, "CS.UserOtherQrySVC.getUserOtherByUserRsrvValueCodeByEc", inparam);
    }

}
