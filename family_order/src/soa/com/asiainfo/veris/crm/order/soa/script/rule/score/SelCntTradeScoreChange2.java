
package com.asiainfo.veris.crm.order.soa.script.rule.score;

import java.util.Iterator;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;

public class SelCntTradeScoreChange2 extends BreBase implements IBREScript
{

    private static Logger logger = Logger.getLogger(SelCntTradeScoreChange2.class);

    /**
     * 判断用户办理积分业务后当前积分值
     */
    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        if (logger.isDebugEnabled())
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入 SelCntTradeScoreChange2() >>>>>>>>>>>>>>>>>>");

        /* 自定义区域 */
        boolean bResult = false;
        int iScoreValue = 0;

        /* 获取规则配置信息 */

        /* 获取业务台账，用户资料信息 */
        iScoreValue = databus.getDataset("TF_F_USER").getData(0).getInt("SCORE_VALUE");
        IDataset listTradeScore = databus.getDataset("TF_B_TRADE_SCORE");

        /* 开始逻辑规则校验 */
        for (Iterator iter = listTradeScore.iterator(); iter.hasNext();)
        {
            IData tradeScore = (IData) iter.next();

            iScoreValue = iScoreValue + tradeScore.getInt("SCORE_CHANGED");
        }

        if (iScoreValue > 0)
        {
            bResult = true;
        }

        if (logger.isDebugEnabled())
            logger.debug(" <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< 退出 SelCntTradeScoreChange2() " + bResult + "<<<<<<<<<<<<<<<<<<<");

        return bResult;
    }

}
