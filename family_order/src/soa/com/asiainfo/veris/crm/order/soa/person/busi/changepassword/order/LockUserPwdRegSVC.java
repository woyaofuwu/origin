
package com.asiainfo.veris.crm.order.soa.person.busi.changepassword.order;

import com.asiainfo.veris.crm.order.soa.frame.bof.execute.service.OrderService;

/**
 * 用户密码锁定76
 * 
 * @author liutt
 */
public class LockUserPwdRegSVC extends OrderService
{

    @Override
    public String getOrderTypeCode() throws Exception
    {
        return "76";
    }

    @Override
    public String getTradeTypeCode() throws Exception
    {
        return "76";
    }

}
