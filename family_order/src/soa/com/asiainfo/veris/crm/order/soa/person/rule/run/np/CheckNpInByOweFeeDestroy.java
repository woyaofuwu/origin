
package com.asiainfo.veris.crm.order.soa.person.rule.run.np;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;

/**
 * Copyright: Copyright (c) 2014 Asiainfo-Linkage
 * 
 * @ClassName: CheckNpOutByOweFeeDestroy.java
 * @Description: 47--携入欠费销号
 * @version: v1.0.0
 * @author: lijm3
 * @date: 2014-6-26 下午6:48:25 Modification History: Date Author Version Description
 *        ---------------------------------------------------------* 2014-6-26 lijm3 v1.0.0 修改原因
 */
public class CheckNpInByOweFeeDestroy extends BreBase implements IBREScript
{

    @Override
    public boolean run(IData dataBus, BreRuleParam paramBreRuleParam) throws Exception
    {
        String userId = dataBus.getString("USER_ID");

        IDataset userInfos = UserInfoQry.getUserInfoByMoveTagUserCodeSet(userId, "Y", "0", "90");
        if (IDataUtil.isEmpty(userInfos))
        {
            return true;
        }
        return false;
    }

}
