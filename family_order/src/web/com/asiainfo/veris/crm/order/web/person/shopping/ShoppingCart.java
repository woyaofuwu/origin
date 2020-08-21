
package com.asiainfo.veris.crm.order.web.person.shopping;

import org.apache.commons.collections.CollectionUtils;
import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;
import com.wade.web.v4.tapestry.component.tree.TreeParam;

public abstract class ShoppingCart extends PersonBasePage
{

    public void cleanShoppingCart(IRequestCycle requestCycle) throws Exception
    {
        IData data = this.getData();
        IDataset resultSet = CSViewCall.call(this, "SS.ShoppingCartSVC.cleanShoppingCart", data);
        setShoppingCartInfos(resultSet);
    }

    public void deleteItemRow(IRequestCycle requestCycle) throws Exception
    {
        IData data = this.getData();
        IDataset resultSet = CSViewCall.call(this, "SS.ShoppingCartSVC.deleteItemRow", data);
        setAjax(resultSet);
    }

    public void deleteOrderRow(IRequestCycle requestCycle) throws Exception
    {
        IData data = this.getData();
        IDataset resultSet = CSViewCall.call(this, "SS.ShoppingCartSVC.deleteOrderRow", data);
        setAjax(resultSet);
    }

    public void loadShoppingCart(IRequestCycle cycle) throws Exception
    {
        TreeParam treeParam = TreeParam.getTreeParam(cycle);
        String parentNodeId = treeParam.getParentNodeId();
        if (parentNodeId == null)
        {
            IData data = new DataMap();
            data.put("USER_ID", getParameter("USER_ID"));
            data.put("TRADE_STAFF_ID", this.getVisit().getStaffId());
            data.put(Route.ROUTE_EPARCHY_CODE, this.getParameter(Route.ROUTE_EPARCHY_CODE));
            IDataset shoppingCartSet = CSViewCall.call(this, "SS.ShoppingCartSVC.loadShoppingCartInfo", data);
            IData shoppingCartData = shoppingCartSet.getData(0);
            this.setShoppingCartData(shoppingCartData);
            data.put("SHOPPING_CART_ID", shoppingCartData.getString("SHOPPING_CART_ID"));
            IDataset nodes = CSViewCall.call(this, "SS.ShoppingCartSVC.loadShoppingCartNodes", data);
            setShoppingCartInfos(nodes);
            if (CollectionUtils.isNotEmpty(nodes))
            {
                int fee = 0;
                for (int index = 0, size = nodes.size(); index < size; index++)
                {
                    fee += Integer.parseInt(nodes.getData(index).getString("fee"));
                }
                IData shoppingFee = new DataMap();
                shoppingFee.put("FEE", fee);
                this.setShoppingFee(shoppingFee);
            }
            setAjax(data);
        }
        else if (!"finalNode".equals(parentNodeId))
        {
            IData data = new DataMap();
            data.put("ORDER_ID", parentNodeId);
            data.put(Route.ROUTE_EPARCHY_CODE, this.getParameter(Route.ROUTE_EPARCHY_CODE));
            IDataset childNodes = CSViewCall.call(this, "SS.ShoppingCartSVC.loadShoppingCartDetailNodes", data);
            setAjax(childNodes);
        }
    }

    public void selectGoods(IRequestCycle requestCycle) throws Exception
    {
        IData data = this.getData();
        data.put(Route.ROUTE_EPARCHY_CODE, this.getParameter(Route.ROUTE_EPARCHY_CODE));
        IDataset result = CSViewCall.call(this, "SS.BarcodeSVC.selectGoods", data);
        this.setAjax(result);
    }

    public abstract void setDiscntRuleSelect(IDataset discntRuleSelect);

    public abstract void setGoodsInfos(IDataset goodsInfos);

    public abstract void setShoppingCartData(IData shoppingCartData);

    public abstract void setShoppingCartInfo(IData shoppingCartInfo);

    public abstract void setShoppingCartInfos(IDataset shoppingCartInfos);

    public abstract void setShoppingFee(IData shoppingFee);

    public void submitShoppingCart(IRequestCycle requestCycle) throws Exception
    {
        IData data = this.getData();
        data.put("SERIAL_NUMBER", data.getString("AUTH_SERIAL_NUMBER"));
        IDataset result = CSViewCall.call(this, "SS.ShoppingCartRegSVC.tradeReg", data);
        this.setAjax(result);
    }

    public void tradeByBarcode(IRequestCycle requestCycle) throws Exception
    {
        IData data = this.getData();
        data.put(Route.ROUTE_EPARCHY_CODE, this.getParameter(Route.ROUTE_EPARCHY_CODE));
        IDataset result = CSViewCall.call(this, "SS.BarcodeSVC.tradeReg", data);
        this.setAjax(result);
    }

    public void tradeByBarcodeCheck(IRequestCycle requestCycle) throws Exception
    {
        IData data = this.getData();
        data.put(Route.ROUTE_EPARCHY_CODE, this.getParameter(Route.ROUTE_EPARCHY_CODE));
        IDataset result = CSViewCall.call(this, "SS.BarcodeSVC.tradeBeforeCheck", data);
        setGoodsInfos(result);
        this.setAjax(result);
    }

}
