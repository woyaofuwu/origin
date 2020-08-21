
package com.asiainfo.veris.crm.order.soa.frame.csservice.person.busi.shoppingcart.order.trade;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.shoppingcart.BusiOrderModuleData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.ITrade;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.BaseTrade;
import com.asiainfo.veris.crm.order.soa.frame.csservice.person.busi.shoppingcart.order.requestdata.BaseShoppingCartReqData;

public class ShoppingCartTrade extends BaseTrade implements ITrade
{

    @SuppressWarnings("unchecked")
    @Override
    public void createBusiTradeData(BusiTradeData btd) throws Exception
    {
        BaseShoppingCartReqData baseShoppingCartReqData = (BaseShoppingCartReqData) btd.getRD();
        List<BusiOrderModuleData> busiOrderDataList = baseShoppingCartReqData.getBusiOrderList();
        if (CollectionUtils.isNotEmpty(busiOrderDataList))
        {
            for (int index = 0, size = busiOrderDataList.size(); index < size; index++)
            {
                BusiOrderModuleData btmd = busiOrderDataList.get(index);
                IDataset elementBusiDataset = btmd.elementListToDataset();
                IData pageInputData = new DataMap();
                pageInputData.put("SHOPPING_PAGE_ELEMENTS", elementBusiDataset);
                btmd.setPageInputData(pageInputData);
            }
        }
    }

}
