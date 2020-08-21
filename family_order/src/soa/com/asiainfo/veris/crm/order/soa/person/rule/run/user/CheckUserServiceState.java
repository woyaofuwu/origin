
package com.asiainfo.veris.crm.order.soa.person.rule.run.user;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;

public class CheckUserServiceState extends BreBase implements IBREScript
{
    // 校验用户手机状态
    public boolean run(IData databus, BreRuleParam paramData) throws Exception
    {

        String serialNumber = databus.getString("SERIAL_NUMBER");
        String valideFlag = databus.getString("VALID_FLAG", "");
        if (!"1".equals(valideFlag))
        {
            if (serialNumber.substring(0, 3).equals("KD_"))
            {
                serialNumber = serialNumber.substring(3);
            }
            IData userInfo = UcaInfoQry.qryUserInfoBySn(serialNumber);
            if (IDataUtil.isNotEmpty(userInfo))
            {
                if (!userInfo.getString("USER_STATE_CODESET").equals("0"))
                {
                    return true;
                }
            }
            else
            {
                return true;
            }
        }

        return false;
    }

}
