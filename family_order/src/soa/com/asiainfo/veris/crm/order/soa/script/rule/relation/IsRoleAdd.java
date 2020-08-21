
package com.asiainfo.veris.crm.order.soa.script.rule.relation;

import java.util.Iterator;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;

public class IsRoleAdd extends BreBase implements IBREScript
{

    private static Logger logger = Logger.getLogger(IsRoleAdd.class);

    /**
     * 主副卡是否变化
     */
    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        if (logger.isDebugEnabled())
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入 IsRoleAdd() >>>>>>>>>>>>>>>>>>");

        /* 自定义区域 */
        boolean bResult = false;

        /* 获取规则配置信息 */
        String strModifyTag = ruleParam.getString(databus, "MODIFY_TAG");
        String strRelationTypeCode = ruleParam.getString(databus, "RELATION_TYPE_CODE");
        String strRoleCodeB = ruleParam.getString(databus, "ROLE_CODE_B");

        /* 获取业务台账，用户资料信息 */
        IDataset listTradeRelation = databus.getDataset("TF_B_TRADE_RELATION");

        /* 开始逻辑规则校验 */
        for (Iterator iterTradeRelation = listTradeRelation.iterator(); iterTradeRelation.hasNext();)
        {
            IData tradeRelation = (IData) iterTradeRelation.next();

            if ("*".equals(strModifyTag) || strModifyTag.equals(tradeRelation.getString("MODIFY_TAG")) && strRelationTypeCode.equals(tradeRelation.getString("RELATION_TYPE_CODE")) && strRoleCodeB.equals(tradeRelation.getString("ROLE_CODE_B")))
            {
                bResult = true;
                break;
            }
        }

        if (logger.isDebugEnabled())
            logger.debug(" <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< 退出 IsRoleAdd() " + bResult + "<<<<<<<<<<<<<<<<<<<");

        return bResult;
    }

}
