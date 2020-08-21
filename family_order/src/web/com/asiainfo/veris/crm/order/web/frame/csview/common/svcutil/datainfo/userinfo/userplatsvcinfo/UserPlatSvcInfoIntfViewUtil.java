
package com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.datainfo.userinfo.userplatsvcinfo;

import com.ailk.biz.view.IBizCommon;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcintf.datainfo.userinfo.userplatsvcinfo.UserPlatSvcInfoIntf;

public class UserPlatSvcInfoIntfViewUtil
{

    /**
     * 通过成员用户ID和集团产品ID查询用户订购的集团产品ID下的sp服务列表信息
     * 
     * @param bc
     * @param userId
     * @param grpProductId
     * @param routeId
     * @return
     * @throws Exception
     */
    public static IDataset qryMebPlatSvcInfosByUserIdAndGrpProductId(IBizCommon bc, String userId, String grpProductId, String routeId) throws Exception
    {
        IDataset infosDataset = UserPlatSvcInfoIntf.qryMebPlatSvcInfosByUserIdAndGrpProductId(bc, userId, grpProductId, routeId);

        return infosDataset;
    }

}
