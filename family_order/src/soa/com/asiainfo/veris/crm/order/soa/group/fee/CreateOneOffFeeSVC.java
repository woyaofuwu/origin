
package com.asiainfo.veris.crm.order.soa.group.fee;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class CreateOneOffFeeSVC extends CSBizService
{
    private static final long serialVersionUID = 1L;

    public IDataset crtTrade(IData data) throws Exception
    {
        CreateOneOffFeeBean bean = new CreateOneOffFeeBean();

        return bean.crtTrade(data);
    }

}
