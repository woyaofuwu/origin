
package com.asiainfo.veris.crm.order.soa.script.rule.res;

import java.util.Iterator;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;

public class ExistUserResChanged extends BreBase implements IBREScript
{

    private static Logger logger = Logger.getLogger(ExistUserResChanged.class);

    /**
     * 检查当前业务是否改变了用户资源(监控用)
     */
    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        if (logger.isDebugEnabled())
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入 ExistUserResChanged() >>>>>>>>>>>>>>>>>>");

        /* 自定义区域 */
        boolean bResult = false;

        /* 获取规则配置信息 */
        String strModifyTag = ruleParam.getString(databus, "MODIFY_TAG");
        String strResTypeCode = ruleParam.getString(databus, "RES_TYPE_CODE");

        /* 获取业务台账，用户资料信息 */
        IDataset listTradeRes = databus.getDataset("TF_B_TRADE_RES");
        IDataset listUserRes = databus.getDataset("TF_F_USER_RES");

        if (logger.isDebugEnabled())
            logger.debug("strModifyTag   = [" + strModifyTag + "]");
        if (logger.isDebugEnabled())
            logger.debug("strResTypeCode = [" + strResTypeCode + "]");
        if (logger.isDebugEnabled())
            logger.debug("listUserRes.size() = [" + listUserRes.size() + "]");

        /* 开始逻辑规则校验 */
        for (Iterator iter = listTradeRes.iterator(); iter.hasNext();)
        {
            IData TradeRes = (IData) iter.next();

            if (logger.isDebugEnabled())
                logger.debug(" tradeRes = " + TradeRes);

            if (strModifyTag.equals(TradeRes.getString("MODIFY_TAG")) && strResTypeCode.equals(TradeRes.getString("RES_TYPE_CODE")))
            {
                if (logger.isDebugEnabled())
                    logger.debug(" 需要进入user_res 的循环 ");

                for (Iterator iterator = listUserRes.iterator(); iterator.hasNext();)
                {

                    if (logger.isDebugEnabled())
                        logger.debug(" 需要进入到 user_res 的循环 ");

                    IData userRes = (IData) iterator.next();

                    if (logger.isDebugEnabled())
                        logger.debug(" userRes = " + userRes);

                    if (strResTypeCode.equals(userRes.getString("RES_TYPE_CODE")) && TradeRes.getString("RES_CODE").equals(userRes.getString("RES_CODE")))
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
            logger.debug(" <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< 退出 ExistUserResChanged() " + bResult + "<<<<<<<<<<<<<<<<<<<");

        return bResult;
    }

}
