
package com.asiainfo.veris.crm.order.soa.frame.csservice.group.creategroupunifiedbill;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizIntercept;

public class CreateGroupUnifiedBillTransData extends CSBizIntercept
{

    public boolean before(IData header, IData input) throws Exception
    {
        String inModeCode = input.getString("IN_MODE_CODE");
        String xSubtransCode = input.getString("X_SUBTRANS_CODE");
        String userId = "";
        return true;
    }
}
