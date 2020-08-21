
package com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.datainfo.userinfo.usersvc;

import com.ailk.biz.view.IBizCommon;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcintf.datainfo.userinfo.usersvc.UserSvcInfoIntf;

public class UserSvcInfoIntfViewUtil
{

    public static IDataset qryGrpUserSvcByUserSvcId(IBizCommon bc, String userId, String serviceId) throws Exception
    {
        IDataset infosDataset = UserSvcInfoIntf.qryUserSvcByUserSvcId(bc, userId, serviceId, Route.CONN_CRM_CG);

        return infosDataset;
    }
}
