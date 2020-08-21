
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.trade.out;

import com.ailk.biz.BizEnv;
import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.tradectrl.TradeCtrl;

public class TradeEntry
{
    public static void entry(IData mainTrade) throws Exception
    {
        String tradeTypeCode = mainTrade.getString("TRADE_TYPE_CODE");

        // 是否调用客管接口
        Boolean entry = TradeCtrl.getCtrlBoolean(tradeTypeCode, TradeCtrl.CTRL_TYPE.TRADE_ENTRY, false);

        if (entry == false)
        {
            return;
        }
        
        entry = BizEnv.getEnvBoolean("CM.BusiEntry", true);  // 开发阶段的总开关，默认调客管的存储过程
        
        if (entry == false)
        {
            return;
        }

        CSAppCall.call("CM.BusiEntry", mainTrade);
    }
}
