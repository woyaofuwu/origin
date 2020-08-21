
package com.asiainfo.veris.crm.order.soa.person.busi.sundryquery.plat.self;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class SelfBizSVC extends CSBizService
{

    private static final long serialVersionUID = 3664907592180270227L;

    public IDataset qrySelfBizInfo(IData param) throws Exception
    {
        SelfBizBean bean = (SelfBizBean) BeanManager.createBean(SelfBizBean.class);
        return bean.qrySelfBizInfo(param, getPagination());
    }

}
