
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

/**
 * @author chenkh 作用：用于用trade_id查询历史表信息 2014-8-26
 */
public class TradeBhQrySVC extends CSBizService
{
    private static final long serialVersionUID = 1L;

    public static IDataset qryTradeHistoryInfo(IData param) throws Exception
    {
        return TradeBhQry.queryTradeInfoByTradeId(param.getString("TRADE_ID"));
    }
}
