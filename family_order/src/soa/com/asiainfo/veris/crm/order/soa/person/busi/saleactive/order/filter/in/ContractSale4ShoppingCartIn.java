
package com.asiainfo.veris.crm.order.soa.person.busi.saleactive.order.filter.in;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IFilterIn;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.shopping.ShoppingCartQry;

public class ContractSale4ShoppingCartIn implements IFilterIn
{

    @Override
    public void transferDataInput(IData input) throws Exception
    {
        String shoppingCartId = input.getString("SHOPPING_CART_ID");
        String orderId = input.getString("ORDER_ID");
        IDataset shoppingCartTrades = ShoppingCartQry.getShoppingOrderInfosByIds(shoppingCartId, orderId);
        IData shoppingCartTradeData = shoppingCartTrades.getData(0);
        IData requestData = new DataMap(shoppingCartTradeData.getString("REQUEST_DATA"));
        requestData.remove("SUBMIT_TYPE");
        input.clear();
        input.putAll(requestData);
    }

}
