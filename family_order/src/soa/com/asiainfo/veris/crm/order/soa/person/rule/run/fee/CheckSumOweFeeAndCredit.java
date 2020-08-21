
package com.asiainfo.veris.crm.order.soa.person.rule.run.fee;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.AcctCall;

/**
 * Copyright: Copyright (c) 2014 Asiainfo-Linkage
 * 
 * @ClassName: CheckSumOweFeeAndCredit.java
 * @Description: 校验用户是否欠费，用户的信用度可以抵消欠费
 * @version: v1.0.0
 * @author: xiaozb
 * @date: 2014-5-19 下午4:22:20
 */
public class CheckSumOweFeeAndCredit extends BreBase implements IBREScript
{

    private static final long serialVersionUID = 1L;

    @Override
    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        String userId = databus.getString("USER_ID");
        IDataset creditDataset = AcctCall.getUserCreditInfos("0", userId);
        double creditValue = 0.0;
        if (IDataUtil.isNotEmpty(creditDataset))
        {
            creditValue = Long.parseLong(creditDataset.getData(0).getString("CREDIT_VALUE", "0"));
        }
        // 取databus里面用户的欠费
        double oweFee = Double.parseDouble(databus.getString("FEE", "0"));
        double sum = creditValue + oweFee;
        if (sum < 0)
        {
            String errorInfo = "对不起，您有费用尚未结清，请您结清后再办理报开！";
            BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 515207, errorInfo);
            return true;
        }
        return false;
    }
}
