
package com.asiainfo.veris.crm.order.soa.script.rule.other;

import java.util.Iterator;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;

public class ExistsLJXYTradeOtherValue extends BreBase implements IBREScript
{

    private static Logger logger = Logger.getLogger(ExistsLJXYTradeOtherValue.class);

    /**
     * 根据[RSRV_VALUE_CODE | RSRV_STR1 | RSRV_STR2 | RSRV_STR3 | RSRV_STR4 | RSRV_STR5]判断用户该笔业务是否操作了Other表信息
     */
    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        if (logger.isDebugEnabled())
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入 ExistsLJXYTradeOtherValue() >>>>>>>>>>>>>>>>>>");

        /* 自定义区域 */
        boolean bResult = false;

        /* 获取规则配置信息 */
        String strModifyTag = ruleParam.getString(databus, "MODIFY_TAG");
        String strRsrvStr1 = ruleParam.getString(databus, "RSRV_STR1");
        String strRsrvStr2 = ruleParam.getString(databus, "RSRV_STR2");
        String strRsrvStr3 = ruleParam.getString(databus, "RSRV_STR3");
        String strRsrvStr4 = ruleParam.getString(databus, "RSRV_STR4");
        String strRsrvStr5 = ruleParam.getString(databus, "RSRV_STR5");
        String strRsrvValueCode = ruleParam.getString(databus, "RSRV_VALUE_CODE");

        /* 获取业务台账，用户资料信息 */
        IDataset listTradeOther = databus.getDataset("TF_B_TRADE_OTHER");

        /* 开始逻辑规则校验 */
        for (Iterator iter = listTradeOther.iterator(); iter.hasNext();)
        {
            IData tradeOther = (IData) iter.next();
            if (strModifyTag.equals(tradeOther.getString("MODIFY_TAG")) && strRsrvValueCode.equals(tradeOther.getString("RSRV_VALUE_CODE")) && (strRsrvStr1.equals(tradeOther.getString("RSRV_STR1")) || "".equals(strRsrvStr1))
                    && (strRsrvStr2.equals(tradeOther.getString("RSRV_STR2")) || "".equals(strRsrvStr2)) && (strRsrvStr3.equals(tradeOther.getString("RSRV_STR3")) || "".equals(strRsrvStr3))
                    && (strRsrvStr4.equals(tradeOther.getString("RSRV_STR4")) || "".equals(strRsrvStr4)) && (strRsrvStr5.equals(tradeOther.getString("RSRV_STR5")) || "".equals(strRsrvStr5)))
            {
                bResult = true;
                break;
            }
        }

        if (logger.isDebugEnabled())
            logger.debug(" <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< 退出 ExistsLJXYTradeOtherValue() " + bResult + "<<<<<<<<<<<<<<<<<<<");

        return bResult;
    }

}
