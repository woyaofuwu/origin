
package com.asiainfo.veris.crm.order.soa.script.rule.fee;

import java.util.Iterator;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;

public class ExistsTradeGiftFee extends BreBase implements IBREScript
{

    private static Logger logger = Logger.getLogger(ExistsTradeGiftFee.class);

    /**
     * 判断该笔台账是否有某类型营销活动赠送费用是否等于XX
     */
    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        if (logger.isDebugEnabled())
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入 ExistsTradeGiftFee() >>>>>>>>>>>>>>>>>>");

        /* 自定义区域 */
        boolean bResult = false;

        /* 获取规则配置信息 */
        String strRsrvStr1 = ruleParam.getString(databus, "RSRV_STR1");
        String strRsrvStr2 = ruleParam.getString(databus, "RSRV_STR2");
        int iFee = ruleParam.getInt(databus, "FEE");

        /* 获取业务台账，用户资料信息 */
        IDataset listTrade = databus.getDataset("TF_B_TRADE");
        IDataset listTradeGiftFee = databus.getDataset("TF_B_TRADEFEE_GIFTFEE");

        /* 开始逻辑规则校验 */
        for (Iterator iter = listTrade.iterator(); iter.hasNext();)
        {
            IData trade = (IData) iter.next();

            if (strRsrvStr1.equals(trade.getString("RSRV_STR7")) && strRsrvStr2.equals(trade.getString("RSRV_STR4")))
            {
                for (Iterator iterator = listTradeGiftFee.iterator(); iterator.hasNext();)
                {
                    IData giftFee = (IData) iterator.next();

                    if (trade.getString("TRADE_ID").equals(giftFee.getString("TRADE_ID")) && giftFee.getInt("FEE") == iFee)
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
            logger.debug(" <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< 退出 ExistsTradeGiftFee() " + bResult + "<<<<<<<<<<<<<<<<<<<");

        return bResult;
    }

}
