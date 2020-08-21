
package com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend;

import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BaseTradeData;

public class FeeTradeData extends BaseTradeData
{
    private String chargeId;

    private String discntGiftId;

    private String efficetDate;

    private String fee;

    private String feeMode;

    private String feeTypeCode;

    private String limitMoney;

    private String months;

    private String oldfee;

    private String remark;

    private String rsrvStr1;

    private String rsrvStr10;

    private String rsrvStr2;

    private String rsrvStr3;

    private String rsrvStr4;

    private String rsrvStr5;

    private String rsrvStr6;

    private String rsrvStr7;

    private String rsrvStr8;

    private String rsrvStr9;

    private String userId;

    public FeeTradeData()
    {

    }

    public FeeTradeData(IData data)
    {
        this.setChargeId(data.getString("CHARGE_ID"));
        this.setDiscntGiftId(data.getString("DISCNT_GIFT_ID"));
        this.setEfficetDate(data.getString("EFFICET_DATE"));
        this.setFee(data.getString("FEE"));
        this.setFeeMode(data.getString("FEE_MODE"));
        this.setFeeTypeCode(data.getString("FEE_TYPE_CODE"));
        this.setLimitMoney(data.getString("LIMIT_MONEY"));
        this.setMonths(data.getString("MONTHS"));
        this.setOldfee(data.getString("OLDFEE"));
        this.setRemark(data.getString("REMARK"));
        this.setRsrvStr1(data.getString("RSRV_STR1"));
        this.setRsrvStr10(data.getString("RSRV_STR10"));
        this.setRsrvStr2(data.getString("RSRV_STR2"));
        this.setRsrvStr3(data.getString("RSRV_STR3"));
        this.setRsrvStr4(data.getString("RSRV_STR4"));
        this.setRsrvStr5(data.getString("RSRV_STR5"));
        this.setRsrvStr6(data.getString("RSRV_STR6"));
        this.setRsrvStr7(data.getString("RSRV_STR7"));
        this.setRsrvStr8(data.getString("RSRV_STR8"));
        this.setRsrvStr9(data.getString("RSRV_STR9"));
        this.setUserId(data.getString("USER_ID"));
    }

    public String getChargeId()
    {
        return chargeId;
    }

    public String getDiscntGiftId()
    {
        return discntGiftId;
    }

    public String getEfficetDate()
    {
        return efficetDate;
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

    public String getLimitMoney()
    {
        return limitMoney;
    }

    public String getMonths()
    {
        return months;
    }

    public String getOldfee()
    {
        return oldfee;
    }

    public String getRemark()
    {
        return remark;
    }

    public String getRsrvStr1()
    {
        return rsrvStr1;
    }

    public String getRsrvStr10()
    {
        return rsrvStr10;
    }

    public String getRsrvStr2()
    {
        return rsrvStr2;
    }

    public String getRsrvStr3()
    {
        return rsrvStr3;
    }

    public String getRsrvStr4()
    {
        return rsrvStr4;
    }

    public String getRsrvStr5()
    {
        return rsrvStr5;
    }

    public String getRsrvStr6()
    {
        return rsrvStr6;
    }

    public String getRsrvStr7()
    {
        return rsrvStr7;
    }

    public String getRsrvStr8()
    {
        return rsrvStr8;
    }

    public String getRsrvStr9()
    {
        return rsrvStr9;
    }

    @Override
    public String getTableName()
    {
        return "TF_B_TRADEFEE_SUB";
    }

    public String getUserId()
    {
        return userId;
    }

    public void setChargeId(String chargeId)
    {
        this.chargeId = chargeId;
    }

    public void setDiscntGiftId(String discntGiftId)
    {
        this.discntGiftId = discntGiftId;
    }

    public void setEfficetDate(String efficetDate)
    {
        this.efficetDate = efficetDate;
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

    public void setLimitMoney(String limitMoney)
    {
        this.limitMoney = limitMoney;
    }

    public void setMonths(String months)
    {
        this.months = months;
    }

    public void setOldfee(String oldfee)
    {
        this.oldfee = oldfee;
    }

    public void setRemark(String remark)
    {
        this.remark = remark;
    }

    public void setRsrvStr1(String rsrvStr1)
    {
        this.rsrvStr1 = rsrvStr1;
    }

    public void setRsrvStr10(String rsrvStr10)
    {
        this.rsrvStr10 = rsrvStr10;
    }

    public void setRsrvStr2(String rsrvStr2)
    {
        this.rsrvStr2 = rsrvStr2;
    }

    public void setRsrvStr3(String rsrvStr3)
    {
        this.rsrvStr3 = rsrvStr3;
    }

    public void setRsrvStr4(String rsrvStr4)
    {
        this.rsrvStr4 = rsrvStr4;
    }

    public void setRsrvStr5(String rsrvStr5)
    {
        this.rsrvStr5 = rsrvStr5;
    }

    public void setRsrvStr6(String rsrvStr6)
    {
        this.rsrvStr6 = rsrvStr6;
    }

    public void setRsrvStr7(String rsrvStr7)
    {
        this.rsrvStr7 = rsrvStr7;
    }

    public void setRsrvStr8(String rsrvStr8)
    {
        this.rsrvStr8 = rsrvStr8;
    }

    public void setRsrvStr9(String rsrvStr9)
    {
        this.rsrvStr9 = rsrvStr9;
    }

    public void setUserId(String userId)
    {
        this.userId = userId;
    }

    @Override
    public IData toData()
    {
        IData data = new DataMap();

        data.put("CHARGE_ID", this.chargeId);
        data.put("DISCNT_GIFT_ID", this.discntGiftId);
        data.put("EFFICET_DATE", this.efficetDate);
        data.put("FEE", this.fee);
        data.put("FEE_MODE", this.feeMode);
        data.put("FEE_TYPE_CODE", this.feeTypeCode);
        data.put("LIMIT_MONEY", this.limitMoney);
        data.put("MONTHS", this.months);
        data.put("OLDFEE", this.oldfee);
        data.put("REMARK", this.remark);
        data.put("RSRV_STR1", this.rsrvStr1);
        data.put("RSRV_STR10", this.rsrvStr10);
        data.put("RSRV_STR2", this.rsrvStr2);
        data.put("RSRV_STR3", this.rsrvStr3);
        data.put("RSRV_STR4", this.rsrvStr4);
        data.put("RSRV_STR5", this.rsrvStr5);
        data.put("RSRV_STR6", this.rsrvStr6);
        data.put("RSRV_STR7", this.rsrvStr7);
        data.put("RSRV_STR8", this.rsrvStr8);
        data.put("RSRV_STR9", this.rsrvStr9);
        data.put("USER_ID", this.userId);

        return data;
    }
}
