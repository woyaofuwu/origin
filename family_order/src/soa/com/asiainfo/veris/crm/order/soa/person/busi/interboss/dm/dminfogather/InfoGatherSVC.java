
package com.asiainfo.veris.crm.order.soa.person.busi.interboss.dm.dminfogather;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class InfoGatherSVC extends CSBizService
{
    private static final long serialVersionUID = 1L;

    public IDataset getBusiList(IData input) throws Exception
    {
        InfoGatherBean bean = BeanManager.createBean(InfoGatherBean.class);
        IDataset dataset = bean.getBusiList(input);
        return dataset;
    }

    public IDataset sendHttpGather(IData input) throws Exception
    {
        IDataset dataset = new DatasetList();
        InfoGatherBean bean = BeanManager.createBean(InfoGatherBean.class);
        IData data = bean.sendHttpGather(input);
        dataset.add(data);
        return dataset;
    }

    public IDataset sendTuxGather(IData input) throws Exception
    {
        InfoGatherBean bean = BeanManager.createBean(InfoGatherBean.class);
        IDataset dataset = bean.sendTuxGather(input);
        return dataset;
    }

}
