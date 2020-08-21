
package com.asiainfo.veris.crm.order.soa.person.rule.run.user;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;

/**
 * 用户激活状态判断
 * 
 * @author chenzm
 * @date 2014-05-23
 */
public class CheckActivateFlagUser extends BreBase implements IBREScript
{

    private static final long serialVersionUID = 1L;

    /**
     * 用户激活状态判断
     */
    public boolean run(IData databus, BreRuleParam paramData) throws Exception
    {

        String serialNumber = databus.getString("SERIAL_NUMBER");
        IData userInfo = UcaInfoQry.qryUserInfoBySn(serialNumber);
        if (IDataUtil.isNotEmpty(userInfo))
        {
            String acctTag = userInfo.getString("ACCT_TAG");
            if (!"0".equals(acctTag))
            {
                return true;
            }

        }

        return false;
    }

}
