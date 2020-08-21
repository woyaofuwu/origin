
package com.asiainfo.veris.crm.order.soa.frame.bre.script;

import org.apache.log4j.Logger;

import com.ailk.biz.BizEnv;
import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;

public class ClazzError implements IBREScript
{
    private static Logger logger = Logger.getLogger(ClazzError.class);

    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        if (logger.isDebugEnabled() || BizEnv.getEnvBoolean("bre.isDebugEnabled"))
        {
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入 ClazzError()");
            logger.debug(" <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< 退出 ClazzError()");
        }

        return false;
    }

}
