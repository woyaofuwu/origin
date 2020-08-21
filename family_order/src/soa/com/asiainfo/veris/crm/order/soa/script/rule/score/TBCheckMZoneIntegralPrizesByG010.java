
package com.asiainfo.veris.crm.order.soa.script.rule.score;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;
import com.asiainfo.veris.crm.order.soa.script.rule.utils.RuleUtils;

/**
 * Copyright: Copyright 2014 Asiainfo-Linkage
 * 
 * @Description: 只有动感地带用户才能办理M值兑奖【TradeCheckBefore】
 * @author: xiaocl
 */
public class TBCheckMZoneIntegralPrizesByG010 extends BreBase implements IBREScript
{
    private static Logger logger = Logger.getLogger(TBCheckMZoneIntegralPrizesByG010.class);

    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        if (logger.isDebugEnabled())
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入 TBCheckMZoneIntegralPrizesByG010() >>>>>>>>>>>>>>>>>>");

        /* 自定义区域 */
        boolean bResult = false;

        // 判断用户资料是否存在
        if (!RuleUtils.existsUserById(databus))
        {
            return bResult;
        }

        String xChoiceTag = databus.getString("X_CHOICE_TAG", "");
        if (StringUtils.isBlank(xChoiceTag) || "0".equals(xChoiceTag))
        {
            /* 获取规则配置信息 */

            /* 获取业务台账，用户资料信息 */
            String strTradeTypeCode = databus.getString("TRADE_TYPE_CODE");

            /* 开始逻辑规则校验 */
            if ("333".equals(strTradeTypeCode))
            {
                String strBrandCode = databus.getString("BRAND_CODE");
                if (!"G010".equals(strBrandCode))
                {
                    bResult = true;
                    BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, "751029", "业务受理前条件判断：只有动感地带用户才能办理M值兑奖业务！");
                }
            }
        }
        if (logger.isDebugEnabled())
            logger.debug(" <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< 退出 TBCheckMZoneIntegralPrizesByG010() " + bResult + "<<<<<<<<<<<<<<<<<<<");

        return bResult;
    }

}
