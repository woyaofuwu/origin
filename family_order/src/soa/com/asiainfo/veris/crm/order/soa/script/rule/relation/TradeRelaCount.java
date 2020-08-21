
package com.asiainfo.veris.crm.order.soa.script.rule.relation;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;

public class TradeRelaCount extends BreBase implements IBREScript
{

    private static Logger logger = Logger.getLogger(TradeRelaCount.class);

    /**
     * 判断relation_uu台账
     */
    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        if (logger.isDebugEnabled())
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入 TradeRelaCount() >>>>>>>>>>>>>>>>>>");

        /* 自定义区域 */
        boolean bResult = false;
        int iCount = 0;

        /* 获取规则配置信息 */
        int iNum = ruleParam.getInt(databus, "NUM");
        String strRelationTypeCode = ruleParam.getString(databus, "RELATION_TYPE_CODE");
        String strRoleCodeB = ruleParam.getString(databus, "ROLE_CODE_B");

        /* 获取业务台账，用户资料信息 */
        IDataset listTradeRelation = databus.getDataset("TF_B_TRADE_RELATION");

        /* 开始逻辑规则校验 */
        int iCountTradeRelation = listTradeRelation.size();
        for (int idx = 0; idx < iCountTradeRelation; idx++)
        {
            if (strRelationTypeCode.equals(listTradeRelation.getData(idx).getString("RELATION_TYPE_CODE")) && ("*".equals(strRoleCodeB) || strRoleCodeB.equals(listTradeRelation.getData(idx).getString("ROLE_CODE_B"))))
            {
                iCount++;
            }

            if (iCount > iNum)
            {
                bResult = true;
                break;
            }
        }

        if (logger.isDebugEnabled())
            logger.debug(" <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< 退出 TradeRelaCount() " + bResult + "<<<<<<<<<<<<<<<<<<<");

        return bResult;
    }

}
