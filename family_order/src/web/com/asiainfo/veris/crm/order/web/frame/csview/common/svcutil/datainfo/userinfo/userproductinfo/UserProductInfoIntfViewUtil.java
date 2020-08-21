
package com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.datainfo.userinfo.userproductinfo;

import com.ailk.biz.view.IBizCommon;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcintf.datainfo.userinfo.userproductinfo.UserProductInfoIntf;

public class UserProductInfoIntfViewUtil
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
        IDataset infosDataset = UserProductInfoIntf.qryUserProductInfsByUserIdAndUserIdAProductId(bc, userId, userIdA, productId, routeId);

        return infosDataset;
    }

}
