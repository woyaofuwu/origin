
package com.asiainfo.veris.crm.order.soa.person.busi.familytrade;

import com.ailk.common.data.IData;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class FamilyShortCodeBusiSVC extends CSBizService
{

    public IData familyShortCodeBatDeal(IData input) throws Exception
    {
        FamilyShortCodeBusiBean bean = BeanManager.createBean(FamilyShortCodeBusiBean.class);
        return bean.familyShortCodeBatDeal(input);
    }

}
