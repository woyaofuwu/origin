
package com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend;

import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BaseTradeData;

public class ShoppingCartDetailTradeData extends BaseTradeData
{

    private static final long serialVersionUID = -966497488645040855L;

    private String shoppingCartId;

    private String orderId;

    private String custId;

    private String userId;

    private String orderTypeCode;

    private String discntRuleId;

    private String oldFee;

    private String freeFee;

    private String fee;

    private String joinType;

    private String joinSeq;

    private String requestData;

    private String tradeStaffId;

    private String tradeDepartId;

    private String acceptDate;

    private String detailStateCode = "0";

    public ShoppingCartDetailTradeData()
    {

    }

    public ShoppingCartDetailTradeData(IData data)
    {
        this.shoppingCartId = data.getString("SHOPPING_CART_ID");
        this.orderId = data.getString("ORDER_ID");
        this.custId = data.getString("CUST_ID");
        this.userId = data.getString("USER_ID");
        this.discntRuleId = data.getString("DISCNT_RULE_ID");
        this.oldFee = data.getString("OLDFEE");
        this.freeFee = data.getString("FREE_FEE");
        this.fee = data.getString("FEE");
        this.joinType = data.getString("JOIN_TYPE");
        this.joinSeq = data.getString("JOIN_SEQ");
        this.requestData = data.getString("REQUEST_DATA");
        this.tradeStaffId = data.getString("TRADE_STAFF_ID");
        this.tradeDepartId = data.getString("TRADE_DEPART_ID");
        this.orderTypeCode = data.getString("ORDER_TYPE_CODE");
        this.acceptDate = data.getString("ACCEPT_DATE");
    }

    public ShoppingCartDetailTradeData clone()
    {
        ShoppingCartDetailTradeData shoppingTradeData = new ShoppingCartDetailTradeData();
        shoppingTradeData.setShoppingCartId(this.getShoppingCartId());
        shoppingTradeData.setOrderTypeCode(this.getOrderTypeCode());
        shoppingTradeData.setOrderId(this.getOrderId());
        shoppingTradeData.setCustId(this.getCustId());
        shoppingTradeData.setUserId(this.getUserId());
        shoppingTradeData.setDiscntRuleId(this.getDiscntRuleId());
        shoppingTradeData.setOldFee(this.getOldFee());
        shoppingTradeData.setFreeFee(this.getFreeFee());
        shoppingTradeData.setFee(this.getFee());
        shoppingTradeData.setJoinType(this.getJoinType());
        shoppingTradeData.setJoinSeq(this.getJoinSeq());
        shoppingTradeData.setRequestData(this.getRequestData());
        shoppingTradeData.setTradeStaffId(this.getTradeStaffId());
        shoppingTradeData.setTradeDepartId(this.getTradeDepartId());
        shoppingTradeData.setAcceptDate(this.getAcceptDate());
        return shoppingTradeData;
    }

    public String getAcceptDate()
    {
        return acceptDate;
    }

    public String getCustId()
    {
        return custId;
    }

    public String getDetailStateCode()
    {
        return detailStateCode;
    }

    public String getDiscntRuleId()
    {
        return discntRuleId;
    }

    public String getFee()
    {
        return fee;
    }

    public String getFreeFee()
    {
        return freeFee;
    }

    public String getJoinSeq()
    {
        return joinSeq;
    }

    public String getJoinType()
    {
        return joinType;
    }

    public String getOldFee()
    {
        return oldFee;
    }

    public String getOrderId()
    {
        return orderId;
    }

    public String getOrderTypeCode()
    {
        return orderTypeCode;
    }

    public String getRequestData()
    {
        return requestData;
    }

    public String getShoppingCartId()
    {
        return shoppingCartId;
    }

    @Override
    public String getTableName()
    {
        return "TF_B_SHOPPING_CART_DETAIL";
    }

    public String getTradeDepartId()
    {
        return tradeDepartId;
    }

    public String getTradeStaffId()
    {
        return tradeStaffId;
    }

    public String getUserId()
    {
        return userId;
    }

    public void setAcceptDate(String acceptDate)
    {
        this.acceptDate = acceptDate;
    }

    public void setCustId(String custId)
    {
        this.custId = custId;
    }

    public void setDetailStateCode(String detailStateCode)
    {
        this.detailStateCode = detailStateCode;
    }

    public void setDiscntRuleId(String discntRuleId)
    {
        this.discntRuleId = discntRuleId;
    }

    public void setFee(String fee)
    {
        this.fee = fee;
    }

    public void setFreeFee(String freeFee)
    {
        this.freeFee = freeFee;
    }

    public void setJoinSeq(String joinSeq)
    {
        this.joinSeq = joinSeq;
    }

    public void setJoinType(String joinType)
    {
        this.joinType = joinType;
    }

    public void setOldFee(String oldFee)
    {
        this.oldFee = oldFee;
    }

    public void setOrderId(String orderId)
    {
        this.orderId = orderId;
    }

    public void setOrderTypeCode(String orderTypeCode)
    {
        this.orderTypeCode = orderTypeCode;
    }

    public void setRequestData(String requestData)
    {
        this.requestData = requestData;
    }

    public void setShoppingCartId(String shoppingCartId)
    {
        this.shoppingCartId = shoppingCartId;
    }

    public void setTradeDepartId(String tradeDepartId)
    {
        this.tradeDepartId = tradeDepartId;
    }

    public void setTradeStaffId(String tradeStaffId)
    {
        this.tradeStaffId = tradeStaffId;
    }

    public void setUserId(String userId)
    {
        this.userId = userId;
    }

    public IData toData()
    {
        IData data = new DataMap();
        data.put("SHOPPING_CART_ID", this.shoppingCartId);
        data.put("CUST_ID", this.custId);
        data.put("USER_ID", this.userId);
        data.put("DETAIL_ORDER_ID", this.orderId);
        data.put("DETAIL_STATE_CODE", this.detailStateCode);
        data.put("DETAIL_TYPE_CODE", this.orderTypeCode);
        data.put("DISCNT_RULE_ID", this.discntRuleId);
        data.put("OLDFEE", this.oldFee);
        data.put("FREE_FEE", this.freeFee);
        data.put("FEE", this.fee);
        data.put("JOIN_TYPE", this.joinType);
        data.put("JOIN_SEQ", this.joinSeq);
        data.put("REQUEST_DATA", this.requestData);
        data.put("TRADE_STAFF_ID", this.tradeStaffId);
        data.put("TRADE_DEPART_ID", this.tradeDepartId);
        data.put("ACCEPT_DATE", this.acceptDate);
        return data;
    }

    public String toString()
    {
        IData data = new DataMap();
        data.put(getTableName(), this.toData());
        return data.toString();
    }

}
