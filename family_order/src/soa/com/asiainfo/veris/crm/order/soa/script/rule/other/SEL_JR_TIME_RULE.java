
package com.asiainfo.veris.crm.order.soa.script.rule.other;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTimeUtil;

/**
 * Copyright: Copyright 2014 Asiainfo-Linkage
 * 
 * @Description: 新校园卡假期套餐只有在1月1日（含1）至3月31日（含）、7月1日（含）至9月30日（含）才能办理！
 * @author: xiaocl
 */

/*
 * SELECT COUNT(1) recordcount FROM DUAL WHERE (TO_CHAR(SYSDATE, 'mmdd') >= '0101' AND TO_CHAR(SYSDATE, 'mmdd')
 * <='0331') OR (TO_CHAR(SYSDATE, 'mmdd') >= '0701' AND TO_CHAR(SYSDATE, 'mmdd') <= '0930')
 */
public class SEL_JR_TIME_RULE extends BreBase implements IBREScript
{
    private static Logger logger = Logger.getLogger(SEL_JR_TIME_RULE.class);

    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        if (logger.isDebugEnabled())
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入 SEL_JR_TIME_RULE() >>>>>>>>>>>>>>>>>>");

        boolean bResult = false;
        StringBuilder strDate = new StringBuilder(BreTimeUtil.getCurDate(databus).substring(5, 7));
        strDate.append(BreTimeUtil.getCurDate(databus).substring(8, 10));
        if (strDate.toString().compareTo("0101") >= 0 && strDate.toString().compareTo("0331") <= 0)
        {
            bResult = true;
        }
        if (strDate.toString().compareTo("0701") >= 0 && strDate.toString().compareTo("0930") <= 0)
        {
            bResult = true;
        }

        if (logger.isDebugEnabled())
            logger.debug(" <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< 退出 SEL_JR_TIME_RULE() " + bResult + "<<<<<<<<<<<<<<<<<<<");

        return bResult;
    }

}
