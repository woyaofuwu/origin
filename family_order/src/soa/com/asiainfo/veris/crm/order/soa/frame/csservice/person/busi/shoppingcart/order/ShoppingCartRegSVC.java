
package com.asiainfo.veris.crm.order.soa.frame.csservice.person.busi.shoppingcart.order;

import java.util.List;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.ShoppingCartConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.shoppingcart.BusiOrderModuleData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.service.OrderService;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.shopping.ShoppingCartQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.person.busi.shoppingcart.order.requestdata.BaseShoppingCartReqData;

public class ShoppingCartRegSVC extends OrderService
{
    private static final long serialVersionUID = -2669181254667443684L;

    @Override
    public String getOrderTypeCode() throws Exception
    {
        return ShoppingCartConst.ORDER_TYPE_CODE_SHOPPINGCART;
    }

    @Override
    public String getTradeTypeCode() throws Exception
    {
        return ShoppingCartConst.TRADE_TYPE_CODE_SHOPPINGCART;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void otherTradeDeal(IData input, BusiTradeData btd) throws Exception
    {
        BaseShoppingCartReqData baseShoppingCartReqData = (BaseShoppingCartReqData) btd.getRD();
        List<BusiOrderModuleData> busiOrderDataList = baseShoppingCartReqData.getBusiOrderList();
        IDataset returnSet = new DatasetList();
        String shoppingCartId = "";
        for (int index = 0, size = busiOrderDataList.size(); index < size; index++)
        {
            IData orderInputData = new DataMap();
            BusiOrderModuleData busiOrderData = busiOrderDataList.get(index);
            orderInputData.put("SERIAL_NUMBER", input.getString("AUTH_SERIAL_NUMBER"));
            shoppingCartId = busiOrderData.getShoppingCartId();
            orderInputData.put("SHOPPING_CART_ID", shoppingCartId);
            orderInputData.put("ORDER_ID", busiOrderData.getOrderId());
            orderInputData.put("CALL_TYPE", "SHOPPING_CART");
            orderInputData.put("SHOPPING_PAGE_ELEMENTS", busiOrderData.getPageInputData().getDataset("SHOPPING_PAGE_ELEMENTS"));
            String tradeRegSvc = busiOrderData.getOrderRegSvc();
            returnSet = CSAppCall.call(tradeRegSvc, orderInputData);
        }

        String orderId = returnSet.getData(0).getString("ORDER_ID");
        ShoppingCartQry.updateShoppingCartState(shoppingCartId, orderId);
        IData inputData = new DataMap();
        inputData.put("SERIAL_NUMBER", input.getString("AUTH_SERIAL_NUMBER"));
        inputData.put("SHOPPING_CART_ID", shoppingCartId);
        inputData.put("SHOPPING_OPER_TYPE", ShoppingCartConst.SHOPPING_STATE_CODE_SUBMIT);
        CSAppCall.call("SS.ShoppingCartSVC.dealTradeByCartId", inputData);
    }

}
