
package com.asiainfo.veris.crm.order.soa.person.busi.interboss.dm.dmtradeopen;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.person.busi.sundryquery.interboss.dmbusisel.DMBusiMgr;

public class DMCommTradeSVC extends CSBizService
{
    private static final long serialVersionUID = 1L;

    public IDataset DMUpdateData(IData input) throws Exception
    {
        DMBusiMgr bean = new DMBusiMgr();
        return bean.DMUpdateData(input);
    }

    public IDataset loadChildTradeInfo(IData input) throws Exception
    {
        DMTradeOpenBean tradeOpenBean = BeanManager.createBean(DMTradeOpenBean.class);

        return tradeOpenBean.loadChildTradeInfo(input);
    }

    public IDataset loadUpdChildTradeInfo(IData input) throws Exception
    {
        DMTradeUpdBean tradeUpdBean = BeanManager.createBean(DMTradeUpdBean.class);

        return tradeUpdBean.loadChildTradeInfo(input);
    }

}
