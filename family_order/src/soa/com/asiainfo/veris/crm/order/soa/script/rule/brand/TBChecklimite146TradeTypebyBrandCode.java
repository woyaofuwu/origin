
package com.asiainfo.veris.crm.order.soa.script.rule.brand;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.query.BreQueryHelp;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;

public class TBChecklimite146TradeTypebyBrandCode extends BreBase implements IBREScript
{

    private static Logger logger = Logger.getLogger(TBChecklimite146TradeTypebyBrandCode.class);

    /**
     * Copyright: Copyright 2014 Asiainfo-Linkage
     * 
     * @ClassName: TBChecklimite146TradeTypebyBrandCode.java
     * @Description: 品牌与业务规则 中断规则 【TradeCheckBefore】
     * @version: v1.0.0
     * @author: xiaocl
     */
    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        if (logger.isDebugEnabled())
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入 TBChecklimite146TradeTypebyBrandCode() >>>>>>>>>>>>>>>>>>");

        /* 自定义区域 */
        boolean bResult = false;
        String xChoiceTag = databus.getString("X_CHOICE_TAG", "");
        if (StringUtils.isBlank(xChoiceTag) || "0".equals(xChoiceTag))
        {
            String strCancelSpTag = databus.getDataset("TD_S_TRADETYPE").getData(0).getString("CANCEL_SP_TAG", "");
            String strBrandCode = databus.getString("BRAND_CODE");

            if ("G001".equals(strBrandCode) || "G002".equals(strBrandCode) || "G010".equals(strBrandCode))
            {
            }
            else
            {
                String strBrandName = BreQueryHelp.getNameByCode("BrandName", strBrandCode);
                StringBuilder strError = new StringBuilder();
                strError.append("业务受理前条件判断：您当前品牌［").append(strBrandCode).append("|").append(strBrandName).append("］不能办理此业务！");
                BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 751016, strError.toString());
            }

        }

        if (logger.isDebugEnabled())
            logger.debug(" <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< 退出 TBChecklimite146TradeTypebyBrandCode() " + bResult + "<<<<<<<<<<<<<<<<<<<");

        return bResult;
    }

}
