
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.rule;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class SimpleRuleCfgSVC extends CSBizService
{
    private static final long serialVersionUID = 1L;

    public IDataset createSimpleRule(IData input) throws Exception
    {
        SimpleRuleCfgBean bean = BeanManager.createBean(SimpleRuleCfgBean.class);
        IDataset dataset = bean.createSimpleRule(input);

        return dataset;
    }
}
