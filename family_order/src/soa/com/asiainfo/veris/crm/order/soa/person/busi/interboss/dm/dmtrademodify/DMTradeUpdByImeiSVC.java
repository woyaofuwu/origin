
package com.asiainfo.veris.crm.order.soa.person.busi.interboss.dm.dmtrademodify;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class DMTradeUpdByImeiSVC extends CSBizService
{
    private static final long serialVersionUID = 1L;

    public IDataset loadChildTradeInfo(IData input) throws Exception
    {
        DMTradeUpdByImeiBean tradeModifyBean = BeanManager.createBean(DMTradeUpdByImeiBean.class);

        return tradeModifyBean.loadChildTradeInfo(input);
    }
}
