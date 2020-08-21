
package com.asiainfo.veris.crm.order.soa.person.busi.createfixteluser.order.action;

import java.util.List;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.data.TradeTableEnum;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.FeeTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.OtherFeeTradeData;

public class TradeAgentFeeAction implements ITradeAction
{

    public void executeAction(BusiTradeData btd) throws Exception
    {
        List<FeeTradeData> feeTradeDatas = btd.getTradeDatas(TradeTableEnum.TRADE_FEESUB);
        int advanceFee = 0;
        for (FeeTradeData feeTradeData : feeTradeDatas)
        {
            if ("2".equals(feeTradeData.getFeeMode()))
            {
                advanceFee = advanceFee + Integer.parseInt(feeTradeData.getFee());
            }

        }

        if (advanceFee > 0)
        {
            // CreateTDPersonUserRequestData createUserRD = (CreateTDPersonUserRequestData) btd.getRD();
            String serialNumber = btd.getRD().getUca().getUser().getSerialNumber();

            // String serial_number = createUserRD.getInPhone();
            IData userInfos = UcaInfoQry.qryUserInfoBySn(serialNumber);

            OtherFeeTradeData otherFeeTD = new OtherFeeTradeData();
            otherFeeTD.setUserId(userInfos.getString("USER_ID"));
            otherFeeTD.setOperFee(String.valueOf(advanceFee));
            otherFeeTD.setOperType(BofConst.OTHERFEE_AGENT);
            otherFeeTD.setPaymentId("0");
            otherFeeTD.setStartDate(SysDateMgr.getSysDate());
            otherFeeTD.setEndDate(SysDateMgr.getTheLastTime());
            btd.add(serialNumber, otherFeeTD);
        }

    }

}
