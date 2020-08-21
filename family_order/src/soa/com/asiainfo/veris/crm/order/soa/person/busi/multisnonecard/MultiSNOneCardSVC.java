
package com.asiainfo.veris.crm.order.soa.person.busi.multisnonecard;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class MultiSNOneCardSVC extends CSBizService
{
    private static final long serialVersionUID = 1L;

    public IDataset getCheck(IData input) throws Exception
    {
        MultiSNOneCardBean bean = (MultiSNOneCardBean) BeanManager.createBean(MultiSNOneCardBean.class);
        IDataset dataset = bean.getCheck(input);
        return dataset;
    }

    public IDataset getCommonParam(IData input) throws Exception
    {
        MultiSNOneCardBean bean = (MultiSNOneCardBean) BeanManager.createBean(MultiSNOneCardBean.class);
        IDataset dataset = bean.getCommonParam(input);
        return dataset;
    }

    public IDataset getCommonParam1(IData input) throws Exception
    {
        MultiSNOneCardBean bean = (MultiSNOneCardBean) BeanManager.createBean(MultiSNOneCardBean.class);
        IDataset dataset = bean.getCommonParam1(input);
        return dataset;
    }

    public IDataset getMultiPlatInfo(IData input) throws Exception
    {
        MultiSNOneCardBean bean = (MultiSNOneCardBean) BeanManager.createBean(MultiSNOneCardBean.class);
        IDataset dataset = bean.getMultiPlatInfo(input);
        return dataset;
    }

    public IDataset getProductInfo(IData input) throws Exception
    {
        MultiSNOneCardBean bean = (MultiSNOneCardBean) BeanManager.createBean(MultiSNOneCardBean.class);
        IDataset dataset = bean.getProductInfo(input);
        return dataset;
    }

    public IDataset getUserOther(IData input) throws Exception
    {
        MultiSNOneCardBean bean = (MultiSNOneCardBean) BeanManager.createBean(MultiSNOneCardBean.class);
        IDataset dataset = bean.getUserOther(input);
        return dataset;
    }
}
