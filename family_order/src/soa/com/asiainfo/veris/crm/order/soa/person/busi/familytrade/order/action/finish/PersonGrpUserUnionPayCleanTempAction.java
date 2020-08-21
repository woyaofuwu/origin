package com.asiainfo.veris.crm.order.soa.person.busi.familytrade.order.action.finish;

import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeFinishAction;

/**
 * 
 * @author chenzg
 *
 */
public class PersonGrpUserUnionPayCleanTempAction implements ITradeFinishAction
{

    @Override
    public void executeAction(IData mainTrade) throws Exception
    {
        String tradeId = mainTrade.getString("TRADE_ID");
        if (!StringUtils.isBlank(tradeId))
        {
            IData param = new DataMap();
            param.put("TRADE_ID", tradeId);
            String sql = "DELETE FROM TP_F_UNIONPAY_MEMBER WHERE TRADE_ID=:TRADE_ID ";
            Dao.executeUpdate(new StringBuilder(sql), param);
        }
    }

}
