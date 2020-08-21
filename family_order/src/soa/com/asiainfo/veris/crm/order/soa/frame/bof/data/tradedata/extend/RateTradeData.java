
package com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend;

import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BaseTradeData;

public class RateTradeData extends BaseTradeData
{
    private String instId;

    private String userId;

    private String rate;

    private String modifyTag;

    private String startDate;

    private String endDate;

    private String remark;

    private String rsrvStr1;

    private String rsrvStr2;

    private String rsrvStr3;

    private String rsrvStr4;

    private String rsrvStr5;

    private String rsrvTag1;

    private String rsrvTag2;

    private String rsrvTag3;

    private String rsrvDate1;

    private String rsrvDate2;

    private String rsrvDate3;

    public RateTradeData()
    {
    }

    public RateTradeData(IData data)
    {
        this.instId = data.getString("INST_ID");
        this.userId = data.getString("USER_ID");
        this.rate = data.getString("RATE");
        this.modifyTag = data.getString("MODIFY_TAG");
        this.startDate = data.getString("START_DATE");
        this.endDate = data.getString("END_DATE");
        this.remark = data.getString("REMARK");
        this.rsrvStr1 = data.getString("RSRV_STR1");
        this.rsrvStr2 = data.getString("RSRV_STR2");
        this.rsrvStr3 = data.getString("RSRV_STR3");
        this.rsrvStr4 = data.getString("RSRV_STR4");
        this.rsrvStr5 = data.getString("RSRV_STR5");
        this.rsrvTag1 = data.getString("RSRV_TAG1");
        this.rsrvTag2 = data.getString("RSRV_TAG2");
        this.rsrvTag3 = data.getString("RSRV_TAG3");
        this.rsrvDate1 = data.getString("RSRV_DATE1");
        this.rsrvDate2 = data.getString("RSRV_DATE2");
        this.rsrvDate3 = data.getString("RSRV_DATE3");
    }

    public RateTradeData clone()
    {
        RateTradeData rateTradeData = new RateTradeData();
        rateTradeData.setInstId(this.getInstId());
        rateTradeData.setUserId(this.getUserId());
        rateTradeData.setRate(this.getRate());
        rateTradeData.setModifyTag(this.getModifyTag());
        rateTradeData.setStartDate(this.getStartDate());
        rateTradeData.setEndDate(this.getEndDate());
        rateTradeData.setRemark(this.getRemark());
        rateTradeData.setRsrvStr1(this.getRsrvStr1());
        rateTradeData.setRsrvStr2(this.getRsrvStr2());
        rateTradeData.setRsrvStr3(this.getRsrvStr3());
        rateTradeData.setRsrvStr4(this.getRsrvStr4());
        rateTradeData.setRsrvStr5(this.getRsrvStr5());
        rateTradeData.setRsrvTag1(this.getRsrvTag1());
        rateTradeData.setRsrvTag2(this.getRsrvTag2());
        rateTradeData.setRsrvTag3(this.getRsrvTag3());
        rateTradeData.setRsrvDate1(this.getRsrvDate1());
        rateTradeData.setRsrvDate2(this.getRsrvDate2());
        rateTradeData.setRsrvDate3(this.getRsrvDate3());

        return rateTradeData;
    }

    public String getEndDate()
    {
        return this.endDate;
    }

    public String getInstId()
    {
        return this.instId;
    }

    public String getModifyTag()
    {
        return this.modifyTag;
    }

    public String getRate()
    {
        return this.rate;
    }

    public String getRemark()
    {
        return this.remark;
    }

    public String getRsrvDate1()
    {
        return this.rsrvDate1;
    }

    public String getRsrvDate2()
    {
        return this.rsrvDate2;
    }

    public String getRsrvDate3()
    {
        return this.rsrvDate3;
    }

    public String getRsrvStr1()
    {
        return this.rsrvStr1;
    }

    public String getRsrvStr2()
    {
        return this.rsrvStr2;
    }

    public String getRsrvStr3()
    {
        return this.rsrvStr3;
    }

    public String getRsrvStr4()
    {
        return this.rsrvStr4;
    }

    public String getRsrvStr5()
    {
        return this.rsrvStr5;
    }

    public String getRsrvTag1()
    {
        return this.rsrvTag1;
    }

    public String getRsrvTag2()
    {
        return this.rsrvTag2;
    }

    public String getRsrvTag3()
    {
        return this.rsrvTag3;
    }

    public String getStartDate()
    {
        return this.startDate;
    }

    public String getTableName()
    {
        return "TF_B_TRADE_RATE";
    }

    public String getUserId()
    {
        return this.userId;
    }

    public void setEndDate(String endDate)
    {
        this.endDate = endDate;
    }

    public void setInstId(String instId)
    {
        this.instId = instId;
    }

    public void setModifyTag(String modifyTag)
    {
        this.modifyTag = modifyTag;
    }

    public void setRate(String rate)
    {
        this.rate = rate;
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

    public void setRsrvTag1(String rsrvTag1)
    {
        this.rsrvTag1 = rsrvTag1;
    }

    public void setRsrvTag2(String rsrvTag2)
    {
        this.rsrvTag2 = rsrvTag2;
    }

    public void setRsrvTag3(String rsrvTag3)
    {
        this.rsrvTag3 = rsrvTag3;
    }

    public void setStartDate(String startDate)
    {
        this.startDate = startDate;
    }

    public void setUserId(String userId)
    {
        this.userId = userId;
    }

    public IData toData()
    {
        IData data = new DataMap();
        data.put("INST_ID", this.instId);
        data.put("USER_ID", this.userId);
        data.put("RATE", this.rate);
        data.put("MODIFY_TAG", this.modifyTag);
        data.put("START_DATE", this.startDate);
        data.put("END_DATE", this.endDate);
        data.put("REMARK", this.remark);
        data.put("RSRV_STR1", this.rsrvStr1);
        data.put("RSRV_STR2", this.rsrvStr2);
        data.put("RSRV_STR3", this.rsrvStr3);
        data.put("RSRV_STR4", this.rsrvStr4);
        data.put("RSRV_STR5", this.rsrvStr5);
        data.put("RSRV_TAG1", this.rsrvTag1);
        data.put("RSRV_TAG2", this.rsrvTag2);
        data.put("RSRV_TAG3", this.rsrvTag3);
        data.put("RSRV_DATE1", this.rsrvDate1);
        data.put("RSRV_DATE2", this.rsrvDate2);
        data.put("RSRV_DATE3", this.rsrvDate3);

        return data;
    }

    public String toString()
    {
        IData data = new DataMap();
        data.put(getTableName(), this.toData());
        return data.toString();
    }
}
