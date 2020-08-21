
package com.asiainfo.veris.crm.order.soa.script.rule.trade;

import java.util.Iterator;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.query.BreQryForCommparaOrTag;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;

public class ExistsTradeStr8Tag extends BreBase implements IBREScript
{

    private static Logger logger = Logger.getLogger(ExistsTradeStr8Tag.class);

    /**
     * 72业务时判断rsrv_str8前8位
     */
    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        if (logger.isDebugEnabled())
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入 ExistsTradeStr8Tag() >>>>>>>>>>>>>>>>>>");

        /* 自定义区域 */
        boolean bResult = false;
        IDataset listCommpara = null;

        /* 获取规则配置信息 */
        String strTradeEparchyCode = ruleParam.getString(databus, "TRADE_EPARCHY_CODE");
        String strParamCode = ruleParam.getString(databus, "PARAM_CODE");

        /* 获取业务台账，用户资料信息 */
        IDataset listTrade = databus.getDataset("TF_B_TRADE");

        /* 开始逻辑规则校验 */
        for (Iterator iter = listTrade.iterator(); iter.hasNext();)
        {
            IData trade = (IData) iter.next();

            if (listCommpara == null)
            {
                listCommpara = BreQryForCommparaOrTag.getCommpara("CSM", 2001, strParamCode, strTradeEparchyCode);
            }

            for (Iterator iterator = listTrade.iterator(); iterator.hasNext();)
            {
                IData commpara = (IData) iterator.next();

                if (trade.getString("RSRV_STR8").substring(1, 8).equals(commpara.getString("PARA_CODE1")))
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
            logger.debug(" <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< 退出 ExistsTradeStr8Tag() " + bResult + "<<<<<<<<<<<<<<<<<<<");

        return bResult;
    }

}
