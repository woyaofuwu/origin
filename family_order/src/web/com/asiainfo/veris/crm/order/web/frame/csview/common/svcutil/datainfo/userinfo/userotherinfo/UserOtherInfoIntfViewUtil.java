
package com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.datainfo.userinfo.userotherinfo;

import com.ailk.biz.view.IBizCommon;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcintf.datainfo.userinfo.userotherinfo.UserOtherInfoIntf;

public class UserOtherInfoIntfViewUtil
{
    /**
     * 查询集团用户的催缴和打印信息
     * 
     * @param bc
     * @param grpUserId
     * @return
     * @throws Exception
     */
    public static IDataset qryGrpAskPrintInfoByUserIdAndRsrvValueCode(IBizCommon bc, String grpUserId) throws Exception
    {
        IDataset infosDataset = qryGrpUserOtherInfosByUserIdAndRsrvValueCode(bc, grpUserId, "GRUA");

        return infosDataset;
    }

    /**
     * 通过集团用户ID查询总机号码信息
     * 
     * @param bc
     * @param grpUserId
     * @return
     * @throws Exception
     */
    public static IDataset qryGrpSuperTeleInfoByUserIdAndRsrvValueCode(IBizCommon bc, String grpUserId) throws Exception
    {
        IDataset infosDataset = qryGrpUserOtherInfosByUserIdAndRsrvValueCode(bc, grpUserId, "MUTISUPERTEL");

        return infosDataset;
    }

    /**
     * 通过集团的USER_ID 和 RSRV_VALUE_CODE 查询userother信息列表
     * 
     * @param bc
     * @param grpUserId
     * @param rsrvValueCode
     * @return
     * @throws Exception
     */
    public static IDataset qryGrpUserOtherInfosByUserIdAndRsrvValueCode(IBizCommon bc, String grpUserId, String rsrvValueCode) throws Exception
    {
        IDataset infosDataset = qryUserOtherInfosByUserIdAndRsrvValueCode(bc, grpUserId, rsrvValueCode, Route.CONN_CRM_CG);

        return infosDataset;
    }

    /**
     * 通过USER_ID 和 RSRV_VALUE_CODE 查询userother信息列表
     * 
     * @param bc
     * @param userId
     * @param rsrvValueCode
     * @param routeId
     * @return
     * @throws Exception
     */
    public static IDataset qryUserOtherInfosByUserIdAndRsrvValueCode(IBizCommon bc, String userId, String rsrvValueCode, String routeId) throws Exception
    {
        IDataset infosDataset = UserOtherInfoIntf.qryUserOtherInfosByUserIdAndRsrvValueCode(bc, userId, rsrvValueCode, routeId);

        return infosDataset;
    }
}
