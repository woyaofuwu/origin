
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.shopping;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class ShoppingCartQry extends CSBizBean
{
    public static int cleanShoppingCartDetail(String shoppingCartId) throws Exception
    {
        IData param = new DataMap();
        param.put("SHOPPING_CART_ID", shoppingCartId);
        return Dao.executeUpdateByCodeCode("TF_B_SHOPPING_CART_DETAIL", "UPD_CLEAN_BY_CID", param);
    }

    public static int deleteRow(String tabName, String sqlRef, IData param) throws Exception
    {
        return Dao.executeUpdateByCodeCode("" + tabName, "" + sqlRef, param);
    }

    public static void deleteTradeByOrderId(String orderId) throws Exception
    {
        IData param = new DataMap();
        param.put("ORDER_ID", orderId);
        Dao.executeUpdateByCodeCode("TF_B_SHOPPING_CART_DETAIL", "DEL_TRADE_BY_ORDER", param);
    }

    public static IDataset getAllShoppingDetailByCartId(String shoppingCartId) throws Exception
    {
        IData param = new DataMap();
        param.put("SHOPPING_CART_ID", shoppingCartId);
        return Dao.qryByCode("TF_B_SHOPPING_CART_DETAIL", "SEL_ALL_BY_ID", param);
    }

    public static IDataset getShoppingDetailById(String shoppingCartId, String eparchyCode) throws Exception
    {
        IData param = new DataMap();
        param.put("SHOPPING_CART_ID", shoppingCartId);
        param.put("TRADE_STAFF_ID", getVisit().getStaffId());
        param.put("EPARCHY_CODE", eparchyCode);
        return Dao.qryByCode("TF_B_SHOPPING_CART_DETAIL", "SEL_BY_ID", param);
    }

    public static IDataset getShoppingElementLimitAll() throws Exception
    {
        return Dao.qryByCode("TF_B_SHOPPING_CART_DETAIL", "SEL_ELEMENT_LIMIT", null, Route.CONN_CRM_CEN);
    }

    public static IDataset getShoppingInfoByOrderId(String busiOrderId) throws Exception
    {
        IData param = new DataMap();
        param.put("DETAIL_ORDER_ID", busiOrderId);
        return Dao.qryByCode("TF_B_SHOPPING_CART_DETAIL", "SEL_DETAIL_BY_ORDER_ID", param);
    }

    public static IDataset getShoppingInfoByStaffUserIds(String staffId, String custId, String state) throws Exception
    {
        IData param = new DataMap();
        param.put("TRADE_STAFF_ID", staffId);
        param.put("USER_ID", custId);
        param.put("STATE", state);
        return Dao.qryByCode("TF_B_SHOPPING_CART", "SEL_BY_STAFF_USER_IDS", param);
    }

    public static IDataset getShoppingOrderInfosByIds(String shoppingCartId, String orderId) throws Exception
    {
        IData param = new DataMap();
        param.put("SHOPPING_CART_ID", shoppingCartId);
        param.put("DETAIL_ORDER_ID", orderId);
        return Dao.qryByCode("TF_B_SHOPPING_CART_DETAIL", "SEL_ALL_BY_SO_ID", param);
    }

    public static IDataset getShoppingSqlParams(String sqlTag) throws Exception
    {
        IData param = new DataMap();
        param.put("TAG_NAME", sqlTag);
        return Dao.qryByCode("TF_B_SHOPPING_CART_DETAIL", "SEL_ALL_SQL", param, Route.CONN_CRM_CEN);
    }

    public static IDataset getShoppingSqlParamsByTableName(String tradeTabName, String sqlTag) throws Exception
    {
        IData param = new DataMap();
        param.put("TAG_NAME", sqlTag);
        param.put("TRADE_TABLE_NAME", tradeTabName);
        return Dao.qryByCode("TF_B_SHOPPING_CART_DETAIL", "SEL_BY_TRADE_TAB", param, Route.CONN_CRM_CEN);
    }

    public static IDataset getShoppingTradeSubById(String tabName, String sqlName, String tradeId) throws Exception
    {
        IData param = new DataMap();
        param.put("TRADE_ID", tradeId);
        return Dao.qryByCode("" + tabName, "" + sqlName, param);
    }

    public static IDataset getShoppingTradeSubByOrderId(String busiOrderId) throws Exception
    {
        IData param = new DataMap();
        param.put("ORDER_ID", busiOrderId);
        return Dao.qryByCode("TF_B_SHOPPING_CART_DETAIL", "SEL_TABLE_BY_ORDER_ID", param);
    }

    public static IDataset getTradeFeeSubByTradeElementIds(String tradeId, String elementId) throws Exception
    {
        IData param = new DataMap();
        param.put("TRADE_ID", tradeId);
        param.put("ELEMENT_ID", elementId);
        return Dao.qryByCode("TF_B_SHOPPING_CART_DETAIL", "SEL_BY_TID_EID", param);
    }

    public static void insertTradeHisByOrderId(String orderId) throws Exception
    {
        IData param = new DataMap();
        param.put("ORDER_ID", orderId);
        Dao.executeUpdateByCodeCode("TF_B_SHOPPING_CART_DETAIL", "INS_TRADE_HIS_BY_ORDER", param);
    }

    public static void updateShoppingCartState(String shoppingCartId, String orderId) throws Exception
    {
        IData param = new DataMap();
        param.put("SHOPPING_CART_ID", shoppingCartId);
        param.put("SHOPPING_ORDER_ID", orderId);
        Dao.executeUpdateByCodeCode("TF_B_SHOPPING_CART", "UPD_CART_STATE", param);
    }

    public static void updateTradeStateByOrderId(String orderId) throws Exception
    {
        IData param = new DataMap();
        param.put("ORDER_ID", orderId);
        Dao.executeUpdateByCodeCode("TF_B_SHOPPING_CART_DETAIL", "UPD_TRADE_DEL_BY_ORDER", param);
    }
}
