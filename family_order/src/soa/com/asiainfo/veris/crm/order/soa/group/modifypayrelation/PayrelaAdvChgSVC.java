
package com.asiainfo.veris.crm.order.soa.group.modifypayrelation;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.base.GroupOrderService;

public class PayrelaAdvChgSVC extends GroupOrderService
{
    private static final long serialVersionUID = 5426198235477919578L;

    public IDataset crtTrade(IData data) throws Exception
    {
        PayrelaAdvBean prab = new PayrelaAdvBean();
        return prab.crtTrade(data);
    }

    /**
     * 集团统付代付
     * 
     * @param data
     * @return
     * @throws Exception
     */
    public IDataset createRelaTrade(IData data) throws Exception
    {
        GroupAcctPayBean bean = new GroupAcctPayBean();
        return bean.crtTrade(data);
    }

}
