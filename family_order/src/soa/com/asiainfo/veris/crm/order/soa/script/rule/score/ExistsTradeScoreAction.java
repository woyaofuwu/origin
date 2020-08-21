
package com.asiainfo.veris.crm.order.soa.script.rule.score;

import java.util.Iterator;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.query.BreQryForCommparaOrTag;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;

public class ExistsTradeScoreAction extends BreBase implements IBREScript
{

    private static Logger logger = Logger.getLogger(ExistsTradeScoreAction.class);

    /**
     * 判断业务中是否办理了某项积分奖项
     */
    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        if (logger.isDebugEnabled())
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入 ExistsTradeScoreAction() >>>>>>>>>>>>>>>>>>");

        /* 自定义区域 */
        boolean bResult = false;
        IDataset listCommpara = null;

        /* 获取规则配置信息 */
        String strParamCode = ruleParam.getString(databus, "PARAM_CODE");
        String strEparchyCode = ruleParam.getString(databus, "EPARCHY_CODE");

        /* 获取业务台账，用户资料信息 */
        IDataset listTradeScore = databus.getDataset("TF_B_TRADE_SCORE");

        /* 开始逻辑规则校验 */
        for (Iterator iter = listTradeScore.iterator(); iter.hasNext();)
        {
            IData tradeScore = (IData) iter.next();

            if (listCommpara == null)
            {
                listCommpara = BreQryForCommparaOrTag.getCommpara("CSM", 2001, strParamCode, strEparchyCode);
            }

            for (Iterator iterator = listCommpara.iterator(); iterator.hasNext();)
            {
                IData commpara = (IData) iterator.next();

                if (tradeScore.getString("RULE_ID").equals(commpara.getString("PARA_CODE1")))
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

        if (logger.isDebugEnabled())
            logger.debug(" <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< 退出 ExistsTradeScoreAction() " + bResult + "<<<<<<<<<<<<<<<<<<<");

        return bResult;
    }

}
