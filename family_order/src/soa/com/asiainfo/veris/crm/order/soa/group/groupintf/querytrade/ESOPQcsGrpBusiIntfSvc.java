
package com.asiainfo.veris.crm.order.soa.group.groupintf.querytrade;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class ESOPQcsGrpBusiIntfSvc extends CSBizService
{

    public IDataset getEosInfo(IData param) throws Throwable
    {
        IDataset resultDataset = ESOPQcsGrpBusiIntf.getEosInfo(param);
        return resultDataset;
    }

}
