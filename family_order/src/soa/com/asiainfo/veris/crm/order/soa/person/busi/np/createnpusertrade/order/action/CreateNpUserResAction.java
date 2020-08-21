
package com.asiainfo.veris.crm.order.soa.person.busi.np.createnpusertrade.order.action;

import java.util.List;

import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.data.TradeTableEnum;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.ResTradeData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.ResCall;

public class CreateNpUserResAction implements ITradeAction
{

    @Override
    public void executeAction(BusiTradeData btd) throws Exception
    {
        String tradeId = btd.getTradeId();
        String userId = btd.getRD().getUca().getUserId();
        String serialNumber = btd.getRD().getUca().getSerialNumber();
        List<ResTradeData> resDatas = btd.getTradeDatas(TradeTableEnum.TRADE_RES);
        String sim = "";
        if (resDatas != null)
        {
            for (ResTradeData resData : resDatas)
            {
                if ("1".equals(resData.getResTypeCode()) && "0".equals(resData.getModifyTag()))
                {
                    sim = resData.getResCode();
                    ResCall.modifyNpSimInfo(sim, "2", serialNumber, tradeId, userId,"40");// sim卡占用，携号在登记时占用
                    break;
                }
            }
        }

    }

}
