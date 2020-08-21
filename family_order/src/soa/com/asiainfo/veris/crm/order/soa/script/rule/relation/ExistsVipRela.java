
package com.asiainfo.veris.crm.order.soa.script.rule.relation;

import java.util.Iterator;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.cust.CustVipInfoQry;

public class ExistsVipRela extends BreBase implements IBREScript
{

    private static Logger logger = Logger.getLogger(ExistsVipRela.class);

    /**
     * 大客户类型与产品类型的依赖
     */
    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        if (logger.isDebugEnabled())
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入 ExistsVipRela() >>>>>>>>>>>>>>>>>>");

        /* 自定义区域 */
        boolean bResult = false;
        IData param = new DataMap();

        /* 获取规则配置信息 */
        String strRelationTypeCode = ruleParam.getString(databus, "RELATION_TYPE_CODE");
        String strRoleCodeB = ruleParam.getString(databus, "ROLE_CODE_B");
        String strClassid = ruleParam.getString(databus, "CLASS_ID");
        String strModifyTag = ruleParam.getString(databus, "MODIFY_TAG");

        /* 获取业务台账，用户资料信息 */
        IDataset listTradeRelation = databus.getDataset("TF_B_TRADE_RELATION");

        /* 开始逻辑规则校验 */
        for (Iterator iterTradeRelation = listTradeRelation.iterator(); iterTradeRelation.hasNext();)
        {
            IData tradeRelation = (IData) iterTradeRelation.next();

            if (strModifyTag.equals(tradeRelation.getString("MODIFY_TAG")) && strRelationTypeCode.equals(tradeRelation.getString("RELATION_TYPE_CODE")) && strRoleCodeB.equals(tradeRelation.getString("ROLE_CODE_B")))
            {
                IDataset listCustVip = CustVipInfoQry.qryVipInfoByUserId(tradeRelation.getString("USER_ID_B"));

                if (listCustVip.size() > 0 && strClassid.equals(listCustVip.getData(0).getString("CLASS_ID")))
                {
                    bResult = true;
                    break;
                }
            }
        }

        if (logger.isDebugEnabled())
            logger.debug(" <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< 退出 ExistsVipRela() " + bResult + "<<<<<<<<<<<<<<<<<<<");

        return bResult;
    }

}
