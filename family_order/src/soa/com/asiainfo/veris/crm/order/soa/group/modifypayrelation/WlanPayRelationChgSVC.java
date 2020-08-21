
package com.asiainfo.veris.crm.order.soa.group.modifypayrelation;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.base.GroupOrderService;

public class WlanPayRelationChgSVC extends GroupOrderService
{

    /**
     * wlan高级付费关系变更
     */
    private static final long serialVersionUID = 1L;

    public IDataset wlanPayRelationChg(IData data) throws Exception
    {
        WlanPayRelationChgBean bean = new WlanPayRelationChgBean();
        return bean.crtTrade(data);
    }
}
