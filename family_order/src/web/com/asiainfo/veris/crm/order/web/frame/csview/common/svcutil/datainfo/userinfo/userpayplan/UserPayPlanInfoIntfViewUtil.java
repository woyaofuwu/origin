
package com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.datainfo.userinfo.userpayplan;

import com.ailk.biz.view.IBizCommon;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcintf.datainfo.userinfo.userpayplan.UserPayPlanInfoIntf;

public class UserPayPlanInfoIntfViewUtil
{
    /**
     * 查询集团成员付费计划信息
     * 
     * @param bc
     * @param mebUserId
     * @param grpUserId
     * @return
     * @throws Exception
     */

    public static IDataset getGrpMemPayPlanByUserId(IBizCommon bc, String mebUserId, String grpUserId, String routeId) throws Exception
    {
        IDataset infosDataset = UserPayPlanInfoIntf.getGrpMemPayPlanByUserId(bc, mebUserId, grpUserId, routeId);

        return infosDataset;
    }

    /**
     * 查询集团付费计划信息
     * 
     * @param bc
     * @param grpUserId
     * @return
     * @throws Exception
     */
    public static IDataset qryPayPlanInfosByGrpUserIdForGrp(IBizCommon bc, String grpUserId) throws Exception
    {
        IDataset infosDataset = UserPayPlanInfoIntf.qryPayPlanInfosByGrpUserIdForGrp(bc, grpUserId, "-1");

        return infosDataset;
    }

}
