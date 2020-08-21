
package com.asiainfo.veris.crm.order.soa.script.rule.svcstate;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;

/**
 * Copyright: Copyright 2014 Asiainfo-Linkage
 * 
 * @Description: 用户状态未发生变化，请重新刷新资料办理业务！
 * @author: xiaocl
 */
public class ExistsTradeSvcstateByTradeId extends BreBase implements IBREScript
{
    private static Logger logger = Logger.getLogger(ExistsTradeSvcstateByTradeId.class);

    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        if (logger.isDebugEnabled())
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入 ExistsTradeSvcstateByTradeId() >>>>>>>>>>>>>>>>>>");

        boolean bResult = false;
        IDataset listTradeDiscnt = databus.getDataset("TF_B_TRADE_SVCSTATE");
        if (IDataUtil.isNotEmpty(listTradeDiscnt))
        {
            bResult = true;
        }
        if (logger.isDebugEnabled())
            logger.debug(" <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< 退出 ExistsTradeSvcstateByTradeId() " + bResult + "<<<<<<<<<<<<<<<<<<<");

        return bResult;
    }

}
