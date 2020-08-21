
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class TradeUserInfoQrySVC extends CSBizService
{

    /**
     * @author chenyi
     * @Description 获取用户信息
     * @throws Exception
     * @param cycle
     */
    public IDataset getTradeUserByTradeIdForGrp(IData input) throws Exception
    {
        return TradeUserInfoQry.getTradeUserByTradeIdForGrp(input.getString("TRADE_ID"));
    }

}
