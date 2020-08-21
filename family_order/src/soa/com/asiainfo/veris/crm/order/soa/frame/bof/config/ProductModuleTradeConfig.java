
package com.asiainfo.veris.crm.order.soa.frame.bof.config;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.ProductModuleData;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.IProductModuleTrade;
import com.asiainfo.veris.crm.order.soa.frame.bof.query.BofQuery;

/**
 * @author Administrator
 */
public class ProductModuleTradeConfig
{
    private static IData productTrade = new DataMap();

    static
    {
        try
        {
            // 获取产品模型附加动作信息
            IDataset trades = BofQuery.getAllProductModuleTrade();
            int size = trades.size();
            // 加载到缓存里
            for (int i = 0; i < size; i++)
            {
                try
                {
                    String key = null;
                    IData trade = trades.getData(i);
                    String configElementTypeCode = trade.getString("ELEMENT_TYPE_CODE");
                    String className = trade.getString("CLASS_NAME").trim();

                    key = configElementTypeCode;
                    IProductModuleTrade productModuleTrade = (IProductModuleTrade) Class.forName(className).newInstance();
                    productTrade.put(key, productModuleTrade);
                }
                catch (Exception e)
                {

                }
            }

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {

        }
    }

    public static IProductModuleTrade getProductModuleTrade(ProductModuleData pmd) throws Exception
    {
        IProductModuleTrade productModuleTrade = null;
        productModuleTrade = (IProductModuleTrade) productTrade.get(pmd.getElementType());

        return productModuleTrade;
    }
}
