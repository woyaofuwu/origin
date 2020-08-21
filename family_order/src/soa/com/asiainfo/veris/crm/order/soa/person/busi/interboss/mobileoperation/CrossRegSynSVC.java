
package com.asiainfo.veris.crm.order.soa.person.busi.interboss.mobileoperation;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class CrossRegSynSVC extends CSBizService
{
    private static final long serialVersionUID = 1L;

    public IDataset checkAllInfo(IData input) throws Exception
    {
        IDataset dataset = new DatasetList();
        CrossRegSynBean bean = BeanManager.createBean(CrossRegSynBean.class);
        IData data = bean.checkAllInfo(input);
        dataset.add(data);
        return dataset;
    }

    public IDataset checkUserInfo(IData input) throws Exception
    {
        IDataset dataset = new DatasetList();
        CrossRegSynBean bean = BeanManager.createBean(CrossRegSynBean.class);
        IData data = bean.checkUserInfo(input);
        dataset.add(data);
        return dataset;
    }

    public IDataset dealSyn(IData input) throws Exception
    {
        IDataset dataset = new DatasetList();
        CrossRegSynBean bean = BeanManager.createBean(CrossRegSynBean.class);
        IData data = bean.dealSyn(input);
        dataset.add(data);
        return dataset;
    }
}
