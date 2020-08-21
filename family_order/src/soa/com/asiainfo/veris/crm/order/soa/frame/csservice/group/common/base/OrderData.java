
package com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.base;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;

public final class OrderData
{
    private IData od = new DataMap();

    /**
     * 设置ORDERID
     * 
     * @author tengg 2012-3-7
     * @param orderId
     * @throws Exception
     */
    public IData getAcctInfo()
    {

        return od.getData("ACCT_INFO");
    }

    public String getBatchId() throws Exception
    {

        return od.getString("BATCH_ID");
    }

    /**
     * 设置ORDERID
     * 
     * @author tengg 2012-3-7
     * @param orderId
     * @throws Exception
     */
    public String getCreadtRemoveTag()
    {

        return od.getString("CREADT_REMOVE_TAG", "");
    }

    /**
     * 设置ORDERID
     * 
     * @author tengg 2012-3-7
     * @param orderId
     * @throws Exception
     */
    public String getCreadtUserStataCodset()
    {

        return od.getString("CREADT_USER_STATE_CODESET", "");
    }

    /**
     * 获取当前正在办理的产品ID
     * 
     * @author tengg 2012-3-7
     * @return
     * @throws Exception
     */
    public String getCurrentProductId() throws Exception
    {

        return od.getString("PRODUCT_ID");
    }

    /**
     * 设置ORDERID
     * 
     * @author tengg 2012-3-7
     * @param orderId
     * @throws Exception
     */
    public String getCustId()
    {

        return od.getString("CUST_ID", "");
    }

    public String getEsoptag() throws Exception
    {

        return od.getString("ESOP_TAG");
    }

    /**
     * 获取费用列表
     * 
     * @author tengg 2012-3-7
     * @param productId
     * @return
     * @throws Exception
     */
    public IDataset getFeeList() throws Exception
    {

        return od.getDataset("FEE_LIST");
    }

    /**
     * 获取融合订单集团信息
     * 
     * @return
     */
    public IData getGroupUserOrderData()
    {

        return od.getData("GROUP_USER");
    }

    /**
     * 获取业务类型
     * 
     * @author tengg 2012-3-14
     * @return
     * @throws Exception
     */
    public String getInModeCode() throws Exception
    {

        return od.getString("IN_MODE_CODE");
    }

    /**
     * 获取融合订单成员信息
     * 
     * @return
     */
    public IData getMemberUserOrderData()
    {

        return od.getData("GROUP_MEMBER");
    }

    public String getMsgNow() throws Exception
    {

        return od.getString("M_msgNow");
    }

    public IData getOrderData()
    {

        return od;
    }

    /**
     * ' 获取ORDERID
     * 
     * @author tengg 2012-3-7
     * @return
     * @throws Exception
     */
    public String getOrderId() throws Exception
    {

        return od.getString("ORDER_ID");
    }

    /**
     * 设置ORDERID
     * 
     * @author tengg 2012-3-7
     * @param orderId
     * @throws Exception
     */
    public String getParentOrderId()
    {

        return od.getString("PARENT_ORDER_ID", "");
    }

    /**
     * 付费关系明细账目
     * 
     * @author tengg 2012-3-12
     * @return
     * @throws Exception
     */
    public IDataset getPayItemDesign() throws Exception
    {

        return (IDataset) od.get("PAY_ITEM");
    }

    /**
     * 获取付款方式列表
     * 
     * @author tengg 2012-3-7
     * @return
     * @throws Exception
     */
    public IDataset getPayModeList() throws Exception
    {

        IDataset obj = od.getDataset("PAYMODE_LIST");
        obj = obj == null ? new DatasetList() : obj;
        return obj;
    }

    /**
     * 付费关系明
     * 
     * @author tengg 2012-3-12
     * @return
     * @throws Exception
     */
    public IDataset getPayPlan() throws Exception
    {

        return (IDataset) od.get("PAY_PLAN");
    }

    /**
     * 获取付款方式列表
     * 
     * @author tengg 2012-3-7
     * @return
     * @throws Exception
     */
    public IData getPayRelation() throws Exception
    {

        return (IData) od.get("PAYRELAITON_LIST");
    }

    /**
     * 获取产品控制参数
     * 
     * @author tengg 2012-3-7
     * @param productId
     *            产品标识
     * @return 产品控制参数
     * @throws Exception
     */
    public IData getProductCtrlInfo(String productId) throws Exception
    {

        return (IData) od.get("PRODUCT_CTRL_INFO");
    }

    /**
     * 获取产品的BBOSS商品信息
     * 
     * @author tengg 2012-3-7
     * @return 产品的BBOSS商品信息
     * @throws Exception
     */
    public IDataset getProductGoodsInfo() throws Exception
    {

        return od.getDataset("PRODUCT_GOODS_INFO");
    }

    /**
     * 设置ORDERID
     * 
     * @author tengg 2012-3-7
     * @param orderId
     * @throws Exception
     */
    public String getProductId()
    {

        return od.getString("PRODUCT_ID", "");
    }

    /**
     * 获取产品信息
     * 
     * @return IData
     * @throws Exception
     */
    public IData getProductInfo() throws Exception
    {

        return (IData) od.get("PRODUCT_INFO");
    }

    /**
     * 获取产品个性化参数
     * 
     * @author tengg 2012-3-7
     * @param productId
     *            产品标识
     * @return 产品个性化参数
     * @throws Exception
     */
    public IData getProductParam() throws Exception
    {

        return (IData) od.get("PRODUCT_PARAM");
    }

    /**
     * 获取产品元素
     * 
     * @author tengg 2012-3-7
     * @param productId
     *            产品ID
     * @return
     * @throws Exception
     */
    public IDataset getProductsElement() throws Exception
    {

        return od.getDataset("SELECTED_ELEMENTS");
    }

    public String getRemark() throws Exception
    {

        return od.getString("REMARK");
    }

    /**
     * 获取资源列表
     * 
     * @author tengg 2012-3-7
     * @return
     * @throws Exception
     */
    public IDataset getResourceInfo() throws Exception
    {

        return od.getDataset("RESOURCE_INFO");
    }

    public String getSelectBusinessId() throws Exception
    {

        return od.getString("SELECT_BUSINESS_ID");
    }

    /**
     * 获取选择的产品
     * 
     * @return 产品信息
     * @author xiajj
     * @throws Exception
     */
    public IDataset getSelProducts() throws Exception
    {

        return od.getDataset("SEL_PRODUCTS");
    }

    public String getSerialNumber()
    {

        return od.getString("SERIAL_NUMBER", "");
    }

    /**
     * 获取业务类型
     * 
     * @author tengg 2012-3-14
     * @return
     * @throws Exception
     */
    public String getTradeTypeCode() throws Exception
    {

        return od.getString("TRADE_TYPE_CODE");
    }

    public String getTransCode()
    {

        return od.getString("X_TRANS_CODE", "");
    }

    /**
     * 设置当前正在办理的产品ID
     * 
     * @author tengg 2012-3-7
     * @param productId
     * @throws Exception
     */
    public void setCurrentProductId(String productId) throws Exception
    {

        od.put("PRODUCT_ID", productId);
    }

    /**
     * 设置费用列表
     * 
     * @author tengg 2012-3-7
     * @param feeList
     * @throws Exception
     */
    public void setFeeList(IDataset feeList) throws Exception
    {

        od.put("FEE_LIST", feeList);
    }

    /**
     * 设置ORDERID
     * 
     * @author tengg 2012-3-7
     * @param orderId
     * @throws Exception
     */
    public void setOrderId(String orderId)
    {

        od.put("ORDER_ID", orderId);
    }

    void setOrderInfo(IData reqOrderData)
    {

        od = reqOrderData;
    }

    /**
     * 设置业务类型
     * 
     * @author tengg 2012-3-14
     * @param inModeCode
     * @throws Exception
     */
    public void setOrderTypeCode()
    {
    }

    /**
     * 设置ORDERID
     * 
     * @author tengg 2012-3-7
     * @param orderId
     * @throws Exception
     */
    void setParentOrderId(String orderId)
    {

        od.put("PARENT_ORDER_ID", orderId);
    }

    /**
     * 设置付费关系明细账目
     * 
     * @author tengg 2012-3-12
     * @return
     * @throws Exception
     */
    public void setPayItemDesign(IDataset payItem) throws Exception
    {

        od.put("PAY_ITEM", payItem);
    }

    /**
     * 设置付款方式列表
     * 
     * @author tengg 2012-3-7
     * @param payModeList
     * @throws Exception
     */
    public void setPayModeList(IDataset payModeList) throws Exception
    {

        od.put("PAYMODE_LIST", payModeList);
    }

    /**
     * 设置付费关系
     * 
     * @author tengg 2012-3-12
     * @return
     * @throws Exception
     */
    public void setPayPlan(IDataset payPlan) throws Exception
    {

        od.put("PAY_PlAN", payPlan);
    }

    /**
     * 设置付费关系
     * 
     * @author tengg 2012-3-7
     * @param payModeList
     * @throws Exception
     */
    public void setPayRelation(IData payRelation) throws Exception
    {

        od.put("PAYRELAITON_LIST", payRelation);
    }

    /**
     * 设置产品的BBOSS商品信息
     * 
     * @author tengg 2012-3-7
     * @param productId
     * @param productGoodsInfo
     * @throws Exception
     */
    public void setProductGoodsInfo(String productId, IData productGoodsInfo) throws Exception
    {

        od.put("PRODUCT_GOODS_INFO", productGoodsInfo);
    }

    /**
     * 设置产品个性化参数
     * 
     * @author tengg 2012-3-7
     * @param productId
     *            产品标识
     * @param productParam
     *            产品个性化参数
     * @throws Exception
     */
    public void setProductParam(IData productParam) throws Exception
    {

        od.put("PRODUCT_PARAM", productParam);
    }

    /**
     * 设置产品元素
     * 
     * @author tengg 2012-3-7
     * @param productId
     *            产品ID
     * @param productsElemet
     *            元素列表
     * @throws Exception
     */
    public void setProductsElement(IDataset productsElemet) throws Exception
    {

        od.put("SELECTED_ELEMENTS", productsElemet);
    }

    /**
     * 设置资源列表
     * 
     * @author tengg 2012-3-7
     * @param payModeList
     * @throws Exception
     */
    public void setResourceInfo(IDataset resInfo) throws Exception
    {

        od.put("RESOURCE_INFO", resInfo);
    }

    /**
     * 设置选择的产品
     * 
     * @param selProducts
     *            产品控制参数
     * @author xiajj
     * @throws Exception
     */
    public void setSelProducts(IDataset selProducts) throws Exception
    {

        od.put("SEL_PRODUCTS", selProducts);
    }

    /**
     * 设置业务类型
     * 
     * @author tengg 2012-3-14
     * @param tradeTypeCode
     * @throws Exception
     */
    public void setTradeTypeCode(String tradeTypeCode)
    {

        od.put("TRADE_TYPE_CODE", tradeTypeCode);
    }

    void setTransCode(String transCode)
    {

        od.put("X_TRANS_CODE", transCode);
    }
}
