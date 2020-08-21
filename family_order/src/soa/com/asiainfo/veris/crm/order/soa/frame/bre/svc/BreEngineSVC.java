
package com.asiainfo.veris.crm.order.soa.frame.bre.svc;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class BreEngineSVC extends CSBizService
{
    /**
     * 业务规则检查
     * 
     * @param databus
     * @return
     * @throws Exception
     */
    public IData bre4SuperLimit(IData databus) throws Exception
    {
        return BreEngine.bre4SuperLimit(databus);
    }
}
