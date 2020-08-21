
package com.asiainfo.veris.crm.order.soa.person.busi.broadband.widenet.createwideuser.order.action.reg;

import java.util.List;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.data.TradeTableEnum;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.FeeTradeData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.AcctCall;

public class DealFeeAction implements ITradeAction
{

    /**
     * 更改费用userId 600,611,612,613费用登记同步缴费
     * 
     * @author chenzm
     */
    public void executeAction(BusiTradeData btd) throws Exception
    {

        String serialNumber = btd.getRD().getUca().getSerialNumber();
        String tradeFee = "0";
        String tradeFee2 = "0";
        String tradeFee3 = "0";
        String feeMode = "";
        String feeTypeCode = "";
        String channelId = "";
        String inModeCode = CSBizBean.getVisit().getInModeCode();
        if (serialNumber.substring(0, 3).equals("KD_"))
        {
            serialNumber = serialNumber.substring(3);
        }
        else
        {
            return;
        }
        IData userInfo = UcaInfoQry.qryUserInfoBySn(serialNumber);
        if (IDataUtil.isEmpty(userInfo))
        {
            return;
        }
        List<FeeTradeData> feeTradeDatas = btd.getTradeDatas(TradeTableEnum.TRADE_FEESUB);
        for (FeeTradeData feeTradeData : feeTradeDatas)
        {
            feeTradeData.setUserId(userInfo.getString("USER_ID"));
            feeMode = feeTradeData.getFeeMode();
            feeTypeCode = feeTradeData.getFeeTypeCode();
            if (feeMode.equals("2"))
            {
                if ("0".equals(feeTypeCode))
                {
                    tradeFee = String.valueOf(Integer.parseInt(tradeFee) + Integer.parseInt(feeTradeData.getFee()));
                }
                else if ("521".equals(feeTypeCode))
                {
                    tradeFee2 = String.valueOf(Integer.parseInt(tradeFee2) + Integer.parseInt(feeTradeData.getFee()));
                }else if ("200".equals(feeTypeCode))
                {
                    tradeFee3 = String.valueOf(Integer.parseInt(tradeFee3) + Integer.parseInt(feeTradeData.getFee()));
                }

            }

        }
        if ("2".equals(inModeCode))
        {
            channelId = "15001";
        }
        else if ("3".equals(inModeCode))
        {
            channelId = "15004";
        }
        else
        {
            channelId = "15000";
        }
        if (Integer.parseInt(tradeFee) > 0)
        {
            AcctCall.recvFee(serialNumber, btd.getTradeId(), tradeFee, channelId, "0", "0", "宽带开户预存缴费");
        }
        if (Integer.parseInt(tradeFee2) > 0)
        {
            AcctCall.recvFee(serialNumber, btd.getTradeId(), tradeFee2, channelId, "521", "0", "宽带开户光猫预存缴费");
        }
        if (Integer.parseInt(tradeFee3) > 0)
        {
            AcctCall.recvFee(serialNumber, btd.getTradeId(), tradeFee3, channelId, "200", "0", "铁通ADSL宽带预存缴费");
        }
    }
}
