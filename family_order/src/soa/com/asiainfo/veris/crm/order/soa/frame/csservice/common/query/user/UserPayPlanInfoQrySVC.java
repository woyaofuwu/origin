
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class UserPayPlanInfoQrySVC extends CSBizService
{
    private static final long serialVersionUID = 1L;

    public IDataset getGrpMemPayPlanByUserId(IData input) throws Exception
    {
        String userId = input.getString("USER_ID");
        String userIdA = input.getString("USER_ID_A");

        return UserPayPlanInfoQry.getGrpMemPayPlanByUserId(userId, userIdA);
    }

    public IDataset getPayPlanInfosByUserIdForGrp(IData input) throws Exception
    {
        String userId = input.getString("USER_ID");
        String userIdA = input.getString("USER_ID_A");

        return UserPayPlanInfoQry.getGrpPayPlanByUserId(userId, userIdA);
    }
}
