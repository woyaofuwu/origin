
package com.asiainfo.veris.crm.order.web.group.rules;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSBizHttpHandler;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;

public class PayRelaAdvChgRule extends CSBizHttpHandler
{
    public void checkRules() throws Exception
    {
        IData conParams = getData();
        // 校验成员规则,必须参数,成员SERIAL_NUMBER,userId,acctId,集团custId
        String sn = conParams.getString("SERIAL_NUMBER");
        String userId = conParams.getString("USER_ID");
        String acctId = conParams.getString("ACCT_ID");
        String grpCustId = conParams.getString("GRP_CUST_ID");
        String grpAcctId = conParams.getString("GRP_ACCT_ID");
        IData checkCondition = new DataMap();

        checkCondition.put("SERIAL_NUMBER", sn);
        checkCondition.put("USER_ID", userId);
        checkCondition.put("ACCT_ID", acctId);
        checkCondition.put("CUST_ID", grpCustId); // 集团custId
        checkCondition.put("GRP_ACCT_ID", grpAcctId); // 集团acctId
        checkCondition.put("TRADE_TYPE_CODE", "3601");

        checkCondition.put("CHK_FLAG", "PayrelaAdvChg");

        IDataset ruleResults = CSViewCall.call(this, "SS.chkPayRelaAdvChg", checkCondition);

        if (IDataUtil.isEmpty(ruleResults))
            return;

        IData ruleResult = ruleResults.getData(0);

        if (IDataUtil.isEmpty(ruleResult))
            return;

        this.setAjax(ruleResult);
    }
}
