
package com.asiainfo.veris.crm.order.soa.script.rule.common;

import java.util.Calendar;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTimeUtil;

public class ExistsDateLimit extends BreBase implements IBREScript
{

    private static Logger logger = Logger.getLogger(ExistsDateLimit.class);

    /**
     * 时间判断
     */
    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        if (logger.isDebugEnabled())
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入 ExistsDateLimit() >>>>>>>>>>>>>>>>>>");

        /* 自定义区域 */
        boolean bResult = false;

        /* 获取规则配置信息 */
        String strEnd = ruleParam.getString(databus, "END_DATE");
        String strStart = ruleParam.getString(databus, "BEGIN_DATE");
        String strType = ruleParam.getString(databus, "TYPE");
        int iNum = ruleParam.getInt(databus, "NUM");

        /* 获取业务台账，用户资料信息 */

        /* 开始逻辑规则校验 */
        if ("".equals(strEnd))
        {
            strEnd = databus.getString("SYSDATE");
            if (logger.isDebugEnabled())
                logger.debug(" END_DATE = [] set END_DATE = SYSDATE = [" + strEnd + "]");
        }

        Calendar cEnd = BreTimeUtil.setCalendar(Calendar.getInstance(), strEnd);

        Calendar cStart = BreTimeUtil.setCalendar(Calendar.getInstance(), strStart, strType, iNum);

        if (cStart.after(cEnd))
        {
            return true;
        }

        if (logger.isDebugEnabled())
            logger.debug(" <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< 退出 ExistsDateLimit() " + bResult + "<<<<<<<<<<<<<<<<<<<");

        return bResult;
    }

}
