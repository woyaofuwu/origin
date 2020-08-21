
package com.asiainfo.veris.crm.order.soa.person.busi.testcarduser;

import com.asiainfo.veris.crm.order.soa.frame.bof.execute.service.OrderService;

/**
 * REQ201609060001_2016年下半年测试卡功能优化（二）
 * @author zhuoyingzhi
 * 20160926
 *
 */
public class TestCardUserManageRegSVC extends OrderService
{

    @Override
    public String getOrderTypeCode() throws Exception
    {
        return "6185";
    }

    @Override
    public String getTradeTypeCode() throws Exception
    {
        return "6185";
    }

}
