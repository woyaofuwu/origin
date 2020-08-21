
package com.asiainfo.veris.crm.order.soa.person.rule.run.discnt;

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
 * SELECT count(1) recordcount FROM tf_b_trade_discnt WHERE trade_id = TO_NUMBER(:TRADE_ID) AND accept_month =
 * TO_NUMBER(:ACCEPT_MONTH) AND discnt_code = TO_NUMBER(:DISCNT_CODE) AND (decode(modify_tag, '4', '0', '5', '1',
 * modify_tag) = :MODIFY_TAG OR :MODIFY_TAG = '*') AND user_id = (SELECT user_id FROM tf_b_trade WHERE trade_id =
 * TO_NUMBER(:TRADE_ID) AND accept_month = TO_NUMBER(:ACCEPT_MONTH))
 */

public class ExistsTradeDiscnt extends BreBase implements IBREScript
{
    private static Logger logger = Logger.getLogger(ExistsTradeDiscnt.class);

    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        if (logger.isDebugEnabled())
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入 ExistsTradeDiscnt() >>>>>>>>>>>>>>>>>>");

        /* 自定义区域 */
        boolean bResult = false;

        /* 获取规则配置信息 */
        String strDiscntCode = ruleParam.getString(databus, "DISCNT_CODE");
        String strModifyTag = ruleParam.getString(databus, "MODIFY_TAG");

        /* 获取业务台账，用户资料信息 */
        IDataset listTradeDiscnt = databus.getDataset("TF_B_TRADE_DISCNT");
        String strUserId = databus.getString("USER_ID");

        String modify = "";
        for (Iterator iter = listTradeDiscnt.iterator(); iter.hasNext();)
        {
            IData tradeDiscnt = (IData) iter.next();
            modify = tradeDiscnt.getString("MODIFY_TAG");
            if (modify.equals("4"))
            {
                modify = "0";
            }
            else if (modify.equals("5"))
            {
                modify = "1";
            }
            if (strDiscntCode.equals(tradeDiscnt.getString("DISCNT_CODE")) && ("*".equals(strModifyTag) || strModifyTag.equals(modify)))
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
