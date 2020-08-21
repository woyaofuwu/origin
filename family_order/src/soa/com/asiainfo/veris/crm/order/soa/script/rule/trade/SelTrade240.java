
package com.asiainfo.veris.crm.order.soa.script.rule.trade;

import java.util.Iterator;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;

public class SelTrade240 extends BreBase implements IBREScript
{

    private static Logger logger = Logger.getLogger(SelTrade240.class);

    /**
     * 判断营销活动台账
     */
    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        if (logger.isDebugEnabled())
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入 SelTrade240() >>>>>>>>>>>>>>>>>>");

        /* 自定义区域 */
        boolean bResult = false;

        /* 获取规则配置信息 */
        String strRsrvStr9 = ruleParam.getString(databus, "RSRV_STR9");
        String strTradeTypeCode = ruleParam.getString(databus, "TRADE_TYPE_CODE");

        /* 获取业务台账，用户资料信息 */
        IDataset listTrade = databus.getDataset("TF_B_TRADE");

        /* 开始逻辑规则校验 */
        for (Iterator iter = listTrade.iterator(); iter.hasNext();)
        {
            IData trade = (IData) iter.next();

            if (strTradeTypeCode.equals(trade.getString("TRADE_TYPE_CODE")) && strRsrvStr9.equals(trade.getString("RSRV_STR9")))
            {
                bResult = true;
                break;
            }
        }

        if (logger.isDebugEnabled())
            logger.debug(" <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< 退出 SelTrade240() " + bResult + "<<<<<<<<<<<<<<<<<<<");

        return bResult;
    }

}
