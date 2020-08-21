
package com.asiainfo.veris.crm.order.soa.person.busi.score.scoreinterface.order;

import com.asiainfo.veris.crm.order.soa.frame.bof.execute.service.OrderService;

/**
 * 新增“携出积分清零”业务//REQ201912220001_关于调整携转限制内容以及在查询携转条件和申请授权码后面追加风险短信 by mnegqx 20200221
 * @author mengqx
 *
 */
public class NPScoreCleanRegSVC extends OrderService
{

    @Override
    public String getOrderTypeCode() throws Exception
    {
        return "342";
    }

    @Override
    public String getTradeTypeCode() throws Exception
    {
        return "342";
    }

}
