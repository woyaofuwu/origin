
package com.asiainfo.veris.crm.order.soa.person.busi.sundryquery.producttrans;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class QueryProductTransSVC extends CSBizService
{
    private static final long serialVersionUID = 4829236996987004886L;

    /**
     * 功能：产品转换查询 作者：GongGuang
     */
    public IDataset queryProductTransInfo(IData data) throws Exception
    {
        QueryProductTransBean bean = (QueryProductTransBean) BeanManager.createBean(QueryProductTransBean.class);
        return bean.queryProductTransInfo(data, getPagination());
    }
}
