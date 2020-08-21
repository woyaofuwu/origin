
package com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.shoppingcart;

import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;

public class BusiElementModuleData
{
    private String orderId;

    private String elementTypeCode;

    private String elementId;

    private String busiTypeCode;

    private String shoppingStateCode;

    private String feeMode;

    private String feeTypeCode;

    private String fee;

    public BusiElementModuleData()
    {

    }

    public BusiElementModuleData(IData data)
    {
        this.orderId = data.getString("ORDER_ID");
        this.elementTypeCode = data.getString("ELEMENT_TYPE_CODE");
        this.elementId = data.getString("ELEMENT_ID");
        this.busiTypeCode = data.getString("MODIFY_TAG");
        this.shoppingStateCode = data.getString("DETAIL_STATE_CODE");
    }

    public String getBusiTypeCode()
    {
        return busiTypeCode;
    }

    public String getElementId()
    {
        return elementId;
    }

    public String getElementTypeCode()
    {
        return elementTypeCode;
    }

    public String getFee()
    {
        return fee;
    }

    public String getFeeMode()
    {
        return feeMode;
    }

    public String getFeeTypeCode()
    {
        return feeTypeCode;
    }

    public String getOrderId()
    {
        return orderId;
    }

    public String getShoppingStateCode()
    {
        return shoppingStateCode;
    }

    public void setBusiTypeCode(String busiTypeCode)
    {
        this.busiTypeCode = busiTypeCode;
    }

    public void setElementId(String elementId)
    {
        this.elementId = elementId;
    }

    public void setElementTypeCode(String elementTypeCode)
    {
        this.elementTypeCode = elementTypeCode;
    }

    public void setFee(String fee)
    {
        this.fee = fee;
    }

    public void setFeeMode(String feeMode)
    {
        this.feeMode = feeMode;
    }

    public void setFeeTypeCode(String feeTypeCode)
    {
        this.feeTypeCode = feeTypeCode;
    }

    public void setOrderId(String orderId)
    {
        this.orderId = orderId;
    }

    public void setShoppingStateCode(String shoppingStateCode)
    {
        this.shoppingStateCode = shoppingStateCode;
    }

    public IData toData()
    {
        IData data = new DataMap();
        data.put("ELEMENT_TYPE_CODE", this.elementTypeCode);
        data.put("ELEMENT_ID", this.elementId);
        data.put("MODIFY_TAG", this.busiTypeCode);
        data.put("SHOPPING_STATE_CODE", this.shoppingStateCode);
        return data;
    }

}
