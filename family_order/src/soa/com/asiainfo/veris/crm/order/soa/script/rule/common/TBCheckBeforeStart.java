
package com.asiainfo.veris.crm.order.soa.script.rule.common;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;

public class TBCheckBeforeStart extends BreBase implements IBREScript
{

    private static Logger logger = Logger.getLogger(TBCheckBeforeStart.class);

    /**
     * 业务前规则判断数据准备， 数据校验
     */
    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        if (logger.isDebugEnabled())
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入 TBCheckBeforeStart() >>>>>>>>>>>>>>>>>>");

        /* 自定义区域 */
        boolean bResult = false;

        /* 数据准备 */
        if (!"10".equals(databus.getString("TRADE_TYPE_CODE")))
        {
            databus.put("USER_ID", databus.getString("ID"));
            databus.get("TF_F_USER");
        }

        if (logger.isDebugEnabled())
            logger.debug(" <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< 退出 TBCheckBeforeStart() " + bResult + "<<<<<<<<<<<<<<<<<<<");

        return bResult;
    }

}
