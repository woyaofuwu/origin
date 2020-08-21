
package com.asiainfo.veris.crm.order.soa.script.rule.user;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;
import com.asiainfo.veris.crm.order.soa.script.rule.utils.RuleUtils;

/**
 * Copyright: Copyright 2014 Asiainfo-Linkage
 * 
 * @Description: 强制限制用户不能办理哪些业务类型业务【TradeCheckBefore】
 * @author: xiaocl
 */
public class TBCheckLimitByTrade extends BreBase implements IBREScript
{

    private static Logger logger = Logger.getLogger(TBCheckLimitByTrade.class);

    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        if (logger.isDebugEnabled())
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入 TBCheckLimitByTrade() >>>>>>>>>>>>>>>>>>");

        boolean bResult = false;

        // 判断用户资料是否存在
        if (!RuleUtils.existsUserById(databus))
        {
            return bResult;
        }

        String xChoiceTag = databus.getString("X_CHOICE_TAG", "");
        if (StringUtils.isBlank(xChoiceTag) || "0".equals(xChoiceTag))
        {
            String strIdType = databus.getString("ID_TYPE");
            if ("1".equals(strIdType))
            {
                String strId = databus.getString("ID");
                String strTradeTypeCode = databus.getString("TRADE_TYPE_CODE");

                IData param = new DataMap();
                param.put("USER_ID", strId);
                param.put("TRADE_TYPE_CODE", strTradeTypeCode);
                if (Dao.qryByRecordCount("TF_F_USER_TRADELIMIT", "SEL_JUDGE_EXISTS", param))
                {
                    StringBuilder strb = new StringBuilder("业务受理前条件判断：用户受限，不能办理该业务！");
                    BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 751027, strb.toString());
                }
            }
        }

        if (logger.isDebugEnabled())
            logger.debug(" <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< 退出 TBCheckLimitByTrade() " + bResult + "<<<<<<<<<<<<<<<<<<<");

        return bResult;
    }

}
