
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class AreaParamInfoQrySVC extends CSBizService
{

    private static final long serialVersionUID = -7936246940324572179L;

    public IDataset queryAreaCode(IData inparam) throws Exception
    {
        String parentAreaCode = inparam.getString("PARENT_AREA_CODE");
        return AreaParamInfoQry.queryAreaCode(parentAreaCode);
    }

}
