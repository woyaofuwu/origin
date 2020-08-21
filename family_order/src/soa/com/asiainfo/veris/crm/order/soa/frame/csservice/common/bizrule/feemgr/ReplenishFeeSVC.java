package com.asiainfo.veris.crm.order.soa.frame.csservice.common.bizrule.feemgr;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class ReplenishFeeSVC extends CSBizService
{
    private static final long serialVersionUID = 1L;

    public IData getReplenishFeeSn(IData input) throws Exception
    {
        return ReplenishFee.getReplenishFeeSn(input);
    }
}
