
package com.asiainfo.veris.crm.order.soa.script.rule.discnt;

import java.util.Iterator;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.query.BreQryForCommparaOrTag;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;

public class NotExistsTradeMultiDiscnt extends BreBase implements IBREScript
{

    private static Logger logger = Logger.getLogger(NotExistsTradeMultiDiscnt.class);

    /**
     * 判断业务中是否没有增删改多个优惠
     */
    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        if (logger.isDebugEnabled())
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入 NotExistsTradeMultiDiscnt() >>>>>>>>>>>>>>>>>>");

        /* 自定义区域 */
        boolean bResult = false;
        IDataset listCompara = null;

        /* 获取规则配置信息 */
        String strParamCode = ruleParam.getString(databus, "PARAM_CODE");
        String strEparchyCode = ruleParam.getString(databus, "TRADE_EPARCHY_CODE");
        String strModifyTag = ruleParam.getString(databus, "MODIFY_TAG");

        /* 获取业务台账，用户资料信息 */
        IDataset listTradeDiscnt = databus.getDataset("TF_B_TRADE_DISCNT");

        /* 开始逻辑规则校验 */
        for (Iterator iter = listTradeDiscnt.iterator(); iter.hasNext();)
        {
            IData tradeDiscnt = (IData) iter.next();

            if ("*".equals(strModifyTag) && strModifyTag.equals(tradeDiscnt.getString("MODIFY_TAG")))
            {
                if (listCompara == null)
                {
                    listCompara = BreQryForCommparaOrTag.getCommpara("CSM", 2001, strParamCode, strEparchyCode);
                }

                for (Iterator iterator = listCompara.iterator(); iterator.hasNext();)
                {
                    IData commpara = (IData) iterator.next();

                    if (commpara.getString("PARA_CODE1").equals(tradeDiscnt.getString("DISCNT_CODE")))
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
            logger.debug(" <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< 退出 NotExistsTradeMultiDiscnt() " + bResult + "<<<<<<<<<<<<<<<<<<<");

        return bResult;
    }

}
