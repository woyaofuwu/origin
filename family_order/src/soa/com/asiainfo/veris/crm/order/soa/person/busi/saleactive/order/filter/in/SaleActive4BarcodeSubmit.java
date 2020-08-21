
package com.asiainfo.veris.crm.order.soa.person.busi.saleactive.order.filter.in;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IFilterIn;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.shopping.ShoppingCartQry;
import com.asiainfo.veris.crm.order.soa.person.busi.saleactive.SaleActiveBean;

public class SaleActive4BarcodeSubmit implements IFilterIn
{

    @Override
    public void transferDataInput(IData input) throws Exception
    {
        String shoppingCartId = input.getString("SHOPPING_CART_ID");
        String orderId = input.getString("ORDER_ID");
        IDataset shoppingCartTrades = ShoppingCartQry.getShoppingOrderInfosByIds(shoppingCartId, orderId);
        IData shoppingCartOrderData = shoppingCartTrades.getData(0);
        IData requestData = new DataMap(shoppingCartOrderData.getString("REQUEST_DATA"));
        String productId = requestData.getString("PRODUCT_ID", "");
        input.put("PRODUCT_ID", productId);
        if (!"".equals(productId))
        {
            SaleActiveBean saleActiveBean = new SaleActiveBean();
            String campnType = saleActiveBean.getCampnType(productId);
            input.put("CAMPN_TYPE", campnType);
        }

        input.put("PACKAGE_ID", requestData.getString("PACKAGE_ID"));
        input.put("RES_ID", requestData.getString("RES_ID"));
        String resNo = requestData.getString("RES_CODE", "");
        if (!"".equals(resNo))
        {
            input.put("SALEGOODS_IMEI", resNo);
        }
        input.put("RES_CODE", resNo);
    }

}
