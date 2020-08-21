
package com.asiainfo.veris.crm.order.soa.script.rule.relation;

import java.util.Iterator;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;

public class ExistPayRelationRela extends BreBase implements IBREScript
{

    private static Logger logger = Logger.getLogger(ExistPayRelationRela.class);

    /**
     * 存在合帐的关系用户
     */
    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        if (logger.isDebugEnabled())
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入 ExistPayRelationRela() >>>>>>>>>>>>>>>>>>");

        /* 自定义区域 */
        boolean bResult = false;
        String strUserIdB = null;
        IData param = null;

        /* 获取规则配置信息 */
        String strModifyTag = ruleParam.getString(databus, "MODIFY_TAG");
        String strRelationTypeCode = ruleParam.getString(databus, "RELATION_TYPE_CODE");
        String strRoleCodeB = ruleParam.getString(databus, "ROLE_CODE_B");
        String strAcctId = ruleParam.getString(databus, "ACCT_ID");

        /* 获取业务台账，用户资料信息 */
        IDataset listTradeRelation = databus.getDataset("TF_B_TRADE_RELATION");

        /* 开始逻辑规则校验 */
        for (Iterator iter = listTradeRelation.iterator(); iter.hasNext();)
        {
            IData tradeRelation = (IData) iter.next();

            if (strRoleCodeB.equals(tradeRelation.getString("ROLE_CODE_B")) && strModifyTag.equals(tradeRelation.getString("MODIFY_TAG")) && strRelationTypeCode.equals(tradeRelation.getString("RELATION_TYPE_CODE")))
            {
                strUserIdB = tradeRelation.getString("USER_ID_B");

                param = null;
                param = new DataMap();
                param.put("USER_ID", strUserIdB);

                IDataset listRelationUU = Dao.qryByCode("", "", param);

                for (Iterator iterator = listRelationUU.iterator(); iterator.hasNext();)
                {
                    IData relationUU = (IData) iterator.next();

                    if (strAcctId.equals(relationUU.getString("ACCT_ID")))
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
            logger.debug(" <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< 退出 ExistPayRelationRela() " + bResult + "<<<<<<<<<<<<<<<<<<<");

        return bResult;
    }

}
