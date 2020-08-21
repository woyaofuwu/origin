
package com.asiainfo.veris.crm.order.soa.script.data.relation;

import java.util.Iterator;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;

public class ExistsBrandRela implements IBREScript
{

    private static Logger logger = Logger.getLogger(ExistsBrandRela.class);

    /**
     * 
     */
    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        if (logger.isDebugEnabled())
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入 ExistsBrandRela() >>>>>>>>>>>>>>>>>>");

        /* 自定义区域 */
        boolean bResult = false;
        IData param = new DataMap();

        /* 获取规则配置信息 */
        String strRelationTypeCode = ruleParam.getString(databus, "RELATION_TYPE_CODE");
        String strModifyTag = ruleParam.getString(databus, "MODIFY_TAG");
        String strRoleCodeB = ruleParam.getString(databus, "ROLE_CODE_B");
        String strPararmCode = ruleParam.getString("PARAM_CODE", "");

        /* 获取业务台账，用户资料信息 */
        IDataset listTradeRelation = databus.getDataset("TF_B_TRADE_RELATION");

        /* 开始逻辑规则校验 */
        for (Iterator iter = listTradeRelation.iterator(); iter.hasNext();)
        {
            IData traderelation = (IData) iter.next();

            if (strRelationTypeCode.equals(traderelation.getString("RELATION_TYPE_CODE")) && strModifyTag.equals(traderelation.getString("MODIFY_TAG")) && strRoleCodeB.equals(traderelation.getString("ROLE_CODE_B")))
            {
                param.put("PARAM_CODE", strPararmCode);
                param.put("USER_ID", traderelation.getString("USER_ID_B"));
                param.put("EPARCHY_CODE", databus.getString("EPARCHY_CODE", ""));

                if (Dao.qryByRecordCount("TD_S_CPARAM", "ExistsBrand", param))
                {
                    bResult = true;
                    break;
                }
            }
        }

        if (logger.isDebugEnabled())
            logger.debug(" <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< 退出 ExistsBrandRela() " + bResult + "<<<<<<<<<<<<<<<<<<<");

        return bResult;
    }

}
