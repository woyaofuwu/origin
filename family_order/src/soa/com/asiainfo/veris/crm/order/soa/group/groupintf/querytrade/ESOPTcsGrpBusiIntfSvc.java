
package com.asiainfo.veris.crm.order.soa.group.groupintf.querytrade;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class ESOPTcsGrpBusiIntfSvc extends CSBizService
{

    public IDataset callEosGrpBusi(IData param) throws Throwable
    {
        IDataset resultDataset = ESOPTcsGrpBusiIntf.callEosGrpBusi(param);
        return resultDataset;
    }

}
