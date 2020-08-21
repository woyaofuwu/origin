
package com.asiainfo.veris.crm.order.soa.person.rule.run.np;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;

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
public class CheckNpOutByActTag extends BreBase implements IBREScript
{

    @Override
    public boolean run(IData databus, BreRuleParam paramBreRuleParam) throws Exception
    {
        String actTag = databus.getString("ACT_TAG");
        if ("1".equals(actTag))
        {
            return true;
        }

        return false;
    }

}
