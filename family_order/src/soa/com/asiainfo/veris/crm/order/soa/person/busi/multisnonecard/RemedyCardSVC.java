
package com.asiainfo.veris.crm.order.soa.person.busi.multisnonecard;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class RemedyCardSVC extends CSBizService
{
    private static final long serialVersionUID = 1L;

    public IDataset getCheck(IData input) throws Exception
    {
        RemedyCardBean bean = (RemedyCardBean) BeanManager.createBean(RemedyCardBean.class);
        IDataset dataset = bean.getCheck(input);
        return dataset;
    }

    public IDataset getImsi(IData input) throws Exception
    {
        RemedyCardBean bean = (RemedyCardBean) BeanManager.createBean(RemedyCardBean.class);
        IDataset dataset = bean.getImsi(input);
        return dataset;
    }

    public IDataset getOperType(IData input) throws Exception
    {
        RemedyCardBean bean = (RemedyCardBean) BeanManager.createBean(RemedyCardBean.class);
        IDataset dataset = bean.getOperType(input);
        return dataset;
    }

    public IDataset getOtherSNInfo(IData input) throws Exception
    {
        String sn = input.getString("SERIAL_NUMBER");

        RemedyCardBean bean = (RemedyCardBean) BeanManager.createBean(RemedyCardBean.class);
        return bean.getOtherSNInfo(sn);
    }

    public IDataset loadInfos(IData input) throws Exception
    {
        RemedyCardBean bean = (RemedyCardBean) BeanManager.createBean(RemedyCardBean.class);
        IDataset dataset = bean.loadInfos(input);
        return dataset;
    }
}
