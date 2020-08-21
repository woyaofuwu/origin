
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class TradeProductInfoQrySVC extends CSBizService
{

    private static final long serialVersionUID = 1L;

    /**
     * @Description: 根据集团BBOSS产品订单号和同步序列号 查询出 跨省工单状态产品表(TF_B_PRODUCTTRADE)
     * @author weixb3
     * @date
     * @param
     * @param
     * @param
     * @return
     * @throws Exception
     */
    public IDataset queryProductTrade(IData input) throws Exception
    {
        String SYNC_SEQUENCE = input.getString("SYNC_SEQUENCE");
        String ORDER_NUMBER = input.getString("ORDER_NUMBER");
        return TradeProductInfoQry.queryProductTrade(SYNC_SEQUENCE, ORDER_NUMBER);
    }

}
