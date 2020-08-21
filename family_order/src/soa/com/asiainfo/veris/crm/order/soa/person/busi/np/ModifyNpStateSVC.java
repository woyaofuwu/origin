
package com.asiainfo.veris.crm.order.soa.person.busi.np;

import com.ailk.common.data.IData;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class ModifyNpStateSVC extends CSBizService
{

    public IData dispatcherSoaNpWork(IData param) throws Exception
    {

        ModifyNpStateBean bean = BeanManager.createBean(ModifyNpStateBean.class);

        return bean.dispatcherSoaNpWork(param);
    }

    public void setTrans(IData param) throws Exception
    {
        param.put(Route.ROUTE_EPARCHY_CODE, param.getString("EPARCHY_CODE"));
    }
}
