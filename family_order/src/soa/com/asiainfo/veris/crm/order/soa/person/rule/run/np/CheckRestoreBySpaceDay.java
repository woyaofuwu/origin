
package com.asiainfo.veris.crm.order.soa.person.rule.run.np;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserNpInfoQry;

public class CheckRestoreBySpaceDay extends BreBase implements IBREScript
{

    @Override
    public boolean run(IData dataBus, BreRuleParam arg1) throws Exception
    {
        String userId = dataBus.getString("USER_ID");
        String limitTime = "120";
        IDataset ids = UserNpInfoQry.qryUserNpInfosByUserIdLimitTime(userId, limitTime);

        if (IDataUtil.isEmpty(ids))
        {
            return true;
        }
        return false;
    }

}
