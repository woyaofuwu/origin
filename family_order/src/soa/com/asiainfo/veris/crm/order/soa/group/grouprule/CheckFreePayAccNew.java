
package com.asiainfo.veris.crm.order.soa.group.grouprule;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeAcctInfoQry;

public class CheckFreePayAccNew extends BreBase implements IBREScript
{

    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        String err = "集团话费自由充产品需要新增一个专有账户来计费";
        String errCode = ErrorMgrUtil.getErrorCode(databus);
        String newProductId = databus.getString("PRODUCT_ID");
        String custId = databus.getString("CUST_ID");
        String acctId = databus.getString("ACCT_ID");
        if (!"9898".equals(newProductId))
        {
            return true;
        }
        IDataset result = TradeAcctInfoQry.getFreePayTradeAccountByCustId(custId,acctId);
        if (result.getData(0).getInt("COUNT") <= 0)
        {
            BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, errCode, err);
            return false;
        }
        return true;
    }

}
