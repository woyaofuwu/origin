
package com.asiainfo.veris.crm.order.soa.frame.bcf.demotest;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class J2eeTestSVC extends CSBizService
{
    private static final long serialVersionUID = 1L;

    public IDataset testBat(IData idata) throws Exception
    {
        return J2eeTestBean.testBat(idata);
    }
}
