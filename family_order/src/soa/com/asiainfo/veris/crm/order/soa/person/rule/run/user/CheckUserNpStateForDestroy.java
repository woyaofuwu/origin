
package com.asiainfo.veris.crm.order.soa.person.rule.run.user;

import com.ailk.common.data.IData;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;

/**
 * Copyright: Copyright (c) 2014 Asiainfo-Linkage
 * 
 * @ClassName: CheckUserNpStateForDestroy.java
 * @Description: 立即销户前校验用户携转状态
 * @version: v1.0.0
 * @author: xiaozb
 * @date: 2014-6-1 下午4:59:37
 */
public class CheckUserNpStateForDestroy extends BreBase implements IBREScript
{

    private static final long serialVersionUID = 1L;

    public boolean run(IData databus, BreRuleParam paramData) throws Exception
    {
        String userTagSet = databus.getString("USER_TAG_SET");
        String userState = databus.getString("USER_STATE_CODESET");

        String npTag = "";
        if (StringUtils.isNotEmpty(userTagSet))
        {
            npTag = StringUtils.substring(userTagSet, 0, 1);
        }
        else
        {
            npTag = userTagSet;
        }

        if (StringUtils.equals("3", npTag))
        {
            String msg = "该用户当前携转标识为3-携出中，不允许进行销户！";
            BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 23457, msg);
            return true;
        }

        if (StringUtils.isNotEmpty(userState) && StringUtils.containsIgnoreCase(userState, "Y"))
        {
            String msg = "该用户当前状态含有Y-携出方欠费停机，不允许进行销户！";
            BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 23458, msg);
            return true;
        }

        return false;
    }

}
