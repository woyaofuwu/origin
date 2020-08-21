
package com.asiainfo.veris.crm.order.soa.person.busi.np.mobilenoreturn.order.trade;

import com.asiainfo.veris.crm.order.soa.frame.bof.execute.service.OrderService;

/**
 * Copyright: Copyright (c) 2014 Asiainfo-Linkage
 * 
 * @ClassName: MobileNoReturnRegSVC.java
 * @Description: 号码归还登记服务 对应TCS_MobileNoReturn
 * @version: v1.0.0
 * @author: lijm3
 * @date: 2014-5-6 下午4:17:49 Modification History: Date Author Version Description
 *        ---------------------------------------------------------* 2014-5-6 lijm3 v1.0.0 修改原因
 */
public class MobileNoReturnRegSVC extends OrderService
{

    @Override
    public String getOrderTypeCode() throws Exception
    {
        return "189";
    }

    @Override
    public String getTradeTypeCode() throws Exception
    {
        return "189";
    }

}
