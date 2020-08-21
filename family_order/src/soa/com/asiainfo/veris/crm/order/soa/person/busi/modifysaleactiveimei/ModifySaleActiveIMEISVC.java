
package com.asiainfo.veris.crm.order.soa.person.busi.modifysaleactiveimei;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class ModifySaleActiveIMEISVC extends CSBizService
{
    private static final long serialVersionUID = 1L;

    public IDataset getUserSaleActiveGoodsInfos(IData userInfo) throws Exception
    {
        ModifySaleActiveIMEIBean bean = (ModifySaleActiveIMEIBean) BeanManager.createBean(ModifySaleActiveIMEIBean.class);
        return bean.getUserSaleActiveGoodsInfos(userInfo);
    }

}
