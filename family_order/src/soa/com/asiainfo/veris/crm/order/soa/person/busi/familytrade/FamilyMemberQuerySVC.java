
package com.asiainfo.veris.crm.order.soa.person.busi.familytrade;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class FamilyMemberQuerySVC extends CSBizService
{

    public IDataset queryFamilyMeb(IData input) throws Exception
    {
        FamilyMemberQueryBean bean = BeanManager.createBean(FamilyMemberQueryBean.class);
        return bean.queryFamilyMeb(input);
    }

}
