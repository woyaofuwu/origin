
package com.asiainfo.veris.crm.order.soa.group.fee;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class CreateOneOffFeeCloudSVC extends CSBizService
{
    private static final long serialVersionUID = 1L;

    public IDataset crtTrade(IData data) throws Exception
    {
        CreateOneOffFeeCloudBean bean = new CreateOneOffFeeCloudBean();

        return bean.crtTrade(data);
    }

}
