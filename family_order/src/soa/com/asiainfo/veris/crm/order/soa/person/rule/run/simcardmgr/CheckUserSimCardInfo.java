
package com.asiainfo.veris.crm.order.soa.person.rule.run.simcardmgr;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserResInfoQry;

/**
 * Copyright: Copyright 2014 Asiainfo-Linkage
 * 
 * @Description:
 */
public class CheckUserSimCardInfo extends BreBase implements IBREScript
{

    @Override
    public boolean run(IData databus, BreRuleParam param) throws Exception
    {
        String xChoiceTag = databus.getString("X_CHOICE_TAG");
        String userId = databus.getString("USER_ID");
        String msg = "";
        if ("0".equals(xChoiceTag))
        {
            IDataset resInfos = UserResInfoQry.queryUserSimInfo(userId, "1");
            if (IDataUtil.isEmpty(resInfos))
            {
                msg = "用户资源信息不正常！";
                BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 201509, msg);
            }
        }
        return false;
    }

}
