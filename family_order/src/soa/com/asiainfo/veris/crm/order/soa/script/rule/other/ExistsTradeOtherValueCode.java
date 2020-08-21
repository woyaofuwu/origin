
package com.asiainfo.veris.crm.order.soa.script.rule.other;

import java.util.Iterator;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;

public class ExistsTradeOtherValueCode extends BreBase implements IBREScript
{
    private static Logger logger = Logger.getLogger(ExistsTradeOtherValueCode.class);

    /**
     * 根据[ＲＳＲＶ＿ＶＡＬＵＥ＿ＣＯＤＥ｜ＲＳＲＶ＿ＶＡＬＵＥ]判断用户该笔业务是否操作了Other表信息
     */
    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        if (logger.isDebugEnabled())
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入 ExistsTradeOtherValueCode() >>>>>>>>>>>>>>>>>>");

        /* 自定义区域 */
        boolean bResult = false;

        /* 获取规则配置信息 */
        String strModifyTag = ruleParam.getString(databus, "MODIFY_TAG");
        String strRsrvStr2 = ruleParam.getString(databus, "RSRV_STR2");
        String strRsrvStr1 = ruleParam.getString(databus, "RSRV_STR1");
        String strRsrvValueCode = ruleParam.getString(databus, "RSRV_VALUE_CODE");

        /* 获取业务台账，用户资料信息 */
        IDataset listTradeOther = databus.getDataset("TF_B_TRADE_OTHER");

        /* 开始逻辑规则校验 */
        for (Iterator iter = listTradeOther.iterator(); iter.hasNext();)
        {
            IData tradeOther = (IData) iter.next();

            if (strModifyTag.equals(tradeOther.getString("MODIFY_TAG")) && strRsrvValueCode.equals(tradeOther.getString("RSRV_VALUE_CODE")) && ("*".equals(tradeOther.getString("RSRV_STR1")) || strRsrvStr1.equals(tradeOther.getString("RSRV_STR1")))
                    && ("*".equals(tradeOther.getString("RSRV_STR2")) || strRsrvStr2.equals(tradeOther.getString("RSRV_STR2"))))
            {
                bResult = true;
                break;
            }
        }

        if (logger.isDebugEnabled())
            logger.debug(" <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< 退出 ExistsTradeOtherValueCode() " + bResult + "<<<<<<<<<<<<<<<<<<<");

        return bResult;
    }
}
