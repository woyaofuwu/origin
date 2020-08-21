
package com.asiainfo.veris.crm.order.soa.script.rule.trade;

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
 * @Description: 访品牌用户不可以办理预约优惠变更业务 F0 包月优惠累计生效(2、3月份28,其余30)天(奥运588、888、1688套餐为180天)后才可以办理变更！
 *               F0包月优惠累计生效(2、3月份28,其余30)天后才可以办理变更，请修改产品预约时间！ F0包月优惠累计生效30天后才可以办理变更！
 * @author: xiaocl
 */

/*
 * SELECT count(1) recordcount FROM tf_b_trade WHERE trade_id = TO_NUMBER(:TRADE_ID) AND SUBSTR(process_tag_set, 19, 1)
 * = '1'
 */

public class ExistsDiscntBookingTrade extends BreBase implements IBREScript
{
    private static Logger logger = Logger.getLogger(ExistsDiscntBookingTrade.class);

    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        if (logger.isDebugEnabled())
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入 ExistsDiscntBookingTrade() >>>>>>>>>>>>>>>>>>");

        boolean bResult = false;
        String strProcessTag = "1";
        IDataset listTrade = databus.getDataset("TF_B_TRADE");
        for (Iterator iter = listTrade.iterator(); iter.hasNext();)
        {
            IData trade = (IData) iter.next();
            if (strProcessTag.equals(trade.getString("PROCESS_TAG_SET").substring(18, 19)))
            {
                bResult = true;
                break;
            }
        }

        if (logger.isDebugEnabled())
            logger.debug(" <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< 退出 ExistsDiscntBookingTrade() " + bResult + "<<<<<<<<<<<<<<<<<<<");

        return bResult;
    }

}
