
package com.asiainfo.veris.crm.order.soa.person.busi.score.scoreinterface.order.filter;

import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IFilterOut;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.MainTradeData;
import com.asiainfo.veris.crm.order.soa.person.busi.score.scoreinterface.order.requestdata.ScoreReviseNReqData;

public class ReviseNOutFilter implements IFilterOut
{

    @Override
    public IData transfterDataOut(IData input, BusiTradeData btd) throws Exception
    {
        ScoreReviseNReqData reqData = (ScoreReviseNReqData) btd.getRD();
        MainTradeData mainList = btd.getMainTradeData();
        int balanceScore = 0;
        if("01".equals(reqData.getOPR_TYPE())){
        	balanceScore = Integer.parseInt(mainList.getRsrvStr1()) + Integer.parseInt(mainList.getRsrvStr2());
        }else{
        	balanceScore = Integer.parseInt(mainList.getRsrvStr1()) - Integer.parseInt(mainList.getRsrvStr2());
        }
        // 设置交易成功数据
        IData succData = new DataMap();
        succData.put("TRADE_SEQ", reqData.getTRADE_SEQ());
        succData.put("ORGID", reqData.getORGID());
        succData.put("MOBILE", btd.getRD().getUca().getSerialNumber());
        succData.put("OPR_TYPE", reqData.getOPR_TYPE());
        succData.put("REVISE_POINT", reqData.getREVISE_POINT());
        succData.put("POINT_BALANCE", balanceScore);
        return succData;
    }

}
