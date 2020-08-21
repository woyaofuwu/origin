
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class UserDeductInfoQrySVC extends CSBizService
{
    public IDataset getUserDeductByUserId(IData input) throws Exception
    {
        String userId = input.getString("USER_ID");
        String destroyTag = input.getString("DESTROY_TAG");

        return UserDeductInfoQry.getUserDeductByUserId(userId, destroyTag);
    }
}
