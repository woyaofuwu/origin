
package com.asiainfo.veris.crm.order.soa.script.rule.common;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;

public class SplCheckBySql extends BreBase implements IBREScript
{

    private static Logger logger = Logger.getLogger(SplCheckBySql.class);

    /**
     * 延续老NG版本的CheckBySql
     */
    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        if (logger.isDebugEnabled())
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入 SplCheckBySql() >>>>>>>>>>>>>>>>>> ruleParam = " + ruleParam);

        /* 自定义区域 */
        boolean bResult = false;
        IData param = new DataMap();
        String strSqlRef = null;
        String strValue = null;

        /* 获取规则配置信息 */
        String[] strNames = ruleParam.getNames();
        for (int i = 0; i < strNames.length; i++)
        {
            if (logger.isDebugEnabled())
                logger.debug("  strNamestrNamestrNamestrName[" + i + "] = " + strNames[i]);

            /* 固定的sqlref名字 */
            if ("EXECUTE_SQL_REF".equals(strNames[i]))
            {
                strSqlRef = ruleParam.getString(databus, strNames[i]);
            }
            else
            {
                strValue = ruleParam.getString(databus, strNames[i]);
                if (logger.isDebugEnabled())
                    logger.debug("-Names--Names--Names = [" + strNames[i] + "];--Value--Value--Value = [" + strValue + "]");
                param.put(strNames[i], strValue);
            }
        }

        /* 获取业务台账，用户资料信息 */

        /* 开始逻辑规则校验 */
        if (!"".equals(strSqlRef))
        {
            if (logger.isDebugEnabled())
                logger.debug("SplCheckBySql SQL_REF=[" + strSqlRef + "]; RULE_PARAM=[" + param + "]");

            bResult = Dao.qryByRecordCount("TD_S_CPARAM", strSqlRef, param);
        }

        if (logger.isDebugEnabled())
            logger.debug(" <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< 退出 SplCheckBySql() " + bResult + "<<<<<<<<<<<<<<<<<<<");

        return bResult;
    }
}
