
package com.asiainfo.veris.crm.order.soa.person.busi.broadband.cttnet.cttnettradeinfoquery;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class CttTradeInfoQuerySVC extends CSBizService
{
    public IDataset getTradeInfo(IData input) throws Exception
    {
        CttTradeInfoQueryBean bean = (CttTradeInfoQueryBean) BeanManager.createBean(CttTradeInfoQueryBean.class);
        return bean.getTradeInfo(input, this.getPagination());
    }
}
