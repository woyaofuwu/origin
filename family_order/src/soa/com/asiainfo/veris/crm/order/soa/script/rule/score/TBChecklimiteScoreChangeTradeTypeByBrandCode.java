
package com.asiainfo.veris.crm.order.soa.script.rule.score;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;

/**
 * Copyright: Copyright 2014 Asiainfo-Linkage
 * 
 * @ClassName: TBChecklimiteScoreChangeTradeTypeByBrandCode.java
 * @Description: 该用户品牌不能受理积分兑换业务 【TradeCheckBefore】
 * @version: v1.0.0
 * @author: xiaocl
 */
public class TBChecklimiteScoreChangeTradeTypeByBrandCode extends BreBase implements IBREScript
{

    private static Logger logger = Logger.getLogger(TBChecklimiteScoreChangeTradeTypeByBrandCode.class);

    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        if (logger.isDebugEnabled())
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入 TBChecklimiteScoreChangeTradeTypeByBrandCode() >>>>>>>>>>>>>>>>>>");

        /* 自定义区域 */
        boolean bResult = false;
        String xChoiceTag = databus.getString("X_CHOICE_TAG", "");
        if (StringUtils.isBlank(xChoiceTag) || "0".equals(xChoiceTag))
        {
            String strBrandCode = databus.getString("BRAND_CODE");

            if (!"G001".equals(strBrandCode) && !"G002".equals(strBrandCode) && !"G010".equals(strBrandCode))
            {
                BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 751018, "业务受理前条件判断：该用户品牌不能办理积分兑奖业务！");
            }

        }

        if (logger.isDebugEnabled())
            logger.debug(" <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< 退出 TBChecklimiteScoreChangeTradeTypeByBrandCode() " + bResult + "<<<<<<<<<<<<<<<<<<<");

        return bResult;
    }

}
