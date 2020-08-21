
package com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend;

import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BaseTradeData;

/**
 * @author Administrator
 */
public class GiftFeeTradeData extends BaseTradeData
{
    private String chargeId;

    private String discntGiftId;

    private String efficetDate;

    private String fee;

    private String feeMode;

    private String feeTypeCode;

    private String limitMoney;

    private String months;

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

    private String userIdA;

    public GiftFeeTradeData()
    {

    }

    public GiftFeeTradeData(IData data)
    {
        this.chargeId = data.getString("CHARGE_ID");
        this.discntGiftId = data.getString("DISCNT_GIFT_ID");
        this.efficetDate = data.getString("EFFICET_DATE");
        this.fee = data.getString("FEE");
        this.feeMode = data.getString("FEE_MODE");
        this.feeTypeCode = data.getString("FEE_TYPE_CODE");
        this.limitMoney = data.getString("LIMIT_MONEY");
        this.months = data.getString("MONTHS");
        this.remark = data.getString("REMARK");
        this.rsrvStr1 = data.getString("RSRV_STR1");
        this.rsrvStr10 = data.getString("RSRV_STR10");
        this.rsrvStr2 = data.getString("RSRV_STR2");
        this.rsrvStr3 = data.getString("RSRV_STR3");
        this.rsrvStr4 = data.getString("RSRV_STR4");
        this.rsrvStr5 = data.getString("RSRV_STR5");
        this.rsrvStr6 = data.getString("RSRV_STR6");
        this.rsrvStr7 = data.getString("RSRV_STR7");
        this.rsrvStr8 = data.getString("RSRV_STR8");
        this.rsrvStr9 = data.getString("RSRV_STR9");
        this.userId = data.getString("USER_ID");
        this.userIdA = data.getString("USER_ID_A");
    }

    public GiftFeeTradeData clone()
    {
        GiftFeeTradeData giftFeeTradeData = new GiftFeeTradeData();
        giftFeeTradeData.setChargeId(this.getChargeId());
        giftFeeTradeData.setDiscntGiftId(this.getDiscntGiftId());
        giftFeeTradeData.setEfficetDate(this.getEfficetDate());
        giftFeeTradeData.setFee(this.getFee());
        giftFeeTradeData.setFeeMode(this.getFeeMode());
        giftFeeTradeData.setFeeTypeCode(this.getFeeTypeCode());
        giftFeeTradeData.setLimitMoney(this.getLimitMoney());
        giftFeeTradeData.setMonths(this.getMonths());
        giftFeeTradeData.setRemark(this.getRemark());
        giftFeeTradeData.setRsrvStr1(this.getRsrvStr1());
        giftFeeTradeData.setRsrvStr10(this.getRsrvStr10());
        giftFeeTradeData.setRsrvStr2(this.getRsrvStr2());
        giftFeeTradeData.setRsrvStr3(this.getRsrvStr3());
        giftFeeTradeData.setRsrvStr4(this.getRsrvStr4());
        giftFeeTradeData.setRsrvStr5(this.getRsrvStr5());
        giftFeeTradeData.setRsrvStr6(this.getRsrvStr6());
        giftFeeTradeData.setRsrvStr7(this.getRsrvStr7());
        giftFeeTradeData.setRsrvStr8(this.getRsrvStr8());
        giftFeeTradeData.setRsrvStr9(this.getRsrvStr9());
        giftFeeTradeData.setUserId(this.getUserId());
        giftFeeTradeData.setUserIdA(this.getUserIdA());
        return giftFeeTradeData;
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

    public String getTableName()
    {
        return "TF_B_TRADEFEE_GIFTFEE";
    }

    public String getUserId()
    {
        return userId;
    }

    public String getUserIdA()
    {
        return userIdA;
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

    public void setUserIdA(String userIdA)
    {
        this.userIdA = userIdA;
    }

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
        data.put("USER_ID_A", this.userIdA);

        return data;
    }

    public String toString()
    {
        IData data = new DataMap();
        data.put(getTableName(), this.toData());
        return data.toString();
    }
}
