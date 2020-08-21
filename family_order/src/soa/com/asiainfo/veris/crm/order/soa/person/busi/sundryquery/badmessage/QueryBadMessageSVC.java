
package com.asiainfo.veris.crm.order.soa.person.busi.sundryquery.badmessage;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class QueryBadMessageSVC extends CSBizService
{
    private static final long serialVersionUID = 4829236996987004886L;

    /**
     *不良短信查询
     * 
     * @param data
     * @return
     * @throws Exception
     */
    public IDataset queryBadMessage(IData data) throws Exception
    {
        QueryBadMessageBean bean = (QueryBadMessageBean) BeanManager.createBean(QueryBadMessageBean.class);
        return bean.queryBadMessage(data, getPagination());

    }
}
