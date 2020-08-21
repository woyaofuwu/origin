
package com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.fee;

import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;

public class FeeData
{
    private String fee;

    private String feeMode;

    private String feeTypeCode;

    private String discntGiftId;

    private String oldFee;

    private String payMode;

    private String elementId;

    public String getDiscntGiftId()
    {
        return discntGiftId;
    }

    public String getElementId()
    {
        return elementId;
    }

    /**
     * 获取实缴费用
     * 
     * @return
     */
    public String getFee()
    {
        return fee;
    }

    /**
     * 获取费用类型 0-营业费 1-押金 2-预存
     * 
     * @return
     */
    public String getFeeMode()
    {
        return feeMode;
    }

    /**
     * 获取费用子类 具体值参见td_b_feeitem,td_s_foregift，td_b_payment
     * 
     * @return
     */
    public String getFeeTypeCode()
    {
        return feeTypeCode;
    }

    /**
     * 获取应缴费用
     * 
     * @return
     */
    public String getOldFee()
    {
        return oldFee;
    }

    public String getPayMode()
    {
        return payMode;
    }

    public void setDiscntGiftId(String discntGiftId)
    {
        this.discntGiftId = discntGiftId;
    }

    public void setElementId(String elementId)
    {
        this.elementId = elementId;
    }

    /**
     * 设置实缴费用
     * 
     * @param fee
     */
    public void setFee(String fee)
    {
        this.fee = fee;
    }

    /**
     * 设置营业费用
     * 
     * @param feeMode
     */
    public void setFeeMode(String feeMode)
    {
        this.feeMode = feeMode;
    }

    /**
     * 设置费用子类
     * 
     * @param feeTypeCode
     */
    public void setFeeTypeCode(String feeTypeCode)
    {
        this.feeTypeCode = feeTypeCode;
    }

    /**
     * 设置应缴费用
     * 
     * @param oldFee
     */
    public void setOldFee(String oldFee)
    {
        this.oldFee = oldFee;
    }

    public void setPayMode(String payMode)
    {
        this.payMode = payMode;
    }

    public IData toData()
    {
        IData data = new DataMap();

        data.put("FEE", this.fee);
        data.put("FEE_MODE", this.feeMode);
        data.put("FEE_TYPE_CODE", this.feeTypeCode);
        data.put("DISCNT_GIFT_ID", this.discntGiftId);
        data.put("OLDFEE", this.oldFee);
        data.put("PAY_MODE", this.payMode);
        data.put("ELEMENT_ID", this.elementId);

        return data;
    }
}
