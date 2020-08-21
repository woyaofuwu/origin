
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.sysorg;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class StaffOperLogSVC extends CSBizService
{

    public void getStaffOperLog(IData data) throws Exception
    {

        StaffOperLogQry.getStaffOperLog(data);
    }
}
