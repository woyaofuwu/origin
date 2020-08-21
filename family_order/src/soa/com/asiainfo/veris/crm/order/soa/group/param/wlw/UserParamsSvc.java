
package com.asiainfo.veris.crm.order.soa.group.param.wlw;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class UserParamsSvc extends CSBizService
{
    private static final long serialVersionUID = 1L;

    public IDataset getServiceParam(IData idata) throws Exception
    {
        UserParams userParams = new UserParams();
        IDataset output = userParams.getServiceParam(idata);
        return output;
    }
}
