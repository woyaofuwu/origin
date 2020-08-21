
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.rule;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class CheckTradeSVC extends CSBizService
{
    private static final long serialVersionUID = 1L;

    public IDataset checkBeforeTrade(IData input) throws Exception
    {
        CheckTradeBean bean = BeanManager.createBean(CheckTradeBean.class);
        IDataset dataset = bean.checkBeforeTrade(input);

        return dataset;
    }
    
    public void verifyUnFinishTrade(IData input) throws Exception
    {
    	CheckTradeBean bean = BeanManager.createBean(CheckTradeBean.class);
    	bean.verifyunFinishTrade(input);
    }
    
}
