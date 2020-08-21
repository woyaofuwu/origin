
package com.asiainfo.veris.crm.order.soa.script.rule.userdeduct;

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

/**
 * Copyright: Copyright 2014 Asiainfo-Linkage
 * 
 * @Description: 该用户办理了个人代扣业务，请取消后办理当前业务【TradeCheckBefore】
 * @author: xiaocl
 */
public class TBChecklimitTradeByDaikou extends BreBase implements IBREScript
{

    private static Logger logger = Logger.getLogger(TBChecklimitTradeByDaikou.class);

    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        if (logger.isDebugEnabled())
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入 TBChecklimitTradeByDaikou() >>>>>>>>>>>>>>>>>>");

        boolean bResult = false;
        String xChoiceTag = databus.getString("X_CHOICE_TAG", "");
        if (StringUtils.isBlank(xChoiceTag) || "0".equals(xChoiceTag))
        {
            IData param = new DataMap();
            String strTradeTypeCode = databus.getString("TRADE_TYPE_CODE");
            String strId = databus.getString("USER_ID");
            if (strTradeTypeCode.equals("160"))
            {
                param.put("USER_ID", strId);
                param.put("DESTROY_TAG", "0");
                if (Dao.qryByRecordCount("TF_F_USER_DEDUCT", "SEL_BY_USER", param))
                {
                    BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 751037, "特殊限制表判断:该用户办理了个人代扣业务，请取消后办理当前业务！");
                }
            }
        }
        if (logger.isDebugEnabled())
            logger.debug(" <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< 退出 TBChecklimitTradeByDaikou() " + bResult + "<<<<<<<<<<<<<<<<<<<");

        return bResult;
    }

}
