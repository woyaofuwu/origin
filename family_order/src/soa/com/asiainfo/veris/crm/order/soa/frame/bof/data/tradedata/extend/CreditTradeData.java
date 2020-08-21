
package com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend;

import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.ProductModuleTradeData;

/**
 * @author Administrator
 */
public class CreditTradeData extends ProductModuleTradeData
{
    private String creditGiftMonths;

    private String creditMode;

    private String creditValue;

    private String endDate;

    private String modifyTag;

    private String remark;

    private String rsrvDate1;

    private String rsrvDate2;

    private String rsrvDate3;

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

    private String startDate;

    private String tagSet;

    private String userId;

    public CreditTradeData()
    {
        this.setElementType(BofConst.ELEMENT_TYPE_CODE_CREDIT);
    }

    public CreditTradeData(IData data)
    {
        this.elementType = BofConst.ELEMENT_TYPE_CODE_CREDIT;
        this.creditGiftMonths = data.getString("CREDIT_GIFT_MONTHS");
        this.creditMode = data.getString("CREDIT_MODE");
        this.creditValue = data.getString("CREDIT_VALUE");
        this.endDate = data.getString("END_DATE");
        this.modifyTag = data.getString("MODIFY_TAG");
        this.remark = data.getString("REMARK");
        this.rsrvDate1 = data.getString("RSRV_DATE1");
        this.rsrvDate2 = data.getString("RSRV_DATE2");
        this.rsrvDate3 = data.getString("RSRV_DATE3");
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
        this.startDate = data.getString("START_DATE");
        this.tagSet = data.getString("TAG_SET");
        this.userId = data.getString("USER_ID");
    }

    public CreditTradeData clone()
    {
        CreditTradeData CreditTradeData = new CreditTradeData();
        CreditTradeData.setCreditGiftMonths(this.getCreditGiftMonths());
        CreditTradeData.setCreditMode(this.getCreditMode());
        CreditTradeData.setCreditValue(this.getCreditValue());
        CreditTradeData.setEndDate(this.getEndDate());
        CreditTradeData.setModifyTag(this.getModifyTag());
        CreditTradeData.setRemark(this.getRemark());
        CreditTradeData.setRsrvDate1(this.getRsrvDate1());
        CreditTradeData.setRsrvDate2(this.getRsrvDate2());
        CreditTradeData.setRsrvDate3(this.getRsrvDate3());
        CreditTradeData.setRsrvStr1(this.getRsrvStr1());
        CreditTradeData.setRsrvStr10(this.getRsrvStr10());
        CreditTradeData.setRsrvStr2(this.getRsrvStr2());
        CreditTradeData.setRsrvStr3(this.getRsrvStr3());
        CreditTradeData.setRsrvStr4(this.getRsrvStr4());
        CreditTradeData.setRsrvStr5(this.getRsrvStr5());
        CreditTradeData.setRsrvStr6(this.getRsrvStr6());
        CreditTradeData.setRsrvStr7(this.getRsrvStr7());
        CreditTradeData.setRsrvStr8(this.getRsrvStr8());
        CreditTradeData.setRsrvStr9(this.getRsrvStr9());
        CreditTradeData.setStartDate(this.getStartDate());
        CreditTradeData.setTagSet(this.getTagSet());
        CreditTradeData.setUserId(this.getUserId());
        return CreditTradeData;
    }

    public String getCreditGiftMonths()
    {
        return creditGiftMonths;
    }

    public String getCreditMode()
    {
        return creditMode;
    }

    public String getCreditValue()
    {
        return creditValue;
    }

    public String getEndDate()
    {
        return endDate;
    }

    public String getModifyTag()
    {
        return modifyTag;
    }

    public String getRemark()
    {
        return remark;
    }

    public String getRsrvDate1()
    {
        return rsrvDate1;
    }

    public String getRsrvDate2()
    {
        return rsrvDate2;
    }

    public String getRsrvDate3()
    {
        return rsrvDate3;
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

    public String getStartDate()
    {
        return startDate;
    }

    public String getTableName()
    {
        return "TF_B_TRADE_CREDIT";
    }

    public String getTagSet()
    {
        return tagSet;
    }

    public String getUserId()
    {
        return userId;
    }

    public void setCreditGiftMonths(String creditGiftMonths)
    {
        this.creditGiftMonths = creditGiftMonths;
    }

    public void setCreditMode(String creditMode)
    {
        this.creditMode = creditMode;
    }

    public void setCreditValue(String creditValue)
    {
        this.creditValue = creditValue;
    }

    public void setEndDate(String endDate)
    {
        this.endDate = endDate;
    }

    public void setModifyTag(String modifyTag)
    {
        this.modifyTag = modifyTag;
    }

    public void setRemark(String remark)
    {
        this.remark = remark;
    }

    public void setRsrvDate1(String rsrvDate1)
    {
        this.rsrvDate1 = rsrvDate1;
    }

    public void setRsrvDate2(String rsrvDate2)
    {
        this.rsrvDate2 = rsrvDate2;
    }

    public void setRsrvDate3(String rsrvDate3)
    {
        this.rsrvDate3 = rsrvDate3;
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

    public void setStartDate(String startDate)
    {
        this.startDate = startDate;
    }

    public void setTagSet(String tagSet)
    {
        this.tagSet = tagSet;
    }

    public void setUserId(String userId)
    {
        this.userId = userId;
    }

    public IData toData()
    {
        IData data = new DataMap();

        data.put("CREDIT_GIFT_MONTHS", this.creditGiftMonths);
        data.put("CREDIT_MODE", this.creditMode);
        data.put("CREDIT_VALUE", this.creditValue);
        data.put("END_DATE", this.endDate);
        data.put("MODIFY_TAG", this.modifyTag);
        data.put("REMARK", this.remark);
        data.put("RSRV_DATE1", this.rsrvDate1);
        data.put("RSRV_DATE2", this.rsrvDate2);
        data.put("RSRV_DATE3", this.rsrvDate3);
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
        data.put("START_DATE", this.startDate);
        data.put("TAG_SET", this.tagSet);
        data.put("USER_ID", this.userId);

        return data;
    }

    public String toString()
    {
        IData data = new DataMap();
        data.put(getTableName(), this.toData());
        return data.toString();
    }
}
