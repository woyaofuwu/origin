
package com.asiainfo.veris.crm.order.soa.script.rule.brand;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.query.BreQueryHelp;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;

/**
 * Copyright: Copyright 2014 Asiainfo-Linkage
 * 
 * @Description: 业务受理前条件判断：GS01|GS02不能受理付费关系变更业务！【TradeCheckBefore】
 * @author: xiaocl
 */
public class TBChecklimitTradeBySpecBrand extends BreBase implements IBREScript
{

    private static Logger logger = Logger.getLogger(TBChecklimitTradeBySpecBrand.class);

    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        if (logger.isDebugEnabled())
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入 TBChecklimitTradeBySpecBrand() >>>>>>>>>>>>>>>>>>");

        boolean bResult = false;
        String strTradeTypeCode = databus.getString("TRADE_TYPE_CODE");
        String strBrandCode = databus.getString("BRAND_CODE");
        if (strTradeTypeCode.equals("160") && ("GS01".equals(strBrandCode) || "GS02".equals(strBrandCode)))
        {
            String strBrand = BreQueryHelp.getNameByCode("BRAND_CODE", strTradeTypeCode);
            StringBuilder strError = new StringBuilder("特殊业务限制提示:").append(strBrand).append("该品牌用户不能办理该业务");
            BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 751030, strError.toString());

        }

        if (logger.isDebugEnabled())
            logger.debug(" <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< 退出 TBChecklimitTradeBySpecBrand() " + bResult + "<<<<<<<<<<<<<<<<<<<");

        return bResult;
    }

}
