
package com.asiainfo.veris.crm.order.soa.person.busi.fixedtelephone.trunktrade;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.exception.UserTelephoeException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserTelephoeInfoQry;

public class ChangeTrunkMainUserSVC extends CSBizService
{
    private static final long serialVersionUID = 1L;

    /**
     * 进行校验判断
     * 
     * @param pd
     * @param inData
     * @return
     * @throws Exception
     */
    public void checkInfo(IData inData) throws Exception
    {
        IDataset userTrunkSet = UserTelephoeInfoQry.getUserTrunkInfoByUserId(inData.getString("USER_ID"));
        if (IDataUtil.isEmpty(userTrunkSet))
            CSAppException.apperr(UserTelephoeException.CRM_TELEPHOE_1);

    }

}
