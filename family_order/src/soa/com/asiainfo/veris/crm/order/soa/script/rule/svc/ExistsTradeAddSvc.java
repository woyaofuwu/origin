
package com.asiainfo.veris.crm.order.soa.script.rule.svc;

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
 * @Description: F0彩铃服务与彩铃优惠不能同时开通！
 * @author: xiaocl
 */
/*
 * SELECT count(*) recordcount FROM tf_b_trade_svc WHERE trade_id=TO_NUMBER(:TRADE_ID) AND
 * accept_month=TO_NUMBER(:ACCEPT_MONTH) AND service_id=:SERVICE_ID AND (modify_tag =:MODIFY_TAG OR :MODIFY_TAG = '*'
 */
public class ExistsTradeAddSvc extends BreBase implements IBREScript
{
    private static Logger logger = Logger.getLogger(ExistsTradeAddSvc.class);

    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        if (logger.isDebugEnabled())
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入 ExistsTradeDiscnt() >>>>>>>>>>>>>>>>>>");

        boolean bResult = false;
        String strSvcId = ruleParam.getString(databus, "SERVICE_ID");// 20
        String strModifyTag = ruleParam.getString(databus, "MODIFY_TAG");// 0
        IDataset listTradeSvc = databus.getDataset("TF_B_TRADE_SVC");
        for (Iterator iter = listTradeSvc.iterator(); iter.hasNext();)
        {
            IData tradeSvc = (IData) iter.next();
            if (strSvcId.equals(tradeSvc.getString("SERVICE_ID")) && ("*".equals(strModifyTag) || strModifyTag.equals(tradeSvc.getString("MODIFY_TAG"))))
            {
                bResult = true;
                break;
            }
        }

        if (logger.isDebugEnabled())
            logger.debug(" <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< 退出 ExistsTradeDiscnt() " + bResult + "<<<<<<<<<<<<<<<<<<<");

        return bResult;
    }
}
