
package com.asiainfo.veris.crm.order.soa.frame.csservice.person.busi.shoppingcart.order.filter;

import org.apache.commons.collections.CollectionUtils;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IFilterIn;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.ShoppingCartConst;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.shopping.ShoppingCartQry;

public abstract class ShoppingCartSelectedElementFilter implements IFilterIn
{

    public abstract void transferByMySelf(IData input) throws Exception;

    @Override
    public void transferDataInput(IData input) throws Exception
    {
        IDataset shoppingPageElements = input.getDataset("SHOPPING_PAGE_ELEMENTS");
        String shoppingCartId = input.getString("SHOPPING_CART_ID");
        String orderId = input.getString("ORDER_ID");
        IDataset shoppingCartTrades = ShoppingCartQry.getShoppingOrderInfosByIds(shoppingCartId, orderId);
        IData shoppingCartOrderData = shoppingCartTrades.getData(0);
        IData requestData = new DataMap(shoppingCartOrderData.getString("REQUEST_DATA"));
        IDataset orderSelectedElements = new DatasetList(requestData.getString("SELECTED_ELEMENTS"));

        if (CollectionUtils.isNotEmpty(shoppingPageElements))
        {
            for (int index = 0, size = shoppingPageElements.size(); index < size; index++)
            {
                String inputElementTag = shoppingPageElements.getData(index).getString("SHOPPING_STATE_CODE");
                if (ShoppingCartConst.SHOPPING_STATE_CODE_DEL.equals(inputElementTag) && CollectionUtils.isNotEmpty(orderSelectedElements))
                {
                    String inputElementId = shoppingPageElements.getData(index).getString("ELEMENT_ID");
                    for (int j = 0, s = orderSelectedElements.size(); j < s; j++)
                    {
                        String shoppingElementId = orderSelectedElements.getData(j).getString("ELEMENT_ID");
                        if (inputElementId.equals(shoppingElementId))
                        {
                            orderSelectedElements.remove(j);
                            s--;
                        }
                    }
                }
            }
        }

        if (CollectionUtils.isNotEmpty(orderSelectedElements))
        {
            for (int j = 0, s = orderSelectedElements.size(); j < s; j++)
            {
                IData shoppingCartElement = orderSelectedElements.getData(j);
                // String elementTypeCode = shoppingCartElement.getString("ELEMENT_TYPE_CODE");
                shoppingCartElement.remove("START_DATE");
                shoppingCartElement.remove("END_DATE");
            }
        }

        requestData.put("SELECTED_ELEMENTS", orderSelectedElements);
        requestData.remove("SUBMIT_TYPE");
        input.clear();
        input.putAll(requestData);
    }

}
