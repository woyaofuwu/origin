
package com.asiainfo.veris.crm.order.soa.person.busi.intf;

import com.ailk.common.data.IData;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class CrmAMInfoSVC extends CSBizService
{

    public IData queryCrmInfoList(IData inparam) throws Exception
    {
    	CrmAMInfoBean uipBean = BeanManager.createBean(CrmAMInfoBean.class);
        IData result = uipBean.queryCrmInfoList(inparam);
        return result;
    }
    
    public IData syncCrmInfoList(IData inparam) throws Exception
    {
    	CrmAMInfoBean uipBean = BeanManager.createBean(CrmAMInfoBean.class);
        IData result = uipBean.syncCrmInfoList(inparam);
        return result;
    }
    
   
}
