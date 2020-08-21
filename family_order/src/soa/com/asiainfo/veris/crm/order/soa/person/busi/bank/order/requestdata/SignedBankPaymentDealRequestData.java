
package com.asiainfo.veris.crm.order.soa.person.busi.bank.order.requestdata;

/**
 * @author think
 */
public class SignedBankPaymentDealRequestData extends BaseSignedBankPaymentDealRequestData
{
    public String partitionId;

    public String reckTag;

    public String openBank;

    public String bankCardNo;

    public String cardTypeCode;

    public String startTime;

    public String endTime;

    public String tradeId;

    public final String getBankCardNo()
    {
        return bankCardNo;
    }

    public final String getCardTypeCode()
    {
        return cardTypeCode;
    }

    public final String getEndTime()
    {
        return endTime;
    }

    public final String getOpenBank()
    {
        return openBank;
    }

    public final String getPartitionId()
    {
        return partitionId;
    }

    public final String getReckTag()
    {
        return reckTag;
    }

    public final String getStartTime()
    {
        return startTime;
    }

    public final String getTradeId()
    {
        return tradeId;
    }

    public final void setBankCardNo(String bankCardNo)
    {
        this.bankCardNo = bankCardNo;
    }

    public final void setCardTypeCode(String cardTypeCode)
    {
        this.cardTypeCode = cardTypeCode;
    }

    public final void setEndTime(String endTime)
    {
        this.endTime = endTime;
    }

    public final void setOpenBank(String openBank)
    {
        this.openBank = openBank;
    }

    public final void setPartitionId(String partitionId)
    {
        this.partitionId = partitionId;
    }

    public final void setReckTag(String reckTag)
    {
        this.reckTag = reckTag;
    }

    public final void setStartTime(String startTime)
    {
        this.startTime = startTime;
    }

    public final void setTradeId(String tradeId)
    {
        this.tradeId = tradeId;
    }

}
