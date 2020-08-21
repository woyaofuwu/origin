
package com.asiainfo.veris.crm.order.soa.script.rule.common;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;

public class ExistIMEASystem extends BreBase implements IBREScript
{

    private static Logger logger = Logger.getLogger(ExistIMEASystem.class);

    /**
     * 判断客户名称是否包涵特殊字符
     */
    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        if (logger.isDebugEnabled())
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入 ExistIMEASystem() >>>>>>>>>>>>>>>>>>");

        /* 自定义区域 */
        boolean bResult = false;

        /* 获取规则配置信息 */
        String strIsMaesTag = ruleParam.getString(databus, "SUBSYS_TAG");

        /* 获取业务台账，用户资料信息 */
        String strIsMaes = databus.getString("IS_MAES");

        /* 开始逻辑规则校验 */
        if (strIsMaesTag.equals(strIsMaes))
        {
            bResult = true;
        }

        if (logger.isDebugEnabled())
            logger.debug(" <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< 退出 ExistIMEASystem() " + bResult + "<<<<<<<<<<<<<<<<<<<");

        return bResult;
    }

}
