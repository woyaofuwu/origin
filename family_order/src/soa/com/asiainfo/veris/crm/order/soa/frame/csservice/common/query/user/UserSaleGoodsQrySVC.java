
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class UserSaleGoodsQrySVC extends CSBizService
{

    private static final long serialVersionUID = 1L;

    public IDataset getByRelationTradeId(IData input) throws Exception
    {
        String relationTradeId = input.getString("RELATION_TRADE_ID");
        return UserSaleGoodsInfoQry.getByRelationTradeId(relationTradeId);
    }

    public IDataset getPurchaseInfoByUserId(IData input) throws Exception
    {
        String user_id = input.getString("USER_ID");
        String goods_state = input.getString("GOODS_STATE");
        IDataset output = UserSaleActiveInfoQry.getPurchaseInfoByUserId(user_id, goods_state, null);
        return output;
    }

    public IDataset qrySaleActiveByUserId(IData input) throws Exception
    {
        String userId = input.getString("USER_ID");
        return UserSaleActiveInfoQry.qrySaleActiveByUserId(userId);
    }
}
