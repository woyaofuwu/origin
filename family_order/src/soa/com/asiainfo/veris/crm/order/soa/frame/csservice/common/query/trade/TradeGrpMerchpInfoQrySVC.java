
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class TradeGrpMerchpInfoQrySVC extends CSBizService
{

    private static final long serialVersionUID = 1L;

    /**
     * 根据userID和tradeId查询merchp表信息
     * 
     * @param inparam
     * @return
     * @throws Exception
     */
    public static IDataset qryGrpMerchpByUserIdAndTradeId(IData inparam) throws Exception
    {

        String user_id = inparam.getString("USER_ID");
        String trade_id = inparam.getString("TRADE_ID");

        return TradeGrpMerchpInfoQry.qryGrpMerchpByUserIdAndTradeId(user_id, trade_id);
    }

    /**
     * @author weixb3
     * @Description:根据商品订购关系找台帐编码
     * @throws Exception
     * @param cycle
     */
    public IDataset getMerchpOnlineByProductofferId(IData input) throws Exception
    {

        return TradeGrpMerchpInfoQry.getMerchpOnlineByProductofferId(input.getString("PRODUCT_OFFER_ID"), getPagination());
    }

    /**
     * 根据trade_id查询tf_b_trade_grp_merchp表信息
     * 
     * @param inparams
     * @param page
     * @author weixb3
     * @return
     * @throws Exception
     */
    public IDataset qryMerchpInfoByTradeId(IData input) throws Exception
    {
        return TradeGrpMerchpInfoQry.qryMerchpInfoByTradeId(input.getString("TRADE_ID"));
    }

    /**
     * @author weixb3
     * @Description 根据trade_id和userIds（eg:1114073022800966,1114073022800967）查询TF_B_TRADE_GRP_MERCHP表信息，带Pagination
     * @throws Exception
     * @param cycle
     */
    public IDataset qryMerchpInfoByTradeIdUserIds(IData input) throws Exception
    {

        return TradeGrpMerchpInfoQry.qryMerchpInfoByTradeIdUserIds(input.getString("TRADE_ID", ""), input.getString("BBOSS_USER_ID", ""), getPagination());
    }

    /**
     * 根据PRODUCT_OFFER_ID、TRADE_ID 更新TF_B_TRADE_GRP_MERCHP表PRODUCT_ORDER_ID字段
     * 
     * @author ft
     * @param input
     * @return
     * @throws Exception
     */
    public void updateMerchpOrderIdByTradeIdOfferId(IData input) throws Exception
    {
        TradeGrpMerchpInfoQry.updateMerchpOrderIdByTradeIdOfferId(input);
    }

    /**
     * @author ft
     * @Description:根据TRADE_ID 更新TF_B_TRADE_GRP_MERCHP表STATUS字段
     * @throws Exception
     * @param cycle
     */
    public void updateMerchpStatusByTradeId(IData input) throws Exception
    {

        TradeGrpMerchpInfoQry.updateMerchpStatusByTradeId(input.getString("TRADE_ID"), input.getString("STATUS"));
    }
}
