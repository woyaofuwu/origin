
package com.asiainfo.veris.crm.order.soa.person.busi.changeproduct.order.filter;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IFilterIn;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.shopping.ShoppingCartQry;

public class ChangeProduct4BarcodeSubmit implements IFilterIn
{

    @Override
    public void transferDataInput(IData input) throws Exception
    {
        IDataset shoppingPageElements = input.getDataset("SHOPPING_PAGE_ELEMENTS");
        String shoppingCartId = input.getString("SHOPPING_CART_ID");
        String orderId = input.getString("ORDER_ID");
        IDataset shoppingCartTrades = ShoppingCartQry.getShoppingOrderInfosByIds(shoppingCartId, orderId);
        IData shoppingCartOrderData = shoppingCartTrades.getData(0);
        IData requestData = new DataMap(shoppingCartOrderData.getString("REQUEST_DATA"));
        String productId = requestData.getString("NEW_PRODUCT_ID");
        input.putAll(requestData);
        /*
         * String disnctArray[] = requestData.getString("DISCNT").split(":");
         * if(CollectionUtils.isNotEmpty(shoppingPageElements)) { for(int index=0,size=shoppingPageElements.size();
         * index<size; index++) { String inputElementTag =
         * shoppingPageElements.getData(index).getString("SHOPPING_STATE_CODE");
         * if(ShoppingCartConst.SHOPPING_STATE_CODE_DEL.equals(inputElementTag)&& disnctArray.length>0) { String
         * inputElementId = shoppingPageElements.getData(index).getString("ELEMENT_ID"); for(int
         * j=0,s=disnctArray.length; j<s; j++) { String discntId = disnctArray[j]; if(inputElementId.equals(discntId)) {
         * disnctArray[j] = null; } } } } }
         */

        // input.put("DISCNT", StringUtils.join(disnctArray,":"));
    }
}
