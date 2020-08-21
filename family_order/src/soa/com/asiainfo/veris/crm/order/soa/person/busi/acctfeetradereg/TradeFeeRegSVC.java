
package com.asiainfo.veris.crm.order.soa.person.busi.acctfeetradereg;

import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class TradeFeeRegSVC extends CSBizService
{

    private static final long serialVersionUID = 1L;

    /**
     * 押金转预存返销
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IData tradeCancelForAcct(IData input) throws Exception
    {
        IData data = new DataMap();
        TradeFeeRegBean bean = BeanManager.createBean(TradeFeeRegBean.class);
        data = bean.tradeCancelForAcct(input);
        return data;
    }

    /**
     * 押金转预存登记台账
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IData tradeRegForAcct(IData input) throws Exception
    {
        IData data = new DataMap();
        TradeFeeRegBean bean = BeanManager.createBean(TradeFeeRegBean.class);
        data = bean.tradeRegForAcct(input);
        return data;
    }

}
