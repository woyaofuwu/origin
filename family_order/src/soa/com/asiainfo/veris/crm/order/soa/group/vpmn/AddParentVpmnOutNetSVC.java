
package com.asiainfo.veris.crm.order.soa.group.vpmn;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.base.GroupOrderService;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.base.TradeBaseBean;

public class AddParentVpmnOutNetSVC extends GroupOrderService
{
    private static final long serialVersionUID = 1L;

    public IDataset crtTrade(IData inParam) throws Exception
    {
        TradeBaseBean tradeBaseBean = new AddParentVpmnOutNetBean();

        return tradeBaseBean.crtTrade(inParam);
    }
}
