package com.asiainfo.veris.crm.order.soa.person.busi.sundryquery.checknpoutresultquery;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class CheckNpOutResultQuerySVC extends CSBizService
{
    private static final long serialVersionUID = 4829236996987004886L;

    /**
     * 功能：携出预审核查询 作者：mengqx
     */
    public IDataset queryCheckNpOut(IData data) throws Exception
    {
    	CheckNpOutResultQueryBean bean = (CheckNpOutResultQueryBean) BeanManager.createBean(CheckNpOutResultQueryBean.class);
        return bean.queryCheckNpOut(data, getPagination());
    }
}
