
package com.asiainfo.veris.crm.order.soa.person.rule.run.np;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeNpQry;

public class CheckNpTradeUnFinish extends BreBase implements IBREScript
{

    @Override
    public boolean run(IData dataBus, BreRuleParam paramBreRuleParam) throws Exception
    {
        String userId = dataBus.getString("USER_ID");
        String tradeTypeCode = dataBus.getString("TRADE_TYPE_CODE", "46");
        String cancelTag = dataBus.getString("CANCEL_TAG", "0");
        IDataset ids = TradeNpQry.getTradeNpsByUserIdTradeTypeCodeCancelTag(userId, tradeTypeCode, cancelTag);
        if (IDataUtil.isNotEmpty(ids))
        {
            return true;
        }
        return false;
    }

}
