
package com.asiainfo.veris.crm.order.soa.person.busi.getsaleactivegift;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class GetSaleActiveGiftSVC extends CSBizService
{
    protected static Logger log = Logger.getLogger(GetSaleActiveGiftSVC.class);

    private static final long serialVersionUID = 1L;

    public IDataset getGiftInfos(IData userInfo) throws Exception
    {
        GetSaleActiveGiftBean bean = BeanManager.createBean(GetSaleActiveGiftBean.class);
        return bean.getGiftInfos(userInfo);
    }
}
