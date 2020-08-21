/**
 * 
 */

package com.asiainfo.veris.crm.order.soa.person.busi.specompensatecard.order.requestdata;

import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;

/**
 * @CREATED by gongp@2014-9-2 修改历史 Revision 2014-9-2 上午10:55:23
 */
public class SpeCompensateCardRequestData extends BaseReqData
{
    private String simCardNo;

    private String payMode;

    private String resTypeCode;

    private String capacityTypeCode;

    private String cardKindCode;
    
    private String resKindCode;

    private String fee;
    
    

    public String getResKindCode()
    {
        return resKindCode;
    }

    public void setResKindCode(String resKindCode)
    {
        this.resKindCode = resKindCode;
    }

    public String getCapacityTypeCode()
    {
        return capacityTypeCode;
    }

    public String getCardKindCode()
    {
        return cardKindCode;
    }

    public String getFee()
    {
        return fee;
    }

    public String getPayMode()
    {
        return payMode;
    }

    public String getResTypeCode()
    {
        return resTypeCode;
    }

    public String getSimCardNo()
    {
        return simCardNo;
    }

    public void setCapacityTypeCode(String capacityTypeCode)
    {
        this.capacityTypeCode = capacityTypeCode;
    }

    public void setCardKindCode(String cardKindCode)
    {
        this.cardKindCode = cardKindCode;
    }

    public void setFee(String fee)
    {
        this.fee = fee;
    }

    public void setPayMode(String payMode)
    {
        this.payMode = payMode;
    }

    public void setResTypeCode(String resTypeCode)
    {
        this.resTypeCode = resTypeCode;
    }

    public void setSimCardNo(String simCardNo)
    {
        this.simCardNo = simCardNo;
    }
}
