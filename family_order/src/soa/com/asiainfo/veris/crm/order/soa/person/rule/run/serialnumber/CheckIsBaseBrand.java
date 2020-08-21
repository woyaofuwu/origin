
package com.asiainfo.veris.crm.order.soa.person.rule.run.serialnumber;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;

public class CheckIsBaseBrand extends BreBase implements IBREScript
{

    @Override
    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        String brandCode = databus.getString("BRAND_CODE", "");
        if (!brandCode.equals("G001") && !brandCode.equals("G002") && !brandCode.equals("G010"))
        {
            return true;
        }
        return false;
    }

}
