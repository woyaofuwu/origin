
package com.asiainfo.veris.crm.order.soa.script.rule.staff;

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
 * @Description: 业务受理前条件判断:非HNSJ和HNHN开头的工号不能使用测试号码办理该业务【TradeCheckBefore】
 * @author: xiaocl
 */
public class TBChecklimitTestTradeByStaffId extends BreBase implements IBREScript
{

    private static Logger logger = Logger.getLogger(TBChecklimitTestTradeByStaffId.class);

    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        if (logger.isDebugEnabled())
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入 TBChecklimitTestTradeByStaffId() >>>>>>>>>>>>>>>>>>");

        boolean bResult = false;
        String xChoiceTag = databus.getString("X_CHOICE_TAG", "");
        String strStaffId = databus.getString("TRADE_STAFF_ID");
        if (StringUtils.isBlank(xChoiceTag) || "0".equals(xChoiceTag))
        {
            String strTradeTypeCode = databus.getString("TRADE_TYPE_CODE");
            if ("142".equals(strTradeTypeCode) || "310".equals(strTradeTypeCode) || "143".equals(strTradeTypeCode) || "146".equals(strTradeTypeCode))
            {
                String cityCode = databus.getString("CITY_CODE", "").trim();
                if ("142".equals(strTradeTypeCode) && "ITFSM000".equals(strStaffId))
                {

                }
                else if (!"".equals(cityCode) && ("HNSJ".equals(cityCode)||"HNHN".equals(cityCode)))
                {
                    String staffId = databus.getString("TRADE_STAFF_ID");
                    // 判断登录工号是否以HNSJ或HNHN开头
                    if (!"SUPERUSR".equals(staffId) && !staffId.startsWith("HNSJ") && !staffId.startsWith("HNHN"))
                    {
                        StringBuilder strError = new StringBuilder("业务受理前条件判断:").append("测试号码以号码业务归属为HNSJ/HNHN为准，优化补换卡业务、复机业务、改号业务、备卡业务，只能由HNSJ和HNHN开头的工号办理!");
                        BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, "", strError.toString());
                    }
                }
            }
        }
        if (logger.isDebugEnabled())
            logger.debug(" <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< 退出 TBChecklimitTestTradeByStaffId() " + bResult + "<<<<<<<<<<<<<<<<<<<");

        return bResult;
    }

}
