
package com.asiainfo.veris.crm.order.soa.person.rule.run.serialnumber;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.consts.PersonConst;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaUUInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserTelephoeInfoQry;

public class CheckFixedTelephone extends BreBase implements IBREScript
{

    private IDataset queryUserRelaUU(IData databus) throws Exception
    {
        String serialNumber = databus.getString("SERIAL_NUMBER");
        IDataset userInfo = UserInfoQry.getUserInfoBySn(serialNumber, "0", PersonConst.FIX_TEL_NET_TYPE_CODE);
        if (IDataUtil.isEmpty(userInfo))
        {
            return userInfo;
        }

        String userIdB = userInfo.getData(0).getString("USER_ID");
        return RelaUUInfoQry.getRelaUUInfoByUserRelarIdB(userIdB, "T2", null);
    }

    @Override
    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        IDataset relaUU = queryUserRelaUU(databus);
        if (IDataUtil.isEmpty(relaUU))
        {
            IDataset fixTel = UserTelephoeInfoQry.getUserTelephoneByUserId(databus.getString("USER_ID"));
            if (IDataUtil.isEmpty(fixTel))
            {
                return true;
            }
        }
        return false;
    }

}
