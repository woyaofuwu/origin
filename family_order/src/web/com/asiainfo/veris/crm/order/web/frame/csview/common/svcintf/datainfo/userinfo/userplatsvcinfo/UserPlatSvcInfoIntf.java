
package com.asiainfo.veris.crm.order.web.frame.csview.common.svcintf.datainfo.userinfo.userplatsvcinfo;

import com.ailk.biz.view.IBizCommon;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;

public class UserPlatSvcInfoIntf
{

    /**
     * 通过成员用户ID和集团产品ID查询用户订购的集团产品ID下的sp服务列表信息
     * 
     * @param bc
     * @param userId
     * @param productId
     * @param routeId
     * @return
     * @throws Exception
     */
    public static IDataset qryMebPlatSvcInfosByUserIdAndGrpProductId(IBizCommon bc, String userId, String grpProductId, String routeId) throws Exception
    {
        IData inparam = new DataMap();
        inparam.put("USER_ID", userId);
        inparam.put("PRODUCT_ID", grpProductId);
        inparam.put(Route.ROUTE_EPARCHY_CODE, routeId);
        return CSViewCall.call(bc, "CS.UserPlatSvcInfoQrySVC.getGrpPlatSvcByUserId", inparam);
    }

}
