
package com.asiainfo.veris.crm.order.soa.person.busi.simcardmgr;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class SelfChangeCardFlowSVC extends CSBizService
{
    protected static Logger log = Logger.getLogger(SelfChangeCardFlowSVC.class);

    public IDataset delFlowInfo(IData input) throws Exception
    {
        SelfChangeCardFlowBean bean = (SelfChangeCardFlowBean) BeanManager.createBean(SelfChangeCardFlowBean.class);
        return bean.delFlowInfo(input);
    }

    public IDataset querySelfCard(IData input) throws Exception
    {
        SelfChangeCardFlowBean bean = (SelfChangeCardFlowBean) BeanManager.createBean(SelfChangeCardFlowBean.class);
        return bean.querySelfCard(input, this.getPagination());
    }

}
