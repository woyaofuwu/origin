
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.interboss;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class MobilePhoneQrySVC extends CSBizService
{
    private static final long serialVersionUID = 1L;

    public IDataset qryMpAreaInfo(IData input) throws Exception
    {
        MoInfoQry bean = new MoInfoQry();
        IDataset result = bean.queryMpAreaCode(input);
        result.getData(0).put("AREA_NAME", result.getData(0).getString("PROVINCE") + result.getData(0).getString("CITY_NAME"));
        return result;
    }
}
