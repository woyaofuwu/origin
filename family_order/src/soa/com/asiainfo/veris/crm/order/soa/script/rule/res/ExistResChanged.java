
package com.asiainfo.veris.crm.order.soa.script.rule.res;

import java.util.Iterator;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;

public class ExistResChanged extends BreBase implements IBREScript
{

    private static Logger logger = Logger.getLogger(ExistResChanged.class);

    /**
     * 查询台帐表中是否有资源被改变过(用于监控程序非法终止资源BUG)
     */
    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        if (logger.isDebugEnabled())
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入 ExistResChanged() >>>>>>>>>>>>>>>>>>");

        /* 自定义区域 */
        boolean bResult = false;

        /* 获取规则配置信息 */
        String strModifyTag = ruleParam.getString(databus, "MODIFY_TAG");

        /* 获取业务台账，用户资料信息 */
        IDataset listTradeRes = databus.getDataset("TF_B_TRADE_RES");

        /* 开始逻辑规则校验 */
        for (Iterator iter = listTradeRes.iterator(); iter.hasNext();)
        {
            IData tradeRes = (IData) iter.next();

            if ("*".equals(strModifyTag) || strModifyTag.equals(tradeRes.getString("MODIFY_TAG")))
            {
                bResult = true;
                break;
            }
        }

        if (logger.isDebugEnabled())
            logger.debug(" <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< 退出 ExistResChanged() " + bResult + "<<<<<<<<<<<<<<<<<<<");

        return bResult;
    }
}
