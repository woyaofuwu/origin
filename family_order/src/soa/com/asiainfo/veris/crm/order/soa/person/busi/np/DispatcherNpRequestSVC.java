
package com.asiainfo.veris.crm.order.soa.person.busi.np;

import com.ailk.common.data.IData;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

/**
 * Copyright: Copyright (c) 2014 Asiainfo-Linkage
 * 
 * @ClassName: DispatcherNpRequest.java
 * @Description: 对应老系统TCS_CreateNpTradeRequest
 * @version: v1.0.0
 * @author: lijm3
 * @date: 2014-5-17 上午10:26:44 Modification History: Date Author Version Description
 *        ---------------------------------------------------------* 2014-5-17 lijm3 v1.0.0 修改原因
 */
public class DispatcherNpRequestSVC extends CSBizService
{

    public IData dispatcherNpRequest(IData param) throws Exception
    {

        DispatcherNpRequestBean bean = BeanManager.createBean(DispatcherNpRequestBean.class);

        return bean.dispatcherNpRequest(param);
    }

    public void setTrans(IData input) throws Exception
    {
        input.put(Route.ROUTE_EPARCHY_CODE, input.getString("EPARCHY_CODE"));
    }

}
