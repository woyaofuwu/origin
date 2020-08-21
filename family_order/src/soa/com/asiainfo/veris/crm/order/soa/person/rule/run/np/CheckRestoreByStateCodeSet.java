
package com.asiainfo.veris.crm.order.soa.person.rule.run.np;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;

public class CheckRestoreByStateCodeSet extends BreBase implements IBREScript
{

    @Override
    public boolean run(IData dataBus, BreRuleParam arg1) throws Exception
    {
        String userStateCodeSet = dataBus.getString("USER_STATE_CODESET");

        if (userStateCodeSet.contains("Y") || userStateCodeSet.contains("y"))
        {
            dataBus.put("ERROR_MSG", "540100-该用户服务状态存在欠费停机，不能进行携入复机！");
            return true;
        }
        if (userStateCodeSet.contains("Z") || userStateCodeSet.contains("z"))
        {
            dataBus.put("ERROR_MSG", "540101-该用户服务状态存在欠费注销，不能进行携入复机！");
            return true;
        }
        return false;
    }

}
