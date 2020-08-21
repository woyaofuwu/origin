
package com.asiainfo.veris.crm.order.soa.script.rule.fee;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;
import com.asiainfo.veris.crm.order.soa.script.rule.utils.RuleUtils;

/**
 * Copyright: Copyright 2014 Asiainfo-Linkage
 * 
 * @Description: 预存清退提示【TradeCheckBefore】
 * @author: xiaocl
 */
public class TBCheckRtnDepositTag extends BreBase implements IBREScript
{

    private static Logger logger = Logger.getLogger(TBCheckRtnDepositTag.class);

    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        if (logger.isDebugEnabled())
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入 TBCheckRtnDepositTag() >>>>>>>>>>>>>>>>>>");

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
                IDataset listTradeTypeCode = databus.getDataset("TD_S_TRADETYPE");
                IData dataTradeTypeCode = listTradeTypeCode.first();
                if (IDataUtil.isEmpty(dataTradeTypeCode))
                    return false;
                String strRtnDepositTag = dataTradeTypeCode.getString("RTN_DEPOSIT_TAG", "");
                int iFee;
                if (!"".equals(strRtnDepositTag) && !"0".equals(strRtnDepositTag))
                {
                    if (databus.containsKey("FEE"))
                    {
                        iFee = databus.getInt("FEE");
                    }
                    else
                    {
                        iFee = databus.getInt("LEAVE_REAL_FEE");
                    }
                    if (iFee > 0)
                    {
                        if ("1".equals(strRtnDepositTag))
                        {
                            BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, "751038", "业务受理前条件判断：该用户有预存，请先办理预存清退！");
                        }
                        else
                        {
                            databus.put("X_CHECK_TAG", 1);
                            BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_CHIOCE, -1, "业务受理前条件判断:该用户有结余，是否要继续业务的办理？<br/>选择【是】继续办理业务，选择【否】终止办理业务。");
                        }
                    }
                }
            }
        }

        if (logger.isDebugEnabled())
            logger.debug(" <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< 退出 TBCheckRtnDepositTag() " + bResult + "<<<<<<<<<<<<<<<<<<<");

        return bResult;
    }

}
