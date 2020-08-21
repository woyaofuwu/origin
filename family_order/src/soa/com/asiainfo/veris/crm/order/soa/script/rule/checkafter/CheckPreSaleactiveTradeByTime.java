
package com.asiainfo.veris.crm.order.soa.script.rule.checkafter;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeInfoQry;

/**
 * Copyright: Copyright 2014 Asiainfo-Linkage
 * 
 * @Description: 业务登记后条件判断:此用户有未完工的营销活动工单, 请稍后办理！【TradeCheckAfter】
 * @author: xiaocl
 */

public class CheckPreSaleactiveTradeByTime extends BreBase implements IBREScript
{
    private static Logger logger = Logger.getLogger(CheckPreTradeSaleactiveRule.class);

    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        if (logger.isDebugEnabled())
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入 CheckPreSaleactiveTradeByTime() >>>>>>>>>>>>>>>>>>");

        /* 自定义区域 */
        boolean bResult = false;
        /* 获取业务台账，用户资料信息 */
        String strTradeTypeCode = databus.getString("TRADE_TYPE_CODE");
        String strUserId = databus.getString("USER_ID");
        String strTradeId = databus.getString("TRADE_ID");
        String strEparchyCode = databus.getString("TRADE_EPARCHY_CODE");
        /* 获取外部信息 */
        IDataset listPreTradeInfo = TradeInfoQry.getUserTradeByUserID(strTradeTypeCode, strUserId, "", "", strEparchyCode, null);
        int iCnt = -1;
        if (IDataUtil.isNotEmpty(listPreTradeInfo))
        {
            iCnt = listPreTradeInfo.size();
        }
        if (iCnt > 1)
        {
            bResult = true;
        }
        else if (iCnt == 1 && strTradeId.equals(listPreTradeInfo.getData(0).getString("TRADE_ID")))
        {
            bResult = true;
        }
        if (logger.isDebugEnabled())
            logger.debug(" <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< 退出 CheckPreSaleactiveTradeByTime() " + bResult + "<<<<<<<<<<<<<<<<<<<");

        return bResult;
    }

}
