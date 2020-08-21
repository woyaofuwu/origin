
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class TradeGrpPkgInfoQrySVC extends CSBizService
{

    private static final long serialVersionUID = 1L;

    /**
     * @author weixb3
     * @Description 查询集团定制的包中Trade相关优惠元素
     * @throws Exception
     * @param cycle
     */
    public IDataset getGrpCustomizeDiscntByTradeId(IData input) throws Exception
    {
        return TradeGrpPkgInfoQry.getGrpCustomizeDiscntByTradeId(input.getString("TRADE_ID"), getPagination());
    }

    /**
     * @author weixb3
     * @Description 查询集团定制的包中Trade相关服务元素
     * @throws Exception
     * @param cycle
     */
    public IDataset getGrpCustomizeServByTradeId(IData input) throws Exception
    {
        return TradeGrpPkgInfoQry.getGrpCustomizeServByTradeId(input.getString("TRADE_ID"), input.getString("USER_ID"), getPagination());
    }

    /**
     * @author weixb3
     * @Description 查询集团定制的包中相关TRADEID服务元素
     * @throws Exception
     * @param cycle
     */
    public IDataset getGrpCustomizeSpByTradeId(IData input) throws Exception
    {
        return TradeGrpPkgInfoQry.getGrpCustomizeSpByTradeId(input.getString("TRADE_ID"), getPagination());
    }
}
