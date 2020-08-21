
package com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.util;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class DealSpecialServerParamSvc extends CSBizService
{

    public IDataset loadSpecialServerParam(IData inparam) throws Exception
    {

        IDataset obj = DealSpecialServerParam.loadSpecialServerParam(inparam);
        return obj;
    }

}
