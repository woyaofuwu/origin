
package com.asiainfo.veris.crm.order.soa.script.rule.discnt;

import java.util.Iterator;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;

public class ExistTradeAndUserDiscnt extends BreBase implements IBREScript
{

    private static Logger logger = Logger.getLogger(ExistTradeAndUserDiscnt.class);

    /**
     * 检查用户优惠和台账优惠
     */
    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        if (logger.isDebugEnabled())
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入 ExistTradeAndUserDiscnt() >>>>>>>>>>>>>>>>>>");

        /* 自定义区域 */
        boolean bResult = false;

        /* 获取规则配置信息 */
        String strModifyTag = ruleParam.getString(databus, "MODIFY_TAG");
        String strDiscntCodeA = ruleParam.getString(databus, "DISCNT_CODE_A");
        String strDiscntCodeB = ruleParam.getString(databus, "DISCNT_CODE_B");

        /* 获取业务台账，用户资料信息 */
        IDataset listTradeDiscnt = databus.getDataset("TF_B_TRADE_DISCNT");
        IDataset listUserDiscnt = databus.getDataset("TF_F_USER_DISCNT");

        /* 开始逻辑规则校验 */
        for (Iterator iter = listTradeDiscnt.iterator(); iter.hasNext();)
        {
            IData tradeDiscnt = (IData) iter.next();

            if (strModifyTag.equals(tradeDiscnt.getString("MODIFY_TAG")) && strDiscntCodeA.equals(tradeDiscnt.getString("DISCNT_CODE")))
            {
                for (Iterator iterator = listUserDiscnt.iterator(); iterator.hasNext();)
                {
                    IData userDiscnt = (IData) iterator.next();

                    if (tradeDiscnt.getString("USER_ID").equals(userDiscnt.getString("USER_ID")) && strDiscntCodeB.equals(userDiscnt.getString("DISCNT_CODE")))
                    {
                        bResult = true;
                        break;
                    }
                }
            }

            if (bResult)
            {
                break;
            }
        }

        if (logger.isDebugEnabled())
            logger.debug(" <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< 退出 ExistTradeAndUserDiscnt() " + bResult + "<<<<<<<<<<<<<<<<<<<");

        return bResult;
    }

}
