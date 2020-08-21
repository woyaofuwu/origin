
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class TradeSvcInfoQrySVC extends CSBizService
{

    private static final long serialVersionUID = 1L;

    /**
     * @author weixb3
     * @Description 获取元素表单包Tarde
     * @throws Exception
     * @param cycle
     */
    public IDataset getElementFromPackageTrade(IData input) throws Exception
    {
        return TradeSvcInfoQry.getElementFromPackageTrade(input.getString("TRADE_ID"), input.getString("PRODUCT_ID"), input.getString("USER_ID"), input.getString("BBOSS_FLAG"), getPagination());
    }

}
