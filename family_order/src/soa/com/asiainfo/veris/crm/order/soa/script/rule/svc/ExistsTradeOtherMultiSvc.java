
package com.asiainfo.veris.crm.order.soa.script.rule.svc;

import java.util.Iterator;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.query.BreQryForCommparaOrTag;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;

public class ExistsTradeOtherMultiSvc extends BreBase implements IBREScript
{

    private static Logger logger = Logger.getLogger(ExistsTradeOtherMultiSvc.class);

    /**
     * 判断用户该笔业务是否办理某一类服务
     */
    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        if (logger.isDebugEnabled())
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入 ExistsTradeOtherMultiSvc() >>>>>>>>>>>>>>>>>>");

        /* 自定义区域 */
        boolean bResult = false;
        IDataset listCommpara = null;

        /* 获取规则配置信息 */
        String strTradeEparchyCode = ruleParam.getString(databus, "TRADE_EPARCHY_CODE");
        String strModifyTag = ruleParam.getString(databus, "MODIFY_TAG");
        String strParamCode = ruleParam.getString(databus, "PARAM_CODE");

        /* 获取业务台账，用户资料信息 */
        IDataset listTradeDiscnt = databus.getDataset("TF_B_TRADE_SVC");

        /* 开始逻辑规则校验 */
        for (Iterator iter = listTradeDiscnt.iterator(); iter.hasNext();)
        {
            IData tradeDiscnt = (IData) iter.next();

            if ("*".equals(strModifyTag) || strModifyTag.equals(tradeDiscnt.getString("MODIFY_TAG")))
            {
                if (listCommpara == null)
                {
                    listCommpara = BreQryForCommparaOrTag.getCommpara("CSM", 2001, strParamCode, strTradeEparchyCode);
                }

                for (Iterator iterator = listTradeDiscnt.iterator(); iterator.hasNext();)
                {
                    IData commpara = (IData) iterator.next();

                    if (tradeDiscnt.getString("SERVICE_ID").equals(commpara.getString("PARA_CODE1")))
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
            logger.debug(" <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< 退出 ExistsTradeOtherMultiSvc() " + bResult + "<<<<<<<<<<<<<<<<<<<");

        return bResult;
    }

}
