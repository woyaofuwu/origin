
package com.asiainfo.veris.crm.order.soa.person.rule.run.np;

import org.apache.log4j.Logger;

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
 * @ClassName: CheckNpTagBeforeDestroyUser.java
 * @Description: 用户办理销户前对携转用户状态进行校验
 * @version: v1.0.0
 * @author: xiaozb
 * @date: 2014-5-30 下午3:01:56
 */
public class CheckNpTagBeforeDestroyUser extends BreBase implements IBREScript
{

    private static final long serialVersionUID = 1L;

    private static Logger logger = Logger.getLogger(CheckNpTagBeforeDestroyUser.class);

    public boolean run(IData databus, BreRuleParam paramBreRuleParam) throws Exception
    {
        String userId = databus.getString("USER_ID", "");
        String serialNumber = databus.getString("SERIAL_NUMBER", "");
        String userTagSet = databus.getString("USER_TAG_SET", "");
        String npTag = "";
        if (StringUtils.isNotEmpty(userTagSet))
        {
            npTag = userTagSet.substring(0, 1);
        }
        else
        {
            npTag = userTagSet;
        }

        if ("3".equals(npTag))
        {
            String errorMsg = "该用户当前携转标识为3-携出中，不允许进行销户！";
            BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 23457, errorMsg);
            return true;
        }

        String userState = databus.getString("USER_STATE_CODESET", "");
        if (StringUtils.containsIgnoreCase(userState, "Y"))
        {
            String errorMsg = "该用户当前状态含有Y-携出方欠费停机，不允许进行销户！";
            BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 23458, errorMsg);
            return true;
        }

        return false;
    }
}
