
package com.asiainfo.veris.crm.order.soa.person.busi.multisnonecard;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class SearchMultiSNOneCardSVC extends CSBizService
{

    private static final long serialVersionUID = 1L;

    public IDataset getdeployState(IData input) throws Exception
    {

        SearchMultiSNOneCardBean bean = (SearchMultiSNOneCardBean) BeanManager.createBean(SearchMultiSNOneCardBean.class);
        IDataset dataset = bean.getdeployState();

        return dataset;
    }

    public IDataset getPreforOneCardMutiNumber(IData input) throws Exception
    {

        SearchMultiSNOneCardBean bean = (SearchMultiSNOneCardBean) BeanManager.createBean(SearchMultiSNOneCardBean.class);
        IDataset dataset = bean.getPreforOneCardMutiNumber(input);

        return dataset;
    }

    public IDataset querySearchCard(IData input) throws Exception
    {

        SearchMultiSNOneCardBean bean = (SearchMultiSNOneCardBean) BeanManager.createBean(SearchMultiSNOneCardBean.class);
        IDataset dataset = bean.querySearchCard(input, getPagination());

        return dataset;
    }

}
