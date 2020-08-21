
package com.asiainfo.veris.crm.order.soa.person.busi.changeproduct.order.filter;

import org.apache.commons.collections.CollectionUtils;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IFilterIn;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.ShoppingCartConst;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.shopping.ShoppingCartQry;

public class ChangeElement4BarcodeSubmit implements IFilterIn
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
        String elementIdArray[] = requestData.getString("ELEMENT_ID").split(",");
        String elementTagArray[] = requestData.getString("MODIFY_TAG").split(",");
        String elementTypeCodeArray[] = requestData.getString("ELEMENT_TYPE_CODE").split(",");

        if (CollectionUtils.isNotEmpty(shoppingPageElements))
        {
            for (int index = 0, size = shoppingPageElements.size(); index < size; index++)
            {
                String inputElementTag = shoppingPageElements.getData(index).getString("SHOPPING_STATE_CODE");
                if (ShoppingCartConst.SHOPPING_STATE_CODE_DEL.equals(inputElementTag) && elementIdArray.length > 0)
                {
                    String inputElementId = shoppingPageElements.getData(index).getString("ELEMENT_ID");
                    for (int j = 0, s = elementIdArray.length; j < s; j++)
                    {
                        String shoppingElementId = elementIdArray[j];
                        if (inputElementId.equals(shoppingElementId))
                        {
                            elementIdArray[j] = null;
                            elementTagArray[j] = null;
                            elementTypeCodeArray[j] = null;
                        }
                    }
                }
            }
        }
        input.put("ELEMENT_ID", StringUtils.join(elementIdArray, ","));
        input.put("ELEMENT_TYPE_CODE", StringUtils.join(elementTypeCodeArray, ","));
        input.put("MODIFY_TAG", StringUtils.join(elementTagArray, ","));
        input.put("NUM", elementIdArray.length);
        IFilterIn changeProductFilter = new ChangeProductFilter();
        changeProductFilter.transferDataInput(input);
    }

}
