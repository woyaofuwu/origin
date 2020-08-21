
package com.asiainfo.veris.crm.order.soa.script.rule.attr;

import java.util.Iterator;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.query.BreQryForCommparaOrTag;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;

public class ExistsTradeSvcServPara1 extends BreBase implements IBREScript
{

    private static Logger logger = Logger.getLogger(ExistsTradeSvcServPara1.class);

    /**
     * 判断用户都是修改了某类服务属性
     */
    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        if (logger.isDebugEnabled())
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入 ExistsTradeSvcServPara1() >>>>>>>>>>>>>>>>>>");

        /* 自定义区域 */
        boolean bResult = false;
        IDataset listCommpara = null;

        /* 获取规则配置信息 */
        String strEparchyCode = ruleParam.getString(databus, "EPARCHY_CODE");
        String strSvcId = ruleParam.getString(databus, "SERVICE_ID");
        String strModifyTag = ruleParam.getString(databus, "MODIFY_TAG");
        String strParamCode = ruleParam.getString(databus, "PARAM_CODE");

        /* 获取业务台账，用户资料信息 */
        IDataset listTradeSvc = databus.getDataset("TF_B_TRADE_SVC");
        IDataset listTradeAttr = databus.getDataset("TF_B_TRADE_ATTR");

        /* 开始逻辑规则校验 */
        for (Iterator iter = listTradeSvc.iterator(); iter.hasNext();)
        {
            IData tradeSvc = (IData) iter.next();

            if (strSvcId.equals(tradeSvc.getString("SERVICE_ID")) && ("*".equals(strModifyTag) && strModifyTag.equals(tradeSvc.getString("MODIFY_TAG"))))
            {
                for (Iterator iterator = listTradeAttr.iterator(); iterator.hasNext();)
                {
                    IData tradeAttr = (IData) iterator.next();

                    if (tradeAttr.getString("TRADE_ID").equals(tradeSvc.getString("TRADE_ID")) && tradeAttr.getString("INST_ID").equals(tradeSvc.getString("INST_ID")))
                    {

                        if (listCommpara == null)
                        {
                            listCommpara = BreQryForCommparaOrTag.getCommpara("CSM", 2001, strParamCode, strEparchyCode);
                        }

                        for (Iterator iterCommparam = listTradeAttr.iterator(); iterCommparam.hasNext();)
                        {
                            IData commpara = (IData) iterCommparam.next();

                            if (commpara.getString("PARA_CODE1").equals(tradeAttr.getString("ATTR_VALUE")))
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
            }

            if (bResult)
            {
                break;
            }
        }

        if (logger.isDebugEnabled())
            logger.debug(" <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< 退出 ExistsTradeSvcServPara1() " + bResult + "<<<<<<<<<<<<<<<<<<<");

        return bResult;
    }

}
