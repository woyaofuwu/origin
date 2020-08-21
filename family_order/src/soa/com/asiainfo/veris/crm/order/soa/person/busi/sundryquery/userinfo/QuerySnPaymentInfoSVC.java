
package com.asiainfo.veris.crm.order.soa.person.busi.sundryquery.userinfo;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class QuerySnPaymentInfoSVC extends CSBizService
{
    private static final long serialVersionUID = 1L;

    public IDataset querySnPaymentInfo(IData input) throws Exception
    {
        QuerySnPaymentInfoBean bean = BeanManager.createBean(QuerySnPaymentInfoBean.class);

        return bean.querySnPaymentInfo(input, getPagination());
    }

}
