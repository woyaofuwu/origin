
package com.asiainfo.veris.crm.order.soa.person.rule.run.serialnumber;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserRentInfoQry;

public class CheckRentMobile extends BreBase implements IBREScript
{

    /** 描述 */

    private static final long serialVersionUID = -5609469268923189940L;

    @Override
    public boolean run(IData databus, BreRuleParam param) throws Exception
    {
        String rentTag = param.getString("RENT_TAG", "");
        String userId = databus.getString("USER_ID", "");
        IDataset result = UserRentInfoQry.queryUserRentInfo(userId);
        if (IDataUtil.isNotEmpty(result))
        {
            if ("1".equals(rentTag))
            {
                String rentSerialNumber = result.getData(0).getString("RENT_SERIAL_NUMBER");
                String rentPhone = result.getData(0).getString("PARA_CODE2");
                databus.put("strSerialNumber", rentSerialNumber);
                databus.put("strRentPhone", rentPhone);
                return true;
            }
        }
        return false;
    }

}
