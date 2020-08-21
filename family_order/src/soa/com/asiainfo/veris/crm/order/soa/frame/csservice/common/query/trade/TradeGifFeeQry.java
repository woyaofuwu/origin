
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class TradeGifFeeQry
{

    /**
     * 根据trade_id查询转预存受益号码信息
     * 
     * @param inparam
     * @param pagination
     * @return
     * @throws Exception
     */
    public static IDataset querySNByTradeId(IData inparam, Pagination pagination) throws Exception
    {
        IDataset dataset = Dao.qryByCode("TF_B_TRADEFEE_GIFTFEE", "SELT_TRADEFEE_GIFTFEE", inparam, pagination);
        return dataset;
    }
}
