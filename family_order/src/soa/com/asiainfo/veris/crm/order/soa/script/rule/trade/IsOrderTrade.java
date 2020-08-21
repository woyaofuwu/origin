
package com.asiainfo.veris.crm.order.soa.script.rule.trade;

import java.util.Iterator;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;

public class IsOrderTrade extends BreBase implements IBREScript
{

    private static Logger logger = Logger.getLogger(IsOrderTrade.class);

    /**
     * 判断是否是预约执行工单
     */
    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        if (logger.isDebugEnabled())
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入 IsOrderTrade() >>>>>>>>>>>>>>>>>>");

        /* 自定义区域 */
        boolean bResult = false;

        /* 获取规则配置信息 */

        /* 获取业务台账，用户资料信息 */
        String strFirstDayOfNextMonth = databus.getString(BreFactory.FIRST_DAY_OF_NEXT_MONTH);
        IDataset listTrade = databus.getDataset("TF_B_TRADE");

        /* 开始逻辑规则校验 */
        for (Iterator iterTrade = listTrade.iterator(); iterTrade.hasNext();)
        {
            IData trade = (IData) iterTrade.next();

            if (trade.getString("EXEC_TIME").equals(strFirstDayOfNextMonth))
            {
                bResult = true;
                break;
            }
        }

        if (logger.isDebugEnabled())
            logger.debug(" <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< 退出 IsOrderTrade() " + bResult + "<<<<<<<<<<<<<<<<<<<");

        return bResult;
    }

}
