
package com.asiainfo.veris.crm.order.soa.person.busi.score.scoreinterface.order.filter;

import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IFilterOut;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.person.busi.score.scoreinterface.order.requestdata.ScoreDonateRollBackRequestData;

/**
 * 积分回退输出转换
 * 
 * @author huangsl
 */
public class ScoreDonateRollBackOutFilter implements IFilterOut
{

    @Override
    public IData transfterDataOut(IData input, BusiTradeData btd) throws Exception
    {
        ScoreDonateRollBackRequestData reqData = (ScoreDonateRollBackRequestData)btd.getRD();
        IData resultInfo = new DataMap();
        resultInfo.put("SERIAL_NUMBER", reqData.getSERIAL_NUMBER());
        resultInfo.put("TRADE_SEQ", reqData.getTRADE_SEQ());
        resultInfo.put("SCORE_VALUE", reqData.getSCORE_VALUE());
        return resultInfo;
    }

}
