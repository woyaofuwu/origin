
package com.asiainfo.veris.crm.order.soa.script.rule.relation;

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
 * @Description: IP直通车不能办理哪些业务【TradeCheckBefore】
 * @author: xiaocl
 */
public class TBCheckLimitIPPhone extends BreBase implements IBREScript
{

    private static Logger logger = Logger.getLogger(TBCheckLimitIPPhone.class);

    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        if (logger.isDebugEnabled())
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入 TBCheckLimitIPPhone() >>>>>>>>>>>>>>>>>>");

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
                String strTradeTypeCode = databus.getString("TRADE_TYPE_CODE");

                if ("100".equals(strTradeTypeCode) || "190".equals(strTradeTypeCode) || "191".equals(strTradeTypeCode) || "192".equals(strTradeTypeCode))
                {
                    String strId = databus.getString("ID");

                    IData param = new DataMap();
                    param.put("USER_ID_B", strId);
                    param.put("RELATION_TYPE_CODE", "50");

                    if (Dao.qryByRecordCount("TF_F_RELATION_UU", "SEL_USER_UUROL", param))
                    {
                        if ("100".equals(strTradeTypeCode))
                        {
                            databus.put("X_CHECK_TAG", 1);
                            bResult = true;
                            BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_CHIOCE, -1, "业务受理前条件判断:该用户有IP直通车业务没有取消，建议终止业务的办理！<br/>是否要继续业务的办理？选择【是】继续办理业务，选择【否】终止办理业务。");
                        }
                        else
                        {
                            bResult = true;
                            BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 751028, "业务受理前条件判断：该用户有IP直通车业务没有取消，请取消后再办理本业务！");
                        }
                    }
                }
            }
        }
        if (logger.isDebugEnabled())
            logger.debug(" <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< 退出 TBCheckLimitIPPhone() " + bResult + "<<<<<<<<<<<<<<<<<<<");

        return bResult;
    }

}
