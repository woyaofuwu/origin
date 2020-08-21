
package com.asiainfo.veris.crm.order.soa.person.busi.badnessinfo;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class BadnessInfoSVC extends CSBizService
{

    public IDataset getMonths(IData data) throws Exception
    {
        BadnessInfoBean bean = BeanManager.createBean(BadnessInfoBean.class);
        return bean.getMonths(data);
    }

    public IDataset getReportCode(IData data) throws Exception
    {
        BadnessInfoBean bean = BeanManager.createBean(BadnessInfoBean.class);
        return bean.getReportCode(data);
    }

    public IDataset queryBadnessInfos(IData data) throws Exception
    {
        BadnessInfoBean bean = BeanManager.createBean(BadnessInfoBean.class);
        return bean.queryBadnessInfos(data, this.getPagination());
    }

    public IDataset queryBadnessInfos4S(IData data) throws Exception
    {
        // BadnessInfoBean bean = BeanManager.createBean(BadnessInfoBean.class);
        // return bean.queryBadnessInfosForm(bean.transferData(data), this.getPagination());
        return null;
    }

    public IDataset queryBadnessInfosForm(IData data) throws Exception
    {
        BadnessInfoBean bean = BeanManager.createBean(BadnessInfoBean.class);
        return bean.queryBadnessInfosForm(data, this.getPagination());
    }

    public IDataset staticsBadnessInfos(IData data) throws Exception
    {
        BadnessInfoBean bean = BeanManager.createBean(BadnessInfoBean.class);
        return bean.staticsBadnessInfos(data, this.getPagination());
    }
}
