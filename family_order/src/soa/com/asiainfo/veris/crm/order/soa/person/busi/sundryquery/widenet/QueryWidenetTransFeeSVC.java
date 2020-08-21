
package com.asiainfo.veris.crm.order.soa.person.busi.sundryquery.widenet;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class QueryWidenetTransFeeSVC extends CSBizService
{
    private static final long serialVersionUID = 4829236996987004886L;

    /**
     * 功能：校园宽带费用转移查询
     */
    public IDataset queryTransFee(IData data) throws Exception
    {
        QueryWidenetTransFeeBean bean = (QueryWidenetTransFeeBean) BeanManager.createBean(QueryWidenetTransFeeBean.class);
        return bean.queryTransFee(data, getPagination());
    }
}
