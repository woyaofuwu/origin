
package com.asiainfo.veris.crm.order.soa.person.rule.run.np;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.CreditCall;

/**
 * Copyright: Copyright (c) 2014 Asiainfo-Linkage
 * 
 * @ClassName: CheckNpOutByOweFeeStop.java
 * @Description: 43--携出欠停（携入方落地）
 * @version: v1.0.0
 * @author: lijm3
 * @date: 2014-6-26 下午7:54:33 Modification History: Date Author Version Description
 *        ---------------------------------------------------------* 2014-6-26 lijm3 v1.0.0 修改原因
 */
public class CheckNpOutByNoDeal extends BreBase implements IBREScript
{

    @Override
    public boolean run(IData databus, BreRuleParam paramBreRuleParam) throws Exception
    {
        String userId = databus.getString("USER_ID");
        IDataset noDealUserDataset = CreditCall.getIsNoDealUser(userId, "");
        String actTag = "0";
        if (IDataUtil.isNotEmpty(noDealUserDataset))
        {
            actTag = noDealUserDataset.getData(0).getString("ACT_TAG");
        }
        if (StringUtils.equals("1", actTag))
        {
            return true;
        }

        return false;
    }
}
