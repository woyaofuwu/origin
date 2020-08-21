
package com.asiainfo.veris.crm.order.web.frame.csview.common.svcintf.datainfo.userinfo.usersvc;

import com.ailk.biz.view.IBizCommon;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;

public class UserSvcInfoIntf
{
    /**
     * 查询用户已经订购的某产品元素信息（资费+服务）
     * 
     * @param bc
     * @param userId
     * @param productId
     * @param routeId
     * @return
     * @throws Exception
     */
    public static IDataset qryUserOrderElementsByUserIdAndProductId(IBizCommon bc, String userId, String productId, String routeId) throws Exception
    {
        IData inparam = new DataMap();
        inparam.put("USER_ID", userId);
        inparam.put("PRODUCT_ID", productId);
        inparam.put(Route.ROUTE_EPARCHY_CODE, routeId);
        return CSViewCall.call(bc, "CS.UserSvcInfoQrySVC.getElementFromPackageByUser", inparam);
    }

    public static IDataset qryUserSvcByUserSvcId(IBizCommon bc, String userId, String serviceId, String routeId) throws Exception
    {
        IData inparam = new DataMap();
        inparam.put("USER_ID", userId);
        inparam.put("SERVICE_ID", serviceId);
        inparam.put(Route.ROUTE_EPARCHY_CODE, routeId);
        return CSViewCall.call(bc, "CS.UserSvcInfoQrySVC.qryUserSvcByUserSvcId", inparam);
    }

}
