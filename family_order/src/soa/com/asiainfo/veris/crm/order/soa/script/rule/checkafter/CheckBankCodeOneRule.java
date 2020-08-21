
package com.asiainfo.veris.crm.order.soa.script.rule.checkafter;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;

/**
 * Copyright: Copyright 2014 Asiainfo-Linkage
 * 
 * @Description: 业务登记后条件判断:非现金付费方式不能选择现金银行!"【TradeCheckAfter】
 * @author: xiaocl
 */
public class CheckBankCodeOneRule extends BreBase implements IBREScript
{
    private static Logger logger = Logger.getLogger(CheckBankCodeOneRule.class);

    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        if (logger.isDebugEnabled())
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入 CheckBankCodeRule() >>>>>>>>>>>>>>>>>>");

        /* 逻辑单元节点定义区域 */
        boolean bResult = false;

        /* 总线相关信息包括台账信息资料信息获取 区域 */
        String strPayModeCode = databus.getDataset("TF_B_TRADE_ACCOUNT").first().getString("PAY_MODE_CODE", "");
        String strBankCode = databus.getDataset("TF_B_TRADE_ACCOUNT").first().getString("BANK_CODE", "");
        /*
         * 分部判断区域
         */
        // 第一逻辑单元
        if (strPayModeCode != "0" && strPayModeCode != "5")
        {
            if (strBankCode.length() >= 2)
            {
                if (strBankCode.substring(strBankCode.length() - 2, 2) == "XJ")
                {
                    bResult = true;
                }
            }
        }

        if (logger.isDebugEnabled())
            logger.debug(" <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< 退出 CheckBankCodeOneRule() " + bResult + "<<<<<<<<<<<<<<<<<<<");

        return bResult;
    }

}
