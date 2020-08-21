
package com.asiainfo.veris.crm.order.soa.script.rule.discnt;

import java.util.Iterator;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;

public class ExistsTradeDiscntBboss extends BreBase implements IBREScript
{

    private static Logger logger = Logger.getLogger(ExistsTradeDiscntBboss.class);

    /**
     * 判断业务中是否增删改某优惠
     */
    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        if (logger.isDebugEnabled())
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入 ExistsTradeDiscntBboss() >>>>>>>>>>>>>>>>>>");

        /* 自定义区域 */
        boolean bResult = false;

        /* 获取规则配置信息 */
        String strDiscntCode = ruleParam.getString(databus, "DISCNT_CODE");
        String strModifyTag = ruleParam.getString(databus, "MODIFY_TAG");

        /* 获取业务台账，用户资料信息 */
        IDataset listTradeDiscnt = databus.getDataset("TF_B_TRADE_DISCNT");
        String strUserId = databus.getString("USER_ID");

        /* 开始逻辑规则校验 */
        for (Iterator iter = listTradeDiscnt.iterator(); iter.hasNext();)
        {
            IData tradeDiscnt = (IData) iter.next();

            if (strDiscntCode.equals(tradeDiscnt.getString("DISCNT_CODE")) && ("*".equals(strModifyTag) && strModifyTag.equals(tradeDiscnt.getString("MODIFY_TAG"))))
            {
                bResult = true;
                break;
            }
        }

        if (logger.isDebugEnabled())
            logger.debug(" <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< 退出 ExistsTradeDiscntBboss() " + bResult + "<<<<<<<<<<<<<<<<<<<");

        return bResult;
    }
}
