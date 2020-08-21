
package com.asiainfo.veris.crm.order.soa.person.busi.sundryquery.querylotties;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class QueryUecLottySVC extends CSBizService
{
    private static final long serialVersionUID = 4829236996987004886L;

    public IDataset querylottys(IData data) throws Exception
    {
        QueryUecLottyBean bean = (QueryUecLottyBean) BeanManager.createBean(QueryUecLottyBean.class);
        return bean.queryLotties(data, getPagination());
    }
}
