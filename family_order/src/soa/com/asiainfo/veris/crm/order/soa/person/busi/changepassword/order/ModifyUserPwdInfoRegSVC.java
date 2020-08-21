
package com.asiainfo.veris.crm.order.soa.person.busi.changepassword.order;

import com.asiainfo.veris.crm.order.soa.frame.bof.execute.service.OrderService;

/**
 * 用户密码修改71、用户密码重置73
 * 
 * @author liutt
 */
public class ModifyUserPwdInfoRegSVC extends OrderService
{

    @Override
    public String getOrderTypeCode() throws Exception
    {
        return this.input.getString("TRADE_TYPE_CODE");
    }

    @Override
    public String getTradeTypeCode() throws Exception
    {
        return this.input.getString("TRADE_TYPE_CODE");
    }

}
