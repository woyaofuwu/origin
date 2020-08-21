
package com.asiainfo.veris.crm.order.soa.person.busi.ziyoubusiness;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class ZiYouBusiDynamicPageSVC extends CSBizService
{
    public IDataset initPageElement(IData input) throws Exception {
        ZiYouBusiDynamicPageBean ziYouBusiDynamicPageBean = BeanManager.createBean(ZiYouBusiDynamicPageBean.class);
        return ziYouBusiDynamicPageBean.initPageElement(input);
    }

}
