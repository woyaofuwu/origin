
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.AcctCall;

public class UserOwenInfoQrySVC extends CSBizService
{
    private static final long serialVersionUID = 1L;

    // 按group_id查
    public IDataset getOweFeeByGrpUserId(IData input) throws Exception
    {
        String groupId = input.getString("GROUP_ID");

        IData idata = AcctCall.getOweFeeByGroupId(groupId);

        IDataset data = IDataUtil.idToIds(idata);

        return data;
    }

    public IDataset getOweFeeByUserId(IData input) throws Exception
    {
        String userId = input.getString("USER_ID");
        IData idata = AcctCall.getOweFeeByUserId(userId);
        IDataset data = IDataUtil.idToIds(idata);

        return data;
    }
}
