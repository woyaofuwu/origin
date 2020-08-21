
package com.asiainfo.veris.crm.order.soa.person.busi.interboss.mobileoperation;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class RemoteCrossRegServiceSVC extends CSBizService
{
    private static final long serialVersionUID = 1L;

    public IDataset getCrossRegserviceInfo(IData input) throws Exception
    {
        RemoteCrossRegServiceBean bean = BeanManager.createBean(RemoteCrossRegServiceBean.class);
        return bean.getCrossRegserviceInfo(input);
    }

    /**
     * 跨区入网业务 接口
     * 
     * @data 2013-9-25
     * @param input
     * @return
     * @throws Exception
     */
    // public IDataset dealTradeRep(IData input) throws Exception
    // {
    // IDataset dataset = new DatasetList();
    // SpannedBusiReqInfBean bean = BeanManager.createBean(SpannedBusiReqInfBean.class);
    // IData data = bean.dealTradeRep(input);
    // dataset.add(data);
    // return dataset;
    // }

    public IDataset getCustInfo(IData input) throws Exception
    {
        IDataset dataset = new DatasetList();
        RemoteCrossRegServiceBean bean = BeanManager.createBean(RemoteCrossRegServiceBean.class);
        IData data = bean.getCustInfo(input);
        dataset.add(data);
        return dataset;
    }

    public IDataset updateInfo(IData input) throws Exception
    {
        IDataset dataset = new DatasetList();
        RemoteCrossRegServiceBean bean = BeanManager.createBean(RemoteCrossRegServiceBean.class);
        IData data = bean.updateInfo(input);
        dataset.add(data);
        return dataset;
    }
}
