
package com.asiainfo.veris.crm.order.soa.person.busi.interboss.dm.dmelementgather;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class ElementGatherSVC extends CSBizService
{
    private static final long serialVersionUID = 1L;

    public IDataset sendHttpGather(IData input) throws Exception
    {
        IDataset dataset = new DatasetList();
        ElementGatherBean bean = BeanManager.createBean(ElementGatherBean.class);
        dataset.add(bean.sendHttpGather(input));
        return dataset;
    }

    public IDataset sendTuxGather(IData input) throws Exception
    {
        ElementGatherBean bean = BeanManager.createBean(ElementGatherBean.class);
        IDataset dataset = bean.sendTuxGather(input);
        return dataset;
    }

}
