
package com.asiainfo.veris.crm.order.soa.person.rule.run.serialnumber;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;

public class CheckIsFixTelephone extends BreBase implements IBREScript
{

    @Override
    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        String netTypeCode = databus.getString("NET_TYPE_CODE", "00");

        if (!"12".equals(netTypeCode))
        {
            databus.put("strSerialNumber", databus.getString("SERIAL_NUMBER"));
            return true;
        }
        return false;
    }

}
