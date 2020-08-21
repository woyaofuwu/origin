
package com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.datainfo.userinfo.fee;

import com.ailk.biz.view.IBizCommon;
import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcintf.datainfo.userinfo.fee.UserFeeIntf;

public class UserFeeIntfViewUtil
{

    /**
     * 查询集团用户预存信息
     * 
     * @param bc
     * @param userId
     *            集团用户ID
     * @return
     * @throws Exception
     */
    public static IData qryGrpUserForegiftInfo(IBizCommon bc, String userId) throws Exception
    {
        return UserFeeIntf.qryUserForegiftInfo(bc, userId, Route.CONN_CRM_CG);
    }

    /**
     * 查询集团用户欠费信息
     * 
     * @param bc
     * @param userId
     *            集团用户ID
     * @return 欠费信息: LAST_OWE_FEE(往月欠费) REAL_FEE(实时欠费) ACCT_BALANCE(实时结余)
     * @throws Exception
     */
    public static IData qryGrpUserOweFeeInfo(IBizCommon bc, String userId) throws Exception
    {
        return UserFeeIntf.qryUserOweFeeInfo(bc, userId, Route.CONN_CRM_CG);
    }

    /**
     * 查询用户预存信息
     * 
     * @param bc
     * @param userId
     *            用户ID
     * @param routeId
     *            路由ID
     * @return
     * @throws Exception
     */
    public static IData qryUserForegiftInfo(IBizCommon bc, String userId, String routeId) throws Exception
    {
        return UserFeeIntf.qryUserForegiftInfo(bc, userId, routeId);
    }

    /**
     * 查询用户欠费信息
     * 
     * @param bc
     * @param userId
     *            用户ID
     * @param routeId
     *            路由ID
     * @return 欠费信息: LAST_OWE_FEE(往月欠费) REAL_FEE(实时欠费) ACCT_BALANCE(实时结余)
     * @throws Exception
     */
    public static IData qryUserOweFeeInfo(IBizCommon bc, String userId, String routeId) throws Exception
    {
        return UserFeeIntf.qryUserOweFeeInfo(bc, userId, routeId);
    }
}
