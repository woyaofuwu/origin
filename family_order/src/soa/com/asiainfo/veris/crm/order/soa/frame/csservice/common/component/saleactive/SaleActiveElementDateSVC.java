
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.component.saleactive;

import com.ailk.common.data.IData;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class SaleActiveElementDateSVC extends CSBizService
{
    private static final long serialVersionUID = 3272953040005405151L;

    public IData callElementStartEndDate(IData Param) throws Exception
    {
        SaleActiveElementDateBean saleActiveElementDateBean = BeanManager.createBean(SaleActiveElementDateBean.class);
        return saleActiveElementDateBean.callElementStartEndDate(Param);
    }
}
