
package com.asiainfo.veris.crm.order.soa.group.enterpriseinternettv;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.base.GroupOrderService;

public class EnterpriseInternetTVUserSVC extends GroupOrderService
{

    private static final long serialVersionUID = 1L;

    public final IDataset crtOrder(IData map) throws Exception
    {

        // 调用OrderBaseBean,生成集团台账和成员台账
        EnterpriseInternetTVUserBean bean = new EnterpriseInternetTVUserBean();
        return bean.crtOrder(map);
    }
}
