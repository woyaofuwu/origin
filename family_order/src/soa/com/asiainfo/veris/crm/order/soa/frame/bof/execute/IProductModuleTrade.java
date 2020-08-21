
package com.asiainfo.veris.crm.order.soa.frame.bof.execute;

import java.util.List;

import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.ProductModuleData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.ProductModuleTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.product.util.ProductTimeEnv;

/**
 * @author Administrator
 */
public interface IProductModuleTrade
{
    public ProductModuleTradeData createProductModuleTrade(ProductModuleData dealPmd, List<ProductModuleData> pmds, UcaData uca, BusiTradeData btd, ProductTimeEnv env) throws Exception;
}
