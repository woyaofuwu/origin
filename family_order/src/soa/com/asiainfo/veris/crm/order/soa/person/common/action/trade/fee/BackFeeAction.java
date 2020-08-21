/**
 * 
 */

package com.asiainfo.veris.crm.order.soa.person.common.action.trade.fee;

import java.util.List;

import com.ailk.biz.BizEnv;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.data.TradeTableEnum;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.databus.OrderDataBus;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.databus.util.DataBusManager;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.FeeTradeData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.AcctCall;

/**
 * Copyright: Copyright (c) 2014 Asiainfo-Linkage
 * 
 * @ClassName: BackFeeAction.java
 * @Description: 该类的功能描述
 * @version: v1.0.0
 * @author: chengxf2
 * @date: 2014-9-8 下午06:10:23 Modification History: Date Author Version Description
 *        ------------------------------------------------------------* 2014-9-8 chengxf2 v1.0.0 修改原因
 */

public class BackFeeAction implements ITradeAction
{

    /**
     * @Function:
     * @Description: 该方法的描述
     * @param:
     * @return：
     * @throws：异常描述
     * @version: v1.0.0
     * @author: chengxf2
     * @date: 2014-9-8 下午06:10:23 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2014-9-8 chengxf2 v1.0.0 修改原因
     */
    public void executeAction(BusiTradeData btd) throws Exception
    {
        boolean nowRunFlag = BizEnv.getEnvBoolean("crm.merch.addShoppingCart", false); // 加购物车跳出此段逻辑,加个开关方便控制
        if (nowRunFlag)
        {
            OrderDataBus dataBus = DataBusManager.getDataBus();
            String submitType = dataBus.getSubmitType();// addShoppingCart
            if (StringUtils.equals(BofConst.SUBMIT_TYPE_SHOPPING_CART, submitType))
            {
                return;
            }
        }
        
        String inModeCode = btd.getMainTradeData().getInModeCode();
        String tradeId = btd.getTradeId();
        String userId = btd.getMainTradeData().getUserId();
        String channelId = "15000";
        if ("2".equals(inModeCode))
        {
            channelId = "15001";
        }
        else if ("3".equals(inModeCode))
        {
            channelId = "15004";
        }
        List<FeeTradeData> feeTradeList = btd.getTradeDatas(TradeTableEnum.TRADE_FEESUB);
        for (FeeTradeData feeTrade : feeTradeList)
        {
            String feeMode = feeTrade.getFeeMode();
            String fee = feeTrade.getFee();
            if (!StringUtils.equals("2", feeMode))
            {
                continue;
            }

            if (Integer.parseInt(fee) >= 0)
            {
                continue;
            }
            String paymentId = feeTrade.getFeeTypeCode();
            fee = (-1) * (Integer.parseInt(fee)) + "";
            AcctCall.backFee(userId, tradeId, channelId, paymentId, "16001", fee);
        }

    }

}
