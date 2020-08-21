
package com.asiainfo.veris.crm.order.soa.person.rule.run.saleactive;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.AcctCall;

//业务受理前条件判断: 用户已经欠费不能办理营销活动!  69900908\69900909

public class CheckOweFeeForSaleActive extends BreBase implements IBREScript
{

    private static final long serialVersionUID = 1L;

    public boolean run(IData databus, BreRuleParam paramData) throws Exception
    {
        boolean returnFlag = false;
        String acctBalance = "0"; // 实时结余
        String userId = databus.getString("USER_ID");
        String productId = databus.getString("PRODUCT_ID");
        IData oweFeeData = AcctCall.getOweFeeByUserId(userId);
        IDataset result = AcctCall.getUserCreditInfos("0", userId);
        String creditValue = "";
        if (!IDataUtil.isEmpty(result) && result.size()>0)
        { 
        IData creditInfo = result.getData(0);
        creditValue = creditInfo.getString("CREDIT_VALUE"); 
        }

        acctBalance = oweFeeData.getString("ACCT_BALANCE", "0");
        if (Double.parseDouble(acctBalance) + Double.parseDouble(creditValue) < 0 )
        {
            returnFlag = true ;
        }
        return returnFlag ;
    }
}
