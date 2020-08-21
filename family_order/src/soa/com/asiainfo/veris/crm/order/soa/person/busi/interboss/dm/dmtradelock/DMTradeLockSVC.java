
package com.asiainfo.veris.crm.order.soa.person.busi.interboss.dm.dmtradelock;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class DMTradeLockSVC extends CSBizService
{
    private static final long serialVersionUID = 1L;

    public IDataset loadChildTradeInfo(IData input) throws Exception
    {
        DMTradeLockBean tradeLockBean = BeanManager.createBean(DMTradeLockBean.class);

        return tradeLockBean.loadChildTradeInfo(input);
    }

    public IDataset submitTrade(IData input) throws Exception
    {
        DMTradeLockBean tradeLockBean = BeanManager.createBean(DMTradeLockBean.class);

        IDataset results = tradeLockBean.submitTrade(input);

        return results;
    }
}
