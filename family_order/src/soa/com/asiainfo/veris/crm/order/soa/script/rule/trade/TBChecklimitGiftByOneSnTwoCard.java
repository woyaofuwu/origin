
package com.asiainfo.veris.crm.order.soa.script.rule.trade;

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
 * @Description: 一卡双号用户与礼包业务的限制！【TradeCheckBefore】
 * @author: xiaocl
 */
public class TBChecklimitGiftByOneSnTwoCard extends BreBase implements IBREScript
{

    private static Logger logger = Logger.getLogger(TBChecklimitGiftByOneSnTwoCard.class);

    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        if (logger.isDebugEnabled())
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入 TBChecklimitGiftByOneSnTwoCard() >>>>>>>>>>>>>>>>>>");
        boolean bResult = false;
        String xChoiceTag = databus.getString("X_CHOICE_TAG", "");
        if (StringUtils.isBlank(xChoiceTag) || "0".equals(xChoiceTag))
        {
            int iCount = 0;
            IData param = new DataMap();
            String strUserId = databus.getString("USER_ID");
            param.put("USER_ID", strUserId);
            param.put("RELATION_TYPE_CODE", "30");
            param.put("ROLE_CODE_B", "2");

            iCount = Integer.parseInt((String) Dao.qryByCode("TD_S_CPARAM", "ExistsRelationUU", param).getData(0).getString("RECORDCOUNT"));
            if (iCount > 0)
            {
                BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 751035, "该用户是一卡双号副卡，不能办理本业务！");
            }
        }
        if (logger.isDebugEnabled())
            logger.debug(" <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< 退出 TBChecklimitGiftByOneSnTwoCard() " + bResult + "<<<<<<<<<<<<<<<<<<<");
        return bResult;
    }
}
