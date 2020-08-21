
package com.asiainfo.veris.crm.order.soa.group.upgprelation;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.base.GroupOrderService;

public class UnifyPayRelationSVC extends GroupOrderService
{
    private static final long serialVersionUID = 5426198235477919578L;

    /**
     * 集团统一付费产品成员新增(高级付费)
     * @param data
     * @return
     * @throws Exception
     */
    public IDataset crtTrade(IData data) throws Exception
    {
        UnifyPayRelationBean prab = new UnifyPayRelationBean();
        return prab.crtTrade(data);
    }

}
