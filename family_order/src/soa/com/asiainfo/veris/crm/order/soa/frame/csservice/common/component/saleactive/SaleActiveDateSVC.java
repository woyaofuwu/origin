
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.component.saleactive;

import com.ailk.common.data.IData;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class SaleActiveDateSVC extends CSBizService
{
    private static final long serialVersionUID = 3272953040005405151L;

    public IData callActiveStartEndDate(IData Param) throws Exception
    {
        SaleActiveDateBean saleActiveDateBean = BeanManager.createBean(SaleActiveDateBean.class);
        return saleActiveDateBean.callActiveStartEndDate(Param);
    }
}
