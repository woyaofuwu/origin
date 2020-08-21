
package com.asiainfo.veris.crm.order.soa.group.modifypayrelation;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.base.GroupOrderService;

public class MebPayRelaDestroySVC extends GroupOrderService
{
    private static final long serialVersionUID = 1L;

    public IDataset mebPayRelaDestroy(IData map) throws Exception
    {
        MebPayRelaDestroyBean bean = new MebPayRelaDestroyBean();

        return bean.crtTrade(map);
    }
    
    
    public IDataset batMebPayRelaDestroy(IData map) throws Exception
    {
        BatMebPayRelaDestroyBean bean = new BatMebPayRelaDestroyBean();

        return bean.crtTrade(map);
    }
    
}
