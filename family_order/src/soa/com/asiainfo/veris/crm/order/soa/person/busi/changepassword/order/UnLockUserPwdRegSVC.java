
package com.asiainfo.veris.crm.order.soa.person.busi.changepassword.order;

import com.asiainfo.veris.crm.order.soa.frame.bof.execute.service.OrderService;

/**
 * 用户密码解锁77
 * 
 * @author liutt
 */
public class UnLockUserPwdRegSVC extends OrderService
{

    @Override
    public String getOrderTypeCode() throws Exception
    {
        return "77";
    }

    @Override
    public String getTradeTypeCode() throws Exception
    {
        return "77";
    }

}
