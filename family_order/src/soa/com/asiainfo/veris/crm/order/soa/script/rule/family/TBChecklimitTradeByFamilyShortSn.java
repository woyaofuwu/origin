
package com.asiainfo.veris.crm.order.soa.script.rule.family;

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
 * @Description: 业务受理前条件判断：家庭网短号用户未退出家庭网不能办理该业务！【TradeCheckBefore】
 * @author: xiaocl
 */
public class TBChecklimitTradeByFamilyShortSn extends BreBase implements IBREScript
{

    private static Logger logger = Logger.getLogger(TBChecklimitTradeByFamilyShortSn.class);

    public static boolean checkIsFmyNetShortCodeUser(String userId) throws Exception
    {
        boolean retFlag = false;
        IData params = new DataMap();
        params.put("USER_ID", userId);
        params.put("SERVICE_ID", "831"); // 家庭网短号服务
        if (Dao.qryByRecordCount("TF_F_USER_SVC", "SEL_BY_USER_SVC_ID", params))
        {
            retFlag = true;
        }
        return retFlag;
    }

    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        if (logger.isDebugEnabled())
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入 TBChecklimitTradeByFamilyShortSn() >>>>>>>>>>>>>>>>>>");

        boolean bResult = false;
        String xChoiceTag = databus.getString("X_CHOICE_TAG", "");
        if (StringUtils.isBlank(xChoiceTag) || "0".equals(xChoiceTag))
        {
            String strTradeTypeCode = databus.getString("TRADE_TYPE_CODE");
            String strUserId = databus.getString("USER_ID");
            if ("100".equals(strTradeTypeCode) || "143".equals(strTradeTypeCode)
            /* || "1301".equals(strTradeTypeCode) */)
            {
                // String userId = strUserId;

                if (checkIsFmyNetShortCodeUser(strUserId))
                {
                    StringBuilder strError = new StringBuilder("业务受理前条件判断:").append("家庭网短号用户未退出家庭网不能办理该业务！");
                    BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 751031, strError.toString());
                }
            }

            if (logger.isDebugEnabled())
                logger.debug(" <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< 退出 TBChecklimitTradeByFamilyShortSn() " + bResult + "<<<<<<<<<<<<<<<<<<<");
        }
        return bResult;
    }

}
