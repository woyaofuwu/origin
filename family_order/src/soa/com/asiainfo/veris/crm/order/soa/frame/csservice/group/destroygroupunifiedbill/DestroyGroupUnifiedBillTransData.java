
package com.asiainfo.veris.crm.order.soa.frame.csservice.group.destroygroupunifiedbill;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizIntercept;

public class DestroyGroupUnifiedBillTransData extends CSBizIntercept
{

    @Override
    public boolean before(String svcName, IData head, IData indata) throws Exception
    {
        return true;
    }

    private void transInputData(IData input) throws Exception
    {
        String xSubtransCode = input.getString("X_SUBTRANS_CODE");
    }
}
