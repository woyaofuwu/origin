
package com.asiainfo.veris.crm.order.soa.person.rule.run.np;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;

public class CheckRestoreByBrandCode extends BreBase implements IBREScript
{

    @Override
    public boolean run(IData dataBus, BreRuleParam paramBreRuleParam) throws Exception
    {
        String brandCode = dataBus.getString("BRAND_CODE");
        if (!brandCode.startsWith("G"))
        {
            dataBus.put("ERROR_MSG", "540006-不受理非个人品牌用户复机");
            return true;
        }
        else
        {
            if (brandCode.startsWith("GS0"))
            {
                dataBus.put("ERROR_MSG", "该用户为原智能网【" + brandCode + "】用户,不能办理复机");

                return true;
            }
        }

        return false;
    }

}
