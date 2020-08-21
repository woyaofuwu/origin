
package com.asiainfo.veris.crm.order.soa.group.groupintf.credit;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.base.GroupOrderService;

public class CreditRegSvc extends GroupOrderService
{

    private static final long serialVersionUID = 6337637316099786690L;

    public IDataset creditReg(IData param) throws Exception
    {
        CreditRegBean bean = new CreditRegBean();
        return bean.crtTrade(param);
    }

}
