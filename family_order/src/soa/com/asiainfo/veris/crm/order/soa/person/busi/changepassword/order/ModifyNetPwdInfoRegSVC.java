
package com.asiainfo.veris.crm.order.soa.person.busi.changepassword.order;

import com.asiainfo.veris.crm.order.soa.frame.bof.execute.service.OrderService;

/**
 * 互联网密码管理78
 * 
 * @author liutt
 */
public class ModifyNetPwdInfoRegSVC extends OrderService
{

    @Override
    public String getOrderTypeCode() throws Exception
    {
        return "78";
    }

    @Override
    public String getTradeTypeCode() throws Exception
    {
        return "78";
    }

}
