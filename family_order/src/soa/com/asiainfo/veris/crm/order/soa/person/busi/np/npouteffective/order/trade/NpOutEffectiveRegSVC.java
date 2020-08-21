
package com.asiainfo.veris.crm.order.soa.person.busi.np.npouteffective.order.trade;

import com.asiainfo.veris.crm.order.soa.frame.bof.execute.service.OrderService;

/**
 * Copyright: Copyright (c) 2014 Asiainfo-Linkage
 * 
 * @ClassName: NpOutEffectiveRegSVC.java
 * @Description: NP用户携出生效 登记服务 对应TCS_AcceptApplyDestroyUserReg
 * @version: v1.0.0
 * @author: lijm3
 * @date: 2014-5-7 上午9:54:34 Modification History: Date Author Version Description
 *        ---------------------------------------------------------* 2014-5-7 lijm3 v1.0.0 修改原因
 */
public class NpOutEffectiveRegSVC extends OrderService
{

    @Override
    public String getOrderTypeCode() throws Exception
    {
        return "42";
    }

    @Override
    public String getTradeTypeCode() throws Exception
    {
        return "42";
    }

}
