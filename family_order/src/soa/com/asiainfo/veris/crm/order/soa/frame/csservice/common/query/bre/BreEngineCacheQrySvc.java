
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.bre;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class BreEngineCacheQrySvc extends CSBizService
{
    public static IDataset loadBreDefinition(IData databus) throws Exception
    {
        return BreEngineCacheQry.loadBreDefinition(databus);
    }

    /**
     * 获取规则参数定义
     * 
     * @return
     * @throws Exception
     */
    public static IDataset loadBreParameter(IData databus) throws Exception
    {
        return BreEngineCacheQry.loadBreParameter(databus);
    }

}
