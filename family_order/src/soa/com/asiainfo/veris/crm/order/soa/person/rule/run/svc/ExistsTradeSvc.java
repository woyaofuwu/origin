
package com.asiainfo.veris.crm.order.soa.person.rule.run.svc;

import java.util.Iterator;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;

/**
 * Copyright: Copyright 2014 Asiainfo-Linkage
 * 
 * @Description: 判断业务中是否增删改某优惠
 * @author: xiaocl
 */

/*
 * SELECT count(*) recordcount FROM tf_b_trade_svc WHERE trade_id=TO_NUMBER(:TRADE_ID) AND
 * accept_month=TO_NUMBER(:ACCEPT_MONTH) AND service_id=:SERVICE_ID AND (decode(modify_tag,'U','0',modify_tag)
 * =:MODIFY_TAG OR :MODIFY_TAG = '*')
 */

public class ExistsTradeSvc extends BreBase implements IBREScript
{
    private static Logger logger = Logger.getLogger(ExistsTradeSvc.class);

    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        if (logger.isDebugEnabled())
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入 ExistsTradeSvc() >>>>>>>>>>>>>>>>>>");

        boolean bResult = false;

        String strSvcId = ruleParam.getString(databus, "SERVICE_ID");
        String strModifyTag = ruleParam.getString(databus, "MODIFY_TAG");
        IDataset listTradeSvc = databus.getDataset("TF_B_TRADE_SVC");
        for (Iterator iter = listTradeSvc.iterator(); iter.hasNext();)
        {
            IData tradeSvc = (IData) iter.next();
            String strModifyfor = tradeSvc.getString("MODIFY_TAG");
            if (tradeSvc.getString("MODIFY_TAG").equals("U"))
            {
                strModifyfor = "0";
            }
            if (strSvcId.equals(tradeSvc.getString("SERVICE_ID")) && ("*".equals(strModifyTag) || strModifyTag.equals(strModifyfor)))
            {
                bResult = true;
                break;
            }
        }

        if (logger.isDebugEnabled())
            logger.debug(" <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< 退出 ExistsTradeSvc() " + bResult + "<<<<<<<<<<<<<<<<<<<");

        return bResult;
    }

}
