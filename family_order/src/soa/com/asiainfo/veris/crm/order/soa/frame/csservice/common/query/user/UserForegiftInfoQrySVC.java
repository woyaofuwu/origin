
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class UserForegiftInfoQrySVC extends CSBizService
{
    private static final long serialVersionUID = 1L;

    /**
     * 查询用户预存信息
     * 
     * @param data
     * @return
     * @throws Exception
     */
    public IDataset qryUserForegiftByUserId(IData data) throws Exception
    {
        String userId = data.getString("USER_ID");

        return UserForegiftInfoQry.getUserForegift(userId);
    }

}
