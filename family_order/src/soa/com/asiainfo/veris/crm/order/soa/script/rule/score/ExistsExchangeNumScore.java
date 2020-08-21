
package com.asiainfo.veris.crm.order.soa.script.rule.score;

import java.util.Iterator;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.query.BreQryForCommparaOrTag;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;

public class ExistsExchangeNumScore extends BreBase implements IBREScript
{

    private static Logger logger = Logger.getLogger(ExistsExchangeNumScore.class);

    /**
     * 自然年内只能兑换NUM次
     */
    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        if (logger.isDebugEnabled())
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入 ExistsExchangeNumScore() >>>>>>>>>>>>>>>>>>");

        /* 自定义区域 */
        boolean bResult = false;
        IData inputparam = new DataMap();
        IDataset listCommpara = null;

        /* 获取规则配置信息 */
        inputparam.put("USER_ID", ruleParam.getString(databus, "USER_ID"));
        inputparam.put("START_DATE", ruleParam.getString(databus, "START_DATE"));
        inputparam.put("FINISH_DATE", ruleParam.getString(databus, "FINISH_DATE"));
        inputparam.put("TRADE_TYPE_CODE", "330");
        inputparam.put("CANCEL_TAG", "0");
        String strParamCode = ruleParam.getString(databus, "PARAM_CODE");
        String strEparchyCode = ruleParam.getString(databus, "EPARCHY_CODE");
        int num = Integer.parseInt(ruleParam.getString(databus, "NUM"));

        /* 获取业务台账，用户资料信息 */
        IDataset listBhTradeScore = Dao.qryByCode("TF_B_TRADE_SCORE", "SEL_BHTRADE_BYUSERID", inputparam);
        listCommpara = BreQryForCommparaOrTag.getCommpara("CSM", 2001, strParamCode, strEparchyCode);

        /* 开始逻辑规则校验 */
        int i = 0;
        for (Iterator iter = listBhTradeScore.iterator(); iter.hasNext();)
        {
            IData BhTradeScore = (IData) iter.next();

            for (Iterator iterator = listCommpara.iterator(); iterator.hasNext();)
            {
                IData commpara = (IData) iterator.next();

                if (BhTradeScore.getString("RULE_ID").equals(commpara.getString("PARA_CODE1")))
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

        if (bResult)
        {
            bResult = false;
            IDataset listTradeScore = databus.getDataset("TF_B_TRADE_SCORE");
            for (Iterator iter = listTradeScore.iterator(); iter.hasNext();)
            {
                IData TradeScore = (IData) iter.next();

                for (Iterator iterator = listCommpara.iterator(); iterator.hasNext();)
                {
                    IData commpara = (IData) iterator.next();

                    if (TradeScore.getString("RULE_ID").equals(commpara.getString("PARA_CODE1")))
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
            logger.debug(" <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< 退出 ExistsExchangeNumScore() " + bResult + "<<<<<<<<<<<<<<<<<<<");

        return bResult;
    }

}
