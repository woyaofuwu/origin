
package com.asiainfo.veris.crm.order.soa.person.busi.score.scoreinterface.order.filter;

import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IFilterOut;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.MainTradeData;
import com.asiainfo.veris.crm.order.soa.person.busi.score.scoreinterface.order.requestdata.ScoreGiftReqData;

public class GiftOutFilter implements IFilterOut
{

    @Override
    public IData transfterDataOut(IData input, BusiTradeData btd) throws Exception
    {
        ScoreGiftReqData reqData = (ScoreGiftReqData) btd.getRD();
        MainTradeData mainList = btd.getMainTradeData();
        int balanceScore = Integer.parseInt(mainList.getRsrvStr1().split("\\^")[0]) + Integer.parseInt(mainList.getRsrvStr2());
        int totalPoint = Integer.parseInt(mainList.getRsrvStr1().split("\\^")[1]) + Integer.parseInt(mainList.getRsrvStr2());
        // 设置交易成功数据
        IData succData = new DataMap();
        succData.put("TRADE_SEQ", reqData.getTRADE_SEQ());
        succData.put("ORGID", reqData.getORGID());
        succData.put("MOBILE", btd.getRD().getUca().getSerialNumber());
        succData.put("ADD_POINT", reqData.getADD_POINT());
        succData.put("VALIDATE_TIME", reqData.getVALIDATE_TIME());
        succData.put("POINT_BALANCE", balanceScore);
        succData.put("TOTAL_POINT", totalPoint);
        return succData;
    }

}
