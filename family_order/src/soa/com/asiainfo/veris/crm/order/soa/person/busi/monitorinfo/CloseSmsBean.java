
package com.asiainfo.veris.crm.order.soa.person.busi.monitorinfo;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.other.SmsRedmemberQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSvcStateInfoQry;

public class CloseSmsBean extends CSBizBean
{

    public IDataset checkRedMemberIsExists(IData data) throws Exception
    {
        String serialNumber = data.getString("SERIAL_NUMBER");
        return SmsRedmemberQry.checkRedMemberIsExists(serialNumber);
    }

    public IDataset qryUserMainSvcState(IData data) throws Exception
    {
        String userId = data.getString("USER_ID");
        return UserSvcStateInfoQry.queryUserMainTagScvState(userId);
    }
}
