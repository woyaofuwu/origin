
package com.asiainfo.veris.crm.order.web.frame.csview.common.svcintf.datainfo.userinfo.userproductinfo;

import com.ailk.biz.view.IBizCommon;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;

public class UserProductInfoIntf
{
    /**
     * 通过USERID,USERIDA,PRODUCTID查询用户产品信息列表
     * 
     * @param bc
     * @param userId
     * @param userIdA
     * @param productId
     * @param routeId
     * @return
     * @throws Exception
     */
    public static IDataset qryUserProductInfsByUserIdAndUserIdAProductId(IBizCommon bc, String userId, String userIdA, String productId, String routeId) throws Exception
    {
        IData inparam = new DataMap();
        inparam.put("USER_ID", userId);
        inparam.put("USER_ID_A", userIdA);
        inparam.put("PRODUCT_ID", productId);
        inparam.put(Route.ROUTE_EPARCHY_CODE, routeId);
        return CSViewCall.call(bc, "CS.UserProductInfoQrySVC.getProductInfoByUserIdUserIdAProdId", inparam);
    }

}
