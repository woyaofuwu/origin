
package com.asiainfo.veris.crm.order.soa.script.rule.other;

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
 * @Description: 本储蓄卡已经办理过尊荣畅享一元银行存款方式购机业务
 * @author: xiaocl
 */
/*
 * select COUNT(1) recordcount from tf_b_trade_other a where trade_id=to_number(:TRADE_ID) and rsrv_value_code='YHXX'
 * and modify_tag='0' and exists (select 1 from tf_f_user_other where rsrv_value_code='YHXX' and rsrv_str6=a.rsrv_str6
 * and end_date>sysdate)
 */
public class ExistsSameYhxx extends BreBase implements IBREScript
{
    private static Logger logger = Logger.getLogger(ExistsSameYhxx.class);

    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        if (logger.isDebugEnabled())
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入 ExistsSameYhxx() >>>>>>>>>>>>>>>>>>");

        boolean bResult = false;
        // String strModifyTag = ruleParam.getString(databus, "MODIFY_TAG");
        String strRsrvValueCode = ruleParam.getString(databus, "RSRV_VALUE_CODE");
        IDataset listTradeOther = databus.getDataset("TF_B_TRADE_OTHER");
        IDataset listUserOther = databus.getDataset("TF_F_USER_OTHER");
        for (Iterator iter = listTradeOther.iterator(); iter.hasNext();)
        {
            IData tradeOther = (IData) iter.next();
            for (Iterator iterUserOther = listUserOther.iterator(); iterUserOther.hasNext();)
            {
                IData userOther = (IData) iterUserOther.next();
                if (tradeOther.getString("MODIFY_TAG").equals("0") && tradeOther.getString("RSRV_VALUE_CODE").equals(strRsrvValueCode) && userOther.getString("RSRV_VALUE_CODE").equals(strRsrvValueCode)
                        && userOther.getString("RSRV_STR6").equals(tradeOther.getString("RSRV_STR6")))
                {
                    bResult = true;
                    break;
                }
            }
            if (bResult)
            {
                break;
            }
        }

        if (logger.isDebugEnabled())
            logger.debug(" <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< 退出 ExistsSameYhxx() " + bResult + "<<<<<<<<<<<<<<<<<<<");

        return bResult;
    }

}
