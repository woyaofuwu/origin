
package com.asiainfo.veris.crm.order.soa.script.rule.score;

import java.util.Iterator;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;

public class ExistsExchangetypeScore extends BreBase implements IBREScript
{

    private static Logger logger = Logger.getLogger(ExistsExchangetypeScore.class);

    /**
     * 是否办理了某种积分兑奖动作或类型
     */
    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        if (logger.isDebugEnabled())
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入 ExistsExchangetypeScore() >>>>>>>>>>>>>>>>>>");

        /* 自定义区域 */
        boolean bResult = false;

        /* 获取规则配置信息 */
        String strActiveCode = ruleParam.getString(databus, "ACTION_CODE");
        String strEparchyCode = ruleParam.getString(databus, "EPARCHY_CODE");
        String strExchangeTypeCode = ruleParam.getString(databus, "EXCHANGE_TYPE_CODE");

        /* 获取业务台账，用户资料信息 */
        IDataset listTradeScore = databus.getDataset("TF_B_TRADE_SCORESUB");

        /* 开始逻辑规则校验 */
        for (Iterator iter = listTradeScore.iterator(); iter.hasNext();)
        {
            IData tradeScore = (IData) iter.next();

            if (null == strActiveCode || strActiveCode.equals(tradeScore.getString("ACTION_CODE")))
            {
                IData param = new DataMap();
                param.put("ACTION_CODE", tradeScore.getString("ACTION_CODE"));

                IDataset listScoreAction = Dao.qryByCode("TD_B_SCORE_ACTION", "SEL_BY_PK", param);

                for (Iterator iterator = listScoreAction.iterator(); iterator.hasNext();)
                {
                    IData action = (IData) iterator.next();

                    if (strEparchyCode.equals(action.getString("EPARCHY_CODE")) && (strExchangeTypeCode == null || strExchangeTypeCode.equals(action.getString("EXCHANGE_TYPE_CODE"))))
                    {
                        bResult = true;
                        break;
                    }
                }

                if (bResult)
                {
                    break;
                }
            }
        }

        if (logger.isDebugEnabled())
            logger.debug(" <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< 退出 ExistsExchangetypeScore() " + bResult + "<<<<<<<<<<<<<<<<<<<");

        return bResult;
    }

}
