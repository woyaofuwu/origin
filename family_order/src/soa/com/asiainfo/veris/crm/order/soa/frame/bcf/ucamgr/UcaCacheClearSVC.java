
package com.asiainfo.veris.crm.order.soa.frame.bcf.ucamgr;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class UcaCacheClearSVC extends CSBizService
{

    private static final long serialVersionUID = 1L;

    public IDataset clear(IData idata) throws Exception
    {

        String paramValue = idata.getString("SERIAL_NUMBER");
        String clearType = idata.getString("CLEAR_TYPE");

        return UcaCacheClear.clear(paramValue, clearType);
    }

}
