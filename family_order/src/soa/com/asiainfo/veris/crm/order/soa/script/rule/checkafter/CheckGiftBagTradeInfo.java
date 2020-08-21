
package com.asiainfo.veris.crm.order.soa.script.rule.checkafter;

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
 * @Description: 业务登记后条件判断:对不起，您已经办理了礼包业务，不能再次办理!【TradeCheckAfter】
 * @author: xiaocl
 */
public class CheckGiftBagTradeInfo extends BreBase implements IBREScript
{

    private static Logger logger = Logger.getLogger(CheckGiftBagTradeInfo.class);

    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        if (logger.isDebugEnabled())
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入 CheckGiftBagTradeInfo() >>>>>>>>>>>>>>>>>>");

        /* 逻辑单元节点定义区域 */
        boolean bResult = false;

        /* 总线相关信息包括台账信息资料信息获取 区域 */
        IDataset listUserOtherServ = databus.getDataset("TF_F_USER_OTHERSERV");
        /* 参数获取区域 */
        String strServiceMode = ruleParam.getString(databus, "SERVICE_MODE");

        /*
         * 分部判断区域
         */
        // 第一逻辑单元
        for (Iterator iter = listUserOtherServ.iterator(); iter.hasNext();)
        {
            IData userOtherServ = (IData) iter.next();
            if (userOtherServ.getString("PROCESS_TAG").equals("strProcessTag") && userOtherServ.getString("SERVICE_MODE").equals(strServiceMode))
            {
                bResult = true;
            }
        }

        if (logger.isDebugEnabled())
            logger.debug(" <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< 退出 CheckGiftBagTradeInfo() " + bResult + "<<<<<<<<<<<<<<<<<<<");

        return bResult;
    }

}
