
package com.asiainfo.veris.crm.order.soa.person.busi.sundryquery.chlcomminfo;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class QueryChlCommInfoSVC extends CSBizService
{
    private static final long serialVersionUID = 4829236996987004886L;

    /**
     * 查询业务区
     * 
     * @param param
     * @return
     * @throws Exception
     */
    public IDataset queryArea(IData param) throws Exception
    {
        QueryChlCommInfoBean bean = BeanManager.createBean(QueryChlCommInfoBean.class);
        return bean.queryArea();
    }

    /**
     * 功能：查询渠道补贴信息 作者：GongGuang
     */
    public IDataset queryChlCommInfo(IData data) throws Exception
    {
        QueryChlCommInfoBean bean = (QueryChlCommInfoBean) BeanManager.createBean(QueryChlCommInfoBean.class);
        return bean.queryChlCommInfo(data, getPagination());
    }
}
