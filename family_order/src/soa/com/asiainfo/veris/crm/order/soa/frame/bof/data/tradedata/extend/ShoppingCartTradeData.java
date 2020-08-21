
package com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend;

import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BaseTradeData;

public class ShoppingCartTradeData extends BaseTradeData
{

    private static final long serialVersionUID = -6346341732691553592L;

    private String shoppingCartId;

    private String shoppingOrderId;

    private String custId;

    private String userId;

    private String discntRuleId;

    private String payModeCode;

    private String oldFee;

    private String freeFee;

    private String fee;

    private String state;

    private String inModeCode;

    private String tradeStaffId;

    private String tradeDepartId;

    private String acceptDate;

    public ShoppingCartTradeData()
    {

    }

    public ShoppingCartTradeData(IData data)
    {
        this.setShoppingCartId(data.getString("SHOPPING_CART_ID"));
        this.setShoppingOrderId(data.getString("SHOPPING_ORDER_ID"));
        this.setCustId(data.getString("CUST_ID"));
        this.setUserId(data.getString("USER_ID"));
        this.setDiscntRuleId(data.getString("DISCNT_RULE_ID"));
        this.setPayModeCode(data.getString("PAY_MODE_CODE"));
        this.setOldFee(data.getString("OLDFEE"));
        this.setFreeFee(data.getString("FREE_FEE"));
        this.setFee(data.getString("FEE"));
        this.setState(data.getString("STATE"));
        this.setInModeCode(data.getString("IN_MODE_CODE"));
        this.setTradeStaffId(data.getString("TRADE_STAFF_ID"));
        this.setTradeDepartId(data.getString("TRADE_DEPART_ID"));
        this.setAcceptDate(data.getString("ACCEPT_DATE"));
    }

    public String getAcceptDate()
    {
        return acceptDate;
    }

    public String getCustId()
    {
        return custId;
    }

    public String getDiscntRuleId()
    {
        return discntRuleId;
    }

    public String getDsicntRuleId()
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

    public String getInModeCode()
    {
        return inModeCode;
    }

    public String getOldFee()
    {
        return oldFee;
    }

    public String getPayModeCode()
    {
        return payModeCode;
    }

    public String getShoppingCartId()
    {
        return shoppingCartId;
    }

    public String getShoppingOrderId()
    {
        return shoppingOrderId;
    }

    public String getState()
    {
        return state;
    }

    public String getTableName()
    {
        return "TF_B_SHOPPING_CART";
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

    public void setInModeCode(String inModeCode)
    {
        this.inModeCode = inModeCode;
    }

    public void setOldFee(String oldFee)
    {
        this.oldFee = oldFee;
    }

    public void setPayModeCode(String payModeCode)
    {
        this.payModeCode = payModeCode;
    }

    public void setShoppingCartId(String shoppingCartId)
    {
        this.shoppingCartId = shoppingCartId;
    }

    public void setShoppingOrderId(String shoppingOrderId)
    {
        this.shoppingOrderId = shoppingOrderId;
    }

    public void setState(String state)
    {
        this.state = state;
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
        data.put("SHOPPING_ORDER_ID", this.shoppingOrderId);
        data.put("CUST_ID", this.custId);
        data.put("USER_ID", this.userId);
        data.put("DISCNT_RULE_ID", this.discntRuleId);
        data.put("PAY_MODE_CODE", this.payModeCode);
        data.put("OLDFEE", this.oldFee);
        data.put("FREE_FEE", this.freeFee);
        data.put("FEE", this.fee);
        data.put("STATE", this.state);
        data.put("IN_MODE_CODE", this.inModeCode);
        data.put("TRADE_STAFF_ID", this.tradeStaffId);
        data.put("TRADE_DEPART_ID", this.tradeDepartId);
        data.put("ACCEPT_DATE", this.acceptDate);
        return data;
    }

    @Override
    public String toString()
    {
        IData data = new DataMap();
        data.put(getTableName(), this.toData());
        return data.toString();
    }
}
