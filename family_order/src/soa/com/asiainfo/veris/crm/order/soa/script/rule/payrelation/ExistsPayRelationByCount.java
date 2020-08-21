
package com.asiainfo.veris.crm.order.soa.script.rule.payrelation;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;

public class ExistsPayRelationByCount extends BreBase implements IBREScript
{

    private static Logger logger = Logger.getLogger(ExistsPayRelationByCount.class);

    /**
     * 判断用户最多只能有几个合账用户
     */
    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        if (logger.isDebugEnabled())
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入 ExistsPayRelationByCount() >>>>>>>>>>>>>>>>>>");

        /* 自定义区域 */
        boolean bResult = false;
        IData param = new DataMap();

        /* 获取规则配置信息 */
        String strDefaultTag = ruleParam.getString(databus, "DEFAULT_TAG");
        int iPayRelationCount = ruleParam.getInt(databus, "NUM");

        /* 获取业务台账，用户资料信息 */
        String strTradeTypeCode = databus.getString("TRADE_TYPE_CODE");
        String strAcctId = databus.getDataset("TF_A_PAYRELATION").getData(0).getString("ACCT_ID");

        /* 开始逻辑规则校验 */

        if ("1".equals(strDefaultTag))
        {
            param.clear();
            param.put("ACCT_ID", strAcctId);
            param.put("DEFAULT_TAG", strDefaultTag);
            param.put("ACT_TAG", "1");
            int iCount = Dao.qryByCode("TF_A_PAYRELATION", "SEL_BY_ACCT_DA", param).size();

            if (iCount >= iPayRelationCount)
            {
                bResult = true;

                if ("192".equals(strTradeTypeCode))
                {
                    param.clear();
                    param.put("USER_ID_B", databus.getString("USER_ID"));
                    param.put("RELATION_TYPE_CODE", "92");
                    if (Dao.qryByCode("TF_F_RELATION_UU", "SEL_USER_IDB", param).size() > 0)
                    {
                        bResult = false;
                    }
                }
            }
        }

        if (logger.isDebugEnabled())
            logger.debug(" <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< 退出 ExistsPayRelationByCount() " + bResult + "<<<<<<<<<<<<<<<<<<<");

        return bResult;
    }

}
