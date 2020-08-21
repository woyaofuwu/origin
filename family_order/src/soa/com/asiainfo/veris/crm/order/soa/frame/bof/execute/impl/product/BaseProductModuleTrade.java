
package com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.product;

import java.util.List;

import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.ProductModuleData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.ProductModuleTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.IProductModuleTrade;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.product.util.ProductTimeEnv;

/**
 * @author Administrator
 */
public abstract class BaseProductModuleTrade implements IProductModuleTrade
{
    public ProductModuleTradeData createProductModuleTrade(ProductModuleData dealPmd, List<ProductModuleData> pmds, UcaData uca, BusiTradeData btd, ProductTimeEnv env) throws Exception
    {
        ProductModuleTradeData pmtd = createSubProductModuleTrade(dealPmd, pmds, uca, btd.getRD(), env);

        return pmtd;
    }

    public abstract ProductModuleTradeData createSubProductModuleTrade(ProductModuleData dealPmd, List<ProductModuleData> pmds, UcaData uca, BaseReqData brd, ProductTimeEnv env) throws Exception;
}
