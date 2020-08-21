package com.asiainfo.veris.crm.order.soa.group.esop.query;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class WorkformDisSVC extends CSBizService
{
    private static final long serialVersionUID = 1L;

    public IDataset queryWorkformDisStrList(IData input) throws Exception
    {
        String ibsysid = input.getString("IBSYSID");
        String mebOfferCode = input.getString("MEB_OFFER_CODE");
        
        return WorkformDisBean.queryWorkformDisStrList(ibsysid, mebOfferCode);
    }
}
