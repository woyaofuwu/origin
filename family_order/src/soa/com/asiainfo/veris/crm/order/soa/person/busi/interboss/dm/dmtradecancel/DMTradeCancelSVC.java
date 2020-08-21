
package com.asiainfo.veris.crm.order.soa.person.busi.interboss.dm.dmtradecancel;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class DMTradeCancelSVC extends CSBizService
{
    private static final long serialVersionUID = 1L;

    public IDataset getUserInfo(IData input) throws Exception
    {
        DMTradeCancelBean tradeCancelBean = BeanManager.createBean(DMTradeCancelBean.class);

        return tradeCancelBean.getUserInfo(input);
    }

    public IDataset loadChildTradeInfo(IData input) throws Exception
    {
        DMTradeCancelBean tradeCancelBean = BeanManager.createBean(DMTradeCancelBean.class);

        return tradeCancelBean.loadChildTradeInfo(input);
    }

    public IDataset queryDmByImei(IData input) throws Exception
    {
        DMTradeCancelBean tradeCancelBean = BeanManager.createBean(DMTradeCancelBean.class);

        return tradeCancelBean.queryDmByImei(input);
    }

}
