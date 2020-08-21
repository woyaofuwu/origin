
package com.asiainfo.veris.crm.order.soa.person.busi.score.scoreinterface.order.filter;

import net.sf.json.JSONArray;

import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IFilterOut;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.MainTradeData;
import com.asiainfo.veris.crm.order.soa.person.busi.score.scoreinterface.order.requestdata.ScorePayRollBackReqData;

public class PayRollBackOutFilter implements IFilterOut
{

    @Override
    public IData transfterDataOut(IData input, BusiTradeData btd) throws Exception
    {
        ScorePayRollBackReqData reqData = (ScorePayRollBackReqData) btd.getRD();
        MainTradeData mainList = btd.getMainTradeData();
        int balanceScore = Integer.parseInt(mainList.getRsrvStr1().split("\\^")[0]) + Integer.parseInt(mainList.getRsrvStr2());
        int totalPoint = Integer.parseInt(mainList.getRsrvStr1().split("\\^")[1]) - Integer.parseInt(mainList.getRsrvStr2());
        // 设置交易成功数据
        IData succData = new DataMap();
        succData.put("TRADE_SEQ", reqData.getTRADE_SEQ());
        succData.put("ORGID", reqData.getORGID());
        succData.put("MOBILE", btd.getRD().getUca().getSerialNumber());
        succData.put("P_TRADE_SEQ", reqData.getP_TRADE_SEQ());
        succData.put("REFUND_POINT", reqData.getREFUND_POINT());
        succData.put("POINT_BALANCE", balanceScore);
        succData.put("CONSUME_POINT", mainList.getRsrvStr3().split("\\^")[0]);
        succData.put("PROMOTION_POINT", mainList.getRsrvStr3().split("\\^")[1]);
		JSONArray scoreInfosTimes =new JSONArray().fromObject(mainList.getRsrvStr6());
		JSONArray scoreInfosPoints =new JSONArray().fromObject(mainList.getRsrvStr7());
		succData.put("VALIDATE_TIME", scoreInfosTimes);
        succData.put("P_PROMOTION_POINT", scoreInfosPoints);
        succData.put("TOTAL_POINT", totalPoint);
        return succData;
    }

}
