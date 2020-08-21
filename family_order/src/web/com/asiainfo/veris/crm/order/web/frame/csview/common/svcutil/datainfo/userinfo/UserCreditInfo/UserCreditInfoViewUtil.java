
package com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.datainfo.userinfo.UserCreditInfo;

import com.ailk.biz.view.IBizCommon;
import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcintf.datainfo.userinfo.userCreditInfo.UserCreditInfoIntf;

public class UserCreditInfoViewUtil
{
    public static String qryUserCredeitValue(IBizCommon bc, String userId) throws Exception
    {
        IData creditData = qryUserCreditInfo(bc, userId);
        String credeitValue = creditData.getString("CREDIT_VALUE", "0");
        return credeitValue;
    }

    /**
     * 查询用户信用度信息
     * 
     * @param bc
     * @param userId
     * @throws Exception
     */
    public static IData qryUserCreditInfo(IBizCommon bc, String userId) throws Exception
    {
        return UserCreditInfoIntf.qryUserCreditInfo(bc, userId, Route.CONN_CRM_CG);
    }
}
