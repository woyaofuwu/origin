
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class TradeExtInfoQrySVC extends CSBizService
{

    private static final long serialVersionUID = 1L;

    /**
     * @author chenyi
     * @Description 根据trade-id 查询esop信息
     * @throws Exception
     * @param cycle
     */
    public IDataset getTradeEsopInfoTradeId(IData input) throws Exception
    {
        return TradeExtInfoQry.getTradeEsopInfoTradeId(input.getString("TRADE_ID", ""));
    }

    /**
     * @param inparams
     * @param page
     * @author weixb3
     * @return
     * @throws Exception
     */
    public IDataset getTradeExtForEsop(IData input) throws Exception
    {
        IDataset data = TradeExtInfoQry.getTradeExtForEsop(input.getString("TRADE_ID"));
        return data;
    }

    /**
     * 根据tradeId 查询 TF_B_POTRADE_STATE数据
     * 
     * @author weixb3
     * @param trade_id
     * @return
     * @throws Exception
     */
    public IDataset queryPotradeStateByTradeIdForEsop(IData input) throws Exception
    {
        return TradeExtInfoQry.queryPotradeStateByTradeIdForEsop(input.getString("TRADE_ID"));
    }

    /**
     * 根据ibisysid 查询tf_b_trade_ext 表
     * 
     * @author weixb3
     * @param ibsys_id
     * @return
     * @throws Exception
     */
    public IDataset queryTradeExtForEsop(IData input) throws Exception
    {
        return TradeExtInfoQry.queryTradeExtForEsop(input.getString("IBSYSID"));
    }
}
