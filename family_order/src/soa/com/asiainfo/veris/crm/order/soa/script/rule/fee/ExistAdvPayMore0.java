
package com.asiainfo.veris.crm.order.soa.script.rule.fee;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;

public class ExistAdvPayMore0 extends BreBase implements IBREScript
{

    private static Logger logger = Logger.getLogger(ExistAdvPayMore0.class);

    /**
     * 获取预存大于0的台帐记录
     */
    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        if (logger.isDebugEnabled())
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入 ExistAdvPayMore0() >>>>>>>>>>>>>>>>>>");

        boolean bResult = false;

        /* 获取规则配置信息 */
        int iFee = ruleParam.getInt(databus, "FEE");

        /* 获取业务台账，用户资料信息 */
        IDataset listTradeMain = databus.getDataset("TF_B_TRADE");

        /* 开始逻辑规则校验 */
        int iCountTradeMain = listTradeMain.size();
        for (int iTradeMain = 0; iTradeMain < iCountTradeMain; iTradeMain++)
        {
            if (listTradeMain.get(iTradeMain, "CANCEL_TAG").equals("0") && Integer.parseInt((String) listTradeMain.get(iTradeMain, "ADVANCE_PAY")) > 0)
            {
                if (logger.isDebugEnabled())
                    logger.debug(" <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< 退出 ExistAdvPayMore0() " + true + "<<<<<<<<<<<<<<<<<<<");
                return true;
            }
        }

        if (logger.isDebugEnabled())
            logger.debug(" <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< 退出 ExistAdvPayMore0() " + bResult + "<<<<<<<<<<<<<<<<<<<");

        return bResult;
    }

}
