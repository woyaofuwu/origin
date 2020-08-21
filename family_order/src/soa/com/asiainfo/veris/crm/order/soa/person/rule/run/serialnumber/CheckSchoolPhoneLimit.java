
package com.asiainfo.veris.crm.order.soa.person.rule.run.serialnumber;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;

public class CheckSchoolPhoneLimit extends BreBase implements IBREScript
{

    // 校园宽带开户157号段的号码不允许办理
    public boolean run(IData databus, BreRuleParam param) throws Exception
    {
        String serialNumber = databus.getString("SERIAL_NUMBER");
        if ("157".equals(serialNumber.substring(0, 3)))
        {
            return true;
        }
        return false;
    }

}
