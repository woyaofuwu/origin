
package com.asiainfo.veris.crm.order.web.frame.csview.common.svcintf.datainfo.userinfo.multitableinfo;

import com.ailk.biz.view.IBizCommon;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;

public class UserProductElementInfoIntf
{
    /**
     * 通过成员用户ID,集团用户ID查询号码已经订购该集团产品的服务，资费，资源信息
     * 
     * @param bc
     * @param userId
     * @param userIdA
     * @param routeId
     * @return
     * @throws Exception
     */
    public static IDataset qryUserElementInfosByUserIdAndUserIdA(IBizCommon bc, String userId, String userIdA, String routeId) throws Exception
    {
        IData inparam = new DataMap();
        inparam.put("USER_ID", userId);
        inparam.put("USER_ID_A", userIdA);
        inparam.put(Route.ROUTE_EPARCHY_CODE, routeId);
        return CSViewCall.call(bc, "CS.ProductInfoQrySVC.getUserProductElement", inparam);
    }

}
