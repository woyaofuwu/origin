
package com.asiainfo.veris.crm.order.soa.person.busi.fixedtelephone.changesvcstate;

import com.asiainfo.veris.crm.order.soa.frame.bof.execute.service.OrderService;

public class FixTelChangeSvcStateRegSVC extends OrderService
{

    /**
     * 固话业务服务状态变更 固话报停：9707 固话报开：9708 固话局方停机：9734 固话局方开机：9735
     */

    private static final long serialVersionUID = 2235735578603480208L;

    @Override
    public String getOrderTypeCode() throws Exception
    {
        return this.input.getString("TRADE_TYPE_CODE", "");
    }

    @Override
    public String getTradeTypeCode() throws Exception
    {
        return this.input.getString("TRADE_TYPE_CODE", "");
    }

}
