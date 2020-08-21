
package com.asiainfo.veris.crm.order.soa.person.busi.shoppingcart.order.filter;

import org.apache.commons.collections.CollectionUtils;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IFilterIn;

public class ShoppingCartPageFilter implements IFilterIn
{

    @Override
    public void transferDataInput(IData pageInputParams) throws Exception
    {
        IDataset shoppingCartElements = new DatasetList(pageInputParams.getString("SHOPPINGCART_ELEMENTS"));
        if (CollectionUtils.isNotEmpty(shoppingCartElements))
        {
            for (int index = 0, size = shoppingCartElements.size(); index < size; index++)
            {
                IData shoppingCartElement = shoppingCartElements.getData(index);
                shoppingCartElement.remove("action");
            }
            pageInputParams.put("SHOPPINGCART_ELEMENTS", shoppingCartElements.toString());
        }
    }

}
