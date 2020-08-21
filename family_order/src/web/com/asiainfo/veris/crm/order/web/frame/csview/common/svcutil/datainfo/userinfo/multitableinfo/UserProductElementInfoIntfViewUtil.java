
package com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.datainfo.userinfo.multitableinfo;

import com.ailk.biz.view.IBizCommon;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcintf.datainfo.userinfo.multitableinfo.UserProductElementInfoIntf;

public class UserProductElementInfoIntfViewUtil
{
    /**
     * 通过集团用户id查询集团用户订购的元素列表
     * 
     * @param bc
     * @param userId
     * @return
     * @throws Exception
     */
    public static IDataset qryGrpUserElementInfosByUserId(IBizCommon bc, String userId) throws Exception
    {
        return qryUserElementInfosByUserIdAndUserIdA(bc, userId, "-1", Route.CONN_CRM_CG);
    }

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
        IDataset infosDataset = UserProductElementInfoIntf.qryUserElementInfosByUserIdAndUserIdA(bc, userId, userIdA, routeId);

        return infosDataset;
    }

}
