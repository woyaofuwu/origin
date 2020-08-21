
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class ProductFeeInfoQrySVC extends CSBizService
{
    private static final long serialVersionUID = 1L;

    public IDataset getFeeByTradeFeeMode(IData input) throws Exception
    {
        return ProductFeeInfoQry.getFeeByTradeFeeMode(input.getString("TRADE_TYPE_CODE"), input.getString("PRODUCT_ID"), input.getString("PACKAGE_ID"), input.getString("ELEMENT_ID"), input.getString("ELEMENT_TYPE_CODE"), input.getString("FEE_MODE"),
                input.getString("EPARCHY_CODE"));
    }

    public IDataset getProductFeeInfo(IData input) throws Exception
    {
        return ProductFeeInfoQry.getProductFeeInfo(input.getString("TRADE_TYPE_CODE"), input.getString("PRODUCT_ID"), input.getString("PACKAGE_ID"), input.getString("ELEMENT_ID"), input.getString("ELEMENT_TYPE_CODE"), input
                .getString("TRADE_FEE_TYPE"), input.getString("EPARCHY_CODE"));
    }

    public IDataset qryTradeTypeFeeForGrp(IData input) throws Exception
    {
        return ProductFeeInfoQry.getGrpTradeTypeFee(input);
    }
}
