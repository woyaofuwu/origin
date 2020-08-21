
package com.asiainfo.veris.crm.order.soa.script.rule.fee;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
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
 * @Description: 业务前校验：押金清退提示【TradeCheckBefore】
 * @author: xiaocl
 */
public class TBCheckRtnForegiftTag extends BreBase implements IBREScript
{

    private static Logger logger = Logger.getLogger(TBCheckRtnForegiftTag.class);

    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        if (logger.isDebugEnabled())
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入 TBCheckRtnForegiftTag() >>>>>>>>>>>>>>>>>>");

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
                String strRtnForegiftTag = dataTradeTypeCode.getString("RTN_FOREGIFT_TAG", "");
                if (!"".equals(strRtnForegiftTag) && !"0".equals(strRtnForegiftTag))
                {
                    IData param = new DataMap();
                    param.put("USER_ID", databus.getString("ID"));

                    if (Dao.qryByRecordCount("TD_S_CPARAM", "SEL_SUM_OF_USER_FOREGIFT", param))
                    {
                        if ("1".equals(strRtnForegiftTag))
                        {
                            bResult = true;
                            BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, "751039", "业务受理前条件判断：该用户有押金，请先办理预存清退！");
                        }
                        else
                        {
                            databus.put("X_CHECK_TAG", 1);
                            bResult = true;
                            BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_CHIOCE, -1, "业务受理前条件判断:该用户有押金，是否要继续业务的办理？<br/>选择【是】继续办理业务，选择【否】终止办理业务。");
                        }
                    }
                }
            }
        }
        if (logger.isDebugEnabled())
            logger.debug(" <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< 退出 TBCheckRtnForegiftTag() " + bResult + "<<<<<<<<<<<<<<<<<<<");

        return bResult;
    }

}
