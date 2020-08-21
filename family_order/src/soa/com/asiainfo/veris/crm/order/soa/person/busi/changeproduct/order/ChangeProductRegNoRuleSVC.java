package com.asiainfo.veris.crm.order.soa.person.busi.changeproduct.order;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.service.OrderService;

/**    
 * Copyright: Copyright  2014 Asiainfo-Linkage
 * 
 * @ClassName: ChangeProductRegNoRuleSVC.java
 * @Description: 产品变更无规则校验登记服务类
 *
 * @version: v1.0.0
 * @author: maoke
 * @date: Oct 2, 2014 10:34:15 AM 
 *
 * Modification History:
 * Date            Author      Version        Description
 *-------------------------------------------------------*
 * Oct 2, 2014    maoke       v1.0.0           修改原因	
 */
public class ChangeProductRegNoRuleSVC extends OrderService
{
    @Override
    public String getOrderTypeCode() throws Exception
    {
        return input.getString("ORDER_TYPE_CODE", input.getString("TRADE_TYPE_CODE", "110"));
    }

    @Override
    public String getTradeTypeCode() throws Exception
    {
        return input.getString("TRADE_TYPE_CODE", "110");
    }
    
    /**
     * 重写不校验规则
     */
    public void checkAfterRule(IData tableData, BusiTradeData btd) throws Exception
    {
        
    }
}
