
package com.asiainfo.veris.crm.order.soa.group.groupintf.querytrade;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.ESOPCall;

public class ESOPTcsGrpBusiIntf
{

    public static IDataset callEosGrpBusi(IData param) throws Throwable
    {
        IDataset resultDataset = ESOPCall.callESOP("ITF_EOS_TcsGrpBusi", param);
        return resultDataset;
    }

}
